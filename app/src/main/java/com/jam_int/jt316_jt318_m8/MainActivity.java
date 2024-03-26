package com.jam_int.jt316_jt318_m8;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jamint.ricette.Element;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.VFKBook;

public class MainActivity extends Activity {

    ShoppingList sl;
    Thread thread_Main;
    boolean Thread_Running = false, StopThread = false,first_cycle = true;
    String Debug_mode="OFF";
    MultiCmdItem[] mci_array_read_all,mci_array_read_one_shot;
    Context context;
    MultiCmdItem MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb4507_stato_Automatico,MultiCmd_Vq1913_C1_UdfVelLavRPM,MultiCmd_Vq1914_C1_UdfPuntiVelIni,MultiCmd_Vq1915_C1_UdfVelIniRPM,
                MultiCmd_Vq1916_C1_UdfPuntiVelRall,MultiCmd_Vq1917_C1_UdfVelRallRPM,MultiCmd_Vq1918_C1_Udf_FeedG0,Multicmd_Vb1025_AppReloadParamC1,Multicmd_dtDB_prog_name,Multicmd_vb1019_Load_Prog,
                MultiCmd_Vb1006_StepPiu_singolo,MultiCmd_Vb1007_StepMeno_singolo, MultiCmd_Vb1022_StepPiu_multiplo, MultiCmd_Vb1023_StepMeno_multiplo,MultiCmd_Vb4802_Reset_Cuci,MultiCmd_Vq1951_punti_totali,
                MultiCmd_Vq1952_punti_parziali,Multicmd_Vq3596_ContPuntiSpola,Multicmd_Vq3597_ImpPuntiSpola,MultiCmd_Vn3804_pagina_touch,Multicmd_Vb4072_AllarmeContSpola,MultiCmd_posizione_X,MultiCmd_posizione_Y,
            MultiCmd_Vq1110_Speed,MultiCmd_Vb1034_Test_Cuci,MultiCmd_VQ1036_BufErrCode,MultiCmd_VQ1037_BufErrStepNum,MultiCmd_VQ1038_BufErrPar,MultiCmd_Vb1018_SbloccaAgo,MultiCmd_Vn2_allarmi_da_CN,
            MultiCmd_Debug14_prog_cn_in_esecuzione,MultiCmd_Debug8_riga_cn_in_esecuzione,Multicmd_Vq3591_CNT_CicliAutomaticoUser,Multicmd_vb4581_aggancio_automatico,Multicmd_vb4582_sgancio_pallet,
            Multicmd_vb4583_partenza_automatica,MultiCmd_Vb21_CmdApriSpola,MultiCmd_Vb22_test_pinzafilo,MultiCmd_Vb1020_piu_Nmulti_step,MultiCmd_Vb1021_meno_Nmulti_step,MultiCmd_Vq1011_numero_multi_step,
            MultiCmd_Vb52_goPC,MultiCmd_Vb1026_multiStep_salto,MultiCmd_Vb27_SbloccaCrochet,MultiCmd_Vn4_Warning,MultiCmd_Vq3591_CNT_produzione,MultiCmd_Vb30_Incucitura,
            MultiCmd_Vb32_Stop_cucitura,MultiCmd_Vb4806_AppPinzaAltaC1,MultiCmd_C1_CmdHoldRelease,MultiCmd_Vb7908_CH1_running;

