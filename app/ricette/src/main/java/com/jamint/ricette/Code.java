package com.jamint.ricette;

public class Code
{
    public enum TipiCodici{ OP1, OP2, OP3, SPEED1, SPEED2, SPEED3, TENS1, TENS2, TENS3}
    public enum TipiValori { VALUE0, VALUE1}

    TipiCodici tipoCodice;
    TipiValori valore = TipiValori.VALUE0;
    int indiceStep; //INDICE POSIZIONALE DELLO STEP ALL'INTERNO DELL'ELEMENTO A CUI QUESTO CODICE FA RIFERIMENTO

    public Element element; //ELEMENTO CONTENITORE

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
