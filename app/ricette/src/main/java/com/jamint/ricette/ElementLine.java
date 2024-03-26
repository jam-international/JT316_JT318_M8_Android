package com.jamint.ricette;

import android.graphics.PointF;


import java.util.ArrayList;

public class ElementLine extends Element
{

    public ElementLine()
    {

    }

    public ElementLine(ElementLine source)
    {
        super(source);
    }

    @Override
    public void createSteps()
    {
        try {
            roundValues();

            float l = (float) Math.sqrt((float) Math.pow(pEnd.x - pStart.x, 2) + (float) Math.pow(pEnd.y - pStart.y, 2)); //lunghezza linea
            if (l > (passo * 1.3)) {
                passo = passo > l ? l : passo;
                float rest = l % passo;
                float NLP = passo;

                if (rest != 0) {
                    float lrest = (l - rest) / passo;
                    NLP = l / (lrest + 1);
                }

                steps = new ArrayList<JamPointStep>();

                for (int i = 0; i * NLP <= l; i++) {
                    ArrayList<PointF> p = intersezioneCerchioRetta(pStart, pEnd, pStart, NLP * i);

                    JamPointStep step = new JamPointStep();
                    step.p.x = p.get(0).x;
                    step.p.y = p.get(0).y;
                    step.roundValues();
                    step.element = this;
                    steps.add(step);
                }
                //controllo se l'ultimo punto trovato è almeno vicino 0.1 dallo pEnd passato altrimenti aggiungo il punto pEnd
                TrigMatGeo trigMatGeo = new TrigMatGeo();
                double dist_last_point = trigMatGeo.distance(pEnd.x, pEnd.y, steps.get(steps.size() - 1).p.x, steps.get(steps.size() - 1).p.y);
                if (dist_last_point > 0.1d) {
                    JamPointStep step = new JamPointStep();
                    step = new JamPointStep();
                    step.p.x = pEnd.x;
                    step.p.y = pEnd.y;
                    step.roundValues();
                    step.element = this;
                    steps.add(step);
                }

            } else {
                JamPointStep step = new JamPointStep();
                step.p.x = pStart.x;
                step.p.y = pStart.y;
                step.roundValues();
                step.element = this;
                steps.add(step);

                step = new JamPointStep();
                step.p.x = pEnd.x;
                step.p.y = pEnd.y;
                step.roundValues();
                step.element = this;
                steps.add(step);


            }
        }catch (Exception e){}

    }

    private ArrayList<PointF> intersezioneCerchioRetta(PointF p1, PointF p2, PointF centro, Float r)
    {
        try
        {
            ArrayList<PointF> res = new ArrayList<PointF>() {};
            double a, b, c;
            double bb4ac;
            float mu1 = 0;
            float mu2 = 0;
            PointF dp = new PointF(p2.x - p1.x, p2.y - p1.y);

            a = dp.x * dp.x + dp.y * dp.y;
            b = 2 * (dp.x * (p1.x - centro.x) + dp.y * (p1.y - centro.y));
            c = centro.x * centro.x + centro.y * centro.y;
            c += p1.x * p1.x + p1.y * p1.y;
            c -= 2 * (centro.x * p1.x + centro.y * p1.y);
            c -= r * r;
            bb4ac = b * b - 4 * a * c;
            if (Math.abs(a) < 0 || bb4ac < 0) {
                mu1 = 0;
                mu2 = 0;
                res.add(new PointF(0, 0));
                res.add(new PointF(0, 0));
                return res;
            }
            double tra = Math.sqrt((double) bb4ac);
            mu1 = (float) ((-b + tra) / (2 * a));
            mu2 = (float) ((-b - tra) / (2 * a));

            res.add(new PointF(p1.x + mu1 * (p2.x - p1.x), p1.y + mu1 * (p2.y - p1.y)));
            res.add(new PointF(p1.x + mu2 * (p2.x - p1.x), p1.y + mu2 * (p2.y - p1.y)));

            return res;
        }
        catch (Exception er)
        {
            ArrayList<PointF> res = new ArrayList<PointF>() {};
            res.add(new PointF(0, 0));
            res.add(new PointF(0, 0));
            return res;
        }
    }
}

