package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import communication.MSysFileInfo;
import communication.Protocol;
import communication.ShoppingList;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public class Upgrade_activity extends Activity {

    ShoppingList sl;
    ListView listview;
    ArrayAdapter<String> adapter_upgrade;
    ArrayList<String> listItems=new ArrayList<String>();
    private ArrayList<String> arrayList;
    ProgressBar Progress_Bar;
    static UsbFile root;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    TextView TextView_barra_sotto;
    String Chiamante="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        sl = SocketHandler.getSocket();

        listview = (ListView) findViewById(R.id.ListView_upgrade);
        Progress_Bar=(ProgressBar) findViewById(R.id.progressBar); // initiate the progress bar
        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        TextView_barra_sotto = (TextView) findViewById(R.id.textView_barra_sotto);
        TextView_barra_sotto.setText("Usb not insert");

        PreparaHmiFolder();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        Chiamante = (String) b.get("chiamante_stringa");

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

        switch (Chiamante)
        {
            case "Upgrade":
                    // parte su evento di inserimento della chiavetta
                break;
            case "Backup_su_hmi":
                String HMI_Folder_Backup = "storage/emulated/0/JamData/Backup/";    //senza data

                Info_file.Crea_cartella(HMI_Folder_Backup);
                Info_file.Crea_cartella(HMI_Folder_Backup+"/plc");
                Info_file.Crea_cartella(HMI_Folder_Backup+"/userdata");
                Info_file.Crea_cartella(HMI_Folder_Backup+"/param");
                Info_file.Crea_cartella(HMI_Folder_Backup+"/prog");
                Info_file.Crea_cartella(HMI_Folder_Backup+"/sys");


                LanciaBackup_suHMI(HMI_Folder_Backup);
                break;

            case "Backup_su_usb":

                break;

            case "Restore_da_hmi":

                break;

            case "Restore_da_usb":

                break;

            default:
                break;

        }
    }
    //*************************************************************************************************
    //PreparaHmiFolder
    //*************************************************************************************************
    private void PreparaHmiFolder() {
        File HmiRoot = android.os.Environment.getExternalStorageDirectory();
        File Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade");

        if (! Folder.exists()){
            Folder.mkdir();
        }
        //userdata
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/userdata");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };
        //fw
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/fw");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };
        //param
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/param");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };
        //plc
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/plc");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };
        //prog
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/prog");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };
        //sys
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/sys");
        if (!Folder.exists()){
            Folder.mkdir();
        }else{
            for (File file : Folder.listFiles())
                file.delete();
        };


        String HMI_Folder_Backup = "storage/emulated/0/JamData/Backup/";    //senza data

        Info_file.Crea_cartella(HMI_Folder_Backup);
        Info_file.Crea_cartella(HMI_Folder_Backup+"/plc");
        Info_file.Crea_cartella(HMI_Folder_Backup+"/userdata");
        Info_file.Crea_cartella(HMI_Folder_Backup+"/param");
        Info_file.Crea_cartella(HMI_Folder_Backup+"/prog");
        Info_file.Crea_cartella(HMI_Folder_Backup+"/sys");
    }

    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        if (devices.length > 0) return true;
        else return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Values.Context = this;
    }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();

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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Upgrade_activity.this, android.R.layout.simple_list_item_1);
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

                                if(Chiamante.equals("Upgrade")) {
                                    Aggiorna_CN_da_USB();
                                }

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
    private void Aggiorna_CN_da_USB() throws IOException {
            root = currentFs.getRootDirectory();
            UsbFile pathFolder_upgrade = null;
            UsbFile pathFolder_userdata = null;
            UsbFile pathFolder_fw = null;
            UsbFile pathFolder_param = null;
            UsbFile pathFolder_plc = null;
            UsbFile pathFolder_prog = null;
            UsbFile pathFolder_sys = null;
            UsbFile[] files_userdata = null;
            UsbFile[] files_fw = null;
            UsbFile[] files_param = null;
            UsbFile[] files_plc = null;
            UsbFile[] files_prog = null;
            UsbFile[] files_sys = null;
            UsbFile[] files = root.listFiles();
            for (UsbFile file : files) {

                if (file.isDirectory()) {
                    if (file.getName().equals("Upgrade"))
                        pathFolder_upgrade = file;

                }
            }
            //
            if (pathFolder_upgrade != null) {
                UsbFile[] files1 = pathFolder_upgrade.listFiles();
                for (UsbFile file : files1) {

                    if (file.isDirectory()) {
                        if (file.getName().equals("userdata"))
                            pathFolder_userdata = file;
                        if (file.getName().equals("fw"))
                            pathFolder_fw = file;
                        if (file.getName().equals("param"))
                            pathFolder_param = file;
                        if (file.getName().equals("plc"))
                            pathFolder_plc = file;
                        if (file.getName().equals("prog"))
                            pathFolder_prog = file;
                        if (file.getName().equals("sys"))
                            pathFolder_sys = file;
                    }

                }


            } else
                Toast.makeText(getApplicationContext(), "Upgrade folder is missing", Toast.LENGTH_SHORT).show();

            ArrayList<File> File_list_da_inviare = new ArrayList<>();


            File Hmiroot = android.os.Environment.getExternalStorageDirectory();


            //userdata
            if (pathFolder_userdata != null) {
                files_userdata = pathFolder_userdata.listFiles();
                for (UsbFile file : files_userdata) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/userdata/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }

            }
            //fw
            if (pathFolder_fw != null) {
                files_fw = pathFolder_fw.listFiles();
                for (UsbFile file : files_fw) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/fw/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }

            }
            //param
            if (pathFolder_param != null) {
                files_param = pathFolder_param.listFiles();
                for (UsbFile file : files_param) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/param/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file

                }

            }
            //plc
            if (pathFolder_plc != null) {
                files_plc = pathFolder_plc.listFiles();
                for (UsbFile file : files_plc) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/plc/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file

                }

            }

            //prog
            if (pathFolder_prog != null) {
                files_prog = pathFolder_prog.listFiles();
                for (UsbFile file : files_prog) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/prog/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file

                }

            }
            //sys
            if (pathFolder_sys != null) {
                files_sys = pathFolder_sys.listFiles();
                for (UsbFile file : files_sys) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/sys/" + file.getName());      //creo file su hmi
                    boolean result = copyFileToHMI(file, file_hmi);   //copio file da usb a hmi
                    if (result)
                        File_list_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file


                }

            }


            if (File_list_da_inviare.size() > 0) {
                if (sl.IsConnected()) {
                    TextView_barra_sotto.setText("Downloading..............Please wait");
                    new Scrivi_listafiles_dentro_CN(Upgrade_activity.this).execute(File_list_da_inviare);
                }
            } else {
                Toast.makeText(getApplicationContext(), "there are no files to download", Toast.LENGTH_SHORT).show();
            }


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


    //**************************************************************************************************
    // LanciaBackup_suHMI
    //**************************************************************************************************
    private void LanciaBackup_suHMI( String HMI_Folder_Backup) {

        ArrayList<String> Lista_cartelle = new ArrayList<>(Arrays.asList("plc","userdata","param","prog","sys"));
        ArrayList<String> Path_Hmi_Backup_folder = new ArrayList<>(Arrays.asList(HMI_Folder_Backup));

        new Backup_CN(Upgrade_activity.this).execute(Lista_cartelle,Path_Hmi_Backup_folder);

    }
    //*************************************************************************************************
    // Background per Leggere file da CN
    //*************************************************************************************************
    class Scrivi_listafiles_dentro_CN extends AsyncTask<ArrayList<File>,ArrayList< String>,ArrayList<Integer>> {
        private Activity activity;
        private ProgressDialog dialog;
        private Context context;
        ImageButton btn_Exit;


        public Scrivi_listafiles_dentro_CN(Activity activity) {
            this.activity = activity;
            this.context = activity;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");
            btn_Exit = (ImageButton) findViewById(R.id.imageButton_exit);
            btn_Exit.setEnabled(false);


        }



        @Override
        protected ArrayList<Integer> doInBackground(ArrayList<File>... params) {
            boolean r = false;
            String File_path_da_inviare ="";
            String File_name = "";
            String CN_file_path="";
            ArrayList< String> val_progress = new ArrayList<>();
            String Font_path_file = "";
            ArrayList<Integer> res = new ArrayList();
            Integer numero_file=0;
            Integer numero_done = 0;
            Integer numero_errori = 0;

            for (File file_item: params[0]) {

                Protocol.OnProgressListener pl = new Protocol.OnProgressListener() {
                    @Override
                    public void onProgressUpdate(int Completion) {

                        // Display Progress value (Completion of 100)
                        Progress_Bar.setProgress(Completion);

                    }
                };
                Boolean result_delete = false;
                File_path_da_inviare = file_item.getPath();
                File_name = file_item.getName();

                if (File_path_da_inviare.contains("plc")) {
                    CN_file_path = "C:\\cnc\\plc\\" + File_name;
                    result_delete = sl.FileDelete(CN_file_path);
                    val_progress.add(CN_file_path + " Erasing ....");
                    publishProgress(val_progress);
                }
                if (File_path_da_inviare.contains("fw")) {
                    CN_file_path = "B:\\fw\\"+File_name;
                    try {

                        MSysFileInfo fi = new MSysFileInfo();
                        String path_folder = "B:\\fw\\*.*";
                        fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                        if (fi != null)    //se la cartella contiene almeno un file
                        {

                            result_delete = sl.FileDelete("B:\\fw\\" + fi.FName);

                        }else
                            result_delete = true;   //non c'è nessun file, lo metto true così mi permette comunque di scaricare il fw
                    } catch (Exception ex) {
                    }

                    publishProgress(val_progress);
                }


                if (File_path_da_inviare.contains("param")) {
                    CN_file_path = "C:\\cnc\\param\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("sys")) {
                    CN_file_path = "C:\\cnc\\sys\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("userdata")) {
                    CN_file_path = "C:\\cnc\\userdata\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("prog")) {
                    CN_file_path = "C:\\cnc\\prog\\" + File_name;
                    result_delete = true;
                }


                CN_file_path = CN_file_path.replace("/", "\\");
                val_progress.add(CN_file_path + " Downloading ....");
                publishProgress(val_progress);



                if (result_delete) {
                    numero_file++;


                    r = sl.FileUpload(CN_file_path, File_path_da_inviare, pl);
                }


               // val_progress.add(CN_file_path);
                if (r)
                    val_progress.add(".......OK");
                else {
                    numero_errori++;
                    val_progress.add(".......error");
                }
                publishProgress(val_progress);

            }

            res.add(numero_file);
            res.add(numero_done);
            res.add(numero_errori);

            return res;
        }

        protected void onPostExecute(ArrayList<Integer> ret) {
            super.onPostExecute(ret);
            this.dialog.dismiss();

            Integer numero_file = 0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            if(ret.size() >1) {
                numero_file = ret.get(0);
                numero_done = ret.get(1);
                numero_errori = ret.get(2);
            }

            btn_Exit.setEnabled(true);


            TextView_barra_sotto.setText("End: Total files: "+ numero_file + ", Errors: " + numero_errori);




        }
        @Override
        protected void onProgressUpdate(ArrayList<String> ... values) {
            ArrayList<String> val = values[0];
            adapter_upgrade = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1,
                    val);
            adapter_upgrade.notifyDataSetChanged();

            listview.setAdapter(adapter_upgrade);
            //listview autoscroll verso il basso
            int count = listview.getCount();
            for (int i = 0; i < count; i++) {
                final int finalI = i;
                listview.post(new Runnable() {
                    @Override
                    public void run() {
                        listview.smoothScrollToPosition(finalI);
                    }
                });
            }

        }

    }
    //*************************************************************************************************
    // CopyFileToHD
    //*************************************************************************************************
    public Boolean copyFileToHMI(UsbFile sourceFile, File HMIPath)
            throws IOException {


            if (HMIPath != null) {

                InputStream is = UsbFileStreamFactory.createBufferedInputStream(sourceFile, currentFs);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                FileOutputStream outputStream = new FileOutputStream(HMIPath);
                outputStream.write(buffer.toByteArray());
                outputStream.close();
                return true;
            }

        return false;
    }
    //**********************************************************************************************
    class Backup_CN extends AsyncTask<ArrayList<String>,ArrayList<String>,ArrayList<Integer>> {
        private Activity activity;
        private ProgressDialog dialog;
        private Context context;
        ArrayList<ArrayList<String>> List_Folder_and_file = new ArrayList<ArrayList<String>>();
        ArrayList<String> val_progress = new ArrayList<>();
        ImageButton btn_Exit;

        public Backup_CN(Activity activity) {
            this.activity = activity;
            this.context = activity;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");
            btn_Exit = (ImageButton) findViewById(R.id.imageButton_exit);
            btn_Exit.setEnabled(false);
        }



        @Override
        protected ArrayList<Integer> doInBackground(ArrayList<String>... params) {
            boolean r = false;
            MSysFileInfo fi = new MSysFileInfo();
            ArrayList<Integer> res = new ArrayList();
            Integer numero_file=0;
            Integer numero_done = 0;
            Integer numero_errori = 0;

            //cerco cartelle
            listItems.clear();

            if(params[0].size() >0) {

                String HMI_Folder_Backup_path = params[1].get(0);


                //per ogni cartella cerco i file
                for (String cartella : params[0]) {

                    ArrayList<String> Folder_and_file = new ArrayList<String>();
                    if (!cartella.equals(".") && !cartella.equals("..")) {
                        String path_folder = "C:\\cnc\\" + cartella + "\\*.*";

                        try {
                            fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                            if (fi != null)    //se la cartella contiene almeno un file
                            {
                                Folder_and_file.add("C:\\cnc\\" + cartella + "\\" + fi.FName);
                                while (true) {
                                    fi = sl.FileDir();
                                    if (fi.FName != null) {
                                        Folder_and_file.add("C:\\cnc\\" + cartella + "\\" + fi.FName);
                                    } else break;
                                }
                            }
                        } catch (Exception ex) {
                        }
                        List_Folder_and_file.add(Folder_and_file);
                    }
                }

                //leggo i file della lista ottenuta

                for (ArrayList<String> Lista_cartella : List_Folder_and_file) {

                    for (String file : Lista_cartella) {


                        Protocol.OnProgressListener pl = new Protocol.OnProgressListener() {
                            @Override
                            public void onProgressUpdate(int Completion) {

                                // Display Progress value (Completion of 100)
                                Progress_Bar.setProgress(Completion);

                            }
                        };
                        //scarico il file dal CN
                        String Path_Destinazione = file.replace("C:\\cnc\\", HMI_Folder_Backup_path);
                        Path_Destinazione = Path_Destinazione.replace("\\", "/");

                        val_progress.add(Path_Destinazione + " Downloading ....");
                        publishProgress(val_progress);
                        numero_file++;
                        r = sl.FileDownload(file, Path_Destinazione, pl);

                        if (r) {
                            numero_done++;
                            val_progress.add(".......OK");
                        } else {
                            numero_errori++;
                            val_progress.add(".......error");
                        }
                        publishProgress(val_progress);

                    }


                }
            }

            res.add(numero_file);
            res.add(numero_done);
            res.add(numero_errori);


            return  res;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> ret) {
            super.onPostExecute(ret);
            this.dialog.dismiss();

            Integer numero_file =0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            if(ret.size() >1) {
                numero_file = ret.get(0);
                numero_done = ret.get(1);
                numero_errori = ret.get(2);
            }

            btn_Exit.setEnabled(true);


            TextView_barra_sotto.setText("End: Total files: "+ numero_file + ", Errors: " + numero_errori);

        }

        @Override
        protected void onProgressUpdate(ArrayList<String> ... values) {
            ArrayList<String> val = values[0];
            adapter_upgrade = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1,
                    val);
            adapter_upgrade.notifyDataSetChanged();

            listview.setAdapter(adapter_upgrade);
            //listview autoscroll verso il basso
            int count = listview.getCount();
            for (int i = 0; i < count; i++) {
                final int finalI = i;
                listview.post(new Runnable() {
                    @Override
                    public void run() {
                        listview.smoothScrollToPosition(finalI);
                    }
                });
            }

        }
    }


}
