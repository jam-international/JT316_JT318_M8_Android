package com.jamint.ricette;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class ElementZigZag extends Element
{
    public float altezza = Ricetta.DEFAULT_ALTEZZA; //SOLO PER ZIG ZAG (DEFAULT 3.0)
    TrigMatGeo tmg = new TrigMatGeo();

    public ElementZigZag() { }

    public ElementZigZag(ElementZigZag source)
    {
        super(source);
        passo = Ricetta.DEFAULT_PASSO_ZIGZAG;
    }

    @Override
    public void createSteps()
    {
        roundValues();
        PopolaTravetta(pStart,pEnd, altezza,passo);
    }

    private void PopolaTravetta(PointF pStart, PointF pEnd, float altezza, float passo) {

        try{
            double x1 = pStart.x;
            double y1 = pStart.y;

            double x2 = pEnd.x;
            double y2 = pEnd.y;

            ArrayList<Double> LineaCentrale = new ArrayList<Double>();
            ArrayList<Double> LineaParallelaSopra = new ArrayList<Double>();
            ArrayList<Double> LineaParallelaSotto = new ArrayList<Double>();
            ArrayList<Double> LineaPerpendicolareInizio = new ArrayList<Double>();
            ArrayList<Double> LineaPerpendicolareFine = new ArrayList<Double>();

            //linea centrale
            double y_linea = tmg.CoordToImplicitaY(x1, y1, x2, y2);
            double x_linea = tmg.CoordToImplicitaX(x1, y1, x2, y2);
            double c_linea = tmg.CoordToImplicitaC(x1, y1, x2, y2);

            LineaCentrale.add(y_linea);
            LineaCentrale.add(x_linea);
            LineaCentrale.add(c_linea);

            //linea sopra
            LineaParallelaSopra = tmg.TrovaParallela((double)altezza / 2, LineaCentrale.get(0), LineaCentrale.get(1), LineaCentrale.get(2));


            //linea sotto
            LineaParallelaSotto = tmg.TrovaParallela((double)-altezza / 2, LineaCentrale.get(0), LineaCentrale.get(1), LineaCentrale.get(2));

            //linea perpendicolare alla linea centrale passante per il punto inizio travetta
            LineaPerpendicolareInizio = tmg.PerpendicolareAdUnPunto(x1, y1, LineaCentrale.get(0), LineaCentrale.get(1), LineaCentrale.get(2));

            //linea perpendicolare alla linea centrale passante per il punto fine travetta

            LineaPerpendicolareFine = tmg.PerpendicolareAdUnPunto(x2, y2, LineaCentrale.get(0), LineaCentrale.get(1), LineaCentrale.get(2));

            //trovo intersezione tra la perpendicolare del primo punto e la retta parallela sopra

            PointF IntSopraInizio = tmg.CalcolaIntersezioneDueRette(LineaParallelaSopra.get(0),
                    LineaParallelaSopra.get(1),
                    LineaParallelaSopra.get(2),
                    LineaPerpendicolareInizio.get(0),
                    LineaPerpendicolareInizio.get(1),
                    LineaPerpendicolareInizio.get(2));



            //trovo intersezione tra la perpendicolare del primo punto e la retta parallela sotto
            PointF IntSottoInizio = tmg.CalcolaIntersezioneDueRette(LineaParallelaSotto.get(0),
                    LineaParallelaSotto.get(1),
                    LineaParallelaSotto.get(2),
                    LineaPerpendicolareInizio.get(0),
                    LineaPerpendicolareInizio.get(1),
                    LineaPerpendicolareInizio.get(2));

            //trovo intersezione tra la perpendicolare del ultimo punto e la retta parallela sopra

            PointF IntSopraFine = tmg.CalcolaIntersezioneDueRette(LineaParallelaSopra.get(0),
                    LineaParallelaSopra.get(1),
                    LineaParallelaSopra.get(2),
                    LineaPerpendicolareFine.get(0),
                    LineaPerpendicolareFine.get(1),
                    LineaPerpendicolareFine.get(2));

            //trovo intersezione tra la perpendicolare del primo punto e la retta parallela sotto
            PointF IntSottoFine = tmg.CalcolaIntersezioneDueRette(LineaParallelaSotto.get(0),
                    LineaParallelaSotto.get(1),
                    LineaParallelaSotto.get(2),
                    LineaPerpendicolareFine.get(0),
                    LineaPerpendicolareFine.get(1),
                    LineaPerpendicolareFine.get(2));



            //calcolo passo della travetta rapportato alla lunghezza della stessa
            double lunghezza_tratto = tmg.distance(x1, y1, x2, y2);


            float l = (float) Math.sqrt((float) Math.pow(pEnd.x - pStart.x, 2) + (float) Math.pow(pEnd.y - pStart.y, 2)); //lunghezza linea
            float rest = l % passo;
            float NuovaLungPunto = passo;

            if (rest != 0) {
                float lrest = (l - rest) / passo;
                NuovaLungPunto = l / (lrest + 1);
            }

           // double NuovaLungPunto = SpalmaLunghezzaPunto(Passo_Travetta, lunghezza_tratto);  //calcolo la lunghezza punto per avere punti tutti uguali

            ArrayList<PointF> TravettaParteAlta = new ArrayList<PointF>();
            ArrayList<PointF> TravettaParteBassa = new ArrayList<PointF>();
            ArrayList<PointF> TravettaParteBassa_EffettoSega = new ArrayList<PointF>();
            ArrayList<PointF> TravettaParteAlta_EffettoSega = new ArrayList<PointF>();

            TravettaParteAlta = tmg.PopolaPuntiLineaTravetta(LineaParallelaSopra.get(0), LineaParallelaSopra.get(1), LineaParallelaSopra.get(2),
                    IntSopraInizio.x, IntSopraInizio.y,
                    IntSopraFine.x, IntSopraFine.y, lunghezza_tratto, NuovaLungPunto);

            TravettaParteAlta_EffettoSega = tmg.EffettoSega(TravettaParteAlta);



            TravettaParteBassa = tmg.PopolaPuntiLineaTravetta(LineaParallelaSotto.get(0), LineaParallelaSotto.get(1), LineaParallelaSotto.get(2),
                    IntSottoInizio.x, IntSottoInizio.y,
                    IntSottoFine.x, IntSottoFine.y, lunghezza_tratto, NuovaLungPunto);


            steps = new ArrayList<JamPointStep>();

            //primo punto
            JamPointStep step = new JamPointStep();
            step.p.x = pStart.x;
            step.p.y = pStart.y;
            step.roundValues();
            step.element = this;
            steps.add(step);
            //punti zigzag

            for (int i = 0; i < TravettaParteAlta_EffettoSega.size(); i++) {
                try {

                    step = new JamPointStep();
                    step.p.x = TravettaParteBassa.get(i).x;
                    step.p.y = TravettaParteBassa.get(i).y;
                    step.roundValues();
                    step.element = this;
                    steps.add(step);
                } catch (Exception e) {
                }
                try {
                    step = new JamPointStep();
                    step.p.x = TravettaParteAlta_EffettoSega.get(i).x;
                    step.p.y = TravettaParteAlta_EffettoSega.get(i).y;
                    step.roundValues();
                    step.element = this;
                    steps.add(step);
                } catch (Exception e) {
                }



            }

            //ultimo punto
            step = new JamPointStep();
            step.p.x = pEnd.x;
            step.p.y = pEnd.y;
            step.roundValues();
            step.element = this;
            steps.add(step);



        }catch (Exception e){}

    }


}

