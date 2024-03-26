package com.jamint.ricette;

public abstract class JamPoint //CLASSE BASE PER JamPointStep e JamPointCode
{
    public Element element; //ELEMENTO CONTENITORE

    public JamPoint()
    {

    }

    public JamPoint(JamPoint source)
    {
        if (source!=null)
            element = source.element;
    }

    abstract String getVN();
    abstract String getVQ1();
    abstract String getVQ2();
    abstract String getVQ3();
    abstract String getVQ4();


}
