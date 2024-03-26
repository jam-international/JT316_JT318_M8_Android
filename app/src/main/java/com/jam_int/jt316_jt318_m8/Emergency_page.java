package com.jam_int.jt316_jt318_m8;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import communication.MSysFileInfo;
import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.SmartAlarm;
import communication.SmartAlarms;
import communication.VFKBook;

import static android.content.ContentValues.TAG;
/*
VN3804 ID pagina
1001 = MainActivity
1002 = tool
1003 = modificaProgramma
1004 =
1005 = Page Test I O
1006 = Parametri
 */
public class Emergency_page extends Activity {
    ShoppingList sl;
    Thread thread_emerg;
    SmartAlarms.OnAlarmListener AlarmListener = null;
    boolean Thread_Running = false, StopThread = false,first_cycle = true,Enable_visualizzazione_errori = false,Leggi_Emergenze=false;
    TextView TView_barra_bassa, TextView_allarmi,TextView_testo_PLC_ver,ver_softwareHMI,TextView_testo_Firmware,TextView_macchina,TextView_lingua,TextView_programma_in_esecuzione,TextView_riga_in_esecuzione,
            TextView_cnt_comunicazione,Allarm_textView;
    Button btn_connection_status,Button_verde,Btn_eth_operational,Button_MoreError;
    String info ="",str_allarmi="", Machine_model="",ver_firmware,str_allarmi_old, str_allarmi_more = "";;
    MultiCmdItem mci_tasto_verde,mci_Vb7903_Reset_Ch1,mci_CH1_in_emergenza,mc1_Vb50_macchina_azzerata,Multicmd_vb4503_Cn_allarme,Multicmd_i4_pressostato,
            Multicmd_vb7013_ax1_home,Multicmd_vb7033_ax2_home,Multicmd_vb7053_ax3_home,Multicmd_vb7073_ax4_home,Multicmd_vb7093_ax5_home,Multicmd_vb7113_ax6_home,MultiCmd_VA31_Ver_PLC,
           MultiCmd_livello_batteria,Multicmd_dtDB_prog_name,MultiCmd_Vn4_Warning,MultiCmd_Vb7814_Eth_operational,MultiCmd_Debug14_prog_cn_in_esecuzione,MultiCmd_Debug8_riga_cn_in_esecuzione,
            MultiCmd_Vn320_modello_macchina,MultiCmd_Vn2_allarmi_da_CN,MultiCmd_ver_macchine,Multicmd_i1_pulsanti_start,Multicmd_ingresso_pressostato;

