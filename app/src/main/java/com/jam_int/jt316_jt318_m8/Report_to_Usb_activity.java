package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public class Report_to_Usb_activity extends Activity {


    ProgressBar Progress_Bar;
    static UsbFile root;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    TextView TextView_barra_sotto;
    ListView listview;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    Thread_LoopEmergenza thread_LoopEmergenza;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        context = this;
        listview = (ListView) findViewById(R.id.ListView_upgrade);
        Progress_Bar=(ProgressBar) findViewById(R.id.progressBar); // initiate the progress bar
        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        TextView_barra_sotto = (TextView) findViewById(R.id.textView_barra_sotto);
        TextView_barra_sotto.setText("Usb not insert");


       // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listItems);

        // Here, you set the data in your ListView
        listview.setAdapter(adapter);


        if (GetUSBConnectionStatus()) {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
            if (devices.length > 0) {
                device_usb = devices[0];

                PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);
                UsbManager manager = (UsbManager) getSystemService(getApplicationContext().USB_SERVICE);
                manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);

            }else
            {
                TextView_barra_sotto.setText("Insert USB!");

            }

        }else
        {
             TextView_barra_sotto.setText("Insert USB!");
        }

        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);


    }



    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        if (devices.length > 0) return true;
        else return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "ABCActivity");

        }

    }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        try {
            thread_LoopEmergenza.KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();


    }
    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(usbReceiver);

        }catch (Exception e)
        {}


    }
    //*************************************************************************************************
    //BroadcastReceiver usbReceiver
    //*************************************************************************************************
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
                if (devices.length > 0) {
                    device_usb = devices[0];

                    PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    registerReceiver(usbReceiver, filter);
                    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);

                }
            }

            if (ACTION_MEDIA_MOUNTED.equals(action)) {


            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Report_to_Usb_activity.this, android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                }
            }

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            try {


                                device_usb.init();

                                // Only uses the first partition on the device
                                currentFs = device_usb.getPartitions().get(0).getFileSystem();
                                Log.d("TAG", "Capacity: " + currentFs.getCapacity());
                                Log.d("TAG", "Occupied Space: " + currentFs.getOccupiedSpace());
                                Log.d("TAG", "Free Space: " + currentFs.getFreeSpace());
                                Log.d("TAG", "Chunk size: " + currentFs.getChunkSize());

                                TextView_barra_sotto.setText("Downloading..............Please wait");
                                Copia_report_to_usb();


                            } catch (Exception e) {
                            }
                            //call method to set up device communication
                        }
                    } else {
                        Log.d("TAG", "permission denied for device " + device);
                    }
                }
            }
        }




    };
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void Copia_report_to_usb() throws IOException {

        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/JamData");
        final String extensions = "deb";
        File[] files = (File[]) dir.listFiles(new FilenameFilter() {
            public boolean accept(final File a_directory,
                                  final String a_name) {
                return a_name.endsWith(extensions);
            }


        });
        for (File file : files) {
           if( copyFileToUsb(file)){

               // this line adds the data of your EditText and puts in your array
               listItems.add(file.getName());
               // next thing you have to do is check if your adapter has changed
               adapter.notifyDataSetChanged();
           }
        }
        try {
            File file_info_Jam = new File(root.getAbsolutePath() + "/JamData/info_Jam.txt");
            if(copyFileToUsb(file_info_Jam)){
                // this line adds the data of your EditText and puts in your array
                listItems.add(file_info_Jam.getName());
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();

            }
        }catch (Exception e){}

        try {
            File file_MachineLog = new File(root.getAbsolutePath() + "/JamData/MachineLog.txt");
            if(copyFileToUsb(file_MachineLog)){

                // this line adds the data of your EditText and puts in your array
                listItems.add(file_MachineLog.getName());
                // next thing you have to do is check if your adapter has changed
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){}
        Toast.makeText(this, "Files successfully copied", Toast.LENGTH_LONG).show();
        TextView_barra_sotto.setText(getString(R.string.Fatto));

    }
    //*************************************************************************************************
    // CopyFileToUsb
    //*************************************************************************************************
    public Boolean copyFileToUsb(File sourceFile)
            throws IOException {

        root = currentFs.getRootDirectory();
        UsbFile folder = root;


        if (sourceFile.isDirectory()) {//è un file o folder?
            UsbFile f = folder.search(sourceFile.getName());
            if (f != null) f.delete();

            UsbFile folder1 = folder.createDirectory(sourceFile.getName());

            File[] allEntries = new File(sourceFile.getPath()).listFiles();
            if (allEntries != null) {
                for (File files : allEntries) {
                    UsbFile file = folder1.createFile(files.getName());

                    OutputStream os = new UsbFileOutputStream(file);

                    os.write(Utility.getByte(files.getPath()));
                    os.close();
                }
            }
        } else {
            UsbFile f = folder.search(sourceFile.getName());
            if (f != null) f.delete();

            UsbFile file = folder.createFile(sourceFile.getName());


            OutputStream os = new UsbFileOutputStream(file);

            os.write(Utility.getByte(sourceFile.getPath()));
            os.close();
        }


        return true;
    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    //nascondo navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            int currentApiVersion;
            currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

                getWindow().getDecorView().setSystemUiVisibility(flags);

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }

        }
    }

    //*************************************************************************************************
    // on click Exit
    //*************************************************************************************************
    public void onclick_button_Exit(View view) throws IOException {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        this.finish();

    }





}
