package com.jamint.ricette;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.jamint.recipes.JamPointCode.TipiCodici.SPEED_M8;
import static com.jamint.recipes.JamPointCode.TipiCodici.TENS_M8;

public class Ricetta
{
//1) su moveSteps che sposta elementofinale intero l'ultimo step continua ad avere le coordinate vecchie
//no) per spostamento<passo elemento spostato non fare i raccordi

    public String descrizioneRicetta = "";
    public int numeroRicetta = 1;

    public float pcX = 0.1F;
    public float pcY = 0.1F;
    public float elementLinkMinDistance = 1.0f;

    public int UdfVelLavRPM = 4000;
    public double UdfPuntiVelIni = 0.003d;
    public int UdfVelIniRPM = 300;
    public double UdfPuntiVelRall = 0.003d;
    public int UdfVelRallRPM = 300;
    public int Udf_FeedG0 = 500;

    public int Udf_ValTensioneT1 = 0;
    public int Udf_20 = 0;
    public int Udf_ValElettrocalamitaSopra = 0;
    public int Udf_ValElettrocalamitaSotto = 0;
    public int Udf_23 = 0;
    public int Udf_24 = 0;
    public int Udf_25 = 0;
    public int Udf_26 = 0;
    public int Udf_27 = 0;
    public int Udf_28 = 0;
    public int Udf_29 = 0;
    public int Udf_30 = 0;
    public int Udf_SequenzaPiegatore_chiusura = 1234;
    public int Udf_SequenzaPiegatore_apetura = 4321;

    public List<Element> elements = new ArrayList<>();
    public List<JamPointCode> codes = new ArrayList<>();

    private static final String RICETTA_TAG = "Ricetta";
    private static final String ELEMENT_LINE_TAG = "Line";
    private static final String ELEMENT_ARC_TAG = "Arc";
    private static final String ELEMENT_ZIGZAG_TAG = "ZigZag";
    private static final String ELEMENT_FEED_TAG = "Feed";
    private static final String STEP_TAG = "Step";
    private static final String CODE_TAG = "Code";

    private static final String DESCRIZIONE_RICETTA_ATTRIBUTE = "descrizione";
    private static final String NUMERO_RICETTA_ATTRIBUTE = "numero";
    private static final String PCX_RICETTA_ATTRIBUTE = "pcx";
    private static final String PCY_RICETTA_ATTRIBUTE = "pcy";
    private static final String VEL_LAV_RPM_ATTRIBUTE  = "UdfVelLavRPM";
    private static final String PUNTI_VEL_INI_ATTRIBUTE  = "UdfPuntiVelIni";
    private static final String VEL_INI_ATTRIBUTE  = "UdfVelIniRPM";
    private static final String PUNTI_VEL_RALL_ATTRIBUTE  = "UdfPuntiVelRall";
    private static final String VEL_RALL_ATTRIBUTE  = "UdfVelRallRPM";
    private static final String FEEDG0_ATTRIBUTE  = "Udf_FeedG0";
    private static final String VAL_TENSIONE_T1_ATTRIBUTE  = "Udf_ValTensioneT1";
    private static final String UDF20  = "Udf_20";
    private static final String VAL_ELETTROCALAMITA_SOPRA_ATTRIBUTE  = "Udf_ValElettrocalamitaSopra";
    private static final String VAL_ELETTROCALAMITA_SOTTO_ATTRIBUTE  = "Udf_ValElettrocalamitaSotto";
    private static final String UDF23  = "Udf_23";
    private static final String UDF24  = "Udf_24";
    private static final String UDF25  = "Udf_25";
    private static final String UDF26  = "Udf_26";
    private static final String UDF27  = "Udf_27";
    private static final String UDF28  = "Udf_28";
    private static final String UDF29  = "Udf_29";
    private static final String UDF30  = "Udf_30";
    private static final String SEQUENZAPIEGATORE_CHIUSURA_ATTRIBUTE  = "Udf_SequenzaPiegatore_chiusura";
    private static final String SEQUENZAPIEGATORE_APERTURA_ATTRIBUTE  = "Udf_SequenzaPiegatore_apetura";




    private static final String X_START_ELEMENT_ATTRIBUTE = "x-start";
    private static final String Y_START_ELEMENT_ATTRIBUTE = "y-start";
    private static final String X_END_ELEMENT_ATTRIBUTE = "x-end";
    private static final String Y_END_ELEMENT_ATTRIBUTE = "y-end";
    private static final String PASSO_ATTRIBUTE = "passo";
    private static final String ENTITY_ATTRIBUTE = "entity";

    private static final String ALTEZZA_ZIGZAG_ATTRIBUTE = "altezza";

    private static final String X_MIDDLE_ARC_ATTRIBUTE = "x-middle";
    private static final String Y_MIDDLE_ARC_ATTRIBUTE = "y-middle";
    private static final String LEFT_ARC_ATTRIBUTE = "left";
    private static final String TOP_ARC_ATTRIBUTE = "top";
    private static final String RIGHT_ARC_ATTRIBUTE = "right";
    private static final String BOTTOM_ARC_ATTRIBUTE = "bottom";
    private static final String START_ARC_ATTRIBUTE = "start";
    private static final String END_ARC_ATTRIBUTE = "end";

    private static final String X_STEP_ATTRIBUTE = "x";
    private static final String Y_STEP_ATTRIBUTE = "y";

    private static final String TIPO_CODE_ATTRIBUTE = "code-type";
    private static final String VALORE_CODE_ATTRIBUTE = "code-value";
    private static final String ELEMENTINDEX_CODE_ATTRIBUTE = "elementIndex";
    private static final String STEPINDEX_CODE_ATTRIBUTE = "stepIndex";

    public static final float DEFAULT_PASSO = 3.0F;
    public static final float DEFAULT_PASSO_ZIGZAG = 1.0F;
    public static final float DEFAULT_ALTEZZA = 3.0F;
    private static final float MIN_FEED_DISTANCE = 12.0f;

    private File file;
    private Paint paint = null;
    private PointF pEndLast = new PointF(); //MEMORIZZA IL PUNTO DI ARRIVO DELL'OPERAZIONE DI DISEGNO PRECEDENTE

    //VARIABILI PER LA SELEZIONE DEGLI STEP DI RICETTA
    private int activeStepIndex = -1;
    public int indexSelectionStepStart = -1;
    public int indexSelectionStepEnd = -1;

    //VARIABILI PER LA SELEZIONE DELLE ENTITIES DI RICETTA
    private String selectedEntityCode = "";
    private LinkedHashMap<String, List<Element>> entities = new LinkedHashMap<>();

    private Ricetta undo = null;
    private Ricetta redo = null;


    public Ricetta()
    {
        //IMPOSTA UN PERCORSO E NOME FILE PREDEFINITI PER LA RICETTA
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/ricette");
        dir.mkdirs();
        file = new File(dir, "ricetta1.xml");
        paint = new Paint();
    }


    public void repair()
    {
        if (elements.size()==0) return;

        saveUndo();

        Iterator<Element> itr = elements.iterator();
        PointF pPrev = new PointF(pcX,pcY);

        while(itr.hasNext())
        {
            Element element = itr.next();

            int minSteps = element instanceof ElementFeed ? 1 : element instanceof ElementLine ? 2 :  3;

            //1) APPROSSIMA TUTTI I VALORI x/y START/END E STEPS A 0.05
            element.pStart.x = Tools.roundTruncate005(element.pStart.x);
            element.pStart.y = Tools.roundTruncate005(element.pStart.y);

            element.pEnd.x = Tools.roundTruncate005(element.pEnd.x);
            element.pEnd.y = Tools.roundTruncate005(element.pEnd.y);

            if (element instanceof ElementArc)
            {
                ((ElementArc)element).pMiddle.x = Tools.roundTruncate005(((ElementArc)element).pMiddle.x);
                ((ElementArc)element).pMiddle.y = Tools.roundTruncate005(((ElementArc)element).pMiddle.y);
            }

            for(JamPointStep step: element.steps)
            {
                step.p.x = Tools.roundTruncate005(step.p.x);
                step.p.y = Tools.roundTruncate005(step.p.y);
            }

            //1) CONTROLLO CONTINUITA' ELEMENTI
//            if (!(element instanceof ElementFeed))
//                if ((Math.abs(pPrev.x-element.pStart.x)>0.05f) || (Math.abs(pPrev.y-element.pStart.y)>0.05f))
//                {
//                    element.pStart.x = pPrev.x;
//                    element.pStart.y = pPrev.y;
//                    element.createSteps();
//                }

            if (element.steps.size()>=minSteps)
            {
                //2) CONTROLLO COORDINATE PRIMO STEP
                if (element instanceof ElementLine || element instanceof ElementArc)
                {
                    JamPointStep step = element.steps.get(0);

                    if ((Math.abs(step.p.x-element.pStart.x)>0.05f) || (Math.abs(step.p.y-element.pStart.y)>0.05f))
                        step.p = new PointF(element.pStart.x, element.pStart.y);
                }

                //3) CONTROLLO COORDINATE ULTIMO STEP
                if (element instanceof ElementLine || element instanceof ElementArc || element instanceof ElementFeed)
                {
                    JamPointStep step = element.steps.get(element.steps.size()-1);

                    if ((Math.abs(step.p.x-element.pEnd.x)>0.05f) || (Math.abs(step.p.y-element.pEnd.y)>0.05f))
                        step.p = new PointF(element.pEnd.x, element.pEnd.y);
                }
            }
            else
            {
                //4) NUMERO STEPS MINIMO NON VALIDO
                element.createSteps();

                if (element.steps.size()<minSteps)
                    itr.remove();
            }

            pPrev.x = element.pEnd.x;
            pPrev.y = element.pEnd.y;
        }
    }

    public void moveElements(int startElementIndex, int endElementIndex, float dx, float dy)
    {
        if (startElementIndex > endElementIndex || startElementIndex<0 || endElementIndex>= elements.size())
            return;

        saveUndo();

        if (startElementIndex == endElementIndex)
            moveElement(startElementIndex, dx, dy); //UN SOLO ELEMENTO, LO MUOVE
        else
            moveElements(startElementIndex, endElementIndex, dx, dy, 0, 0);
    }

    public boolean isNextElementFeed()
    {
        JamPointStep step = getActiveStep();

        if (isLastStepOfElement(step))
            if (!isLastElement(step.element))
            {
                Element element = getNextElement(step.element);
                return element instanceof ElementFeed;
            }

        return false;
    }

    public boolean isPreviousElementFeed()
    {
        JamPointStep step = getActiveStep();

        if (step!=null && step.element instanceof ElementFeed)
            return true;

        if (isFirstStepOfElement(step))
            if (!isFirstElement(step.element))
            {
                Element element = getPrevElement(step.element);
                return element instanceof ElementFeed;
            }

        return false;
    }

    //RISISTEMAZIONE DEI CODICI ESISTENTI - RESTITUISCE QUELLI CHE PUNTANO A STEP/ELEMENT NON PIU' VALIDI
    public List<JamPointCode> checkInvalidCodes(boolean tryAdjustInvalidCodes)
    {
        //saveUndo();
        List<JamPointCode> ret = new ArrayList<>();

        //1° CICLO RISISTEMA I CODICI CHE PUNTANO AD UNO STEP NON PIU' VALIDO
        for (JamPointCode code: codes)
        {
            JamPointStep step = code.getStep();

            if (step==null)
                if (!tryAdjustInvalidCodes || !code.adjustStep())
                    ret.add(code);
        }

        //2° CICLO AGGIUSTA I CODICI PUNTANO AD UNO STEP NON "VISITABILE"
        for (JamPointCode code: codes)
        {
            JamPointStep step = code.getStep();
            code.step = getNextActivableStep(step);
        }

        return ret;
    }

    public void clearInvalidCodes()
    {
        List<JamPointCode> codes2 = new ArrayList<>(codes);

        for (JamPointCode code: codes2)
            if (code.getStep() == null)
                codes.remove(code);
    }

    //********************  METODI DI GESTIONE STEPS ********************

    //RESTITUISCE LO STEP PASSATO SE E' ATTIVABILE O IL SUCCESSIVO IN CASO CONTRARIO
    private JamPointStep getNextActivableStep(JamPointStep step)
    {
        if (step!=null)
            if (isLastStepOfElement(step))
            {
                JamPointStep relatedStep = getRelatedStep(getStepIndex(step)); //SE IL CODICE E' FINITO SU UNO STEP FINALE DI GIUNTURA PASSA ALLO STEP SUCCESSIVO

                if (relatedStep!=null)
                    return relatedStep;
            }

        return step;
    }

    public JamPointStep goToNearestStep(PointF pNear)
    {
        return goToNearestStep(pNear, Double.POSITIVE_INFINITY);
    }

    public JamPointStep goToNearestStep(PointF pNear, double maxDistance)
    {
        JamPointStep nearStep = null;
        double minDist = maxDistance;

        for(Element element : elements )
            for (JamPointStep step : element.steps)
            {
                double dist = Tools.getDistance(step.p, pNear);

                if (dist<=minDist) //= SIGNIFICA CHE IN CASO DI STEP DI GIUNTURA SI PRENDE IL PRIMO DELL'ELEMENTO SUCCESSIVO
                {
                    nearStep = step;
                    minDist = dist;
                }
            }

        if (nearStep!=null)
        {
            nearStep = getNextActivableStep(nearStep);
            activeStepIndex = getStepIndex(nearStep);
            selectEntity(false, false);
            selectedEntityCode = nearStep.element.entity;
            selectEntity(true, false);
        }

        return nearStep;
    }


    public JamPointStep getActiveStep()
    {
        return getStep(activeStepIndex);
    }

    public int getActiveStepIndex()
    {
        return activeStepIndex;
    }

    public void clearActiveStep()
    {
        activeStepIndex = -1;
    }

    public int getStepsCount()
    {
        int ret = 0;

        for(Element element: elements)
            ret += element.steps.size();

        return ret;
    }

    //RESTITUISCE L'INDICE GLOBALE DELLO STEP SPECIFICATO
    public int getStepIndex(JamPointStep stepObject)
    {
        int index = -1;

        for (Element element : elements)
            for (JamPointStep step : element.steps)
            {
                index++;
                if (step == stepObject) //STEPINDEX E' UN INDICE GLOBALE, NON RIFERITO ALL'ELEMENTO
                    return index;
            }
/*
        //ALTERNATIVA CON UN SOLO CICLO!
        for (Element element : elements)
        {
            int stepIndexElement = element.steps.indexOf(stepObject);

            if (stepIndexElement == -1)
                index += element.steps.size() -1;
            else
                return index + stepIndexElement;
        }
*/
        return -1;
    }

    //SI SPOSTA SULLO STEP PRECEDENTE A QUELLO ATTUALE. OMETTE L'ULTIMO STEP DI (EVENTUALE) ELEMENTO PRECEDENTE SE COINCIDE CON IL PRIMO STEP DI ELEMENTO SUCCESSIVO
    public JamPointStep goToPreviousStep()
    {
        JamPointStep prev = null;
        JamPointStep step = null;

        if (activeStepIndex==-1)
        {
            prev = getLastStep();

            if (prev!=null)
                activeStepIndex = getStepIndex(prev);
        }
        else if (activeStepIndex==0)
        {
            activeStepIndex=-1;
        }
        else
        {
            prev = getStep(activeStepIndex-1);
            step = getStep(activeStepIndex);
        }

        if (step!=null && prev!=null)
        {
            activeStepIndex--;

            if (isLastStepOfElement(prev) && !(step.element instanceof  ElementFeed))
                if (isJunctionPoints(prev.element.pEnd, step.element.pStart))
                {
                    if (activeStepIndex==0)
                    {
                        prev = null;
                        activeStepIndex = -1;
                    }
                    else
                    {
                        prev = getStep(activeStepIndex-1);

                        if (prev!=null)
                            activeStepIndex--;
                    }
                }
        }

        if (prev!=null && !prev.element.entity.equals(selectedEntityCode))
        {
            selectEntity(false, false);
            selectedEntityCode = prev.element.entity;
            selectEntity(true, false);
        }

        return prev;
    }

