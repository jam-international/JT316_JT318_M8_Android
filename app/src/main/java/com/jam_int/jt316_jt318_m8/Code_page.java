package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.jamint.recipes.JamPointCode;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Code_page extends Activity {

    static List<JamPointCode> codeStatus;
    private static Activity activity;

    static Button Button_slow_valore,Button_tension_H1_valore;
    int stepindex =0;
    ArrayList<Integer> Lista_codici = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        Button_slow_valore = (Button) findViewById(R.id.button_slow);

        //Receive a json that contains all the codes
        Bundle bundle_code = getIntent().getExtras();
        String str = bundle_code.getString("codes");
        stepindex = bundle_code.getInt("stepindex");

        //Convert the Json to a list of JamPointCode
        Gson g = new Gson();
        JamPointCode[] codeStatus = g.fromJson(str, JamPointCode[].class);

        //Display the code values
        try {
            show_codeStatus(Arrays.asList(codeStatus));
        }catch (Exception e){}

    }

    private void show_codeStatus(List<JamPointCode> codeStatus) {
        //Get the Buttons
        Button Button_op1 = findViewById(R.id.button_op1);
        Button Button_op2 = findViewById(R.id.button_op2);
        Button Button_op3 = findViewById(R.id.button_op3);
        Button Button_rasafilo1 = findViewById(R.id.button_rasafilo1);

        Button_op1.setTag(R.drawable.code_op1);
        Button_op2.setTag(R.drawable.code_op2);
        Button_op3.setTag(R.drawable.code_op3);
        Button_rasafilo1.setTag(R.drawable.code_rasafilo);


        //Insert the value of the code in the JamPointCode list to the Buttons
        for (JamPointCode item : codeStatus) {
            JamPointCode.CodeType codice = item.codeType;

            switch (codice) {
                case OP1:
                    if (item.value == 1) {
                        Button_op1.setTag(R.drawable.code_op1_on);
                        Button_op1.setBackground(getResources().getDrawable(R.drawable.code_op1_on));
                    } else if (item.value == 0) {
                        Button_op1.setTag(R.drawable.code_op1_off);
                        Button_op1.setBackground(getResources().getDrawable(R.drawable.code_op1_off));
                    } else {
                        Button_op1.setTag(R.drawable.code_op1);
                    }
                    break;
                case OP2:
                    if (item.value == 1) {
                        Button_op2.setTag(R.drawable.code_op2_on);
                        Button_op2.setBackground(getResources().getDrawable(R.drawable.code_op2_on));
                    }
                    if (item.value == 0) {
                        Button_op2.setTag(R.drawable.code_op2_off);
                        Button_op2.setBackground(getResources().getDrawable(R.drawable.code_op2_off));
                    }
                    break;
                case OP3:
                    if (item.value == 1) {
                        Button_op3.setTag(R.drawable.code_op3_on);
                        Button_op3.setBackground(getResources().getDrawable(R.drawable.code_op3_on));
                    }
                    if (item.value == 0) {
                        Button_op3.setTag(R.drawable.code_op3_off);
                        Button_op3.setBackground(getResources().getDrawable(R.drawable.code_op3_off));
                    }
                    break;
                case RASAFILO1:
                    if (item.value == 1) {
                        Button_rasafilo1.setTag(R.drawable.code_rasafilo_no);
                        Button_rasafilo1.setBackground(getResources().getDrawable(R.drawable.code_rasafilo_no));
                    }
                    if (item.value == 0) {
                        Button_rasafilo1.setTag(R.drawable.code_rasafilo_yes);
                        Button_rasafilo1.setBackground(getResources().getDrawable(R.drawable.code_rasafilo_yes));
                    }
                    break;



                case SPEED_M8:
                    Button Button_slow_valore = (Button) findViewById(R.id.button_slow);
                    Button_slow_valore.setText(item.value);
                    break;

                default:
                    break;
            }
        }
    }

    public void on_click_set_slow(View view) {
        Button Button = (Button) findViewById(R.id.button_slow);
        Button.setText("");
        KeyDialog.Lancia_KeyDialogo(null, this, Button, 4000, 0, false, false, 1000, false, "", false);
    }
    public void on_click_op1(View view) {
        Button Button_op1 = (Button) findViewById(R.id.button_op1);
        if ((int) Button_op1.getTag() == R.drawable.code_op1) {
            Lista_codici.set(0, 1);
            Button_op1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op1_on));
            Button_op1.setTag(R.drawable.code_op1_on);
        } else if ((int) Button_op1.getTag() == R.drawable.code_op1_on) {
            Lista_codici.set(0, 2);
            Button_op1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op1_off));
            Button_op1.setTag(R.drawable.code_op1_off);
        } else if ((int) Button_op1.getTag() == R.drawable.code_op1_off) {
            Lista_codici.set(0, 0);
            Button_op1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op1));
            Button_op1.setTag(R.drawable.code_op1);
        }
    }

    public void on_click_op2(View view) {
        Button Button_op2 = (Button) findViewById(R.id.button_op2);
        if ((int) Button_op2.getTag() == R.drawable.code_op2) {
            Lista_codici.set(1, 1);
            Button_op2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op2_on));
            Button_op2.setTag(R.drawable.code_op2_on);
        } else if ((int) Button_op2.getTag() == R.drawable.code_op2_on) {
            Lista_codici.set(1, 2);
            Button_op2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op2_off));
            Button_op2.setTag(R.drawable.code_op2_off);
        } else if ((int) Button_op2.getTag() == R.drawable.code_op2_off) {
            Lista_codici.set(1, 0);
            Button_op2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op2));
            Button_op2.setTag(R.drawable.code_op2);
        }
    }

    public void on_click_op3(View view) {
        Button Button_op3 = (Button) findViewById(R.id.button_op3);
        if ((int) Button_op3.getTag() == R.drawable.code_op3) {
            Lista_codici.set(2, 1);
            Button_op3.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op3_on));
            Button_op3.setTag(R.drawable.code_op3_on);
        } else if ((int) Button_op3.getTag() == R.drawable.code_op3_on) {
            Lista_codici.set(2, 2);
            Button_op3.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op3_off));
            Button_op3.setTag(R.drawable.code_op3_off);
        } else if ((int) Button_op3.getTag() == R.drawable.code_op3_off) {
            Lista_codici.set(2, 0);
            Button_op3.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_op3));
            Button_op3.setTag(R.drawable.code_op3);
        }
    }

    public void on_click_rasafilo1(View view) {
        Button Button_rasafilo = (Button) findViewById(R.id.button_rasafilo1);
        if ((int) Button_rasafilo.getTag() == R.drawable.code_rasafilo) {
            Lista_codici.set(3, 1);
            Button_rasafilo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_rasafilo_no));
            Button_rasafilo.setTag(R.drawable.code_rasafilo_no);
        } else if ((int) Button_rasafilo.getTag() == R.drawable.code_rasafilo_no) {
            Lista_codici.set(3, 2);
            Button_rasafilo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_rasafilo_yes));
            Button_rasafilo.setTag(R.drawable.code_rasafilo_yes);
        } else if ((int) Button_rasafilo.getTag() == R.drawable.code_rasafilo_yes) {
            Lista_codici.set(3, 0);
            Button_rasafilo.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.code_rasafilo));
            Button_rasafilo.setTag(R.drawable.code_rasafilo);
        }
    }
    public void on_click_exit(View view) {
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    //Save the codes in a new Json and send back the response
    public void on_click_save(View view) throws JSONException {
        List<JamPointCode> newCodeStatus = new ArrayList<>();
        newCodeStatus.clear();

        //Create a new list of codes
        for (int i = 0; i < Lista_codici.size(); i++) {
            JamPointCode codeop = new JamPointCode();
            switch (i) {
                case 0:
                    codeop.codeType = JamPointCode.CodeType.OP1;
                    break;
                case 1:
                    codeop.codeType = JamPointCode.CodeType.OP2;
                    break;
                case 2:
                    codeop.codeType = JamPointCode.CodeType.OP3;
                    break;
                case 3:
                    codeop.codeType = JamPointCode.CodeType.RASAFILO1;

                    break;

                default:
                    break;
            }
            if (Lista_codici.get(i) == 1) {
                codeop.value = 1;
                newCodeStatus.add(codeop);
            } else if (Lista_codici.get(i) == 2) {
                codeop.value = 0;
                newCodeStatus.add(codeop);
            }
        }

        Button Button_slow_valore = (Button) findViewById(R.id.button_slow);
        String slow_valore = Button_slow_valore.getText().toString();
        if (Utility.isNumeric(slow_valore)) {
            JamPointCode codeop = new JamPointCode();
            codeop.codeType = JamPointCode.CodeType.SPEED_M8;
            codeop.codeValueType = JamPointCode.CodeValueType.Integer;
            codeop.value = Integer.valueOf( slow_valore);
            newCodeStatus.add(codeop);
        }



        //Convert the codes to a JsonArray
        JSONArray array = new JSONArray();
        for (JamPointCode code : newCodeStatus) {
            array.put(code.toJson());
        }

        //Create the response String from the JsonArray
        String res = array.toString();

        //Send the new Json codes
        getIntent().putExtra("newCodes", res);
        setResult(Activity.RESULT_OK, getIntent());

        finish();
    }


    //*************************************************************************************************
    //
    //*************************************************************************************************
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    //*************************************************************************************************
    @Override
    protected void onResume() {
        super.onResume();
        //Values.Context = this;
    }

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
            if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
            {

                getWindow().getDecorView().setSystemUiVisibility(flags);

                // JamPointCode below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                        {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility)
                            {
                                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                                {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }

        }
    }
    //Per non visualizzare la Navigation bar del Dialog, inizialmente la setto non-focusable e poi con l'evento show la rimetto focusable (in questo modo non appare).
    public static void showImmersiveDialog(final Dialog mDialog, final Activity mActivity) {
        //Set the dialog to not focusable
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        mDialog.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //Clear the not focusable flag from the window
                mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                //Update the WindowManager with the new attributes
                WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
                wm.updateViewLayout(mDialog.getWindow().getDecorView(), mDialog.getWindow().getAttributes());

            }
        });



        mDialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    mDialog.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());
                }

            }
        });
    }

    public static int setSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }



    public void onClick_exit(View view) throws IOException
    {

    //    Intent intent_par = new Intent(activity_modifica_programma, Modifica_programma.class);
    //    startActivity(intent_par);
      //  finish();


    }

}
