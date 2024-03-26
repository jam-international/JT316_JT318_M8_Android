package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Delta_parametri extends Activity {
    ShoppingList sl;
    Thread t_Delta;
    Handler UpdateHandler = new Handler();
    MultiCmdItem MultiCmd_Vn1690_Delta_Status,MultiCmd_Vn1691_Delta_error,MultiCmd_Vq1690_Delta_valore,Multicmd_Vn1693_num_ax,Multicmd_Vn1692_comando_read_write,
            Multicmd_Vn1694_categoria,Multicmd_Vn1695_num_parametro,MultiCmd_Vn1696_SetParamSize;
    MultiCmdItem[] mci_array_read_all;
    Boolean Thread_Running = false,  StopThread = false;
    EditText EditTextTextCategoria,EditTextTextParametro,EditTextTextWriteValue;
    TextView  EditTextTextReadResult;
    RadioButton RadioButton_ax1,RadioButton_ax2,RadioButton_size16,RadioButton_size32;
    boolean Read = false,Write = false,Read_dopo_Write = false,ShowRead = false,Show_Read_dopo_Write = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delta_parametri);

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera

        EditTextTextCategoria = (EditText)findViewById(R.id.editTextTextCategoria);
        EditTextTextParametro = (EditText)findViewById(R.id.editTextTextParametro);
        EditTextTextWriteValue = (EditText)findViewById(R.id.editTextTextWriteValue);
        EditTextTextCategoria.setText("2");
        EditTextTextParametro.setText("4");
        RadioButton_ax1 = (RadioButton)findViewById(R.id.radioButton_ax1);
        RadioButton_ax1.setChecked(true);
        RadioButton_ax2 = (RadioButton)findViewById(R.id.radioButton_ax2);

        RadioButton_size16 = (RadioButton)findViewById(R.id.radioButton_size16);
        RadioButton_size16.setChecked(true);
        RadioButton_size32 = (RadioButton)findViewById(R.id.radioButton_size32);
        EditTextTextReadResult = (TextView)findViewById(R.id.editTextTextReadResult);

        sl = SocketHandler.getSocket();
        sl.Clear();

        MultiCmd_Vn1690_Delta_Status  = sl.Add("Io", 1, MultiCmdItem.dtVN, 1690, MultiCmdItem.dpNONE);
        MultiCmd_Vn1691_Delta_error  = sl.Add("Io", 1, MultiCmdItem.dtVN, 1691, MultiCmdItem.dpNONE);
        MultiCmd_Vq1690_Delta_valore = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1690, MultiCmdItem.dpNONE);
        Multicmd_Vn1693_num_ax = sl.Add("Io", 1, MultiCmdItem.dtVN, 1693, MultiCmdItem.dpNONE);
        Multicmd_Vn1692_comando_read_write = sl.Add("Io", 1, MultiCmdItem.dtVN, 1692, MultiCmdItem.dpNONE);
        Multicmd_Vn1694_categoria = sl.Add("Io", 1, MultiCmdItem.dtVN, 1694, MultiCmdItem.dpNONE);
        Multicmd_Vn1695_num_parametro = sl.Add("Io", 1, MultiCmdItem.dtVN, 1695, MultiCmdItem.dpNONE);
        MultiCmd_Vn1696_SetParamSize  = sl.Add("Io", 1, MultiCmdItem.dtVN, 1696, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_Vn1690_Delta_Status,MultiCmd_Vn1691_Delta_error};


        if (!Thread_Running ) {
            StopThread = false;
            MyAndroidThread_Delta DeltaTask = new MyAndroidThread_Delta(Delta_parametri.this);
            t_Delta = new Thread(DeltaTask, "Delta Task");
            t_Delta.setName("Thread Delta");
            t_Delta.start();
            Log.d("JAM TAG","Start Delta_parametri Thread");
        }


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
    class MyAndroidThread_Delta implements Runnable {
        Activity activity;
        int cnt_giri_a_vuoto=5;

        public MyAndroidThread_Delta(Activity activity) {
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
                       // rc_error = true;

                    } else {

                       // rc_error = false;

                        if(Read) {


                            sl.WriteItem(Multicmd_Vn1694_categoria);
                            sl.WriteItem(Multicmd_Vn1695_num_parametro);
                            sl.WriteItem(Multicmd_Vn1693_num_ax);
                            sl.WriteItem(MultiCmd_Vn1696_SetParamSize);
                            sl.WriteItem(Multicmd_Vn1692_comando_read_write);
                            sl.ReadItem(MultiCmd_Vq1690_Delta_valore);


                            if(cnt_giri_a_vuoto==0) {    //faccio due letture e poi visualizzo il risultato
                                cnt_giri_a_vuoto = 2;
                                Read = false;
                                ShowRead = true;
                            }else
                                cnt_giri_a_vuoto--;



                        }
                        if(Read_dopo_Write){
                            Read_dopo_Write = false;
                            Multicmd_Vn1692_comando_read_write.setValue(1.0d);
                            sl.WriteItem(Multicmd_Vn1692_comando_read_write);


                        }
                        if(Write) {
                            sl.WriteItem(Multicmd_Vn1694_categoria);
                            sl.WriteItem(Multicmd_Vn1695_num_parametro);
                            sl.WriteItem(Multicmd_Vn1693_num_ax);
                            sl.WriteItem(MultiCmd_Vn1696_SetParamSize);
                            sl.WriteItem(MultiCmd_Vq1690_Delta_valore);

                            sl.WriteItem(Multicmd_Vn1692_comando_read_write);
                            Write = false;
                            Show_Read_dopo_Write = true;
                        }

                    }





                    AggiornaGuiDaThread();









                }else
                    sl.Connect();

            }
        }
        //**************************************************************************************************
        //AggiornaGuiDaThread
        //**************************************************************************************************
        private void AggiornaGuiDaThread(){

            UpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    TextView EditTextTextReady = (TextView) findViewById(R.id.editTextTextReady);
                    if ((Double) MultiCmd_Vn1690_Delta_Status.getValue() == 0.0d) {
                        EditTextTextReady.setText("Status: Ready");

                    } else
                    {
                        EditTextTextReady.setText("Status: NOT Ready");
                    }

                    TextView EditTextTextError = (TextView) findViewById(R.id.editTextTextError);
                    Double error = (Double)MultiCmd_Vn1691_Delta_error.getValue();
                    EditTextTextError.setText("Error: "+ error);

                    if(ShowRead) {
                        Double val = (Double) MultiCmd_Vq1690_Delta_valore.getValue();
                        EditTextTextReadResult.setText("" + val);
                        ShowRead = false;

                    }
                    if(Show_Read_dopo_Write) {
                        Double val = (Double) MultiCmd_Vq1690_Delta_valore.getValue();
                        EditTextTextReadResult.setText("" + val);
                        Show_Read_dopo_Write = false;
                    }
                }


            });
        }
    }

    //*************************************************************************************************
    // onClick_Write
    //*************************************************************************************************
    public void onClick_Write(View view) throws IOException {

        try {
            if(RadioButton_size16.isChecked() || RadioButton_size32.isChecked()) {
                String Valore = EditTextTextWriteValue.getText().toString();
                if (Valore.trim().length() > 0){
                    Double Valore_da_scrivere = Double.parseDouble(Valore);
                    MultiCmd_Vq1690_Delta_valore.setValue((Double)Valore_da_scrivere  );

                    Double Categoria = Double.parseDouble(EditTextTextCategoria.getText().toString());
                    Double Parametro = Double.parseDouble(EditTextTextParametro.getText().toString());
                    Multicmd_Vn1694_categoria.setValue((Double) Categoria);
                    Multicmd_Vn1695_num_parametro.setValue((Double) Parametro);

                    if (RadioButton_ax1.isChecked()) {
                        Multicmd_Vn1693_num_ax.setValue(3.0d);

                    }
                    if (RadioButton_ax2.isChecked()) {
                        Multicmd_Vn1693_num_ax.setValue(4.0d);

                    }


                    if(RadioButton_size16.isChecked()){

                        MultiCmd_Vn1696_SetParamSize.setValue(2.0d);
                    }
                    if(RadioButton_size32.isChecked()){

                        MultiCmd_Vn1696_SetParamSize.setValue(4.0d);
                    }

                    Multicmd_Vn1692_comando_read_write.setValue(2.0d);
                    Write = true;

                }
                else
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.InserireValore), Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.SelezionaSize), Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){}

    }
    //*************************************************************************************************
    // on_click_read
    //*************************************************************************************************
    public void on_click_read(View view) throws IOException {


        try {
            if(RadioButton_size16.isChecked() || RadioButton_size32.isChecked()) {
                Double Categoria = Double.parseDouble(EditTextTextCategoria.getText().toString());
                Double Parametro = Double.parseDouble(EditTextTextParametro.getText().toString());
                Multicmd_Vn1694_categoria.setValue((Double) Categoria);
                Multicmd_Vn1695_num_parametro.setValue((Double) Parametro);

                if (RadioButton_ax1.isChecked()) {
                    Multicmd_Vn1693_num_ax.setValue(3.0d);

                }
                if (RadioButton_ax2.isChecked()) {
                    Multicmd_Vn1693_num_ax.setValue(4.0d);

                }


                if(RadioButton_size16.isChecked()){

                    MultiCmd_Vn1696_SetParamSize.setValue(2.0d);
                }
                if(RadioButton_size32.isChecked()){

                    MultiCmd_Vn1696_SetParamSize.setValue(4.0d);
                }

                Multicmd_Vn1692_comando_read_write.setValue(1.0d);
                Read = true;
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.SelezionaSize), Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){}
    }
    //
    //*************************************************************************************************
    // onClick_exit
    //*************************************************************************************************
    public void onClick_exit(View view) throws IOException {
        StopThread = true;
        KillThread();
        finish();
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


        try {
            t_Delta.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG","End Delta_parametri Thread");

    }
}