    //SI SPOSTA SULLO STEP SUCCESSIVO A QUELLO ATTUALE. OMETTE L'ULTIMO STEP DI ELEMENTO SE IL PRIMO STEP DI (EVENTUALE) ELEMENTO SUCCESSIVO COINCIDE
    public JamPointStep goToNextStep()
    {
        JamPointStep next = null;

        if (activeStepIndex==-1)
        {
            next = getFirstStep(); //E' TERMINATA LA RICETTA: PRENDE IL PRIMO STEP
        }
        else
        {
            next = getStep(activeStepIndex+1); //OTTIENE LO STEP SUCCESSIVO

            if (next==null)
                activeStepIndex = -1;
        }

        if (next!=null)
        {
            activeStepIndex++; //HA TROVATO UNO STEP SUCCESSIVO, ORA INCREMENTA INDEX

            if (isLastStepOfElement(next) && !isLastElement(next.element)) //CONTROLLA SE STEP SUCCESSIVO E' ULTIMO DI ELEMENTO. SULL'ULTIMO STEP DI RICETTA CI SI FERMA SEMPRE!!!
            {
                JamPointStep nextStep = null;

                if (isLastElement(next.element))
                {
                    nextStep = getFirstStep();

                    if (nextStep!=null)
                        activeStepIndex = -1;
                }
                else
                    nextStep = getStep(activeStepIndex+1);

                if (nextStep!=null && !(nextStep.element instanceof ElementFeed) && isJunctionPoints(next.element.pEnd, nextStep.element.pStart))
                {
                    next = getStep(activeStepIndex+1); //STEP SOVRAPPOSTI: PRENDE IL PRIMO DI ELEMENTO SUCCESSIVO

                    if (next!=null)
                        activeStepIndex++;
                }
            }

            if (next!=null && !next.element.entity.equals(selectedEntityCode)) //SE NECESSARIO AGGIORNA LA SELEZIONE ENTITY
            {
                selectEntity(false, false);
                selectedEntityCode = next.element.entity;
                selectEntity(true, false);
            }

        }

        return next;
    }

    public JamPointStep goToLastStep()
    {
        JamPointStep last = getLastStep();

        if (last!=null)
            activeStepIndex = getStepIndex(last);

        return last;
    }

    //METODO CHE SETTA INDEX DI INIZIO SELEZIONE STEPS ALLO STEP ATTUALMENTE SELEZIONATO
    public JamPointStep selectionStepStart()
    {
        JamPointStep ret = getStep(activeStepIndex);

        if (ret!=null)
            indexSelectionStepStart = activeStepIndex;

        indexSelectionStepEnd = -1;
        setSelectedSteps(false);

        if (ret!=null)
            ret.isSelected = true;
        
        return ret;
    }

    //METODO CHE SETTA INDEX DI FINE SELEZIONE STEPS ALLO STEP ATTUALMENTE SELEZIONATO
    public JamPointStep selectionStepEnd()
    {
        JamPointStep ret = null;

        if (activeStepIndex>indexSelectionStepStart)
        {
            ret = getStep(activeStepIndex);

            if (ret!=null)
            {
                indexSelectionStepEnd = activeStepIndex;

                //SE LO STEP DI FINE SELEZIONE E' L'INIZIALE DI UN ELEMENTO CERCA DI PRENDERE LO STEP FINALE DELL'ELEMENTO PRECEDENTE (SE ESISTE)
                if (isFirstStepOfElement(ret) && !isFirstElement(ret.element))
                {
                    JamPointStep prevStep = getStep(activeStepIndex-1);

                    if (prevStep!=null)
                    {
                        if (ret.p.x == prevStep.p.x && ret.p.y == prevStep.p.y)
                            indexSelectionStepEnd = activeStepIndex-1;
                    }
                }
            }
        }

        setSelectedSteps(ret!=null);

        return ret;
    }

    //METODO CHE ANNULLA INDEX DI INIZIO E FINE SELEZIONE DI STEPS
    public void selectionStepClear()
    {
        indexSelectionStepStart = -1;
        indexSelectionStepEnd = -1;
        setSelectedSteps(false);
    }

    private void setSelectedSteps(boolean select)
    {
        int index = -1;

        for (Element element : elements)
            for (JamPointStep step : element.steps)
            {
                index++;
                step.isSelected = select && index>=indexSelectionStepStart && index <=indexSelectionStepEnd;
            }
    }

    //MUOVE UN SINGOLO STEP E RACCORDA CREANDO NUOVE LINEE
    public boolean moveActiveStep(float dx, float dy)
    {
        saveUndo();
        return moveStep(activeStepIndex, dx, dy);
    }

    //ELIMINA LO STEP CORRENTE
    public boolean deleteActiveStep()
    {
        saveUndo();

        int startIndex = activeStepIndex;
        int endIndex = activeStepIndex;

        JamPointStep relatedStep = getRelatedStep(activeStepIndex);
        if (relatedStep!=null)
        {
            int relatedIndex = getStepIndex(relatedStep);

            if (relatedIndex<activeStepIndex)
                startIndex = relatedIndex;
            else if (relatedIndex>activeStepIndex)
                endIndex = relatedIndex;
        }

        boolean ret = deleteSteps(startIndex, endIndex);

        if (ret)
        {
            activeStepIndex = startIndex;
            goToPreviousStep();
        }

        return ret;
    }

    //ESEGUE LO STRETCH SULLO STEP ATTIVO CON STEP START/END COME MARGINE
    public boolean stretchActiveStep(float dx, float dy)
    {
        if (indexSelectionStepStart<0 || indexSelectionStepEnd<0) return false;
        if (activeStepIndex - indexSelectionStepStart<2 || indexSelectionStepEnd - activeStepIndex <2)return false; //NON PERMESSO: CHIAMARE moveActiveStep

        JamPointStep step = getStep(activeStepIndex);
        if (step == null) return false;

        JamPointStep step1 = getStep(indexSelectionStepStart);
        if (step1 == null) return false;

        JamPointStep step2 = getStep(indexSelectionStepEnd);
        if (step2 == null) return false;

        saveUndo();

        int indexElement2 = elements.indexOf(step2.element);
        int num2 = splitElementAtStep(step2, 0); //PRIMO SPLIT, SU ULTIMO PUNTO

        if (num2==-1)
            num2=0;

        indexElement2 += num2;

        //daniele 14/02/2020
        if(num2 == 0)
            indexElement2++;
        //

        step1 = getStep(indexSelectionStepStart); //LO RILEGGE
        if (step1 == null) return false;

        int indexElement1 = elements.indexOf(step1.element);
        int num1 = splitElementAtStep(step1, 0); //SECONDO SPLIT, SU PRIMO PUNTO

        if (num1==-1)
            num1=0;

        indexElement1 += num1; //AGGIUSTA INDICE ELEMENTI DA ELIMINARE (DAL)
        indexElement2 += num1; //AGGIUSTA INDICE ELEMENTI DA ELIMINARE (FINO AL)

        for(int index = indexElement1; index<indexElement2; index++)
            elements.remove(indexElement1);

        Element elementLink1 = new ElementLine();
        insertElement(elementLink1, step1.p, new PointF(step.p.x + dx, step.p.y + dy) , step1.element, indexElement1);

        Element elementLink2 = new ElementLine();
        insertElement(elementLink2, new PointF(step.p.x + dx, step.p.y + dy) , step2.p, step2.element, indexElement1+1);

        if (elementLink2.steps.size()>0)
        {
            activeStepIndex = getStepIndex(elementLink2.steps.get(0));
            return true;
        }

        return false;
    }

    //MUOVE TUTTI GLI STEP CONTIGUI COMPRESI NELLA SELEZIONE START/END E RACCORDA CREANDO NUOVE LINEE
    public boolean moveSelectedSteps(float dx, float dy)
    {
        saveUndo();
        return moveSteps(indexSelectionStepStart, indexSelectionStepEnd, dx, dy);
    }

    public boolean deleteSelectedSteps()
    {
        saveUndo();
        boolean ret = deleteSteps(indexSelectionStepStart, indexSelectionStepEnd);

        if (ret && activeStepIndex >= indexSelectionStepStart)
        {
            activeStepIndex = indexSelectionStepStart;
            //goToPreviousStep();
        }

        return ret;
    }

    //CREA UNA LINEA TRA DUE STEPS SPECIFICATI ELIMINANDO IL PRECEDENTE CONTENUTO
    public boolean joinSelectedStepsByLine()
    {
        saveUndo();
        return joinSteps(indexSelectionStepStart, indexSelectionStepEnd, -1);
    }


    //daniele 26/06/2020 (mi permette di raddrizzare passando idx dei tre punti)
    public boolean joinSelectedStepsByArc(int idxStepStart, int idxStepMiddle, int idxStepEnd)
    {
       indexSelectionStepStart = idxStepStart;
        activeStepIndex = idxStepMiddle;
       indexSelectionStepEnd = idxStepEnd;
       boolean ret = joinSelectedStepsByArc();
       return  ret;
    }

    //CREA UN ARCO TRA DUE STEPS SPECIFICATI (IL middlePoint E' LO STEP ATTIVO!) ELIMINANDO IL PRECEDENTE CONTENUTO
    public boolean joinSelectedStepsByArc()
    {
        saveUndo();
        return joinSteps(indexSelectionStepStart, indexSelectionStepEnd, activeStepIndex);
    }

    public boolean isActiveStepIntersection()
    {
        return (isStepIntersection(activeStepIndex));
    }

    private boolean isStepIntersection(int stepIndex)
    {
        return getRelatedStep(stepIndex)!=null;
    }

    //RITORNA L'EVENTUALE STEP SOVRAPPOSTO (DI GIUNTURA) CON LO STEP RICHIESTO, NULL SE NON ESISTE
    private JamPointStep getRelatedStep(int stepIndex)
    {
        JamPointStep step = getStep(stepIndex);

        if (step != null)
        {
            Element element = step.element;

            if (isFirstStepOfElement(step) && !isFirstElement(element) && !(element instanceof ElementFeed))
            {
                Element elementBefore = getPrevElement(element);

                if (elementBefore!=null)
                    if (isJunctionPoints(elementBefore.pEnd, element.pStart))
                        if (elementBefore.steps.size()!=0)
                            return elementBefore.steps.get(elementBefore.steps.size()-1);
            }

            if (isLastStepOfElement(step) && !isLastElement(element))
            {
                Element elementAfter = getNextElement(element);

                if (elementAfter!=null && !(elementAfter instanceof ElementFeed))
                    if (isJunctionPoints(element.pEnd, elementAfter.pStart))
                        if (elementAfter.steps.size()!=0 && !(elementAfter instanceof ElementFeed))
                            return elementAfter.steps.get(0);
            }
        }

        return null;
    }

    //VERIFICA SE 2 PUNTI COINCIDONO COME COORDINATE ENTRO UNA TOLLERANZA DI 0.05
    private boolean isJunctionPoints(PointF p1, PointF p2)
    {
        if (p1!=null && p2!=null) //CONTROLLO SE STEP DI GIUNTURA (2 SOVRAPPOSTI, IN CASO PASSA AL SUCCESSIVO)
            return (Math.abs(p1.x - p2.x)<0.05 && Math.abs(p1.y - p2.y)<0.05);

        return false;
    }

    //NUOVI METODI + EFFICIENTI PER goTo... DA TERMINARE!!!
    private JamPointStep getNextStep(JamPointStep step)
    {
        JamPointStep ret = null;

        if (step!=null && step.element!=null)
        {
            int elementStepIndex = step.element.steps.indexOf(step);

            if (elementStepIndex == step.element.steps.size()-1)
            {
                int elementIndex = elements.indexOf(step.element);

                if (elementIndex == elements.size()-1)
                    elementIndex = 0;
                else
                    elementIndex++;

                if (elements.get(elementIndex).steps.size()!=0)
                    ret = elements.get(elementIndex).steps.get(0);
            }
            else
                ret = step.element.steps.get(elementStepIndex+1);
        }

        return ret;
    }


    //RESTITUISCE LO STEP RELATIVO ALL'INDICE GLOBALE SPECIFICATO
    private JamPointStep getStep(int stepIndex)
    {
        int index = 0;

        for(Element element:elements)
            for (JamPointStep step : element.steps)
                if (index++ == stepIndex) //STEPINDEX E' UN INDICE GLOBALE, NON RIFERITO ALL'ELEMENTO
                    return step;

        return null;
    }

    private boolean moveStep(int stepIndex, float dx, float dy)
    {
        JamPointStep step = getStep(stepIndex);

        if (step==null)
            return false;

        JamPointStep relatedStep = getRelatedStep(stepIndex);

        if (relatedStep!=null && !(relatedStep.element instanceof ElementFeed) && !(relatedStep.element instanceof ElementZigZag)) //LO STEP E' DI GIUNTURA, ELIMINA IL RELATED STEP
        {
            int relatedStepIndex = getStepIndex(relatedStep);
            if (!deleteSteps(relatedStepIndex, relatedStepIndex))
                return false;

            if (relatedStepIndex<stepIndex)
                stepIndex = getStepIndex(step);
        }

        Element element = step.element;
        int stepIndexInElement = element.steps.indexOf(step);

        if (element instanceof ElementZigZag) //MOVE SINGOLO STEP SU ZIGZAG: ESEGUE ED ESCE, NON SPEZZA ELEMENTO!
        {
            step.p.x += dx;
            step.p.y += dy;
            return true;
        }

        if (element instanceof ElementFeed)
        {
            JamPointStep nextStep = getStepAfter(stepIndex);

            if (nextStep==null || nextStep.element instanceof ElementFeed)
            {
                element.pEnd =  new PointF(step.p.x + dx, step.p.y + dy);
                element.createSteps();

                if (nextStep!=null)
                {
                    nextStep.element.pStart =  new PointF(element.pEnd.x, element.pEnd.y);
                    nextStep.element.createSteps();
                }

                return true;
            }

            return false;
        }

        Element newElementA = new ElementLine(); //INIZIALMENTE SPEZZERA' IN DUE LINEE
        Element newElementB = new ElementLine(); //INIZIALMENTE SPEZZERA' IN DUE LINEE

        if (element instanceof ElementArc)
        {
            //ARCO: CERCA DI CAPIRE SE RIMANGONO ALMENO 3 PUNTI PER OGNUNO DEI 2 NUOVI SOTTOARCHI A E B

            if (stepIndexInElement>1) //0,1,2 CI SONO ALMENO TRE PUNTI: PUO' RICREARE IL PRIMO SOTTOARCO
            {
                newElementA = new ElementArc();
                JamPointStep stepMiddle = element.steps.get(stepIndexInElement/2); //IMPOSTA pMiddle AD UN PUNTO MEDIO
                ((ElementArc) newElementA).pMiddle = new PointF(stepMiddle.p.x, stepMiddle.p.y);
            }

            if (stepIndexInElement<element.steps.size()-2) //RIMANGONO ALMENO TRE PUNTI ALLA FINE PUO' RICREARE IL SECONDO SOTTOARCO
            {
                newElementB = new ElementArc();
                JamPointStep stepMiddle = element.steps.get(stepIndexInElement + ((element.steps.size()-stepIndexInElement)/2)); //IMPOSTA pMiddle AD UN PUNTO MEDIO
                ((ElementArc) newElementB).pMiddle = new PointF(stepMiddle.p.x, stepMiddle.p.y);
            }
        }

        int indexOfElement = elements.indexOf(element);

        JamPointStep stepBefore = getStepBefore(stepIndex);
        JamPointStep stepAfter = getStepAfter(stepIndex);
        JamPointStep xStep = null; //NUOVO STEP CHE CORRISPONDE A QUELLO SPOSTATO, AVRA' I CODICI E IL FOCUS

        if (stepBefore!=null)
        {
            if (stepBefore.element instanceof ElementFeed)
            {
                stepBefore.element.pEnd = new PointF(stepBefore.element.pEnd.x + dx, stepBefore.element.pEnd.y + dy);
                stepBefore.element.createSteps();
            }
            else
            {
                if (stepBefore.element == element)
                {
                    //CREA IL NUOVO ELEMENTO A (LINEA O ARCO)
                    insertElement(newElementA, new PointF(element.pStart.x, element.pStart.y), new PointF(stepBefore.p.x, stepBefore.p.y), element, indexOfElement);
                    indexOfElement++;
                }

                //CREA LA PRIMA LINEA DI RACCORDO VERSO IL PUNTO SPOSTATO (IN REALTA' NON SI E' SPOSTATO NESSUN PUNTO!)
                ElementLine newElementLink1 = new ElementLine();
                insertElement(newElementLink1, new PointF(stepBefore.p.x, stepBefore.p.y), new PointF(step.p.x + dx, step.p.y + dy), element, indexOfElement);
                indexOfElement++;

                if (newElementLink1.steps.size()!=0)
                    xStep = newElementLink1.steps.get(newElementLink1.steps.size()-1);
            }
        }

        if (stepAfter!=null)
        {
            if (stepAfter.element instanceof ElementFeed)
            {
                stepAfter.element.pStart = new PointF(stepAfter.element.pStart.x + dx, stepAfter.element.pStart.y + dy);
                stepAfter.element.createSteps();
            }
            else
            {
                //CREA LA SECONDA LINEA DI RACCORDO DAL PUNTO SPOSTATO (IN REALTA' NON SI E' SPOSTATO NESSUN PUNTO!)
                ElementLine newElementLink2 = new ElementLine();
                insertElement(newElementLink2, new PointF(step.p.x + dx, step.p.y + dy), new PointF(stepAfter.p.x, stepAfter.p.y), element, indexOfElement);
                indexOfElement++;

                if (stepAfter.element == element)&& !isLastStepOfElement (stepAfter))
                {
                    //CREA IL NUOVO ELEMENTO B (LINEA O ARCO)
                    insertElement(newElementB, new PointF(stepAfter.p.x, stepAfter.p.y), new PointF(element.pEnd.x, element.pEnd.y), element, indexOfElement);
                }

                if (newElementLink2.steps.size()!=0)
                    xStep = newElementLink2.steps.get(0);
            }
        }

