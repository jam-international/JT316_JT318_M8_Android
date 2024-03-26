package com.jam_int.jt316_jt318_m8;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class PopUpReset extends AppCompatActivity {

    boolean Edit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_reset);
    }

    public void BtnSave(View v)
    {
        try {
            //Info_Jam.txt
            CheckBox checkBox = findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File info_Jam = new File(JamData, "info_Jam.txt");
                info_Jam.delete();
                Edit = true;
            }

            //MachineLog.txt
            CheckBox checkBox1 = findViewById(R.id.checkBox1);
            if (checkBox1.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File MachineLog = new File(JamData, "MachineLog.txt");
                MachineLog.delete();
                Edit = true;
            }

            //Password.txt
            CheckBox checkBox2 = findViewById(R.id.checkBox2);
            if (checkBox2.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File Password = new File(JamData, "Password.txt");
                Password.delete();
                Edit = true;

                Toast.makeText(getApplicationContext(), "Default Password is 67872", Toast.LENGTH_LONG).show();
            }

            //MachineLog.txt
            CheckBox checkBox3 = findViewById(R.id.checkBox3);
            if (checkBox3.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File Users = new File(JamData, "Users.txt");
                Users.delete();
                Edit = true;
            }

            if(Edit) {
                Intent mStartActivity = new Intent(PopUpReset.this, Emergency_page.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(PopUpReset.this, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) PopUpReset.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }else {
                finish();
            }
        }catch (Exception e)
        {}
    }
    public void on_click_exit(View v) {
        finish();
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Values.Context = this;
    }
}
