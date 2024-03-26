package com.jam_int.jt316_jt318_m8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.jamint.ricette.CenterPointRadius;
import com.jamint.ricette.Element;
import com.jamint.ricette.ElementArc;
import com.jamint.ricette.ElementFeed;
import com.jamint.ricette.ElementLine;
import com.jamint.ricette.ElementZigZag;
import com.jamint.ricette.JamPointCode;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.TrigMatGeo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dynamic_view extends View {
    //*************************************************************************************
    //*************************************************************************************
    // Classe DynamicView
    //*************************************************************************************
    //*************************************************************************************

    int i = 0;
    Bitmap frame;
    Canvas frameDrawer;
    Paint paint;
    int width, height;
    private Matrix mMatrix = new Matrix();
    List<Element> List_entità = new ArrayList<>();
    ArrayList<PointF> List_punti = new ArrayList<>();
    ArrayList<PointF> List_punti_pts = new ArrayList<>();
    ArrayList<JamPointCode> List_code = new ArrayList<>();
    float zoom_canvas = 1F;
    float offsetX = 0;
    float offsetY = 0;
    float x_down = 0F;
    float y_down = 0F;
    float x_delta = 0F;
    float y_delta = 0F;
    boolean AutoZoomCenter = true;
    boolean StopRunnable = false;
    boolean OneShot = false;
    boolean PrimaVisualizzazione = true;
    int offsetCentraggioIniziale_X,offsetCentraggioIniziale_Y;
    boolean Inverti_x=true;
    Wrapper Info_disegno;

    MainActivity.CoordPosPinza CoordPosPinza;

 	float centerX = 0;
    float centerY = 0;
    float defaultcenterX = 0;
    float defaultcenterY = 0;


    // constructor
    public Dynamic_view(Context context, int width, int height, ArrayList<Element> List_entità, float zoom_canvas, ArrayList<PointF> List_punti, MainActivity.CoordPosPinza CoordPinza,boolean AutoZoomCenter) {
        super(context);

        this.width = width;
        this.height = height;

        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        frameDrawer = new Canvas(frame);
        this.List_entità = List_entità;
        this.zoom_canvas = zoom_canvas;
        this.CoordPosPinza = CoordPinza;
        this.AutoZoomCenter = AutoZoomCenter;


        paint = new Paint();


        setOnTouchListener(handleTouch);
    }





    // constructor
    public Dynamic_view(Context context, int width, int height, ArrayList elements, float zoom_canvas, MainActivity.CoordPosPinza coord_pinza,boolean AutoZoomCenter, int OffsetCentraggioIniziale_X,int OffsetCentraggioIniziale_Y,ArrayList<PointF> List_punti_pts,boolean inverti_X, float canvas_width, float canvas_height) {
        super(context);


        this.width = width;
        this.height = height;

        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        frameDrawer = new Canvas(frame);

        Inverti_x = inverti_X;


        List_punti = new ArrayList<>();
        this.List_entità = elements;
        this.zoom_canvas = zoom_canvas;
        this.CoordPosPinza = coord_pinza;
        this.AutoZoomCenter = AutoZoomCenter;
        this.offsetCentraggioIniziale_X = OffsetCentraggioIniziale_X;
        this.offsetCentraggioIniziale_Y = OffsetCentraggioIniziale_Y;
        this.List_punti_pts = List_punti_pts;


 		centerX = (canvas_width/2)/zoom_canvas;
        centerY = (canvas_height/2)/zoom_canvas;
        defaultcenterX = (canvas_width/2)/zoom_canvas;
        defaultcenterY = (canvas_height/2)/zoom_canvas;
        paint = new Paint();

        setOnTouchListener(handleTouch);

    }

    //*************************************************************************************
    // On Draw   (passa qui alla creazione e ad ogni invalidate();
    //*************************************************************************************
    @Override
    protected void onDraw(Canvas canvas) {


        Matrix matrix = mMatrix;
        matrix.reset();


        Disegna_punti(List_punti);
        Disegna_entità(List_entità);
        if(List_punti_pts != null && List_punti_pts.size() > 0)
            Disegna_pts(  List_punti_pts);

        try {
            double X = CoordPosPinza.XCoordPosPinza;
            double Y = CoordPosPinza.YCoordPosPinza;

            float Xf = (float) X;
            float Yf = (float) Y;

           	paint.setColor(Color.BLUE);
            frameDrawer.drawCircle(Xf, Yf, 2F, paint);

            if(Values.Machine_model.equals("JT318M_1000x800")) {
                frameDrawer.drawLine(-25, -30, 975, -30, paint);           //riquadro area macchina
                frameDrawer.drawLine(975, -30, 975, 770, paint);         //riquadro area macchina
                frameDrawer.drawLine(975, 770, -25, 770, paint);         //riquadro area macchina
                frameDrawer.drawLine(-25, 770, -25, -30, paint);           //riquadro area macchina
            }else
            {

                frameDrawer.drawLine(-4, -8, 970, -8, paint);           //riquadro area macchina
                frameDrawer.drawLine(970, -8, 970, 575, paint);         //riquadro area macchina
                frameDrawer.drawLine(970, 575, -4, 575, paint);         //riquadro area macchina
                frameDrawer.drawLine(-4, 575, -4, -8, paint);           //riquadro area macchina
            }





/*
            if(!AutoZoomCenter) {

                if ( !OneShot) {
                    OneShot = true;
                    frame.eraseColor(Color.TRANSPARENT);

                    frameDrawer.scale(1f, 1f, 0, 0);
                    frameDrawer.translate(90, 0);
                }
            }
*/

        } catch (Exception e) {
        }




        if(PrimaVisualizzazione && Inverti_x) {
       //     frameDrawer.scale(-1f, 1f, 0, 0);       //inverto asse X
            PrimaVisualizzazione = false;
        }

        canvas.drawBitmap(frame, matrix, null);

    }
    //*************************************************************************************
    //
    //*************************************************************************************
    private void Disegna_pts(ArrayList<PointF> list_punti_pts) {

        paint.setColor(Color.BLACK);
        for (int i = 0; i <list_punti_pts.size()-2; i++)
        {
            PointF p1 = list_punti_pts.get(i);
            PointF p2 = list_punti_pts.get(i+1);

            frameDrawer.drawLine(p1.x, p1.y, p2.x,p2.y, paint);

        }




    }


    //*************************************************************************************
    // Imposta ed esegue la traslazione della bitmap
    //*************************************************************************************
    public void setOffsetX_Y(Float newValueX, Float newValueY) {

        if (List_entità.size() > 0) {
            float ValX = (float) (newValueX / (Math.sqrt(zoom_canvas) * 2));
            float ValY = (float) (newValueY / (Math.sqrt(zoom_canvas) * 2));


            offsetX = offsetX + ValX;
            offsetY = offsetY + ValY;
            frameDrawer.translate(-ValX, +ValY);

            centerX += ValX;
            centerY += -ValY;


        }
    }
    //*************************************************************************************
    //
    //*************************************************************************************
    public void Center_Bitmap_Main(float zomm, int X_traslate, int Y_traslate) {
        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        frameDrawer = new Canvas(frame);
        paint = new Paint();

   //         frameDrawer.scale(-1F, 1F);
        frameDrawer.translate(-X_traslate, Y_traslate);
        frameDrawer.scale(zomm, zomm, 0, 0);

        centerX = defaultcenterX;
        centerY = defaultcenterY;
    }
    //*************************************************************************************
    // Disegno al centro
    //*************************************************************************************
    public void Center_Bitmap() {

        if (List_entità.size() > 0) {
            float ScaleFactor = 1f;
            frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            frameDrawer = new Canvas(frame);
            paint = new Paint();

            frameDrawer.scale(zoom_canvas, zoom_canvas, 0, 0);
            frameDrawer.translate(offsetCentraggioIniziale_X, offsetCentraggioIniziale_Y);

          //  frameDrawer.scale(-1F, 1F);
            /*
            if(AutoZoomCenter) {
                float minXScaleFactor = (433) / Info_disegno.DeltaX;  //-50 fa fare un zoom all leggermente pi piccolo del canvas
                float minYScaleFactor = (350) / Info_disegno.DeltaY;
                ScaleFactor = Math.min(minXScaleFactor, minYScaleFactor);
                frameDrawer.translate(-400, 10);        //qui la tasca è in alto a dx
            }else
            {

                ScaleFactor = zoom_canvas; //1f;
                frameDrawer.translate(offsetCentraggioIniziale_X, offsetCentraggioIniziale_Y);        //qui la tasca è in alto a dx

            }
            zoom_canvas = ScaleFactor - 0.1F; //riduco per lasciare margini
            frameDrawer.scale(zoom_canvas, zoom_canvas, 0, 0);
            setOffsetX_Y(x_delta, y_delta);
*/

        }

    }




    //*************************************************************************************

    public void Zoom(float valoreZoom) {

        if (List_entità.size() > 0) {
            //zoom

            zoom_canvas = zoom_canvas + valoreZoom;
           //s frameDrawer.scale(1f + valoreZoom, 1f + valoreZoom, Info_disegno.XCentro - offsetX, Info_disegno.YCentro + offsetY);
 			frameDrawer.scale(1f + valoreZoom, 1f + valoreZoom, centerX, centerY);


        }

    }

    //*************************************************************************************************
    // erase bitmap
    //*************************************************************************************************
    public void EraseBitmap() {
        frame.eraseColor(Color.TRANSPARENT);
        invalidate();

    }
    //*************************************************************************************************
    // erase bitmap
    //*************************************************************************************************
    public void AggiornaCanvas(boolean cancella) {
        if(cancella)
            frame.eraseColor(Color.TRANSPARENT);
        this.invalidate();

    }


    //*************************************************************************************************
    // Disegna_entità
    //*************************************************************************************************
    public void Disegna_entità(List<Element> list_entità) {


        if (list_entità.size() > 0) {
            List<Float> X_max_minList = new ArrayList<Float>(); //creo una list per poi trovare il valore min e max sull'asse X
            List<Float> Y_max_minList = new ArrayList<Float>(); //creo una list per poi trovare il valore min e max sull'asse Y
            for (int i = 0; i < list_entità.size(); i++) {
                try {
                    Element entita;

                    entita = list_entità.get(i);

                    X_max_minList.add(entita.pStart.x);
                    Y_max_minList.add(entita.pStart.y);
                    X_max_minList.add(entita.pEnd.x);
                    Y_max_minList.add(entita.pEnd.y);
                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setStyle(Paint.Style.STROKE);

                    if (entita instanceof ElementLine || entita instanceof ElementZigZag) {
                        if (entita.isSelected) {
                            paint.setColor(Color.YELLOW);
                        } else
                            paint.setColor(Color.RED);
                        frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                    }
                    if (entita instanceof ElementFeed) {

                        paint.setColor(Color.GREEN);
                        frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                    }
                    if (entita instanceof ElementArc) {


                        if (entita.isSelected) {
                            paint.setColor(Color.YELLOW);
                        } else
                            paint.setColor(Color.RED);



                        RectF rect = new RectF();
                        rect.left = ((ElementArc) entita).left;
                        rect.top = ((ElementArc) entita).top;
                        rect.right = ((ElementArc) entita).right;
                        rect.bottom = ((ElementArc) entita).bottom;
                        float sweep=0;
                        if(((ElementArc) entita).startAngle > ((ElementArc) entita).endAngle)
                            sweep = Math.abs(((ElementArc) entita).startAngle - ((ElementArc) entita).endAngle); //caso end = 155 start = 186
                        else sweep = (360- ((ElementArc) entita).endAngle) + ((ElementArc) entita).startAngle;  //nel caso limite dove end =340 start = 19


                        TrigMatGeo tmg = new TrigMatGeo();
                        CenterPointRadius cpr = tmg.getCenterPointRadius(entita.pStart, ((ElementArc) entita).pMiddle,entita.pEnd);
                        if(tmg.Sagitta(entita.pStart,entita.pEnd,cpr.radius,sweep) < 0.1){  //se la "freccia" dell'arco è piccola disegno una linea
                            paint.setColor(Color.YELLOW);
                            frameDrawer.drawLine(entita.pStart.x,entita.pStart.y,entita.pEnd.x,entita.pEnd.y,paint);
                        }
                        else {

                            frameDrawer.drawArc(rect, ((ElementArc) entita).endAngle, sweep, false, paint);
                            //Log.d("arco", "left: " + rect.left + "right: " + rect.right + "top: " + rect.top + "bottom: " + rect.bottom +
                            //        "endAngle: " + ((ElementArc) entita).endAngle + "sweep: " + sweep);
                        }

                    }

                } catch (Exception e) {

                }
            }

            Float Xmin = Collections.min(X_max_minList);
            Float Xmax = Collections.max(X_max_minList);
            Float Ymin = Collections.min(Y_max_minList);
            Float Ymax = Collections.max(Y_max_minList);

            Float DeltaX, DeltaY;
            if (Xmax > Xmin) {
                DeltaX = Xmax - Xmin;
            } else {
                DeltaX = Xmin - Xmax;
            }
            if (Ymax > Ymin) {
                DeltaY = Ymax - Ymin;
            } else {
                DeltaY = Ymin - Ymax;
            }


            Info_disegno = new Wrapper(Xmin, Xmax, Ymin, Ymax, (Xmin + Xmax) / 2, (Ymin + Ymax) / 2, DeltaX, DeltaY);



        }else
        {
            float Xmin = 0;
            float Xmax = 250;
            float Ymin = 0;
            float Ymax = 250;
            float DeltaX = Xmax;
            float DeltaY = Ymax;
            Info_disegno = new Wrapper(Xmin, Xmax, Ymin, Ymax, (Xmin + Xmax) / 2, (Ymin + Ymax) / 2, DeltaX, DeltaY);



        }

    }


    //*************************************************************************************************
    // Draw punti cucitura
    //*************************************************************************************************
    public void Disegna_punti(ArrayList<PointF> list_punti) {
        if(list_punti.size() == 0){
            Ricalcola_entità_canvas(List_entità);
        }

        if (list_punti.size() > 0) {
            for (int i = 0; i < list_punti.size(); i++) {
                try {
                    PointF punto = list_punti.get(i);

                    paint.setColor(Color.BLUE);
                    float radius = 1F;
                    frameDrawer.drawCircle(punto.x, punto.y, radius, paint);


                } catch (Exception e) {

                }
            }
        }
        if (List_code.size() > 0) {
            for (int i = 0; i < List_code.size(); i++) {
                try {
                    JamPointCode code =  List_code.get(i);

                    paint.setColor(Color.MAGENTA);
                    float radius = 1F;
                    paint.setStyle(Paint.Style.FILL);
                    frameDrawer.drawCircle(code.getStep().p.x, code.getStep().p.y, 2F, paint);



                } catch (Exception e) {

                }
            }
        }





    }

    //*************************************************************************************
    // View OnTouchListener
    //*************************************************************************************
    private OnTouchListener handleTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = (float) event.getX();
            float y = (float) event.getY();


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x_down = x;
                    y_down = y;
                    //Log.i("TAG", "touched down " + x +" " +y);
                    break;
                case MotionEvent.ACTION_MOVE:

                    x_delta = x - x_down;
                    x_down = x;


                    y_delta = y - y_down;
                    y_down = y;

                    if (x_delta > 1 || x_delta < -1) {
                        setOffsetX_Y(-x_delta, y_delta);
                        //        AggiornaSchermo = true;
                    }
                    AggiornaCanvas(true);
                    break;
                case MotionEvent.ACTION_UP:
                    AggiornaCanvas(true);


                    break;
            }

            return true;
        }
    };

    //**************************************************************************************************
    // Aggiorna canvas
    //**********************************************************************************************
    public void Ricalcola_entità_canvas(List<Element> elements)
    {
        paint.setColor(Color.LTGRAY);
        frameDrawer.drawRect(0,0,frame.getWidth(),frame.getHeight(),paint);

        List_entità = elements;
        List_punti.clear();
        List_code = new ArrayList<>();
        for (Element item : (ArrayList<Element>) elements) {

            for (JamPointStep step : item.steps) {
                try {


                    List_punti.add(new PointF(step.p.x,step.p.y));

                } catch (Exception e) {

                }
            }

            for( JamPointCode code : item.ricetta.codes){
                List_code.add(code);
            }

/*
            for (JamPointCode code : item.codes) {

                List_code.add(code);
            }
*/
        }
        //  AggiornaSchermo = true;
    }


    //*************************************************************************************
    //
    //*************************************************************************************
   /* private void Disegna_steps(List<Element> elements) {
        for (Element item : (ArrayList<Element>) elements) {
            for (JamPointStep step : item.steps) {
                try {
                    float radius = 1F;
                    if(step.isSelected()) {
                        paint.setColor(Color.WHITE);
                        radius = 1F;
                    }
                    else {
                        paint.setColor(Color.BLUE);
                    }
                    frameDrawer.drawCircle(step.p.x, step.p.y, radius, paint);


                } catch (Exception e) {

                }
            }
            for (JamPointCode code : item.codes) {

                List_code.add(code);
            }

        }
    }
    */
}


//*************************************************************************************************
// Wrapper Class per passare valori
//*************************************************************************************************
class Wrapper {
    public float Xmin,Xmax,Ymin,Ymax,XCentro,YCentro,DeltaX,DeltaY; // use this as ref


    public Wrapper(float Xmin, float Xmax,float Ymin,float Ymax, float XCentro, float YCentro, float DeltaX, float DeltaY) {
        this.Xmin = Xmin;
        this.Xmax = Xmax;
        this.Ymin = Ymin;
        this.Ymax = Ymax;
        this.XCentro = XCentro;
        this.YCentro = YCentro;
        this.DeltaX = DeltaX;
        this.DeltaY = DeltaY;

    }
}