        if (xStep!=null)
        {
            activeStepIndex = getStepIndex(xStep);

            for(JamPointCode code:codes)
                if (code.step == step)
                    code.step = xStep; //ASSEGNA EVENTUALI CODICI
        }

        //RIMUOVE L'ELEMENTO ORIGINALE
        this.elements.remove(element);

        return true;
    }

    //MUOVE TUTTI GLI STEP CONTIGUI COMPRESI NELLA SELEZIONE START/END E RACCORDA CREANDO NUOVE LINEE
    private boolean moveSteps(int stepIndexFrom, int stepIndexTo, float dx, float dy)
    {
        if (stepIndexFrom > stepIndexTo)
            return false;

        if (stepIndexFrom < 0)
            stepIndexFrom = 0;

        int maxIndex = getStepIndex(getLastStep());

        if (stepIndexTo > maxIndex)
            stepIndexTo = maxIndex;

        if (stepIndexFrom == stepIndexTo) //I 2 PUNTI COINCIDONO: VA ALLA FUNZIONE moveStep
            return moveStep(stepIndexFrom, dx, dy);

        int indexElement2 = elements.size()-1;

        JamPointStep s1 = getStep(stepIndexFrom);
        JamPointStep s2 = getStep(stepIndexTo);

        //SE STEP INIZIALE = PRIMO O ULTIMO DI ELEMENTO ED ESISTE UNO STEP PRECEDENTE ALLORA REIMPOSTA STEP INIZIALE = STEP PRECEDENTE
        if (s1!=null && (isFirstStepOfElement(s1) || isLastStepOfElement(s1)) && getStep(stepIndexFrom-1)!=null)
        {
            stepIndexFrom--;
            if(stepIndexFrom!=0)
                stepIndexFrom--;        //daniele 25-02-2020
            s1 = getStep(stepIndexFrom);
        }

        //SE STEP FINALE = ULTIMO DI ELEMENTO ED ESISTE UNO STEP SUCCESSIVO ALLORA REIMPOSTA STEP FINALE = STEP SUCCESSIVO
        if (s2!=null && isLastStepOfElement(s2) && getStep(stepIndexTo+1)!=null)
        {
            stepIndexTo++;
            stepIndexTo++;          //daniele 25-02-2020
            s2 = getStep(stepIndexTo);
        }

        JamPointStep sr2 = getRelatedStep(stepIndexTo);
        PointF p2 = (sr2 != null ? new PointF(sr2.p.x,sr2.p.y) : new PointF(s2.p.x,s2.p.y));
        boolean isStep2Last = false;

        if (s1!=null && s2!=null && s1.element instanceof ElementZigZag && s1.element == s2.element)
        {
            //CASO PARTICOLARE DI SPOSTAMENTO DI INSIEME DI PUNTI DI UNO STESSO ZIGZAG
            for (int i = stepIndexFrom; i <= stepIndexTo; i++)
            {
                JamPointStep step = getStep(i);

                if (step!=null)
                {
                    step.p.x+=dx;
                    step.p.y+=dy;
                }
            }

            return true;
        }

        if ((s1!=null && s1.element instanceof ElementZigZag) || (s2!=null && s2.element instanceof ElementZigZag))
            return false; //NON CONSENTITO CHE STEP INIZIALE E/O FINALE APPARTENGANO AD ELEMENTI ZIGZAG

        //PRIMA ELABORA P2 (E' OLTRE P1 E QUINDI NON INTERFERISCE) POI ELABORA P1
        if (s2!=null)
        {
            indexElement2 = elements.indexOf(s2.element);
            int num2 = splitElementAtStep(s2, 1); //ret=-1 = PRIMO STEP: NESSUNA DIVISIONE; ret=0 ULTIMO STEP: NESSUNA DIVISIONE ret=1 E' STATO SUDDIVISO IN 2

            if (num2==-1) //LO STEP E' IL PRIMO DI ELEMENTO 2: NO DIVISIONE E NO SPOSTAMENTO ELEMENTO 2!
                indexElement2--;

            isStep2Last = num2 == 0;
        }

        //ORA ELABORA P1: PER PRIMA COSA DEVE RILEGGERLO PERCHE splitElementAtStep POTREBBE AVERLO DISTRUTTO!
        s1 = getStep(stepIndexFrom);

        if (s1!=null)
        {
            int indexElement1 = elements.indexOf(s1.element);
            int num1 = 0;

            if (!(s1.element instanceof ElementFeed)) //SE step1 APPARTIENE AD UN FEED DEVE SOLO REIMPOSTARE pEnd DEL FEED SENZA POI TRACCIARE IL RACCORDO 1!!
                num1 = splitElementAtStep(s1, -1); //ret=-1 = PRIMO STEP: NESSUNA DIVISIONE; ret=0 ULTIMO STEP: NESSUNA DIVISIONE ret=1 E' STATO SUDDIVISO IN 2

            boolean isStep1First = num1 == -1;

            if (num1==0) //LO STEP E' L'ULTIMO DI ELEMENTO 1: NO DIVISIONE E NO SPOSTAMENTO ELEMENTO 1!
                indexElement1++;

            if (num1==-1) //LO STEP E' IL PRIMO DI ELEMENTO 1 : NO DIVISIONE MA SPOSTAMENTO INTERO ELEMENTO 1!!
                num1=0;

            indexElement1 += num1; //AGGIUSTA INDICE ELEMENTI DA SPOSTARE (DAL)
            indexElement2 += num1; //AGGIUSTA INDICE ELEMENTI DA SPOSTARE (FINO AL)

            for (int i = indexElement1; i <= indexElement2; i++)
                elements.get(i).move(dx, dy);

            if (s1.element instanceof ElementFeed) //SE ELEMENTO1=FEED LO DEVE MODIFICARE MA NON CREARE LINEA DI RACCORDO 1!
            {
                s1.element.pEnd.x += dx;
                s1.element.pEnd.y += dy;
                s1.element.createSteps();
            }
            else if (indexElement1>0)
            {
                //CREA LA LINEA DI RACCORDO N.1 NEL PUNTO DI DIVISIONE
                ElementLine newElementLink1 = new ElementLine();
                PointF pStart = new PointF(elements.get(indexElement1-1).pEnd.x, elements.get(indexElement1-1).pEnd.y);

                //SE STEP INIZIALE E' PRIMO STEP DI ELEMENT LINE DI 2 STEPS ELIMINA L'ELEMENTO E ESEGUE IL RACCORDO CON IL PUNTO FINALE DI ELEMENTO PRECEDENTE
                if (isStep1First && indexElement1>1)
                {
                    if (elements.get(indexElement1-1).steps.size() == 2)
                    {
                        pStart.x = elements.get(indexElement1-2).pEnd.x;
                        pStart.y = elements.get(indexElement1-2).pEnd.y;
                        elements.remove(indexElement1-1);
                        indexElement1--;
                        indexElement2--;
                    }
                }

                PointF pEnd = new PointF(elements.get(indexElement1).pStart.x, elements.get(indexElement1).pStart.y);

                insertElement(newElementLink1, pStart, pEnd, s1.element, indexElement1); //s1.p.x, s1.p.y
                indexElement2++;

                if (activeStepIndex>=indexSelectionStepStart)
                    activeStepIndex += newElementLink1.steps.size()+1; //AGGIORNA LA POSIZIONE ACTIVE STEP AGGIUNGENDO IL NUMERO DI STEP DI LINEA DI RACCORDO
            }

            if (s2.element instanceof ElementFeed) //SE ELEMENTO2=FEED LO DEVE MODIFICARE MA NON CREARE LINEA DI RACCORDO 2!
            {
                s2.element.pStart.x += dx;
                s2.element.pStart.y += dy;
                s2.element.createSteps();
            }
            else if (indexElement2<elements.size()-1)
            {
                if(elements.get(indexElement2+1) instanceof  ElementFeed)
                {
                    ElementFeed elementFeed = (ElementFeed) elements.get(indexElement2+1);

                    elementFeed.pStart.x = s2.p.x;
                    elementFeed.pStart.y = s2.p.y;
                    elementFeed.createSteps();
                }
                else
                {
                    //CREA LA LINEA DI RACCORDO N.2 NEL PUNTO DI DIVISIONE
                    ElementLine newElementLink2 = new ElementLine();
                    PointF pStart = new PointF(elements.get(indexElement2).pEnd.x, elements.get(indexElement2).pEnd.y);
                    PointF pEnd = new PointF(s2.p.x, s2.p.y);

                    //SE STEP FINALE E' ULTIMO STEP DI ELEMENT LINE DI 2 STEPS ELIMINA L'ELEMENTO E ESEGUE IL RACCORDO CON IL PUNTO INIZIALE DI ELEMENTO SUCCESSIVO
                    if (isStep2Last)
                    {
                        pEnd.x = p2.x;
                        pEnd.y = p2.y;

                        if(elements.get(indexElement2+1).steps.size()==2)
                        {
                            pEnd.x = elements.get(indexElement2+1).steps.get(1).p.x;
                            pEnd.y = elements.get(indexElement2+1).steps.get(1).p.y;
                            elements.remove(indexElement2+1);
                        }
                    }

                    insertElement(newElementLink2, pStart, pEnd, s2.element, indexElement2 + 1);
                }
            }

            return true;
        }

        return false;
    }

    //ELIMINA UN INSIEME DI STEPS CONTIGUI - GESTITO ANCHE ZIGZAG (SOLO ELIMINAZIONE DI STEP, SE NESSUNO STEP RIMASTO ELIMINA ELEMENTO)
    private boolean deleteSteps(int stepIndexFrom, int stepIndexTo)
    {
        if (stepIndexFrom > stepIndexTo || stepIndexFrom < 0)
            return false;

        JamPointStep step1 = getStep(stepIndexFrom); //PRIMO STEP DA ELIMINARE
        JamPointStep step2 = getStep(stepIndexTo); //ULTIMO STEP DA ELIMINARE

        if (step1==null || step2==null)
            return false;

        Element element1 = step1.element;
        Element element2 = step2.element;

        boolean isSameElement = element1 == element2;

        JamPointStep stepBefore = getStepBefore(stepIndexFrom); //EVENTUALE STEP PRECEDENTE A QUELLO DA ELIMINARE
        JamPointStep stepAfter = getStepAfter(stepIndexTo); //EVENTUALE STEP SUCCESSIVO A QUELLO DA ELIMINARE

        if (stepIndexFrom == stepIndexTo) //CASO PARTICOLARE DI ELIMINAZIONE DI SINGOLO STEP
        {
            if (element1 instanceof ElementFeed) //ELIMINAZIONE DEL PUNTO (FINALE) DI UN FEED
            {
                if (stepBefore!=null && stepBefore.element instanceof ElementFeed) //SE PRIMA DI QUESTO FEED C'E' UN ALTRO FEED LO ESTENDO ALLA FINE DI QUESTO
                {
                    stepBefore.element.pEnd = new PointF(element1.pEnd.x, element1.pEnd.y);
                    stepBefore.element.createSteps();
                }
                else if (stepAfter!=null && stepAfter.element instanceof ElementFeed) //SE DOPO ABBIAMO UN ALTRO FEED ESTENDIAMO IL PROSSIMO FEED ALL'INIZIO DI QUESTO
                {
                    stepAfter.element.pStart = new PointF(element1.pStart.x, element1.pStart.y);
                    stepAfter.element.createSteps();
                }
                else
                    return false;

                elements.remove(element1); //IN OGNI CASO ELIMINO IL FEED!
                return true; //NON DEVE ESEGUIRE ALTRO
            }
        }

        boolean existsStepBefore = stepBefore!=null;
        boolean existsStepAfter = stepAfter!=null;
        boolean element1IsFeed = element1 instanceof ElementFeed;
        boolean element2IsFeed = element2 instanceof ElementFeed;
        boolean elementBeforeIsFeed = existsStepBefore ? stepBefore.element instanceof ElementFeed : false;
        boolean elementAfterIsFeed = existsStepAfter ? stepAfter.element instanceof ElementFeed : false;
        boolean isStep1StartOfElement = element1.steps.indexOf(step1)==0 && !element1IsFeed;
        boolean isStep1EndOfElement = element1.steps.indexOf(step1)==element1.steps.size()-1;
        boolean isStep2StartOfElement = element2.steps.indexOf(step2)==0 && !element1IsFeed;
        boolean isStep2EndOfElement = element2.steps.indexOf(step2)==element2.steps.size()-1;
        int indexToInsertFeed = -1;

        //ELIMINA SUBITO TUTTI GLI STEP DA ELIMINARE!!
        for (int i = stepIndexFrom; i <= stepIndexTo; i++)
            removeStep(stepIndexFrom);

        if (isSameElement && !(element1 instanceof ElementZigZag)) //PER LO ZIGZAG SI ESEGUE SOLO ELIMINA STEPS, SE STEPS=0 SI ELIMINA L'ELEMENTO (isSameElement) 2019-06-10
        {
            //DEVE SPEZZARE IN DUE L'ELEMENTO!
            Element elementA = new ElementLine();
            Element elementB = new ElementLine();

            int indexStepBefore = element1.steps.indexOf(stepBefore);
            int numStepsElement1 = indexStepBefore == -1 ? 0 : indexStepBefore+1;
            int indexStepAfter = element1.steps.indexOf(stepAfter);
            int numStepsElement2 = indexStepAfter == -1 ? 0 : element1.steps.size()-indexStepAfter;

            if (element1 instanceof ElementArc)
            {
                if (numStepsElement1>2)
                {
                    //SE POSSIBILE RICREA IL PRIMO ARCO
                    elementA = new ElementArc();
                    int indexOfMiddleStep = numStepsElement1/2;
                    ((ElementArc)elementA).pMiddle = new PointF(element1.steps.get(indexOfMiddleStep).p.x, element1.steps.get(indexOfMiddleStep).p.y);
                }

                if (numStepsElement2>2)
                {
                    //SE POSSIBILE RICREA IL SECONDO ARCO
                    elementB = new ElementArc();
                    int indexOfMiddleStep = indexStepAfter + numStepsElement2/2;
                    ((ElementArc)elementB).pMiddle = new PointF(element1.steps.get(indexOfMiddleStep).p.x, element1.steps.get(indexOfMiddleStep).p.y);
                }
            }

            int indexToInsert = elements.indexOf(element1);

            if (numStepsElement1>1)
            {
                //SE POSSIBILE RICREA IL PRIMO ELEMENTO (LINEA O ARCO)
                insertElement(elementA, new PointF(element1.pStart.x, element1.pStart.y), new PointF(stepBefore.p.x, stepBefore.p.y), element1, ++indexToInsert);
                stepBefore.element = elementA;
            }

            indexToInsertFeed = indexToInsert+1;

            if (numStepsElement2>1)
            {
                //SE POSSIBILE RICREA IL SECONDO ELEMENTO (LINEA O ARCO)
                insertElement(elementB, new PointF(stepAfter.p.x, stepAfter.p.y), new PointF(element1.pEnd.x, element1.pEnd.y), element1, ++indexToInsert);
                stepAfter.element = elementB;
            }
        }
        else
        {
            if (element1IsFeed && existsStepAfter)
            {
                //IL PRIMO STEP DA ELIMINARE E' IL PUNTO FINALE DI UN ELEMENTO FEED: LO ELIMINA E RICREA IL FEED FINO AL PRIMO PUNTO NON CANCELLATO!
                element1.pEnd = new PointF(stepAfter.p.x, stepAfter.p.y);
                element1.createSteps();
            }

            if (element2IsFeed && existsStepBefore)
            {
                element2.pStart = new PointF(stepBefore.p.x, stepBefore.p.y);
                element2.createSteps();
            }

            indexToInsertFeed = elements.indexOf(element1)+1;

            //CICLO PER RISISTEMARE TUTTI GLI ELEMENTI COINVOLTI
            List<Element> elements2 = new ArrayList<>(elements); //ESEMPIO DUPLICAZIONE LISTA!!!!
            for (Element element : elements2) //ALTRIMENTI DA ERRORE IL REMOVE!!!!!
            {
                int numSteps = element.steps.size();

                if ((numSteps == 0) || (numSteps == 1 && !(element instanceof ElementFeed))) //E' RIMASTO UN SOLO STEP O NESSUNO: VA ELIMINATO TUTTO L'ELEMENTO!!
                {
                    if (element == element1)
                        indexToInsertFeed--;

                    elements.remove(element);
                }
                else if ((element == element1 || element == element2)  && !(element instanceof ElementFeed) && !(element instanceof ElementZigZag)) //PER ZIGZAG SOLO ELIMINA STEP!
                {
                    Element elementNew = new ElementLine();
                    PointF pStart = new PointF(element.steps.get(0).p.x, element.steps.get(0).p.y);
                    PointF pEnd = new PointF(element.steps.get(numSteps-1).p.x, element.steps.get(numSteps-1).p.y);

                    if (element instanceof ElementArc && numSteps > 2)
                    {
                        elementNew = new ElementArc(); //ERA UN ELEMENTO ARCO: PROVA A RICREARLO SE HA ALMENO 3 PUNTI
                        ((ElementArc) elementNew).pMiddle = new PointF(element.steps.get(numSteps / 2).p.x, element.steps.get(numSteps / 2).p.y);
                    }

                    replaceElement(elementNew, pStart, pEnd, element, elements.indexOf(element));
                }
            }
        }

        if (existsStepBefore && existsStepAfter)
        {
            //SE ESISTONO ENTRAMBI I DUE PUNTI ESTERNI A QUELLI CANCELLATI CALCOLA LA DISTANZA
            double distanza = Tools.getDistance(stepBefore, stepAfter);
            if(distanza > MIN_FEED_DISTANCE)
            {
                //SE NECESSARIO CREA IL FEED DI RACCORDO FRA BEFORE E AFTER
                ElementFeed elementFeed = new ElementFeed();
                insertElement(elementFeed, new PointF(stepBefore.p.x, stepBefore.p.y), new PointF(stepAfter.p.x, stepAfter.p.y), stepBefore.element, indexToInsertFeed);
            }
            else //AGGIUNTO
            {
                //ESTENSIONE EVENTUALI ELEMENTI FEED PRECEDENTI E/O SUCCESSIVI AI PUNTI DI ROTTURA
                if (isStep1StartOfElement && stepBefore.element instanceof ElementFeed)
                {
                    stepBefore.element.pEnd = new PointF(stepAfter.p.x, stepAfter.p.y);
                    stepBefore.element.createSteps();
                }

                if (isStep2EndOfElement && stepAfter.element instanceof ElementFeed)
                {
                    stepAfter.element.pStart = new PointF(stepBefore.p.x, stepBefore.p.y);
                    stepAfter.element.createSteps();
                }
            }
        }

        if (isSameElement && !(element1 instanceof ElementZigZag)) //LO ZIGZAG NON VIENE MAI SDOPPIATO!!! 2019-06-10
            elements.remove(element1); //NON POSSO FARLO PRIMA ALTRIMENTI stepBefore E stepAfter NON SAREBBERO PIU' VALIDI!!!

        return true;
    }

    private boolean isFirstElement(Element element)
    {
        boolean ret = false;

        if (element!=null)
            ret = elements.indexOf(element) == 0;

        return ret;
    }

    private boolean isLastElement(Element element)
    {
        boolean ret = false;

        if (element!=null)
            ret = elements.indexOf(element) == elements.size()-1;

        return ret;
    }

    private boolean isFirstStepOfElement(JamPointStep step)
    {
        boolean ret = false;

        if (step!=null && step.element!=null)
            ret = step.element.steps.indexOf(step) == 0;

        return ret;
    }

    private boolean isLastStepOfElement(JamPointStep step)
    {
        boolean ret = false;

        if (step!=null && step.element!=null)
            ret = step.element.steps.indexOf(step) == step.element.steps.size()-1;

        return ret;
    }

    public boolean isLastStep(JamPointStep step)
    {
        boolean ret = false;

        if (step!=null && step.element!=null)
            ret = isLastElement(step.element) && isLastStepOfElement(step);

        return ret;
    }

    private boolean deleteElement(int elementIndex)
    {
        boolean ret = false;

        if (elements.size()>elementIndex)
        {
            Element element = elements.get(elementIndex);
            Element prevElement = getPrevElement(element);
            Element nextElement = getNextElement(element);

            if (element.isRealElement() && prevElement.isRealElement() && nextElement.isRealElement())
            {
                //ELEMENTO CUCITURA IN MEZZO A DUE ELEMENTI CUCITURA: LO TRASFORMA IN ELEMENTO FEED
                ElementFeed elementFeed = new ElementFeed();
                replaceElement(elementFeed, element.pStart, element.pEnd, element, elementIndex);
            }
            else
            {
                if (!isFirstElement(element) && prevElement instanceof ElementFeed)
                {
                    prevElement.pEnd = new PointF(element.pEnd.x, element.pEnd.y);
                    prevElement.createSteps();
                }

                if (!isLastElement(element) && nextElement instanceof ElementFeed)
                {
                    nextElement.pStart = new PointF(element.pStart.x, element.pStart.y);
                    nextElement.createSteps();
                }

                elements.remove(elementIndex);
            }

            ret = true;
        }

        return ret;
    }

    //ELIMINA UN GRUPPO CONTIGUO DI ELEMENTI
    private void deleteElements(int startElementIndex, int endElementIndex)
    {
        for(int i=startElementIndex; i<=endElementIndex; i++)
            elements.remove(startElementIndex);
    }

    //ELIMINA IL SINGOLO STEP SPECIFICATO E GLI EVENTUALI CODICI ASSOCIATI
    private void removeStep(int stepIndex)
    {
        JamPointStep step = getStep(stepIndex);

        if (step!=null)
        {
            clearStepCodes(step);
            step.element.steps.remove(step);
        }
    }

    //CREA UNA LINEA (O ARCO) TRA DUE (O 3) STEPS SPECIFICATI, ELIMINANDO IL PRECEDENTE CONTENUTO
    private boolean joinSteps(int stepIndexFrom, int stepIndexTo, int stepIndexMiddleArc)
    {
        if (stepIndexFrom >= stepIndexTo)
            return false;

        if (stepIndexFrom < 0)
            stepIndexFrom = 0;

        int maxIndex = getStepIndex(getLastStep());

        if (stepIndexTo > maxIndex)
            stepIndexTo = maxIndex;

        int indexElement2 = elements.size()-1;

        JamPointStep s1 = getStep(stepIndexFrom);
        JamPointStep s2 = getStep(stepIndexTo);
        JamPointStep sm = getStep(stepIndexMiddleArc);

        if (s1==null || s2==null)
            return false;

        //if (s1.element instanceof ElementFeed || s2.element instanceof ElementFeed)
        //    return false; //NON CONSENTITO CHE STEP INIZIALE E/O FINALE APPARTENGANO AD ELEMENTI ZIGZAG O FEED

        //PRIMA ELABORA P2 (E' OLTRE P1 E QUINDI NON INTERFERISCE) POI ELABORA P1
        indexElement2 = elements.indexOf(s2.element);
        int num2 = splitElementAtStep(s2, 0); //ret=-1 = PRIMO STEP: NESSUNA DIVISIONE; ret=0 ULTIMO STEP: NESSUNA DIVISIONE ret=1 E' STATO SUDDIVISO IN 2

        if (num2==-1) //LO STEP E' IL PRIMO DI ELEMENTO 2: NO DIVISIONE E NO SPOSTAMENTO ELEMENTO 2!
            indexElement2--;

        //ORA ELABORA P1: PER PRIMA COSA DEVE RILEGGERLO PERCHE splitElementAtStep POTREBBE AVERLO DISTRUTTO!
        s1 = getStep(stepIndexFrom);
        int indexElement1 = elements.indexOf(s1.element);
        int num1 = splitElementAtStep(s1, 0); //ret=-1 = PRIMO STEP: NESSUNA DIVISIONE; ret=0 ULTIMO STEP: NESSUNA DIVISIONE ret=1 E' STATO SUDDIVISO IN 2

        if (num1==0) //LO STEP E' L'ULTIMO DI ELEMENTO 1: NO DIVISIONE E NO ELABORAZIONE ELEMENTO 1!
            indexElement1++;

        if (num1==-1) //LO STEP E' IL PRIMO DI ELEMENTO 1 : NO DIVISIONE MA ELABORAZIONE INTERO ELEMENTO 1!!
            num1=0;

        indexElement1 += num1; //AGGIUSTA INDICE ELEMENTI DA SPOSTARE (DAL)
        indexElement2 += num1; //AGGIUSTA INDICE ELEMENTI DA SPOSTARE (FINO AL)

        int app = 0;
        for (int i = indexElement1; i <= indexElement2; i++)
        {
            elements.remove(indexElement1);
            app++;
        }

        //indexElement1-=app;
        indexElement2-=app;

        if ((indexElement1>0 && indexElement2<elements.size()-1) || stepIndexTo == maxIndex)
        {
            //CREA UNA NUOVA LINEA (O ARCO) DI RACCORDO NEI PUNTI DI DIVISIONE
            Element newElementLink = new ElementLine();

            if (sm!=null)
            {
                newElementLink = new ElementArc();
                ((ElementArc) newElementLink).pMiddle = new PointF(sm.p.x, sm.p.y);
            }

            insertElement(newElementLink, new PointF(s1.p.x, s1.p.y), new PointF(s2.p.x, s2.p.y), s1.element, indexElement1);

            if (newElementLink.steps.size()>0)
                activeStepIndex = getStepIndex(newElementLink.steps.get(newElementLink.steps.size()-1)); //SI POSIZIONA SULL'ULTIMO PUNTO RADDRIZZATO
        }

        return true;
    }

    //RIMPIAZZA L'ELEMENTO NELL' INDICE SPECIFICATO CON UN NUOVO ELEMENTO (GIA' INSTANZIATO)
    private void replaceElement(Element elementNew, PointF pStart, PointF pEnd, Element elementModel, int indexToReplace)
    {
        setElementAndCreateSteps(elementNew, pStart, pEnd, elementModel);
        elements.set(indexToReplace, elementNew);
    }

    //INSERISCE UN ELEMENTO (NUOVO E GIA' INSTANZIATO) NELLA POSIZIONE SPECIFICATA, IMPOSTANDO I PUNTI ESTREMI - GLI ALTRI VALORI DA ELEMENTO MODELLO
    private void insertElement(Element elementNew, PointF pStart, PointF pEnd, Element elementModel, int indexToInsert)
    {
        setElementAndCreateSteps(elementNew, pStart, pEnd, elementModel);
        this.elements.add(indexToInsert, elementNew);
    }

    //REIMPOSTA I VALORI MINIMI DI UN ELEMENTO E NE RICREA GLI STEPS
    private void setElementAndCreateSteps(Element element, PointF pStart, PointF pEnd, Element elementModel)
    {
        element.pStart = new PointF(pStart.x, pStart.y);
        element.pEnd = new PointF(pEnd.x, pEnd.y);

        if (elementModel!=null)
        {
           float defaultPasso = element instanceof ElementZigZag ? DEFAULT_PASSO_ZIGZAG : DEFAULT_PASSO;

           if (!(element.passo != elementModel.passo && element.passo != defaultPasso))
               element.passo = elementModel.passo;

            element.entity = elementModel.entity;
        }

        element.ricetta = this;
        element.createSteps();
    }

    //SPEZZA UN ELEMENTO IN PROSSIMITA' DI UNO STEP CREANDO DUE SOTTOELEMENTI DELLO STESSO TIPO IN CUI RICARICA I CODICI DEGLI STEP DELL'ELEMENTO ORIGINARIO
    private int splitElementAtStep(JamPointStep step, int stepOffset) //ret=-1 = NESSUNA DIVISIONE (PRIMO STEP); ret=0 NESSUNA DIVISIONE (ULTIMO STEP); ret=1 E' STATO SUDDIVISO IN 2 SOTTOELEMENTI
    {
        //PARAMETRO stepOffset: INSERITO PERCHE' IL MOVE DEVE COLLEGARE "OBLIQUAMENTE" DA STEP PRECEDENTE E FINO A STEP SUCCESSIVO A QUELLI SPOSTATI
        //SE =-1 SETTA COME END DEL NUOVO SOTTOELEMENTO (A) LO STEP PRECEDENTE A QUELLO PASSATO (TERMINA SOTTOELEMENTO (A) UNO STEP PRIMA DEL DOVUTO)
        //SE =1 SETTA COME START DEL NUOVO SOTTOELEMENTO (B) LO STEP SUCCESSIVO A QUELLO PASSATO (INIZIA SOTTOELEMENTO (B) UNO STEP DOPO IL DOVUTO)
        //SE =0 END SOTTOELEMENTO A E START SOTTOELEMENTO B CONDIVIDONO LO STEP PASSATO
        //ATTENZIONE: SE stepOffset<>0 AL RITORNO SONO CAMBIATE LE COORDINATE DELLO STEP PASSATO COME PARAMETRO!!

        Element element = step.element;
        int indexStepInElement = element.steps.indexOf(step);

        if (indexStepInElement == 0) //LO STEP E' QUELLO INIZIALE, NON PUO' DIVIDERE E RITORNA -1;
            return (element instanceof ElementFeed) ? 0 : -1; //SE FEED IL PRIMO STEP E' ANCHE ULTIMO!!

        if (indexStepInElement==element.steps.size()-1) //ULTIMO STEP DELL'ELEMENTO: NON PUO' DIVIDERLO, RITORNA 0!!
            return 0;

        Element newElementA = new ElementLine(); //INIZIALMENTE SPEZZERA' IN DUE LINEE
        Element newElementB = new ElementLine(); //INIZIALMENTE SPEZZERA' IN DUE LINEE

        if (element instanceof ElementArc)
        {
            //ERA UN ARCO: CERCA DI CAPIRE SE RIMANGONO ALMENO 3 PUNTI PER OGNUNO DEI 2 NUOVI SOTTOARCHI A E B

            if (indexStepInElement>1) //0,1,2 CI SONO ALMENO TRE PUNTI: PUO' RICREARE IL PRIMO SOTTOARCO
            {
                newElementA = new ElementArc();
                JamPointStep stepMiddle = element.steps.get(indexStepInElement/2); //IMPOSTA pMiddle AD UN PUNTO MEDIO
                ((ElementArc) newElementA).pMiddle = new PointF(stepMiddle.p.x, stepMiddle.p.y);
            }

            if (indexStepInElement<element.steps.size()-2) //RIMANGONO ALMENO TRE PUNTI ALLA FINE PUO' RICREARE IL SECONDO SOTTOARCO
            {
                newElementB = new ElementArc();
                JamPointStep stepMiddle = element.steps.get(indexStepInElement + ((element.steps.size()-indexStepInElement)/2)); //IMPOSTA pMiddle AD UN PUNTO MEDIO
                ((ElementArc) newElementB).pMiddle = new PointF(stepMiddle.p.x, stepMiddle.p.y);
            }
        }

        int indexOfElement = elements.indexOf(element);
        int indexOfStep = element.steps.indexOf(step) + stepOffset;
        JamPointStep stepOK = element.steps.get(indexOfStep);

        //CREA IL NUOVO ELEMENTO A (LINEA O ARCO)
        insertElement(newElementA, new PointF(element.pStart.x, element.pStart.y), stepOffset == -1 ? new PointF(stepOK.p.x, stepOK.p.y) : new PointF(step.p.x, step.p.y), element, indexOfElement);

        //CREA IL NUOVO ELEMENTO B (LINEA O ARCO)
        insertElement(newElementB, stepOffset == 1 ? new PointF(stepOK.p.x, stepOK.p.y) : new PointF(step.p.x, step.p.y), new PointF(element.pEnd.x, element.pEnd.y), element, indexOfElement+1);

        //AGGIUSTA IL RIFERIMENTO DI EVENTUALI CODICI ASSEGNATI AGLI STEP CHE FACEVANO PARTE DELL'ELEMENTO ORIGINARIO
        List<Element> list = new ArrayList<>();
        list.add(newElementA);
        list.add(newElementB);

        for(JamPointStep s:element.steps)
            for (JamPointCode c:codes)
                if (c.step == s)
                    c.adjustStep(list);

        if (stepOffset!=0)
            step.p = new PointF(stepOK.p.x, stepOK.p.y);

        //RIMUOVE L'ELEMENTO ORIGINALE. LO STEP PASSATO COME PARAMETRO NON SARA' PIU' FRA QUELLI VALIDI!!!
        this.elements.remove(element);
        return 1;
    }



    //RESTITUISCE LO STEP PRECEDENTE A QUELLO INDICATO O NULL SE INIZIO RICETTA
    private JamPointStep getStepBefore(int stepIndex)
    {
        JamPointStep ret = null;
        int index = 0;

        for(Element element:elements)
        {
            for (JamPointStep step : element.steps)
            {
                if (index == stepIndex - 1)
                {
                    ret = step;
                    break;
                }

                index++;
            }

            if (ret != null)
                break;
        }

        return ret;
    }

    //RESTITUISCE LO STEP PRECEDENTE A QUELLO INDICATO O L'ULTIMO SE NON CI SONO ALTRI STEP PRIMA
    private JamPointStep getStepBefore(int stepIndex, boolean returnLast)
    {
        JamPointStep step = getStepBefore(stepIndex);

        if (step==null && returnLast)
            step = getLastStep();

        return step;
    }

    //RESTITUISCE LO STEP SUCCESSIVO A QUELLO INDICATO O NULL SE FINE RICETTA
    private JamPointStep getStepAfter(int stepIndex)
    {
        JamPointStep ret = null;
        int index = 0;

        for(Element element:elements)
        {
            for (JamPointStep step : element.steps)
            {
                if (index == stepIndex + 1)
                {
                    ret = step;
                    break;
                }

                index++;
            }

            if (ret != null)
                break;
        }

        return ret;
    }

    //RESTITUISCE LO STEP SUCCESSIVO A QUELLO INDICATO O IL PRIMO SE NON CI SONO ALTRI STEP DOPO
    private JamPointStep getStepAfter(int stepIndex, boolean returnFirst)
    {
        JamPointStep step = getStepAfter(stepIndex);

        if (step==null && returnFirst)
            step = getFirstStep();

        return step;
    }

    //RESTITUISCE L'ULTIMO STEP O NULL SE NON CI SONO STEP
    private JamPointStep getLastStep()
    {
        JamPointStep step = null;

        if (elements.size()!=0)
            if (elements.get(elements.size()-1).steps.size()!=0)
                step = elements.get(elements.size()-1).steps.get(elements.get(elements.size()-1).steps.size()-1);

        return step;
    }

    //RESTITUISCE L'ULTIMO STEP O NULL SE NON CI SONO STEP
    private JamPointStep getFirstStep()
    {
        JamPointStep step = null;

        if (elements.size()!=0)
            if (elements.get(0).steps.size()!=0)
                step = elements.get(0).steps.get(0);

        return step;
    }

    //MUOVE UN ELEMENTO DELLA RICETTA
    private void moveElement(int index, float dx, float dy)
    {
        moveElements(index, index, dx,dy,0,0);
    }

    //MUOVE UNA SERIE DI ELEMENTI CONTIGUI DELLA RICETTA
    private void moveElements(int startElementIndex, int endElementIndex, float dx, float dy, float pcdx, float pcdy)
    {
        pcX += pcdx;
        pcY += pcdy;

        for(int i=startElementIndex; i<=endElementIndex; i++)
        {
            Element element = elements.get(i);

            if (i==startElementIndex)
            {
                //ESEGUE LO STRETCH SULL'EVENTUALE ELEMENTO PRECEDENTE ALLA SELEZIONE
                Element elementPrev = getPrevElement(element);
                if (elementPrev!=null)
                    if (element.pStart.x == elementPrev.pEnd.x && element.pStart.y == elementPrev.pEnd.y)
                    {
                        elementPrev.pEnd.x += dx;
                        elementPrev.pEnd.y += dy;
                        elementPrev.createSteps();
                    }
            }

            if (i==endElementIndex)
            {
                //ESEGUE LO STRETCH SULL'EVENTUALE ELEMENTO SUCCESSIVO ALLA SELEZIONE
                Element elementNext = getNextElement(element);
                if (elementNext!=null)
                    if (element.pEnd.x == elementNext.pStart.x && element.pEnd.y == elementNext.pStart.y)
                    {
                        elementNext.pStart.x += dx;
                        elementNext.pStart.y += dy;
                        elementNext.createSteps();
                    }
            }

            //MUOVE L'ELEMENTO CORRENTE
            element.move(dx, dy);
        }

    }




    //******************** METODI DI GESTIONE CODICI ********************

    //AGGIUNGE UN CODICE ALLO STEP ATTIVO
    public boolean addActiveStepCode(JamPointCode.TipiCodici codeType, JamPointCode.TipiValori codeValue)
    {
        return addStepCode(getStep(activeStepIndex), codeType, codeValue);
    }
    public boolean addActiveStepCodeM8(JamPointCode.TipiCodici codeType, String codeValue) {
        return addStepCodeM8(getStep(activeStepIndex), codeType, codeValue);
    }
    //AGGIUNGE UN CODICE AD UNO STEP SPECIFICATO ELETTRONICA M8
    private boolean addStepCodeM8(JamPointStep step, JamPointCode.TipiCodici codeType, String codeValue) {
        if (step!=null)
        {
            saveUndo();
            JamPointCode code = new JamPointCode();
            code.tipoCodice = codeType;
            code.valore_M8 = codeValue;
            code.step = step;
            codes.add(code);
            return true;
        }

        return false;
    }

    //AGGIUNGE UN CODICE AD UNO STEP SPECIFICATO
    public boolean addStepCode(JamPointStep step, JamPointCode.TipiCodici codeType, JamPointCode.TipiValori codeValue)
    {
        if (step!=null)
        {
            saveUndo();
            JamPointCode code = new JamPointCode();
            code.tipoCodice = codeType;
            code.valore = codeValue;
            code.step = step;
            codes.add(code);
            return true;
        }

        return false;
    }

    //RITORNA TUTTI GLI EVENTUALI CODICI ASSOCIATI AD UNO STEP
    public List<JamPointCode> getActiveStepCodes()
    {
        List<JamPointCode> ret = new ArrayList<>();
        JamPointStep step = getStep(activeStepIndex);

        if (step!=null)
            for(JamPointCode code:codes)
                if (code.step == step)
                    ret.add(code);

        return ret;
    }

    //RIMUOVE TUTTI GLI EVENTUALI CODICI ASSOCIATI AD UNO STEP
    public boolean clearActiveStepCodes()
    {
        JamPointStep step = getStep(activeStepIndex);

        if (step!=null)
        {
            saveUndo();
            return clearStepCodes(step);
        }

        return false;
    }

    private boolean clearStepCodes(JamPointStep step)
    {
        if (step!=null)
        {
            List<JamPointCode> codes2 = new ArrayList<>(codes);

            for(JamPointCode code:codes2)
                if (code.step == step)
                    codes.remove(code);

            return true;
        }

        return false;

    }

    //TORNA I CODICI CON VALORE1 ATTIVI PRIMA DI UN DETERMINATO STEP
    public List<String> getActiveCodesAtActiveStep()
    {
        List<String> ret = new ArrayList<>();

        int index = 0;

        for(Element element:elements)
        {
            for (JamPointStep step : element.steps)
            {
                if (index < activeStepIndex)
                {
                    for(JamPointCode code:codes)
                        if (code.step == step)
                        {
                            if (code.valore == JamPointCode.TipiValori.VALUE0 && ret.contains(code.tipoCodice.name()))
                                ret.remove(code.tipoCodice.name());
                            else if (code.valore == JamPointCode.TipiValori.VALUE1 && !ret.contains(code.tipoCodice.name()))
                                ret.add(code.tipoCodice.name());
                        }
                }
                else
                    return ret;

                index++;
            }
        }

        return ret;
    }

    public void clearCodes()
    {
        saveUndo();
        codes.clear();
    }

    //ELENCO DI TUTTI I PUNTI (STEP UNIVOCI E CODICI) PRESENTI IN RICETTA - PER EXPORT USR
    public List<JamPoint> getPoints()
    {
        List<JamPoint> points = new ArrayList<>();

        PointF pLast =  new PointF(0,0); //PER VERIFICARE SE LO STEP E' DUPLICATO

        for(Element element: elements)
        {
            for (JamPointStep step : element.steps)
            {
                step.isDuplicateStep = isJunctionPoints(step.p, pLast); //IMPOSTA SE LO STEP E' DUPLICATO (STESSE COORDINATE DEL PRECEDENTE)
                points.add(step);
                pLast = step.p;

                //AGGIUNGE DI SEGUITO ALLO STEP I RISPETTIVI CODICI, SE PRESENTI
                for(JamPointCode code: codes)
                    if (code.step == step)
                        points.add(code);
            }
        }

        return points;
    }



    public boolean splitLineAtStep(float xEnd, float yEnd, float passo)
    {
        boolean ret = false;

        JamPointStep step = getStep(activeStepIndex);

        if (step!=null)
        {
            Element element1 = step.element;

            if (element1 instanceof ElementLine)
            {
                saveUndo();
                int elementIndex = elements.indexOf(element1);

                ElementLine element4 = new ElementLine();
                element4.pStart = step.p;
                element4.pEnd = element1.pEnd;
                element4.passo = element1.passo;
                element4.ricetta = this;
                element4.createSteps();

                //TAGLIA l'ELEMENTO ORIGINALE IN PROSSIMITA' DELLO STEP SELEZIONATO
                element1.pEnd = step.p;
                element1.createSteps();

                //CREA UNA NUOVA LINEA DAL PUNTO DI ROTTURA AL PUNTO FINALE
                ElementLine element2 = new ElementLine();
                element2.pStart = step.p;
                element2.pEnd = new PointF(xEnd, yEnd);
                element2.passo = passo;
                element2.ricetta = this;
                element2.createSteps();

                ElementFeed element3= new ElementFeed();
                element3.pStart = new PointF(xEnd, yEnd);
                element3.pEnd = step.p;
                element3.ricetta = this;
                element3.createSteps(); //IN REALTA' NON SERVE

                elements.set(elementIndex, element1);
                elements.add(elementIndex+1, element2);
                elements.add(elementIndex+2, element3);
                elements.add(elementIndex+3, element4);

                ret = true;
            }
        }

        return ret;
    }



    //RITORNA L'ELEMENTO SUCCESSIVO A QUELLO SPECIFICATO, O IL PRIMO SE VIENE SPECIFICATO L'ULTIMO, NULL SE NON DISPONIBILE
    private Element getNextElement(Element element)
    {
        Element ret = null;

        if (elements.indexOf(element) < elements.size() - 1)
            ret = elements.get(elements.indexOf(element) + 1);
        else if (elements.indexOf(element)!=0)
            ret = elements.get(0);

        return ret;
    }

    //RITORNA L'ELEMENTO PRECEDENTE A QUELLO SPECIFICATO, O L'ULTIMO SE VIENE SPECIFICATO IL PRIMO, NULL SE NON DISPONIBILE
    private Element getPrevElement(Element element)
    {
        Element ret = null;

        if (elements.indexOf(element)>0) //ESISTE UN ELEMENTO PRECEDENTE NELLA RICETTA
            ret = elements.get(elements.indexOf(element)-1);
        else if(elements.size()>1) //NON ESISTE UN ELEMENTO PRECEDENTE PRENDE L'ULTIMO SE SONO ALMENO 2
            ret = elements.get(elements.size() - 1);

        return ret;
    }




    //********************  METODI DI DISEGNO RICETTA ********************

    public void setDrawPosition(PointF pStartPosition)
    {
        pEndLast.x = pStartPosition.x;
        pEndLast.y = pStartPosition.y;
    }

    public ElementLine drawLineTo(PointF pEnd)
    {
        return drawOrInsertLine(pEndLast, pEnd, Ricetta.DEFAULT_PASSO);
    }

    public ElementLine drawLineTo(PointF pEnd, float passo)
    {
        return drawOrInsertLine(pEndLast, pEnd, passo);
    }

    public ElementArc drawArcTo(PointF pMiddle, PointF pEnd)
    {
        return drawOrInsertArc(pEndLast, pMiddle, pEnd, Ricetta.DEFAULT_PASSO);
    }

    public ElementArc drawArcTo(PointF pMiddle, PointF pEnd, float passo)
    {
        return drawOrInsertArc(pEndLast, pMiddle, pEnd, passo);
    }

    public ElementZigZag drawZigZagTo(PointF pEnd)
    {
        return drawZigZagTo(pEnd, Ricetta.DEFAULT_ALTEZZA);
    }

    public ElementZigZag drawZigZagTo(PointF pEnd, float altezza)
    {
        return drawZigZagTo(pEnd, altezza, Ricetta.DEFAULT_PASSO_ZIGZAG);
    }

    public ElementZigZag drawZigZagTo(PointF pEnd, float altezza, float passo)
    {
        return drawOrInsertZigZag(pEndLast, pEnd, altezza, passo);
    }

    public ElementFeed drawFeedTo(PointF pEnd)
    {
        return drawOrInsertFeed(pEndLast, pEnd);
    }



    private ElementLine drawOrInsertLine(PointF pStart, PointF pEnd, float passo)
    {
        ElementLine elementLine = new ElementLine();
        elementLine.passo = passo;

        JamPointStep activeStep = getActiveStep();

        if (activeStep==null || isLastStep(activeStep))
        {
            drawElement(elementLine, pStart, pEnd);
            return elementLine;
        }
        else
            return (ElementLine)insertElementAtActiveStep(elementLine, pEnd);
    }

    private ElementZigZag drawOrInsertZigZag(PointF pStart, PointF pEnd, float altezza, float passo)
    {
        ElementZigZag elementZigZag = new ElementZigZag();
        elementZigZag.altezza = altezza;
        elementZigZag.passo = passo;

        JamPointStep activeStep = getActiveStep();

        if (activeStep==null || isLastStep(activeStep))
        {
            drawElement(elementZigZag, pStart, pEnd);
            return elementZigZag;
        }
        else
            return (ElementZigZag)insertElementAtActiveStep(elementZigZag, pEnd);
    }

    private ElementArc drawOrInsertArc(PointF pStart, PointF pMiddle, PointF pEnd, float passo)
    {
        ElementArc elementArc = new ElementArc();
        elementArc.pMiddle = new PointF(pMiddle.x, pMiddle.y);
        elementArc.passo = passo;

        JamPointStep activeStep = getActiveStep();

        if (activeStep==null || isLastStep(activeStep))
        {
            drawElement(elementArc, pStart, pEnd);
            return elementArc;
        }
        else
            return (ElementArc)insertElementAtActiveStep(elementArc, pEnd);
    }

    private ElementFeed drawOrInsertFeed(PointF pStart, PointF pEnd)
    {
        ElementFeed elementFeed = new ElementFeed();

        JamPointStep activeStep = getActiveStep();

        if (activeStep==null || isLastStep(activeStep))
        {
            drawElement(elementFeed, pStart, pEnd);
            return elementFeed;
        }
        else
            return (ElementFeed)insertElementAtActiveStep(elementFeed, pEnd);
    }

    private void drawElement(Element element, PointF pStart, PointF pEnd)
    {
        saveUndo();

        element.pStart = new PointF(pStart.x, pStart.y);
        element.pEnd = new PointF(pEnd.x, pEnd.y);
        element.ricetta = this;
        element.createSteps();
        elements.add(element);

        pEndLast = pEnd;
        goToLastStep();
    }




    //********************  METODI DI INSERT ELEMENT ********************

