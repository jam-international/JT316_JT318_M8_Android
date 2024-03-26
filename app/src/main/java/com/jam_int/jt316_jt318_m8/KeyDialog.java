package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * Created by Daniele Albani on 23/05/2018.
 */

public class KeyDialog extends Activity {

    static boolean first_cycle;
    static String Text="";


    // private void Lancia_KeyDialogo( final TextView Text_premuto, final double massimo_valore, final double minimo_valore, final boolean decimale, boolean negativo) {
    public static void Lancia_KeyDialogo(final MainActivity.Mci_write mciWrite, final Activity activityctivity, final TextView Text_premuto, final double massimo_valore, final double minimo_valore, boolean decimale, boolean negativo, final double valore_default, final boolean password, final String intent_name,final boolean X1000) {

        first_cycle = true;
        final Dialog dialog = new Dialog(activityctivity);

        try {
            Text = Text_premuto.getText().toString();
        }catch (Exception e){

        }



        // Include dialog.xml file
        try {
            dialog.setContentView(R.layout.keydialog);
        }
        catch (Exception e)
        {

        }

        if(dialog != null) {

            // Set dialog title


            final TextView titolo = (TextView) dialog.findViewById(R.id.textDialog_titlo);
            titolo.setText("min:" + minimo_valore + " max:" + massimo_valore + " Default:" + valore_default);
            titolo.setTextSize(20);
            titolo.setTextColor(Color.BLUE);
            // dialog.setTitle("min:" + minimo_valore + " max:" + massimo_valore + " Default:" + valore_default);


            // set values for custom dialog components - text, image and button
            final TextView text = (TextView) dialog.findViewById(R.id.textDialog);
            if (Text_premuto != null)
                text.setText(Text_premuto.getText());


            showImmersiveDialog(dialog, activityctivity);
            dialog.show();
            dialog.getWindow().setLayout(500, 600);


            Button DecimaleButton = (Button) dialog.findViewById(R.id.button_punto);
            if (decimale == false) {
                DecimaleButton.setEnabled(false);
            }

            Button MenoButton = (Button) dialog.findViewById(R.id.button_meno);


            if (negativo == false) {
                MenoButton.setEnabled(false);
            }


            Button Pulsante1 = (Button) dialog.findViewById(R.id.button_1);
            Pulsante1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "1");
                }
            });
            Button Pulsante2 = (Button) dialog.findViewById(R.id.button_2);
            Pulsante2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "2");
                }
            });
            Button Pulsante3 = (Button) dialog.findViewById(R.id.button_3);
            Pulsante3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "3");
                }
            });
            Button Pulsante4 = (Button) dialog.findViewById(R.id.button_4);
            Pulsante4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "4");
                }
            });
            Button Pulsante5 = (Button) dialog.findViewById(R.id.button_5);
            Pulsante5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "5");
                }
            });
            Button Pulsante6 = (Button) dialog.findViewById(R.id.button_6);
            Pulsante6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "6");
                }
            });
            Button Pulsante7 = (Button) dialog.findViewById(R.id.button_7);
            Pulsante7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "7");
                }
            });
            Button Pulsante8 = (Button) dialog.findViewById(R.id.button_8);
            Pulsante8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "8");
                }
            });
            Button Pulsante9 = (Button) dialog.findViewById(R.id.button_9);
            Pulsante9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + "9");
                }
            });
            Button Pulsante0 = (Button) dialog.findViewById(R.id.button_0);
            Pulsante0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    // if(!text.getText().equals(""))
                    text.setText(text.getText() + "0");
                }
            });
            Button PulsanteMeno = (Button) dialog.findViewById(R.id.button_meno);
            PulsanteMeno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText("-");
                }
            });
            Button PulsantePunto = (Button) dialog.findViewById(R.id.button_punto);
            PulsantePunto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first_cycle) {
                        text.setText("");
                        first_cycle = false;
                    }
                    text.setText(text.getText() + ".");
                }
            });
            Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //text.setText("");

                    /*
                    if (password) {

                        //Intent intent = new Intent("KeyDialog_ret");
                        Intent intent = new Intent(intent_name);


                        intent.putExtra("ret_valore", text.getText());

                        LocalBroadcastManager.getInstance(activityctivity).sendBroadcast(intent);   //lancio il BoradCast per proseguire
                        dialog.dismiss();

                    } else {

                        if (Text_premuto != null) {
                            Text_premuto.setText(Text);
                            if (mciWrite != null) {
                                String st = text.getText().toString();
                                Double valore = Double.parseDouble(st);
                                mciWrite.valore = valore;
                                mciWrite.write_flag = true;


                            }

                        }
                        */


                        if(!intent_name.equals("")) {
                            //lancio il Broadcast per far ripartire il thread di comunicazione
                            Intent intent = new Intent(intent_name);
                           // intent.putExtra("KeyDialog_valore", "Exit");
                            intent.putExtra("ret_valore", text.getText());
                            if (Text_premuto != null) {
                                intent.putExtra("txtview_id", Text_premuto.getId());
                            }

                            LocalBroadcastManager.getInstance(activityctivity).sendBroadcast(intent);   //lancio il BoradCast per proseguire
                        }
                        dialog.dismiss();
                    }
               // }
            });
            Button CancelButton = (Button) dialog.findViewById(R.id.CancellaButton);
            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text.setText("");
                }
            });
            Button ConfermaButton = (Button) dialog.findViewById(R.id.ConfirmButton);
            ConfermaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        double number = Double.parseDouble(text.getText().toString());
                        if (number <= massimo_valore && number >= minimo_valore) {

                            if (password) {

                                Intent intent = new Intent(intent_name);
                                intent.putExtra("ret_valore", text.getText());

                                LocalBroadcastManager.getInstance(activityctivity).sendBroadcast(intent);   //lancio il BoradCast per proseguire
                                dialog.dismiss();

                            } else {

                                if (Text_premuto != null) {
                                    try {
                                        Text_premuto.setText(text.getText());
                                    }catch (Exception e)
                                    {

                                    }
                                    if (mciWrite != null) {
                                        String st = text.getText().toString();
                                        Double valore = Double.parseDouble(st);
                                            if(X1000)valore = valore *1000;
                                        mciWrite.valore = valore;
                                        mciWrite.write_flag = true;



                                    }

                                }
                                if(!intent_name.equals("")) {
                                    //lancio il Broadcast per far ripartire il thread di comunicazione
                                    Intent intent = new Intent(intent_name);
                                    // intent.putExtra("KeyDialog_valore", "Exit");
                                    intent.putExtra("ret_valore", text.getText());
                                    if (Text_premuto != null) {
                                        intent.putExtra("txtview_id", Text_premuto.getId());
                                    }

                                    LocalBroadcastManager.getInstance(activityctivity).sendBroadcast(intent);   //lancio il BoradCast per proseguire
                                }
                                dialog.dismiss();
                            }


                        } else {
                            text.setText("");
                        }
                    } catch (NumberFormatException nfe) {
                        text.setText("");
                    }


                }

            });
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keydialog);

     //   this.setFinishOnTouchOutside(false);

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


}
