package com.jamint.ricette;

import android.graphics.PointF;

public class JamPointStep extends JamPoint
{
    public PointF p = new PointF();


    boolean isDuplicateStep = false;
    boolean isSelected = false;

    public JamPointStep()
    {

    }

    public JamPointStep(JamPointStep source)
    {
        super(source);

        if (source!=null)
        {
            //ESEGUE UNA COPIA ESATTA: RIPORTARE TUTTE LE PROPERTY!
            p.x = source.p.x;
            p.y = source.p.y;
            isDuplicateStep = source.isDuplicateStep;
        }
    }

    void move(float dx, float dy)
    {
        p.x += dx;
        p.y += dy;
    }

    public boolean isSelected()
    {
        return isSelected;
    }

//    {
//        int indexSelectionStepStart = element.ricetta.indexSelectionStepStart;
//        int indexSelectionStepEnd = element.ricetta.indexSelectionStepEnd;
//
//        if (indexSelectionStepStart == -1 || indexSelectionStepEnd == -1)
//            return false;
//
//        int stepIndex = element.ricetta.getStepIndex(this);
//        return stepIndex >= indexSelectionStepStart && stepIndex <= indexSelectionStepEnd;
//    }

    String getVN()
    {
        String ret ="103";

        if (!isDuplicateStep)
        {
            if (element instanceof ElementFeed)
                ret = "0";
            else if (element instanceof ElementLine || element instanceof ElementArc || element instanceof ElementZigZag)
                ret = "6";
        }

        return ret;
    }

    String getVQ1()
    {
        String ret ="0";

        if (!isDuplicateStep)
            ret = Tools.roundTruncate005toString(p.x); //ARROTONDATO AL #.#5 o #.#0

        return ret;
    }

    String getVQ2()
    {
        String ret ="0";

        if (!isDuplicateStep)
            ret = Tools.roundTruncate005toString(p.y); //ARROTONDATO AL #.#5 o #.#0

        return ret;
    }

    String getVQ3()
    {
        String ret ="0";

        if (!isDuplicateStep)
        {
            if (element instanceof ElementFeed)
                ret = "60";
            else if (element instanceof ElementLine || element instanceof ElementArc || element instanceof ElementZigZag)
                ret = "0";
        }

        return ret;
    }

    String getVQ4()
    {
        String ret ="0";

        if (!isDuplicateStep)
        {
            if (element instanceof ElementFeed)
                ret = "61";
            else if (element instanceof ElementLine || element instanceof ElementArc || element instanceof ElementZigZag)
                ret = "0";
        }

        return ret;
    }

    void roundValues()
    {
        p.x = Tools.roundTruncate005(p.x);
        p.y = Tools.roundTruncate005(p.y);
    }


}
