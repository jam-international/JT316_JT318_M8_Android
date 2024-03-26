package com.jamint.ricette;

import android.graphics.PointF;

public class Step
{
    public enum TipiStep{ CUCITURA, FEED, CODICE};

    public TipiStep tipoStep = TipiStep.CUCITURA; //1=CUCITURA->6, 2=FEED->0, 3=CODICE->103

    public PointF p = new PointF();
    public Element element;

    void move(float dx, float dy)
    {
        p.x += dx;
        p.y += dy;
    }

    String getVN()
    {
        String ret ="0";

        if (tipoStep == TipiStep.CUCITURA)
            ret = "6";
        else if (tipoStep == TipiStep.FEED)
            ret = "0";
        else if (tipoStep == TipiStep.CODICE)
            ret = "103";

        return ret;
    }

    String getVQ1()
    {
        return String.format("%.2f", Tools.roundTruncate005(p.x)).replace(",","."); //ARROTONDATO AL #.#5 o #.#0
    }

    String getVQ2()
    {
        return String.format("%.2f", Tools.roundTruncate005(p.y)).replace(",","."); //ARROTONDATO AL #.#5 o #.#0
    }

    String getVQ3()
    {
        String ret ="0";

        if (tipoStep == TipiStep.CUCITURA)
            ret = "0";
        else if (tipoStep == TipiStep.FEED)
            ret = "60";
        else if (tipoStep == TipiStep.CODICE)
            ret = "0";

        return ret;
    }

    String getVQ4()
    {
        String ret ="0";

        if (tipoStep == TipiStep.CUCITURA)
            ret = "0";
        else if (tipoStep == TipiStep.FEED)
            ret = "61";
        else if (tipoStep == TipiStep.CODICE)
            ret = "0";

        return ret;
    }

    void roundValues()
    {
        p.x = Tools.roundTruncate005(p.x);
        p.y = Tools.roundTruncate005(p.y);
    }

}
