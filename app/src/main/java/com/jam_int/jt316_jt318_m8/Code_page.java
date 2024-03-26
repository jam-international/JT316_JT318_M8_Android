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
import android.widget.EditText;
import android.widget.TextView;

import com.jamint.ricette.JamPointCode;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Code_page extends Activity {

    static List<JamPointCode> codeStatus;
    private static Activity activity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);


    }


    public static void Lancia_Code_Page(final Activity activity_modifica_programma, List<JamPointCode> codeStatus, final int punto_attuale,final int angolo) {
        int idx_punto = punto_attuale;



        codeStatus =  codeStatus;
        activity = activity_modifica_programma;

        final List<JamPointCode> newCodeStatus = new ArrayList<JamPointCode>();;// = new List<JamPointCode> ();
        final ArrayList<Integer> Lista_codici = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0,0,0,0,0));
        final Dialog dialog = new Dialog(activity_modifica_programma);

        dialog.setContentView(R.layout.activity_code);
        showImmersiveDialog(dialog, activity_modifica_programma);
        dialog.show();
        dialog.getWindow().setLayout(1000, 800);

        show_codeStatus(codeStatus,dialog,activity_modifica_programma);

        Spegni_Icone(dialog);


        if(idx_punto == -1)
        {
            EditText EditText_set_angolo = (EditText) dialog.findViewById(R.id.editText_set_angolo);
            EditText_set_angolo.setVisibility(View.GONE);
            TextView TextView_set_angolo = (TextView) dialog.findViewById(R.id.textView_set_angolo);
            TextView_set_angolo.setVisibility(View.GONE);
            EditText EditText_numeri_punti_angolo = (EditText) dialog.findViewById(R.id.editText_numeri_punti_angolo);
            EditText_numeri_punti_angolo.setVisibility(View.GONE);
            TextView TextView_set_punti_rotaz = (TextView) dialog.findViewById(R.id.textView_set_punti_rotaz);
            TextView_set_punti_rotaz.setVisibility(View.GONE);

        }else
        {
            EditText EditText_angolo_iniziale = (EditText) dialog.findViewById(R.id.editText_angolo_iniziale);
            EditText_angolo_iniziale.setVisibility(View.GONE);
            TextView TextView_angolo_iniziale = (TextView) dialog.findViewById(R.id.textView_angolo_iniziale);
            TextView_angolo_iniziale.setVisibility(View.GONE);


            TextView TextView_set_angolo = (TextView) dialog.findViewById(R.id.textView_set_angolo);
            TextView_set_angolo.setText(""+angolo);



        }


        Button Button_save = (Button) dialog.findViewById(R.id.button_save);
        Button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // List<JamPointCode> newCodeStatus = Elabora_risultato();
                newCodeStatus.clear();
                for(int i = 0 ; i < Lista_codici.size(); i++)
                {
                    JamPointCode codeop = new JamPointCode();
                    switch (i) {

                        case 0:
                            codeop.tipoCodice = JamPointCode.TipiCodici.OP1;
                            break;
                        case 1:
                            codeop.tipoCodice = JamPointCode.TipiCodici.OP2;
                            break;
                        case 2:
                            codeop.tipoCodice = JamPointCode.TipiCodici.OP3;
                            break;
                        case 3:
                            codeop.tipoCodice = JamPointCode.TipiCodici.RASAFILO;
                            break;
                        default:
                            break;

                    }
                    if (Lista_codici.get(i) == 1) {
                        codeop.valore = JamPointCode.TipiValori.VALUE1;
                        newCodeStatus.add(codeop);
                    }
                    else if (Lista_codici.get(i) == 2) {
                        codeop.valore = JamPointCode.TipiValori.VALUE0;
                        newCodeStatus.add(codeop);
                    }

                }

                TextView TextView_slow_valore = (TextView) dialog.findViewById(R.id.textView_slow_valore);
                String slow_valore = TextView_slow_valore.getText().toString();
                if(isNumeric(slow_valore)){
                    JamPointCode codeop = new JamPointCode();
                    codeop.tipoCodice = JamPointCode.TipiCodici.SPEED_M8;
                    codeop.valore_M8 = slow_valore;
                    newCodeStatus.add(codeop);

                }


                 TextView TextView_tension_valore = (TextView) dialog.findViewById(R.id.textView_tension_valore);
                String tensione_valore = TextView_tension_valore.getText().toString();
                if(isNumeric(tensione_valore)){
                    JamPointCode codeop = new JamPointCode();
                    codeop.tipoCodice = JamPointCode.TipiCodici.TENS_M8;
                    codeop.valore_M8 = tensione_valore;
                    newCodeStatus.add(codeop);

                }

                TextView TextView_angolo_iniziale = (TextView) dialog.findViewById(R.id.textView_angolo_iniziale);
                String angolo_iniziale = TextView_angolo_iniziale.getText().toString();
                if(isNumeric(angolo_iniziale)){
                    JamPointCode codeop = new JamPointCode();
                    codeop.tipoCodice = JamPointCode.TipiCodici.ANGOLO_ROT;
                    codeop.Angolo_rotaz = angolo_iniziale;
                    codeop.Num_punti_rotaz = "iniziale";
                    newCodeStatus.add(codeop);

                }
                TextView TextView_set_angolo = (TextView) dialog.findViewById(R.id.textView_set_angolo);
                String Angolo = TextView_set_angolo.getText().toString();
                TextView TextView_set_punti_rotaz = (TextView) dialog.findViewById(R.id.textView_set_punti_rotaz);
                String punti_rotaz = TextView_set_punti_rotaz.getText().toString();

                if(isNumeric(Angolo) && isNumeric(punti_rotaz)){
                    JamPointCode codeop = new JamPointCode();
                    codeop.tipoCodice = JamPointCode.TipiCodici.ANGOLO_ROT;
                    codeop.Angolo_rotaz = Angolo;
                    codeop.Num_punti_rotaz = punti_rotaz;
                    newCodeStatus.add(codeop);

                }


                if(newCodeStatus.size()>0) {

                    Intent intent_code = new Intent("CodeDialog_exit");
                    Bundle bundle_code = new Bundle();
                    bundle_code.putSerializable("valoreCodice", (Serializable) newCodeStatus);
                    intent_code.putExtras(bundle_code);

                    LocalBroadcastManager.getInstance(activity_modifica_programma).sendBroadcast(intent_code);   //lancio il BoradCast per proseguire
                }
                dialog.dismiss();
            }
        });

        Button Button_exit = (Button) dialog.findViewById(R.id.button_exit);
        Button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();

            }
        });

        Button Button_op1 = (Button) dialog.findViewById(R.id.button_op1);
        Button_op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op1 = (Button) dialog.findViewById(R.id.button_op1);
                if((int)Button_op1.getTag() == R.drawable.code_op1){
                    Lista_codici.set(0, 1);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1_on));
                    Button_op1.setTag(R.drawable.code_op1_on);
                }
                else if((int) Button_op1.getTag() == R.drawable.code_op1_on){
                    Lista_codici.set(0,2);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1_off));
                    Button_op1.setTag(R.drawable.code_op1_off);
                } else  if((int)Button_op1.getTag() == R.drawable.code_op1_off){
                    Lista_codici.set(0,0);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1));
                    Button_op1.setTag(R.drawable.code_op1);
                }
            }

        });
        Button Button_op2 = (Button) dialog.findViewById(R.id.button_op2);
        Button_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op2 = (Button) dialog.findViewById(R.id.button_op2);
                if ((int)Button_op2.getTag() == R.drawable.code_op2) {
                    Lista_codici.set(1,1);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2_on));
                    Button_op2.setTag(R.drawable.code_op2_on);
                } else if ((int)Button_op2.getTag() == R.drawable.code_op2_on) {
                    Lista_codici.set(1,2);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2_off));
                    Button_op2.setTag(R.drawable.code_op2_off);
                } else if ((int)Button_op2.getTag() == R.drawable.code_op2_off) {
                    Lista_codici.set(1,0);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2));
                    Button_op2.setTag(R.drawable.code_op2);
                }
            }
        });
        Button Button_op3 = (Button) dialog.findViewById(R.id.button_op3);
        Button_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op3 = (Button) dialog.findViewById(R.id.button_op3);
                if ((int)Button_op3.getTag() == R.drawable.code_op3) {
                    Lista_codici.set(2,1);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3_on));
                    Button_op3.setTag(R.drawable.code_op3_on);
                } else if ((int)Button_op3.getTag() == R.drawable.code_op3_on) {
                    Lista_codici.set(2,2);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3_off));
                    Button_op3.setTag(R.drawable.code_op3_off);
                } else if ((int)Button_op3.getTag() == R.drawable.code_op3_off) {
                    Lista_codici.set(2,0);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3));
                    Button_op3.setTag(R.drawable.code_op3);
                }
            }
        });
        Button Button_rasafilo = (Button) dialog.findViewById(R.id.button_rasafilo);
        Button_rasafilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_rasafilo = (Button) dialog.findViewById(R.id.button_rasafilo);
                if ((int)Button_rasafilo.getTag() == R.drawable.code_rasafilo) {
                    Lista_codici.set(3,1);
                    Button_rasafilo.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_rasafilo_no));
                    Button_rasafilo.setTag(R.drawable.code_rasafilo_no);
                } else if ((int)Button_rasafilo.getTag() == R.drawable.code_rasafilo_no) {
                    Lista_codici.set(3,2);
                    Button_rasafilo.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_rasafilo_yes));
                    Button_rasafilo.setTag(R.drawable.code_rasafilo_yes);
                } else if ((int)Button_rasafilo.getTag() == R.drawable.code_rasafilo_yes) {
                    Lista_codici.set(3,0);
                    Button_rasafilo.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_rasafilo));
                    Button_rasafilo.setTag(R.drawable.code_rasafilo);
                }
            }
        });
        final TextView TextView_slow_valore = (TextView) dialog.findViewById(R.id.textView_slow_valore);
        TextView_slow_valore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView_slow_valore.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
               KeyDialog.Lancia_KeyDialogo(null, activity, TextView_slow_valore, 4000, 0, false, false,1000, false, "",false);

            }
        });
        final TextView TextView_tension_valore = (TextView) dialog.findViewById(R.id.textView_tension_valore);
        TextView_tension_valore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView_tension_valore.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity, TextView_tension_valore, 10000, 0, false, false,1000, false, "",false);

            }
        });
        final TextView TextView_angolo_iniziale = (TextView) dialog.findViewById(R.id.textView_angolo_iniziale);
        TextView_angolo_iniziale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView_angolo_iniziale.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity, TextView_angolo_iniziale, 360, -360, false, true,0, false, "",false);

            }
        });
        final TextView TextView_set_angolo = (TextView) dialog.findViewById(R.id.textView_set_angolo);
        TextView_set_angolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView_set_angolo.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity, TextView_set_angolo, 360, -360, false, true,0, false, "",false);

            }
        });
        final TextView TextView_set_punti_rotaz = (TextView) dialog.findViewById(R.id.textView_set_punti_rotaz);
        TextView_set_punti_rotaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView_set_punti_rotaz.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity, TextView_set_punti_rotaz, 100, 0, false, false,0, false, "",false);

            }
        });



    }

    private static void Spegni_Icone(Dialog dialog) {
        if(Values.Machine_model.equals("JT316M") || Values.Machine_model.equals("JT318M") || Values.Machine_model.equals("JT318M_1000x800") ){

            EditText EditText_tension,EditText_angolo_iniziale,EditText_set_angolo,EditText_numeri_punti_angolo;
            TextView TextView_tension_valore,TextView_angolo_iniziale,TextView_set_angolo,TextView_set_punti_rotaz;

            EditText_tension = (EditText) dialog.findViewById(R.id.editText_tension);
            EditText_angolo_iniziale = (EditText)  dialog.findViewById(R.id.editText_angolo_iniziale);
            EditText_set_angolo = (EditText)  dialog.findViewById(R.id.editText_set_angolo);
            EditText_numeri_punti_angolo = (EditText)  dialog.findViewById(R.id.editText_numeri_punti_angolo);
            TextView_tension_valore = (TextView)  dialog.findViewById(R.id.textView_tension_valore);
            TextView_angolo_iniziale = (TextView)  dialog.findViewById(R.id.textView_angolo_iniziale);
            TextView_set_angolo = (TextView)  dialog.findViewById(R.id.textView_set_angolo);
            TextView_set_punti_rotaz = (TextView)  dialog.findViewById(R.id.textView_set_punti_rotaz);

            EditText_tension.setVisibility(View.GONE);
            EditText_angolo_iniziale.setVisibility(View.GONE);
            EditText_set_angolo.setVisibility(View.GONE);
            EditText_numeri_punti_angolo.setVisibility(View.GONE);
            TextView_tension_valore.setVisibility(View.GONE);
            TextView_angolo_iniziale.setVisibility(View.GONE);
            TextView_set_angolo.setVisibility(View.GONE);
            TextView_set_punti_rotaz.setVisibility(View.GONE);

        }

    }


    //************************************************************************************
    //
    //************************************************************************************
    private static void show_codeStatus(List<JamPointCode> codeStatus, Dialog dialog, Context base_context) {


        Button Button_op1 = (Button) dialog.findViewById(R.id.button_op1);
        Button Button_op2 = (Button) dialog.findViewById(R.id.button_op2);
        Button Button_op3 = (Button) dialog.findViewById(R.id.button_op3);
        Button Button_rasafilo = (Button) dialog.findViewById(R.id.button_rasafilo);

        Button_op1.setTag(R.drawable.code_op1);
        Button_op2.setTag(R.drawable.code_op2);
        Button_op3.setTag(R.drawable.code_op3);
        Button_rasafilo.setTag(R.drawable.code_rasafilo);

        for(JamPointCode item: codeStatus)
        {
            String codice = item.tipoCodice.toString();



            switch (codice)
            {
                case "OP1":
                   // Button Button_op1 = (Button) dialog.findViewById(R.id.button_op1);
                    if(item.valore == JamPointCode.TipiValori.VALUE1) {
                        Button_op1.setTag(R.drawable.code_op1_on);
                        Button_op1.setBackground(base_context.getResources().getDrawable(R.drawable.code_op1_on));
                    }else if (item.valore == JamPointCode.TipiValori.VALUE0) {
                        Button_op1.setTag(R.drawable.code_op1_off);
                        Button_op1.setBackground(base_context.getResources().getDrawable(R.drawable.code_op1_off));
                    }
                    else {
                        Button_op1.setTag(R.drawable.code_op1);
                    }

                    break;

                case "OP2":
                    if(item.valore == JamPointCode.TipiValori.VALUE1) {
                        Button_op2.setTag(R.drawable.code_op2_on);
                        Button_op2.setBackground(base_context.getResources().getDrawable(R.drawable.code_op2_on));
                    }
                    if(item.valore == JamPointCode.TipiValori.VALUE0) {
                        Button_op2.setTag(R.drawable.code_op2_off);
                        Button_op2.setBackground(base_context.getResources().getDrawable(R.drawable.code_op2_off));
                    }
                    break;
                case "OP3":
                    if(item.valore == JamPointCode.TipiValori.VALUE1) {
                        Button_op3.setTag(R.drawable.code_op3_on);
                        Button_op3.setBackground(base_context.getResources().getDrawable(R.drawable.code_op3_on));
                    }
                    if(item.valore == JamPointCode.TipiValori.VALUE0) {
                        Button_op3.setTag(R.drawable.code_op3_off);
                        Button_op3.setBackground(base_context.getResources().getDrawable(R.drawable.code_op3_off));
                    }
                    break;

                case "ANGOLO_ROT":
                    if(item.Num_punti_rotaz.equals("iniziale")) {
                        TextView TextView_angolo_iniziale = (TextView) dialog.findViewById(R.id.textView_angolo_iniziale);
                        TextView_angolo_iniziale.setText("" + item.Angolo_rotaz);
                    }

                    break;
                case "RASAFILO":
                    if(item.valore == JamPointCode.TipiValori.VALUE1) {
                        Button_rasafilo.setTag(R.drawable.code_rasafilo_no);
                        Button_rasafilo.setBackground(base_context.getResources().getDrawable(R.drawable.code_rasafilo_no));
                    }
                    if(item.valore == JamPointCode.TipiValori.VALUE0) {
                        Button_rasafilo.setTag(R.drawable.code_rasafilo_yes);
                        Button_rasafilo.setBackground(base_context.getResources().getDrawable(R.drawable.code_rasafilo_yes));
                    }

                    break;

                    default:
                        break;

            }

        }
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
