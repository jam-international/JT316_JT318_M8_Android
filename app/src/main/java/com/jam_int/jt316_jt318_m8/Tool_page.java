package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mjdev.libaums.fs.UsbFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Tool_page  extends Activity {
    final private static int PAGE_UDF = 200;
    final private static int PAGE_PARAM = 201;
    final private static int PAGE_MODIFICA_PROG = 202;
    final private static int PAGE_DELTA = 203;
    final private static int PAGE_Z_AGO = 204;
    final private static int PAGE_UPGRADE = 205;
    final private static int PAGE_PARAM_ADMIN = 206;
    final private static int RESULT_PAGE_LOAD_UDF = 102;
    final private static int RESULT_PAGE_PARAM = 105;
    final private static int RESULT_PAGE_UPGRADE = 106;
    final private static int RESULT_REPORT_TO_USB = 107;
    final private static int RESULT_PAGE_PARAM_ADMIN = 108;
    Intent databack = new Intent();
    String databack_text = "";  //serve per indicare alla Mainactivity quali file XML vanno ricaricati
    String chiamante="";
    UsbFile root;
    ShoppingList sl;
    boolean Thread_Running = false, StopThread = false,first_cycle = true;
    Thread thread_Tool;
    MultiCmdItem MultiCmd_Vn3804_pagina_touch,MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb4080_TestFcZeroC1,MultiCmd_Vb27_SbloccaCrochet;
    MultiCmdItem[] mci_array_read_all;
    MainActivity.Mci_write Mci_write_Vb4080_TestFcZeroC1,Mci_write_Vb27_SbloccaCrochet;
    Button Button_Delta,Button_Z_ago,Button_setting,Button_test_io,Button_upgrade_plc,Button_backup_cn_su_hmi,Button_disegna,Button_report_to_usb,Button_zero_sensori,Button_sgancio_ago,
            Button_pagina_par_admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        Button_Delta = (Button)findViewById(R.id.button_Delta);
        Button_Z_ago = (Button)findViewById(R.id.button_Z_ago);
        Button_setting = (Button)findViewById(R.id.button_setting);
        Button_test_io = (Button)findViewById(R.id.button_test_io);
        Button_upgrade_plc = (Button)findViewById(R.id.button_upgrade_plc);
        Button_backup_cn_su_hmi = (Button)findViewById(R.id.button_backup_cn_su_hmi);
        Button_disegna = (Button)findViewById(R.id.button_disegna);
        Button_report_to_usb  = (Button)findViewById(R.id.button_report_to_usb);
        Button_zero_sensori  = (Button)findViewById(R.id.button_zero_sensori);
        Button_sgancio_ago  = (Button)findViewById(R.id.btn_sgancio_ago);
        Button_pagina_par_admin  = (Button)findViewById(R.id.button_pagina_par_admin);
        Button_Delta.setVisibility(View.GONE);
        Button_Z_ago.setVisibility(View.GONE);
        Button_setting.setVisibility(View.GONE);
        Button_upgrade_plc.setVisibility(View.GONE);
        Button_test_io.setVisibility(View.GONE);
        Button_backup_cn_su_hmi.setVisibility(View.GONE);
        Button_report_to_usb.setVisibility(View.GONE);
        Button_zero_sensori.setVisibility(View.GONE);
        Button_sgancio_ago.setVisibility(View.GONE);
        Button_pagina_par_admin.setVisibility(View.GONE);
        Button_disegna.setVisibility(View.GONE);


        try {
            Bundle extras = getIntent().getExtras();
            chiamante = extras.getString("chiamante");
        }catch (Exception e){}

        if(chiamante.equals("Pagina_emergenza"))
            Button_disegna.setEnabled(false);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vb4080_TestFcZeroC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4080, MultiCmdItem.dpNONE);
        MultiCmd_Vb27_SbloccaCrochet = sl.Add("Io", 1, MultiCmdItem.dtVB, 27, MultiCmdItem.dpNONE);

        Mci_write_Vb4080_TestFcZeroC1 = new MainActivity.Mci_write(); Mci_write_Vb4080_TestFcZeroC1.mci = MultiCmd_Vb4080_TestFcZeroC1;
        Mci_write_Vb27_SbloccaCrochet = new MainActivity.Mci_write(); Mci_write_Vb27_SbloccaCrochet.mci = MultiCmd_Vb27_SbloccaCrochet;
        mci_array_read_all = new MultiCmdItem[]{MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb27_SbloccaCrochet};


        Toggle_Button.CreaToggleButton(Mci_write_Vb27_SbloccaCrochet, Button_sgancio_ago,null,null, getApplicationContext(), sl);

        if (!Thread_Running) {

            //  sl.Close();
            Tool_page.MyAndroidThread_Tool myTask_tool= new Tool_page.MyAndroidThread_Tool(this);
            thread_Tool = new Thread(myTask_tool, "Tool myTask");
            thread_Tool.start();
            Log.d("JAM TAG", "Start Toolpage Thread");
        }


    }
    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_password_ret"));

        if (!Thread_Running) {
            Thread_Running = false;
            StopThread = false;
            first_cycle = true;
            //  sl.Close();
            Tool_page.MyAndroidThread_Tool myTask_tool= new Tool_page.MyAndroidThread_Tool(this);
            thread_Tool = new Thread(myTask_tool, "Tool myTask");
            thread_Tool.start();
            Log.d("JAM TAG", "Start Toolpage Thread");
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
    class MyAndroidThread_Tool implements Runnable {
        Activity activity;

        public MyAndroidThread_Tool(Activity activity) {
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
                       // MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                       // sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }
                if (sl.IsConnected()) {
                    if(first_cycle) {

                        first_cycle = false;
                    }

                    MultiCmd_Vn3804_pagina_touch.setValue(1002.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);

                    sl.ReadItems(mci_array_read_all);


                    ScrivoVbVnVq(Mci_write_Vb4080_TestFcZeroC1);
                    GestiscoMci_Out_Toggle(Mci_write_Vb27_SbloccaCrochet);


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if( !chiamante.equals("Pagina_emergenza"))
                                Emergenza(activity);
                            GestioneVisualizzazioneToggleButton(Mci_write_Vb27_SbloccaCrochet, Button_sgancio_ago,"ic_sblocca_ago_press", "ic_sblocca_ago");

                        }
                    });




                } else
                    sl.Connect();
            }


        }
    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestiscoMci_Out_Toggle(MainActivity.Mci_write mci_write) {
        switch (mci_write.mc_stati) {
            case 0:
                if (mci_write.Fronte_positivo == true){

                    if((Double) mci_write.mci.getValue() == 0.0d ) {


                        mci_write.mci.setValue(1.0d);
                    }
                    else
                        mci_write.mci.setValue(0.0d);


                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 10;
                    mci_write.Fronte_positivo = false;


                }

                break;

            case 10:

                mci_write.mc_stati = 0;


                break;

            default:

                break;
        }
    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestioneVisualizzazioneToggleButton(MainActivity.Mci_write mci_write, Button button, String ic_press, String ic_unpress) {

        if (Double.compare( mci_write.valore_precedente,(Double) mci_write.mci.getValue()) != 0) {

            if((Double) mci_write.mci.getValue()== 1.0d) {

                int image_Premuto = this.getResources().getIdentifier(ic_press, "drawable", this.getPackageName());
                button.setBackground(this.getResources().getDrawable((image_Premuto)));

            }
            else
            {
                int image_Premuto = this.getResources().getIdentifier(ic_unpress, "drawable", this.getPackageName());
                button.setBackground(this.getResources().getDrawable((image_Premuto)));
            }

            mci_write.valore_precedente = (Double) mci_write.mci.getValue();
        }
    }
    //*********************************************************************************************
    //Emergenza
    //*********************************************************************************************
    private void Emergenza(Activity activity) {

        if((Double)MultiCmd_tasto_verde.getValue()==0.0d || (Double)MultiCmd_CH1_in_emergenza.getValue()==1.0d)
        {
            // StopThread = true;
            KillThread();
            Intent intent_emergenza = new Intent(activity,Emergency_page.class);

            startActivity(intent_emergenza);
        }
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


    //*************************************************************************************************
    //On_click_par_Admin
    //*************************************************************************************************
    public void On_click_par_Admin(View view) throws IOException
    {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_PARAM_ADMIN);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_PARAM_ADMIN);
    }

    //*************************************************************************************************
    //
    //*************************************************************************************************
    public void on_click_zero_sensori(View view) throws IOException
    {
        Mci_write_Vb4080_TestFcZeroC1.valore = 1.0d;
        Mci_write_Vb4080_TestFcZeroC1.write_flag = true;



    }
    //*************************************************************************************************
    // on_click_report_to_usb
    //*************************************************************************************************
    public void on_click_report_to_usb(View view) throws IOException
    {
        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Report_to_Usb_activity.class);
        startActivityForResult(intent_par, RESULT_REPORT_TO_USB);


    }
    //*************************************************************************************************
    // on_click_copia_file
    //*************************************************************************************************
    public void on_click_copia_file(View view) throws IOException
    {
        KillThread();
       // Log.d("JAM TAG","Toolpage OnclickCopyFilePage");

        Intent intent_par = new Intent(getApplicationContext(), Copy_Files_Activity.class);
        startActivity(intent_par);

    }
    //*************************************************************************************************
    // on_click_modifica_programma
    //*************************************************************************************************
    public void on_click_modifica_programma(View view) throws IOException
    {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Modifica_programma.class);

        startActivityForResult(intent_for_parametri,PAGE_MODIFICA_PROG);

    }

    //*************************************************************************************************
    // on_click_password
    //*************************************************************************************************
    public void on_click_password(View view) throws IOException
    {

        KeyDialog.Lancia_KeyDialogo(null, Tool_page.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_password_ret",false);


    }
    //*************************************************************************************************
    // on_click_setting
    //*************************************************************************************************
    public void on_click_setting(View view) throws IOException
    {



    }
    //*************************************************************************************************
    // onClick_test_io
    //*************************************************************************************************
    public void onClick_test_io(View view) throws IOException
    {

        KillThread();
        Intent intent_io = new Intent(getApplicationContext(), Page_Test_IO.class);
        startActivity(intent_io);


    }
    //*************************************************************************************************
    // on_click_backup_cn_su_hmi
    //*************************************************************************************************
    public void on_click_backup_cn_su_hmi(View view) throws IOException {

        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Upgrade_activity.class);
        intent_par.putExtra("chiamante_stringa","Backup_su_hmi");
        startActivityForResult(intent_par, RESULT_PAGE_UPGRADE);
    }

    //*************************************************************************************************
    // Button_update_click PLC
    //*************************************************************************************************
    public void Button_update_plc_click(View view) throws IOException {

        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Upgrade_activity.class);
        intent_par.putExtra("chiamante_stringa","Upgrade");
        startActivityForResult(intent_par, RESULT_PAGE_UPGRADE);
    }

    //*************************************************************************************************
    // On_click_par_Testa
    //*************************************************************************************************
    public void On_click_par_Testa(View view) throws IOException {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_PARAM);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_PARAM);
    }
    //*************************************************************************************************
    // on_click_Delta
    //*************************************************************************************************
    public void on_click_Delta(View view) throws IOException
    {
        KillThread();
        Intent intent_for_Delta = new Intent(getApplicationContext(), Delta_parametri.class);
        //startActivityForResult(intent_for_Delta,PAGE_DELTA);
        startActivity(intent_for_Delta);


    }

    //*************************************************************************************************
    // OnclickUsbPage
    //*************************************************************************************************
    public void OnclickUsbPage(View view) throws IOException
    {

        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Usb_Files_Activity.class);
        startActivity(intent_par);


    }
    //*************************************************************************************************
    // Onclick_Z_Ago
    //*************************************************************************************************
    public void Onclick_Z_Ago(View view) throws IOException
    {

        KillThread();
        Intent intent_Z_Ago = new Intent(getApplicationContext(), Z_Ago.class);
       // startActivityForResult(intent_Z_Ago,PAGE_Z_AGO);
        startActivity(intent_Z_Ago);


    }
    //*************************************************************************************************
    // On_click_par_udf_C1
    //*************************************************************************************************
    public void On_click_par_udf(View view) throws IOException
    {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        //  intent_for_parametri.putExtra("Chiamante", "udf_cucitura");


        intent_for_parametri.putExtra("Chiamante", PAGE_UDF);
        startActivityForResult(intent_for_parametri, PAGE_UDF);


    }
    //*************************************************************************************************
    // onClick_exit
    //*************************************************************************************************
    public void onClick_exit(View view) throws IOException {
        finish();
    }
    //*************************************************************************************************
    // ritorno da pagina x
    //*************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String Result_ricarica_XML ="SI";   //di default rileggo sempre udf tranne se da una specifica pagina mi arriva di non ricaricare
        try {
            Result_ricarica_XML = data.getData().toString();
        }catch (Exception e){
            Result_ricarica_XML ="SI";
        }


        if(Result_ricarica_XML.equals("SI") || Result_ricarica_XML.equals("CARICATO")) {

            Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);


            switch (requestCode) {

                case PAGE_MODIFICA_PROG:

                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = databack_text + "PAGE_MODIFICA_PROG";
                    //---set the data to pass back---
                    databack.setData(Uri.parse("CARICATO"));
                    setResult(RESULT_OK, databack);
                    //---close the activity---

                    break;
                case PAGE_UDF:

                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = "PAGE_UDF";
                    //---set the data to pass back---
                    databack.setData(Uri.parse(databack_text));
                    setResult(RESULT_OK, databack);
                    //---close the activity---


                    //invio file modificato al CN se ho almeno cambiato un dato

                    if (resultCode == RESULT_OK) {
                        //ho cambiato almeno un dato nella pagina parametri

                        intent.putExtra("operazione", "Saving....");
                        intent.putExtra("Chiamante", "T1_R");
                        intent.putExtra("File_path", Values.File_XML_path);
                        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF);

                    }


                    break;



                case PAGE_PARAM:


                    break;

                case PAGE_DELTA:
                    break;

                case PAGE_Z_AGO:

                    break;

                case RESULT_REPORT_TO_USB:
                    break;

                case RESULT_PAGE_PARAM_ADMIN:
                    break;
                default:

                    break;


            }
        }

    }
    //*************************************************************************************************
    // BroadcastReceiver per prendere la risposta dal KeyDialog
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
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

            if(val.equals(linea1))
            {
                Button_Delta.setVisibility(View.GONE);
                Button_disegna.setVisibility(View.VISIBLE);
                Button_Z_ago.setVisibility(View.VISIBLE);
                Button_setting.setVisibility(View.GONE);
                Button_upgrade_plc.setVisibility(View.VISIBLE);
                Button_test_io.setVisibility(View.VISIBLE);
                Button_report_to_usb.setVisibility(View.VISIBLE);
                Button_zero_sensori.setVisibility(View.GONE);
                Button_sgancio_ago.setVisibility(View.GONE);
                Button_pagina_par_admin.setVisibility(View.GONE);
                Button_backup_cn_su_hmi.setVisibility(View.GONE);

            }else{
                if(val.equals(linea2)){
                    Button_Delta.setVisibility(View.VISIBLE);
                    Button_zero_sensori.setVisibility(View.VISIBLE);
                    Button_sgancio_ago.setVisibility(View.VISIBLE);
                    Button_pagina_par_admin.setVisibility(View.VISIBLE);


                }else{

                    if(val.equals(""))
                    {}

                    else
                    {
                        Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
                    }




                }







            }



        }
    };
    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopThread = true;

        try {

            if (!Thread_Running) {
                thread_Tool.join();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Toolpage Thread");

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


}
