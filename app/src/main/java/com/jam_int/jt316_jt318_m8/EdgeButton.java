package com.jam_int.jt316_jt318_m8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import communication.MultiCmdItem;
import communication.ShoppingList;

/**
 * Created by Daniele Albani on 24/05/2018.
 */

public class EdgeButton {

    Context context;
    long delay = 0;

    @SuppressLint("ClickableViewAccessibility")
    public static void CreaEdgeButton(final MainActivity.Mci_write multiCmd, final Button button, final String ic_button_press, final String ic_button, final Context applicationContext, final ShoppingList sl, final long delay) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED


                            if(ic_button_press != null) {
                                int image_Premuto = applicationContext.getResources().getIdentifier(ic_button_press, "drawable", applicationContext.getPackageName());
                                button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                            }
                            multiCmd.Fronte_positivo = true;




                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED

                            if (ic_button_press != null) {
                                int image_Non_Premuto = applicationContext.getResources().getIdentifier(ic_button, "drawable", applicationContext.getPackageName());
                                button.setBackground(applicationContext.getResources().getDrawable((image_Non_Premuto)));
                            }



                            multiCmd.Fronte_negativo = true;




                        return true;

                    default:


                }
                return false;
            }
        });


    }
    public static void CreaEdgeButton_Frecce(final MainActivity.Mci_write multiCmd, final Button button, final String ic_button_press, final String ic_button, final Context applicationContext, final ShoppingList sl, final long delay) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if(ic_button_press != null) {
                            int image_Premuto = applicationContext.getResources().getIdentifier(ic_button_press, "drawable", applicationContext.getPackageName());
                            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                        }


                        multiCmd.valore = 1.0d;
                        multiCmd.write_flag = true;


                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED


                        if(ic_button_press != null) {
                            int image_Non_Premuto = applicationContext.getResources().getIdentifier(ic_button, "drawable", applicationContext.getPackageName());
                            button.setBackground(applicationContext.getResources().getDrawable((image_Non_Premuto)));
                        }

                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        multiCmd.valore = 0.0d;
                        multiCmd.write_flag = true;

                        return true;

                    default:


                }
                return false;
            }
        });


    }


    //*************************************************************************************************
    // Disabilita Image button
    //*************************************************************************************************
    public static void Disabilita_Imagebutton(ImageButton imagebutton, String image_filename, Context context)
    {

        imagebutton.setClickable(false);
        imagebutton.setEnabled(false);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        imagebutton.setImageResource(image_Premuto);

    }
    //*************************************************************************************************
    // abilita Image button
    //*************************************************************************************************
    public static void Abilita_Imagebutton(ImageButton imagebutton, String image_filename, Context context)
    {

        imagebutton.setClickable(true);
        imagebutton.setEnabled(true);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        imagebutton.setImageResource(image_Premuto);

    }

    //*************************************************************************************************
    // Visualizza_stato_VB
    //*************************************************************************************************
    public static void Visualizza_stato_VB(final MainActivity.Mci_write Mci, final Button button, final String stato_off, final String stato_on, final Context applicationContext, final ShoppingList sl) {


        if((Double) Mci.mci.getValue() == 1.0d  )
        {
            int image_Premuto = applicationContext.getResources().getIdentifier(stato_on, "drawable", applicationContext.getPackageName());
            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            Mci.valore_precedente = 1.0d;

        }else{

            int image_Premuto = applicationContext.getResources().getIdentifier(stato_off, "drawable", applicationContext.getPackageName());
            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            Mci.valore_precedente = 0.0d;
        }

    }
    //*************************************************************************************************
    // Visibilita_associata_a_VB
    //*************************************************************************************************
    public static void Visibilita_associata_a_VB(MultiCmdItem multiCmd, ImageView imageV, Context applicationContext) {

        if((Double)multiCmd.getValue()== 1.0d) imageV.setVisibility(View.VISIBLE);
        else imageV.setVisibility(View.GONE);
    }
    //*************************************************************************************************
    // Visibilita_associata_a_VB
    //*************************************************************************************************
    public static void Visibilita_button_associata_a_VB(MultiCmdItem multiCmd, Button button, Context applicationContext) {

        if((Double)multiCmd.getValue()== 1.0d) button.setVisibility(View.VISIBLE);
        else button.setVisibility(View.GONE);
    }
    //*************************************************************************************************
    //
    //*************************************************************************************************

    public static void CreaEdgeButton_MultiCmdItem(final MultiCmdItem multiCmd, final Button button, final String ic_button_press, final String ic_button, final Context applicationContext, final ShoppingList sl) {
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // PRESSED
                    if(ic_button_press != null) {
                        int image_Premuto = applicationContext.getResources().getIdentifier(ic_button_press, "drawable", applicationContext.getPackageName());
                        button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                    }
                    multiCmd.setValue(1.0d);
                    sl.WritePush(multiCmd);

                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_UP:
                    // RELEASED

                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(ic_button_press != null) {
                        int image_Non_Premuto = applicationContext.getResources().getIdentifier(ic_button, "drawable", applicationContext.getPackageName());
                        button.setBackground(applicationContext.getResources().getDrawable((image_Non_Premuto)));
                    }

                    multiCmd.setValue(0.0d);
                    sl.WritePush(multiCmd);



                    return true; // if you want to handle the touch event
            }
            return false;
        }
    });
    }
}
