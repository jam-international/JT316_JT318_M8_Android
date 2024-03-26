package com.jamint.ricette;

import android.graphics.PointF;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Tools
{

    //RITORNA LA DISTANZA TRA DUE PUNTI
    public static double getDistance(PointF p1, PointF p2)
    {
        if (p1!=null && p2!=null)
        {
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            return Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
        }
        else
            return 0;
    }

    //RITORNA LA DISTANZA TRA DUE STEP
    public static double getDistance(JamPointStep s1, JamPointStep s2)
    {
        if (s1!=null && s2!=null)
            return getDistance(s1.p, s2.p);
        else
            return 0;
    }

    public static float roundTruncate005(float value)
    {
        int val1 = (int)(value * 10)*100;
        int val2 = (int)(value * 1000);

        if (value >0 ){
            if (val2-val1>0 && val2-val1<=25)
                val1 += 0;
            if (val2-val1>25 && val2-val1<=50)
                val1 += 50;
            if (val2-val1>50 && val2-val1<=75)
                val1 += 50;
            if (val2-val1>75)
                val1 += 100;
        }else {
            if (val2 - val1 < 0 && val2 + val1 >= -25)
                val1 -= 0;
            if (val2 - val1 < -25 && val2 - val1 >= -50)
                val1 -= 50;
            if (val2 - val1 < -50 && val2 - val1 >= -75)
                val1 -= 50;
            if (val2 - val1 < -75)
                val1 -= 100;
        }

        return BigDecimal.valueOf((float)val1/1000).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();


    }

    public static String roundTruncate005toString(float value)
    {
        return String.format("%.2f", roundTruncate005(value)).replace(",",".");
    }
}
