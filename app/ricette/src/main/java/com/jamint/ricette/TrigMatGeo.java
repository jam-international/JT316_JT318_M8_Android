package com.jamint.recipes;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

public class TrigMatGeo {

    //**********************************************************************************************************
    // IN: 3 PUNTI  A B C
    // OUT: raggio in m_dRadius  e centro m_Center    !! XCentro, YCentro, Raggio  !!
    // il centro è l'intersezione delle bisettrice di due lati (bisettrici = retta perpendicolare al lato che passa per il suo punto medio)
    //**********************************************************************************************************
    public CenterPointRadius getCenterPointRadius(PointF pt1, PointF pt2, PointF pt3) {

        CenterPointRadius centerPointRadius = new CenterPointRadius();

        try {
            //N.B: di seguito calcolo i delta, se le due coordinate sono uguali la sottrazione da risultato infinito e quindi si blocca tutto,
            //per risolvere questo, in questi casi sposto una coordinata di 0.001 e supero il problema
            if (pt2.x == pt1.x) pt2.x = pt2.x + 0.001F;
            if (pt2.y == pt1.y) pt2.y = pt2.y + 0.001F;
            if (pt2.x == pt3.x) pt2.x = pt2.x + 0.001F;
            if (pt2.y == pt3.y) pt2.y = pt2.y + 0.001F;

            //calcolo punti medi dei lati P1P2 e P2P3
            PointF P1P2_medio = calcolaPuntoMedio(pt1.x, pt1.y, pt2.x, pt2.y);
            PointF P2P3_medio = calcolaPuntoMedio(pt2.x, pt2.y, pt3.x, pt3.y);
            //trovo perpendicolare ai lati P1P2 e P2P3 che passano per il loro punto medio

            double y_rettaP1P2 = coordToImplicitaY(pt1.x, pt1.y, pt2.x, pt2.y);
            double x_rettaP1P2 = coordToImplicitaX(pt1.x, pt1.y, pt2.x, pt2.y);
            double c_rettaP1P2 = coordToImplicitaC(pt1.x, pt1.y, pt2.x, pt2.y);

            List<Double> P1P2_bisettrice = perpAdUnPunto(P1P2_medio.x, P1P2_medio.y, y_rettaP1P2, x_rettaP1P2, c_rettaP1P2);

            double y_rettaP2P3 = coordToImplicitaY(pt2.x, pt2.y, pt3.x, pt3.y);
            double x_rettaP2P3 = coordToImplicitaX(pt2.x, pt2.y, pt3.x, pt3.y);
            double c_rettaP2P3 = coordToImplicitaC(pt2.x, pt2.y, pt3.x, pt3.y);

            List<Double> P2P3_bisettrice = perpAdUnPunto(P2P3_medio.x, P2P3_medio.y, y_rettaP2P3, x_rettaP2P3, c_rettaP2P3);

            //calcolo intersezione = centro
            PointF Centro = calcolaIntersezioneDueRette(P1P2_bisettrice.get(0),
                    P1P2_bisettrice.get(1),
                    P1P2_bisettrice.get(2),
                    P2P3_bisettrice.get(0),
                    P2P3_bisettrice.get(1),
                    P2P3_bisettrice.get(2));

            double p1x = (double) pt1.x;
            double p1y = (double) pt1.y;
            double p2x = (double) Centro.x;
            double p2y = (double) Centro.y;
            double val = ((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
            double ret = Math.sqrt(val);

            float Raggio = (float) ret;

            centerPointRadius.center.x = Centro.x;
            centerPointRadius.center.y = Centro.y;
            centerPointRadius.radius = Raggio;
        } catch (Exception ex) {
        }

        return centerPointRadius;
    }


    //*************************************************************************************************************************
    // trovo punto medio di un segmento
    // p1 + (p2-p1)*0.5 = (p1 + p2) * 0.5
    //*************************************************************************************************************************
    private PointF calcolaPuntoMedio(float x1, float y1, float x2, float y2) {
        PointF PuntoMedio = new PointF(0, 0);

        try {
            PuntoMedio.x = (Math.max(x1, x2) - Math.min(x1, x2)) / 2 + Math.min(x1, x2);
            PuntoMedio.y = (Math.max(y1, y2) - Math.min(y1, y2)) / 2 + Math.min(y1, y2);
        } catch (Exception ex) {
        }

        return PuntoMedio;
    }

    //**************************************************************************************************************************
    // calcolo y dell'equazione implicita tra due punti x1,y1 - x2,y2
    // ritorna la y della formula by= ax +c
    //**************************************************************************************************************************
    private double coordToImplicitaY(double x1, double y1, double x2, double y2) {
        double y;

        y = x2 - x1;
        return y;
    }

    //**************************************************************************************************************************
    // calcolo x dell'equazione implicita tra due punti x1,y1 - x2,y2
    //ritorna la x della formula by= ax +c
    //**************************************************************************************************************************
    private double coordToImplicitaX(double x1, double y1, double x2, double y2) {
        double x;
        x = (y2 - y1);
        return x;
    }

    //**************************************************************************************************************************
    // calcolo c dell'equazione implicita tra due punti x1,y1 - x2,y2
    //ritorna la c della formula by= ax +c
    //**************************************************************************************************************************
    private double coordToImplicitaC(double x1, double y1, double x2, double y2) {
        double c;
        c = -x1 * (y2 - y1) + y1 * (x2 - x1);

        return c;
    }

    //**************************************************************************************************************************
    // linea perpendicolare ad una linea passante per un suo punto stesso
    // IN: x1 y1 è il punto. y x c la linea
    // OUT: linea in formato y x c
    //**************************************************************************************************************************
    private List<Double> perpAdUnPunto(double x1, double y1, double y, double x, double c) {
        List<Double> ret = new ArrayList<Double>();

        if (x == 0) {
            ret.add(0.0D);
            ret.add(-1.0D);
            ret.add(x1);
            return ret;
        } else if (y == 0) {
            ret.add(1.0D);
            ret.add(0.0D);
            ret.add(y1);
            return ret;
        } else {
            double m_Perp = -y / x;
            ret.add(1.0D);
            ret.add(m_Perp);
            ret.add(y1 - (x1 * m_Perp));
        }

        return ret;
    }


    private PointF calcolaIntersezioneDueRette(double b1, double a1, double c1, double b2, double a2, double c2) {
        double x, y;
        PointF p = new PointF();

        if (!retteParallele(b1, a1, c1, b2, a2, c2)) {

            if (b1 == 0) //la prima retta è verticale quindi quindi la x dell'intersezione sarà per forza a1(x) della stessa retta, mentre trovare la y,
            //la trovo dall'equazione della seconda retta
            {
                if (c1 == 0) {
                    x = a1;
                } else {
                    x = -c1 / a1;
                }

                p.x = (float) x;
                //b2y = a2x + c2 dove x l'ho trovato sopra
                y = (a2 * x + c2) / b2;
                //  y = (-a1 * a2 - c2) / b2;
                p.y = (float) y;
                return p;
            }

            if (b2 == 0) //la seconda retta è verticale quindi quindi la x dell'intersezione sarà per forza a2(x) della stessa retta, mentre trovare la y,
            //la trovo dall'equazione della prima retta
            {
                x = -(c2 / a2);
                p.x = (float) x;
                //b1y = a1x + c1 dove x l'ho trovato sopra
                y = (a1 * x + c1) / b1;
                //   y = (-a2 * a1 - c1) / b1;
                p.y = (float) y;
                return p;
            }

            if (a1 == 0) //la prima retta è orizzontale quindi quindi la y dell'intersezione sarà per forza b1(y) della stessa retta, mentre trovare la x,
            //la trovo dall'equazione della seconda retta
            {
                y = c1 / b1;
                p.y = (float) y;
                //b2y = a2x + c2 dove y l'ho trovato sopra
                //quindi
                x = (b2 * y - c2) / a2;
                //x = (-b2 * b1 - c2) / a2;
                p.x = (float) x;
                return p;
            }

            if (a2 == 0) //la seconda retta è orizzontale quindi quindi la y dell'intersezione sarà per forza b2(y) della stessa retta, mentre trovare la x,
            //la trovo dall'equazione della prima retta
            {
                y = c2 / b2;
                p.y = (float) y;
                //b1y = a1x + c1 dove y l'ho trovato sopra
                x = (b1 * y - c1) / a1;
                //  x = (-b1 * b2 - c1) / a1;
                p.x = (float) x;

                return p;
            }

            //se i casi sopra non erano veri allora le linee sono inclinate e calcolo l'intersezione

            x = (c1 * b2 - c2 * b1) / (a2 * b1 - a1 * b2);
            y = a2 * x + c2;

            p.x = (float) x;
            p.y = (float) y;
            return p;

        } else  //le rette sono parallele, non gestisco la cosa me resituico il punto 0,0.
        {
            p.x = 0;
            p.y = 0;
            return p;
        }
    }

    //**************************************************************************************************************************
    //controllo se le rette sono parallele
    // a1 · b2 = a2 · b1
    //**************************************************************************************************************************
    private static boolean retteParallele(double b1, double a1, double c1, double b2, double a2, double c2) {
        if (a1 * b2 == a2 * b1) return true;
        else return false;
    }

    //**********************************************************************************************************************
    // Find the points where the two circles intersect.
    //**********************************************************************************************************************
    public static ArrayList<PointF> FindCircleCircleIntersections(PointF center0, float radius0, PointF center1, float radius1) {
        // Find the distance between the centers.
        float dx = center0.x - center1.x;
        float dy = center0.y - center1.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // See how many solutions there are.
        if (dist > radius0 + radius1) {
            // No solutions, the circles are too far apart.
            return null;
        } else if (dist < Math.abs(radius0 - radius1)) {
            // No solutions, one circle contains the other.
            return null;
        } else if ((dist == 0) && (radius0 == radius1)) {
            // No solutions, the circles coincide.
            return null;
        } else {
            // Find a and h.
            double a = (radius0 * radius0 -
                    radius1 * radius1 + dist * dist) / (2 * dist);
            Double h = Math.sqrt(radius0 * radius0 - a * a);

            if(h.isNaN() || h == 0) {     //se l'arco ha raggio molto grande (quasi una linea) allora..
                h= (double) radius1;
            }

            // Find P2.
            double cx2 = center0.x + a * (center1.x - center0.x) / dist;
            double cy2 = center0.y + a * (center1.y - center0.y) / dist;

            // Get the points P3.
            PointF intersection1 = new PointF(
                    (float) (cx2 + h * (center1.y - center0.y) / dist),
                    (float) (cy2 - h * (center1.x - center0.x) / dist));
            PointF intersection2 = new PointF(
                    (float) (cx2 - h * (center1.y - center0.y) / dist),
                    (float) (cy2 + h * (center1.x - center0.x) / dist));

            ArrayList<PointF> ret = new ArrayList<PointF>();
            ret.add(intersection1);
            ret.add(intersection2);

            return ret;
        }
    }
    //**********************************************************************************************************************
    // lunghezza arco
    //**********************************************************************************************************************
    public double arcLenght(PointF start, PointF end, double radius, double sagitta)
    {
        double L = distance(start.x,start.y,end.x,end.y) /2 ;
        double o = 2*Math.asin(L/radius);
        double c = o*radius;
        if(sagitta>radius) c = 2*radius*Math.PI-c;
        return c;
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
    //  d = sqrt((x2 - x1)^2 + (y2 - y1)^2)
    //
    //    With that, the angle can be calculated as (r is radius of the circle):
    //    angle = 2asin(d/2r)
    //The problem with this solution is that it doesn't produce angles over 180 degrees.
    // When it goes over 180 degrees it starts giving the minor angle again, which leaves me unable to determine if the degrees
    // it is giving are clockwise or counter clockwise from the starting point. (Here's a demo showing how to calculate the central angle,
    // and it also runs into the same problem I encountered. It doesn't seem to know how to calculate an angle greater than 180 degrees.)
    //**************************************************************************************************************************
    public double calc_arc_angle(PointF start, PointF end,double radius)
    {



/*
        double circonferenza = 2*radius*Math.PI;
        double LungArco = arcLenght( start, end,  radius, sagitta);
        double angolo = 360 * (LungArco/circonferenza);
        return  angolo;
      */
            return 0;
    }

    public double Ang(PointF centro, PointF p1, PointF p2,double sagitta,double radius){


/*
       // static inline CGFloat angleBetweenLinesInRadians(CGPoint line1Start, CGPoint line1End, CGPoint line2Start, CGPoint line2End) {
            double angle1 = Math.atan2(centro.y-p1.y, centro.x-p1.x);
            double angle2 = Math.atan2(centro.y-p2.y, centro.x-p2.x);
            double result = (angle2-angle1) * 180 / 3.14;
            if (result<0) {
                result+=360;
            }
            return result;
*/
    return 0;

    }

    public double Ang(PointF centro, PointF p1, PointF p2){



        // static inline CGFloat angleBetweenLinesInRadians(CGPoint line1Start, CGPoint line1End, CGPoint line2Start, CGPoint line2End) {
        double angle1 = Math.atan2(centro.y-p1.y, centro.x-p1.x);
        double angle2 = Math.atan2(centro.y-p2.y, centro.x-p2.x);
        double result = (angle2-angle1) * 180 / 3.14;

        return result;


    }

    //*********************************************************************************************
    //  s=(s1,s2), m=(m1,m2), e=(e1,e2)
    //        //σ:=(m−s)∧(e−s)=     (m1−s1)(e2−s2)−(m2−s2)(e1−s1) .
    //*********************************************************************************************
    public String CalcDirez(PointF pStart, PointF pMiddle, PointF pEnd) {

        float ret = ((pMiddle.x-pStart.x)*(pEnd.y-pStart.y) - (pMiddle.y-pStart.y)*(pEnd.x-pStart.x));
        if(ret > 0) return "anti-clockwise";
        else return "clockwise";

    }
    //*********************************************************************************************
    // s = i-sqr(r*r - L*L)
    // ci sono due Sagitte, per capire quale prendere, guardo se l'arco è più lungo o no della lunghezza
    // della metà della circonferenza, se è più lunga prendo la sagitta più lunga
    //*********************************************************************************************

    public double Sagitta(PointF start, PointF end, float radius,float angolo){
        double mezzaCorda = distance(start.x,start.y,end.x,end.y) /2;
        double sagitta1 = (float) radius - Math.sqrt(radius * radius - mezzaCorda*mezzaCorda);
        double sagitta2 = (float) radius + Math.sqrt(radius * radius - mezzaCorda*mezzaCorda);
        if(angolo > 180){
            if(sagitta1 > sagitta2) return sagitta1;
            else return sagitta2;
        }
        else
        {
            if(sagitta1 < sagitta2) return sagitta1;
            else return sagitta2;
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
    // calcolo y dell'equazione implicita tra due punti x1,y1 - x2,y2
    // ritorna la y della formula by= ax +c
    //**************************************************************************************************************************
    public static double CoordToImplicitaY(double x1, double y1, double x2, double y2)
    {
        double y;

        y = x2 - x1;
        return y;
    }
    //**************************************************************************************************************************
    // calcolo x dell'equazione implicita tra due punti x1,y1 - x2,y2
    //ritorna la x della formula by= ax +c
    //**************************************************************************************************************************
    public static double CoordToImplicitaX(double x1, double y1, double x2, double y2)
    {
        double x;
        x = (y2 - y1);
        return x;
    }
    //**************************************************************************************************************************
    // calcolo c dell'equazione implicita tra due punti x1,y1 - x2,y2
    //ritorna la c della formula by= ax +c
    //**************************************************************************************************************************
    public static double CoordToImplicitaC(double x1, double y1, double x2, double y2)
    {
        double c;
        c = -x1 * (y2 - y1) + y1 * (x2 - x1);

        return c;
    }
    //**************************************************************************************************************************
    // calcolo linea parallela alla distanza passata
    //**************************************************************************************************************************

    public static ArrayList<Double> TrovaParallela(Double distanza, Double y, Double x, Double c)
    {
        ArrayList<Double> dt = new ArrayList<Double>();
        if (y == 0) //se la retta originale è verticale, non incontrerà mai l'asse Y quindi faccio questo calcolo
        {
            dt.add(0.0);
            dt.add(-1.0);
            double cc = -(c / x);
            if(x < 0) cc= cc + distanza;
            else cc = cc - distanza;
            dt.add(cc);



        }

        else
        {
            if (x == 0) //se la retta originale è orizzontale, non incontrerà mai l'asse X quindi faccio questo calcolo
            {
                dt.add(1.0);
                dt.add(0.0);
                double cc = c / y - distanza;
                dt.add(cc);



            }
            else
            {
                double yConXuguale0 = DistPuntoDaRetta(distanza, y, x, c);


                dt = ParallelaPasPuntoConX0(yConXuguale0, y, x, c);
            }

        }
        return dt;

    }
    //**************************************************************************************************************************
    // calcolo punto x,y distante da una retta nota ax + by +c = 0
    //**************************************************************************************************************************
    public static Double DistPuntoDaRetta(Double distanza, Double y, Double x, Double cc)
    {
        double a, b, c, yConXuguale0;
        a = x;
        b = -y;
        c = -cc;
        if (a == 0) { return yConXuguale0 = -cc/y + distanza; }
        if (b != 0) { yConXuguale0 = ((distanza * Math.sqrt(a * a + b * b)) - c) / b; }
        else
        {
            yConXuguale0 = ((distanza * Math.sqrt(a * a)) - c);
        }

        return yConXuguale0;


    }
    //**************************************************************************************************************************
    // trovo retta parallela passante per punto x,y
    // y - y1 = m (x - x1)  equazione retta passante parallela per un punto
    // x1,y1 = punto passante
    // x y = valori retta di partenza
    //**************************************************************************************************************************
    public static ArrayList<Double> ParallelaPasPuntoConX0(Double yConXuguale0, Double y, Double x, Double c)
    {
        Double m, cp;
        ArrayList<Double> dt = new ArrayList<Double>();
        if (y != 0)
        {
            m = x / y;   //inclinazione retta
            cp = -yConXuguale0;
            dt.add(1.0);
            dt.add(m);
            dt.add(cp);
        }
        else
        {
            cp = yConXuguale0;
            dt.add(0.0);
            dt.add(1.0);
            dt.add(cp);

        }


        return dt;


    }
    //**************************************************************************************************************************
    // linea perpendicolare ad una linea passante per un suo punto stesso
    // IN: x1 y1 è il punto. y x c la linea
    // OUT: linea in formato y x c
    //**************************************************************************************************************************
    public static ArrayList<Double> PerpendicolareAdUnPunto(Double x1, Double y1, Double y, Double x, Double c)
    {
        Double m_Perp;
        ArrayList<Double> ritorno = new ArrayList<Double>();
        if (x == 0)
        {
            ritorno.add(0.0);
            ritorno.add(-1.0);
            ritorno.add(x1);
            return ritorno;
        }
        if (y == 0)
        {
            ritorno.add(1.0);
            ritorno.add(0.0);
            ritorno.add(y1);
            return ritorno;
        }




        m_Perp = -y / x;

        ritorno.add(1.0);
        ritorno.add(m_Perp);
        ritorno.add(y1 - (x1 * m_Perp));

        return ritorno;
    }
//**************************************************************************************************************************
    // calcolo punto intesezione tra due rette
    // entrano due rette in forma by = ax + c
    // esce punto intersezione x,y
    //**************************************************************************************************************************

    public static PointF CalcolaIntersezioneDueRette(double b1, double a1, double c1, double b2, double a2, double c2)
    {
        double x, y;
        PointF p = new PointF();
        boolean Rette_Parallele = retteParallele(b1, a1, c1, b2, a2, c2);
        if (!Rette_Parallele)
        {

            if(b1==0) //la prima retta è verticale quindi quindi la x dell'intersezione sarà per forza a1(x) della stessa retta, mentre trovare la y,
            //la trovo dall'equazione della seconda retta
            {
                if (c1 == 0) { x = a1; } else { x = -c1 / a1; }

                p.x = (float)x;
                //b2y = a2x + c2 dove x l'ho trovato sopra
                y = (a2 * x + c2) / b2;
                //  y = (-a1 * a2 - c2) / b2;
                p.y = (float)y;
                return p;




            }
            if (b2 == 0) //la seconda retta è verticale quindi quindi la x dell'intersezione sarà per forza a2(x) della stessa retta, mentre trovare la y,
            //la trovo dall'equazione della prima retta
            {
                x = -(c2 / a2);
                p.x = (float)x;
                //b1y = a1x + c1 dove x l'ho trovato sopra
                y = (a1 * x + c1) / b1;
                //   y = (-a2 * a1 - c1) / b1;
                p.y = (float)y;
                return p;

            }
            if (a1 == 0) //la prima retta è orizzontale quindi quindi la y dell'intersezione sarà per forza b1(y) della stessa retta, mentre trovare la x,
            //la trovo dall'equazione della seconda retta
            {
                y = c1 / b1;
                p.y = (float)y;
                //b2y = a2x + c2 dove y l'ho trovato sopra
                //quindi
                x = (b2 * y - c2) / a2;
                //x = (-b2 * b1 - c2) / a2;
                p.x = (float)x;
                return p;


            }
            if (a2 == 0) //la seconda retta è orizzontale quindi quindi la y dell'intersezione sarà per forza b2(y) della stessa retta, mentre trovare la x,
            //la trovo dall'equazione della prima retta
            {
                y = c2 / b2;
                p.y = (float)y;
                //b1y = a1x + c1 dove y l'ho trovato sopra
                x = (b1 * y - c1) / a1;
                //  x = (-b1 * b2 - c1) / a1;
                p.x = (float)x;

                return p;

            }


            //se i casi sopra non erano veri allora le linee sono inclinate e calcolo l'intersezione


            x = (c1 * b2 - c2 * b1) / (a2 * b1 - a1 * b2);
            y = a2 * x + c2;

            p.x = (float)x;
            p.y = (float)y;
            return p;

        }
        else  //le rette sono parallele, non gestisco la cosa me resituico il punto 0,0.
        {

            p.x = 0;
            p.y = 0;
            return p;

        }


    }
    //**************************************************************************************************************************
    //IN: linea sopra o sotto della travetta, punto inizio, punto fine, lunghezza punto
    //OUT: in AppoggTravettadataGriewView metto i punti xy trovati
    //**************************************************************************************************************************
    public ArrayList<PointF> PopolaPuntiLineaTravetta(double by, double ax, double cc, double xx1, double yy1, double xx2, double yy2, double lunghezza_tratto, double NuovaLungPunto)
    {
        ArrayList<PointF> _Travetta = new ArrayList<PointF>();
        PointF Punto = new PointF();


        float Arr5_xx1 = Tools.roundTruncate005((float)xx1);
        float Arr5_yy1 = Tools.roundTruncate005((float)yy1);

        Punto = new PointF(Arr5_xx1, Arr5_yy1);
        _Travetta.add(Punto);


        int Punti = (int)(lunghezza_tratto / NuovaLungPunto);

        double a = ax;
        double b = by;
        double c = cc;
        double x1 = xx1;
        double y1 = yy1;
        double x2 = xx2;
        double y2 =yy2;
        if (b != 1 && b != 0)
        {
            a = a / b;
            c = c / b;
        }

        try
        {
            double A, B, C, xpos, xneg, ypos, yneg;
            if (a == 0)   //linea orizzontale
            {
                for (int i = 1; i <= Punti; i++)
                {
                    if ((x2 - x1) > 0)   //linea da sx verso dx
                    {

                        x1 = x1 + NuovaLungPunto;


                    }
                    else                //linea da dx verso sx
                    {
                        x1 = x1 - NuovaLungPunto;
                    }

                    Punto = new PointF(Tools.roundTruncate005((float)x1), Tools.roundTruncate005((float)y1));
                    _Travetta.add(Punto);


                }


                Punto = new PointF(Tools.roundTruncate005((float)x2), Tools.roundTruncate005((float)y2));
                _Travetta.add(Punto);




            }
            else
            {
                if (b == 0)
                {
                    for (int i = 1; i <= Punti; i++)
                    {
                        if ((y2 - y1) > 0)   //linea da basso verso alto
                        {

                            y1 = y1 + NuovaLungPunto;


                        }
                        else                //linea da alto verso basso
                        {
                            y1 = y1 - NuovaLungPunto;
                        }

                        Punto = new PointF(Tools.roundTruncate005((float)x1), Tools.roundTruncate005((float)y1));
                        _Travetta.add(Punto);


                    }


                    Punto = new PointF(Tools.roundTruncate005((float)x2), Tools.roundTruncate005((float)y2));
                    _Travetta.add(Punto);
                }
                else
                {

                    for (int i = 1; i <= Punti; i++)
                    {

                        A = 1 + a * a;
                        B = 2 * a * c - 2 * x1 - 2 * a * y1;
                        C = (x1 * x1) + (c * c) + (y1 * y1) - (2 * y1 * c) - (NuovaLungPunto * NuovaLungPunto);
                        xpos = (-B + Math.sqrt(B * B - 4 * A * C)) / 2 / A;
                        xneg = (-B - Math.sqrt(B * B - 4 * A * C)) / 2 / A;
                        //by = ax + c)
                        if ((x2 - x1) > 0)
                        {
                            ypos = a * xpos + c;
                            x1 = xpos;
                            y1 = ypos;


                            Punto = new PointF(Tools.roundTruncate005((float)xpos), ((float)ypos));
                            _Travetta.add(Punto);

                        }
                        else
                        {

                            yneg = a * xneg + c;
                            x1 = xneg;
                            y1 = yneg;


                            Punto = new PointF(Tools.roundTruncate005((float)xneg), ((float)yneg));
                            _Travetta.add(Punto);
                        }

                    }

                    Punto = new PointF(Tools.roundTruncate005((float)x2), Tools.roundTruncate005((float)y2));
                    _Travetta.add(Punto);
                }
            }

            return _Travetta;


        }

        catch (Exception e) {};
        return _Travetta;
    }
    //*************************************************************************************************************************
    // Effetto \/\/\/\/\ invece che ||||||||||
    //Per fare questo, prendo i punti medi tra due punti di un lato.
    //*************************************************************************************************************************
    public ArrayList<PointF> EffettoSega(ArrayList<PointF> PuntiIN)
    {
        ArrayList<PointF> PuntiOut = new ArrayList<PointF>();
        int CntPunti = PuntiIN.size();
        if (CntPunti > 2)
        {
            for (int i = 0; i < CntPunti - 1; i++)
            {
                PointF PuntoMedio = new PointF();
                float x1, x2, y1, y2;
                x1 = PuntiIN.get(i).x;
                y1 = PuntiIN.get(i).y;
                x2 = PuntiIN.get(i + 1).x;
                y2 = PuntiIN.get(i + 1).y;
                PuntoMedio = CalcolaPuntoMedio(x1, y1, x2, y2);
                PuntiOut.add(PuntoMedio);

            }
        }
        else
        {


            return null;
        }
        return PuntiOut;
    }

    //*************************************************************************************************************************
    // trovo punto medio di un segmento
    //*************************************************************************************************************************
    private PointF CalcolaPuntoMedio(float x1, float y1, float x2, float y2)
    {
        PointF PuntoMedio = new PointF();
        if (x1 != 0 & x2 != 0)              //caso estremo che potrebbe dare dare un crash
        { PuntoMedio.x = (x1 + x2) / 2; }
        else
        {
            PuntoMedio.x = 0;
        }
        if (y1 != 0 & y2 != 0)              //caso estremo che potrebbe dare dare un crash
        {
            PuntoMedio.y = (y1 + y2) / 2;
        }
        else
        {

            PuntoMedio.y = 0;
        }

        return PuntoMedio;
    }
    //*************************************************************************************************************************
    // ritorna il punto perpendicolare allo step selezionato sulla linea tra start e end dello zigzag
    //*************************************************************************************************************************
    public PointF PuntoSpezzaZigZag(PointF selectedEntityStartPoint, PointF selectedEntityEndPoint, JamPointStep activeStep) {
        PointF ret = new PointF();
        double y = coordToImplicitaY(selectedEntityStartPoint.x, selectedEntityStartPoint.y,selectedEntityEndPoint.x,selectedEntityEndPoint.y);
        double x = coordToImplicitaX(selectedEntityStartPoint.x, selectedEntityStartPoint.y,selectedEntityEndPoint.x,selectedEntityEndPoint.y);
        double c = coordToImplicitaC(selectedEntityStartPoint.x, selectedEntityStartPoint.y,selectedEntityEndPoint.x,selectedEntityEndPoint.y);
        List<Double> retta_perendicolare = perpAdUnPunto(activeStep.p.x, activeStep.p.y, y,  x,  c);

        if(retta_perendicolare.size() == 3)
        {
            ret =  calcolaIntersezioneDueRette( y,  x,  c, retta_perendicolare.get(0),retta_perendicolare.get(1),retta_perendicolare.get(2));
        }


        return ret;
    }
}

