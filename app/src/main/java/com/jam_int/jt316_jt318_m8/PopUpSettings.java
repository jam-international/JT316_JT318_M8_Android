package com.jam_int.jt316_jt318_m8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class PopUpSettings extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_settings);

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

    }

    public void onClick_Button_exit(View view) {
        finish();
    }

    public void On_click_btn_reset(View view) {
        Intent settings = new Intent(getApplicationContext(),PopUpReset.class);
        startActivityForResult(settings,1);
    }

    public void On_click_btn_change_password(View view) {

        KeyDialog.Lancia_KeyDialogo(null,PopUpSettings.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_parameter_ret",false);

    }

    public void On_click_btn_disable_login(View view) {

    }

    public void on_click_exit(View v)
    {
        finish();
    }




    //*************************************************************************************************
    // BroadcastReceiver per prendere la risposta dal KeyDialog
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                String val = intent.getStringExtra("ret_valore");

                if(!val.equals("")) {

                    File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");

                    BufferedWriter bw = new BufferedWriter(new FileWriter(password));

                    bw.write(String.format("%s%n", val));

                    bw.close();

                    Toast.makeText(getApplicationContext(), "Password saved", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error: Password not saved", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_parameter_ret"));
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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {

        try {
            unregisterReceiver(mMessageReceiver);

        }catch (Exception e)
        {}
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1)
        {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