    MainActivity.Mci_write Mci_write_dtDB_prog_name,Mci_write_Vn4_Warning,Mci_Vn2_allarmi_da_CN;
    Double warning_old=0.0d;
    String[] tab_names = new String[]{};
    CheckBox CheckBox_ax5_home,CheckBox_ax6_home;
    List<String> list_allarmi = new ArrayList<>();
    int mc_stati_riarmo = 0,mc_stati_visualizzazione_allarmi=0;
    ArrayList<SmartAlarm> ListaAllarmiCN = new ArrayList<SmartAlarm>();
    MultiCmdItem[] mci_array_read_all;
    ImageView ImageView_battery;
    Context context;
    MachineLog machinelog = new MachineLog();
    final private static int RESULT_PAGE_LOAD_UDF = 102;
    Boolean path_udf_presente = false;
    int[] emebuf_old;
    int cnt=0,cnt_comunicazione = 0;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static  final String MACHINE_LOG_FILE = "MachineLog.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergenza);
        context =this;

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }





        Gestione_lingua();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera

        TView_barra_bassa = (TextView)findViewById(R.id.textView_barra_sotto);
        TextView_allarmi = (TextView)findViewById(R.id.textView_allarmi);
        TextView_testo_PLC_ver = (TextView)findViewById(R.id.textView_testo_PLC_ver);
        ver_softwareHMI = (TextView) findViewById(R.id.textView_testo_HMI_ver);
        TextView_testo_Firmware = (TextView) findViewById(R.id.textView_testo_Firmware);
        TextView_macchina = (TextView)findViewById(R.id.textView_macchina);
        TextView_programma_in_esecuzione = (TextView)findViewById(R.id.textView_programma_in_esecuzione);
        TextView_riga_in_esecuzione = (TextView)findViewById(R.id.textView_riga_in_esecuzione);
        TextView_cnt_comunicazione = (TextView)findViewById(R.id.textView_cnt_comunicazione);
        Allarm_textView = findViewById(R.id.allarm_textView);

        btn_connection_status = (Button) findViewById(R.id.btn_connection_status);
        Button_verde = (Button) findViewById(R.id.button_verde);
        Btn_eth_operational  = (Button) findViewById(R.id.btn_eth_operational);
        Button_MoreError = (Button) findViewById(R.id.button_allarm_more);
        CheckBox_ax5_home = (CheckBox)findViewById((R.id.checkBox_ax5_home));
        CheckBox_ax6_home = (CheckBox)findViewById((R.id.checkBox_ax6_home));
        ImageView_battery = (ImageView)findViewById(R.id.imageView_battery);
        ImageView_battery.setImageResource(R.drawable.battery_full);
        ImageView_battery.setVisibility(View.GONE);

        info = "Info device: " + android.os.Build.MODEL +" brand = "+android.os.Build.BRAND +" OS version = "+android.os.Build.VERSION.RELEASE + " SDK version = " +android.os.Build.VERSION.SDK_INT;
        TView_barra_bassa.setText(info);

        AlarmListener = new SmartAlarms.OnAlarmListener()
        {

            @Override
            public void onAlarm(SmartAlarm al) {
                try {
                    ListaAllarmiCN.add(al);
                }catch (Exception e){}
            }
        };

        try {
            int versionCode = BuildConfig.VERSION_CODE;
            ver_softwareHMI.setText(""+versionCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sl = SocketHandler.getSocket();
            if(sl == null) {    //se è la prima accensione entro altrimenti se provengo da un'altra pagina non c'è bisogno di instanziare nnuovamente
                sl = new ShoppingList("192.168.0.92", 12001, 1000d, 2000d);
                sl.getAlarms().registerAlarmListener(AlarmListener);


                
                SocketHandler.setSocket(sl);



            }
        }catch (Exception e)
        {

        }


        sl.Clear("Io");
        VFKBook.Load(this);     //serve per usare variabili MultiCmdItem.dtDB
        sl.setVFK(VFKBook.getVFK());    //serve per usare variabili MultiCmdItem.dtDB




        mci_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        mci_Vb7903_Reset_Ch1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 7903, MultiCmdItem.dpNONE);
        mci_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        mc1_Vb50_macchina_azzerata = sl.Add("Io", 1, MultiCmdItem.dtVB, 50, MultiCmdItem.dpNONE);
        Multicmd_vb4503_Cn_allarme = sl.Add("Io", 1, MultiCmdItem.dtVB, 4503, MultiCmdItem.dpNONE);
        Multicmd_i4_pressostato = sl.Add("Io", 1, MultiCmdItem.dtDI, 4, MultiCmdItem.dpNONE);
        Multicmd_vb7013_ax1_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7013, MultiCmdItem.dpNONE);
        Multicmd_vb7033_ax2_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7033, MultiCmdItem.dpNONE);
        Multicmd_vb7053_ax3_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7053, MultiCmdItem.dpNONE);
        Multicmd_vb7073_ax4_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7073, MultiCmdItem.dpNONE);
        Multicmd_vb7093_ax5_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7093, MultiCmdItem.dpNONE);
        Multicmd_vb7113_ax6_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7113, MultiCmdItem.dpNONE);
        MultiCmd_VA31_Ver_PLC = sl.Add("Io", 1, MultiCmdItem.dtVA, 31, MultiCmdItem.dpNONE);
        MultiCmd_livello_batteria = new MultiCmdItem(1, MultiCmdItem.dtGP, 6, MultiCmdItem.dpNONE, sl);
        Multicmd_dtDB_prog_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Vn4_Warning = sl.Add("Io", 1, MultiCmdItem.dtVN, 4, MultiCmdItem.dpNONE);
        MultiCmd_Vb7814_Eth_operational = sl.Add("Io", 1, MultiCmdItem.dtVB, 7814, MultiCmdItem.dpNONE);
        MultiCmd_Debug14_prog_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 14 , MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug8_riga_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 8 , MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Vn320_modello_macchina = sl.Add("Io", 1, MultiCmdItem.dtVN, 320, MultiCmdItem.dpNONE);
        MultiCmd_Vn2_allarmi_da_CN = sl.Add("Io", 1, MultiCmdItem.dtVN, 2, MultiCmdItem.dpNONE);
        MultiCmd_ver_macchine = sl.Add("Io", 1, MultiCmdItem.dtVN, 320, MultiCmdItem.dpNONE);
        Multicmd_i1_pulsanti_start = sl.Add("Io", 1, MultiCmdItem.dtDI, 2, MultiCmdItem.dpNONE);
        Multicmd_ingresso_pressostato = sl.Add("Io", 1, MultiCmdItem.dtDI, 4, MultiCmdItem.dpNONE);

        Mci_write_dtDB_prog_name = new MainActivity.Mci_write(); Mci_write_dtDB_prog_name.mci = Multicmd_dtDB_prog_name;
        Mci_write_Vn4_Warning  = new MainActivity.Mci_write(); Mci_write_Vn4_Warning.mci = MultiCmd_Vn4_Warning;
        Mci_Vn2_allarmi_da_CN = new MainActivity.Mci_write(); Mci_Vn2_allarmi_da_CN.mci = MultiCmd_Vn2_allarmi_da_CN;

        mci_array_read_all = new MultiCmdItem[]{
                Multicmd_vb4503_Cn_allarme,Multicmd_i4_pressostato, Multicmd_vb7013_ax1_home,Multicmd_vb7033_ax2_home,Multicmd_vb7053_ax3_home,
                Multicmd_vb7073_ax4_home,Multicmd_vb7093_ax5_home,Multicmd_vb7113_ax6_home,MultiCmd_VA31_Ver_PLC,MultiCmd_livello_batteria,MultiCmd_Vn4_Warning,
                MultiCmd_Vb7814_Eth_operational,MultiCmd_Debug14_prog_cn_in_esecuzione,MultiCmd_Debug8_riga_cn_in_esecuzione,MultiCmd_Vn2_allarmi_da_CN,
                Multicmd_i1_pulsanti_start,Multicmd_ingresso_pressostato



        };

        Inizializzo_dati_macchina();



        //thread
        if (!Thread_Running) {

            MyAndroidThread_Emg myTask_emg = new MyAndroidThread_Emg(Emergency_page.this);
            thread_emerg = new Thread(myTask_emg, "Emg myTask");
            thread_emerg.start();
            Log.d("JAM TAG", "Start Emergency Thread");
        }



    }
    //**********************************************************************************************
    // Gestione_lingua
    //**********************************************************************************************
    private void Gestione_lingua() {
        String languagename = Locale.getDefault().getDisplayLanguage();
        String country = Locale.getDefault().getCountry();



        switch (languagename){

            case "italiano":

                String localeCode = "it";
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(new Locale(localeCode.toLowerCase()));
                resources.updateConfiguration(configuration, displayMetrics);
                configuration.locale = new Locale(localeCode.toLowerCase());
                resources.updateConfiguration(configuration, displayMetrics);

                break;
            case "English":

                localeCode = "es";
                resources = getResources();
                displayMetrics = resources.getDisplayMetrics();
                configuration = resources.getConfiguration();
                configuration.setLocale(new Locale(localeCode.toLowerCase()));
                resources.updateConfiguration(configuration, displayMetrics);
                configuration.locale = new Locale(localeCode.toLowerCase());
                resources.updateConfiguration(configuration, displayMetrics);
                break;
        }

     //   Locale current = getResources().getConfiguration().locale;

       // String f = languagename + country+current;


    }


    //**********************************************************************************************
    // Inizializzo_dati_macchina
    //**********************************************************************************************
    private void Inizializzo_dati_macchina() {

        //controllo se c'è la cartella JamData altrimenti la prendo da assets
        File file = new File(Environment.getExternalStorageDirectory() + "/JamData/info_Jam.txt");
        if(!file.exists()) {
            try {

                copyAssets("JamData", Environment.getExternalStorageDirectory() + "/");
            }
            catch (Exception e) {
            }
        }
        //carico modello della macchina
        try {
            Values.File_XML_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());

            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());

            TextView_macchina.setText(Machine_model);



