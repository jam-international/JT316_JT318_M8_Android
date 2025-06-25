package com.jamint.ricette;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public abstract class Element implements IElement
{
    public PointF pStart = new PointF();
    public PointF pEnd = new PointF();
    public float passo = Ricetta.DEFAULT_PASSO;

    public List<JamPointStep> steps = new ArrayList<JamPointStep>();

    public String entity = java.util.UUID.randomUUID().toString();
    public Boolean isSelected = false;
    public Ricetta .Recipe.

    public Element()
    {

    }

    public Element(Element source)
    {
        if (source!=null)
        {
            pStart.x = source.pStart.x;
            pStart.y = source.pStart.y;
            pEnd.x = source.pEnd.x;
            pEnd.y = source.pEnd.y;
            passo = source.passo;
            isSelected = source.isSelected;
            ricetta = source..Recipe.
            entity = source.entity;
        }
    }

    public boolean isRealElement()
    {
        return this instanceof ElementLine || this instanceof ElementArc || this instanceof ElementZigZag;
    }

    @Override
    public void move(float dx, float dy)
    {
        pStart.x += dx;
        pStart.y += dy;
        pEnd.x += dx;
        pEnd.y += dy;

        //MUOVE GLI STEP
        for(JamPointStep step: steps)
            step.move(dx, dy);
    }


    void roundValues()
    {
        pStart.x = Tools.roundTruncate005(pStart.x);
        pStart.y = Tools.roundTruncate005(pStart.y);
        pEnd.x = Tools.roundTruncate005(pEnd.x);
        pEnd.y = Tools.roundTruncate005(pEnd.y);
    }
}