//    public Element insertLineTo(PointF pEnd)
//    {
//        return insertLine(pEnd, 3.0f);
//    }
//
//    public Element insertLineTo(PointF pEnd, float passo)
//    {
//        return insertLine(pEnd, passo);
//    }
//
//    private Element insertLine(PointF pEnd, float passo)
//    {
//        ElementLine elementLine = new ElementLine();
//        elementLine.passo = passo;
//        return insertElementAtActiveStep(elementLine, pEnd);
//    }


//    public Element insertArcTo(PointF pMiddle, PointF pEnd)
//    {
//        return insertArc(pMiddle, pEnd, 3.0f);
//    }
//
//    public Element insertArcTo(PointF pMiddle, PointF pEnd, float passo)
//    {
//        return insertArc(pMiddle, pEnd, passo);
//    }
//
//    private Element insertArc(PointF pMiddle, PointF pEnd, float passo)
//    {
//        ElementArc elementArc = new ElementArc();
//        elementArc.pMiddle = new PointF(pMiddle.x, pMiddle.y);
//        elementArc.passo = passo;
//        return insertElementAtActiveStep(elementArc, pEnd);
//    }


//    public Element insertZigZagTo(PointF pEnd)
//    {
//        return insertZigZag(pEnd, 3.0f, 3.0f);
//    }
//
//    public Element insertZigZagTo(PointF pEnd, float passo)
//    {
//        return insertZigZag(pEnd, passo, 3.0f);
//    }
//
//    public Element insertZigZagTo(PointF pEnd, float passo, float altezza)
//    {
//        return insertZigZag(pEnd, passo, altezza);
//    }
//
//    private Element insertZigZag(PointF pEnd, float passo, float altezza)
//    {
//        ElementZigZag elementZigZag = new ElementZigZag();
//        elementZigZag.altezza = altezza;
//        elementZigZag.passo = passo;
//        return insertElementAtActiveStep(elementZigZag, pEnd);
//    }


