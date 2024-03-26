package com.jam_int.jt316_jt318_m8;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import communication.ShoppingList;

/**
 * Created by Daniele Albani on 25/05/2018.
 */

public class Toggle_Button {

    public Toggle_Button()
    {

    }


    //*************************************************************************************************
    // CreaToggleButton
    //*************************************************************************************************
    public static void CreaToggleButton(final MainActivity.Mci_write mci, final Button button, final String ic_press, final String ic_unpress, final Context applicationContext, final ShoppingList sl) {


        //al primo giro, imposto l'icona del button premuta oppure no a seconda della Vb letta, poi verrà cambiata dall'evento
        if((Double) mci.mci.getValue()== 1.0d) {
            if(ic_press != null) {
                int image_Premuto = applicationContext.getResources().getIdentifier(ic_press, "drawable", applicationContext.getPackageName());
                button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            }
        }else
        {
            if(ic_unpress != null) {
                int image_Premuto = applicationContext.getResources().getIdentifier(ic_unpress, "drawable", applicationContext.getPackageName());
                button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            }
        }

        button.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    mci.Fronte_positivo = true;


                }

                return false;
            }
        });

    }
    //*************************************************************************************************
    // Disabilita Image button
    //*************************************************************************************************
    public static void Disabilita_Imagebutton(Button button, String image_filename, Context context)
    {

        button.setClickable(false);
        button.setEnabled(false);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        //button.setBackground(context.getResources().getDrawable((image_Premuto)));
        button.setBackgroundResource(image_Premuto);



    }
    //*************************************************************************************************
    // abilita Image button
    //*************************************************************************************************
    public static void Abilita_Imagebutton(Button button, String image_filename, Context context)
    {

        button.setClickable(true);
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.ic_login_on);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        //button.setBackground(context.getResources().getDrawable((image_Premuto)));
        button.setBackgroundResource(image_Premuto);

    }
}