/*
            //controllo se esiste il file di cucitura
            ArrayList<String> path_and_folder =  new ArrayList<String>();
            path_and_folder.add("C:\\cnc\\userdata\\");

            String filename1 = SubString.SubstringExtensions.After( Values.File_XML_path,"/");
            String filename =filename1.replace(".xml",".udf");

            path_and_folder.add(filename);

            boolean PresenzaProgramma = CheckPresenzaProgramma(path_and_folder);

*/


        }catch (Exception e){
            Toast.makeText(this, "error Leggi_campo", Toast.LENGTH_SHORT).show();
        }



        if(!file.exists() || (file.exists() && Machine_model.equals("null"))) {
            try {

                copyAssets("JamData", Environment.getExternalStorageDirectory() + "/");

             //   AlertDialog = true;
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage("Machine:");
                final Spinner input = new Spinner(this);
                input.setFocusable(false);
                String[] arraySpinner = new String[]{
                        "JT316M", "JT318M", "JT350M","JT318M_1000x800"
                };
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(adapter);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                try {
                                    String modello_selezionato = input.getSelectedItem().toString();
                                    Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "MachineModel", null, null, "Machine_model", modello_selezionato, getApplicationContext());
                                } catch (Exception e) {

                                }
                                //controllo se c'è la cartella ricette altrimenti la prendo da assets
                                File file_ricette = new File(Environment.getExternalStorageDirectory() + "/ricette");
                                if (!file_ricette.exists()) {

                                    String path = Environment.getExternalStorageDirectory() + "/";
                                    copyAssets("ricette", Environment.getExternalStorageDirectory() + "/");
                                }





                               // AlertDialog = false;


                                TextView_macchina.setText(input.getSelectedItem().toString());

                            }
                        });
                alertDialog.show();
            } catch (Exception e) {

            }
        }
        if(Machine_model.equals(""))  Machine_model = "null";
        Values.Machine_model = Machine_model;




        //lavoro su filelog

        File MachineLog = new File(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt ");
        if(!MachineLog.exists()) {

            try {
                FileOutputStream oFile = new FileOutputStream(MachineLog, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        long filesize = MachineLog.length();
        if(filesize > 5000000) {       //50000000 se la dimensione supera 50Mega, cancello i primi 10 Mega portando i secondi 5Mega in cima al file
            try {
                DimezzaFileLog(MachineLog,filesize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //controllo se c'è la cartella tutorial altrimenti la creo
        File tutorial_folder = new File(Environment.getExternalStorageDirectory() + "/Tutorial");
        if(!tutorial_folder.exists()) {
            boolean success = tutorial_folder.mkdirs();
            if (success) {
                // Do something on success
            } else {
                Toast.makeText(getApplicationContext(),"Can't create tutorial folder",Toast.LENGTH_SHORT).show();
            }
        }


        if(!new File(Environment.getExternalStorageDirectory()  + "/ricette").exists()) {

            String path = Environment.getExternalStorageDirectory() + "/";
            copyAssets("ricette", Environment.getExternalStorageDirectory() + "/");

           // new File(Environment.getExternalStorageDirectory()  + "/ricette").mkdir();
        }

        //inizializzo la password se non c'è
        File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
        if(!password.exists())
        {
            try {
                password.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(password));

                bw.write(String.format("%s%n", "67872"));
                bw.write(String.format("%s%n", "67873"));

                bw.close();
            }catch (Exception e)
            {}

        }



    }

    //**********************************************************************************************
    @Override
    public void onResume(){
        super.onResume();



        StopThread = false;
        if (!Thread_Running) {

            MyAndroidThread_Emg myTask_emg = new MyAndroidThread_Emg(Emergency_page.this);
            thread_emerg = new Thread(myTask_emg, "Emg myTask");
            thread_emerg.start();
            Log.d("JAM TAG", "Start Emergency Thread");
        }


    }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        try {
            KillThread();
        }catch (Exception e){}

    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();


    }
    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {

        super.onDestroy();




    }
    //**********************************************************************************************
    // MyAndroidThread_Emg
    //**********************************************************************************************
    class MyAndroidThread_Emg implements Runnable {
        Activity activity;
        boolean rc_error;
        public MyAndroidThread_Emg(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;




                try {
                    Thread.sleep((long) 300d);
                    if (StopThread) {
                        Thread_Running = false;
                        return;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }
                if (sl.IsConnected()) {
                    cnt_comunicazione++;
                    rc_error = false;
                    sl.Clear();
                    if(first_cycle){

                        first_cycle = false;
                        ver_firmware = get_ver_firmware();

                        switch (Machine_model){
                            case "JT318M_1000x800":
                                MultiCmd_ver_macchine.setValue(3181.0d);
                                break;

                            case "JT316M":
                                MultiCmd_ver_macchine.setValue((316.0d));
                                break;
                            case  "JT318M":
                                MultiCmd_ver_macchine.setValue(318.0d);
                                break;
                            case "JT350M":
                                MultiCmd_ver_macchine.setValue(350.0d);
                                break;
                            default:
                                break;
                        }
                        sl.WriteItem(MultiCmd_ver_macchine);

                        MultiCmd_Vn4_Warning.setValue(0.0d);
                       sl.WriteItem(MultiCmd_Vn4_Warning);

                    }

                    ScrivoStringaDB(Mci_write_dtDB_prog_name);
                    ScrivoVbVnVq(Mci_write_Vn4_Warning);
                    ScrivoVbVnVq(Mci_Vn2_allarmi_da_CN);

                    try {
                        sl.ReadItems(mci_array_read_all);
                        if (sl.getReturnCode() != 0) {
                            rc_error = true;
                        }



                    }
                    catch (Exception e){
                        Log.e(TAG, "Persa connessione");
                    }



                    if (!rc_error) {
                        mc_stati_riarmo();
                        //Lettura delle emergenze attive
                        if (Leggi_Emergenze) LeggoEmergenze();
                    }


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!rc_error) {
                                ShowFirmwareVersion();
                                switch (Machine_model) {
                                    case "JT318M":
                                    case "JT316M":
                                    case "JT318M_1000x800":
                                        CheckBox_ax5_home.setVisibility(View.GONE);
                                        CheckBox_ax6_home.setVisibility(View.GONE);
                                        break;
                                    case "JT350M":

                                        break;
                                    default:
                                        break;
                                }
                                Verifica_batteria();
                                Icona_tasto_verde();
                                Icona_IP(sl);
                                CheckBox();
                                GestiscoWarning();
                                Riga_CN_Esecuzione();
                                if(cnt_comunicazione>1000)cnt_comunicazione =0;
                                TextView_cnt_comunicazione.setText("Cnt: "+cnt_comunicazione);

                                //controllo se all'accensione il file udf di cucitura è presente dentro al CN
                               // if (!path_udf_presente) {
                               //     str_allarmi = str_allarmi + "Missing sewing program file";
                              //  }


                                if (!str_allarmi.equals(str_allarmi_old)) {

                                    TextView_allarmi.setText(str_allarmi);
                                 ////////rimettere   MachineLog.AddToLogFile(str_allarmi);
                                    str_allarmi_old = str_allarmi;
                                    //str_allarmi = "";
                                }
                                //else TextView_allarmi.setText("");

                                TextView_testo_PLC_ver.setText((String) MultiCmd_VA31_Ver_PLC.getValue().toString());

                                if((Double)MultiCmd_Vn2_allarmi_da_CN.getValue() !=0){
                                    Double err = (Double)MultiCmd_Vn2_allarmi_da_CN.getValue();
                                    int i = err.intValue();
                                    String Stringa_allarme = "";

                                    tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                                    Stringa_allarme = tab_names[i];
                                    str_allarmi = str_allarmi + Stringa_allarme;
                                    Mci_Vn2_allarmi_da_CN.valore = 0.0d;
                                    Mci_Vn2_allarmi_da_CN.write_flag = true;
                                    TextView_allarmi.setText(str_allarmi);
                                    str_allarmi_old = str_allarmi;
                                }


                                ScriviEmergenza();

                            }


                        }


                    });


                }else{
                    sl.Connect();

                    if (sl.IsConnected()) {


                        //Inizializzazione della lingua sul CN (Una sola volta dopo la connessione)
                        MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 3081, MultiCmdItem.dpNONE, sl);
                        String lingua_device = Locale.getDefault().getLanguage();

                        switch (lingua_device){

                            case "it":
                                mci.setValue("it-IT");
                                break;

                            case "en":
                                mci.setValue("gb-GB");
                                break;

                            case "tr":
                            mci.setValue("tr-TR");
                            break;


                            default:
                                mci.setValue("gb-GB");
                                break;

                        }

                        //String cul = prj.getTextDB().getCultureName();

                        sl.WriteItem(mci);
                    }
                }
            }


        }
        //*********************************************************************************************
        //Riga_CN_Esecuzione
        //*********************************************************************************************
        private void GestiscoWarning() {
            double Warning = (Double)MultiCmd_Vn4_Warning.getValue();
            try {
                if (Warning > 0.0d)  {
                    if(Warning != warning_old) {
                        warning_old = Warning;

                        tab_names = getResources().getStringArray(R.array.warning_vn4);
                        String warning_string = tab_names[(int) Warning];


                        //        str_allarmi = warning_string;

                        AlertDialog.Builder warningDialog = new AlertDialog.Builder(context);
                        // Setting Dialog Title
                        warningDialog.setTitle("Warning");

                        warningDialog.setMessage(warning_string)
                                .setCancelable(false)
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        warning_old = 0.0d;        //cancello chiamata
                                        Mci_write_Vn4_Warning.valore = 0.0d;
                                        Mci_write_Vn4_Warning.write_flag = true;
                                    }
                                });

                        AlertDialog alert = warningDialog.create();
                        alert.show();



                    }
                }

            }catch (Exception e){}

        }
        //**************************************************************************************************
        //ScrivoVbVnVq
        //**************************************************************************************************
        private void ScrivoVbVnVq(MainActivity.Mci_write variabile) {
            if( variabile.write_flag == true){
                variabile.write_flag = false;
                if(variabile.tipoVariabile == MainActivity.Mci_write.TipoVariabile.VQ)
                    variabile.mci.setValue(variabile.valore*1000);
                else
                    variabile.mci.setValue(variabile.valore);
                sl.WriteItem(variabile.mci);

            }
        }
        //*********************************************************************************************
        //EScrivoStringaDB
        //*********************************************************************************************
        private void ScrivoStringaDB(MainActivity.Mci_write mci_write) {
            if(mci_write.write_flag) {
                mci_write.mci.setValue(mci_write.path_file);
                sl.WriteItem(mci_write.mci);
                mci_write.write_flag = false;
            }

        }
        //**********************************************************************************************
        // Verifica_batteria
        //**********************************************************************************************
        private void Verifica_batteria() {
            if((Double)MultiCmd_livello_batteria.getValue() ==2.0d){
                ImageView_battery.setVisibility(View.VISIBLE);
                ImageView_battery.setImageResource(R.drawable.battery_low);
            }
        }
        //*********************************************************************************************
        //Riga_CN_Esecuzione
        //*********************************************************************************************
        private void Riga_CN_Esecuzione() {
            String prog = (String)MultiCmd_Debug14_prog_cn_in_esecuzione.getValue();
            String st1 = getString( R.string.Prog_esecuzione);
            TextView_programma_in_esecuzione.setText( st1 + prog);
            Double riga = (Double)MultiCmd_Debug8_riga_cn_in_esecuzione .getValue();
            String st2 = getString(R.string.Riga_esecuzione);
            TextView_riga_in_esecuzione.setText(st2 + Double. toString( riga));
        }
        //**********************************************************************************************
        // ShowFirmwareVersion
        //**********************************************************************************************
        private void ShowFirmwareVersion() {
            try {
                String ver1 = ver_firmware.substring(6, (ver_firmware.length() - 4));
                TextView_testo_Firmware.setText(ver1);
            }catch (Exception e){
                TextView_testo_Firmware.setText("missing firmware");
            }
        }
        //**********************************************************************************************
        // get_ver_firmware
        //**********************************************************************************************
        private String get_ver_firmware() {
            String ret="";
            try {
                ArrayList<String> Folder_and_file = new ArrayList<String>();
                MSysFileInfo fi = new MSysFileInfo();
                String path_folder = "B:\\fw\\*.*";
                fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                if (fi != null)    //se la cartella contiene almeno un file
                {
                    Folder_and_file.add("B:\\fw\\" + fi.FName);
                    return  Folder_and_file.get(0);
                }
            } catch (Exception ex) {
            }

            return ret;
        }

        //**********************************************************************************************
        // CheckBox
        //**********************************************************************************************
        private void CheckBox() {
            CheckBox CheckBox_status_cn = (CheckBox) findViewById(R.id.checkBox_status_cn);
            CheckBox CheckBox_pressostato = (CheckBox) findViewById(R.id.checkBox_pressostato);
            CheckBox CheckBox_ax1_home = (CheckBox) findViewById(R.id.checkBox_ax1_home);
            CheckBox CheckBox_ax2_home = (CheckBox) findViewById(R.id.checkBox_ax2_home);
            CheckBox CheckBox_ax3_home = (CheckBox) findViewById(R.id.checkBox_ax3_home);
            CheckBox CheckBox_ax4_home = (CheckBox) findViewById(R.id.checkBox_ax4_home);
            CheckBox CheckBox_ax5_home = (CheckBox) findViewById(R.id.checkBox_ax5_home);
            CheckBox CheckBox_ax6_home = (CheckBox) findViewById(R.id.checkBox_ax6_home);




            if((Double)Multicmd_vb4503_Cn_allarme.getValue() == 0.0d){
                CheckBox_status_cn.setChecked(true);
            }else CheckBox_status_cn.setChecked(false);

            if((Double)Multicmd_i4_pressostato.getValue() == 0.0d){
                CheckBox_pressostato.setChecked(true);
            }else  CheckBox_pressostato.setChecked(false);

            if((Double)Multicmd_vb7013_ax1_home.getValue() == 1.0d){
                CheckBox_ax1_home.setChecked(true);
            }else  CheckBox_ax1_home.setChecked(false);

            if((Double)Multicmd_vb7033_ax2_home.getValue() == 1.0d){
                CheckBox_ax2_home.setChecked(true);
            }else  CheckBox_ax2_home.setChecked(false);

            if((Double)Multicmd_vb7053_ax3_home.getValue() == 1.0d){
                CheckBox_ax3_home.setChecked(true);
            }else  CheckBox_ax3_home.setChecked(false);

            if((Double)Multicmd_vb7073_ax4_home.getValue() == 1.0d){
                CheckBox_ax4_home.setChecked(true);
            }else  CheckBox_ax4_home.setChecked(false);

            if((Double)Multicmd_vb7093_ax5_home.getValue() == 1.0d){
                CheckBox_ax5_home.setChecked(true);
            }else  CheckBox_ax5_home.setChecked(false);

            if((Double)Multicmd_vb7113_ax6_home.getValue() == 1.0d){
                CheckBox_ax6_home.setChecked(true);
            }else  CheckBox_ax6_home.setChecked(false);



        }

        //**********************************************************************************************
        // Icona_tasto_verde
        //**********************************************************************************************
        private void Icona_tasto_verde() {

            switch (mc_stati_riarmo){
                case 0:
                    Button_verde.setBackgroundResource(R.drawable.tasto_verde);
                    break;
                case 10:
                case 20:
                    Button_verde.setBackgroundResource(R.drawable.dito1);
                    break;
                case 30:
                    Button_verde.setBackgroundResource(R.drawable.casetta);
                    break;

                default:
                    break;
            }

        }
        //*********************************************************************************************
    //mc_stati_riarmo
    //*********************************************************************************************
        private void mc_stati_riarmo() {
            switch (mc_stati_riarmo) {
                case 0: //arrivo qui quando parte questa activity, guardo come è messo il tasto verde, se non è premuto allora è una prima accensione oppure è stato premuto emergenza
                    //se invece è ancora acceso vuol dire che sono arrivato da una emergenza CN
                    sl.ReadItem(mci_tasto_verde);
                    if (sl.getReturnCode() == 0) {
                        if ((Double) mci_tasto_verde.getValue() == 1.0d)    //se premo pulsante verde salto a 10
                        {
                            mc_stati_riarmo = 1;    //emergenza da CN
                        }else
                            mc_stati_riarmo = 5;    //tasto verde no premuto
                    }
                    break;
                //Inizio ZONA Emergenza CN
                case 1: //arrivo da una emergenza che non ha sganciato il tasto verde = emergenza da CN

                    Leggi_Emergenze = true;     //faccio leggere glia allarmi CN dal Thread di comunicazione
                    mc_stati_riarmo = 2;        //salto e aspetto la fine della lettura degli allarmi CN
                    break;

                case 2:
                    if(!Leggi_Emergenze) {      //se ho finito di leggere gli allarmi CN...
                        if (list_allarmi.size() > 0) {   //se ho almeno un allarme..
                            mc_stati_visualizzazione_allarmi = 60;  //allora chiamo la visualizzazione dalla procedura di visualizzazione del Thread GUI

                        }else
                        {
                            mc_stati_visualizzazione_allarmi = 70;
                        }
                        mc_stati_riarmo = 32;   //se sono arrivato qui vuol dire che il tasto verde è acceso allora vado ad aspettare la pressione dell'emergenza
                    }
                    break;
                //fine ZONA Emergenza CN

                //Inizio ZONA gestione tasto Verde spento
                case 5:
                    mc_stati_visualizzazione_allarmi = 10;  //scrivo che bisogna premere il tasto verde
                    sl.ReadItem(mci_tasto_verde);
                    if (sl.getReturnCode() == 0) {
                        if ((Double) mci_tasto_verde.getValue() == 1.0d)    //se premo pulsante verde salto a 10
                        {
                            mc_stati_riarmo = 10;
                        }
                    }
                    break;
                //Fine ZONA gestione tasto Verde spento

                //Inizio ZONA premere su touch screen
                case 10: //aspetto click touch
                    sl.ReadItem(mci_tasto_verde);
                    if (sl.getReturnCode() == 0) {
                        if ((Double) mci_tasto_verde.getValue() == 0.0d)    //mentre aspetto onclick dal touch che imposterà 20..
                        {
                            mc_stati_riarmo = 0;
                        }
                    }
                    break;
                case 20:    //aspetto che premo il touch ( vedi case 20 della procedura Icona_tasto_verde())
                    mci_Vb7903_Reset_Ch1.setValue(1.0);     //ho premuto il touch, resetto CH1 e vado in 30
                    sl.WriteItem(mci_Vb7903_Reset_Ch1);

                    mc_stati_riarmo = 30;

                    break;
                //fine ZONA premere su touch screen

                //Inizio ZONA azzeramento assi
                case 30:    //avendo richiesto il reset del CH1, qui verifico se è avvenuto senza allarmi, se ci sono allarmi li leggo dal CN e vado a visualizzarli,
                    //se non ci sono allarmi aspetto l'azzeramento e poi lancio la MainActivity

                    sl.ReadItem(mci_tasto_verde);           //controllo se si spegne pulsante verde (emergenza hardware)
                    sl.ReadItem(mci_CH1_in_emergenza);      //controllo se il CH1 è in emergenza
                    if (sl.getReturnCode() == 0) {
                        if ((Double) mci_tasto_verde.getValue() == 0.0d || (Double) mci_CH1_in_emergenza.getValue() == 1.0d) {
                            //ho premuto il tasto verde oppure il CH1 è ancora in errore (per esempio durante azzeramento)
                            Leggi_Emergenze = true;
                            mc_stati_riarmo = 31;
                            break;
                        }
                        else {
                            //CH1 resettato e non ci sono errori

                            if((Double)Multicmd_i1_pulsanti_start.getValue() == 1.0d){
                                mc_stati_visualizzazione_allarmi = 101; //controllo se per fare l'azzeramento mancano sensori ed eventualmente lo scrivo a schermo
                            }else
                                mc_stati_visualizzazione_allarmi = 100; //scrivo a schermo di premere i pulsanti di start

                            sl.ReadItem(mc1_Vb50_macchina_azzerata);      //controllo se la macchina è azzerata
                            if (sl.getReturnCode() == 0) {
                                if ((Double) mc1_Vb50_macchina_azzerata.getValue() == 1.0d && (Double) Multicmd_ingresso_pressostato.getValue() == 0.0d) {
                                    StopThread = true;
                                    Intent intent_par = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent_par);
                                }
                            }
                        }
                    }

                    break;
                //Fine ZONA azzeramento assi

                // inizio ZONA ci sono stati allarmi dopo che ho tentato di resettare il CH1 oppure mentre stava facendo l'azzeramento
                case 31://arrivo qui se durante l'azzeramento succede un errore
                    if(!Leggi_Emergenze) {      //se ho finito di leggere le emergenze avanzo
                        if (list_allarmi.size() > 0) {
                            mc_stati_visualizzazione_allarmi = 50;
                            mc_stati_riarmo = 32;
                        }else
                            mc_stati_riarmo = 0;
                    }
                    break;

                case 32://aspetto pressione emergenza
                    sl.ReadItem(mci_tasto_verde);

                    if (sl.getReturnCode() == 0) {
                        if (!((Double) mci_tasto_verde.getValue() ==1.0d))    //se verde ancora premuto torno al 32 altrimenti riparto 0
                        {
                            mc_stati_riarmo = 0;
                        }
                    }
                    Leggi_Emergenze = true;             //aggiunto quando ho fatto 882
                    if (list_allarmi.size() > 0) {      //aggiunto quando ho fatto 882
                        mc_stati_visualizzazione_allarmi = 50;//aggiunto quando ho fatto 882

                    }
                    break;
                // fine ZONA ci sono stati allarmi dopo che ho tentato di resettare il CH1 oppure mentre stava facendo l'azzeramento
                default:
                    break;
            }
        }

        ////////////////////////

        private void ScriviEmergenza() {

            switch (mc_stati_visualizzazione_allarmi){

                case 0:
                    Allarm_textView.setText("");    //non faccio vedere la scritta rossa Alarm

                    break;
                case 10: //scrivo che bisogna premere il tasto verde
                    Allarm_textView.setText("");     //non faccio vedere la scritta rossa Alarm
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.PremiTastoVerde);
                    mc_stati_visualizzazione_allarmi = 0;
                    break;

                case 20: //scrivo che bisogna premere il touch screen
                    Allarm_textView.setText("");     //non faccio vedere la scritta rossa Alarm
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.ToccaTouch);
                    mc_stati_visualizzazione_allarmi = 0;

                    break;

                case 30:

                    break;

                case 50:    //mostro gli errori
                    if(list_allarmi.size()>0){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextSize(15);
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText("");
                        String testo = "";
                        List<String> listAllarmiDecodificati = new ArrayList<>();
                        listAllarmiDecodificati =  DecodificaCodiceErrore();
                        for (String s : listAllarmiDecodificati) {
                            testo = testo + s;
                        }
                        TextView_allarmi.setText(testo);
                        mc_stati_visualizzazione_allarmi = 0;
                    }
                    break;

                case 60:
                    if(list_allarmi.size()>0){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextSize(15);
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText("");
                        String testo = "";
                        List<String> listAllarmiDecodificati = new ArrayList<>();
                        listAllarmiDecodificati =  DecodificaCodiceErrore();
                        for (String s : listAllarmiDecodificati) {
                            testo = testo + s;
                        }
                        TextView_allarmi.setText(testo);
                        mc_stati_visualizzazione_allarmi = 1000;
                    }
                    break;

                case 70:
                    Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                    TextView_allarmi.setTextColor(Color.RED);
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setText(R.string.PremiEmergenza);
                    mc_stati_visualizzazione_allarmi = 0;

                    break;
                case 100:  //non ci sono errori, aspetto lo start per azzerare
                    Allarm_textView.setText("");     // faccio vedere la scritta rossa Allarm
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.PremiStartAzz);

                    mc_stati_visualizzazione_allarmi = 101;
                    break;
                case 101:


                    if((Double) Multicmd_ingresso_pressostato.getValue() == 1.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.MancaAria);
                    }

                    break;
                default:
                    break;

            }


        }

        //////////
        private List<String> DecodificaCodiceErrore() {

            List<String> ret = new ArrayList<>();
            List<String> listAllarmiDecodificati = new ArrayList<>();
            List<String> listAllarmiNonDecodificati = new ArrayList<>();

            String[] Descrizioni = new String[]{};
            Descrizioni = getResources().getStringArray(R.array.AllarmiCN_Descrizione_JAM);
            String testo = "";
            str_allarmi_more = "";
            for (String d : list_allarmi) {
                str_allarmi_more = str_allarmi_more + d +"\n"+"\n"; //compilo la stringa che mostrerò se premo + nel display
                testo = "";
                boolean findJAMCode = false;
                if (d.contains("30800001")) {
                    findJAMCode = true;
                } //0 //emergenza all'accensione, non scrivo niente
                if (d.contains("30800002")) {
                    findJAMCode = true;
                } //1
                if (d.contains("30800003")) {
                    d = Descrizioni[2];
                    testo = testo + d ;
                    findJAMCode = true;
                } //2
                if (d.contains("30800004")) {
                    d = Descrizioni[3];
                    testo = testo + d ;
                    findJAMCode = true;
                } //3
                if (d.contains("30800005")) {
                    d = Descrizioni[4];
                    testo = testo + d ;
                    findJAMCode = true;
                } //4
                if (d.contains("30800006")) {
                    d = Descrizioni[5];
                    testo = testo + d ;
                    findJAMCode = true;
                } //5
                if (d.contains("30800007")) {
                    d = Descrizioni[6];
                    testo = testo + d ;
                    findJAMCode = true;
                } //6
                if (d.contains("30003801")) {
                    d = Descrizioni[7];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //7
                if (d.contains("30004801")) {
                    d = Descrizioni[8];
                    testo = testo + d ;
                    findJAMCode = true;
                } //8
                if (d.contains("30005801")) {
                    d = Descrizioni[9];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //9
                if (d.contains("30008801")) {
                    d = Descrizioni[10];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //10
                if (d.contains("30009801")) {
                    d = Descrizioni[11];
                    testo = testo + d ;
                    findJAMCode = true;
                } //11
                if (d.contains("30010801")) {
                    d = Descrizioni[12];
                    testo = testo + d ;
                    findJAMCode = true;
                }//12
                if (d.contains("150001632")) {
                    d = Descrizioni[13];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //13
                if (d.contains("150002632")) {
                    d = Descrizioni[14];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //14
                if (d.contains("150003632")) {
                    d = Descrizioni[15];
                    testo = testo + d ;
                    findJAMCode = true;
                } //15
                if (d.contains("150004632")) {
                    d = Descrizioni[16];
                    testo = testo + d ;
                    findJAMCode = true;
                } //16
                if (d.contains("150005632")) {
                    d = Descrizioni[17];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //17
                if (d.contains("150006632")) {
                    d = Descrizioni[18];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //18
                if (d.contains("150007632")) {
                    d = Descrizioni[19];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //19
                if (d.contains("150008632")) {
                    d = Descrizioni[20];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //20
                if (d.contains("150009632")) {
                    d = Descrizioni[21];
                    testo = testo + d ;
                    findJAMCode = true;
                } //21
                if (d.contains("150010632")) {
                    d = Descrizioni[22];
                    testo = testo + d ;
                    findJAMCode = true;
                } //22
                if (d.contains("150001332") || d.contains("150001333") || d.contains("150001334")) {
                    d = Descrizioni[23];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //23
                if (d.contains("150002132") || d.contains("150002332") || d.contains("150002333") || d.contains("150002334")) {
                    d = Descrizioni[24];
                    testo = testo + d ;
                    findJAMCode = true;
                }   //24
                if (d.contains("150003132") || d.contains("150003332") || d.contains("150003333") || d.contains("150003334")) {
                    d = Descrizioni[25];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //25
                if (d.contains("150004132") || d.contains("150004332") || d.contains("150004333") || d.contains("150004334")) {
                    d = Descrizioni[26];
                    testo = testo + d ;
                    findJAMCode = true;
                } //26
                if (d.contains("350005132") || d.contains("350005332") || d.contains("350005333") || d.contains("350005334")) {
                    d = Descrizioni[27];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //27
                if (d.contains("250006332") || d.contains("250006333") || d.contains("250006334")) {
                    d = Descrizioni[28];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //28
                if (d.contains("250007332") || d.contains("250007333") || d.contains("250007334")) {
                    d = Descrizioni[29];
                    testo = testo + d ;
                    findJAMCode = true;
                }  //29
                if (d.contains("250008332") || d.contains("250008333") || d.contains("250008334")) {
                    d = Descrizioni[30];
                    testo = testo + d ;
                    findJAMCode = true;
                }   //30
                if (d.contains("250009332") || d.contains("250009333") || d.contains("250009334")) {
                    d = Descrizioni[31];
                    testo = testo + d ;
                    findJAMCode = true;
                } //31
                if (d.contains("250010332") || d.contains("250010333") || d.contains("250010334")) {
                    d = Descrizioni[32];
                    testo = testo + d ;
                    findJAMCode = true;
                } //31
                if (d.contains("30003850")) {
                    d = Descrizioni[33];
                    testo = testo + d ;
                    findJAMCode = true;
                } //33
                if (d.contains("30004850")) {
                    d = Descrizioni[34];
                    testo = testo + d ;
                    findJAMCode = true;
                } //34
                if (d.contains("30005850")) {
                    d = Descrizioni[35];
                    testo = testo + d ;
                    findJAMCode = true;
                } //35
                if (d.contains("30008850")) {
                    d = Descrizioni[36];
                    testo = testo + d ;
                    findJAMCode = true;
                } //36
                if (d.contains("30009850")) {
                    d = Descrizioni[37];
                    testo = testo + d ;
                    findJAMCode = true;
                } //37
                if (d.contains("30010850")) {
                    d = Descrizioni[38];
                    testo = testo + d ;
                    findJAMCode = true;
                } //38
                if (d.contains("62050101")) {
                    d = Descrizioni[39];
                    testo = testo + d ;
                    findJAMCode = true;
                } //39  Emergenza da nodo Ethercat Ago
                if (d.contains("62050102")) {
                    d = Descrizioni[40];
                    testo = testo + d ;
                    findJAMCode = true;
                } //40  Emergenza da nodo Ethercat Hook
                if (d.contains("62050103")) {
                    d = Descrizioni[41];
                    testo = testo + d ;
                    findJAMCode = true;
                } //41  Emergenza da nodo Ethercat CAricatore
                if (d.contains("62050104")) {
                    d = Descrizioni[42];
                    testo = testo + d + "\n";
                    findJAMCode = true;
                } //42  Emergenza da nodo Ethercat Ago
                if (d.contains("62050105")) {
                    d = Descrizioni[43];
                    testo = testo + d + "\n";
                    findJAMCode = true;
                } //43  Emergenza da nodo Ethercat Hook
                if (d.contains("62050106")) {
                    d = Descrizioni[44];
                    testo = testo + d + "\n";
                    findJAMCode = true;
                } //44  Emergenza da nodo Ethercat CAricatore


                if (d.contains("140045900") || d.contains("240045900") || d.contains("340045900")) {  //"Automatic cycle interrupted due to an emergency
                    d = getString(R.string.ErroreCicloAuto);
                    findJAMCode = false;
                }

                if(findJAMCode) {
                    boolean giàPresente = false;
                    for (String s:listAllarmiDecodificati) {
                        if(s.contains(testo))
                            giàPresente = true;
                    }
                    if(!giàPresente)
                        listAllarmiDecodificati.add(testo + "\n");
                }
                else {
                    boolean giàPresente = false;
                    for (String s:listAllarmiNonDecodificati) {
                        if(s.contains(testo))
                            giàPresente = true;
                    }
                    if(!giàPresente)
                        listAllarmiNonDecodificati.add(d + "\n");
                }
            }

            //aggiungo in coda ai codici decodificati quelli non decodificati
            for (String s:listAllarmiDecodificati) {
                ret.add(s);
            }
            for (String s:listAllarmiNonDecodificati) {
                ret.add(s);
            }
            return ret;

        }
        //////////
        private void  LeggoEmergenze() {
            // String str_allarmi_return ="";
            boolean findJAMCode = false;
            MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32, sl);
            sl.ReadItem(mci);
            int[] emebuf = (int[]) mci.getValue();

            Map<String, SmartAlarm> eme = new LinkedHashMap<String, SmartAlarm>();

            Integer idx2 = 0;

            try {
                for (Integer idx = 1; idx < emebuf.length; idx += 4) {
                    idx2++;

                    Integer[] p = {emebuf[idx], emebuf[idx + 1], emebuf[idx + 2], emebuf[idx + 3]};

                    SmartAlarm al = new SmartAlarm(1, SmartAlarm.satEmergM, p, true, "", "", null);

                    al.setIndex(idx2);

                    eme.put(al.getFingerPrint(), al);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            //Lettura delle descrizioni delle emergenze attive
            try {

                list_allarmi = new ArrayList<>();
                for (SmartAlarm al : eme.values()) {
                    MultiCmdItem descmci = new MultiCmdItem(1, MultiCmdItem.dtAL, al.getIndex(), MultiCmdItem.dpAL_M32_Description, sl);
                    sl.ReadItem(descmci);
                    String d = (String) descmci.getValue();
                    list_allarmi.add(d);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Leggi_Emergenze = false;
        }
    }
    //*************************************************************************************************
    // CheckPresenzaProgramma
    //*************************************************************************************************
    private boolean CheckPresenzaProgramma(String file_path) {

        MSysFileInfo fi = new MSysFileInfo();

        fi = sl.FileDir(file_path, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
        if (fi != null)
            return  true;
        return false;
    }

    //*************************************************************************************************
    // Icona_IP
    //*************************************************************************************************
    private void Icona_IP(ShoppingList sl) {
    //    TView_barra_bassa.setText(info);
        String IP = sl.getIP();
        if(sl.IsConnected()) {
            btn_connection_status.setBackgroundColor(Color.GREEN);
            btn_connection_status.setText(IP);
        }
        else
        {
            btn_connection_status.setBackgroundColor(Color.RED);
            btn_connection_status.setText("No Connect");
        }
        if((Double)MultiCmd_Vb7814_Eth_operational.getValue()==0.0d)
            Btn_eth_operational.setBackgroundColor(Color.RED);
        else
            Btn_eth_operational.setBackgroundColor(Color.GREEN);



    }
    /////////
    /**
     * Button for open alarm page
     *
     * @param view
     */
    public void onclick_alarm_more(View view) {
        KillThread();
        Intent PopUpAlarm = new Intent(getApplicationContext(), PopUpAlarm.class);
        PopUpAlarm.putExtra("stringAlarm", str_allarmi_more);
        startActivity(PopUpAlarm);


    }
    //*************************************************************************************************
    // on_click_
    //*************************************************************************************************
    public void onclick_buttonv_dito(View view) throws IOException
    {

        if(mc_stati_riarmo == 10 && sl.IsConnected())
        {
            Enable_visualizzazione_errori = true;
            mc_stati_riarmo = 20;
        }

        //sl.M32ConsoleCmd("reboot");

    }
    //*************************************************************************************************
    // onclick_debug
    //*************************************************************************************************
    public void onclick_debug(View view) throws IOException
    {
     //   savePreference();
        loadPreference();
        KeyDialog.Lancia_KeyDialogo(null,Emergency_page.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_parameter_ret",false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_debug, new IntentFilter("KeyDialog_parameter_ret"));
    }

    //*************************************************************************************************
    // on_click_machine_model
    //*************************************************************************************************
    public void on_click_machine_model(View view) throws IOException
    {

        KeyDialog.Lancia_KeyDialogo(null,Emergency_page.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_parameter_ret",false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_mac, new IntentFilter("KeyDialog_parameter_ret"));
    }
    //*************************************************************************************************
    // loadPreference
    //*************************************************************************************************
    private void loadPreference() {
        SharedPreferences sharePreference = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String val = sharePreference.getString(MACHINE_LOG_FILE,"default");
        TextView_allarmi.setText(val);
    }

    //*************************************************************************************************
    // savePreference
    //*************************************************************************************************
    private void savePreference() {
        SharedPreferences sharePreference = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePreference.edit();
        editor.putString(MACHINE_LOG_FILE, "prova");
        editor.apply();
    }

    //*************************************************************************************************
    // onclick_pagina_tools
    //*************************************************************************************************
    public void onclick_pagina_tools(View view) throws IOException
    {

        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Tool_page.class);
        intent_par.putExtra("chiamante", "Pagina_emergenza");
        startActivity(intent_par);

    }

    //*************************************************************************************************
    // On_click_Load
    //*************************************************************************************************
    public void On_click_Load(View view) throws IOException
    {

        Onclick_Load();


    }
    private void Onclick_Load() {
        KillThread();
        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent.putExtra("operazione", "Loading....");
        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF);
    }
    //*************************************************************************************************
    // BroadcastReceiver per entrare nella pagina popup
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver_mac = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            String linea1 = "",linea2 = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea1 = br.readLine();
                linea2 = br.readLine();
                br.close();
            }catch (Exception e)
            {}


            if(val.equals(linea1) )
            {
                KillThread();
                if(val.equals(linea1)) {
                    Intent settings = new Intent(getApplicationContext(), PopUpSettings.class);
                    startActivity(settings);
                }


            }

            else
            {
                Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
            }


        }
    };
    //*************************************************************************************************
    // ritorno da pagina x
    //*************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent databack) {
        super.onActivityResult(requestCode, resultCode, databack);
        String returnedResult = "";
        try {
            returnedResult = databack.getData().toString();
        } catch (Exception e) {
            returnedResult = "";
        }
        switch (requestCode) {

            case RESULT_PAGE_LOAD_UDF:
                if (returnedResult.equals("CARICATO")) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                    }


                    if (Values.File_XML_path != null) {
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path, getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();

                            Toast.makeText(this,
                                    "info_Jam_.txt file error",
                                    Toast.LENGTH_SHORT).show();

                        }

                        File file = new File(Values.File_XML_path);
                        int i = file.getName().lastIndexOf('.');
                        String name = file.getName().substring(0, i);



                        String path_file_udf = "c:\\cnc\\userdata\\"+name+".udf";

                        Mci_write_dtDB_prog_name.path_file = path_file_udf;     //invio il path al CN
                        Mci_write_dtDB_prog_name.write_flag = true;


                    }

                }
                break;

            default:
                break;

        }


    }
    //*************************************************************************************************
    // BroadcastReceiver per prendere pagina debug
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver_debug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            String linea1 = "",linea2 = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea1 = br.readLine();
                linea2 = br.readLine();
                br.close();
            }catch (Exception e)
            {}


            if( val.equals(linea2))
            {
                KillThread();

                if(val.equals(linea2)) {
                    Intent settings = new Intent(getApplicationContext(), Debug.class);
                    startActivity(settings);
                }


            }

            else
            {
                Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
            }


        }
    };

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private boolean copyAssets(String path, String outPath) {
        boolean ret = false;
        AssetManager assetManager = this.getAssets();
        String assets[];
        boolean ret_createdir = false;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path, outPath);
            } else {

                String lastcar = outPath.substring(outPath.length() - 1);
                String fullPath;
                if(lastcar.equals("/"))
                    fullPath = outPath +  path;
                else
                    fullPath = outPath + "/" + path;

                File dir = new File(fullPath);
                if (!dir.exists())
                    ret_createdir = dir.mkdir();
                if (!ret_createdir)
                    Toast.makeText(this, "No create external directory: " + dir, Toast.LENGTH_SHORT).show();

                for (String asset : assets) {
                    copyAssets(path + "/" + asset, outPath);
                }
            }
            ret = true;
        } catch (IOException ex) {
            Log.e(TAG, "I/O Exception", ex);
        }
        return ret;
    }
    private void copyFile(String filename, String outPath) {
        AssetManager assetManager = this.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String newFileName = outPath + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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

                // JamPointCode below is to handle presses of Volume up or Volume down.
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
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void DimezzaFileLog(File File_in, long filesize) throws IOException {
        File tmp = new File(Environment.getExternalStorageDirectory() + "/JamData/tmp.txt");


        BufferedReader br = new BufferedReader(new FileReader(File_in.getAbsolutePath()));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

        br.readLine();

        long lines = 0;
        while (br.readLine() != null) lines++;
        br.close();

        br = new BufferedReader(new FileReader(File_in.getAbsolutePath()));

        long metà_righe = lines/2;
        long righe_cnt = 0;

        for(long i =0; i<lines; i++)
        {
            righe_cnt++;
            String linea = br.readLine();
            if(righe_cnt > metà_righe)
                bw.write(String.format("%s%n", linea));


        }

        br.close();
        bw.close();

        File oldFile = new File(File_in.getAbsolutePath());
        if (oldFile.delete())
            tmp.renameTo(oldFile);
    }

    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_mac);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_debug);
        StopThread = true;


        try {
            if(Thread_Running) {
                thread_emerg.join();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Emergency Thread");

    }
}
