package com.jamint.ricette;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ElementArc extends Element
{
    public PointF pMiddle = new PointF();

    public float left = 0;
    public float top = 0;
    public float right = 0;
    public float bottom = 0;
    public float startAngle = 0;
    public float endAngle = 0;

    public ElementArc()
    {

    }

    public ElementArc(ElementArc source)
    {
        super(source);
        pMiddle.x = source.pMiddle.x;
        pMiddle.y = source.pMiddle.y;

        left = source.left;
        top = source.top;
        bottom = source.bottom;
        right = source.right;
        startAngle = source.startAngle;
        endAngle = source.endAngle;
    }

    @Override
    public void createSteps()       //daniele 060419
    {
        try
        {
            roundValues();

            TrigMatGeo tmg = new TrigMatGeo();
            CenterPointRadius cpr = tmg.getCenterPointRadius(pStart, pMiddle, pEnd);

            Log.d("arco", "Sx: " + pStart.x + "Sy: " + pStart.y + "Mx: " + pMiddle.x + "My: " + pMiddle.y + "Ex: " + pEnd.x + "Ey: " + pEnd.y + "Cx: " + cpr.center.x + "Cy: " + cpr.center.y + "R: " + cpr.radius);

            left = Tools.roundTruncate005(cpr.center.x - cpr.radius);
            top = Tools.roundTruncate005(cpr.center.y - cpr.radius);
            right = Tools.roundTruncate005(cpr.center.x + cpr.radius);
            bottom = Tools.roundTruncate005(cpr.center.y + cpr.radius);


            double angolo1 = tmg.CalcAngle2(cpr.center, pStart, cpr.center, pMiddle);
            double angolo2 = tmg.CalcAngle2(cpr.center, pMiddle, cpr.center, pEnd);


            float arcAngle = (float) (angolo1 + angolo2);
            double sagitta = tmg.Sagitta(pStart, pEnd, cpr.radius, arcAngle);
            float arcLenght = (float) tmg.arcLenght(pStart, pEnd, cpr.radius, sagitta); // (float) arc_lenght(pStart,pMiddle,pEnd,cpr.radius,cpr.center,arcAngle);

            Log.d("arco", "A1: " + angolo1 + " A2: " + angolo2 + "SommaAng: " + arcAngle + " Sagitta: " + sagitta + " ArcLenght: " + arcLenght);


            passo = passo > arcLenght ? arcLenght : passo;
            String dir_arco_padre = tmg.CalcDirez(pStart, pMiddle, pEnd);

            float rest = arcLenght % passo;         //divido la lunghezza dell'arco per il passo
            float NLP = passo;                      //numero punti con il passo richiest

            if (rest != 0)                          //se con il passo richiesto ho del resto allora modifico il passo in modo da rendere tutti punti ben spalmati
            {
                float lrest = (arcLenght - rest) / passo;
                NLP = arcLenght / (lrest + 1);              //nuovo "passo"
            }


            if (dir_arco_padre.equals("anti-clockwise")) {

                endAngle = (float) (Math.atan2((float) pStart.y - (float) cpr.center.y, (float) pStart.x - (float) cpr.center.x) * 180 / (float) Math.PI);
                startAngle = (float) Math.atan2(pEnd.y - (float) cpr.center.y, pEnd.x - (float) cpr.center.x) * 180 / (float) Math.PI;
                if (startAngle < 0) startAngle = startAngle + 360;
                if (endAngle < 0) endAngle = endAngle + 360;
                if (arcAngle > 180) startAngle = endAngle + arcAngle;

            } else {

                startAngle = (float) (Math.atan2((float) pStart.y - (float) cpr.center.y, (float) pStart.x - (float) cpr.center.x) * 180 / (float) Math.PI);
                endAngle = (float) Math.atan2(pEnd.y - (float) cpr.center.y, pEnd.x - (float) cpr.center.x) * 180 / (float) Math.PI;
                if (startAngle < 0) startAngle = startAngle + 360;
                if (endAngle < 0) endAngle = endAngle + 360;
                if (arcAngle > 180) startAngle = endAngle + arcAngle;


            }


            PointF centroCerchietto = pStart;   //creo un cerchietto di raggio = passo sul pStart dell'arco
            steps = new ArrayList<JamPointStep>();


            if (arcLenght / NLP == 2) {     // nel caso che l'arco avrà 3 punti ....
                JamPointStep step;
                step = new JamPointStep();
                step.p = new PointF(pStart.x, pStart.y);
                step.roundValues();
                step.element = this;
                steps.add(step);


                step = new JamPointStep();
                step.p = new PointF(pMiddle.x, pMiddle.y);
                step.roundValues();
                step.element = this;
                steps.add(step);

                step = new JamPointStep();
                step.p = new PointF(pEnd.x, pEnd.y);
                step.roundValues();
                step.element = this;
                steps.add(step);


            } else {

                if(cpr.radius < 3000) {

                    for (int i = 0; i <= arcLenght / NLP; i++) {

                        JamPointStep step = new JamPointStep();             //creo lo Step scegliendo le coordinate tali per cui la direzione del arco di una delle due intersezioni è uguale alla direzione dell'arco da suddividere in step

                        if (i == 0) {

                            step.p = new PointF(pStart.x, pStart.y);

                        } else if (i == arcLenght / NLP) {
                            step.p = new PointF(pEnd.x, pEnd.y);
                        } else {
                            ArrayList Intersezioni = tmg.FindCircleCircleIntersections(cpr.center, cpr.radius, centroCerchietto, NLP);  //trovo le due intersezioni del cerchietto con l'arco
                            PointF Int1A = (PointF) Intersezioni.get(0);
                            PointF Int2A = (PointF) Intersezioni.get(1);

                            PointF Int1 = new PointF(Int1A.x, Int1A.y);
                            PointF Int2 = new PointF(Int2A.x, Int2A.y);

                            if (Intersezioni.size() == 2) {


                                String direzioneIntersezione1 = tmg.CalcDirez(centroCerchietto, Int1, pEnd);//mi segno la direzione dell'arco in cui devo creare gli steps
                                String direzioneIntersezione2 = tmg.CalcDirez(centroCerchietto, Int2, pEnd);//mi segno la direzione dell'arco in cui devo creare gli steps


                                if (!direzioneIntersezione1.equals(dir_arco_padre)) {
                                    step.p = (PointF) Intersezioni.get(1);
                                    centroCerchietto = new PointF();
                                    centroCerchietto = Int2;    //setto il centro cerchietto per il prossimo giro del for
                                } else {
                                    step.p = (PointF) Intersezioni.get(0);
                                    centroCerchietto = new PointF();
                                    centroCerchietto = Int1;    //setto il centro cerchietto per il prossimo giro del for
                                }


                            }
                        }
                        step.roundValues();
                        step.element = this;
                        steps.add(step);
                    }
                }else   //linea
                {
                    float l = (float) Math.sqrt((float) Math.pow(pEnd.x - pStart.x, 2) + (float) Math.pow(pEnd.y - pStart.y, 2)); //lunghezza linea
                    if (l > (passo * 1.3)) {
                        passo = passo > l ? l : passo;
                         rest = l % passo;
                         NLP = passo;

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

                }
            }
        }catch (Exception e){}
    }

    @Override
    public void move(float dx, float dy)
    {
        pMiddle.x += dx;
        pMiddle.y += dy;
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;

        super.move(dx, dy); //SPOSTA GLI STEPS
    }

    void roundValues()
    {
        super.roundValues();

        pMiddle.x = Tools.roundTruncate005(pMiddle.x);
        pMiddle.y = Tools.roundTruncate005(pMiddle.y);
        left = Tools.roundTruncate005(left);
        top = Tools.roundTruncate005(top);
        right = Tools.roundTruncate005(right);
        bottom = Tools.roundTruncate005(bottom);
        startAngle = Tools.roundTruncate005(startAngle);
        endAngle = Tools.roundTruncate005(endAngle);
    }


    private boolean direzioneArco(PointF P1, PointF P2, PointF P3)
    {
        double _Area = Area(P1, P2, P3);
        if (_Area > 0) { return true; }
        else { return false; }
    }
    private static double Area(PointF P1, PointF P2, PointF P3)
    { return ((P1.x - P3.x) * (P2.y - P3.y) - (P1.y - P3.y) * (P2.x - P3.x)) / 2; }

    private String direzioneArco_string(PointF pStart, PointF pMiddle, PointF pEnd)
    {
        double dir_value = ((pStart.x - pEnd.x)*(pMiddle.y - pEnd.y) - (pStart.y - pEnd.y)*(pMiddle.x - pEnd.x))/2;
        if(dir_value < 0.1 && dir_value > -0.1)
        {
            return "straight line";
        }


        else
        if(dir_value< 0) return "clockwise";
        else return  "anti-clockwise";

    }
    //**************************************************************************************************************************
    // IN: 2 linea (P1 , Center e P2 Center)
    // OUT: angolo fra due linee
    //**************************************************************************************************************************
    private float calcArcAngle(PointF p_start, PointF p_end, PointF p_center)
{
    //
    // calculate the angle between the line from p1 to p2
    // and the line from p3 to p4
    //
    // uses the theorem :
    //
    // given directional vectors v = ai + bj and w = ci + di
    //
    // then cos(angle) = (ac + bd) / ( |v| * |w| )
    //
    if (p_start != p_center & p_end != p_center)
    {
        float a = p_start.x - p_center.x;
        float b = p_start.y - p_center.y;
        float c = p_end.x - p_center.x;
        float d = p_end.y - p_center.y;
        //
        double cos_angle, angle;
        double mag_v1 = Math.sqrt(a * a + b * b);
        double mag_v2 = Math.sqrt(c * c + d * d);
        //
        cos_angle = (a * c + b * d) / (mag_v1 * mag_v2);
        if (cos_angle > 1) cos_angle = 1;
        if (cos_angle < -1) cos_angle = -1;
        angle = Math.acos(cos_angle);
        angle = angle * 180.0 / 3.14159; // convert to degrees
        //
        return (float)angle;
    }
    else { return 999; }




    }
    //**************************************************************************************************************************
    // "The bulge is the tangent of one fourth the included angle for an arc segment, made negative if the arc goes clockwise
    // from the start point to the endpoint. A bulge of 0 indicates a straight segment, and a bulge of 1 is a semicircle."
    // (Source: http://www.autodesk.com/techpubs/autocad/acad2000/dxf/vertex_dxf_06.htm).
    //  Bulge = D1/D2=tan(Delta/4)
    // D1 = Sagitta = altezza dell'arco = segmento perpendicolare tra la corda e il punto medio dell'arco
    // D2 = corda = segmento tra i lpunto inizio e finale dell'arco
    // Delta = angolo arco
    //**************************************************************************************************************************
    public double Bulge(double Startangle, double EndAngle)
    {

        double swep_angolo = EndAngle - Startangle;
        if (swep_angolo < 0) swep_angolo = 360 + swep_angolo;


        return Math.tan((swep_angolo) / 4 * Math.PI / 180d);
    }

    //**************************************************************************************************************************
    // The bulge is the tangent of one fourth the included angle for an arc segment, made negative if the arc goes clockwise from the start point to the endpoint.
    //  A bulge of 0 indicates a straight segment, and a bulge of 1 is a semicircle..
    //**************************************************************************************************************************
    public String Direzione_Arco_con_Bulge(double Startangle, double EndAngle)
    {
        double Bulge_value = Bulge(Startangle,EndAngle);
        if(Bulge_value == 0)
        {
            return "straight";
        }
        if(Bulge_value == 1)
        {
            return "semicircle";

        }
        if(Bulge_value<0)
        {
            return "cw";
        }
        else
        {
            return "ccw";
        }

    }


    //*********************************************************************************************
    // lunghezza arco = 2*arcsin(L/r)  * r
    //
    //*********************************************************************************************
    public double arc_lenght(PointF pStart, PointF pMiddle, PointF pend, float radius, PointF center, float arcAngle){
      //  double distPMiddle =  distance(pMiddle.x,pMiddle.y,center.x, center.y);
        double L =  distance(pStart.x,pStart.y,pend.x,pend.y) / 2;   //distanza p1 e p1 / 2

        double arcLenght1 = 2* Math.asin(L/radius) * radius;
        double lungCirc = 2*radius*Math.PI;
        if(arcAngle > 180)
        {
            if(arcLenght1 < lungCirc/2) {
                return lungCirc-arcLenght1;

            }
        }

        return arcLenght1;
    }

    //**************************************************************************************************************************
    // calcola distanza tra due punti
    //**************************************************************************************************************************
    public double distance(double p1x, double p1y, double p2x, double p2y)
    {

        double val = ((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));

        double ret = Math.sqrt(val);
        return ret;




    }
    //**************************************************************************************************************************
    // "The bulge is the tangent of one fourth the included angle for an arc segment, made negative if the arc goes clockwise
    // from the start point to the endpoint. A bulge of 0 indicates a straight segment, and a bulge of 1 is a semicircle."
    // (Source: http://www.autodesk.com/techpubs/autocad/acad2000/dxf/vertex_dxf_06.htm).
    //  Bulge = D1/D2=tan(Delta/4)
    // D1 = Sagitta = altezza dell'arco = segmento perpendicolare tra la corda e il punto medio dell'arco
    // D2 = corda = segmento tra i lpunto inizio e finale dell'arco
    // Delta = angolo arco
    //**************************************************************************************************************************
    public double Bulge_Sagitta_corda(double Sagitta, double Corda)
    {
        return Sagitta / (Corda / 2);
    }
    //**************************************************************************************************************************
    //
    //**************************************************************************************************************************
    private String Direzione_Arco_con_Bulge(PointF pStart, PointF pEnd, float sagitta) {
        double Corda =  distance(pStart.x,pStart.y,pEnd.x,pEnd.y) /2;
        double Bulge_value = sagitta/Corda;

        if(Bulge_value == 0)
        {
            return "straight";
        }
        if(Bulge_value == 1)
        {
            return "semicircle";

        }
        if(Bulge_value<0)
        {
            return "cw";
        }
        else
        {
            return "ccw";
        }
    }


    //**************************************************************************************************************************
    // IN: 2 linea (x1,y1,x2,y2)
    // OUT: angolo fra due linee
    //**************************************************************************************************************************
    public float CalcAngle2(PointF p1, PointF p2, PointF p3, PointF p4)
    {
        //
        // calculate the angle between the line from p1 to p2
        // and the line from p3 to p4
        //
        // uses the theorem :
        //
        // given directional vectors v = ai + bj and w = ci + di
        //
        // then cos(angle) = (ac + bd) / ( |v| * |w| )
        //
        if (p1 != p2 & p3 != p4)
        {
            float a = p1.x - p2.x;
            float b = p1.y - p2.y;
            float c = p3.x - p4.x;
            float d = p3.y - p4.y;
            //
            double cos_angle, angle;
            double mag_v1 = Math.sqrt(a * a + b * b);
            double mag_v2 = Math.sqrt(c * c + d * d);
            //
            cos_angle = (a * c + b * d) / (mag_v1 * mag_v2);
            if (cos_angle > 1) cos_angle = 1;
            if (cos_angle < -1) cos_angle = -1;
            angle = Math.acos(cos_angle);
            angle = angle * 180.0 / 3.14159; // convert to degrees ???
            //
            return (float)angle;
        }
        else { return 999; }
    }
    //**************************************************************************************************************************
    // torva la lunghezza punto in modo da avere tutti i punti uguali (tranne l'ultimo)
    //**************************************************************************************************************************
    public double SpalmaLunghezzaPunto(double LunghezzaPunto, double lung)
    {
        float NPunti = (float) ((float)lung / LunghezzaPunto); //trovo numero punti con la lunghezza punto richiesta
        int decimalPlaces = 3;
        int ParteIntera = (int) NPunti;              //prendo solo la parte intera del numero dei punti che stanno nel segmento
        if (NPunti - ParteIntera > 0.5F) { ParteIntera++; }
        float NuovaLungPunto = (float) (lung / ParteIntera);

        double Ritorno = Math.round(NuovaLungPunto * 100) / 100;    //arrotondo a due cifre decimali


        return Ritorno;



    }
    //**************************************************************************************************************************
    // PopolaPuntiArco
    //**************************************************************************************************************************
    private ArrayList<PointF> PopolaPuntiArco(PointF P1, PointF P2, PointF P3, PointF Centro, double Raggio, double Lunghezza_arco, double NuovaLungPunto, boolean Direzione)
    {
        ArrayList<PointF> Lista_Punti = new ArrayList<PointF>();
        TrigMatGeo tmg = new TrigMatGeo();
        try
        {


            PointF IntersezioneBuona = new PointF();







            double x1_iniziale =  Tools.roundTruncate005(P1.x);        //scrivo il primo punto
            double y1_iniziale =Tools.roundTruncate005(P1.y);               //scrivo il primo punto
            double x3_finale = Tools.roundTruncate005(P3.x);              //scrivo il primo punto
            double y3_finale =Tools.roundTruncate005(P3.x);              //scrivo il primo punto




            int numero_Punti =  ((Double)(Lunghezza_arco / NuovaLungPunto)).intValue();   //calcolo quanti punti ci sono nell'arco

            float XCentroCerchietto = P1.x;  //prendo primo punto dell'arco
            float YCentroCerchietto = P1.y;  //prendo primo punto dell'arco

            for (int i = 0; i < numero_Punti; i++)
            {
                PointF CentroCerchietto = new PointF(XCentroCerchietto, YCentroCerchietto);

            //    Trig_Mat_Geo.Circle3P cerchietto = new Trig_Mat_Geo.Circle3P(XCentroCerchietto, YCentroCerchietto, (float)NuovaLungPunto);

                PointF Int1 = new PointF();
                PointF Int2 = new PointF();

            //    ArrayList Intersezioni = tmg.FindCircleCircleIntersections(CentroCerchietto.x,CentroCerchietto, centroCerchietto, NLP);
                ArrayList<PointF> Intersezioni = FindCircleCircleIntersections(CentroCerchietto.x, CentroCerchietto.y, (float) NuovaLungPunto,
                        Centro.x, Centro.y, (float)( Raggio)); //, out Int1, out Int2);
                // Intersezioni = arco.intersections(cerchietto);


                //passo il centro del cerchietto, P2, P3 , le due intersezioni trovate e la direzione dell'arco per scegliere una delle due intersezioni
                IntersezioneBuona = ScelgoIntersezioneSecondoDirezioneArco(CentroCerchietto, P2, P3, Intersezioni, Direzione);
                XCentroCerchietto = IntersezioneBuona.x;
                YCentroCerchietto = IntersezioneBuona.y;
                if (XCentroCerchietto != 99999 & YCentroCerchietto != 99999)  //controllo se c'è stato errore nel determinare la direzione dell'aro
                {
                    Lista_Punti.add(IntersezioneBuona);


                }
                else { } //nel caso c'è stato un errore



            }

            int n_punti = Lista_Punti.size();
            if (n_punti > 0)
            {

         //       Lista_Punti.set(0,new PointF(Tools.roundTruncate005((float) x1_iniziale),Tools.roundTruncate005((float)y1_iniziale)));
         //       Lista_Punti.set(0,new PointF(Tools.roundTruncate005((float) x3_finale),Tools.roundTruncate005((float)y3_finale)));



            }

        }
        catch (Exception e)
        {

        }

        return Lista_Punti;
    }
    //**********************************************************************************************************************
    // Find the points where the two circles intersect.
    //**********************************************************************************************************************
    public ArrayList<PointF> FindCircleCircleIntersections(float cx0, float cy0, float radius0, float cx1, float cy1, float radius1)
            //out PointF intersection1, out PointF intersection2)
    {
        ArrayList<PointF> ret = new ArrayList<PointF>();
        // Find the distance between the centers.
        float dx = cx0 - cx1;
        float dy = cy0 - cy1;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // See how many solutions there are.
        if (dist > radius0 + radius1)
        {
            // No solutions, the circles are too far apart.
            PointF intersection1 = new PointF();
            PointF intersection2 = new PointF();
            ret.add(intersection1);
            ret.add(intersection2);
            return ret;
        }
        else if (dist < Math.abs(radius0 - radius1))
        {
            // No solutions, one circle contains the other.
            PointF intersection1 = new PointF();
            PointF intersection2 = new PointF();
            ret.add(intersection1);
            ret.add(intersection2);
            return ret;
        }
        else if ((dist == 0) && (radius0 == radius1))
        {
            // No solutions, the circles coincide.
            PointF intersection1 = new PointF();
            PointF intersection2 = new PointF();
            ret.add(intersection1);
            ret.add(intersection2);
            return ret;
        }
        else
        {
            // Find a and h.
            double a = (radius0 * radius0 -
                    radius1 * radius1 + dist * dist) / (2 * dist);
            double h = Math.sqrt(radius0 * radius0 - a * a);

            // Find P2.
            double cx2 = cx0 + a * (cx1 - cx0) / dist;
            double cy2 = cy0 + a * (cy1 - cy0) / dist;

            // Get the points P3.
            PointF intersection1 = new PointF(
                    (float)(cx2 + h * (cy1 - cy0) / dist),
                    (float)(cy2 - h * (cx1 - cx0) / dist));
            PointF intersection2 = new PointF(
                    (float)(cx2 - h * (cy1 - cy0) / dist),
                    (float)(cy2 + h * (cx1 - cx0) / dist));
            ret.add(intersection1);
            ret.add(intersection2);
            return  ret;

        }
    }
    //************************************************************************************************
    // scelgo una delle tue intersezioni (tra due cerchi) guardando la direzione dell'arco
    // Per capire la direzione dell'arco, calcolo l'area tra i punti P1,P2,P3, il rusultato verrà negativo o positivo a seconda della direzione
    // Arrivando qui, mi porto la Direzione dell'arco e qui calcolo la'area dei due triangoli usando le due intersezioni trovate.
    //L'intersezione giusta, sarà quella del triangolo che avrà lo stesso segno dell'arco primario
    //************************************************************************************************
    private PointF ScelgoIntersezioneSecondoDirezioneArco(PointF CentroCerchietto, PointF p2, PointF p3, ArrayList<PointF> Intersezioni, boolean Direzione)
    {
        PointF Inter1 = new PointF(Intersezioni.get(0).x, Intersezioni.get(0).y);
        PointF Inter2 = new PointF(Intersezioni.get(1).x, Intersezioni.get(1).y);
        PointF InterScelta = new PointF();

        boolean DirezioneInter1 = direzioneArco(CentroCerchietto, Inter1, p3);
        boolean DirezioneInter2 = direzioneArco(CentroCerchietto, Inter2, p3);

        if (Direzione == false)
        {
            if (DirezioneInter1 == false) { return InterScelta = new PointF(Inter1.x, Inter1.y); }
            if (DirezioneInter2 == false) { return InterScelta = new PointF(Inter2.x, Inter2.y); }
            return InterScelta = new PointF(99999, 99999);  //se c'è errore, può capitare se l'ultimo punto dell'arco che sto calcolando supera il p3 (punto finale teorico)
        }
        if (Direzione == true)
        {
            if (DirezioneInter1 == true) { return InterScelta = new PointF(Inter1.x, Inter1.y); }
            if (DirezioneInter2 == true) { return InterScelta = new PointF(Inter2.x, Inter2.y); }
            return InterScelta = new PointF(99999, 99999);  //se c'è errore, può capitare se l'ultimo punto dell'arco che sto calcolando supera il p3 (punto finale teorico)
        }




        return InterScelta = new PointF(99999, 99999);  //se c'è errore, può capitare se l'ultimo punto dell'arco che sto calcolando supera il p3 (punto finale teorico)
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