//    public Element insertFeedTo(PointF pEnd)0
//    {
//        return insertFeed(pEnd);
//    }
//
//    private Element insertFeed(PointF pEnd)
//    {
//        ElementFeed elementFeed = new ElementFeed();
//        return insertElementAtActiveStep(elementFeed, pEnd);
//    }


    private Element insertElementAtActiveStep(Element element, PointF pEnd)
    {
        JamPointStep step = getActiveStep();
		float passo_element = step.element.passo;   //mi serve per eventuale elemento di raccordo tipo linea     
        if (step!=null)
        {
            saveUndo();
            boolean isLastElement = isLastElement(step.element);
            boolean isLastStepOfElement = isLastStepOfElement(step);
            Element nextElement = isLastElement ? null : getNextElement(step.element);
            boolean isNextElementFeed = nextElement == null ? false : nextElement instanceof ElementFeed;
            int indexElement = elements.indexOf(step.element)+1;
            PointF pSplit = new PointF(step.p.x, step.p.y);

            if (isLastStepOfElement && isNextElementFeed) //ELEMENTO SUCCESSIVO FEED: DEVE INSERIRE PRIMA E MODIFICARE IL PUNTO INIZIALE DI QUESTO FEED
            {
                nextElement.pStart = new PointF(pEnd.x, pEnd.y);
                nextElement.createSteps();
            }

            //SPEZZA L'ELEMENTO
int num = splitElementAtStep(step, 1);

            if (num == -1) //ret=-1 = NESSUNA DIVISIONE (PRIMO STEP); ret=0 NESSUNA DIVISIONE (ULTIMO STEP); ret=1 E' STATO SUDDIVISO IN 2 SOTTOELEMENTI
                indexElement--;
            else if (num == 1)
                isNextElementFeed = false; //ELEMENTO SPEZZATO: LA 2° PARTE SARA' IL NUOVO ELEMENTO SUCCESSIVO, NON SARA' MAI UN FEED!


            insertElement(element, pSplit, new PointF(pEnd.x, pEnd.y), step.element, indexElement);

            if (element.steps.size()!=0)
            {
                JamPointStep newLastStep = element.steps.get(element.steps.size()-1);

                if (num == 0){  //END SOTTOELEMENTO A E START SOTTOELEMENTO B CONDIVIDONO LO STEP PASSATO

                    double distance = Tools.getDistance(nextElement.pStart, nextElement.pEnd); //DOPO LA CHIAMATA A splitElementAtStep step CONTIENE LE COORDINATE D ELLO STEP APPENA SEPARATO, POSSO QUINDI TROVARE LA DISTANZA!

                    if (distance < 0.3)
                    {
                        JamPointStep nextElementStartStep = nextElement.steps.get(0);   //trovo punto dell'elemento next
                        int elementIndex = elements.indexOf(nextElementStartStep.element); //trovo id dell'elemento next
                        deleteElement(elementIndex);    //lo cancello

                    }

                }else {
                    if (!isNextElementFeed && (!(isLastElement && isLastStepOfElement))) {

                        //AGGIUNGE UN FEED SE NECESSARIO

                        Element elementLink = new ElementLine(); //ELEMEMTO DI RACCORDO
                        double distance = Tools.getDistance(newLastStep, step); //DOPO LA CHIAMATA A splitElementAtStep step CONTIENE LE COORDINATE D ELLO STEP APPENA SEPARATO, POSSO QUINDI TROVARE LA DISTANZA!

                        if (distance >= MIN_FEED_DISTANCE)
                            elementLink = new ElementFeed(); //SE NECESSARIO CREA IL FEED

                        insertElement(elementLink, new PointF(newLastStep.p.x, newLastStep.p.y), new PointF(step.p.x, step.p.y), newLastStep.element, indexElement + 1);

                        if(elementLink instanceof ElementLine) {
                            elementLink.passo = passo_element;  //metto il passo del padre
                            elementLink.createSteps();
                        }

                    }
                }
                activeStepIndex = getStepIndex(newLastStep); //IMPOSTA NUOVO INDICE ATTIVO SULLO STEP FINALE DEL NUOVO ELEMENTO
                return element;
            }
        }
        return null;
    }





    //********************  METODI DI GESTIONE ENTITIES ********************

    public List<Element> selectPreviousEntity()
    {
        List<Element> ret = null;

        selectEntity(false, false);
        List<String> indexes = new ArrayList<String>(entities.keySet());
        int index = indexes.indexOf(selectedEntityCode);

        if (index <= 0 && indexes.size()!=0)
            index = indexes.size();

        if (index >0)
        {
            selectedEntityCode = indexes.get(index-1);
            ret = entities.get(selectedEntityCode);
            selectEntity(true, true);
        }

        return ret;
    }

    public List<Element> selectNextEntity()
    {
        List<Element> ret = null;

        selectEntity(false, false);
        List<String> indexes = new ArrayList<String>(entities.keySet());
        int index = indexes.indexOf(selectedEntityCode); //SE NON LA TROVA RESTITUISCE -1

        if (index == indexes.size()-1 && indexes.size()!=0)
            index = -1; //SE ULTIMO O NESSUNO SELEZIONATO VA ALLA PRIMA

        if (indexes.size()!=0)
        {
            selectedEntityCode = indexes.get(index+1);
            ret = entities.get(selectedEntityCode);
            selectEntity(true, true);
        }

        return ret;
    }

    public PointF getSelectedEntityStartPoint()
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return null;

        return entity.get(0).pStart;
    }

    public PointF getSelectedEntityEndPoint()
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return null;

        return entity.get(entity.size()-1).pEnd;
    }

    //MUOVE UN ENTITY SPOSTANDO IN BLOCCO TUTTI I SUOI ELEMENTI
    public void moveSelectedEntity(float dx, float dy)
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        saveUndo();

        if (entity.size()==1)
            moveElement(elements.indexOf(entity.get(0)), dx, dy); //HA UN SOLO ELEMENTO, LO MUOVE
        else if (entity.size()>1)
        {
            int startElementIndex = elements.indexOf(entity.get(0));
            int endElementIndex = elements.indexOf(entity.get(entity.size()-1));
            moveElements(startElementIndex, endElementIndex, dx, dy, 0, 0); //??
        }
    }

    public int getSelectedEntityFirstElementIndex()
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return -1;

        return elements.indexOf(entity.get(0));
    }

    public int getSelectedEntityLastElementIndex()
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return -1;

        return elements.indexOf(entity.get(entity.size()-1));
    }

    //ELIMINA UN ENTITY ELIMINANDO IN BLOCCO TUTTI I SUOI ELEMENTI
    public void deleteSelectedEntity()
    {
        loadEntities();
        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        saveUndo();

        if (entity.size()==1)
            deleteElement(elements.indexOf(entity.get(0))); //HA UN SOLO ELEMENTO, LO ELIMINA
        else if (entity.size()>1)
        {
            int startElementIndex = elements.indexOf(entity.get(0));
            int endElementIndex = elements.indexOf(entity.get(entity.size()-1));
            deleteElements(startElementIndex, endElementIndex);
        }
    }

    public void explodeSelectedEntity()
    {
        loadEntities();

        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        saveUndo();

        selectEntity(false, false);

        for(Element element : entity)
            element.entity = java.util.UUID.randomUUID().toString();

        selectedEntityCode = entity.get(0).entity;
        selectEntity(true, true);
    }

    public void modifySelectedEntity(float passo)
    {
        loadEntities();

        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        saveUndo();

        for(Element element : entity)
            if (element instanceof ElementLine || element instanceof ElementArc || element instanceof ElementZigZag)
            {
                element.passo = passo;
                element.createSteps();
            }
    }

    public void modifySelectedEntityZigZag(float altezza)
    {
        loadEntities();

        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        saveUndo();

        for(Element element : entity)
            if (element instanceof ElementZigZag)
            {
                ((ElementZigZag) element).altezza = altezza;
                element.createSteps();
            }
    }

    //TRASFORMA UNA ENTITY IN UNA LINEA DAL SUO PUNTO INIZIALE AL SUO PUNTO FINALE
    public void transformSelectedEntityToLine()
    {
        ElementLine elementNew = new ElementLine();
        transformSelectedEntity(elementNew);
    }

    //TRASFORMA UNA ENTITY IN UNO ZIGZAG DAL SUO PUNTO INIZIALE AL SUO PUNTO FINALE
    public void transformSelectedEntityToZigZag()
    {
        ElementZigZag elementNew = new ElementZigZag();
        transformSelectedEntity(elementNew);
    }

    //TRASFORMA UNA ENTITY IN UN FEED DAL SUO PUNTO INIZIALE AL SUO PUNTO FINALE
    public void transformSelectedEntityToFeed()
    {
        ElementFeed elementNew = new ElementFeed();
        transformSelectedEntity(elementNew);
    }

    //TRASFORMA UNA ENTITY IN UN ARCO DAL SUO PUNTO INIZIALE AL SUO PUNTO FINALE E CON IL PUNTO MEDIANO SPECIFICATO
    public void transformSelectedEntityToArc(PointF pMiddle)
    {
        ElementArc elementNew = new ElementArc();

        if (pMiddle!=null)
            elementNew.pMiddle = new PointF(pMiddle.x, pMiddle.y);

        transformSelectedEntity(elementNew);
    }


    private void transformSelectedEntity(Element elementNew)
    {
        try {
            loadEntities();
            List<Element> entity = entities.get(selectedEntityCode);

            if (entity == null || entity.size() == 0)
                return;

            if (entity.size() > 0) {
                saveUndo(); //NON VIENE CHIAMATO DA UN METODO PUBBLICO!

                int startElementIndex = elements.indexOf(entity.get(0));
                int endElementIndex = elements.indexOf(entity.get(entity.size() - 1));

                PointF p1 = entity.get(0).pStart;
                PointF p2 = entity.get(entity.size() - 1).pEnd;

                insertElement(elementNew, p1, p2, entity.get(0), startElementIndex);
                deleteElements(startElementIndex + 1, endElementIndex + 1);

                selectEntity(true, true);
            }
        }catch (Exception e){}
    }

    //SELEZIONA/DESELEZIONA TUTTI GLI ELEMENTI DI UNA ENTITY
    private void selectEntity(boolean select, boolean resetActiveStep)
    {
        loadEntities();

        List<Element> entity = entities.get(selectedEntityCode);

        if (entity == null || entity.size()==0)
            return;

        for(Element element : entity)
            element.isSelected = select;

        if (select && resetActiveStep && entity.get(0).steps.size()!=0)
            activeStepIndex = getStepIndex(entity.get(0).steps.get(0)); //ATTIVA IL PRIMO STEP SE ESISTE
    }

    //RIGENERA L'ELENCO ENTITIES IN BASE AL UUID DI OGNI ELEMENTO
    private void loadEntities()
    {
        entities = new LinkedHashMap<>();

        for(Element element: elements)
        {
            if(element.entity == null)
                element.entity = java.util.UUID.randomUUID().toString();

            if (!entities.containsKey(element.entity))
                entities.put(element.entity, new ArrayList<Element>());

            entities.get(element.entity).add(element);
        }
    }



    //******************** METODI GENERALI DI RICETTA ********************

    //MUOVE TUTTA LA RICETTA COMPLETA
    public void move(float dx, float dy)
    {
        saveUndo();

        for(Element element:elements)
            element.move(dx, dy);
    }

    //MODIFICA IL PASSO IN TUTTI GLI ELEMENTI LINE, ARC DELLA RICETTA
    public void modify(float passo)
    {
        saveUndo();

        for(Element element: elements)
            if (element instanceof ElementLine || element instanceof ElementArc)
            {
                element.passo = passo;
                element.createSteps();
            }
    }

    //MODIFICA L'ALTEZZA IN TUTTI GLI ELEMENTI ZIGZAG DELLA RICETTA
    public void modifyZigZag(float altezza)
    {
        saveUndo();

        for(Element element: elements)
            if (element instanceof ElementZigZag)
            {
                ((ElementZigZag) element).altezza = altezza;
                element.createSteps();
            }
    }

    public void draw(Canvas canvas, float scale, boolean drawSteps)
    {
        paint.setColor(Color.WHITE);
        canvas.scale(1f,1f);
        canvas.drawPaint(paint);

        for(Element element:elements)
        {
            paint.setStyle(Paint.Style.STROKE);

            if (element instanceof ElementFeed)
                paint.setColor(element.isSelected ? Color.MAGENTA : Color.LTGRAY);
            else
                paint.setColor(element.isSelected ? Color.MAGENTA : Color.BLUE);

            if (element instanceof ElementLine || element instanceof ElementZigZag || element instanceof ElementFeed)
                canvas.drawLine(element.pStart.x * scale,
                        element.pStart.y * scale,
                        element.pEnd.x * scale,
                        element.pEnd.y * scale, paint);

            if (element instanceof ElementArc)
            {
                RectF rect = new RectF();
                rect.left = ((ElementArc) element).left * scale;
                rect.top = ((ElementArc) element).top * scale;
                rect.right = ((ElementArc) element).right * scale;
                rect.bottom = ((ElementArc) element).bottom * scale;

                float sweep = 0;
                if(((ElementArc) element).startAngle > ((ElementArc) element).endAngle)
                    sweep = Math.abs(((ElementArc) element).startAngle - ((ElementArc) element).endAngle); //caso end = 155 start = 186
                else
                    sweep = (360- ((ElementArc) element).endAngle) + ((ElementArc) element).startAngle;  //nel caso limite dove end =340 start
                canvas.drawArc(rect, ((ElementArc) element).endAngle, sweep, false, paint);
            }

            if (drawSteps)
            {
                paint.setColor(element.isSelected ? Color.MAGENTA : Color.RED);
                paint.setStyle(Paint.Style.FILL);

                for(JamPointStep step: element.steps)
                {
                    if (getStepIndex(step) == activeStepIndex)
                        paint.setColor(Color.BLACK);
                    else
                        paint.setColor(Color.RED);

                    canvas.drawCircle(step.p.x*scale, step.p.y*scale, 3, paint);
                }
            }
        }
    }

    public void open(File file) throws IOException, XmlPullParserException
    {
        this.file = file;
        FileInputStream fis = new FileInputStream(file);// context.openFileInput(xmlFile);
        InputStreamReader isr = new InputStreamReader(fis);
        char[] inputBuffer = new char[fis.available()];
        isr.read(inputBuffer);
        String data = new String(inputBuffer);
        isr.close();
        fis.close();

        XmlPullParserFactory factory = null;
        factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(data));
        int eventType = xpp.getEventType();

        Element lastElement = null;

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                String tagName = xpp.getName();

                if (tagName.equals(RICETTA_TAG))
                {
                    int f = xpp.getAttributeCount();
                   descrizioneRicetta = xpp.getAttributeValue(null, DESCRIZIONE_RICETTA_ATTRIBUTE);
                    numeroRicetta = Integer.parseInt(xpp.getAttributeValue(null, NUMERO_RICETTA_ATTRIBUTE));
                    pcX = Float.parseFloat(xpp.getAttributeValue(null, PCX_RICETTA_ATTRIBUTE));
                    pcY = Float.parseFloat(xpp.getAttributeValue(null, PCY_RICETTA_ATTRIBUTE));
                    try {
                        UdfVelLavRPM = Integer.parseInt(xpp.getAttributeValue(null, VEL_LAV_RPM_ATTRIBUTE));
                        UdfPuntiVelIni = Float.parseFloat(xpp.getAttributeValue(null, PUNTI_VEL_INI_ATTRIBUTE));
                        UdfVelIniRPM = Integer.parseInt(xpp.getAttributeValue(null, VEL_INI_ATTRIBUTE));
                        UdfPuntiVelRall = Float.parseFloat(xpp.getAttributeValue(null, PUNTI_VEL_RALL_ATTRIBUTE));
                        UdfVelRallRPM = Integer.parseInt(xpp.getAttributeValue(null, VEL_RALL_ATTRIBUTE));
                        Udf_FeedG0 = Integer.parseInt(xpp.getAttributeValue(null, FEEDG0_ATTRIBUTE));
                        Udf_ValTensioneT1 = Integer.parseInt(xpp.getAttributeValue(null, VAL_TENSIONE_T1_ATTRIBUTE));
                        Udf_20 = Integer.parseInt(xpp.getAttributeValue(null, UDF20));
                        Udf_ValElettrocalamitaSopra = Integer.parseInt(xpp.getAttributeValue(null, VAL_ELETTROCALAMITA_SOPRA_ATTRIBUTE));
                        Udf_ValElettrocalamitaSotto = Integer.parseInt(xpp.getAttributeValue(null, VAL_ELETTROCALAMITA_SOTTO_ATTRIBUTE));
                        Udf_23 = Integer.parseInt(xpp.getAttributeValue(null, UDF23));
                        Udf_24 = Integer.parseInt(xpp.getAttributeValue(null, UDF24));
                        Udf_25 = Integer.parseInt(xpp.getAttributeValue(null, UDF25));
                        Udf_26 = Integer.parseInt(xpp.getAttributeValue(null, UDF26));
                        Udf_27 = Integer.parseInt(xpp.getAttributeValue(null, UDF27));
                        Udf_28 = Integer.parseInt(xpp.getAttributeValue(null, UDF28));
                        Udf_29 = Integer.parseInt(xpp.getAttributeValue(null, UDF29));
                        Udf_30 = Integer.parseInt(xpp.getAttributeValue(null, UDF30));
                        Udf_SequenzaPiegatore_chiusura = Integer.parseInt(xpp.getAttributeValue(null, SEQUENZAPIEGATORE_CHIUSURA_ATTRIBUTE));
                        Udf_SequenzaPiegatore_apetura = Integer.parseInt(xpp.getAttributeValue(null, SEQUENZAPIEGATORE_APERTURA_ATTRIBUTE));

                    }catch (Exception e)
                    {
                        //ho exception nel caso carico una vecchia ricetta senza questi attributi, al successivo salvataggio della ricetta
                        //gli attributi verranno messi con valori di dfault anche se non verranno mai utilizzati
                    }
                    elements = new ArrayList<Element>();
                }
                else if (tagName.equals(ELEMENT_LINE_TAG))
                {
                    lastElement = new ElementLine();
                    lastElement.pStart.x = Float.parseFloat(xpp.getAttributeValue(null, X_START_ELEMENT_ATTRIBUTE));
                    lastElement.pStart.y = Float.parseFloat(xpp.getAttributeValue(null, Y_START_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.x = Float.parseFloat(xpp.getAttributeValue(null, X_END_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.y = Float.parseFloat(xpp.getAttributeValue(null, Y_END_ELEMENT_ATTRIBUTE));
                    lastElement.passo = Float.parseFloat(xpp.getAttributeValue(null, PASSO_ATTRIBUTE));
                    lastElement.entity = xpp.getAttributeValue(null, ENTITY_ATTRIBUTE);
                    lastElement.roundValues();
                    lastElement.ricetta = this;
                    elements.add(lastElement);
                }
                else if (tagName.equals(ELEMENT_ZIGZAG_TAG))
                {
                    lastElement = new ElementZigZag();
                    lastElement.pStart.x = Float.parseFloat(xpp.getAttributeValue(null, X_START_ELEMENT_ATTRIBUTE));
                    lastElement.pStart.y = Float.parseFloat(xpp.getAttributeValue(null, Y_START_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.x = Float.parseFloat(xpp.getAttributeValue(null, X_END_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.y = Float.parseFloat(xpp.getAttributeValue(null, Y_END_ELEMENT_ATTRIBUTE));
                    ((ElementZigZag)lastElement).altezza = Float.parseFloat(xpp.getAttributeValue(null, ALTEZZA_ZIGZAG_ATTRIBUTE));
                    lastElement.passo = Float.parseFloat(xpp.getAttributeValue(null, PASSO_ATTRIBUTE));
                    lastElement.entity = xpp.getAttributeValue(null, ENTITY_ATTRIBUTE);
                    lastElement.roundValues();
                    lastElement.ricetta = this;
                    elements.add(lastElement);
                }
                else if (tagName.equals(ELEMENT_ARC_TAG))
                {
                    lastElement = new ElementArc();
                    lastElement.pStart.x = Float.parseFloat(xpp.getAttributeValue(null, X_START_ELEMENT_ATTRIBUTE));
                    lastElement.pStart.y = Float.parseFloat(xpp.getAttributeValue(null, Y_START_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.x = Float.parseFloat(xpp.getAttributeValue(null, X_END_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.y = Float.parseFloat(xpp.getAttributeValue(null, Y_END_ELEMENT_ATTRIBUTE));
                    ((ElementArc)lastElement).pMiddle.x = Float.parseFloat(xpp.getAttributeValue(null, X_MIDDLE_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).pMiddle.y = Float.parseFloat(xpp.getAttributeValue(null, Y_MIDDLE_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).left = Float.parseFloat(xpp.getAttributeValue(null, LEFT_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).top = Float.parseFloat(xpp.getAttributeValue(null, TOP_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).right = Float.parseFloat(xpp.getAttributeValue(null, RIGHT_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).bottom = Float.parseFloat(xpp.getAttributeValue(null, BOTTOM_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).startAngle = Float.parseFloat(xpp.getAttributeValue(null, START_ARC_ATTRIBUTE));
                    ((ElementArc)lastElement).endAngle = Float.parseFloat(xpp.getAttributeValue(null, END_ARC_ATTRIBUTE));
                    lastElement.passo = Float.parseFloat(xpp.getAttributeValue(null, PASSO_ATTRIBUTE));
                    lastElement.entity = xpp.getAttributeValue(null, ENTITY_ATTRIBUTE);
                    lastElement.roundValues();
                    lastElement.ricetta = this;
                    elements.add(lastElement);
                }
                else if (tagName.equals(ELEMENT_FEED_TAG))
                {
                    lastElement = new ElementFeed();
                    lastElement.pStart.x = Float.parseFloat(xpp.getAttributeValue(null, X_START_ELEMENT_ATTRIBUTE));
                    lastElement.pStart.y = Float.parseFloat(xpp.getAttributeValue(null, Y_START_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.x = Float.parseFloat(xpp.getAttributeValue(null, X_END_ELEMENT_ATTRIBUTE));
                    lastElement.pEnd.y = Float.parseFloat(xpp.getAttributeValue(null, Y_END_ELEMENT_ATTRIBUTE));
                    lastElement.entity = xpp.getAttributeValue(null, ENTITY_ATTRIBUTE);
                    lastElement.roundValues();
                    lastElement.ricetta = this;
                    elements.add(lastElement);
                }
                else if (tagName.equals(STEP_TAG))
                {
                    JamPointStep step = new JamPointStep();
                    step.p.x = Float.parseFloat(xpp.getAttributeValue(null, X_STEP_ATTRIBUTE));
                    step.p.y = Float.parseFloat(xpp.getAttributeValue(null, Y_STEP_ATTRIBUTE));
                    step.roundValues();
                    step.element = lastElement;
                    lastElement.steps.add(step);
                }
                else if (tagName.equals(CODE_TAG))
                {
                    JamPointCode code = new JamPointCode();
                    code.tipoCodice = JamPointCode.TipiCodici.valueOf(xpp.getAttributeValue(null, TIPO_CODE_ATTRIBUTE));
                    if(code.tipoCodice == SPEED_M8 || code.tipoCodice == TENS_M8)
                        code.valore_M8 = (xpp.getAttributeValue(null, VALORE_CODE_ATTRIBUTE));
                    else
                        code.valore = JamPointCode.TipiValori.valueOf(xpp.getAttributeValue(null, VALORE_CODE_ATTRIBUTE));
                    int elementIndex = Integer.parseInt(xpp.getAttributeValue(null, ELEMENTINDEX_CODE_ATTRIBUTE));
                    int stepIndex = Integer.parseInt(xpp.getAttributeValue(null, STEPINDEX_CODE_ATTRIBUTE));
                    code.step = getStep(elementIndex, stepIndex); //PUNTA ALLO STEP INDICATO, SE GLI index NON SONO VALIDI step -> null
                    codes.add(code);
                }

                if(lastElement != null && lastElement.entity == null)
                    lastElement.entity = java.util.UUID.randomUUID().toString();
            }

            eventType = xpp.next();
        }
    }

    public void save() throws IOException
    {
        save(file);
    }

    public void save(File file) throws IOException
    {
        FileOutputStream fileos = new FileOutputStream(file);

        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);

        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

        xmlSerializer.startTag(null, RICETTA_TAG);
        xmlSerializer.attribute(null, DESCRIZIONE_RICETTA_ATTRIBUTE, descrizioneRicetta);
        xmlSerializer.attribute(null, NUMERO_RICETTA_ATTRIBUTE, String.valueOf(numeroRicetta));
        xmlSerializer.attribute(null, PCX_RICETTA_ATTRIBUTE, String.valueOf(pcX));
        xmlSerializer.attribute(null, PCY_RICETTA_ATTRIBUTE, String.valueOf(pcY));
        xmlSerializer.attribute(null, VEL_LAV_RPM_ATTRIBUTE, String.valueOf(UdfVelLavRPM));
        xmlSerializer.attribute(null, PUNTI_VEL_INI_ATTRIBUTE, String.valueOf(UdfPuntiVelIni));
        xmlSerializer.attribute(null, VEL_INI_ATTRIBUTE, String.valueOf(UdfVelIniRPM));
        xmlSerializer.attribute(null, PUNTI_VEL_RALL_ATTRIBUTE, String.valueOf(UdfPuntiVelRall));
        xmlSerializer.attribute(null, VEL_RALL_ATTRIBUTE, String.valueOf(UdfVelRallRPM));
        xmlSerializer.attribute(null, FEEDG0_ATTRIBUTE, String.valueOf(Udf_FeedG0));
        xmlSerializer.attribute(null, VAL_TENSIONE_T1_ATTRIBUTE, String.valueOf(Udf_ValTensioneT1));
        xmlSerializer.attribute(null, UDF20, String.valueOf(Udf_20));
        xmlSerializer.attribute(null, VAL_ELETTROCALAMITA_SOPRA_ATTRIBUTE, String.valueOf(Udf_ValElettrocalamitaSopra));
        xmlSerializer.attribute(null, VAL_ELETTROCALAMITA_SOTTO_ATTRIBUTE, String.valueOf(Udf_ValElettrocalamitaSotto));
        xmlSerializer.attribute(null, UDF23, String.valueOf(Udf_23));
        xmlSerializer.attribute(null, UDF24, String.valueOf(Udf_24));
        xmlSerializer.attribute(null, UDF25, String.valueOf(Udf_25));
        xmlSerializer.attribute(null, UDF26, String.valueOf(Udf_26));
        xmlSerializer.attribute(null, UDF27, String.valueOf(Udf_27));
        xmlSerializer.attribute(null, UDF28, String.valueOf(Udf_28));
        xmlSerializer.attribute(null, UDF29, String.valueOf(Udf_29));
        xmlSerializer.attribute(null, UDF30, String.valueOf(Udf_30));
        xmlSerializer.attribute(null, SEQUENZAPIEGATORE_CHIUSURA_ATTRIBUTE, String.valueOf(Udf_SequenzaPiegatore_chiusura));
        xmlSerializer.attribute(null, SEQUENZAPIEGATORE_APERTURA_ATTRIBUTE, String.valueOf(Udf_SequenzaPiegatore_apetura));

        for (Element element : elements)
        {
            if (element instanceof ElementLine)
                xmlSerializer.startTag(null, ELEMENT_LINE_TAG);
            else if (element instanceof ElementZigZag)
                xmlSerializer.startTag(null, ELEMENT_ZIGZAG_TAG);
            else if (element instanceof  ElementArc)
                xmlSerializer.startTag(null, ELEMENT_ARC_TAG);
            else if (element instanceof  ElementFeed)
                xmlSerializer.startTag(null, ELEMENT_FEED_TAG);

            xmlSerializer.attribute(null, X_START_ELEMENT_ATTRIBUTE, String.valueOf(element.pStart.x));
            xmlSerializer.attribute(null, Y_START_ELEMENT_ATTRIBUTE, String.valueOf(element.pStart.y));
            xmlSerializer.attribute(null, X_END_ELEMENT_ATTRIBUTE, String.valueOf(element.pEnd.x));
            xmlSerializer.attribute(null, Y_END_ELEMENT_ATTRIBUTE, String.valueOf(element.pEnd.y));
            xmlSerializer.attribute(null, ENTITY_ATTRIBUTE, String.valueOf(element.entity));

            if (element instanceof ElementZigZag)
            {
                xmlSerializer.attribute(null, ALTEZZA_ZIGZAG_ATTRIBUTE, String.valueOf(((ElementZigZag)element).altezza));
            }

            else if (element instanceof  ElementArc)
            {
                xmlSerializer.attribute(null, X_MIDDLE_ARC_ATTRIBUTE, String.valueOf(((ElementArc)element).pMiddle.x));
                xmlSerializer.attribute(null, Y_MIDDLE_ARC_ATTRIBUTE, String.valueOf(((ElementArc)element).pMiddle.y));
                xmlSerializer.attribute(null, LEFT_ARC_ATTRIBUTE, String.valueOf(((ElementArc) element).left));
                xmlSerializer.attribute(null, TOP_ARC_ATTRIBUTE, String.valueOf(((ElementArc) element).top));
                xmlSerializer.attribute(null, RIGHT_ARC_ATTRIBUTE, String.valueOf(((ElementArc) element).right));
                xmlSerializer.attribute(null, BOTTOM_ARC_ATTRIBUTE, String.valueOf(((ElementArc) element).bottom));
                xmlSerializer.attribute(null, START_ARC_ATTRIBUTE, String.valueOf(((ElementArc)element).startAngle));
                xmlSerializer.attribute(null, END_ARC_ATTRIBUTE, String.valueOf(((ElementArc)element).endAngle));
            }

            if (element instanceof ElementLine || element instanceof  ElementArc || element instanceof  ElementZigZag)
            {
                xmlSerializer.attribute(null, PASSO_ATTRIBUTE, String.valueOf(element.passo));
            }

            for (JamPointStep step : element.steps)
            {
                xmlSerializer.startTag(null, STEP_TAG);
                xmlSerializer.attribute(null, X_STEP_ATTRIBUTE, String.valueOf(step.p.x));
                xmlSerializer.attribute(null, Y_STEP_ATTRIBUTE, String.valueOf(step.p.y));
                xmlSerializer.endTag(null, STEP_TAG);
            }

            if (element instanceof ElementLine)
                xmlSerializer.endTag(null, ELEMENT_LINE_TAG);
            else if (element instanceof ElementZigZag)
                xmlSerializer.endTag(null, ELEMENT_ZIGZAG_TAG);
            else if (element instanceof  ElementArc)
                xmlSerializer.endTag(null, ELEMENT_ARC_TAG);
            else if (element instanceof  ElementFeed)
                xmlSerializer.endTag(null, ELEMENT_FEED_TAG);
        }

        for (JamPointCode code : codes)
        {
            xmlSerializer.startTag(null, CODE_TAG);
            xmlSerializer.attribute(null, TIPO_CODE_ATTRIBUTE, String.valueOf(code.tipoCodice));
            if(code.tipoCodice == SPEED_M8 || code.tipoCodice == TENS_M8)
                xmlSerializer.attribute(null, VALORE_CODE_ATTRIBUTE, String.valueOf(code.valore_M8));
            else
            xmlSerializer.attribute(null, VALORE_CODE_ATTRIBUTE, String.valueOf(code.valore));
            xmlSerializer.attribute(null, ELEMENTINDEX_CODE_ATTRIBUTE, String.valueOf(code.getElementIndex()));
            xmlSerializer.attribute(null, STEPINDEX_CODE_ATTRIBUTE, String.valueOf(code.getStepIndex()));
            xmlSerializer.endTag(null, CODE_TAG);
        }

        xmlSerializer.endTag(null, RICETTA_TAG);

        xmlSerializer.endDocument();
        xmlSerializer.flush();

        String dataWrite = writer.toString();
        fileos.write(dataWrite.getBytes());
        fileos.close();
    }

    public void exportToUsr(File file) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0,i);

        sb.append("[Name]");
        sb.append(newLine);
        sb.append(name);
        sb.append(newLine);
        sb.append(descrizioneRicetta);
        sb.append(newLine);
        sb.append("[Numero]");
        sb.append(newLine);
        sb.append(numeroRicetta);
        sb.append(newLine);
        sb.append("[Header VA Len]");
        sb.append(newLine);
        sb.append("3");
        sb.append(newLine);
        sb.append("[Data VA Len]");
        sb.append(newLine);
        sb.append("21");
        sb.append(newLine);
        sb.append("[Base Matrix]");
        sb.append(newLine);
        sb.append("9800");
        sb.append(newLine);
        sb.append("[Header Matrix]");
        sb.append(newLine);
        sb.append("9801");
        sb.append(newLine);
        sb.append("[Header]");
        sb.append(newLine);
        sb.append("VQ:"+pcX);
        sb.append(newLine);
        sb.append("VQ:"+pcY);
        sb.append(newLine);
        sb.append("VQ:-228.4071");
        sb.append(newLine);
        sb.append("VQ:-140.6177");
        sb.append(newLine);
        sb.append("VQ:-73.5009");
        sb.append(newLine);
        sb.append("VQ:2.2000");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("[Header End]");
        sb.append(newLine);

        sb.append(newLine);
        sb.append("[Step]");
        sb.append(newLine);
        sb.append("VN:1");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("VQ:0");
        sb.append(newLine);
        sb.append("[End Step]");
        sb.append(newLine);

        List<JamPoint> points = getPoints();
        for(JamPoint point: points) {
            if(point instanceof  JamPointStep){
                JamPointStep jamPointStep = (JamPointStep) point;
                if(jamPointStep.isDuplicateStep)
                    continue;
            }
               sb.append(newLine);
               sb.append("[Step]");
               sb.append(newLine);
               sb.append("VN:" + point.getVN());
               sb.append(newLine);
               sb.append("VQ:" + point.getVQ1());
               sb.append(newLine);
               sb.append("VQ:" + point.getVQ2());
               sb.append(newLine);
               sb.append("VQ:" + point.getVQ3());
               sb.append(newLine);
               sb.append("VQ:" + point.getVQ4());
               sb.append(newLine);
               sb.append("[End Step]");
               sb.append(newLine);


        }

        FileOutputStream fileos= new FileOutputStream(file);
        fileos.write(sb.toString().getBytes());
        fileos.close();
    }

    public void exportToUdf(File file)throws IOException
    {


        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0,i);

        sb.append("[StartUdf]");
        sb.append(newLine);
        sb.append(newLine);
        sb.append("[StartMatrixStep]");
        sb.append(newLine);
        sb.append("MatrixId = 111");
        sb.append(newLine);
        sb.append("MatrixName ="+'"'+ "C1_Base_Profilo"+'"');
        sb.append(newLine);
        sb.append("VN1901 = 0");
        sb.append(newLine);
        sb.append("VQ1901 = 0.000");
        sb.append(newLine);
        sb.append("VQ1902 = 0.000");
        sb.append(newLine);
        sb.append("[EndMatrixStep]");
        sb.append(newLine);
        sb.append(newLine);

        sb.append("[StartMatrixStep]");
        sb.append(newLine);
        sb.append("MatrixId = 211");
        sb.append(newLine);
        sb.append("MatrixName ="+'"'+ "C2_Base_Profilo"+'"');
        sb.append(newLine);
        sb.append("VN2901 = 0");
        sb.append(newLine);
        sb.append("VQ2901 = 0.000");
        sb.append(newLine);
        sb.append("VQ2902 = 0.000");
        sb.append(newLine);
        sb.append("[EndMatrixStep]");
        sb.append(newLine);
        sb.append(newLine);

        sb.append("[StartMatrixStep]");
        sb.append(newLine);
        sb.append("MatrixId = 112");
        sb.append(newLine);
        sb.append("MatrixName ="+'"'+ "C1_Head_Profilo"+'"');
        sb.append(newLine);
        sb.append("VQ1911 = 0.000");
        sb.append(newLine);
        sb.append("VQ1912 = 0.000");
        sb.append(newLine);
        sb.append("VQ1913 = 0.000");
        sb.append(newLine);
        sb.append("VQ1914 = 0.000");
        sb.append(newLine);
        sb.append("VQ1915 = 0.000");
        sb.append(newLine);
        sb.append("VQ1916 = 0.000");
        sb.append(newLine);
        sb.append("VQ1917 = 0.000");
        sb.append(newLine);
        sb.append("VQ1918 = 0.000");
        sb.append(newLine);
        sb.append("VQ1919 = 0.000");
        sb.append(newLine);
        sb.append("VQ1920 = 0.000");
        sb.append(newLine);
        sb.append("VQ1921 = 0.000");
        sb.append(newLine);
        sb.append("VQ1922 = 0.000");
        sb.append(newLine);
        sb.append("VQ1923 = 0.000");
        sb.append(newLine);
        sb.append("VQ1924 = 0.000");
        sb.append(newLine);
        sb.append("VQ1925 = 0.000");
        sb.append(newLine);
        sb.append("VQ1926 = 0.000");
        sb.append(newLine);
        sb.append("VQ1927 = 0.000");
        sb.append(newLine);
        sb.append("VQ1928 = 0.000");
        sb.append(newLine);
        sb.append("VQ1929 = 0.000");
        sb.append(newLine);
        sb.append("VQ1930 = 0.000");
        sb.append(newLine);
        sb.append("[EndMatrixStep]");
        sb.append(newLine);
        sb.append(newLine);

        sb.append("[StartMatrixStep]");
        sb.append(newLine);
        sb.append("MatrixId = 212");
        sb.append(newLine);
        sb.append("MatrixName ="+'"'+ "C2_Head_Profilo"+'"');
        sb.append(newLine);
        sb.append("VQ2911 = 0.000");
        sb.append(newLine);
        sb.append("VQ2912 = 0.000");
        sb.append(newLine);
        sb.append("VQ2913 = 0.000");
        sb.append(newLine);
        sb.append("VQ2914 = 0.000");
        sb.append(newLine);
        sb.append("VQ2915 = 0.000");
        sb.append(newLine);
        sb.append("VQ2916 = 0.000");
        sb.append(newLine);
        sb.append("VQ2917 = 0.000");
        sb.append(newLine);
        sb.append("VQ2918 = 0.000");
        sb.append(newLine);
        sb.append("VQ2919 = 0.000");
        sb.append(newLine);
        sb.append("VQ2920 = 0.000");
        sb.append(newLine);
        sb.append("VQ2921 = 0.000");
        sb.append(newLine);
        sb.append("VQ2922 = 0.000");
        sb.append(newLine);
        sb.append("VQ2923 = 0.000");
        sb.append(newLine);
        sb.append("VQ2924 = 0.000");
        sb.append(newLine);
        sb.append("VQ2925 = 0.000");
        sb.append(newLine);
        sb.append("VQ2926 = 0.000");
        sb.append(newLine);
        sb.append("VQ2927 = 0.000");
        sb.append(newLine);
        sb.append("VQ2928 = 0.000");
        sb.append(newLine);
        sb.append("VQ2929 = 0.000");
        sb.append(newLine);
        sb.append("VQ2930 = 0.000");
        sb.append(newLine);
        sb.append("[EndMatrixStep]");
        sb.append(newLine);
        sb.append(newLine);

        sb.append("BaseMatrixId = 111");
        sb.append(newLine);
        sb.append("HeadMatrixId = 112");
        sb.append(newLine);
        sb.append(newLine);

        sb.append("[StartHeadVal]");
        sb.append(newLine);
        sb.append(String.format ("%,.2f", pcX));
        sb.append(newLine);
        sb.append(String.format ("%,.2f", pcY));
        sb.append(newLine);
        sb.append(String.format ("%d", UdfVelLavRPM));
        sb.append(newLine);
        sb.append(String.format ("%,.3f", UdfPuntiVelIni/1000));
        sb.append(newLine);
        sb.append(String.format ("%d", UdfVelIniRPM));
        sb.append(newLine);
        sb.append(String.format ("%,.3f", UdfPuntiVelRall/1000));
        sb.append(newLine);
        sb.append(String.format ("%d", UdfVelRallRPM));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_FeedG0));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_ValTensioneT1));
        sb.append(newLine);
        sb.append(String.format ("%d",Udf_20 ));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_ValElettrocalamitaSopra));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_ValElettrocalamitaSotto));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_23));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_24));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_25));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_26));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_27));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_28));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_29));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_30));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_SequenzaPiegatore_chiusura));
        sb.append(newLine);
        sb.append(String.format ("%d", Udf_SequenzaPiegatore_apetura));
        sb.append(newLine);


        sb.append("[EndHeadVal]");
        sb.append(newLine);




        sb.append(" [StartStepsMultiVal]");
        sb.append(newLine);

        List<JamPoint> points = getPoints();

        for(JamPoint point: points)
        {
            try {
                JamPointStep jp = (JamPointStep) point;
                List<JamPointCode> ListCodici = new ArrayList<>();
                if (jp != null)
                {
                    for (JamPointCode code : codes) {
                        if (code.step == jp)
                            ListCodici.add(code);
                    }
                }


                String X = Tools.roundTruncate005toString(((JamPointStep) point).p.x); //ARROTONDATO AL #.#5 o #.#0
                String Y = Tools.roundTruncate005toString(((JamPointStep) point).p.y); //ARROTONDATO AL #.#5 o #.#0
                if (point.element instanceof ElementLine || point.element instanceof ElementArc || point.element instanceof ElementZigZag) {
                    if (ListCodici.size()>0){
                        for(JamPointCode code:ListCodici ){
                            switch (code.tipoCodice){
                                case OP1:
                                    if(code.valore == JamPointCode.TipiValori.VALUE0)
                                        sb.append("2763 11.00 0.00");   //VDC11 = 0
                                    else
                                        sb.append("2763 11.00 1.00");   //VDC11 = 1
                                    break;
                                case OP2:
                                    if(code.valore == JamPointCode.TipiValori.VALUE0)
                                        sb.append("2763 12.00 0.00");   //VDC12 = 0
                                    else
                                        sb.append("2763 12.00 1.00");   //VDC12 = 1
                                    break;

                                case OP3:
                                    if(code.valore == JamPointCode.TipiValori.VALUE0)
                                        sb.append("2763 13.00 0.00");   //VDC13 = 0
                                    else
                                        sb.append("2763 13.00 1.00");   //VDC13 = 1
                                    break;

                                case SPEED_M8:
                                    sb.append("2763 1.00 "+code.valore_M8);   //VDC1 = velocità del codice
                                    break;


                                case TENS_M8:
                                    sb.append("2763 2.00 "+code.valore_M8);   //VDC2 = valore tensione
                                    break;

                            }
                            sb.append(newLine);
                        }


                    }
                    sb.append("1001 " + X + " " + Y);
                }
                if (point.element instanceof ElementFeed) {
                    sb.append("1000 " + X + " " + Y);
                 //   sb.append(newLine);
                //    sb.append("1001 " + X + " " + Y);
                }

                sb.append(newLine);

            }
            catch (Exception e)
            {

            }

        }

        sb.append("[EndStepsMultiVal]");
        sb.append(newLine);
        sb.append(newLine);
        sb.append("[EndUdf]");

        FileOutputStream fileos= new FileOutputStream(file);
        fileos.write(sb.toString().getBytes());
        fileos.close();
    }

    public boolean hasSteps()
    {
        for(Element element: elements)
            if (element.steps.size()!=0)
                return true;

        return false;
    }

    //FUNZIONI DI GESTIONE UNDO/REDO I METODI PUBBLICI DEVONO CHIAMARE saveUndo: QUINDI I METODI PUBBLICI NON DEVONO ESSERE CHIAMATI DALL'INTERNO!
    public void undo()
    {
        if (undo!=null)
        {
            Ricetta ricettaRedo = new Ricetta();
            copy(this, ricettaRedo);

            ricettaRedo.undo = this;
            ricettaRedo.redo = this.redo;

            copy(undo, this);

            this.undo = undo.undo;
            this.redo = ricettaRedo;
        }
    }

    public void redo()
    {
        if (redo!=null)
        {
            Ricetta ricettaUndo = new Ricetta();
            copy(this, ricettaUndo);

            ricettaUndo.undo = this.undo;
            ricettaUndo.redo = this;

            copy(redo, this);

            this.undo = ricettaUndo;
            this.redo = redo.redo;
        }
    }



    //saveUndo DEVE ESSERE CHIAMATA SOLO DENTRO I METODI public CHE MODIFICANO LA RICETTA, NON DAI private PER EVITARE CHIAMATE MULTIPLE
    private void saveUndo()
    {
        //ESEGUE UNA COPIA DELLA RICETTA ATTUALE E LA SALVA PER UNDO
        Ricetta ricettaUndo = new Ricetta();
        copy(this, ricettaUndo);

        ricettaUndo.undo = this.undo;
        ricettaUndo.redo = this;

        this.undo = ricettaUndo;
        this.redo = null;
    }

    //ESEGUE UNA COPIA COMPLETA DI UNA RICETTA SU UN'ALTRA RICETTA
    private void copy(Ricetta source, Ricetta dest)
    {
        //ATTENZIONE: RIPORTARE SEMPRE TUTTE LE PROPERTY ESISTENTI!!
        dest.descrizioneRicetta = source.descrizione.Recipe.
        dest.numeroRicetta = source.numero.Recipe.
        dest.pcX = source.pcX;
        dest.pcY = source.pcY;

        dest.activeStepIndex = source.activeStepIndex;
        dest.indexSelectionStepStart = source.indexSelectionStepStart;
        dest.indexSelectionStepEnd = source.indexSelectionStepEnd;

        dest.selectedEntityCode = source.selectedEntityCode;

        dest.elements = new ArrayList<Element>();

        for(Element elementSource: source.elements)
        {
            Element element = null;

            if (elementSource instanceof ElementLine)
                element = new ElementLine((ElementLine)elementSource);
            else if (elementSource instanceof ElementArc)
                element = new ElementArc((ElementArc)elementSource);
            else if (elementSource instanceof ElementZigZag)
                element = new ElementZigZag((ElementZigZag)elementSource);
            else if (elementSource instanceof ElementFeed)
                element = new ElementFeed((ElementFeed)elementSource);

            if (element!=null)
            {
                for(JamPointStep stepSource: elementSource.steps)
                {
                    JamPointStep step = new JamPointStep(stepSource);
                    step.element = element;
                    element.steps.add(step);
                }

                element.ricetta = dest;
                dest.elements.add(element);
            }
        }

        //NUOVA GESTIONE CODE
        dest.codes = new ArrayList<>();

        for(JamPointCode codeSource: source.codes)
        {
            JamPointCode code = new JamPointCode(codeSource);
            int elementIndex = codeSource.getElementIndex();
            int stepIndex = codeSource.getStepIndex();
            code.step = dest.getStep(elementIndex, stepIndex); //DEVE PUNTARE ALLO STEP CORRISPONDENTE NELLA RICETTA dest!!
            dest.codes.add(code);
        }
    }

    JamPointStep getStep(int elementIndex, int stepIndex)
    {
        JamPointStep ret = null;

        if (elementIndex>-1 && elementIndex < elements.size())
        {
            Element element = elements.get(elementIndex);

            if (stepIndex>-1 && stepIndex < element.steps.size())
                ret = element.steps.get(stepIndex);
        }

        return ret;
    }


}





