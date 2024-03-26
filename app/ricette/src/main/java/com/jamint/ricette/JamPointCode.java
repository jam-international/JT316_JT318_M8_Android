package com.jamint.ricette;

import java.util.ArrayList;
import java.util.List;

public class JamPointCode extends JamPoint
{
    public enum TipiCodici{ OP1, OP2, OP3, SPEED1, SPEED2, SPEED3, TENS1, TENS2, TENS3,SPEED_M8,TENS_M8}
    public enum TipiValori { VALUE0, VALUE1}

    public TipiCodici tipoCodice;
    public TipiValori valore = TipiValori.VALUE0;
    public String valore_M8;

    JamPointStep step;

    public JamPointCode()
    {

    }

    public JamPointCode(JamPointCode source)
    {
        super(source);

        if (source!=null)
        {
            //ESEGUE UNA COPIA ESATTA: RIPORTARE TUTTE LE PROPERTY!
            tipoCodice = source.tipoCodice;
            valore = source.valore;
            valore_M8 = source.valore_M8;
        }
    }

    boolean adjustStep()
    {
        if (step!=null && step.element!=null)
        {
            List<Element> elements = new ArrayList();

            if (getElementIndex()!=-1)
                elements.add(step.element); //OGGETTO element ANCORA VALIDO: PRENDE step PIU' VICINO NELLO STESSO ELEMENT!
            else if (step.element.ricetta!=null)
                elements = step.element.ricetta.elements; //OGGETTO element NON PIU' VALIDO: PRENDE step PIU' VICINO IN TUTTA LA RICETTA!

            return adjustStep(elements);
        }

        return false;
    }

    boolean adjustStep(List<Element> elements)
    {
        if (step!=null && step.element!=null)
        {
            double minDist = Double.POSITIVE_INFINITY;
            JamPointStep nearStep = null;

            for(Element e:elements )
                for (JamPointStep s: e.steps)
                {
                    double dist = Tools.getDistance(s.p, step.p);

                    if (dist<=minDist) //= SIGNIFICA CHE IN CASO DI STEP DI GIUNTURA SI PRENDE IL PRIMO DELL'ELEMENTO SUCCESSIVO
                    {
                        nearStep = s;
                        minDist = dist;
                    }
                }

            if (nearStep!=null)
            {
                step = nearStep;
                return true;
            }
        }

        return false;
    }

    int getElementIndex()
    {
        int ret = -1;

        if (step!=null && step.element!=null && step.element.ricetta!=null) //SE step O step.element NON SONO PIU' VALIDI TORNA -1
            ret = step.element.ricetta.elements.indexOf(step.element);

        return ret;
    }

    int getStepIndex()
    {
        int ret = -1;

        if (step!=null && step.element!=null && step.element.ricetta!=null) //SE step O step.element NON SONO PIU' VALIDI TORNA -1
            ret = step.element.steps.indexOf(step);

        return ret;
    }

    public JamPointStep getStep()
    {
        JamPointStep ret = null;

        if (step!=null && step.element!=null && step.element.ricetta !=null)
        {
            int elementIndex = getElementIndex();
            int stepIndex = getStepIndex();
            ret = step.element.ricetta.getStep(elementIndex, stepIndex);
        }

        return  ret;
    }

    String getVN()
    {
        return "103";
    }

    String getVQ1()
    {
        String ret ="0";

        if (tipoCodice == TipiCodici.OP1)
            ret = "771";
        else if (tipoCodice == TipiCodici.OP2)
            ret = "772";
        else if (tipoCodice == TipiCodici.OP3)
            ret = "773";
        else if (tipoCodice == TipiCodici.SPEED1)
            ret = "667";
        else if (tipoCodice == TipiCodici.SPEED2)
            ret = "668";
        else if (tipoCodice == TipiCodici.SPEED3)
            ret = "669";
        else if (tipoCodice == TipiCodici.TENS1)
            ret = "801";
        else if (tipoCodice == TipiCodici.TENS2)
            ret = "802";
        else if (tipoCodice == TipiCodici.TENS3)
            ret = "803";

        return ret;
    }

    String getVQ2()
    {
        String ret ="0";

        if (valore == TipiValori.VALUE1)
            ret = "1";

        return ret;
    }

    String getVQ3()
    {
        return "0";
    }

    String getVQ4()
    {
        return "0";
    }

}
