package com.jam_int.jt316_jt318_m8;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.fs.UsbFileStreamFactory;
import com.jamint.ricette.Ricetta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

//import androidx.core.app.ActivityCompat;


public class Usb_Files_Activity extends Activity {

    ListView LvListDX;
    ListView LvListSX;
    Button Button_dxsx;
    Button Button_sxdx;
    Button Button_back_sx;
    Button Button_back_dx;
    Button Button_delete_dx;
    Button Button_delete_sx;
    Button Button_sxdx_all;
    Button Button_dxsx_all;
    ImageButton IButton_exit;

    ArrayList<String> estensione_list_sx = new ArrayList<String>();

    String estensione_list_dx = "xml";
    String Folder_corrente_dx = "/storage/emulated/0/ricette";
    String Folder_corrente_sx = "";

    String Folder_sx;
    String Folder_dx;

    boolean FolderSx = false, FolderDx = false;

    File FileSelezionato_dx, FileSelezionato_sx;
    UsbMassStorageDevice device_usb;
    static UsbFile root;
    FileSystem currentFs;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    Thread_LoopEmergenza thread_LoopEmergenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_files);

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        estensione_list_sx.add("xml");
        estensione_list_sx.add("eep");

        //controllo se la usb è già inserita, se si partirà il broadcast ACTION_USB_PERMISSION che scriverà i file sullo schermo,
        //se invece verrà inserita successivamente, partirà il broadcast ACTION_USB_DEVICE_ATTACHED il quale controllerà il permesso e
        //poi farà partire il broadcast ACTION_USB_PERMISSION che scriverà i file sullo schermo.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if (GetUSBConnectionStatus()) {
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

        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);


        //findViewById
        Button_dxsx = (Button) findViewById(R.id.Btn_dxsx);
        Button_sxdx = (Button) findViewById(R.id.Btn_sxdx);
        Button_dxsx_all = (Button) findViewById(R.id.Btn_dxsx_all);
        Button_sxdx_all = (Button) findViewById(R.id.Btn_sxdx_all);
        Button_back_sx = (Button) findViewById(R.id.button_back_sx);
        Button_back_dx = (Button) findViewById(R.id.button_back_dx);
        Button_sxdx = (Button) findViewById(R.id.Btn_sxdx);
        Button_delete_dx = (Button) findViewById(R.id.btn_delete_dx);
        Button_delete_sx = (Button) findViewById(R.id.btn_delete_sx);
        IButton_exit = (ImageButton) findViewById(R.id.imageButton_exit);
        Button_dxsx.setEnabled(false);
        Button_sxdx.setEnabled(false);
        Button_delete_dx.setEnabled(false);
        Button_delete_sx.setEnabled(false);
        Button_dxsx_all.setEnabled(false);
        Button_sxdx_all.setEnabled(false);
        LvListDX = (ListView) findViewById(R.id.LvList_dx);
        LvListSX = (ListView) findViewById(R.id.LvList_sx);
        Inizializzo_eventi();

        ShowList(LvListDX,Folder_corrente_dx,estensione_list_dx);

    }

    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        if (devices.length > 0) return true;
        else return false;
    }
    @Override
    protected void onResume() {
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

                    Button_dxsx_all.setEnabled(true);
                    Button_sxdx_all.setEnabled(true);

                }
            }

            if (ACTION_MEDIA_MOUNTED.equals(action)) {


            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Usb_Files_Activity.this, android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();
                    LvListSX.setAdapter(adapter);

                    Button_dxsx_all.setEnabled(false);
                    Button_sxdx_all.setEnabled(false);
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
                                root = currentFs.getRootDirectory();

                                UsbFile[] files = root.listFiles();

                                ShowList_USB(LvListSX, files, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                                Log.d("ListRow", "Row ListSx: " + LvListSX.getAdapter().getCount());
                                Log.d("ListRow", "Row ListDx: " + LvListDX.getAdapter().getCount());


                                Button_dxsx_all.setEnabled(true);
                                Button_sxdx_all.setEnabled(true);

                            } catch (Exception e) {
                                Log.d("TAG", e.toString());
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


    //*************************************************************************************************
    // ShowList_USB
    //*************************************************************************************************
    private void ShowList_USB(ListView lvList, UsbFile[] files, ArrayList<String> filtro_estensione) {
        ArrayAdapter<String> adapter;
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> files_list = new ArrayList<String>();

        for (UsbFile file : files) {

            if (file.isDirectory()) {
                folders.add(file.getName());
            } else {
                String name = file.getName();
                String estensione = "";
                try {
                    estensione = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {

                }

                for (String estenzione : filtro_estensione) {
                    if (estensione.equalsIgnoreCase(estenzione))
                        files_list.add(file.getName());
                }
            }

        }


        Collections.sort(folders, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(files_list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        listItems.clear();

        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        for (int i = 0; i < files_list.size(); i++) {
            listItems.add(files_list.get(i));
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                String str = textView.getText().toString();
                if(str.endsWith("/")) {
                    textView.setTextColor(Color.BLUE);
                }

                return view;
            }
        };
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }

    //*************************************************************************************************
    // ShowList
    //*************************************************************************************************
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {
        ArrayAdapter<String> adapter;
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();
        File[] allEntries = new File(path_folder).listFiles();
        if (allEntries != null) {

            for (int i = 0; i < allEntries.length; i++) {
                if (allEntries[i].isDirectory()) {
                    folders.add(allEntries[i].getName());
                } else if (allEntries[i].isFile()) {
                    String name = allEntries[i].getName();
                    String estensione = "";
                    try {
                        estensione = name.substring(name.lastIndexOf(".") + 1);
                    } catch (Exception e) {

                    }

                    if (estensione.equalsIgnoreCase(filtro_estensione))
                        files.add(allEntries[i].getName());
                }
            }

            Collections.sort(folders, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            Collections.sort(files, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            listItems.clear();

            for (int i = 0; i < folders.size(); i++) {
                listItems.add(folders.get(i) + "/");
            }

            for (int i = 0; i < files.size(); i++) {
                listItems.add(files.get(i));
            }

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    listItems){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view =super.getView(position, convertView, parent);

                    TextView textView=(TextView) view.findViewById(android.R.id.text1);

                    /*YOUR CHOICE OF COLOR*/
                    String str = textView.getText().toString();
                    if(str.endsWith("/")) {
                        textView.setTextColor(Color.BLUE);
                    }

                    return view;
                }
            };
            adapter.notifyDataSetChanged();

            lvList.setAdapter(adapter);
        } else {

            File folder = new File(path_folder);    //se la cartella ricette non esiste la creo
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    //*************************************************************************************************
    // eventi List
    //*************************************************************************************************
    private void Inizializzo_eventi() {

        //Evento alla pressione di una riga del List di DX
        LvListDX.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (entryName.endsWith("/")) {  //è un file o folder?
                    //folder

                    //Se FolderDx è false è stata selezionata una cartella ma non ci si è entrati
                    //Se FolderDx è true è stata selezionata una cartella e ci si è entrati
                    if (FolderDx && FileSelezionato_dx.getPath().equals(Folder_corrente_dx + "/" + SubString.SubstringExtensions.Before(entryName, "/"))) {
                        Folder_corrente_dx = Folder_corrente_dx + "/" + SubString.SubstringExtensions.Before(entryName, "/");
                        ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                        FolderDx = false;
                        Button_dxsx.setEnabled(false);
                        Button_delete_dx.setEnabled(false);
                        FileSelezionato_dx = null;
                    } else {
                        String file_selezionato = Folder_corrente_dx + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato_dx != null)
                            file_precedente_selezionato = FileSelezionato_dx.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisiva(adapterView);
                            Button_dxsx.setEnabled(false);
                            Button_delete_dx.setEnabled(false);
                            FileSelezionato_dx = null;

                        } else {
                            //selezionato nuovo file
                            FileSelezionato_dx = new File(Folder_corrente_dx + "/" + entryName);
                            SelezionaGrigio(adapterView, view);
                            Button_dxsx.setEnabled(true);
                            Button_delete_dx.setEnabled(true);
                        }
                        FolderDx = true;
                    }

                } else {   //file
                    String file_selezionato = Folder_corrente_dx + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato_dx != null)
                        file_precedente_selezionato = FileSelezionato_dx.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva(adapterView);
                        Button_dxsx.setEnabled(false);
                        Button_delete_dx.setEnabled(false);
                        FileSelezionato_dx = null;

                    } else {
                        //selezionato nuovo file
                        FileSelezionato_dx = new File(Folder_corrente_dx + "/" + entryName);
                        SelezionaGrigio(adapterView, view);
                        Button_dxsx.setEnabled(true);
                        Button_delete_dx.setEnabled(true);
                    }

                }
            }
        });

        LvListSX.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (entryName.endsWith("/")) {  //è un file o folder?
                    //folder
                    if (FolderSx && FileSelezionato_sx.getPath().equals(Folder_corrente_sx + "/" + SubString.SubstringExtensions.Before(entryName, "/"))) {
                        Folder_corrente_sx = Folder_corrente_sx + "/" + SubString.SubstringExtensions.Before(entryName, "/");
                        String[] str = Folder_corrente_sx.split("/");
                        UsbFile[] files = new UsbFile[0];
                        try {
                            files = root.listFiles();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UsbFile[] files1 = new UsbFile[0];
                        for (UsbFile file : files) {
                            if (str[str.length - 1].equals(file.getName())) {
                                try {
                                    files1 = root.search(SubString.SubstringExtensions.Before(entryName, "/")).listFiles();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        ShowList_USB(LvListSX, files1, estensione_list_sx);
                        FolderSx = false;
                        Button_sxdx.setEnabled(false);
                        Button_delete_sx.setEnabled(false);
                        FileSelezionato_sx = null;
                    } else {
                        String file_selezionato = Folder_corrente_sx + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato_sx != null)
                            file_precedente_selezionato = FileSelezionato_sx.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisiva(adapterView);
                            Button_sxdx.setEnabled(false);
                            Button_delete_sx.setEnabled(false);
                            FileSelezionato_sx = null;

                        } else {
                            //selezionato nuovo file
                            FileSelezionato_sx = new File(Folder_corrente_sx + "/" + entryName);
                            SelezionaGrigio(adapterView, view);
                            Button_sxdx.setEnabled(true);
                            Button_delete_sx.setEnabled(true);
                        }
                        FolderSx = true;
                    }

                } else {   //file
                    String file_selezionato = Folder_corrente_sx + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato_sx != null)
                        file_precedente_selezionato = FileSelezionato_sx.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva(adapterView);
                        Button_sxdx.setEnabled(false);
                        Button_delete_sx.setEnabled(false);
                        FileSelezionato_sx = null;

                    } else {
                        //selezionato nuovo file
                        FileSelezionato_sx = new File(Folder_corrente_sx + "/" + entryName);
                        SelezionaGrigio(adapterView, view);
                        Button_sxdx.setEnabled(true);
                        Button_delete_sx.setEnabled(true);
                    }

                }


            }
        });

    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void TolgoSelezioneVisiva(AdapterView<?> adapterView) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void SelezionaGrigio(AdapterView<?> adapterView, View view) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

        // change the background color of the selected element
        view.setBackgroundColor(Color.RED);

    }

    //*************************************************************************************************
    //On click del button Back dx
    //*************************************************************************************************
    public void On_click_button_back_dx(View view) throws IOException {

        if (!SubString.SubstringExtensions.After(Folder_corrente_dx, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.SubstringExtensions.BeforeLast(Folder_corrente_dx, "/");
            Folder_corrente_dx = Folder_back;
            ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
            Button_dxsx.setEnabled(false);
            Button_delete_dx.setEnabled(false);

            TolgoSelezioneVisiva(LvListDX);
        }
    }

    //*************************************************************************************************
    //On click del button Back sx
    //*************************************************************************************************
    public void On_click_button_back_sx(View view) throws IOException {

        if (!Folder_corrente_sx.equals("/")) //non faccio scendere sotto la root
        {
            String Folder_back = SubString.SubstringExtensions.BeforeLast(Folder_corrente_sx, "/");
            Folder_corrente_sx = Folder_back;
            String[] str = Folder_corrente_sx.split("/");
            UsbFile[] files = new UsbFile[0];
            try {
                files = root.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UsbFile[] files1 = new UsbFile[0];
            if (str.length > 1) {
                for (UsbFile file : files) {
                    if (str[str.length - 1].equals(file.getName())) {
                        try {
                            files1 = root.search(Folder_corrente_sx).listFiles();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                files1 = root.search("/").listFiles();
            }

            ShowList_USB(LvListSX, files1, estensione_list_sx);
            Button_sxdx.setEnabled(false);
            Button_delete_sx.setEnabled(false);

            TolgoSelezioneVisiva(LvListSX);
        }
    }

    //*************************************************************************************************
    //On click del button sx verso dx
    //*************************************************************************************************
    public void On_click_Btn_sxdx(View view) throws IOException {

        if (FileSelezionato_sx != null) {
            if (!FileSelezionato_sx.getName().contains(".")) {
                String name = FileSelezionato_sx.getName();
                String Path_HMI = Folder_corrente_dx + "/" + name;
                File DestinationLocation = new File(Path_HMI);
                UsbFile file = new UsbFile() {
                    @Nullable
                    @Override
                    public UsbFile search(@NonNull String path) throws IOException {
                        return null;
                    }

                    @Override
                    public boolean isDirectory() {
                        return false;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public String getAbsolutePath() {
                        return null;
                    }

                    @Override
                    public void setName(String newName) throws IOException {

                    }

                    @Override
                    public long createdAt() {
                        return 0;
                    }

                    @Override
                    public long lastModified() {
                        return 0;
                    }

                    @Override
                    public long lastAccessed() {
                        return 0;
                    }

                    @Override
                    public UsbFile getParent() {
                        return null;
                    }

                    @Override
                    public String[] list() throws IOException {
                        return new String[0];
                    }

                    @Override
                    public UsbFile[] listFiles() throws IOException {
                        return new UsbFile[0];
                    }

                    @Override
                    public long getLength() {
                        return 0;
                    }

                    @Override
                    public void setLength(long newLength) throws IOException {

                    }

                    @Override
                    public void read(long offset, ByteBuffer destination) throws IOException {

                    }

                    @Override
                    public void write(long offset, ByteBuffer source) throws IOException {

                    }

                    @Override
                    public void flush() throws IOException {

                    }

                    @Override
                    public void close() throws IOException {

                    }

                    @Override
                    public UsbFile createDirectory(String name) throws IOException {
                        return null;
                    }

                    @Override
                    public UsbFile createFile(String name) throws IOException {
                        return null;
                    }

                    @Override
                    public void moveTo(UsbFile destination) throws IOException {

                    }

                    @Override
                    public void delete() throws IOException {

                    }

                    @Override
                    public boolean isRoot() {
                        return false;
                    }
                };
                try {
                    file = root.search(FileSelezionato_sx.getName());
                } catch (Exception e) {
                }

                try {
                    copyFileToHD(file);     //cartella
                    Ricetta r = new Ricetta();
                    try {
                        r.open(DestinationLocation);
                        r.exportToUsr(new File(DestinationLocation.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                    } catch (Exception e) {
                        Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                    }
                    UsbFile[] files = root.listFiles();

                    ShowList_USB(LvListSX, files, estensione_list_sx);
                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();

                }
            } else {
                String extension = FileSelezionato_sx.getPath().substring(FileSelezionato_sx.getPath().lastIndexOf("."));

                if (extension.toLowerCase().equals(".xml")) {
                    boolean esiste = ControlloSeEsiste(LvListDX, FileSelezionato_sx.getName());
                    if (esiste) {
                        // chiamo il messaggio Yes/No per sovrascrivere
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle("File already exists");
                        builder.setMessage("do you want overwrite existing file?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Premuto Yes
                                String name = FileSelezionato_sx.getName();
                                String Path_HMI = Folder_corrente_dx + "/" + name;
                                File DestinationLocation = new File(Path_HMI);
                                UsbFile file = new UsbFile() {
                                    @Nullable
                                    @Override
                                    public UsbFile search(@NonNull String path) throws IOException {
                                        return null;
                                    }

                                    @Override
                                    public boolean isDirectory() {
                                        return false;
                                    }

                                    @Override
                                    public String getName() {
                                        return null;
                                    }

                                    @Override
                                    public String getAbsolutePath() {
                                        return null;
                                    }

                                    @Override
                                    public void setName(String newName) throws IOException {

                                    }

                                    @Override
                                    public long createdAt() {
                                        return 0;
                                    }

                                    @Override
                                    public long lastModified() {
                                        return 0;
                                    }

                                    @Override
                                    public long lastAccessed() {
                                        return 0;
                                    }

                                    @Override
                                    public UsbFile getParent() {
                                        return null;
                                    }

                                    @Override
                                    public String[] list() throws IOException {
                                        return new String[0];
                                    }

                                    @Override
                                    public UsbFile[] listFiles() throws IOException {
                                        return new UsbFile[0];
                                    }

                                    @Override
                                    public long getLength() {
                                        return 0;
                                    }

                                    @Override
                                    public void setLength(long newLength) throws IOException {

                                    }

                                    @Override
                                    public void read(long offset, ByteBuffer destination) throws IOException {

                                    }

                                    @Override
                                    public void write(long offset, ByteBuffer source) throws IOException {

                                    }

                                    @Override
                                    public void flush() throws IOException {

                                    }

                                    @Override
                                    public void close() throws IOException {

                                    }

                                    @Override
                                    public UsbFile createDirectory(String name) throws IOException {
                                        return null;
                                    }

                                    @Override
                                    public UsbFile createFile(String name) throws IOException {
                                        return null;
                                    }

                                    @Override
                                    public void moveTo(UsbFile destination) throws IOException {

                                    }

                                    @Override
                                    public void delete() throws IOException {

                                    }

                                    @Override
                                    public boolean isRoot() {
                                        return false;
                                    }
                                };
                                try {
                                    file = root.search(FileSelezionato_sx.getName());
                                } catch (Exception e) {
                                }

                                try {
                                    copyFileToHD(file);     //se esiste file
                                    String Filename = file.getName();
                                    if (Filename.contains(".XML"))      //controllo se è uppercase
                                    {
                                        String newfile = Utility.Downcase_ExtensionFile(DestinationLocation);
                                        DestinationLocation = new File(newfile);
                                        FileSelezionato_sx = new File(FileSelezionato_sx.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".XML", ".xml")));

                                    }//

                                    Ricetta r = new Ricetta();
                                    try {
                                        r.open(DestinationLocation);
                                        r.exportToUsr(new File(DestinationLocation.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                                    } catch (Exception e) {
                                        Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    UsbFile[] files = root.listFiles();

                                    ShowList_USB(LvListSX, files, estensione_list_sx);
                                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();

                                }

                                Toast.makeText(getApplicationContext(), "File " + FileSelezionato_sx.getName() + " Copied", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }

                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Premuto no
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        String name = FileSelezionato_sx.getName();
                        String Path_HMI = Folder_corrente_dx + "/" + name;
                        File DestinationLocation = new File(Path_HMI);
                        UsbFile file = new UsbFile() {
                            @Nullable
                            @Override
                            public UsbFile search(@NonNull String path) throws IOException {
                                return null;
                            }

                            @Override
                            public boolean isDirectory() {
                                return false;
                            }

                            @Override
                            public String getName() {
                                return null;
                            }

                            @Override
                            public String getAbsolutePath() {
                                return null;
                            }

                            @Override
                            public void setName(String newName) throws IOException {

                            }

                            @Override
                            public long createdAt() {
                                return 0;
                            }

                            @Override
                            public long lastModified() {
                                return 0;
                            }

                            @Override
                            public long lastAccessed() {
                                return 0;
                            }

                            @Override
                            public UsbFile getParent() {
                                return null;
                            }

                            @Override
                            public String[] list() throws IOException {
                                return new String[0];
                            }

                            @Override
                            public UsbFile[] listFiles() throws IOException {
                                return new UsbFile[0];
                            }

                            @Override
                            public long getLength() {
                                return 0;
                            }

                            @Override
                            public void setLength(long newLength) throws IOException {

                            }

                            @Override
                            public void read(long offset, ByteBuffer destination) throws IOException {

                            }

                            @Override
                            public void write(long offset, ByteBuffer source) throws IOException {

                            }

                            @Override
                            public void flush() throws IOException {

                            }

                            @Override
                            public void close() throws IOException {

                            }

                            @Override
                            public UsbFile createDirectory(String name) throws IOException {
                                return null;
                            }

                            @Override
                            public UsbFile createFile(String name) throws IOException {
                                return null;
                            }

                            @Override
                            public void moveTo(UsbFile destination) throws IOException {

                            }

                            @Override
                            public void delete() throws IOException {

                            }

                            @Override
                            public boolean isRoot() {
                                return false;
                            }
                        };
                        try {
                            file = root.search(FileSelezionato_sx.getName());
                        } catch (Exception e) {
                        }

                        try {

                            copyFileToHD(file);     //se non esiste file
                            String Filename = file.getName();
                            if (Filename.contains(".XML"))      //controllo se è uppercase
                            {
                                String newfile = Utility.Downcase_ExtensionFile(DestinationLocation);
                                DestinationLocation = new File(newfile);
                                FileSelezionato_sx = new File(FileSelezionato_sx.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".XML", ".xml")));

                            }//



                            Ricetta r = new Ricetta();
                            try {
                                r.open(DestinationLocation);
                                r.exportToUsr(new File(DestinationLocation.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                            } catch (Exception e) {
                                Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                            }
                            UsbFile[] files = root.listFiles();

                            ShowList_USB(LvListSX, files, estensione_list_sx);
                            ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(getApplicationContext(), "File " + FileSelezionato_sx.getName() + " Copied", Toast.LENGTH_LONG).show();
                    }
                    TolgoSelezioneVisiva(LvListSX);
                    Button_sxdx.setEnabled(false);
                    Button_delete_sx.setEnabled(false);
                } else if (extension.toLowerCase().equals(".eep")) {
                    try {
                        //////////////   prova   ////////////////////////////////////////////////////////////////////
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                        alertDialog.setMessage("Convert eep file to xml file?");

                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                AlertDialog.Builder alertdialogname = new AlertDialog.Builder(Usb_Files_Activity.this);

                                alertdialogname.setTitle("File Name");

                                final EditText input = new EditText(getApplicationContext());
                                input.setFocusable(false);
                                input.setOnTouchListener(new View.OnTouchListener() {

                                    //	@SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        // TODO Auto-generated method stub
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Usb_Files_Activity.this, input, "");
                                        }

                                        return false;
                                    }
                                });
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertdialogname.setView(input);

                                alertdialogname.setPositiveButton("Convert", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        UsbFile file = new UsbFile() {
                                            @Nullable
                                            @Override
                                            public UsbFile search(@NonNull String path) throws IOException {
                                                return null;
                                            }

                                            @Override
                                            public boolean isDirectory() {
                                                return false;
                                            }

                                            @Override
                                            public String getName() {
                                                return null;
                                            }

                                            @Override
                                            public String getAbsolutePath() {
                                                return null;
                                            }

                                            @Override
                                            public void setName(String newName) throws IOException {

                                            }

                                            @Override
                                            public long createdAt() {
                                                return 0;
                                            }

                                            @Override
                                            public long lastModified() {
                                                return 0;
                                            }

                                            @Override
                                            public long lastAccessed() {
                                                return 0;
                                            }

                                            @Override
                                            public UsbFile getParent() {
                                                return null;
                                            }

                                            @Override
                                            public String[] list() throws IOException {
                                                return new String[0];
                                            }

                                            @Override
                                            public UsbFile[] listFiles() throws IOException {
                                                return new UsbFile[0];
                                            }

                                            @Override
                                            public long getLength() {
                                                return 0;
                                            }

                                            @Override
                                            public void setLength(long newLength) throws IOException {

                                            }

                                            @Override
                                            public void read(long offset, ByteBuffer destination) throws IOException {

                                            }

                                            @Override
                                            public void write(long offset, ByteBuffer source) throws IOException {

                                            }

                                            @Override
                                            public void flush() throws IOException {

                                            }

                                            @Override
                                            public void close() throws IOException {

                                            }

                                            @Override
                                            public UsbFile createDirectory(String name) throws IOException {
                                                return null;
                                            }

                                            @Override
                                            public UsbFile createFile(String name) throws IOException {
                                                return null;
                                            }

                                            @Override
                                            public void moveTo(UsbFile destination) throws IOException {

                                            }

                                            @Override
                                            public void delete() throws IOException {

                                            }

                                            @Override
                                            public boolean isRoot() {
                                                return false;
                                            }
                                        };
                                        try {
                                            file = root.search(FileSelezionato_sx.getName());
                                        } catch (Exception e) {
                                        }
                                        try {
                                            UsbFile[] files = root.listFiles();
                                            File UsrFromEep = EepToXml.ConvertEepToUsr_UsbFile(file);
                                            ArrayList<ArrayList<String>> points = EepToXml.getPointsFromUsr(UsrFromEep);
                                            Ricetta r = new Ricetta();
                                            r = EepToXml.CreaRicetta(points);

                                            if (r.getStepsCount() > 0) {
                                                File root = Environment.getExternalStorageDirectory();
                                                File dir;

                                                dir = new File(root.getAbsolutePath() + "/ricette");

                                                dir.mkdirs();
                                                File file_xml = new File(dir, input.getText() + ".xml");
                                                File file_usr = new File(dir, input.getText() + ".usr");
                                                try {
                                                    r.save(file_xml);
                                                    try {
                                                        r.exportToUsr(file_usr);
                                                    } catch (Exception e) {
                                                        Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                    }

                                                    ShowList_USB(LvListSX, files, estensione_list_sx);
                                                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                                                } catch (Exception e) {
                                                    Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                }
                                                TolgoSelezioneVisiva(LvListSX);
                                                Button_sxdx.setEnabled(false);
                                                Button_delete_sx.setEnabled(false);

                                                Toast.makeText(getApplicationContext(), "File convetred", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "error converting eep file ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //////////////////////////////////////////////////////////////////////////////////
                                });

                                alertdialogname.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                alertdialogname.show();
                            }
                        });

                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        alertDialog.show();
                    } catch (Exception e) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
    }

    public void On_click_Btn_sxdx_all(View view) throws IOException {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Owerwrite All");
        builder.setMessage("All file will be OverWrite, Ok?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> folders = new ArrayList<String>();
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayList<String> files_list = new ArrayList<String>();
                try {
                    UsbFile[] files = root.listFiles();

                    for (UsbFile file : files) {

                        if (file.isDirectory()) {
                            folders.add(file.getName());
                        } else {
                            String name = file.getName();
                            String estensione = "";
                            try {
                                estensione = name.substring(name.lastIndexOf(".") + 1);
                            } catch (Exception e) {

                            }

                            if (estensione.equalsIgnoreCase(estensione_list_sx.get(0)))
                                files_list.add(file.getName());
                        }

                    }
                } catch (Exception e) {
                }

                Collections.sort(folders, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                Collections.sort(files_list, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                listItems.clear();

                for (int i = 0; i < files_list.size(); i++) {
                    listItems.add(Folder_corrente_sx + "/" + files_list.get(i));
                }

                for (int i = 0; i < folders.size(); i++) {
                    try {
                        if (CheckXmlinFolder(folders.get(i))) {
                            String name = folders.get(i);

                            UsbFile file = new UsbFile() {
                                @Nullable
                                @Override
                                public UsbFile search(@NonNull String path) throws IOException {
                                    return null;
                                }

                                @Override
                                public boolean isDirectory() {
                                    return false;
                                }

                                @Override
                                public String getName() {
                                    return null;
                                }

                                @Override
                                public String getAbsolutePath() {
                                    return null;
                                }

                                @Override
                                public void setName(String newName) throws IOException {

                                }

                                @Override
                                public long createdAt() {
                                    return 0;
                                }

                                @Override
                                public long lastModified() {
                                    return 0;
                                }

                                @Override
                                public long lastAccessed() {
                                    return 0;
                                }

                                @Override
                                public UsbFile getParent() {
                                    return null;
                                }

                                @Override
                                public String[] list() throws IOException {
                                    return new String[0];
                                }

                                @Override
                                public UsbFile[] listFiles() throws IOException {
                                    return new UsbFile[0];
                                }

                                @Override
                                public long getLength() {
                                    return 0;
                                }

                                @Override
                                public void setLength(long newLength) throws IOException {

                                }

                                @Override
                                public void read(long offset, ByteBuffer destination) throws IOException {

                                }

                                @Override
                                public void write(long offset, ByteBuffer source) throws IOException {

                                }

                                @Override
                                public void flush() throws IOException {

                                }

                                @Override
                                public void close() throws IOException {

                                }

                                @Override
                                public UsbFile createDirectory(String name) throws IOException {
                                    return null;
                                }

                                @Override
                                public UsbFile createFile(String name) throws IOException {
                                    return null;
                                }

                                @Override
                                public void moveTo(UsbFile destination) throws IOException {

                                }

                                @Override
                                public void delete() throws IOException {

                                }

                                @Override
                                public boolean isRoot() {
                                    return false;
                                }
                            };
                            try {
                                file = root.search(name);
                            } catch (Exception e) {
                            }

                            try {
                                copyFileToHD(file);     //all cartelle
                                UsbFile[] files = root.listFiles();

                                ShowList_USB(LvListSX, files, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Copy folder: " + name + " error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } catch (Exception e) {
                    }
                }

                for (String item : listItems) {
                    FileSelezionato_sx = new File(item);
                    String name = FileSelezionato_sx.getName();
                    String Path_HMI = Folder_corrente_dx + "/" + name;
                    File DestinationLocation = new File(Path_HMI);

                    UsbFile file = new UsbFile() {
                        @Nullable
                        @Override
                        public UsbFile search(@NonNull String path) throws IOException {
                            return null;
                        }

                        @Override
                        public boolean isDirectory() {
                            return false;
                        }

                        @Override
                        public String getName() {
                            return null;
                        }

                        @Override
                        public String getAbsolutePath() {
                            return null;
                        }

                        @Override
                        public void setName(String newName) throws IOException {

                        }

                        @Override
                        public long createdAt() {
                            return 0;
                        }

                        @Override
                        public long lastModified() {
                            return 0;
                        }

                        @Override
                        public long lastAccessed() {
                            return 0;
                        }

                        @Override
                        public UsbFile getParent() {
                            return null;
                        }

                        @Override
                        public String[] list() throws IOException {
                            return new String[0];
                        }

                        @Override
                        public UsbFile[] listFiles() throws IOException {
                            return new UsbFile[0];
                        }

                        @Override
                        public long getLength() {
                            return 0;
                        }

                        @Override
                        public void setLength(long newLength) throws IOException {

                        }

                        @Override
                        public void read(long offset, ByteBuffer destination) throws IOException {

                        }

                        @Override
                        public void write(long offset, ByteBuffer source) throws IOException {

                        }

                        @Override
                        public void flush() throws IOException {

                        }

                        @Override
                        public void close() throws IOException {

                        }

                        @Override
                        public UsbFile createDirectory(String name) throws IOException {
                            return null;
                        }

                        @Override
                        public UsbFile createFile(String name) throws IOException {
                            return null;
                        }

                        @Override
                        public void moveTo(UsbFile destination) throws IOException {

                        }

                        @Override
                        public void delete() throws IOException {

                        }

                        @Override
                        public boolean isRoot() {
                            return false;
                        }
                    };
                    try {
                        file = root.search(FileSelezionato_sx.getName());
                    } catch (Exception e) {
                    }

                    try {
                        copyFileToHD(file);     //all file xml
                        String Filename = file.getName();
                        if (Filename.contains(".XML"))      //controllo se è uppercase
                        {
                            String newfile = Utility.Downcase_ExtensionFile(DestinationLocation);
                            DestinationLocation = new File(newfile);
                            FileSelezionato_sx = new File(FileSelezionato_sx.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".XML", ".xml")));

                        }//


                        Ricetta r = new Ricetta();
                        try {
                            r.open(DestinationLocation);
                            r.exportToUsr(new File(DestinationLocation.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                        } catch (Exception e) {
                            Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                        }
                        UsbFile[] files1 = root.listFiles();

                        ShowList_USB(LvListSX, files1, estensione_list_sx);
                        ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Copy file: " + name + " error", Toast.LENGTH_SHORT).show();
                    }
                }
                TolgoSelezioneVisiva(LvListSX);
                Button_sxdx.setEnabled(false);
                Button_delete_sx.setEnabled(false);

                Toast.makeText(Usb_Files_Activity.this, "All file copied", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Premuto no
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    private boolean CheckXmlinFolder(String folder) throws IOException {
        UsbFile folder1 = root.search(folder);
        UsbFile[] files = folder1.listFiles();

        for (UsbFile file : files) {

            if (file.isDirectory()) {
                if (CheckXmlinFolder(folder1 + "/" + file.getName())) {
                    return true;
                }
            } else {
                String name = file.getName();
                String estensione = "";
                try {
                    estensione = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {

                }
                if (estensione.equalsIgnoreCase("xml"))
                    return true;
            }
        }
        return false;
    }

    //*************************************************************************************************
    //On click del button dx verso sx
    //*************************************************************************************************
    public void On_click_Btn_dxsx(View view) throws IOException {
        final boolean[] error = {false};
        if (FileSelezionato_dx != null) {

            boolean esiste = ControlloSeEsiste(LvListSX, FileSelezionato_dx.getName());
            if (esiste) {
                // chiamo il messaggio Yes/No per sovrascrivere
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("File already exists");
                builder.setMessage("do you want overwrite existing file?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            if(copyFileToUsb(FileSelezionato_dx)) {
                                UsbFile[] files = root.listFiles();
                                ShowList_USB(LvListSX, files, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                            }else{
                                Toast.makeText(getApplicationContext(), "USB unavailable", Toast.LENGTH_SHORT).show();
                                error[0] = true;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                            error[0] = true;
                        }
                        if(!error[0]) Toast.makeText(getApplicationContext(), "File " + FileSelezionato_dx.getName() + " Copied", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Premuto no

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            } else {

                try {
                    if(copyFileToUsb(FileSelezionato_dx))
                    {
                        UsbFile[] files = root.listFiles();
                        ShowList_USB(LvListSX, files, estensione_list_sx);
                        ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                    }else {
                        Toast.makeText(getApplicationContext(), "USB unavailable", Toast.LENGTH_SHORT).show();
                        error[0] = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                    error[0] = true;
                }

               if(!error[0]) Toast.makeText(getApplicationContext(), "File " + FileSelezionato_dx.getName() + " Copied", Toast.LENGTH_LONG).show();
            }

            TolgoSelezioneVisiva(LvListDX);
            Button_dxsx.setEnabled(false);
            Button_delete_dx.setEnabled(false);
        }


    }

    public void On_click_Btn_dxsx_all(View view) throws IOException {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Owerwrite All");
        builder.setMessage("All file will be OverWrite, Ok?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Premuto Yes
                ArrayList<String> folders = new ArrayList<String>();
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayList<String> files = new ArrayList<String>();
                File[] allEntries = new File(Folder_corrente_dx).listFiles();

                for (int i = 0; i < allEntries.length; i++) {
                    if (allEntries[i].isDirectory()) {
                        folders.add(allEntries[i].getName());
                    } else if (allEntries[i].isFile()) {
                        String name = allEntries[i].getName();
                        String estensione = "";
                        try {
                            estensione = name.substring(name.lastIndexOf(".") + 1);
                        } catch (Exception e) {

                        }

                        if (estensione.equalsIgnoreCase(estensione_list_dx))
                            files.add(allEntries[i].getName());
                    }
                }

                Collections.sort(folders, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                Collections.sort(files, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                listItems.clear();

                for (int i = 0; i < files.size(); i++) {
                    listItems.add(Folder_corrente_dx + "/" + files.get(i));
                }

                for (int i = 0; i < folders.size(); i++) {
                    FileSelezionato_dx = new File(Folder_corrente_dx + "/" + folders.get(i));
                    if (FileSelezionato_dx != null) {
                        String name = FileSelezionato_dx.getName();

                        try {
                            if(copyFileToUsb(FileSelezionato_dx)) {

                                UsbFile[] files1 = root.listFiles();
                                ShowList_USB(LvListSX, files1, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                            }else
                                Toast.makeText(getApplicationContext(), "USB unavailable", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy folder: " + name + " error", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                for (String item : listItems) {
                    FileSelezionato_dx = new File(item);
                    if (FileSelezionato_dx != null) {
                        String name = FileSelezionato_dx.getName();

                        try {
                            if(copyFileToUsb(FileSelezionato_dx)) {
                                UsbFile[] files1 = root.listFiles();

                                ShowList_USB(LvListSX, files1, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                            }else
                                Toast.makeText(getApplicationContext(), "USB unavailable", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy file: " + name + " error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                TolgoSelezioneVisiva(LvListDX);
                Button_dxsx.setEnabled(false);
                Button_delete_dx.setEnabled(false);

                Toast.makeText(Usb_Files_Activity.this, "All file copied", Toast.LENGTH_LONG).show();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Premuto no
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    //*************************************************************************************************
    //
    //*************************************************************************************************
    private boolean ControlloSeEsiste(ListView lvList, String filename) {
        Adapter lista = lvList.getAdapter();
        if(lista != null) {
            for (int i = 0; i < lista.getCount(); i++) {
                String item = (String) lista.getItem(i);
                if (item.equals(filename)) return true;

            }
        }
        return false;
    }

    //*************************************************************************************************
    // CopyFileToUsb
    //*************************************************************************************************
    public Boolean copyFileToUsb(File sourceFile)
            throws IOException {

        String[] folder_split = Folder_corrente_sx.split("/");

        UsbFile folder = root;
        if(folder !=null) {


            for (String str : folder_split) {
                if (!str.equals("")) {
                    folder = folder.search(str);
                }
            }

            if (sourceFile.isDirectory()) {//è un file o folder?
                UsbFile f = folder.search(sourceFile.getName());
                if (f != null) f.delete();

                UsbFile folder1 = folder.createDirectory(sourceFile.getName());

                File[] allEntries = new File(sourceFile.getPath()).listFiles();
                if (allEntries != null) {
                    for (File files : allEntries) {
                        UsbFile file = folder1.createFile(files.getName());

                        OutputStream os = new UsbFileOutputStream(file);

                        os.write(getByte(files.getPath()));
                        os.close();
                    }
                }
            } else {
                UsbFile f = folder.search(sourceFile.getName());
                if (f != null) f.delete();

                UsbFile file = folder.createFile(sourceFile.getName());


                OutputStream os = new UsbFileOutputStream(file);

                os.write(getByte(sourceFile.getPath()));
                os.close();
            }


            return true;
        }
        return false;
    }

    private byte[] getByte(String path) {
        byte[] getBytes = {};
        try {
            File file = new File(path);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBytes;
    }

    //*************************************************************************************************
    // CopyFileToHD
    //*************************************************************************************************
    public Boolean copyFileToHD(UsbFile sourceFile)
            throws IOException {
        if (sourceFile.isDirectory()) {//è un file o folder?
            Folder_sx = Folder_corrente_sx;
            Folder_dx = Folder_corrente_dx;
            Copy(sourceFile);
        } else {
            File f = new File(Folder_corrente_dx + "/" + sourceFile.getName());
            if (f != null) f.delete();

            f.createNewFile();

            InputStream is = UsbFileStreamFactory.createBufferedInputStream(sourceFile, currentFs);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(buffer.toByteArray());
            outputStream.close();
        }
        return true;
    }


    public void Copy(UsbFile sourceFile) throws IOException {
        File f = new File(Folder_dx + "/" + sourceFile.getName());
        if (f != null) f.delete();

        f.mkdir();

        String[] folder_split = Folder_sx.split("/");

        UsbFile folder = root;
        for (String str : folder_split) {
            if (!str.equals("")) {
                folder = folder.search(str);
            }
        }
        UsbFile[] files = folder.search(f.getName()).listFiles();
        for (UsbFile file : files) {
            if (file.isDirectory()) {
                Folder_sx = Folder_sx + "/" + sourceFile.getName();
                Folder_dx = Folder_dx + "/" + sourceFile.getName();
                Copy(file);
                Folder_sx = SubString.SubstringExtensions.BeforeLast(Folder_sx, "/");
                Folder_dx = SubString.SubstringExtensions.BeforeLast(Folder_dx, "/");
            } else {
                InputStream is = UsbFileStreamFactory.createBufferedInputStream(file, currentFs);

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                f = new File(Folder_dx + "/" + sourceFile.getName() + "/" + file.getName());
                FileOutputStream outputStream = new FileOutputStream(f);
                outputStream.write(buffer.toByteArray());
                outputStream.close();
            }
        }
    }

    //*************************************************************************************************
    //On click del button delete sx
    //*************************************************************************************************
    public void On_click_Btn_delete_sx(View view) throws IOException {

        if ((FileSelezionato_sx != null)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm delete");
            builder.setMessage("Are you sure to delete file?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Premuto Yes
                    try {
                        UsbFile file;
                        String[] folder_split = Folder_corrente_sx.split("/");

                        UsbFile folder = root;
                        for (String str : folder_split) {
                            if (!str.equals("")) {
                                folder = folder.search(str);
                            }
                        }
                        file = folder.search(FileSelezionato_sx.getName());
                        file.delete();
                        ShowList_USB(LvListSX, folder.listFiles(), estensione_list_sx);
                    } catch (Exception e) {
                    }


                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Premuto no
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            Button_delete_sx.setEnabled(false);
            Button_sxdx.setEnabled(false);
        }

    }

    //*************************************************************************************************
    // On click del button Delete list dx
    //*************************************************************************************************
    public void On_click_Btn_delete(View view) throws IOException {

        if ((FileSelezionato_dx != null)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm delete");
            builder.setMessage("Are you sure to delete file?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Premuto Yes
                    deleteRecursive(FileSelezionato_dx);
                    dialog.dismiss();
                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Premuto no
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            Button_delete_dx.setEnabled(false);
            Button_dxsx.setEnabled(false);
        }

    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }


    //**************************************************************************************************
    //
    //**************************************************************************************************
    //nascondo navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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
    //On click del button Exit
    //*************************************************************************************************
    public void onClick_Button_exit(View view) throws IOException {
        finish();
    }

}

