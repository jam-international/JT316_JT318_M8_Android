package com.jamint.ricette;

import java.util.ArrayList;

public class ElementFeed extends Element
{

    public ElementFeed()
    {

    }

    public ElementFeed(ElementFeed source)
    {
        super(source);
        passo = 0;
    }

    @Override
    public void createSteps()
    {
        //ELEMENTO FEED: CREA SOLO UNO STEP NEL PUNTO FINALE
        roundValues();

        steps = new ArrayList<JamPointStep>();
        JamPointStep step = new JamPointStep();
        step.p.x = pEnd.x;
        step.p.y = pEnd.y;
        step.roundValues();
        step.element = this;
        steps.add(step);
    }
}