    Mci_write Mci_write_Vb4507_stato_Automatico, Mci_write_Vq1913_C1_UdfVelLavRPM,Mci_write_Vq1914_C1_UdfPuntiVelIni,Mci_write_Vq1915_C1_UdfVelIniRPM,Mci_write_Vq1916_C1_UdfPuntiVelRall,Mci_write_Vq1917_C1_UdfVelRallRPM,
                Mci_write_Vq1918_C1_Udf_FeedG0,Mci_write_Vb1025_AppReloadParamC1,Mci_write_dtDB_prog_name,Mci_write_vb1019_Load_Prog,Mci_write_Vb1006_StepPiu_singolo,Mci_write_Vb1007_StepMeno_singolo,
                Mci_write_Vb1022_StepPiu_multiplo,Mci_write_Vb1023_StepMeno_multiplo,Mci_write_Vb4802_Reset_Cuci, Mci_write_Vq1951_punti_totali,Mci_write_Vq1952_punti_parziali,Mci_write_Vq3596_ContPuntiSpola,
                Mci_write_Vq3597_ImpPuntiSpola,Mci_write_Vb4072_AllarmeContSpola,Mci_write_Vq1110_Speed,Mci_write_Vb1034_Test_Cuci,Mci_VQ1036_BufErrCode,Mci_write_Vb1018_SbloccaAgo,Mci_Vn2_allarmi_da_CN,
                Mci_write_Vq3591_CNT_CicliAutomaticoUser,Mci_write_vb4581_aggancio_automatico,Mci_write_vb4582_sgancio_pallet,Mci_write_vb4583_partenza_automatica,Mci_write_Vb21_CmdApriSpola,Mci_write_Vb22_test_pinzafilo,
                Mci_write_Vb1020_piu_Nmulti_step,Mci_write_Vb1021_meno_Nmulti_step,Mci_write_Vq1011_numero_Nmulti_step,Mci_write_Vb52_goPC,Mci_write_Vb1026_multiStep_salto,Mci_write_Vb27_SbloccaCrochet;
    TextView TextView_val_speed,TextView_punti_tot,TextView_punti_parziale,TextView_Cnt_spola,TextSpola_limite,TextView_errore,TextView_programma_in_esecuzione,TextView_riga_in_esecuzione,
            TextView_cnt_thread,TextView_val_production,Text_punti_da_saltare,TextView_cnt_connection,TextView_max_speed_value;
    Button Button_pagina_tools,Button_step_piu,Button_step_meno,Button_reset,Button_reset_spola,Button_test_cuci,Button_Sgancio_ago,Button_aggancio_automatico,Button_sgancio_automatico,
            Button_partenza_automatica,Button_cambio_spola,Button_test_pinzafilo,Button_step_piu100,Button_step_meno100,Button_reset_allarm;
    SeekBar seekBar_speed;
    String Machine_model,File_XML_path;
    Integer[] Array_foto_allarmi;
    Ricetta ricetta;
    String[] tab_names = new String[]{};
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    FrameLayout frame_canvas;
    Dynamic_view myView;
    Utility utility;
    Double warning_old=0.0d;
    final private static int RESULT_PAGE_TOOLS = 100;
    final private static int RESULT_PAGE_LOAD_UDF = 102;
    MachineLog machinelog;
    int cnt_comunicazione = 0,seekbar_value=100;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context =this;
        utility = new Utility();
        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            Debug_mode = extras.getString("Debug_Mode");
        }

        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        VFKBook.Load(this);     //serve per usare variabili MultiCmdItem.dtDB
        sl.setVFK(VFKBook.getVFK());    //serve per usare variabili MultiCmdItem.dtDB

        frame_canvas = (FrameLayout)findViewById(R.id.FrameLayout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        Init_TextView();

        Init_mci();

        Init_Button();

        Init_XML();
        Init_Eventi();
        Foto_Allarmi();
        tab_names = getResources().getStringArray(R.array.allarmi);
        machinelog = new MachineLog();

        if (!Thread_Running) {

            //  sl.Close();
            MyAndroidThread_Main myTask_main= new MyAndroidThread_Main(this);
            thread_Main = new Thread(myTask_main, "Main myTask");
            thread_Main.start();
            Log.d("JAM TAG","Start MainActivity Thread");
        }
    }

    private void Foto_Allarmi() {
        Array_foto_allarmi = new Integer[]{
                0,
                R.drawable.foto_spola,
                R.drawable.foto_sfilatura,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                R.drawable.foto_lamiera_aperta,         //15
                R.drawable.foto_lamiera_aperta,         //16

        };
    }

    private void Init_Eventi() {
        CreaEventoEditText(Mci_write_Vq1110_Speed,TextView_val_speed,2500d,100d,false,false,false);


        CreaEventoStepPiuMeno();
        EdgeButton.CreaEdgeButton(Mci_write_Vb21_CmdApriSpola, Button_cambio_spola,"ic_cambio_spola_press","ic_cambio_spola",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb22_test_pinzafilo, Button_test_pinzafilo,"ic_test_pinzafilo_press","ic_test_pinzafilo",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb4802_Reset_Cuci, Button_reset,"ic_tasto_reset_premuto","ic_tasto_reset",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb4072_AllarmeContSpola, Button_reset_spola, "ic_spola_reset_red", "ic_spola_reset_yellow", getApplicationContext(), sl, 0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb1020_piu_Nmulti_step, Button_step_piu100, "ic_step_piu100_press", "ic_step_piu100", getApplicationContext(), sl, 0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb1021_meno_Nmulti_step, Button_step_meno100, "ic_step_meno100_press", "ic_step_meno100", getApplicationContext(), sl, 0);
        CreaEventoEditText(Mci_write_Vq3597_ImpPuntiSpola, TextSpola_limite, 50000d, 0d, false, false,false);
        Toggle_Button.CreaToggleButton(Mci_write_Vb1034_Test_Cuci, Button_test_cuci,null,null, getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb27_SbloccaCrochet, Button_Sgancio_ago,null,null, getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_vb4581_aggancio_automatico, Button_aggancio_automatico, "ic_aggancio_pallet_auto_press", "ic_aggancio_pallet_auto", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_vb4582_sgancio_pallet, Button_sgancio_automatico, "ic_sgancio_pallet_fine_press", "ic_sgancio_pallet_fine", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_vb4583_partenza_automatica, Button_partenza_automatica, "ic_partenza_automatica_press", "ic_partenza_automatica", getApplicationContext(), sl);
        CreaEventoEditText(Mci_write_Vq1011_numero_Nmulti_step, Text_punti_da_saltare, 5000d, 0d, false, false,true);

        TextView_val_speed.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double speed = 0.0d;
                double speedmax = 0.0d;

                try {
                    String speed_touch = String.valueOf(s);
                    speed = Float.parseFloat(speed_touch);
                    String speed_max = TextView_max_speed_value.getText().toString();
                    speedmax = Float.parseFloat(speed_max);

                    double delta = speed / speedmax;
                    seekBar_speed.setProgress((100 * (int) (delta * 100)) / 100);
                    Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;
                    Mci_write_Vb1025_AppReloadParamC1.write_flag = true;



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void Init_TextView() {
        TextView_val_speed = (TextView)findViewById(R.id.textView_val_speed);
        TextView_punti_tot = (TextView)findViewById(R.id.textView_punti_tot);
        TextView_punti_parziale = (TextView)findViewById(R.id.textView_punti_parziale);
        TextView_Cnt_spola = (TextView)findViewById(R.id.textView_Cnt_spola);
        TextSpola_limite = (TextView)findViewById(R.id.textSpola_limite);
        TextView_errore = (TextView)findViewById(R.id.textView_errore);
        TextView_programma_in_esecuzione = (TextView)findViewById(R.id.textView_programma_in_esecuzione);
        TextView_riga_in_esecuzione = (TextView)findViewById(R.id.textView_riga_in_esecuzione);
        TextView_cnt_thread = (TextView)findViewById(R.id.textView_cnt_thread);
        TextView_val_production = (TextView)findViewById(R.id.textView_val_production);
        TextView_cnt_connection  = (TextView)findViewById(R.id.textView_cnt_connection);
        Text_punti_da_saltare = (TextView)findViewById(R.id.text_punti_da_saltare);
        Text_punti_da_saltare.setVisibility(View.GONE);
        TextView_errore.setText("");
        TextView_max_speed_value = findViewById(R.id.textView_max_speed_value);

    }

    private void Init_XML() {
        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
            File_XML_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());
            if(File_XML_path!="" && File_XML_path!= null) {
                Load_XML(File_XML_path);
                Values.File_XML_path = File_XML_path;
                Carica_programma(Values.File_XML_path);

                TextView TextView_nomeprog = (TextView) findViewById(R.id.textView_nomeprog_val);
                File file = new File(File_XML_path);
                int i = file.getName().lastIndexOf('.');
                String name = file.getName().substring(0, i);
                TextView_nomeprog.setText(name);
                TextView TextView_folder_val = (TextView) findViewById(R.id.textView_folder_val);
                i = file.getPath().lastIndexOf('/');
                name = file.getPath().substring(0, i);
                TextView_folder_val.setText(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "info_Jam.txt is missing", Toast.LENGTH_SHORT).show();
        }

    }

    private void Load_XML(String percorso_file) {
        try {
            File file = new File(percorso_file);
            if (!file.exists() || file.length() <= 0) {
                CreaProgCucituraVuoto();
                percorso_file = Environment.getExternalStorageDirectory() + "/JamData/file_empty.xml";
            }
            file = new File(percorso_file);
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/ricette");
            dir.mkdirs();

            ricetta = new Ricetta();
            try
            {
                ricetta.open(file);
            }
            catch(Exception e){
                Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
            }
            if(ricetta.elements.size() !=0) {
                DrawTasca();
                Values.UdfPuntiVelIni = Math.round(Double.valueOf(ricetta.UdfPuntiVelIni)*1000.0)/1000.0;
                Values.UdfVelIniRPM = ricetta.UdfVelIniRPM;
                Values.UdfPuntiVelRall = Math.round(Double.valueOf(ricetta.UdfPuntiVelRall)*1000.0)/1000.0;
                Values.UdfVelRallRPM = ricetta.UdfVelRallRPM;
                Values.Udf_FeedG0 = ricetta.Udf_FeedG0;



                Mci_write_vb1019_Load_Prog.valore = 0.0d;               //comando di forzatura load profilo
                Mci_write_vb1019_Load_Prog.write_flag = true;

                Mci_write_Vb1025_AppReloadParamC1.valore = 0.0d;    //faccio rileggere i parametri da header udf
                Mci_write_Vb1025_AppReloadParamC1.write_flag = true;



            }else{

                Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception  e)
        {
            Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();

        }

    }
    //**************************************************************************************************
    private void DrawTasca() {
        try{
        ArrayList List_entità_T1 = (ArrayList<Element>) ricetta.elements;

        if(Values.Machine_model.equals("JT318M_1000x800")) {
            myView = new Dynamic_view(this, 733, 450, List_entità_T1, 0.52F, Coord_Pinza, false, 100, 40, null, true, getResources().getDimension(R.dimen.main_activity_framelayout_width), getResources().getDimension(R.dimen.main_activity_framelayout_height));
        }else {
            myView = new Dynamic_view(this, 733, 450, List_entità_T1, 0.6F, Coord_Pinza, false, 10, 10, null, true, getResources().getDimension(R.dimen.main_activity_framelayout_width), getResources().getDimension(R.dimen.main_activity_framelayout_height));
        }
        frame_canvas.addView(myView);
        myView.setBackgroundColor(Color.LTGRAY);
        myView.Disegna_entità(List_entità_T1);
        myView.Center_Bitmap();



        }catch (Exception  e)
            {
                Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();

            }

    }
    //**********************************************************************
    //
    //**********************************************************************
    private File CreaProgCucituraVuoto() {



        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/JamData");

        dir.mkdirs();
        File file = new File(dir,  "file_empty.xml");
        File file1 = new File(dir, "file_empty.usr");

        if(!file.exists() && !file1.exists()){
            Ricetta r = new Ricetta();
            r.setDrawPosition(new PointF(0.1f, 0f));
            r.drawFeedTo(new PointF(10f,10f));
            r.drawFeedTo(new PointF(0.1f, 0f));
            try {
                r.save(file);
                try {
                    r.exportToUsr(file1);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
            }
        }

        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void Init_Button() {
        Button_pagina_tools = (Button)findViewById(R.id.btn_pagina_tools);
        Button_step_piu = (Button)findViewById(R.id.button_step_piu);
        Button_step_meno = (Button)findViewById(R.id.button_step_meno);
        Button_reset = (Button)findViewById(R.id.button_reset);
        Button_reset_spola = (Button)findViewById(R.id.button_reset_spola);
        Button_test_cuci  = (Button)findViewById(R.id.btn_test_cuci);
        Button_Sgancio_ago = (Button) findViewById(R.id.btn_sgancio_ago);
        Button_aggancio_automatico = (Button) findViewById(R.id.button_aggancio_automatico);
        Button_sgancio_automatico = (Button) findViewById(R.id.button_sgancio_automatico);
        Button_partenza_automatica = (Button) findViewById(R.id.button_partenza_automatica);
        Button_cambio_spola = (Button) findViewById(R.id.button_cambio_spola);
        Button_test_pinzafilo = (Button) findViewById(R.id.button_test_pinzafilo);
        Button_step_piu100 = (Button)findViewById(R.id.button_step_piu100);
        Button_step_piu100.setVisibility(View.GONE);
        Button_step_meno100 = (Button)findViewById(R.id.button_step_meno100);
        Button_step_meno100.setVisibility(View.GONE);
        Button_reset_allarm = (Button)findViewById(R.id.button_reset_allarm);
        Button_reset_allarm.setVisibility(View.GONE);
        BarSeekSpeed();


    }

    private void BarSeekSpeed() {
        seekBar_speed = (SeekBar) findViewById(R.id.seekBar_speed);
        seekBar_speed.setMax(100);
        seekBar_speed.setProgress(100);
        seekBar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekbar_value = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Double vel = (Double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue();
                Double vel_modificata;
                vel_modificata = vel;
                if(seekbar_value <100) {
                    vel_modificata = vel  * (100 - seekbar_value) /100;
                    vel_modificata = vel - vel_modificata;
                    int IntValue = (int) Math. round(vel_modificata);
                    vel_modificata = Double.valueOf(IntValue);



                }
                vel_modificata = vel_modificata /1000;
                if(vel_modificata <300)vel_modificata = 300.0d;
                Mci_write_Vq1110_Speed.valore =  vel_modificata;
                Mci_write_Vq1110_Speed.write_flag = true;
                //daniele
                //daniele
            }
        });
    }

    private void Init_mci() {
        MultiCmd_Vq1110_Speed = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1110, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vb4507_stato_Automatico  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4507, MultiCmdItem.dpNONE);
        MultiCmd_Vq1913_C1_UdfVelLavRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1913, MultiCmdItem.dpNONE);
        MultiCmd_Vq1914_C1_UdfPuntiVelIni = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1914, MultiCmdItem.dpNONE);
        MultiCmd_Vq1915_C1_UdfVelIniRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1915, MultiCmdItem.dpNONE);
        MultiCmd_Vq1916_C1_UdfPuntiVelRall = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1916, MultiCmdItem.dpNONE);
        MultiCmd_Vq1917_C1_UdfVelRallRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1917, MultiCmdItem.dpNONE);
        MultiCmd_Vq1918_C1_Udf_FeedG0 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1918, MultiCmdItem.dpNONE);
        Multicmd_Vb1025_AppReloadParamC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 1025, MultiCmdItem.dpNONE);
        Multicmd_dtDB_prog_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug14_prog_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 14 , MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug8_riga_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 8 , MultiCmdItem.dpDB_MAIN1);
        Multicmd_vb1019_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 1019, MultiCmdItem.dpNONE);
        MultiCmd_Vb1006_StepPiu_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1006, MultiCmdItem.dpNONE);
        MultiCmd_Vb1007_StepMeno_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1007, MultiCmdItem.dpNONE);
        MultiCmd_Vb1022_StepPiu_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1022, MultiCmdItem.dpNONE);
        MultiCmd_Vb1023_StepMeno_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1023, MultiCmdItem.dpNONE);
        MultiCmd_Vb4802_Reset_Cuci = sl.Add("Io", 1, MultiCmdItem.dtVB, 4802, MultiCmdItem.dpNONE);
        MultiCmd_Vq1951_punti_totali = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1951, MultiCmdItem.dpNONE);
        MultiCmd_Vq1952_punti_parziali = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1952, MultiCmdItem.dpNONE);
        Multicmd_Vq3596_ContPuntiSpola = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3596, MultiCmdItem.dpNONE);
        Multicmd_Vq3597_ImpPuntiSpola = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3597, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_Vb4072_AllarmeContSpola = sl.Add("Io", 1, MultiCmdItem.dtVB, 4072, MultiCmdItem.dpNONE);
        MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
        MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
        MultiCmd_Vb1034_Test_Cuci = sl.Add("Io", 1, MultiCmdItem.dtVB, 1034, MultiCmdItem.dpNONE);
        MultiCmd_VQ1036_BufErrCode = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1036, MultiCmdItem.dpNONE);
        MultiCmd_VQ1037_BufErrStepNum = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1037, MultiCmdItem.dpNONE);
        MultiCmd_VQ1038_BufErrPar = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1038, MultiCmdItem.dpNONE);
        MultiCmd_Vb1018_SbloccaAgo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
        MultiCmd_Vn2_allarmi_da_CN = sl.Add("Io", 1, MultiCmdItem.dtVN, 2, MultiCmdItem.dpNONE);
        Multicmd_Vq3591_CNT_CicliAutomaticoUser = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3591, MultiCmdItem.dpNONE);
        Multicmd_vb4581_aggancio_automatico = sl.Add("Io", 1, MultiCmdItem.dtVB, 4581, MultiCmdItem.dpNONE);
        Multicmd_vb4582_sgancio_pallet = sl.Add("Io", 1, MultiCmdItem.dtVB, 4582, MultiCmdItem.dpNONE);
        Multicmd_vb4583_partenza_automatica = sl.Add("Io", 1, MultiCmdItem.dtVB, 4583, MultiCmdItem.dpNONE);
        MultiCmd_Vb21_CmdApriSpola = sl.Add("Io", 1, MultiCmdItem.dtVB, 21, MultiCmdItem.dpNONE);
        MultiCmd_Vb22_test_pinzafilo = sl.Add("Io", 1, MultiCmdItem.dtVB, 22, MultiCmdItem.dpNONE);
        MultiCmd_Vb1020_piu_Nmulti_step = sl.Add("Io", 1, MultiCmdItem.dtVB, 1020, MultiCmdItem.dpNONE);
        MultiCmd_Vb1021_meno_Nmulti_step = sl.Add("Io", 1, MultiCmdItem.dtVB, 1021, MultiCmdItem.dpNONE);
        MultiCmd_Vq1011_numero_multi_step = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1011, MultiCmdItem.dpNONE);
        MultiCmd_Vb52_goPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
        MultiCmd_Vb1026_multiStep_salto  = sl.Add("Io", 1, MultiCmdItem.dtVB, 1026, MultiCmdItem.dpNONE);
        MultiCmd_Vb27_SbloccaCrochet = sl.Add("Io", 1, MultiCmdItem.dtVB, 27, MultiCmdItem.dpNONE);
        MultiCmd_Vn4_Warning = sl.Add("Io", 1, MultiCmdItem.dtVN, 4, MultiCmdItem.dpNONE);
        MultiCmd_Vq3591_CNT_produzione = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3591, MultiCmdItem.dpNONE);
        MultiCmd_Vb30_Incucitura = sl.Add("Io", 1, MultiCmdItem.dtVB, 30, MultiCmdItem.dpNONE);
        MultiCmd_Vb32_Stop_cucitura = sl.Add("Io", 1, MultiCmdItem.dtVB, 32, MultiCmdItem.dpNONE);
        MultiCmd_Vb4806_AppPinzaAltaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4806, MultiCmdItem.dpNONE);
        MultiCmd_C1_CmdHoldRelease = sl.Add("Io", 1, MultiCmdItem.dtVB, 1009, MultiCmdItem.dpNONE);
        MultiCmd_Vb7908_CH1_running  = sl.Add("Io", 1, MultiCmdItem.dtVB, 7908, MultiCmdItem.dpNONE);


        Mci_write_Vq1110_Speed = new Mci_write(); Mci_write_Vq1110_Speed.mci = MultiCmd_Vq1110_Speed; Mci_write_Vq1110_Speed.tipoVariabile = Mci_write.TipoVariabile.VQ;


        Mci_write_Vb4507_stato_Automatico = new Mci_write(); Mci_write_Vb4507_stato_Automatico.mci = MultiCmd_Vb4507_stato_Automatico;
        Mci_write_Vq1913_C1_UdfVelLavRPM = new Mci_write(); Mci_write_Vq1913_C1_UdfVelLavRPM.mci = MultiCmd_Vq1913_C1_UdfVelLavRPM;Mci_write_Vq1913_C1_UdfVelLavRPM.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_Vq1914_C1_UdfPuntiVelIni = new Mci_write(); Mci_write_Vq1914_C1_UdfPuntiVelIni.mci = MultiCmd_Vq1914_C1_UdfPuntiVelIni;
        Mci_write_Vq1915_C1_UdfVelIniRPM = new Mci_write(); Mci_write_Vq1915_C1_UdfVelIniRPM.mci = MultiCmd_Vq1915_C1_UdfVelIniRPM;
        Mci_write_Vq1916_C1_UdfPuntiVelRall = new Mci_write(); Mci_write_Vq1916_C1_UdfPuntiVelRall.mci = MultiCmd_Vq1916_C1_UdfPuntiVelRall;
        Mci_write_Vq1917_C1_UdfVelRallRPM = new Mci_write(); Mci_write_Vq1917_C1_UdfVelRallRPM.mci = MultiCmd_Vq1917_C1_UdfVelRallRPM;
        Mci_write_Vq1918_C1_Udf_FeedG0 = new Mci_write(); Mci_write_Vq1918_C1_Udf_FeedG0.mci = MultiCmd_Vq1918_C1_Udf_FeedG0;
        Mci_write_Vb1025_AppReloadParamC1 = new Mci_write(); Mci_write_Vb1025_AppReloadParamC1.mci = Multicmd_Vb1025_AppReloadParamC1;
        Mci_write_dtDB_prog_name = new Mci_write(); Mci_write_dtDB_prog_name.mci = Multicmd_dtDB_prog_name;
        Mci_write_vb1019_Load_Prog = new Mci_write(); Mci_write_vb1019_Load_Prog.mci = Multicmd_vb1019_Load_Prog;
        Mci_write_Vb1006_StepPiu_singolo = new Mci_write(); Mci_write_Vb1006_StepPiu_singolo.mci = MultiCmd_Vb1006_StepPiu_singolo;
        Mci_write_Vb1007_StepMeno_singolo = new Mci_write(); Mci_write_Vb1007_StepMeno_singolo.mci = MultiCmd_Vb1007_StepMeno_singolo;
        Mci_write_Vb1022_StepPiu_multiplo = new Mci_write(); Mci_write_Vb1022_StepPiu_multiplo.mci = MultiCmd_Vb1022_StepPiu_multiplo;
        Mci_write_Vb1023_StepMeno_multiplo = new Mci_write(); Mci_write_Vb1023_StepMeno_multiplo.mci = MultiCmd_Vb1023_StepMeno_multiplo;
        Mci_write_Vb4802_Reset_Cuci = new Mci_write(); Mci_write_Vb4802_Reset_Cuci.mci = MultiCmd_Vb4802_Reset_Cuci;
        Mci_write_Vb21_CmdApriSpola = new Mci_write(); Mci_write_Vb21_CmdApriSpola.mci = MultiCmd_Vb21_CmdApriSpola;
        Mci_write_Vb22_test_pinzafilo = new Mci_write(); Mci_write_Vb22_test_pinzafilo.mci = MultiCmd_Vb22_test_pinzafilo;
        Mci_write_Vq1951_punti_totali = new Mci_write(); Mci_write_Vq1951_punti_totali.mci = MultiCmd_Vq1951_punti_totali;
        Mci_write_Vq1952_punti_parziali = new Mci_write(); Mci_write_Vq1952_punti_parziali.mci = MultiCmd_Vq1952_punti_parziali;
        Mci_write_Vq3596_ContPuntiSpola = new Mci_write(); Mci_write_Vq3596_ContPuntiSpola.mci = Multicmd_Vq3596_ContPuntiSpola;Mci_write_Vq3596_ContPuntiSpola.valore_precedente=0.0d;
        Mci_write_Vq3597_ImpPuntiSpola = new Mci_write(); Mci_write_Vq3597_ImpPuntiSpola.mci = Multicmd_Vq3597_ImpPuntiSpola;Mci_write_Vq3597_ImpPuntiSpola.valore_precedente=0.0d;
        Mci_write_Vb4072_AllarmeContSpola = new Mci_write();Mci_write_Vb4072_AllarmeContSpola.mci = Multicmd_Vb4072_AllarmeContSpola;Mci_write_Vb4072_AllarmeContSpola.write_flag = false;Mci_write_Vb4072_AllarmeContSpola.valore_precedente = 0.0d;Mci_write_Vb4072_AllarmeContSpola.valore = 0.0d;
        Mci_write_Vb1034_Test_Cuci = new Mci_write(); Mci_write_Vb1034_Test_Cuci.mci = MultiCmd_Vb1034_Test_Cuci;
        Mci_VQ1036_BufErrCode = new Mci_write(); Mci_VQ1036_BufErrCode.mci = MultiCmd_VQ1036_BufErrCode;Mci_VQ1036_BufErrCode.Fronte_positivo =false;
        Mci_write_Vb1018_SbloccaAgo = new Mci_write(); Mci_write_Vb1018_SbloccaAgo.mci = MultiCmd_Vb1018_SbloccaAgo;
        Mci_Vn2_allarmi_da_CN  = new Mci_write(); Mci_Vn2_allarmi_da_CN.mci = MultiCmd_Vn2_allarmi_da_CN;Mci_Vn2_allarmi_da_CN.valore_precedente = -1.0d;Mci_Vn2_allarmi_da_CN.valore = 0.0d;
        Mci_write_Vq3591_CNT_CicliAutomaticoUser = new Mci_write();Mci_write_Vq3591_CNT_CicliAutomaticoUser.mci = Multicmd_Vq3591_CNT_CicliAutomaticoUser;Mci_write_Vq3591_CNT_CicliAutomaticoUser.valore_precedente = 0.0d;
        Mci_write_vb4581_aggancio_automatico = new Mci_write(); Mci_write_vb4581_aggancio_automatico.mci = Multicmd_vb4581_aggancio_automatico;
        Mci_write_vb4582_sgancio_pallet = new Mci_write(); Mci_write_vb4582_sgancio_pallet.mci = Multicmd_vb4582_sgancio_pallet;
        Mci_write_vb4583_partenza_automatica = new Mci_write(); Mci_write_vb4583_partenza_automatica.mci = Multicmd_vb4583_partenza_automatica;
        Mci_write_Vb1020_piu_Nmulti_step = new Mci_write(); Mci_write_Vb1020_piu_Nmulti_step.mci = MultiCmd_Vb1020_piu_Nmulti_step;
        Mci_write_Vb1021_meno_Nmulti_step = new Mci_write(); Mci_write_Vb1021_meno_Nmulti_step.mci = MultiCmd_Vb1021_meno_Nmulti_step;
        Mci_write_Vq1011_numero_Nmulti_step = new Mci_write(); Mci_write_Vq1011_numero_Nmulti_step.mci = MultiCmd_Vq1011_numero_multi_step;
        Mci_write_Vb52_goPC = new Mci_write(); Mci_write_Vb52_goPC.mci = MultiCmd_Vb52_goPC;
        Mci_write_Vb1026_multiStep_salto = new Mci_write(); Mci_write_Vb1026_multiStep_salto.mci = MultiCmd_Vb1026_multiStep_salto;
        Mci_write_Vb27_SbloccaCrochet = new Mci_write(); Mci_write_Vb27_SbloccaCrochet.mci = MultiCmd_Vb27_SbloccaCrochet;

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_Vq1110_Speed,MultiCmd_CH1_in_emergenza,MultiCmd_Vb4507_stato_Automatico,MultiCmd_tasto_verde,MultiCmd_Vq1913_C1_UdfVelLavRPM,MultiCmd_Vq1951_punti_totali,
                MultiCmd_Vq1952_punti_parziali,Multicmd_Vq3596_ContPuntiSpola,Multicmd_Vb4072_AllarmeContSpola,MultiCmd_posizione_X,MultiCmd_posizione_Y,MultiCmd_VQ1036_BufErrCode,
                MultiCmd_VQ1037_BufErrStepNum,MultiCmd_VQ1038_BufErrPar,MultiCmd_Vn2_allarmi_da_CN,MultiCmd_Debug14_prog_cn_in_esecuzione,MultiCmd_Debug8_riga_cn_in_esecuzione,
                Multicmd_Vq3591_CNT_CicliAutomaticoUser,Multicmd_vb4581_aggancio_automatico, Multicmd_vb4582_sgancio_pallet, Multicmd_vb4583_partenza_automatica,MultiCmd_Vb1034_Test_Cuci,
                MultiCmd_Vb27_SbloccaCrochet,MultiCmd_Vn4_Warning,MultiCmd_Vq3591_CNT_produzione,MultiCmd_Vb30_Incucitura,MultiCmd_Vb32_Stop_cucitura,MultiCmd_Vb4806_AppPinzaAltaC1,
                MultiCmd_C1_CmdHoldRelease,MultiCmd_Vb7908_CH1_running
        };
        mci_array_read_one_shot = new MultiCmdItem[]{



        };

    }

    @Override
    public void onResume() {
        super.onResume();

        first_cycle = true;
        if (!Thread_Running) {
            StopThread = false;
            Log.d("JAM TAG","Start MainActivity Thread");
            MyAndroidThread_Main myTask_main= new MyAndroidThread_Main(this);
            thread_Main = new Thread(myTask_main, "Main myTask");
            thread_Main.start();
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


    } @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();

    }
    //**********************************************************************************************
    // MyAndroidThread_Emg
    //**********************************************************************************************
    class MyAndroidThread_Main implements Runnable {
        Activity activity;
        boolean rc_error;
        public MyAndroidThread_Main(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;


                try {
                    Thread.sleep((long) 200d);
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
                    cnt_comunicazione++;
                    if(first_cycle){
                        first_cycle = false;

                        sl.ReadItem(MultiCmd_Vq1110_Speed);
                        sl.ReadItem(MultiCmd_Vq1913_C1_UdfVelLavRPM);
                        double speed = (double) MultiCmd_Vq1110_Speed.getValue();
                        double speedmax = (double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue();
                        if (speedmax < 100000)
                            speedmax = speedmax * 1000;
                        double delta = speed / speedmax;
                        seekBar_speed.setProgress((100 * (int) (delta * 100)) / 100);

                        AggiornoMciValoriAccensione();

                    }
                    rc_error = false;
                    sl.Clear();

                    MultiCmd_Vn3804_pagina_touch.setValue(1001.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);



                    // ------------------------ RX -------------------------------
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }
                    else
                    {


                        ScrivoVbVnVq(Mci_write_Vq1110_Speed);


                        if((Double)MultiCmd_Vq1110_Speed.getValue()<50000){     //50000 = 50rpm
                                Mci_write_Vq1110_Speed.valore = 300d;
                                Mci_write_Vq1110_Speed.write_flag = true;

                        }

                        ScrivoVbVnVq(Mci_write_Vq1913_C1_UdfVelLavRPM);
                        ScrivoVbVnVq(Mci_write_Vq1914_C1_UdfPuntiVelIni);
                        ScrivoVbVnVq(Mci_write_Vq1915_C1_UdfVelIniRPM);
                        ScrivoVbVnVq(Mci_write_Vq1916_C1_UdfPuntiVelRall);
                        ScrivoVbVnVq(Mci_write_Vq1917_C1_UdfVelRallRPM);
                        ScrivoVbVnVq(Mci_write_Vq1918_C1_Udf_FeedG0);
                        ScrivoVbVnVq(Mci_write_Vb1025_AppReloadParamC1);
                        ScrivoVbVnVq(Mci_write_Vb52_goPC);
                        ScrivoVbVnVq(Mci_write_Vb1026_multiStep_salto);
                        ScrivoStringaDB(Mci_write_dtDB_prog_name);
                        ScrivoVbVnVq(Mci_write_vb1019_Load_Prog);
                        GestiscoMci_Edge_Out(Mci_write_Vb1006_StepPiu_singolo);
                        GestiscoMci_Edge_Out(Mci_write_Vb1007_StepMeno_singolo);
                        GestiscoMci_Edge_Out(Mci_write_Vb21_CmdApriSpola);
                        GestiscoMci_Edge_Out(Mci_write_Vb22_test_pinzafilo);
                        ScrivoVbVnVq(Mci_write_Vb1022_StepPiu_multiplo);
                        ScrivoVbVnVq(Mci_write_Vb1023_StepMeno_multiplo);
                        GestiscoMci_Edge_Out(Mci_write_Vb4802_Reset_Cuci);
                        ScrivoVbVnVq(Mci_write_Vb4802_Reset_Cuci);
                        ScrivoVbVnVq(Mci_write_Vq3597_ImpPuntiSpola);
                        ScrivoVbVnVq(Mci_write_Vq1011_numero_Nmulti_step);
                        GestiscoMci_Edge_Out(Mci_write_Vb4072_AllarmeContSpola);
                        GestiscoMci_Out_Toggle(Mci_write_Vb1034_Test_Cuci);
                        GestiscoMci_Out_Toggle(Mci_write_Vb27_SbloccaCrochet);
                        ScrivoVbVnVq(Mci_Vn2_allarmi_da_CN);
                        GestiscoMci_Out_Toggle(Mci_write_vb4581_aggancio_automatico);
                        GestiscoMci_Out_Toggle(Mci_write_vb4582_sgancio_pallet);
                        GestiscoMci_Out_Toggle(Mci_write_vb4583_partenza_automatica);
                        GestiscoMci_Edge_Out(Mci_write_Vb1020_piu_Nmulti_step);
                        GestiscoMci_Edge_Out(Mci_write_Vb1021_meno_Nmulti_step);





                        double X = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

                        Coord_Pinza.CoordPosPinza(X, Y, ricetta);

                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            int max_speed = (int) ((double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue() / 1000);
                            TextView_max_speed_value.setText(""+max_speed); //scrivo la massima velelocità associata al programma


                            if(cnt_comunicazione>1000)cnt_comunicazione =0;
                            TextView_cnt_connection.setText("Cnt: "+cnt_comunicazione);

                            if (!rc_error) {
                                Emergenza();

                                Visualizza_contatore(TextView_val_speed, Mci_write_Vq1110_Speed, false, true, false);     //velocità

                                Visualizza_contatore(TextView_punti_tot, Mci_write_Vq1951_punti_totali, true, false, false);     //punti totale programma
                                Visualizza_contatore(TextView_punti_parziale, Mci_write_Vq1952_punti_parziali, true, false, false);     //punti parziali programma
                                Visualizza_contatore(TextView_Cnt_spola, Mci_write_Vq3596_ContPuntiSpola, true, false, false);     //conta spola
                                Visualizza_contatore(TextSpola_limite, Mci_write_Vq3597_ImpPuntiSpola, true, false, false);     //conta spola limite
                                GestioneVisualizzazioneToggleButton(Mci_write_Vb4072_AllarmeContSpola, Button_reset_spola, "ic_spola_reset_red", "ic_spola_reset_yellow");
                                GestioneVisualizzazioneToggleButton(Mci_write_Vb1034_Test_Cuci, Button_test_cuci, "ic_test_cucitura_press", "ic_test_cucitura");
                                GestioneVisualizzazioneToggleButton(Mci_write_Vb27_SbloccaCrochet, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");
                                Visualizza_contatore(TextView_val_production, Mci_write_Vq3591_CNT_CicliAutomaticoUser, true, false, false);     //produzione
                                GestioneVisualizzazioneToggleButton(Mci_write_vb4581_aggancio_automatico, Button_aggancio_automatico, "ic_aggancio_pallet_auto_press", "ic_aggancio_pallet_auto");
                                GestioneVisualizzazioneToggleButton(Mci_write_vb4582_sgancio_pallet, Button_sgancio_automatico, "ic_sgancio_pallet_fine_press", "ic_sgancio_pallet_fine");
                                GestioneVisualizzazioneToggleButton(Mci_write_vb4583_partenza_automatica, Button_partenza_automatica, "ic_partenza_automatica_press", "ic_partenza_automatica");
                                if ((Double) MultiCmd_Vb1034_Test_Cuci.getValue() == 0.0d)
                                    Button_test_cuci.setBackground(context.getResources().getDrawable(R.drawable.ic_test_cucitura));
                                Visualizza_contatore(Text_punti_da_saltare, Mci_write_Vq1011_numero_Nmulti_step, true, true, true);     //


                                Riga_CN_Esecuzione();
                                machinelog.MachineLog_write(MultiCmd_Vb30_Incucitura, MultiCmd_Vq3591_CNT_produzione, MultiCmd_Vb32_Stop_cucitura, Multicmd_dtDB_prog_name);
                                ControlloErrori();
                                GestiscoWarning();
                                int thread_cnt = utility.ContaThread();

                                TextView_cnt_thread.setText(getString(R.string.cnt_thread) + thread_cnt);
                                if ((Double) MultiCmd_Vb4806_AppPinzaAltaC1.getValue() == 0.0d) {
                                    if((Double)MultiCmd_Vb7908_CH1_running.getValue() == 0.0d)
                                        Button_pagina_tools.setVisibility(View.VISIBLE);
                                    else
                                        Button_pagina_tools.setVisibility(View.GONE);
                                } else
                                    Button_pagina_tools.setVisibility(View.GONE);
                                if (Coord_Pinza.XCoord_precedente != Coord_Pinza.XCoordPosPinza || Coord_Pinza.YCoord_precedente != Coord_Pinza.YCoordPosPinza) {
                                    if (myView != null && (Double)MultiCmd_Vn2_allarmi_da_CN.getValue() == 0.0d)
                                        myView.AggiornaCanvas(true);
                                    Coord_Pinza.XCoord_precedente = Coord_Pinza.XCoordPosPinza;
                                    Coord_Pinza.YCoord_precedente = Coord_Pinza.YCoordPosPinza;

                                }
                            }

                        }




                    });


                } else
                    sl.Connect();
            }


        }
        //*********************************************************************************************
        //Riga_CN_Esecuzione
        //*********************************************************************************************
        private void GestiscoWarning() {
            double Warning = (Double)MultiCmd_Vn4_Warning.getValue();
            try {
                if (Warning > 0 && Warning != warning_old) {
                    warning_old = Warning;

                    tab_names = getResources().getStringArray(R.array.warning_vn4);
                    String warning_string = tab_names[(int) Warning];

                    AlertDialog.Builder warningDialog = new AlertDialog.Builder(context);
                    // Setting Dialog Title
                    warningDialog.setTitle("Warning");

                    warningDialog.setMessage(warning_string)
                            .setCancelable(false)
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alert = warningDialog.create();
                    alert.show();


                    Warning = 0;        //cancello chiamata
                }

            }catch (Exception e){}

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
        //*********************************************************************************************
        //ControlloErrori
        //*********************************************************************************************
        private void ControlloErrori() {

            int i = 0;
            Context context = getApplicationContext();
            try {
                Double err = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                i = err.intValue();

                switch (i){
                    case 0:
                        //se ho ancora la spia rossa spola non tolgo l'immagine spola
                        if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {
                            if ((Double) Multicmd_Vb4072_AllarmeContSpola.getValue() == 0.0d) {
                                Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                if (Double.compare(val_prec, val_CN) != 0) {
                                    Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                    DrawTasca();
                                    TextView_errore.setText("");
                                }
                            }
                        }
                        Button_reset_allarm.setVisibility(View.GONE);
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 15:
                    case 16:

                        if (Double.compare(Mci_Vn2_allarmi_da_CN.valore_precedente, (Double) Mci_Vn2_allarmi_da_CN.mci.getValue()) != 0) {
                            Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                            Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                            String[] tab_names = new String[]{};
                            String Stringa_allarme = "";

                            tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                            Stringa_allarme = tab_names[i];


                            TextView_errore.setText(Stringa_allarme);
                            try {

                                ImageView imageView = new ImageView(context);
                                imageView.setBackground(context.getResources().getDrawable(Array_foto_allarmi[i]));
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.MATCH_PARENT));

                                frame_canvas.addView(imageView);
                            } catch (Exception e) {
                            }
                            Button_reset_allarm.setVisibility(View.VISIBLE);
                        }

                        break;

                    case 14:
                        Mci_Vn2_allarmi_da_CN.valore = 0.0d;
                        Mci_Vn2_allarmi_da_CN.write_flag = true;
                        String[] tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                        String Stringa_allarme = tab_names[i];
                        Toast.makeText(context, Stringa_allarme , Toast.LENGTH_SHORT).show();
                        break;






                }
/*
                if (i > 0) {
                    if (Double.compare(Mci_Vn2_allarmi_da_CN.valore_precedente, (Double) Mci_Vn2_allarmi_da_CN.mci.getValue()) != 0) {
                        Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                        Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                        String[] tab_names = new String[]{};
                        String Stringa_allarme = "";

                            tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                            Stringa_allarme = tab_names[i];


                        TextView_errore.setText(Stringa_allarme);
                        try {

                            ImageView imageView = new ImageView(context);
                            imageView.setBackground(context.getResources().getDrawable(Array_foto_allarmi[i]));
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT));

                            frame_canvas.addView(imageView);
                        } catch (Exception e) {
                        }
                    }
                } else {
                    //  if(Fronte_allarme){
                    if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {
                        if ((Double) Multicmd_Vb4072_AllarmeContSpola.getValue() == 0.0d) {    //se ho ancora la spia rossa spola non tolgo l'immagine spola
                            Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                            Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                            Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                            if (Double.compare(val_prec, val_CN) != 0) {
                                Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                DrawTasca();
                                TextView_errore.setText("");
                            }
                        }
                    }

                }
*/
            } catch (Exception e) {

                Toast.makeText(context, "Allarmi_Vn2 catch numero: " + i, Toast.LENGTH_SHORT).show();
            }

            //controllo sovrapposizione codici rotazione
            if((Double)MultiCmd_VQ1036_BufErrCode.getValue() != 0.0d && !Mci_VQ1036_BufErrCode.Fronte_positivo)
            {
                if((Double)MultiCmd_VQ1036_BufErrCode.getValue() == 8.0d) {
                    Mci_VQ1036_BufErrCode.Fronte_positivo = true;   //per non rientrare
                    if(Mci_VQ1036_BufErrCode.Fronte_positivo){

                            String testo_errore = tab_names[1];
                            TextView_errore.setText("Error: " + testo_errore + " n.point: " + (Double) MultiCmd_VQ1037_BufErrStepNum.getValue());
                    }
                }

            }
            else{
                if(Mci_VQ1036_BufErrCode.Fronte_positivo && (Double)MultiCmd_VQ1036_BufErrCode.getValue() != 8.0d){
                    TextView_errore.setText("");
                    Mci_VQ1036_BufErrCode.Fronte_positivo = false;

                }

            }




        }

        //*********************************************************************************************
        //AggiornoMciValoriAccensione
        //*********************************************************************************************
        private void AggiornoMciValoriAccensione() {

            MultiCmd_Vn3804_pagina_touch.setValue(1001.0d);
            sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
            sl.ReadItem(Multicmd_Vq3597_ImpPuntiSpola);
            sl.ReadItem(MultiCmd_Vq1011_numero_multi_step);
            Mci_write_Vb1026_multiStep_salto.valore = 1.0d;
            Mci_write_Vb1026_multiStep_salto.write_flag = true;


        }

        //**************************************************************************************************
        //
        //**************************************************************************************************
        private void Visualizza_contatore(TextView textView, Mci_write mci_write, boolean numero_intero, boolean variabile_VQ, boolean Div1000) {
            try{

                //  String valore_attuale_str = textView.getText().toString();
                // Double valore_attuale = Double.valueOf(valore_attuale_str);
                if(Double.compare(mci_write.valore_precedente,(Double)mci_write.mci.getValue()) != 0)
                {

                    if(Div1000){
                        Double val = (Double)mci_write.mci.getValue();
                        val = val /1000;
                        textView.setText(SubString.SubstringExtensions.Before(val.toString(), "."));
                    }else {

                        if (numero_intero)
                            textView.setText(SubString.SubstringExtensions.Before(mci_write.mci.getValue().toString(), "."));

                        else if (variabile_VQ)
                            textView.setText(SubString.SubstringExtensions.Before(mci_write.mci.getValue().toString(), "000."));
                        else
                            textView.setText(mci_write.mci.getValue().toString());
                    }

                    mci_write.valore_precedente = (Double) mci_write.mci.getValue();
                    mci_write.valore =   mci_write.valore_precedente;

                }

            }catch (Exception e)
            {}

        }
        //*********************************************************************************************
        //EScrivoStringaDB
        //*********************************************************************************************
        private void Visualizza_e_gestisco_velocità(TextView textView, Mci_write mci_write) {

            try {

                if (Double.compare(mci_write.valore_precedente, (Double) mci_write.mci.getValue()) != 0) {


                    textView.setText(SubString.SubstringExtensions.Before(mci_write.mci.getValue().toString(), "000."));



                    mci_write.valore_precedente = (Double) mci_write.mci.getValue();
                    mci_write.valore = mci_write.valore_precedente;

                    //se non sto cucendo alzo la vb1025 che farà rileggere i parametri udf per far ricalcolare i punti lenti prima del rasafilo con riferimento alla velocità che ho inserito
                    if((Double)MultiCmd_Vb7908_CH1_running.getValue() == 0.0d){
                        Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;    //serve rileggere i parametri da header udf
                        Mci_write_Vb1025_AppReloadParamC1.write_flag = true;
                    }

                }

            } catch (Exception e) {
            }
        }
        //*********************************************************************************************
        //EScrivoStringaDB
        //*********************************************************************************************
        private void ScrivoStringaDB(Mci_write mci_write) {
            if(mci_write.write_flag) {
                mci_write.mci.setValue(mci_write.path_file);
                sl.WriteItem(mci_write.mci);
                mci_write.write_flag = false;
            }

        }
        //**************************************************************************************************
        //ScrivoVbVnVq
        //**************************************************************************************************
        private void ScrivoVbVnVq(Mci_write variabile) {
            if( variabile.write_flag == true){
                variabile.write_flag = false;
                if(variabile.tipoVariabile == Mci_write.TipoVariabile.VQ)
                    variabile.mci.setValue(variabile.valore*1000);
                else
                    variabile.mci.setValue(variabile.valore);
                sl.WriteItem(variabile.mci);

            }
        }
        //*********************************************************************************************
        //Ciclo_button
        //*********************************************************************************************
        private void ShowStatusButton(Info_Button_cicli button_ciclo, Button button_stato) {

            if((Double)button_ciclo.multicmd_stato_green_red.getValue()==1.0d)
            {
                int image_Premuto = context.getResources().getIdentifier("stato_verde", "drawable", context.getPackageName());
                button_stato.setBackground(getApplication().getResources().getDrawable((image_Premuto)));

            }
            else
            {
                int image_Premuto = context.getResources().getIdentifier("stato_rosso", "drawable", context.getPackageName());
                button_stato.setBackground(getApplication().getResources().getDrawable((image_Premuto)));
            }


        }

        //*********************************************************************************************
        //Emergenza
        //*********************************************************************************************
        private void Emergenza() {

            if((Double)MultiCmd_tasto_verde.getValue()==0.0d || (Double)MultiCmd_CH1_in_emergenza.getValue()==1.0d)
            {
                // StopThread = true;
                KillThread();
                Intent intent_emergenza = new Intent(getApplicationContext(),Emergency_page.class);

                startActivity(intent_emergenza);
            }
        }
    }
    //*************************************************************************************************
    private void Carica_programma(String path_xml) {

        File file = new File(path_xml);
        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0, i);

        TextView TextView_nomeprog_val = (TextView)findViewById(R.id.textView_nomeprog_val);
        TextView_nomeprog_val.setText(name);
        TextView TextView_folder_val = (TextView) findViewById(R.id.textView_folder_val);
        i = file.getPath().lastIndexOf('/');
        String folder = file.getPath().substring(0, i);
        TextView_folder_val.setText(folder);

        String path_file_udf = "c:\\cnc\\userdata\\"+name+".udf";
        Load_XML(path_xml);



        Mci_write_dtDB_prog_name.path_file = path_file_udf;     //invio il path al CN
        Mci_write_dtDB_prog_name.write_flag = true;


        Mci_write_vb1019_Load_Prog.valore = 1.0d;               //comando di forzatura load profilo
        Mci_write_vb1019_Load_Prog.write_flag = true;

        Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;    //serve rileggere i parametri da header udf
        Mci_write_Vb1025_AppReloadParamC1.write_flag = true;



    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestioneVisualizzazioneToggleButton(Mci_write mci_write, Button button, String ic_press, String ic_unpress) {

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
    private void CreaEventoEditText(final Mci_write mciWrite, final TextView textView, final double max_value, final double min_value, boolean decimale, boolean negativo, final boolean x1000) {
        textView.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    try {
                        Thread.sleep((long) 300d);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    KeyDialog.Lancia_KeyDialogo(mciWrite ,MainActivity.this,textView,max_value,min_value,false,false,0d,false,"",x1000);
                }
                return false;
            }
        });
        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


            }
        });
    }
    //*************************************************************************************************
    // Center on click
    //*************************************************************************************************
    public void btn_Center_on_click(View view) throws IOException {

        myView.Center_Bitmap_Main(0.6f, -10, 10);
        myView.AggiornaCanvas(true);

    }
    //*************************************************************************************************
    //on_click_debug
    //*************************************************************************************************
    public void on_click_debug(View view) throws IOException {

        KeyDialog.Lancia_KeyDialogo(null,MainActivity.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_parameter_ret",false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_debug, new IntentFilter("KeyDialog_parameter_ret"));

    }

    //*************************************************************************************************
    // Zoom + on click
    //*************************************************************************************************
    public void btn_ZoomPiu_on_click(View view) throws IOException {
        myView.Zoom(0.1F);
        myView.AggiornaCanvas(true);

    }

    //*************************************************************************************************
    // Zoom - on click
    //*************************************************************************************************
    public void btn_ZoomMeno_on_click(View view) throws IOException {

        myView.Zoom(-0.1F);
        myView.AggiornaCanvas(true);


    }
    //*************************************************************************************************
    // onclickPaginaTools
    //*************************************************************************************************
    public void onclickPaginaTools(View view) throws IOException {


        KillThread();
        Intent intent_for_tools = new Intent(getApplicationContext(), Tool_page.class);
        intent_for_tools.putExtra("Lato_tasca_T1", "DX");
        intent_for_tools.putExtra("Lato_tasca_T2", "DX");
        intent_for_tools.putExtra("chiamante", "Pagina_main");
        startActivityForResult(intent_for_tools, RESULT_PAGE_TOOLS);
    }
        //*************************************************************************************************
  // On_click_Load
  //*************************************************************************************************
  public void On_click_Load(View view) throws IOException
  {
      if((Double)MultiCmd_C1_CmdHoldRelease.getValue() == 1.0d){

          Mci_write_Vb4802_Reset_Cuci.valore = 1.0d;
          Mci_write_Vb4802_Reset_Cuci.write_flag = true;
      }
      Onclick_Load();
      Mci_write_Vb52_goPC.valore = 1.0d;
      Mci_write_Vb52_goPC.write_flag = true;

  }

    private void Onclick_Load() {
        KillThread();
        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent.putExtra("operazione", "Loading....");
        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF);
    }
    //*************************************************************************************************
    // on_click_button_reset_allarm
    //*************************************************************************************************
    public void on_click_button_reset_allarm(View view) throws IOException {


        Mci_Vn2_allarmi_da_CN.valore = 0.0d;
        Mci_Vn2_allarmi_da_CN.write_flag = true;


    }

    //*************************************************************************************************
    //
    //*************************************************************************************************
    private void CreaEventoStepPiuMeno() {
        Button_step_piu.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    Mci_write_Vb1006_StepPiu_singolo.Fronte_positivo = true;





                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    Mci_write_Vb1022_StepPiu_multiplo.valore = 0.0d;
                    Mci_write_Vb1022_StepPiu_multiplo.write_flag = true;

                    Mci_write_Vb1006_StepPiu_singolo.Fronte_negativo= true;



                }

                return false;

            }

        });
        Button_step_piu.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                Mci_write_Vb1022_StepPiu_multiplo.valore = 1.0d;
                Mci_write_Vb1022_StepPiu_multiplo.write_flag = true;


                return true;    // <- set to true
            }
        });


        Button_step_meno.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Mci_write_Vb1007_StepMeno_singolo.Fronte_positivo = true;
                    if((Double) MultiCmd_Vn2_allarmi_da_CN.getValue() == 2.0d){
                        Mci_Vn2_allarmi_da_CN.valore = 0.0d;
                        Mci_Vn2_allarmi_da_CN.write_flag = true;
                    }



                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    Mci_write_Vb1023_StepMeno_multiplo.valore = 0.0d;
                    Mci_write_Vb1023_StepMeno_multiplo.write_flag = true;

                    Mci_write_Vb1007_StepMeno_singolo.Fronte_negativo = true;

                }

                return false;

            }

        });
        Button_step_meno.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {


                Mci_write_Vb1023_StepMeno_multiplo.valore = 1.0d;
                Mci_write_Vb1023_StepMeno_multiplo.write_flag = true;


                return true;    // <- set to true
            }
        });





    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestiscoMci_Out_Toggle(Mci_write mci_write) {
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
    //*********************************************************************************************
    //GestiscoMci_Edge_Out
    //*********************************************************************************************
    private void GestiscoMci_Edge_Out(Mci_write mci_write) {
        switch (mci_write.mc_stati) {
            case 0:
                if (mci_write.Fronte_positivo == true ) {


                    mci_write.mci.setValue(1.0d);
                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 10;
                    mci_write.Fronte_positivo = false;

                }
                break;

            case 10:

                if (mci_write.Fronte_negativo == true ) {

                    mci_write.mci.setValue(0.0d);
                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 0;
                    mci_write.Fronte_negativo = false;

                }

                break;

            default:

                break;


        }
    }
    //*************************************************************************************************
    // ritorno da pagina x
    //*************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent databack) {
        super.onActivityResult(requestCode, resultCode, databack);


        ThreadGroup currentGroup1 = Thread.currentThread().getThreadGroup();
        int noThreads1 = currentGroup1.activeCount();
        Thread[] lstThreads1 = new Thread[noThreads1];
        currentGroup1.enumerate(lstThreads1);

        String returnedResult = "";
        try {
            returnedResult = databack.getData().toString();
        } catch (Exception e) {
            returnedResult = "";
        }
        switch (requestCode) {

            case RESULT_PAGE_TOOLS:

                //if (resultCode == RESULT_OK) {
                if (returnedResult.equals("CARICATO") || returnedResult.equals("PAGE_UDF")) {
                    //da tools mi ritorna una stringa databack che contiene tutti gli xml che devo ricaricare


                    try {
                        Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path, getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();

                        Toast.makeText(this,
                                "info_Jam.txt file error",
                                Toast.LENGTH_SHORT).show();

                    }
                    Load_XML(Values.File_XML_path);
                    Carica_programma(Values.File_XML_path);
                    Mci_write_Vb52_goPC.valore = 1.0d;
                    Mci_write_Vb52_goPC.write_flag = true;

                }
                break;

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

                        Carica_programma(Values.File_XML_path);
                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;
                    }

                }
                break;

            default:
                break;


        }



    }

    //*************************************************************************************************
  //
  //*************************************************************************************************
  public  static class Mci_write
  {
      MultiCmdItem mci;
      Boolean write_flag = false;
      Double valore = 0.0d;
      enum Comando{NULL,set_ON,set_OFF};
      public Comando comando = Comando.NULL;
      boolean SingoloImpulso = false;
      boolean PressioneLunga = false;
      Integer mc_stati = 0;
      boolean Fronte_positivo = false;
      boolean Fronte_negativo = false;
      Double valore_precedente = 0.0d;
      String mci_stringa ="";
      boolean Aggiorna_grafica = false;
      String path_file = "";
      enum TipoVariabile{NULL,VB,VN,VQ};
      public TipoVariabile tipoVariabile = TipoVariabile.NULL;


      public void Mci_write(Double Valore, Double Valore_precedente)
      {
          this.valore = Valore;
          this.valore_precedente = Valore_precedente;
          this.write_flag = false;
          this.tipoVariabile = TipoVariabile.NULL;



      }

  }

    //*********************************************************************************************
  public static class Info_Button_cicli
  {

      int step_mc_stati = 0;
      public enum Button_name{NULL,PIEGATORE,CARICATORE,SCARICATORE,TEST_PIEGATORE,CAMBIO_ALL1};
      public Button_name button_name = Button_name.NULL;
      public enum TipoButton{NULL,EDGE,TOOGLE};
      public TipoButton tipoButton = TipoButton.NULL;
      MultiCmdItem multicmd_button;
      MultiCmdItem multicmd_stato_green_red;
      boolean Run = false;
      boolean errore = false;




  }

    //*************************************************************************************************
    // Variabili "globali" per altre classi
    //*************************************************************************************************
    public static class CoordPosPinza
    {
        double XCoordPosPinza,YCoordPosPinza, XCoord_precedente,YCoord_precedente ;
        public void CoordPosPinza(double X, double Y, Ricetta r)
        {
            /*
            if(r!=null) {
                XCoordPosPinza = X - r.pcX;
                YCoordPosPinza = Y - r.pcY;
            }
            else
            {*/
                XCoordPosPinza = X;
                YCoordPosPinza = Y;
           // }

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

    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_debug);
        StopThread = true;

        try {

            if (!Thread_Running) {
                thread_Main.join();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End MainActivity Thread");

    }
}

