package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Z_Ago extends Activity {

    ShoppingList sl;
    Thread t_Z_ago;
    Handler UpdateHandler = new Handler();
    TextView TextView_status,TextView_quota_asseX,TextView_quota_asseY,TextView_quota_Ago,TextView_quota_Crochet,TextView_quota_errore,TextView_quota_maxerror,TextView_Vel_ago;
    MultiCmdItem MultiCmd_Vb1032_Stato_azzeramento,MultiCmd_Vn1953_contatore_tacche_Z,MultiCmd_Vb1017_comando_calcola_offset,MultiCmd_Vq1716_OffsetAgoTacca,MultiCmd_Vb7041_JogAgoPiu,
            MultiCmd_Vb4047_AxAgoFermo,MultiCmd_QuotaAsseX,MultiCmd_QuotaAsseCrochet,MultiCmd_QuotaAsseY,MultiCmd_QuotaAsseAgo,MultiCmd_Vb7001_JogXpiu,MultiCmd_Vb7002_JogXmeno,MultiCmd_Vb7021_JogYpiu,MultiCmd_Vb7022_JogYmeno,
            MultiCmd_Vb7042_JogAgomeno,MultiCmd_Vq1030_error,MultiCmd_Vq1031_MAxerror,MultiCmd_Vq1032_ResetError,MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vq1955_VelAgoReale,MultiCmd_Vq108_WRITE_Vel_manualeAgo_C1,
            Multicmd_Vb78SetVelManualeAsseAgo_C1,MultiCmd_Vb1012_C1_CmdSaveParam;
    MainActivity.Mci_write Mci_write_Vb1032_Stato_azzeramento,Mci_write_Vb7001_JogXpiu,Mci_write_Vb7002_JogXmeno,Mci_write_Vb7021_JogYpiu,Mci_write_Vb7022_JogYmeno,
            Mci_write_Vb7041_JogAgopiu,Mci_write_Vb7042_JogAgomeno,Mci_write_Vb7041_JogAgo,Mci_write_ResetError,Mci_Vq108_WRITE_Vel_manualeAgo_C1,Mci_Vb78SetVelManualeAsseAgo_C1;
    MultiCmdItem[] mci_array_read_all;
    Boolean Thread_Running = false,  StopThread = false,Tacca_Z_done = true;
    Double cnt_z_iniziale= 0.0d;
    Button Button_Xpiu,Button_Xmeno,Button_Ypiu,Button_Ymeno,Button_Agopiu,Button_Agomeno,Button_reset_error,Button_Fai_Z;
    SeekBar seekBar_speed;
    Switch Switch_run_ago;

    int step_avanzamento = 0,seekbar_value=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_ago);

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera


        sl = SocketHandler.getSocket();
        sl.Clear();

        Button_Xpiu = (Button) findViewById(R.id.button_Xpiu);
        Button_Xmeno = (Button) findViewById(R.id.button_Xmeno);
        Button_Ypiu = (Button) findViewById(R.id.button_Ypiu);
        Button_Ymeno = (Button) findViewById(R.id.button_Ymeno);
        Button_Agopiu = (Button) findViewById(R.id.button_Agopiu);
        Button_Agomeno = (Button) findViewById(R.id.button_Agomeno);
        Button_reset_error = (Button) findViewById(R.id.button_reset_error);
        Button_Fai_Z = (Button) findViewById(R.id.button_Fai_Z);
        Button_Fai_Z.setVisibility(View.GONE);
        TextView_quota_asseX = (TextView)  findViewById(R.id.textView_quota_asseX);
        TextView_quota_asseY = (TextView)  findViewById(R.id.textView_quota_asseY);
        TextView_quota_Ago = (TextView)  findViewById(R.id.textView_quota_Ago);
        TextView_status = (TextView)  findViewById(R.id.textView_status);
        TextView_quota_Crochet = (TextView)  findViewById(R.id.textView_quota_Crochet);
        TextView_quota_errore = (TextView)  findViewById(R.id.textView_quota_errore);
        TextView_quota_maxerror = (TextView)  findViewById(R.id.textView_quota_maxerror);
        TextView_Vel_ago  = (TextView)  findViewById(R.id.textView_Vel_ago);

        TextView_quota_maxerror.setVisibility(View.GONE);
        Button_reset_error.setVisibility(View.GONE);
        

        MultiCmd_Vb1032_Stato_azzeramento = sl.Add("Io", 1, MultiCmdItem.dtVB, 1032, MultiCmdItem.dpNONE);
        MultiCmd_Vb1017_comando_calcola_offset = sl.Add("Io", 1, MultiCmdItem.dtVB, 1017, MultiCmdItem.dpNONE);
        MultiCmd_Vn1953_contatore_tacche_Z  = sl.Add("Io", 1, MultiCmdItem.dtVN, 1953, MultiCmdItem.dpNONE);
        MultiCmd_Vq1716_OffsetAgoTacca = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1716, MultiCmdItem.dpNONE);
        MultiCmd_Vb4047_AxAgoFermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4047, MultiCmdItem.dpNONE);

        MultiCmd_QuotaAsseX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
        MultiCmd_QuotaAsseY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
        MultiCmd_QuotaAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 53, MultiCmdItem.dpNONE);
        MultiCmd_QuotaAsseCrochet = sl.Add("Io", 1, MultiCmdItem.dtVQ, 54, MultiCmdItem.dpNONE);
        MultiCmd_Vb7001_JogXpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7001, MultiCmdItem.dpNONE);
        MultiCmd_Vb7002_JogXmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7002, MultiCmdItem.dpNONE);
        MultiCmd_Vb7021_JogYpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7021, MultiCmdItem.dpNONE);
        MultiCmd_Vb7022_JogYmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7022, MultiCmdItem.dpNONE);
        MultiCmd_Vb7041_JogAgoPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7041, MultiCmdItem.dpNONE);
        MultiCmd_Vb7042_JogAgomeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7042, MultiCmdItem.dpNONE);
        MultiCmd_Vq1030_error = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1030, MultiCmdItem.dpNONE);
        MultiCmd_Vq1031_MAxerror = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1031, MultiCmdItem.dpNONE);
        MultiCmd_Vq1032_ResetError  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1032, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vq1955_VelAgoReale  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1955, MultiCmdItem.dpNONE);
        MultiCmd_Vq108_WRITE_Vel_manualeAgo_C1 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 108, MultiCmdItem.dpNONE);
        Multicmd_Vb78SetVelManualeAsseAgo_C1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 78, MultiCmdItem.dpNONE);
        MultiCmd_Vb1012_C1_CmdSaveParam = sl.Add("Io", 1, MultiCmdItem.dtVB, 1012, MultiCmdItem.dpNONE);


        Mci_write_Vb1032_Stato_azzeramento = new MainActivity.Mci_write(); Mci_write_Vb1032_Stato_azzeramento.mci = MultiCmd_Vb1032_Stato_azzeramento;
        Mci_write_Vb7001_JogXpiu = new MainActivity.Mci_write(); Mci_write_Vb7001_JogXpiu.mci = MultiCmd_Vb7001_JogXpiu;
        Mci_write_Vb7002_JogXmeno = new MainActivity.Mci_write(); Mci_write_Vb7002_JogXmeno.mci = MultiCmd_Vb7002_JogXmeno;
        Mci_write_Vb7021_JogYpiu = new MainActivity.Mci_write(); Mci_write_Vb7021_JogYpiu.mci = MultiCmd_Vb7021_JogYpiu;
        Mci_write_Vb7022_JogYmeno = new MainActivity.Mci_write(); Mci_write_Vb7022_JogYmeno.mci = MultiCmd_Vb7022_JogYmeno;
        Mci_write_Vb7041_JogAgopiu = new MainActivity.Mci_write(); Mci_write_Vb7041_JogAgopiu.mci = MultiCmd_Vb7041_JogAgoPiu;
        Mci_write_Vb7042_JogAgomeno = new MainActivity.Mci_write(); Mci_write_Vb7042_JogAgomeno.mci = MultiCmd_Vb7042_JogAgomeno;
        Mci_write_Vb7041_JogAgo = new MainActivity.Mci_write(); Mci_write_Vb7041_JogAgo.mci = MultiCmd_Vb7041_JogAgoPiu;
        Mci_write_ResetError  = new MainActivity.Mci_write(); Mci_write_ResetError.mci = MultiCmd_Vq1032_ResetError;
        Mci_Vq108_WRITE_Vel_manualeAgo_C1  = new MainActivity.Mci_write(); Mci_Vq108_WRITE_Vel_manualeAgo_C1.mci = MultiCmd_Vq108_WRITE_Vel_manualeAgo_C1;
        Mci_Vb78SetVelManualeAsseAgo_C1  = new MainActivity.Mci_write(); Mci_Vb78SetVelManualeAsseAgo_C1.mci = Multicmd_Vb78SetVelManualeAsseAgo_C1;

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_Vb1032_Stato_azzeramento,MultiCmd_Vn1953_contatore_tacche_Z,MultiCmd_Vq1716_OffsetAgoTacca,MultiCmd_QuotaAsseX,
                MultiCmd_QuotaAsseY,MultiCmd_QuotaAsseAgo,MultiCmd_QuotaAsseCrochet,MultiCmd_Vq1030_error,MultiCmd_Vq1031_MAxerror,MultiCmd_Vq1032_ResetError,
                MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vq1955_VelAgoReale};

        EdgeButton.CreaEdgeButton(Mci_write_Vb7001_JogXpiu, Button_Xpiu,"tasto_piu_b","tasto_piu_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb7002_JogXmeno, Button_Xmeno,"tasto_meno_b","tasto_meno_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb7021_JogYpiu, Button_Ypiu,"tasto_piu_b","tasto_piu_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb7022_JogYmeno, Button_Ymeno,"tasto_meno_b","tasto_meno_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb7041_JogAgopiu, Button_Agopiu,"tasto_piu_b","tasto_piu_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_Vb7042_JogAgomeno, Button_Agomeno,"tasto_meno_b","tasto_meno_a",getApplicationContext(),sl,0);
        EdgeButton.CreaEdgeButton(Mci_write_ResetError, Button_reset_error,"tasto_meno_b","tasto_meno_a",getApplicationContext(),sl,0);


        if (!Thread_Running ) {
            StopThread = false;
            Thread_Z_ago thread_A_ago = new Thread_Z_ago(Z_Ago.this);
            t_Z_ago = new Thread(thread_A_ago, "Z_ago Task");
            t_Z_ago.setName("Thread Delta");
            t_Z_ago.start();
            Log.d("JAM TAG","Start Z_Ago Thread");
        }


        seekBar_speed = (SeekBar) findViewById(R.id.seekBar_speed);
        seekBar_speed.setMax(50);
        seekBar_speed.setProgress(1);
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


                int vel_modificata = seekbar_value;
                if(vel_modificata >50 )vel_modificata = 50;
                if(vel_modificata == 0 )vel_modificata = 5;


                Mci_Vq108_WRITE_Vel_manualeAgo_C1.valore = ( Double.valueOf(vel_modificata)*1000.0d);
                Mci_Vq108_WRITE_Vel_manualeAgo_C1.write_flag = true;

                Mci_Vb78SetVelManualeAsseAgo_C1.valore = 1.0d;
                Mci_Vb78SetVelManualeAsseAgo_C1.write_flag = true;


            }
        });


        Switch_run_ago = (Switch)  findViewById(R.id.switch_run_ago);
        Switch_run_ago.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Mci_write_Vb7041_JogAgopiu.valore =1.0d;
                else
                    Mci_write_Vb7041_JogAgopiu.valore =0.0d;

                Mci_write_Vb7041_JogAgopiu.write_flag = true;


            }

        });

    }
    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_password_ret"));


    }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        KillThread();

    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();




    }
    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();

        KillThread();

    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    class Thread_Z_ago implements Runnable {
        Activity activity;


        public Thread_Z_ago(Activity activity) {
            this.activity = activity;


        }

        @Override
        public void run() {

            while (true) {
                Thread_Running = true;
                Boolean rc_error;
                try {
                    Thread.sleep((long) 10d);
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

                    sl.WriteQueued();
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;

                    }
                    else {
                        GestiscoMci_Edge_Out(Mci_write_Vb7001_JogXpiu);
                        GestiscoMci_Edge_Out(Mci_write_Vb7002_JogXmeno);
                        GestiscoMci_Edge_Out(Mci_write_Vb7021_JogYpiu);
                        GestiscoMci_Edge_Out(Mci_write_Vb7022_JogYmeno);
                        GestiscoMci_Edge_Out(Mci_write_Vb7041_JogAgopiu);
                        GestiscoMci_Edge_Out(Mci_write_Vb7042_JogAgomeno);
                        GestiscoMci_Edge_Out(Mci_write_ResetError);
                        ScrivoVbVnVq(Mci_Vq108_WRITE_Vel_manualeAgo_C1);
                        ScrivoVbVnVq(Mci_Vb78SetVelManualeAsseAgo_C1);
                        ScrivoVbVnVq(Mci_write_Vb7041_JogAgopiu);

                        switch (step_avanzamento) {
                            case 0:
                                break;

                            case 10:
                                MultiCmd_Vq108_WRITE_Vel_manualeAgo_C1.setValue(7000.0d);
                                sl.WriteItem(MultiCmd_Vq108_WRITE_Vel_manualeAgo_C1);
                                Multicmd_Vb78SetVelManualeAsseAgo_C1.setValue(1.0d);
                                sl.WriteItem(Multicmd_Vb78SetVelManualeAsseAgo_C1);
                                step_avanzamento = 15;


                                break;
                            case 15:
                                MultiCmd_Vb7041_JogAgoPiu.setValue(1.0d);
                                sl.WriteItem(MultiCmd_Vb7041_JogAgoPiu);
                                step_avanzamento = 20;


                                break;
                            case 20:

                                if (((Double) MultiCmd_Vn1953_contatore_tacche_Z.getValue() - cnt_z_iniziale) > 5) {
                                    MultiCmd_Vb7041_JogAgoPiu.setValue(0.0d);
                                    sl.WriteItem(MultiCmd_Vb7041_JogAgoPiu);
                                    step_avanzamento = 30;
                                }
                                break;
                            case 30:
                                sl.ReadItem(MultiCmd_Vb4047_AxAgoFermo);
                                if ((Double) MultiCmd_Vb4047_AxAgoFermo.getValue() == 1.0d) {
                                    Mci_write_Vb7041_JogAgo.Fronte_negativo = true;
                                    MultiCmd_Vb1017_comando_calcola_offset.setValue(1.0d);
                                    sl.WriteItem(MultiCmd_Vb1017_comando_calcola_offset);
                                    step_avanzamento = 40;

                                }


                                break;
                            case 40:

                                MultiCmd_Vb1012_C1_CmdSaveParam.setValue(1.0d);
                                sl.WriteItem(MultiCmd_Vb1012_C1_CmdSaveParam);
                                Tacca_Z_done = true;
                                step_avanzamento = 0;

                                break;


                            default:
                                break;

                        }




                        AggiornaGuiDaThread();
                    }

                } else {
                    sl.Connect();


                }

            }

         }

        //*********************************************************************************************
        //GestiscoMci_Edge_Out
        //*********************************************************************************************
        private void GestiscoMci_Edge_Out(MainActivity.Mci_write mci_write) {
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
        //**************************************************************************************************
        //AggiornaGuiDaThread
        //**************************************************************************************************
        private void AggiornaGuiDaThread(){

            UpdateHandler.post(new Runnable() {
                @Override
                public void run() {

                    if((Double)MultiCmd_Vb1032_Stato_azzeramento.getValue() == 1.0d && !Mci_write_Vb1032_Stato_azzeramento.Fronte_positivo){

                        Mci_write_Vb1032_Stato_azzeramento.Fronte_positivo = true;
                        TextView_status.setText(getString(R.string.Ready));
                    }
                    if(Tacca_Z_done ){

                        Double v = ((Double)MultiCmd_Vq1716_OffsetAgoTacca.getValue()) /1000;
                        TextView_status.setText(getString(R.string.Done) +": "+v+"°");
                        Tacca_Z_done = false;
                    }

                    Double quotaX = (Double)MultiCmd_QuotaAsseX.getValue()/1000;
                    TextView_quota_asseX.setText("X: "+quotaX);
                    Double quotaY = (Double)MultiCmd_QuotaAsseY.getValue()/1000;
                    TextView_quota_asseY.setText("Y: "+quotaY);
                    Double quotaAgo = (Double)MultiCmd_QuotaAsseAgo.getValue()/1000;
                    TextView_quota_Ago.setText("Needle: "+quotaAgo);
                    Double quotaCrochet = (Double)MultiCmd_QuotaAsseCrochet.getValue()/1000;
                    TextView_quota_Crochet.setText("Hook: "+quotaCrochet);
                    Double quotaError = (Double)MultiCmd_Vq1030_error.getValue()/1000;
                    TextView_quota_errore.setText("Error: "+quotaError);
                    Double quotaMaxError = (Double)MultiCmd_Vq1031_MAxerror.getValue()/1000;
                    TextView_quota_maxerror.setText("MaxError: "+quotaMaxError);

                    Double VelRealeAgo = (Double)MultiCmd_Vq1955_VelAgoReale.getValue();
                    TextView_Vel_ago.setText("Real Speed: "+VelRealeAgo);

                   Emergenza();

                }


            });
        }
    }
    //*************************************************************************************************
    // onClick_exit
    //*************************************************************************************************
    public void onClick_exit(View view) throws IOException {
        KillThread();
        finish();
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
    //*************************************************************************************************
    // on_click_password
    //*************************************************************************************************
    public void on_click_password(View view) throws IOException
    {

        KeyDialog.Lancia_KeyDialogo(null, Z_Ago.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_password_ret",false);


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

            if(val.equals(linea2))
            {
                Button_Fai_Z.setVisibility(View.VISIBLE);
                TextView_quota_maxerror.setVisibility(View.VISIBLE);
                Button_reset_error.setVisibility(View.VISIBLE);

            }else if(val.equals(""))
            {}
            else
            {
                Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
            }


        }
    };
    //*************************************************************************************************
    // onClickRun
    //*************************************************************************************************
    public void onClickRun(View view) throws IOException
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning");
        builder.setMessage(getString(R.string.togliere_ago));

        builder.setPositiveButton("Run", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Premuto run
                try {

                    cnt_z_iniziale =  (Double)MultiCmd_Vn1953_contatore_tacche_Z.getValue();
                    step_avanzamento = 10;

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
    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopThread = true;
        try {
            t_Z_ago.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG","End Z_Ago Thread");

    }
}
