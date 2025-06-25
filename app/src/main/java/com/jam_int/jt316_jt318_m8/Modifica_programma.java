package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jamint.recipes.Element;
import com.jamint.recipes.ElementArc;
import com.jamint.recipes.ElementFeed;
import com.jamint.recipes.ElementLine;
import com.jamint.recipes.ElementLineZigZag;
import com.jamint.recipes.JamPointCode;
import com.jamint.recipes.JamPointStep;
import com.jamint.recipes.Recipe;
import com.jamint.recipes.Tools;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Modifica_programma extends Activity {
    ShoppingList sl;
    Thread t1;
    FrameLayout frame_canvas;
    String File_Xml_path,Machine_model;
    Recipe recipe, ricetta_undo;
    Context context;
    ArrayList<Element> List_entità = new ArrayList<>();
    List<Element> ElemSelezionati = new ArrayList<Element>(); ;
    Dynamic_view myView;
    Handler UpdateHandler = new Handler();
    MainActivity.CoordPosPinza Coord_Pinza = new MainActivity.CoordPosPinza();
    Button Button_arrow_up,Button_freccia_giu,Button_arrow_right,Button_arrow_left,Button_arrow_up_right,Button_arrow_down_right,Button_arrow_down_left,Button_arrow_up_left,ButtonPuntoPiu,ButtonPuntoMeno,
            Button_tasto_home, Button_sposta,Button_feed ,Button_codici,Button_888M ,Button_linea,Button_arco3p ,Button_spline,Button_travetta ,Button_cancella, Button_tasto0, Button_tasto1, Button_tasto2,
            Button_tasto3, Button_tasto4, Button_tasto5, Button_tasto6 , Button_tasto7 , Button_tasto8, Button_tasto9, Button_tasto_punto, Button_tasto_del,
            Button_entita_piu,Button_entita_meno,Button_piu,Button_meno,Button_tasto_enter,Button_stretch_edge,Button_esc,Button_move_all,
            Button_raddrizza_arco,Button_raddrizza_linea,Button_Sgancio_ago,Button_exit,Button_traform_toLine,Button_traform_toZigZag,Button_traform_toFeed,Button_explode,
            Button_debug,Button_undo,Button_redo,Button_new,Button_delete_code,Button_jog_rot_sx,Button_jog_rot_dx;
    TextView TextView_Prog_name,TextView_XAss,TextView_YAss,TextView_xRel,TextView_YRel,TextView_cnt_punti,TextView_tot_punti,TextView_valore_A,TextView_valore_B,TextView_info,TextView_Code,
            TextView_debug,TextView_angolo_testa;
    ImageView ImageView;
    Boolean Thread_Running = false,  StopThread = false, first_cycle = true;
    MultiCmdItem MultiCmd_XY_fermi,MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,MultiCmd_Vb_OutPiedino_su,MultiCmd_Vb_OutPiedino_giu,MultiCmd_Status_Piedino,
        MultiCmd_Start_movimento_X,MultiCmd_Start_movimento_Y,MultiCmd_posizione_X,MultiCmd_posizione_Y,MultiCmd_Vn3804_pagina_touch,
        MultiCmd_go_Home,MultiCmd_JogYMeno,MultiCmd_JogYPiu, MultiCmd_start_Jog_incrementaleXPiu, MultiCmd_start_Jog_incrementaleXMeno ,
        MultiCmd_start_Jog_incrementaleYPiu ,MultiCmd_start_Jog_incrementaleYMeno,MultiCmd_incrementaleX,MultiCmd_incrementaleY,  MultiCmd_tasto_verde,
        MultiCmd_CH1_in_emergenza,MultiCmd_JogXMeno,MultiCmd_JogXPiu, MultiCmd_jogXPiuYPiu,MultiCmd_jogXPiuYMeno ,MultiCmd_jogXMenoYPiu ,MultiCmd_jogXMenoYMeno,
            MultiCmd_Vb27_SbloccaCrochet, MultiCmd_Quota_Assoluta_rotazione,MultiCmd_jog_Rotaz_dx ,MultiCmd_jog_Rotaz_sx,MultiCmd_Vn3081_override_rotaz,
        MultiCmd_Vq_104_READ_FC_ind_X,MultiCmd_Vq_105_READ_FC_ava_X,MultiCmd_Vq_106_READ_FC_ind_Y,MultiCmd_Vq_107_READ_FC_ava_Y,MultiCmd_Vb72HmiMoveXY_C1, MultiCmd_Vb74_RichiestaPiedinoSu_C1,
        MultiCmd_Vb4303_out_aggancio_pallet;


    MainActivity.Mci_write Mci_write_quota_destinazione_X,Mci_write_quota_destinazione_Y, Mci_write_JogXMeno, Mci_write_JogXPiu, Mci_write_JogYMeno,
            Mci_write_JogYPiu, Mci_write_jogXPiuYPiu , Mci_write_jogXPiuYMeno , Mci_write_jogXMenoYPiu , Mci_write_jogXMenoYMeno, Mci_Vb_OutPiedino_su,
           Mci_Sblocca_Ago,Mci_write_jog_Rotaz_dx,Mci_write_jog_Rotaz_sx,Mci_Vn3081_override_rotaz,Mci_Vb4303_out_aggancio_pallet;

    MultiCmdItem[] mci_array_read_all;
    Info_StepPiuMeno info_StepPiuMeno = new Info_StepPiuMeno();
    Info_modifica info_modifica = new Info_modifica();
    ArrayList<Button> Lista_pulsanti_comandi = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_comandi_numeri = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_comandi_frecce = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_PiuMeno = new ArrayList<>();
    int step_Home = 0;
    int step_Fai_Esci = 0;
    String Folder;
    float old_A = 3.0f, old_B = 0.5f;
    boolean onClickTastoEntitaPiu = false,onClickTastoEntitaMeno = false;

    final private static int PAGE_UDF_T1_DX = 200;
    final private static int PAGE_UDF_T1_SX = 201;
    final private static int PAGE_UDF_T2_DX = 202;
    final private static int PAGE_UDF_T2_SX = 203;
    final private static int RESULT_PAGE_EMG = 101;
    final private static int RESULT_PAGE_LOAD_EEP = 102;
    final private static int RESULT_PAGE_CODE = 103;
    final private static int POPUPFOLDER = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_programma);

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        context = this;
        sl = SocketHandler.getSocket();
        sl.Clear();

        frame_canvas = (FrameLayout)findViewById(R.id.frameLayout);

        ImageView = (ImageView) findViewById(R.id.imageView_tasca);
        Button_arrow_up = (Button) findViewById(R.id.button_arrow_up);
        Button_freccia_giu = (Button) findViewById(R.id.button_freccia_giu);
        Button_arrow_right = (Button) findViewById(R.id.button_arrow_right_caric);
        Button_arrow_left = (Button) findViewById(R.id.button_arrow_left);
        Button_arrow_up_right = (Button) findViewById(R.id.button_arrow_up_right);
        Button_arrow_down_right = (Button) findViewById(R.id.button_arrow_down_right);
        Button_arrow_down_left = (Button) findViewById(R.id.button_arrow_down_left);
        Button_arrow_up_left = (Button) findViewById(R.id.button_arrow_up_left);
        Button_exit = (Button)findViewById(R.id.button_exit);
        Button_jog_rot_sx = (Button) findViewById(R.id.button_arrow_rotaz_sx);
        Button_jog_rot_dx = (Button) findViewById(R.id.button_arrow_rotaz_dx);
        TextView_Prog_name = (TextView) findViewById(R.id.textView_Prog_name);
        TextView_XAss = (TextView) findViewById(R.id.textView_XAss);
        TextView_YAss = (TextView) findViewById(R.id.textView_YAss);
        TextView_xRel = (TextView) findViewById(R.id.textView_xRel);
        TextView_YRel = (TextView) findViewById(R.id.textView_YRel);
        TextView_info = (TextView) findViewById(R.id.textView_info);
        TextView_Code = (TextView) findViewById(R.id.textView_Code);
        TextView_cnt_punti = (TextView) findViewById(R.id.textView_cnt_punti);
        TextView_tot_punti  = (TextView) findViewById(R.id.textView_tot_punti);
        TextView_debug  = (TextView) findViewById(R.id.textView_angolo_testa);
        TextView_angolo_testa  = (TextView) findViewById(R.id.textView_angolo_testa);
        ButtonPuntoPiu = (Button) findViewById(R.id.button_piu);
        ButtonPuntoMeno = (Button) findViewById(R.id.button_meno);
        Button_tasto_home = (Button) findViewById(R.id.button_tasto_home);
        Button_sposta  = (Button) findViewById(R.id.button_sposta);
        Button_feed  = (Button) findViewById(R.id.button_feed);
        Button_codici  = (Button) findViewById(R.id.button_codici);
        Button_888M  = (Button) findViewById(R.id.button_888M);
        Button_linea  = (Button) findViewById(R.id.button_linea);
        Button_arco3p  = (Button) findViewById(R.id.button_arco3p);
        Button_spline  = (Button) findViewById(R.id.button_spline);
        Button_travetta  = (Button) findViewById(R.id.button_travetta);
        Button_cancella  = (Button) findViewById(R.id.button_cancella);
        Button_tasto0 = (Button) findViewById(R.id.button_tasto0);
        Button_tasto1 = (Button) findViewById(R.id.button_tasto1);
        Button_tasto2 = (Button) findViewById(R.id.button_tasto2);
        Button_tasto3 = (Button) findViewById(R.id.button_tasto3);
        Button_tasto4 = (Button) findViewById(R.id.button_tasto4);
        Button_tasto5 = (Button) findViewById(R.id.button_tasto5);
        Button_tasto6 = (Button) findViewById(R.id.button_tasto6);
        Button_tasto7 = (Button) findViewById(R.id.button_tasto7);
        Button_tasto8 = (Button) findViewById(R.id.button_tasto8);
        Button_tasto9 = (Button) findViewById(R.id.button_tasto9);
        Button_tasto_punto = (Button) findViewById(R.id.button_tasto_punto);
        Button_tasto_del = (Button) findViewById(R.id.button_tasto_del);
        Button_move_all = (Button) findViewById(R.id.button_move_all);
        TextView_valore_A = (TextView) findViewById(R.id.textView_valore_A);
        TextView_valore_B = (TextView) findViewById(R.id.textView_valore_B);
        Button_entita_piu = (Button) findViewById(R.id.button_entita_piu);
        Button_entita_meno = (Button) findViewById(R.id.button_entita_meno);
        Button_piu = (Button) findViewById(R.id.button_piu);
        Button_meno = (Button) findViewById(R.id.button_meno);
        Button_tasto_enter = (Button) findViewById(R.id.button_tasto_enter);
        Button_stretch_edge = (Button) findViewById(R.id.button_stretch_edge);
        Button_esc = (Button) findViewById(R.id.button_esc);
        Button_raddrizza_arco = (Button) findViewById(R.id.button_raddrizza_arco);
        Button_raddrizza_linea = (Button) findViewById(R.id.button_raddrizza_linea);
        Button_Sgancio_ago = (Button)findViewById(R.id.button_sblocca_ago);
        Button_traform_toLine = (Button)findViewById(R.id.button_traform_toLine);
        Button_traform_toZigZag  = (Button)findViewById(R.id.button_traform_toZigZag);
        Button_traform_toFeed  = (Button)findViewById(R.id.button_traform_toFeed);
        Button_explode = (Button)findViewById(R.id.button_explode);
        Button_debug = (Button)findViewById(R.id.button_debug);
        Button_undo = (Button)findViewById(R.id.button_undo);
        Button_redo = (Button)findViewById(R.id.button_redo);
        Button_new = (Button)findViewById(R.id.button_new);
        Button_delete_code = (Button)findViewById(R.id.button_delete_code);


        Crea_Liste_pulsanti();  //inizializzo

       

        try {
            File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());

        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = SubString.SubstringExtensions.After( File_Xml_path,"/");
        TextView_Prog_name.setText(SubString.SubstringExtensions.Before( filename,"."));



        MultiCmd_XY_fermi = sl.Add("Io", 1, MultiCmdItem.dtVB, 4058, MultiCmdItem.dpNONE);
        MultiCmd_Vb_OutPiedino_su = sl.Add("Io", 1, MultiCmdItem.dtVB, 1003, MultiCmdItem.dpNONE);
        MultiCmd_Vb_OutPiedino_giu = sl.Add("Io", 1, MultiCmdItem.dtVB, 1004, MultiCmdItem.dpNONE);
        MultiCmd_quota_destinazione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7002, MultiCmdItem.dpNONE);
        MultiCmd_quota_destinazione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7022, MultiCmdItem.dpNONE);
        MultiCmd_Status_Piedino = sl.Add("Io", 1, MultiCmdItem.dtDO,65, MultiCmdItem.dpNONE);
        MultiCmd_Start_movimento_X = sl.Add("Io", 1, MultiCmdItem.dtVB, 7005, MultiCmdItem.dpNONE);
        MultiCmd_Start_movimento_Y = sl.Add("Io", 1, MultiCmdItem.dtVB, 7025, MultiCmdItem.dpNONE);
        MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
        MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
        MultiCmd_go_Home  = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
        MultiCmd_jogXPiuYPiu  = sl.Add("Io", 1, MultiCmdItem.dtVB, 56, MultiCmdItem.dpNONE);
        MultiCmd_jogXPiuYMeno  = sl.Add("Io", 1, MultiCmdItem.dtVB, 57, MultiCmdItem.dpNONE);
        MultiCmd_jogXMenoYPiu  = sl.Add("Io", 1, MultiCmdItem.dtVB, 54, MultiCmdItem.dpNONE);
        MultiCmd_jogXMenoYMeno  = sl.Add("Io", 1, MultiCmdItem.dtVB, 55, MultiCmdItem.dpNONE);
        MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 63, MultiCmdItem.dpNONE);
        MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 62, MultiCmdItem.dpNONE);
        MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);
        MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
        MultiCmd_start_Jog_incrementaleXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7003, MultiCmdItem.dpNONE);
        MultiCmd_start_Jog_incrementaleXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7004, MultiCmdItem.dpNONE);
        MultiCmd_start_Jog_incrementaleYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7023, MultiCmdItem.dpNONE);
        MultiCmd_start_Jog_incrementaleYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7024, MultiCmdItem.dpNONE);
        MultiCmd_incrementaleX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7001, MultiCmdItem.dpNONE);
        MultiCmd_incrementaleY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7021, MultiCmdItem.dpNONE);
        MultiCmd_Vb27_SbloccaCrochet = sl.Add("Io", 1, MultiCmdItem.dtVB, 27, MultiCmdItem.dpNONE);



        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Quota_Assoluta_rotazione = sl.Add("Io", 1, MultiCmdItem.dtAX, 5, MultiCmdItem.dpAX_M32_Quota_Reale_Assoluta);
        MultiCmd_jog_Rotaz_dx = sl.Add("Io", 1, MultiCmdItem.dtVB, 7081, MultiCmdItem.dpNONE);
        MultiCmd_jog_Rotaz_sx = sl.Add("Io", 1, MultiCmdItem.dtVB, 7082, MultiCmdItem.dpNONE);
        MultiCmd_Vq_104_READ_FC_ind_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 104, MultiCmdItem.dpNONE);
        MultiCmd_Vq_105_READ_FC_ava_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 105, MultiCmdItem.dpNONE);
        MultiCmd_Vq_106_READ_FC_ind_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 106, MultiCmdItem.dpNONE);
        MultiCmd_Vq_107_READ_FC_ava_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 107, MultiCmdItem.dpNONE);
        MultiCmd_Vb72HmiMoveXY_C1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 72, MultiCmdItem.dpNONE);
        MultiCmd_Vb74_RichiestaPiedinoSu_C1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 74, MultiCmdItem.dpNONE);
        MultiCmd_Vb4303_out_aggancio_pallet  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4303, MultiCmdItem.dpNONE);

        MultiCmd_Vn3081_override_rotaz = sl.Add("Io", 1, MultiCmdItem.dtVN, 3081, MultiCmdItem.dpNONE);


        try {

            Load_XML(File_Xml_path);

        }catch (Exception e) { Toast.makeText(getApplicationContext(), "Errore XML in ingresso", Toast.LENGTH_SHORT).show();}



        Mci_write_quota_destinazione_X = new MainActivity.Mci_write(); Mci_write_quota_destinazione_X.mci = MultiCmd_quota_destinazione_X; Mci_write_quota_destinazione_X.write_flag = false;
        Mci_write_quota_destinazione_Y = new MainActivity.Mci_write(); Mci_write_quota_destinazione_Y.mci = MultiCmd_quota_destinazione_Y; Mci_write_quota_destinazione_Y.write_flag = false;
        Mci_write_JogXMeno = new MainActivity.Mci_write(); Mci_write_JogXMeno.mci = MultiCmd_JogXMeno; Mci_write_JogXMeno.write_flag = false;
        Mci_write_JogXPiu = new MainActivity.Mci_write(); Mci_write_JogXPiu.mci = MultiCmd_JogXPiu; Mci_write_JogXPiu.write_flag = false;
        Mci_write_JogYMeno = new MainActivity.Mci_write(); Mci_write_JogYMeno.mci = MultiCmd_JogYMeno; Mci_write_JogYMeno.write_flag = false;
        Mci_write_JogYPiu = new MainActivity.Mci_write(); Mci_write_JogYPiu.mci = MultiCmd_JogYPiu; Mci_write_JogYPiu.write_flag = false;
        Mci_write_jogXPiuYPiu = new MainActivity.Mci_write(); Mci_write_jogXPiuYPiu.mci = MultiCmd_jogXPiuYPiu; Mci_write_jogXPiuYPiu.write_flag = false;
        Mci_write_jogXPiuYMeno  = new MainActivity.Mci_write(); Mci_write_jogXPiuYMeno.mci = MultiCmd_jogXPiuYMeno; Mci_write_jogXPiuYMeno.write_flag = false;
        Mci_write_jogXMenoYPiu  = new MainActivity.Mci_write(); Mci_write_jogXMenoYPiu.mci = MultiCmd_jogXMenoYPiu; Mci_write_jogXMenoYPiu.write_flag = false;
        Mci_write_jogXMenoYMeno = new MainActivity.Mci_write(); Mci_write_jogXMenoYMeno.mci = MultiCmd_jogXMenoYMeno; Mci_write_jogXMenoYMeno.write_flag = false;
        Mci_Vb_OutPiedino_su = new MainActivity.Mci_write(); Mci_Vb_OutPiedino_su.mci = MultiCmd_Vb_OutPiedino_su; Mci_Vb_OutPiedino_su.write_flag = false;
        Mci_Sblocca_Ago = new MainActivity.Mci_write(); Mci_Sblocca_Ago.mci = MultiCmd_Vb27_SbloccaCrochet; Mci_Sblocca_Ago.write_flag = false;
        Mci_write_jog_Rotaz_sx = new MainActivity.Mci_write(); Mci_write_jog_Rotaz_sx.mci = MultiCmd_jog_Rotaz_sx; Mci_write_jog_Rotaz_sx.write_flag = false;
        Mci_write_jog_Rotaz_dx = new MainActivity.Mci_write(); Mci_write_jog_Rotaz_dx.mci = MultiCmd_jog_Rotaz_dx; Mci_write_jog_Rotaz_dx.write_flag = false;
        Mci_Vn3081_override_rotaz = new MainActivity.Mci_write(); Mci_Vn3081_override_rotaz.mci = MultiCmd_Vn3081_override_rotaz; Mci_Vn3081_override_rotaz.write_flag = false;
        Mci_Vb4303_out_aggancio_pallet = new MainActivity.Mci_write(); Mci_Vb4303_out_aggancio_pallet.mci = MultiCmd_Vb4303_out_aggancio_pallet; Mci_Vb4303_out_aggancio_pallet.write_flag = false;





        mci_array_read_all = new MultiCmdItem[]{MultiCmd_XY_fermi,MultiCmd_Status_Piedino,MultiCmd_posizione_X,MultiCmd_posizione_Y,MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,
                MultiCmd_Quota_Assoluta_rotazione};

        
        Init_Eventi();
        TextView_tot_punti.setText(""+  recipe.getStepsCount());
        TextView_xRel.setVisibility(View.GONE);
        TextView_YRel.setVisibility(View.GONE);
        Button_debug.setVisibility(View.GONE);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu,Button_arrow_up,"ic_up_press","ic_up",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno,Button_freccia_giu,"ic_down_press","ic_down",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXPiu,Button_arrow_right,"ic_right_press","ic_right",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXMeno,Button_arrow_left,"ic_left_press","ic_left",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYMeno ,Button_arrow_up_right,"freccia_su_dx_b","freccia_su_dx_a",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYPiu,Button_arrow_down_right,"freccia_giu_dx_b","freccia_giu_dx_a",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYPiu ,Button_arrow_down_left,"freccia_giu_sx_b","freccia_giu_sx_a",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYMeno,Button_arrow_up_left,"freccia_su_sx_b","freccia_su_sx_a",getApplicationContext(),sl,100);
        Toggle_Button.CreaToggleButton(Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jog_Rotaz_sx,Button_jog_rot_sx,"freccia_rotaz_sx_premuto","freccia_rotaz_sx",getApplicationContext(),sl,100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jog_Rotaz_dx,Button_jog_rot_dx,"freccia_rotaz_dx_premuto","freccia_rotaz_dx",getApplicationContext(),sl,100);

        if(Machine_model.equals("JT316M") || Machine_model.equals("JT318M") || Machine_model.equals("JT318M_1000x800")){
            Button_jog_rot_sx.setVisibility(View.GONE);
            Button_jog_rot_dx.setVisibility(View.GONE);
            TextView_angolo_testa.setVisibility(View.GONE);
        }



        //faccio partire il broadcast per ricevere eventuale ritorno del cambio della lunghezza punto (qui parte sempe ma solo una volta)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_KeyDialog, new IntentFilter("ret_valore"));


        if (!Thread_Running ) {
            StopThread = false;
            MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(Modifica_programma.this);
            t1 = new Thread(myTask, "Main myTask");
            t1.setName("Thread_modifica_programma");
            t1.start();
            Log.d("JAM TAG", "Start Modifica_programma Thread");
        }
        
    }

    private void Init_Eventi() {
        ButtonPuntoPiu.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if(info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d ) {

                                for (Element elementi : List_entità) {     //selezione
                                    elementi.isSelected = false;       //deseleziono tutti gli element
                                }
                                for (Element elementi : ElemSelezionati) {     //selezione
                                    elementi.isSelected = false;       //deseleziono gli element dell'entità
                                }



                                if (isNumeric((String) TextView_info.getText())) {
                                    try {
                                        Info_StepPiuMeno.numeroRipetuto = Integer.parseInt((String) TextView_info.getText());
                                        if (Info_StepPiuMeno.numeroRipetuto > 0) {
                                            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.N_SALTO;
                                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                            info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                                            info_StepPiuMeno.MacStati_StepSingolo = 10;

                                        }
                                    } catch (Exception e) {
                                    }
                                }else {


                                    info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                    info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                                    info_StepPiuMeno.MacStati_StepSingolo = 10;
                                }
                                info_modifica.QuoteRelativeAttive = true;
                                info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                                info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                            }

                        }
                    }else
                    {
                        Info_StepPiuMeno.numeroRipetuto = 1;    //se arrivo sta spostamenti SINGOLO_RIPETUTO faccio fare un altro passo e poi si ferma
                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.NULL;
                    }
                }


                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.STOP;
                }

                return false;
            }
        });
        ButtonPuntoPiu.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if(info_StepPiuMeno.MacStati_StepVeloce == 0 && info_StepPiuMeno.MacStati_StepSingolo == 0 ) {
                    if(info_StepPiuMeno.MacStati_StepSingolo == 0)
                    {

                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.VELOCE;
                        info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                        info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                        info_StepPiuMeno.MacStati_StepSingolo = 10;


                    }
                }

                return true;    // <- set to true
            }
        });


        ButtonPuntoMeno.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if (info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {

                                for (Element elementi : List_entità) {     //selezione
                                    elementi.isSelected = false;       //deseleziono tutti gli element
                                }
                                for (Element elementi : ElemSelezionati) {     //selezione
                                    elementi.isSelected = false;       //deseleziono gli element dell'entità
                                }


                                if (isNumeric((String) TextView_info.getText())) {
                                    try {
                                        Info_StepPiuMeno.numeroRipetuto = Integer.parseInt((String) TextView_info.getText());
                                        if (Info_StepPiuMeno.numeroRipetuto > 0) {
                                            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.N_SALTO;
                                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                            info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                                            info_StepPiuMeno.MacStati_StepSingolo = 10;

                                        }
                                    } catch (Exception e) {
                                    }
                                } else{

                                    info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                    info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                                    info_StepPiuMeno.MacStati_StepSingolo = 10;
                                }
                                info_modifica.QuoteRelativeAttive = true;
                                info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                                info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

                            }

                        }
                    }else{
                            Info_StepPiuMeno.numeroRipetuto = 1;    //se arrivo sta spostamenti SINGOLO_RIPETUTO faccio fare un altro passo e poi si ferma
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.STOP;
                }
                return false;
            }
        });
        ButtonPuntoMeno.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if(info_StepPiuMeno.MacStati_StepVeloce == 0 && info_StepPiuMeno.MacStati_StepSingolo == 0 ) {
                    if(info_StepPiuMeno.MacStati_StepSingolo == 0)
                    {

                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.VELOCE;
                        info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                        info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                        info_StepPiuMeno.MacStati_StepSingolo = 10;


                    }
                }

                return true;    // <- set to true
            }
        });
        TextView_valore_A.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    KeyDialog KeyD_A = new KeyDialog();
                    KeyD_A.Lancia_KeyDialogo(null ,Modifica_programma.this,TextView_valore_A,5,0.1,true,false,0d,false,"ret_valore",false);


                }else
                if(event.getAction() == MotionEvent.ACTION_UP){




                }

                return false;

            }
        });


        TextView_valore_B.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    KeyDialog KeyD_B = new KeyDialog();
                    KeyD_B.Lancia_KeyDialogo(null ,Modifica_programma.this,TextView_valore_B,5,0.1,true,false,0d,false,"ret_valore",false);


                }else
                if(event.getAction() == MotionEvent.ACTION_UP){




                }

                return false;
            }
        });



    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();


    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();




    }
    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();

       KillThread();

    }
    
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void Load_XML(String percorso_file) {
        try {

            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/ricette");
            dir.mkdirs();
            File file = new File(percorso_file);
            int i = file.getName().lastIndexOf('.');
            String name = file.getName().substring(0,i);
            File file1 = new File(file.getParent() + "/" + name + ".xml");
            //  ricetta = new Ricetta(file1);

            recipe = new Recipe();
            try
            {
                recipe.open(file1);
            }
            catch(Exception e){
                Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
            }



            List_entità = (ArrayList<Element>)recipe.elements;

            if(Values.Machine_model.equals("JT318M_1000x800")) {
                myView = new Dynamic_view(this, 733, 350, List_entità, .4F, Coord_Pinza, false, 30, 40, null, true, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));
            }else
            {
                myView = new Dynamic_view(this, 733, 350, List_entità, .4F, Coord_Pinza, false, 20, 100, null, true, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));

            }
            frame_canvas.addView(myView);
            myView.Ricalcola_entità_canvas((ArrayList<Element>) List_entità);
            myView.Center_Bitmap();
            //myView.Center_Bitmap_Main(.7F,400,20);





        }catch (Exception  e)
        {
            Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();

        }

    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    class MyAndroidThread_Modifica implements Runnable {
        Activity activity;

        int cnt = 10;

        public MyAndroidThread_Modifica(Activity activity) {
            this.activity = activity;


        }

        @Override
        public void run() {

            while (true) {
                Thread_Running = true;
                Boolean rc_error;
                try {
                    Thread.sleep((long) 10d);
                    if (StopThread) {
                        Thread_Running = false;
                      //  MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                       // sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    if(first_cycle){
                       // MultiCmd_Vn3804_pagina_touch.setValue(1003.0d);
                       // sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        first_cycle = false;
                        Mci_Vn3081_override_rotaz.valore = 10d;
                        Mci_Vn3081_override_rotaz.write_flag = true;

                        sl.ReadItem(MultiCmd_Vq_104_READ_FC_ind_X);
                        sl.ReadItem(MultiCmd_Vq_105_READ_FC_ava_X);
                        sl.ReadItem(MultiCmd_Vq_106_READ_FC_ind_Y);
                        sl.ReadItem(MultiCmd_Vq_107_READ_FC_ava_Y);
                    }

                    MultiCmd_Vn3804_pagina_touch.setValue(1003.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);

                    sl.WriteQueued();
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;

                    } else rc_error = false;

                    if (rc_error == false) { //se ho avuto un errore di ricezione salto

                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.SINGOLO || info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.VELOCE)
                        {
                            StepSingolo(1);
                        }
                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {
                            StepSingolo(info_StepPiuMeno.numeroRipetuto);
                        }
                        if(info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.TO_STEP_ATTIVO) {
                            Moveto( recipe.getActiveStep());
                        }

                        if(info_modifica.comando == Info_modifica.Comando.HOME) Fai_Home();
                        if(info_modifica.comando == Info_modifica.Comando.ESCI) Fai_Esci();
                        GestiscoFreccia(Mci_write_JogYMeno);
                        GestiscoFreccia(Mci_write_JogYPiu);
                        GestiscoFreccia(Mci_write_JogXPiu);
                        GestiscoFreccia(Mci_write_JogXMeno);
                        GestiscoFreccia(Mci_write_jogXPiuYMeno);
                        GestiscoFreccia(Mci_write_jogXPiuYPiu);
                        GestiscoFreccia(Mci_write_jogXMenoYPiu);
                        GestiscoFreccia(Mci_write_jogXMenoYMeno);
                        GestiscoFreccia(Mci_write_jog_Rotaz_sx);
                        GestiscoFreccia(Mci_write_jog_Rotaz_dx);
                        ScrivoVbVnVq(Mci_Vb_OutPiedino_su);
                        ScrivoVbVnVq(Mci_Vn3081_override_rotaz);
                        GestiscoMci_Out_Toggle(Mci_Sblocca_Ago);
                        GestioneEntitaPiu();
                        GestioneEntitaMeno();
                        double X = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        Coord_Pinza.CoordPosPinza(X, Y, recipe);

                        AggiornaGuiDaThread();

                    } else {

                        sl.Connect();


                    }
                }

            }
        }

        //**************************************************************************************************
//AggiornaGuiDaThread
//**************************************************************************************************
        private void AggiornaGuiDaThread(){

            UpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(!Values.Debug_mode)Emergenza();
                    if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO && info_StepPiuMeno.comando == Info_StepPiuMeno.Comando.NULL) {
                        //finito salto di n punti
                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.NULL;
                        TextView_info.setText(info_StepPiuMeno.last_testo_textView_info);  //
                        info_StepPiuMeno.last_testo_textView_info = "Info:";
                    }
                    if(info_modifica.comando == Info_modifica.Comando.HOME_DONE){
                        info_modifica.comando = Info_modifica.Comando.Null;
                        Mci_Vn3081_override_rotaz.valore = 10d;
                        Mci_Vn3081_override_rotaz.write_flag = true;
                        Mostra_Tutte_Icone();
                    }
                    if( info_modifica.comando == Info_modifica.Comando.ESCI_DONE_AZZERAMENTO){
                        info_modifica.comando = Info_modifica.Comando.Null;
                        recipe.clearActiveStep();  //imposta indice step a -1
                        recipe.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate
                        Run_Alert_dialog();

                    }
                    GestioneVisualizzazioneToggleButton(Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");
                    Scrivi_codice_HMI();
                    TextView_XAss.setText("" + ((Double) MultiCmd_posizione_X.getValue() / 1000d));
                    TextView_YAss.setText("" + ((Double) MultiCmd_posizione_Y.getValue() / 1000d));
                    ShowQuoteRelative();
                    TextView_tot_punti.setText("" + recipe.getStepsCount());
                    ShowIndicePunto(recipe.getActiveStepIndex());
                    if (Coord_Pinza.XCoord_precedente != Coord_Pinza.XCoordPosPinza || Coord_Pinza.YCoord_precedente != Coord_Pinza.YCoordPosPinza) {
                        myView.AggiornaCanvas(true);
                        Coord_Pinza.XCoord_precedente = Coord_Pinza.XCoordPosPinza;
                        Coord_Pinza.YCoord_precedente = Coord_Pinza.YCoordPosPinza;

                    }
                    Show_info_entità();
                    Show_debug_entità();
                    Show_angolo_testa();
                }



            });
        }
        //**************************************************************************************************
        private void Show_angolo_testa() {
            int Angolo = (int) Math.round((Double)MultiCmd_Quota_Assoluta_rotazione.getValue()/1000);
            TextView_angolo_testa.setText("Rotation Angle: "+Angolo);
        }
        //**************************************************************************************************
        private void Show_debug_entità() {
            try {
                Element element = recipe.getActiveStep().element;
                int indexElement = recipe.elements.indexOf(recipe.getActiveStep().element);
                TextView_debug.setText("Num_entità: " + recipe.elements.size() + "\n" +
                        "Index elemento attivo: " + indexElement + "\n" +
                        "index step: " + recipe.getActiveStepIndex());
            }catch (Exception e){
                TextView_debug.setText("d:");
            }
        }

        //**************************************************************************************************
        //Run_Alert_dialog
        //**************************************************************************************************
        private void Run_Alert_dialog() {
            // Creating alert Dialog with one Button
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setCancelable(false);   //Antonio Cella, nel caso si preme fuori dal dialog il dialog non viene cancellato
            // Setting Dialog Title
            alertDialog.setTitle("File Name:");

            // Setting Dialog Message
            try {
                //Tolgo la parte di storage, emulated, 0 ecc....
                String newFolder = "";
                String[] str = Folder.split("/");
                int i = 0;
                for (String st : str) {
                    if (i == 1) {
                        newFolder = newFolder + "/" + st;
                    }
                    if (st.equals("ricette")) {
                        i = 1;
                    }
                }
                alertDialog.setMessage("Folder: " + newFolder);
            } catch (Exception e) {}

            final EditText input = new EditText(context);

            File file;
            file = new File(File_Xml_path);

            int i = file.getName().lastIndexOf('.');
            String name = file.getName().substring(0, i);
            input.setText(name);
            input.setFocusable(false);
            input.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        KeyDialog_lettere.Lancia_KeyDialogo_lettere(Modifica_programma.this, input,"");
                    }

                    return false;
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //  Esci_con_azzeramento = true;
                            //Fai_Home_primo_Home();
                            if (!input.getText().toString().matches("")) {
                                dialog.cancel();
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir;
                                if(Folder == null) {
                                    dir = new File(root.getAbsolutePath() + "/ricette");
                                }
                                else
                                {
                                    dir = new File(Folder);
                                }
                                dir.mkdirs();
                                File file = new File(dir, input.getText().toString() + ".xml");
                                File file1 = new File(dir, input.getText().toString() + ".usr");
                                if (file.exists() || file1.exists()) {
                                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Modifica_programma.this);

                                    // Setting Dialog Title
                                    alertDialog1.setTitle("overWrite");

                                    // Setting Dialog Message
                                    alertDialog1.setMessage("overWrite?");

                                    // Setting Positive "Yes" Button
                                    alertDialog1.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    File root = android.os.Environment.getExternalStorageDirectory();
                                                    File dir;
                                                    if(Folder == null) {
                                                        dir = new File(root.getAbsolutePath() + "/ricette");
                                                    }
                                                    else
                                                    {
                                                        dir = new File(Folder);
                                                    }
                                                    dir.mkdirs();
                                                    File file = new File(dir, input.getText().toString() + ".xml");
                                                    File file1 = new File(dir, input.getText().toString() + ".usr");
                                                    try {
                                                        recipe.save(file);

                                                        try {
                                                            recipe.exportToUsr(file1);
                                                        }catch (Exception e)
                                                        {
                                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (Exception e){
                                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                    }

                                                    CaricaPaginaSendToCn(file.getPath());
                                                }
                                            });

                                    alertDialog1.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                   // Mci_write_Vb556_PCConnesso.valore = 1.0d;
                                                 //   Mci_write_Vb556_PCConnesso.write_flag = true;
                                                    Mostra_Tutte_Icone();

                                                }
                                            });
                                    alertDialog1.show();

                                } else {
                                    try {
                                        recipe.save(file);
                                        try {
                                            recipe.exportToUsr(file1);
                                        }catch (Exception e)
                                        {
                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e){
                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    CaricaPaginaSendToCn(file.getPath());
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Please insert Text",Toast.LENGTH_LONG).show();
                                //  onclickExit(view);
                            }
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            KillThread();
                            finish();

                        }
                    });



            alertDialog.setNeutralButton("Folder",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                            startActivityForResult(intent_par, POPUPFOLDER);
                        }
                    });

            // Showing Alert Message
            alertDialog.show();


        }

        //*************************************************************************************************
        // CaricaPaginaSendToCn
        //*************************************************************************************************
        private void CaricaPaginaSendToCn(String path) {
            StopThread = true;
            KillThread();

            Intent intent_par = new Intent(getApplicationContext(), Select_file_to_CN.class);
            intent_par.putExtra("File_path", path);
            intent_par.putExtra("operazione", "Saving....");


            startActivityForResult(intent_par,RESULT_PAGE_LOAD_EEP);
        }
        //**************************************************************************************************
        //
        //**************************************************************************************************

        private void GestioneVisualizzazioneToggleButton(MainActivity.Mci_write mci_write, Button button, String ic_press, String ic_unpress) {


            if (Double.compare( mci_write.valore_precedente,(Double) mci_write.mci.getValue()) != 0) {

                if((Double) mci_write.mci.getValue()== 1.0d) {

                    int image_Premuto = context.getResources().getIdentifier(ic_press, "drawable", context.getPackageName());
                    button.setBackground(context.getResources().getDrawable((image_Premuto)));

                }
                else
                {
                    int image_Premuto = context.getResources().getIdentifier(ic_unpress, "drawable", context.getPackageName());
                    button.setBackground(context.getResources().getDrawable((image_Premuto)));
                }

                mci_write.valore_precedente = (Double) mci_write.mci.getValue();
            }




        }
        //**************************************************************************************************
        //
        //**************************************************************************************************
        private void GestiscoMci_Out_Toggle(MainActivity.Mci_write mci_write) {
            switch (mci_write.mc_stati) {
                case 0:
                    if (mci_write.Fronte_positivo == true){

                        if((Double) mci_write.mci.getValue() == 0.0d ) {


                            mci_write.mci.setValue(1.0d);
                        }
                        else
                            mci_write.mci.setValue(0.0d);


                        sl.WriteItem(mci_write.mci);
                        mci_write.mc_stati = 10;
                        mci_write.Fronte_positivo = false;


                    }

                    break;

                case 10:

                    mci_write.mc_stati = 0;




                    break;

                default:

                    break;


            }
        }

        //****************************************************************************************************
        //
        //****************************************************************************************************
        private void ShowQuoteRelative() {
            try{
              TextView TextView_Dist_Val = (TextView) findViewById(R.id.textView_Dist_Val);
                if(info_modifica.QuoteRelativeAttive) {
                    TextView_xRel.setVisibility(View.VISIBLE);
                    TextView_YRel.setVisibility(View.VISIBLE);
                    TextView_Dist_Val.setVisibility(View.VISIBLE);
                    double XAttuale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                    double YAttuale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                    double XPartenza = info_modifica.DeltaX_inizio;
                    double YPartenza = info_modifica.DeltaY_inizio;

                    double DeltaX = Tools.roundTruncate005((float) (XAttuale - XPartenza));
                    DeltaX = Math.floor(DeltaX * 100) / 100;
                    double DeltaY = Tools.roundTruncate005((float) (YAttuale - YPartenza));
                    DeltaY = Math.floor(DeltaY * 100) / 100;
                    double val = ((XAttuale - XPartenza) * (XAttuale - XPartenza) + (YAttuale - YPartenza) * (YAttuale - YPartenza));
                    double distanza = Tools.roundTruncate005((float) Math.sqrt(val));
                    distanza = Math.floor(distanza * 100) / 100;


                    TextView_xRel.setText("" + DeltaX);
                    TextView_YRel.setText("" + DeltaY);
                    TextView_Dist_Val.setText("" + distanza);
                }

            }catch (Exception e){}




        }
        //****************************************************************************************************
        //
        //****************************************************************************************************
        private void ShowIndicePunto(int activeStepIndex) {
            if(activeStepIndex < 0)
                TextView_cnt_punti.setText("0");    //altrimenti mi scrive -1
            else
                TextView_cnt_punti.setText(""+recipe.getActiveStepIndex());
        }
        //*********************************************************************************************
        //Fai Esci
        //*********************************************************************************************
        private void Fai_Esci() {
            switch (step_Fai_Esci)
            {
                case 0:
                    MultiCmd_Vb4303_out_aggancio_pallet.setValue(0.0);  //mi assicuro che il pallet è agganciato altrimenti non si azzera
                    sl.WriteItem(MultiCmd_Vb4303_out_aggancio_pallet);
                    MultiCmd_Vn3081_override_rotaz.setValue(1000d);
                    sl.WriteItem(MultiCmd_Vn3081_override_rotaz);
                    MultiCmd_go_Home.setValue(1.0d);
                    sl.WriteItem(MultiCmd_go_Home);
                    step_Fai_Esci = 10;
                    break;

                case 10:
                    sl.ReadItem(MultiCmd_go_Home);
                    if((Double) MultiCmd_go_Home.getValue() ==0.0d)
                        step_Fai_Esci = 20;

                    break;

                case 20:
                    step_Fai_Esci = 0;

                    info_modifica.comando = Info_modifica.Comando.ESCI_DONE_AZZERAMENTO;
                    break;

                default:
                    break;
            }
        }
        //*********************************************************************************************
        //Emergenza
        //*********************************************************************************************
        private void Emergenza() {

            if((Double)MultiCmd_tasto_verde.getValue()==0.0d || (Double)MultiCmd_CH1_in_emergenza.getValue()==1.0d)
            {
                KillThread();
                Intent intent_emergenza = new Intent(getApplicationContext(),Emergency_page.class);

                startActivity(intent_emergenza);
            }
        }

        //*********************************************************************************************
        //Fai_Home
        //*********************************************************************************************
        private void Fai_Home() {
            switch (step_Home){

                case 0:
                    MultiCmd_go_Home.setValue(1.0d);
                    sl.WriteItem(MultiCmd_go_Home);
                    step_Home = 10;
                    break;

                case 10:
                    sl.ReadItem(MultiCmd_go_Home);
                    if((Double) MultiCmd_go_Home.getValue() ==0.0d)
                        step_Home = 20;

                    break;

                case 20:
                    step_Home = 0;
                    info_modifica.comando = Info_modifica.Comando.HOME_DONE;
                    break;

                default:
                    break;

            }

        }
        //**************************************************************************************************
        private void Moveto(JamPointStep activeStep) {
            switch (info_StepPiuMeno.MacStati_StepSingolo) {
                case 10:

                    MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
                    sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);


                    float X_destinazione = 0.0f, Y_destinazione = 0.0f;


                  //  X_destinazione =  recipe.pcX;
                  //  Y_destinazione =  recipe.pcY;
                    if(activeStep !=null) {

                        X_destinazione += activeStep.p.x;
                        Y_destinazione += activeStep.p.y;




                    }
                    else
                        info_StepPiuMeno.MacStati_StepSingolo = 0;
                    double X = Double.valueOf(X_destinazione);                              //invio quote al PLC
                    double Y = Double.valueOf(Y_destinazione);

                    Double LimiteXNegativo =(Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue()/1000;
                    Double LimiteXPositivo =(Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue()/1000;
                    Double LimiteYNegativo =(Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue()/1000;
                    Double LimiteYPositivo =(Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue()/1000;


                    //porcata pe cercare di risolvere da distanza un problema
                    if( Machine_model.equals("JT318M_1000x800")) {

                        LimiteYPositivo = LimiteYPositivo + 200;
                    }

                    if(X <LimiteXNegativo|| X >LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo)


                    {
                        X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                        Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                    }


                    // se sono già sopra le quote da raggiungere allora esco senza muovermi e ricomincio
                    if(X == (Double) MultiCmd_posizione_X.getValue() && Y == (Double) MultiCmd_posizione_Y.getValue())
                    {
                        info_StepPiuMeno.MacStati_StepSingolo = 0;
                        break;
                    }



                    MultiCmd_quota_destinazione_X.setValue(X*1000);
                    MultiCmd_quota_destinazione_Y.setValue(Y*1000);

                    MultiCmd_Vb72HmiMoveXY_C1.setValue(1.0d);


                    MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                            MultiCmd_Vb72HmiMoveXY_C1};
                    sl.WriteItems(Dati_out);

                    info_StepPiuMeno.MacStati_StepSingolo = 25;

                    break;


                case 25:
                    if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d ) { //aspetto che si muove


                            info_StepPiuMeno.MacStati_StepSingolo = 0;
                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.NULL;


                    }

                    break;

                default:

                    break;

            }
        }
        //**************************************************************************************************
        //*************************************************************************************************
        // StepSingolo
        //*************************************************************************************************
        private void StepSingolo(int nPunti) {
            switch (info_StepPiuMeno.MacStati_StepSingolo) {
                case 10:

                    if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d ) { //se X e Y sono fermi
                        info_StepPiuMeno.MacStati_StepSingolo = 20;
                    }
                    if(recipe.getActiveStepIndex() == -1 ||info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO ){     //se sono sul punto di carico
                        //alzo piedino nel caso faccio un psostamento dal punto di carico oppure faccio un aslto di n punti


                        MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
                        sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);

                    }
                    ElemSelezionati.clear();
                    break;

                case 20:
                    JamPointStep step = new JamPointStep();
                    if(info_StepPiuMeno.direzione == Info_StepPiuMeno.Direzione.AVANTI) {
                        try {
                            //alzo il piedino se è basso e se trovo un feed
                            if((recipe.isNextElementFeed())&& (double) MultiCmd_Status_Piedino.getValue() == 0.0d){

                                MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
                                sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);
                            }


                            for(int i= 0; i < nPunti; i++)
                                step = recipe.goToNextStep();


                            if(ElemSelezionati != null) ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity




                        }catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "getStepAfter catch Singolo", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else  {
                        try {
                            //alzo il piedino se è basso e se trovo un feed
                            if((recipe.isPreviousElementFeed())&& (double) MultiCmd_Status_Piedino.getValue() == 0.0d){

                                MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
                                sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);
                            }
                            //modifica per passa sul punto di carico quando faccio step- sul primo punto di cucitura
                            if(recipe.getActiveStepIndex() == 1){  //se devo andare al punto di carico
                                JamPointStep step1 = recipe.goToPreviousStep();    //lo uso per decrementare ActiveStepIndex

                                step.p.x = recipe.pcX;
                                step.p.y = recipe.pcY;

                            }else
                                {   //procedura normale per trovare lo step precedente
                                for (int i = 0; i < nPunti; i++)
                                    step = recipe.goToPreviousStep();
                            }

                            if(ElemSelezionati != null) ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "getStepBefore catch Singolo", Toast.LENGTH_SHORT).show();
                        }
                    }



                    float X_destinazione = 0.0f, Y_destinazione = 0.0f;
                    if(step == null){

                        MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
                        sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);
                        X_destinazione = recipe.pcX;
                        Y_destinazione = recipe.pcY;
                    }
                    else{

                    }


                    if(step !=null) {


                        //else {
                            X_destinazione += step.p.x;                                  //quota X destinazione del primo step dell'element successivo
                            Y_destinazione += step.p.y;                                          //quota Y destinazione del primo step dell'element successivo
                        //}
                    }
                    else
                        info_StepPiuMeno.MacStati_StepSingolo = 0;
                    double X = Double.valueOf(X_destinazione);                              //invio quote al PLC
                    double Y = Double.valueOf(Y_destinazione);


                    Double LimiteXNegativo =(Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue()/1000;
                    Double LimiteXPositivo =(Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue()/1000;
                    Double LimiteYNegativo =(Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue()/1000;
                    Double LimiteYPositivo =(Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue()/1000;

                    //porcata pe cercare di risolvere da distanza un problema
                    if( Machine_model.equals("JT318M_1000x800")) {

                        LimiteYPositivo = LimiteYPositivo + 200;
                    }



                    if(X <LimiteXNegativo|| X >LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo)
                    {
                        X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                        Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                    }


                    // se sono già sopra le quote da raggiungere allora esco senza muovermi e ricomincio
                    if(X == (Double) MultiCmd_posizione_X.getValue() && Y == (Double) MultiCmd_posizione_Y.getValue())
                    {
                        info_StepPiuMeno.MacStati_StepSingolo = 0;
                        break;
                    }



                    MultiCmd_quota_destinazione_X.setValue(X*1000);
                    MultiCmd_quota_destinazione_Y.setValue(Y*1000);


                    MultiCmd_Vb72HmiMoveXY_C1.setValue(1.0d);


                    MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                            MultiCmd_Vb72HmiMoveXY_C1};
                    sl.WriteItems(Dati_out);

                    info_StepPiuMeno.MacStati_StepSingolo = 25;

                    break;


                case 25:
                    if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d ) { //aspetto che si muove
                        info_StepPiuMeno.MacStati_StepSingolo = 30;
                    }



                    break;
                case 30:
                    if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d ) { //aspetto che sono fermo

                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.VELOCE && info_StepPiuMeno.comando != Info_StepPiuMeno.Comando.STOP) {
                            info_StepPiuMeno.MacStati_StepSingolo = 10;
                        }
                        else {
                            info_StepPiuMeno.MacStati_StepSingolo = 0;
                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.NULL;

                        }
                    }

                    break;

                default:

                    break;

            }
        }

        //*************************************************************************************************
        //
        //*********************************************************************************************
        private void Scrivi_codice_HMI() {

            List<JamPointCode> codeStatus = recipe.getActiveStepCodes(); //getStepCodes(punto_attuale);

            int punto_attuale = recipe.getActiveStepIndex();



            if (codeStatus.size() > 0) {

                Button_delete_code.setVisibility(View.VISIBLE);
                String stringa_codice = "Code: ";
                String delimiter ="";
                for (JamPointCode code : codeStatus) {
                    String valore_codice = ""+code.value;
                    String tipo_codice = code.codeType.toString();

                    switch (tipo_codice) {
                        case "OP1":
                            if(code.value==1)
                                stringa_codice = stringa_codice + delimiter + " OP1 ON";
                            else
                                stringa_codice = stringa_codice + delimiter + " OP1 OFF";
                            break;
                        case "OP2":
                            if(code.value==1)
                                stringa_codice = stringa_codice + delimiter + " OP2 ON";
                            else
                                stringa_codice = stringa_codice + delimiter + " OP2 OFF";
                            break;
                        case "OP3":
                            if(code.value==1)
                                stringa_codice = stringa_codice + delimiter + " OP3 ON";
                            else
                                stringa_codice = stringa_codice + delimiter + " OP3 OFF";
                            break;
                        case "SPEED_M8":
                            stringa_codice = stringa_codice + delimiter + " SPEED :" + code.value;

                            break;


                        case "RASAFILO1":
                            // stringa_codice = stringa_codice + tipo_codice;
                            if (valore_codice.equals("0"))
                                stringa_codice = stringa_codice + delimiter + " Trim1 Enable ";
                            else stringa_codice = stringa_codice + delimiter + " Trim1 Disable";

                            break;

                    }
                    delimiter ="||";
                }
                TextView_Code.setText(stringa_codice);
            } else {
                TextView_Code.setText("Code:");

                Button_delete_code.setVisibility(View.GONE);
            }


        } //*************************************************************************************************
        // Show_info_entità
        //*************************************************************************************************
        private void Show_info_entità() {
            //info

            if(ElemSelezionati.size() > 0) {

                if (ElemSelezionati.get(0) instanceof ElementFeed) {
                    ImageView.setVisibility(View.GONE);
                    TextView_valore_A.setVisibility(View.GONE);
                    TextView_valore_B.setVisibility(View.GONE);
                    Button_traform_toLine.setVisibility(View.VISIBLE);
                    Button_traform_toZigZag.setVisibility(View.VISIBLE);
                    Button_traform_toFeed.setVisibility(View.VISIBLE);
                    Button_explode.setVisibility(View.GONE);
                } else {
                    ImageView.setVisibility(View.VISIBLE);
                    Button_traform_toLine.setVisibility(View.VISIBLE);
                    Button_traform_toZigZag.setVisibility(View.VISIBLE);
                    Button_traform_toFeed.setVisibility(View.VISIBLE);
                    Button_explode.setVisibility(View.VISIBLE);
                    if (ElemSelezionati.get(0) instanceof ElementLine || ElemSelezionati.get(0) instanceof ElementArc) {
                        ImageView.setBackground(getResources().getDrawable(R.drawable.info_cucitura));
                        TextView_valore_A.setVisibility(View.VISIBLE);
                        TextView_valore_B.setVisibility(View.GONE);
                        TextView_valore_A.setText("" + ElemSelezionati.get(0).stepLength);

                    }
                    if (ElemSelezionati.get(0) instanceof ElementLineZigZag) {
                        ImageView.setBackground(getResources().getDrawable(R.drawable.info_travetta));
                        TextView_valore_B.setVisibility(View.VISIBLE);
                        TextView_valore_A.setVisibility(View.VISIBLE);
                        TextView_valore_B.setText("" + ((ElementLineZigZag) ElemSelezionati.get(0)).height);
                        TextView_valore_A.setText("" + ((ElementLineZigZag) ElemSelezionati.get(0)).stepLength);

                    }
                }
            }else
            {
                ImageView.setVisibility(View.GONE);
                TextView_valore_A.setVisibility(View.GONE);
                TextView_valore_B.setVisibility(View.GONE);
                Button_traform_toLine.setVisibility(View.GONE);
                Button_traform_toZigZag.setVisibility(View.GONE);
                Button_traform_toFeed.setVisibility(View.GONE);
                Button_explode.setVisibility(View.GONE);
            }


        }
    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestioneEntitaPiu() {

        if(onClickTastoEntitaPiu) {

            onClickTastoEntitaPiu = false;

            MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
            sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);

            float X_destinazione = 0.0f, Y_destinazione = 0.0f;

            if ( (Double) MultiCmd_XY_fermi.getValue() == 1.0d ) {
                if (List_entità.size() > 0) {


                    if (recipe.getActiveStepIndex() == -1)    //nel caso entrando ero a -1 faccio un agonoNextStep per andare al primo punto.
                    {
                        recipe.goToNextStep();
                        //ElemSelezionati = recipe.selectNextEntity();
                       // ElemSelezionati = recipe.selectNextEntity();
                    } else
                    if( info_modifica.comando ==  info_modifica.comando.SPOSTA2){   //se avevo questi comandi e ho ripremuto E+ continuo a inserire le entità in modo da farle tutte gialle
                        List<Element> el =  recipe.selectNextEntity();
                        for (Element  element : el) {
                            ElemSelezionati.add(element);

                        }
                    }
                    else{
                        for (Element elementi : ElemSelezionati) {     //selezione
                            elementi.isSelected = false;       //seleziono gli element dell'entità
                        }
                        ElemSelezionati = recipe.selectNextEntity();   //seleziono solo una entità, solo una sarà gialla


                    }
                    X_destinazione = recipe.getSelectedEntityStartPoint().x;  // + recipe.pcX;                                      //quota X destinazione del primo step dell'element successivo
                    Y_destinazione = recipe.getSelectedEntityStartPoint().y;  // + recipe.pcY;

                    /*
                    if (recipe.getActiveStepIndex() == -1)    //nel caso entrando ero a -1 faccio un altro Next altrimenti alla prima pressione non si muove
                    {
                        ElemSelezionati = recipe.selectNextEntity();
                        ElemSelezionati = recipe.selectNextEntity();
                    } else
                        ElemSelezionati = recipe.selectNextEntity();


                    X_destinazione = recipe.getSelectedEntityStartPoint().x;  // + recipe.pcX;                                      //quota X destinazione del primo step dell'element successivo
                    Y_destinazione = recipe.getSelectedEntityStartPoint().y;  // + recipe.pcY;                                      //quota Y destinazione del primo step dell'element successivo
*/




                    try {
                        double X = Double.valueOf(X_destinazione);
                        double Y = Double.valueOf(Y_destinazione);



                        Double LimiteXNegativo =(Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue()/1000;
                        Double LimiteXPositivo =(Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue()/1000;
                        Double LimiteYNegativo =(Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue()/1000;
                        Double LimiteYPositivo =(Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue()/1000;

                        //porcata pe cercare di risolvere da distanza un problema
                        if( Machine_model.equals("JT318M_1000x800")) {

                            LimiteYPositivo = LimiteYPositivo + 200;
                        }

                        if(X <LimiteXNegativo|| X >LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo)

                        {
                            X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                            Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                        }

                        info_modifica.QuoteRelativeAttive = true;
                        //invio quote al PLC



                        MultiCmd_quota_destinazione_X.setValue(X*1000);
                        MultiCmd_quota_destinazione_Y.setValue(Y*1000);

                        MultiCmd_Vb72HmiMoveXY_C1.setValue(1.0d);


                        MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                                MultiCmd_Vb72HmiMoveXY_C1};
                        sl.WriteItems(Dati_out);






                    } catch (Exception e) {
                    }

                    for (Element elementi : List_entità) {     //selezione
                        elementi.isSelected = false;       //deseleziono tutti gli element
                    }
                    for (Element elementi : ElemSelezionati) {     //selezione
                        elementi.isSelected = true;       //seleziono gli element dell'entità
                    }


                }
            }
        }

    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void GestioneEntitaMeno() {
        if(onClickTastoEntitaMeno) {
            onClickTastoEntitaMeno = false;

            MultiCmd_Vb74_RichiestaPiedinoSu_C1.setValue(1.0d);
            sl.WriteItem(MultiCmd_Vb74_RichiestaPiedinoSu_C1);

            float X_destinazione = 0.0f, Y_destinazione = 0.0f;

            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {

                if (List_entità.size() > 0) {

                    if (recipe.getActiveStepIndex() == -1)    //nel caso entrando ero a -1 faccio un goToPreviousStep per posizionarmi all'ultimo punto
                    {
                        recipe.goToPreviousStep();
                        recipe.selectNextEntity();

                    }


                    ElemSelezionati = recipe.selectPreviousEntity();

                    try {
                        //Set_button_PiuMeno_invisibili();
                        X_destinazione = recipe.getSelectedEntityStartPoint().x ; //+ recipe.pcX;                                      //quota X destinazione del primo step dell'element successivo
                        Y_destinazione = recipe.getSelectedEntityStartPoint().y; // + recipe.pcY;                                      //quota Y destinazione del primo step dell'element successivo

                        //  info_modifica.QuoteRelativeAttive = true;
                        double X = Double.valueOf(X_destinazione);                              //invio quote al PLC

                        double Y = Double.valueOf(Y_destinazione);



                        MultiCmd_quota_destinazione_X.setValue(X*1000);
                        MultiCmd_quota_destinazione_Y.setValue(Y*1000);

                        MultiCmd_Vb72HmiMoveXY_C1.setValue(1.0d);


                        MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                                MultiCmd_Vb72HmiMoveXY_C1};
                        sl.WriteItems(Dati_out);


                        for (Element elementi : List_entità) {     //selezione
                            elementi.isSelected = false;       //deseleziono tutti gli element
                        }
                        for (Element elementi : ElemSelezionati) {     //selezione
                            elementi.isSelected = true;       //seleziono gli element dell'entità
                        }




                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    //*************************************************************************************************
    //
    //*************************************************************************************************
    private void Aggiorna_canvas() {


        myView.Ricalcola_entità_canvas(recipe.elements);
        myView.AggiornaCanvas(true);

    }

    //*************************************************************************************************
    // Center on click
    //*************************************************************************************************
    public void btn_Center_on_click(View view) throws IOException
    {
        myView.Center_Bitmap_Main(0.4F,-10,20);
        myView.AggiornaCanvas(true);
    }
    //*************************************************************************************************
    // Zoom + on click
    //*************************************************************************************************
    public void btn_ZoomPiu_on_click(View view) throws IOException
    {

        myView.Zoom(0.1F);
        myView.AggiornaCanvas(true);



    }
    //*************************************************************************************************
    // Zoom - on click
    //*************************************************************************************************
    public void btn_ZoomMeno_on_click(View view) throws IOException
    {

        myView.Zoom( - 0.1F);
        myView.AggiornaCanvas(true);

    }
    //*************************************************************************************************
    // on_click_move_to_home
    //*************************************************************************************************
    public void on_click_move_to_home(View view) throws IOException {

        recipe.clearActiveStep();  //imposta indice step a -1
        info_StepPiuMeno.MacStati_StepSingolo = 0; //pulisco
        SpegniTutteIcone();
        Mci_Vn3081_override_rotaz.valore = 1000d;
        Mci_Vn3081_override_rotaz.write_flag = true;
        info_modifica.comando = Info_modifica.Comando.HOME;

    }
    //*************************************************************************************************
    // onclick_Undo
    //*************************************************************************************************
    public void onclick_Undo(View view) throws IOException {

        try {
            Mci_Vb_OutPiedino_su.valore = 1.0d;
            Mci_Vb_OutPiedino_su.write_flag = true;

            JamPointStep step = recipe.getActiveStep();

            if(step == null){
                try {
                    recipe.undo();
                    recipe.clearActiveStep();  //imposta indice step a -1
                }catch
                (Exception e){}

                info_modifica.comando = Info_modifica.Comando.HOME;
               // MultiCmd_Vb558_PCHome.setValue(1.0d); // mi serve per non far ricomparire le icone dal thread della grafica
                SpegniTutteIcone();
             //  Fai_Home();

            }else {


                try {
                    PointF p = new PointF(step.p.x, step.p.y);

                    recipe.undo();


                    JamPointStep step_risultato = recipe.goToNearestStep(p, 12.7d);
                    if (step_risultato == null) {

                        recipe.clearActiveStep();  //imposta indice step a -1
                        info_modifica.comando = Info_modifica.Comando.HOME;
                      //  MultiCmd_Vb558_PCHome.setValue(1.0d); // mi serve per non far ricomparire le icone dal thread della grafica
                        SpegniTutteIcone();
                      //  Fai_Home();
                    }
                } catch (Exception e) {
                }
            }

            Aggiorna_canvas();
        }catch ( Exception e){}
    }
    //*************************************************************************************************
    // onclick_New
    //*************************************************************************************************
    public void onclick_New(View view) throws IOException {

        recipe = new Recipe();
        recipe.pcX = 0.1f;
        recipe.pcY = 0.1f;
        recipe.clearActiveStep();  //imposta indice step a -1
        recipe.setDrawPosition(new PointF(0.1f, 0f));

        Aggiorna_canvas();

    }
    //*************************************************************************************************
    // onclick_Redo
    //*************************************************************************************************
    public void onclick_Redo(View view) throws IOException {

        Mci_Vb_OutPiedino_su.valore = 1.0d;
        Mci_Vb_OutPiedino_su.write_flag = true;

        try {
            recipe.redo();
        }catch (Exception e)
        {}
        Aggiorna_canvas();

    }
    //*************************************************************************************************
    // onclickExit
    //*************************************************************************************************
    public void onclickExit(final View view)
    {
        SpegniTutteIcone();
        Button_exit.setVisibility(View.GONE);
        if(Values.Debug_mode) info_modifica.comando = Info_modifica.Comando.ESCI_DONE_AZZERAMENTO;
        else
        info_modifica.comando = Info_modifica.Comando.ESCI;


    }
    //*************************************************************************************************
    // on_Click_Tasto_Entita_piu
    //*************************************************************************************************
    public void on_Click_Tasto_Entita_piu(View view) throws IOException
    {

        onClickTastoEntitaPiu = true;


    }
    //*************************************************************************************************
    // on_Click_Tasto_Entita_meno
    //*************************************************************************************************
    public void on_Click_Tasto_Entita_meno(View view) throws IOException
    {
        onClickTastoEntitaMeno = true;



    }
    //*************************************************************************************************
    // onclick_Esc
    //*************************************************************************************************
    public void onclick_Esc(View view) throws IOException {


        recipe.selectionStepClear();
        info_StepPiuMeno.MacStati_StepSingolo = 0;
        Visualizza_tutti_Button();
        TextView_info.setText("");
        Mci_Vb_OutPiedino_su.valore = 1.0d;
        Mci_Vb_OutPiedino_su.write_flag = true;




    }
    //*************************************************************************************************
    // onclick_button_tasto_enter
    //*************************************************************************************************
    public void onclick_button_tasto_enter(View view) throws IOException {
        boolean risultato;

        try {
            if (info_modifica.comando != Info_modifica.Comando.Null) {
                switch (info_modifica.comando) {
                    case ARCO3P_0:  //da finire


                        info_modifica.X_Middle = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        info_modifica.Y_Middle = (Double) MultiCmd_posizione_Y.getValue() / 1000d;


                        TextView_info.setText(getString(R.string.Arco3P_p3));  //Muovere usando le frecce poi premi Enter
                        info_modifica.QuoteRelativeAttive = true;
                        info_modifica.comando = Info_modifica.Comando.ARCO3P_1;


                        break;
                    case ARCO3P_1:  //da finire
                        info_modifica.X_End = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        info_modifica.Y_End = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto
                        info_modifica.comando = Info_modifica.Comando.ARCO3P_2;
                        break;
                    case ARCO3P_2:

                        float LP_arco = old_A;
                        if (isNumeric((String) TextView_info.getText())) {
                            try {
                                LP_arco = Float.parseFloat((String) TextView_info.getText());
                                old_A = LP_arco;


                            } catch (Exception e) {
                                TextView_info.setText(getString(R.string.Errore));
                            }


                        }
                        else
                        {
                            LP_arco = old_A;
                        }
                        //daniele 18/06/20
                        //se ho raggiunto il punto finale dell'arco con il tasto + porto in dietro lo step attivo altrimenti no.
                        int idx_attivo = recipe.getActiveStepIndex();
                        if(idx_attivo > info_modifica.id_punto_inizio_modifica) {       //se ho raggiunto il punto finale dell'arco con il tasto +
                            for (int i = idx_attivo; i > info_modifica.id_punto_inizio_modifica+1; i--) {
                                recipe.goToPreviousStep();
                            }

                        }
                        //

                        idx_attivo = recipe.getActiveStepIndex();
                        if(idx_attivo >= info_modifica.id_punto_inizio_modifica) {
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Element el = recipe.drawArcTo(new PointF(((float) info_modifica.X_Middle ), ((float) info_modifica.Y_Middle)), new PointF(((float) info_modifica.X_End ), ((float) info_modifica.Y_End)), LP_arco);
                            el.stepLength = LP_arco;
                            TextView_info.setText(getString(R.string.Fatto));  //
                        }else
                        {
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        Set_Tutti_button_comandi_visibili();
                        info_modifica.QuoteRelativeAttive = false;
                        Aggiorna_canvas();




                        Mci_Vb_OutPiedino_su.valore = 1.0d;
                        Mci_Vb_OutPiedino_su.write_flag = true;


                        break;

                    case FEED:
                        try {
                            double Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                            double Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

                            JamPointStep StepAttuale = recipe.getActiveStep();
                            //int f = recipe.elements.size();
                            if(StepAttuale == null ||  recipe.elements.size() ==0) {   //probabilemnte è il primo feed di un programma vutot

                                double  x = (double)info_modifica.DeltaX_inizio;
                                float x_f =  Tools.roundTruncate005((float)x);
                                double  y = (double)info_modifica.DeltaY_inizio;
                                float y_f = Tools.roundTruncate005((float)y);
                             //   recipe.setDrawPosition(new PointF(x_f, y_f));
                                recipe.setDrawPosition(new PointF(x_f, y_f));
                                recipe.pcX = x_f;
                                recipe.pcY = y_f;
                            }
                            else
                            recipe.setDrawPosition(new PointF(StepAttuale.p.x,StepAttuale.p.y ));

                            recipe.drawFeedTo(new PointF(((float) Xfinale ), ((float) Yfinale)));

                            info_modifica.comando = Info_modifica.Comando.Null;
                            Set_Tutti_button_comandi_visibili();
                            info_modifica.QuoteRelativeAttive = false;
                            Aggiorna_canvas();
                            TextView_info.setText(getString(R.string.Fatto));  //
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        break;
                    case CANCELLA:
                        risultato = false;
                        try{
                            if (ElemSelezionati.size() == 0) {   //Steps
                                if (recipe.getActiveStepIndex() == info_modifica.id_punto_inizio_modifica) {   //un punto
                                    risultato = recipe.deleteActiveStep();
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = recipe.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    recipe.clearInvalidCodes();
                                    info_modifica.comando = Info_modifica.Comando.Null;
                                } else   //più punti
                                {
                                    recipe.selectionStepEnd();
                                    risultato = recipe.deleteSelectedSteps();
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = recipe.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    recipe.clearInvalidCodes();
                                    info_modifica.comando = Info_modifica.Comando.Null;
                                }

                                recipe.selectionStepClear();
                                info_modifica.comando = Info_modifica.Comando.Null;
                            }

                            if (risultato) {
                                TextView_info.setText(getString(R.string.Fatto));
                                Set_Tutti_button_comandi_visibili();

                                Aggiorna_canvas();
                            } else {
                                TextView_info.setText(getString(R.string.Errore));
                                info_modifica.Init_info_modifica();
                                Set_Tutti_button_comandi_visibili();
                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;

                    case LINEA:


                        double Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;



                        TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto

                        if (Math.abs(Xfinale - info_modifica.DeltaX_inizio) > 0 || Math.abs(Yfinale - info_modifica.DeltaY_inizio) > 0) {
                            info_modifica.comando = Info_modifica.Comando.LINEA1;
                            info_modifica.DeltaX_inizio = Xfinale;
                            info_modifica.DeltaY_inizio = Yfinale;
                        } else {
                            //non ho mosso le frecce allora nnullo il comando
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Set_Tutti_button_comandi_visibili();
                            info_modifica.Init_info_modifica();
                            TextView_info.setText("");
                        }
                        break;

                    case LINEA1:
                        try{
                            if (isNumeric((String) TextView_info.getText())) {
                                try {
                                    float LP = Float.parseFloat((String) TextView_info.getText());
                                    info_modifica.LP = LP;
                                    old_A = LP;
                                } catch (Exception e) {
                                    TextView_info.setText(getString(R.string.Errore));
                                }
                            } else {
                                info_modifica.LP = old_A;
                            }

                            info_modifica.QuoteRelativeAttive = false;

                            float X = info_modifica.DeltaX_inizio.floatValue();
                            float Y = info_modifica.DeltaY_inizio.floatValue();


                            JamPointStep StepAttuale = recipe.getActiveStep();
                            recipe.setDrawPosition(new PointF(StepAttuale.p.x,StepAttuale.p.y ));

                            if(info_modifica.StepAttivo != StepAttuale && info_modifica.StepAttivo != null)     //caso in cui ho avanzato con + e non con le frecce
                            {
                                TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto
                                recipe.selectionStepEnd();
                                recipe.joinSelectedStepsByLine();
                            }
                            else
                            {
                              //	Element el_linea = recipe.drawLineTo(new PointF(X - recipe.pcX, Y - recipe.pcY), info_modifica.LP);
                                Element el_linea = recipe.drawLineTo(new PointF(X , Y ), info_modifica.LP);
                               	el_linea.stepLength = info_modifica.LP;
                                el_linea.createSteps();
                            }
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Aggiorna_canvas();
                            Set_Tutti_button_comandi_visibili();
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;

                            TextView_info.setText(getString(R.string.Fatto));

                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        break;

                    case SPOSTA1:
                        int step_index = recipe.activeStepIndex;
                        recipe.selectionStepEnd();
                        if(step_index == -1){

                            if( CheckPuntisovrapposti( Coord_Pinza.XCoordPosPinza,Coord_Pinza.YCoordPosPinza, recipe.pcX, recipe.pcY)){

                                info_modifica.puntoCarico = true;
                            }
                        }

                        info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        info_modifica.comando = Info_modifica.Comando.SPOSTA2;
                        info_modifica.id_punto_fine_modifica = recipe.getActiveStepIndex();
                        TextView_info.setText(getString(R.string.StringMove2));  //Muovere usando le frecce poi premi Enter
                        info_modifica.QuoteRelativeAttive = true;


                        break;
                    case SPOSTA2:

                        try {
                            Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                            Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                            info_modifica.QuoteRelativeAttive = false;
                            if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;


                                if (ElemSelezionati.size() == 0) {   //Steps
                                    if (info_modifica.puntoCarico) {   //sono sul punto di carico?

                                        info_modifica.puntoCarico = false;
                                        recipe.pcX = recipe.pcX + (float) DeltaX;
                                        recipe.pcY = recipe.pcY + (float) DeltaY;
                                        if (recipe.elements.size() > 0) {
                                            Element element = recipe.elements.get(0);
                                            element.pStart.x = recipe.pcX;
                                            element.pStart.y = recipe.pcY;
                                        }


                                    } else {  //non sono sul punto di carico

                                        if (info_modifica.id_punto_fine_modifica == info_modifica.id_punto_inizio_modifica) {

                                            recipe.moveActiveStep((float) DeltaX, (float) DeltaY);    //sposto solo un punto
                                            info_modifica.comando = Info_modifica.Comando.Null;
                                        } else {
                                            recipe.moveSelectedSteps((float) DeltaX, (float) DeltaY);     //sposto tutti i punti
                                            info_modifica.comando = Info_modifica.Comando.Null;
                                        }
                                        recipe.selectionStepClear();

                                        List<JamPointCode> ListCodici = new ArrayList<>();
                                        ListCodici = recipe.checkInvalidCodes(true);
                                        if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                        recipe.clearInvalidCodes();


                                    }
                                } else   //entity
                                {
                                    Element lastElem = ElemSelezionati.get(ElemSelezionati.size() - 1);
                                    for(int i = ElemSelezionati.size() - 1; i > 0; i--)
                                    {
                                        if(ElemSelezionati.get(i) instanceof ElementLine || ElemSelezionati.get(i) instanceof ElementLine)
                                        {
                                            lastElem = ElemSelezionati.get(i);
                                            break;
                                        }
                                    }
                                    info_modifica.id_element_fine_modifica = recipe.elements.indexOf(lastElem);
                                    JamPointStep step = recipe.getActiveStep();
                                    PointF p = new PointF(step.p.x, step.p.y);      //quota dell'ultimo punto attivo

                                    if (info_modifica.id_element_fine_modifica == info_modifica.id_element_inizio_modifica)    //se ho una sola entità

                                        recipe.moveSelectedEntity((float) DeltaX, (float) DeltaY,info_modifica.id_element_inizio_modifica,info_modifica.id_element_fine_modifica); //sposto entity
                                    else
                                        //se ho selezionato più entità
                                        recipe.moveElements(info_modifica.id_element_inizio_modifica, info_modifica.id_element_fine_modifica, (float) DeltaX, (float) DeltaY);

                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = recipe.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    recipe.clearInvalidCodes();

                                    info_modifica.comando = Info_modifica.Comando.Null;
                                }
                                recipe.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate

                                Aggiorna_canvas();

                                //ElemSelezionati.clear();


                                Set_Tutti_button_comandi_visibili();
                                TextView_info.setText(getString(R.string.Fatto));  //
                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;

                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case STRETCH:

                        recipe.selectionStepEnd();
                        TextView_info.setText(getString(R.string.StretchStep));  //Muovere con +- fino allo step di stretch
                        info_modifica.comando = Info_modifica.Comando.STRETCH1;
                        break;

                    case STRETCH1:

                        info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        TextView_info.setText(getString(R.string.StretchPointFrecce));  //Muovere con frecce per stirare il punto
                        info_modifica.comando = Info_modifica.Comando.STRETCH2;
                        break;

                    case STRETCH2:
                        try {
                            Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                            Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;


                            double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                            double DeltaY = Yfinale - info_modifica.DeltaY_inizio;


                            boolean ret = recipe.stretchActiveStep((float) DeltaX, (float) DeltaY);
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Aggiorna_canvas();

                            Set_Tutti_button_comandi_visibili();
                            if (ret) {
                                List<JamPointCode> ListCodici = new ArrayList<>();
                                ListCodici = recipe.checkInvalidCodes(true);
                                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                recipe.clearInvalidCodes();


                                TextView_info.setText(getString(R.string.Fatto));

                            } else
                                TextView_info.setText(getString(R.string.Errore));

                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;


                    case ZIGZAG:    //da finire

                        info_modifica.X_End = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        info_modifica.Y_End = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

                        info_modifica.QuoteRelativeAttive = false;
                        TextView_info.setText(getString(R.string.ZigZag1)+" (" + old_A + ")");  //
                        info_modifica.comando = Info_modifica.Comando.ZIGZAG_1;
                        break;

                    case ZIGZAG_1:
                        if (isNumeric((String) TextView_info.getText())) {
                            try {

                                info_modifica.AltezzaZigZag = Float.parseFloat((String) TextView_info.getText());
                                old_A = info_modifica.AltezzaZigZag;
                                TextView_info.setText(getString(R.string.ZigZag2)+" (" + old_B + ")");  //
                                info_modifica.comando = Info_modifica.Comando.ZIGZAG_2;
                            } catch (Exception e) {
                                TextView_info.setText(getString(R.string.Errore));
                            }


                        }else
                        {
                            info_modifica.AltezzaZigZag = old_A;
                            TextView_info.setText(getString(R.string.ZigZag2)+" (" + old_B + ")");  //
                            info_modifica.comando = Info_modifica.Comando.ZIGZAG_2;


                        }


                        break;

                    case ZIGZAG_2:
                        try {
                            float Passo_ZigZag = old_B;
                            if (isNumeric((String) TextView_info.getText())) {
                                try {

                                    Passo_ZigZag = Float.parseFloat((String) TextView_info.getText());
                                    old_B = Passo_ZigZag;

                                } catch (Exception e) {
                                    TextView_info.setText(getString(R.string.Errore));
                                }


                            }
                            else
                            {
                                Passo_ZigZag = old_B;


                            }

                            JamPointStep StepAttuale = recipe.getActiveStep();

                            if(info_modifica.StepAttivo != StepAttuale && info_modifica.StepAttivo != null)     //caso in cui sono avanzato con + e non con le frecce
                            {
                                recipe.setDrawPosition(new PointF(info_modifica.StepAttivo.p.x,info_modifica.StepAttivo.p.y ));
                                recipe.goToPreviousStep();
                                Element el_z = recipe.drawLineZigZagTo(new PointF(StepAttuale.p.x, StepAttuale.p.y),  info_modifica.AltezzaZigZag,Passo_ZigZag);
                                el_z.stepLength = Passo_ZigZag;
                                if (el_z instanceof ElementLineZigZag)
                                    ((ElementLineZigZag) el_z).height = info_modifica.AltezzaZigZag;
                                el_z.createSteps();

                            }
                            else {

                                recipe.setDrawPosition(new PointF(StepAttuale.p.x,StepAttuale.p.y ));

                                Element el_z = recipe.drawLineZigZagTo(new PointF(((float) info_modifica.X_End ), ((float) info_modifica.Y_End) ),  info_modifica.AltezzaZigZag,Passo_ZigZag);
                                el_z.stepLength = Passo_ZigZag;
                                if (el_z instanceof ElementLineZigZag)
                                    ((ElementLineZigZag) el_z).height = info_modifica.AltezzaZigZag;
                                el_z.createSteps();

                            }
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Set_Tutti_button_comandi_visibili();
                            TextView_info.setText(getString(R.string.Fatto));  //
                            Aggiorna_canvas();


                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        break;
                    case M888:
                        try {
                            if (isNumeric((String) TextView_info.getText())) {
                                try {
                                    float LP = Float.parseFloat((String) TextView_info.getText());
                                    recipe.modify(LP);

                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = recipe.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    recipe.clearInvalidCodes();


                                    info_modifica.comando = Info_modifica.Comando.Null;
                                    Set_Tutti_button_comandi_visibili();
                                    TextView_info.setText(getString(R.string.Fatto));  //
                                    Aggiorna_canvas();
                                } catch (Exception e) {
                                    TextView_info.setText(getString(R.string.Errore));
                                }


                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case SPOSTA_ALL:
                        try {
                            if(recipe.getActiveStepIndex() == -1){  //controllo se sono sul punto di carico
                                if (recipe.elements.size() > 0) {
                                    float PCX = recipe.pcX;
                                    float PCY = recipe.pcY;

                                    Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                                    Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                                    info_modifica.QuoteRelativeAttive = true;
                                    if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                        double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                        double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                        recipe.move((float) DeltaX, (float) DeltaY);
                                        recipe.pcX = PCX;  //rimetto il pc come era prima
                                        recipe.pcY = PCY;  //rimetto il pc come era prima
                                        recipe.elements.get(0).pStart = new PointF(PCX, PCY); // FirstEntityPoint;
                                        Aggiorna_canvas();
                                        info_modifica.comando = Info_modifica.Comando.Null;
                                        Set_Tutti_button_comandi_visibili();
                                        TextView_info.setText(getString(R.string.Fatto));  //
                                    }
                                }
                            }else
                            {//non sono sul punto di carico allora sposto tutto quello che c'è dopo
                                Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                                Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                int id_step_start = recipe.getActiveStepIndex();
                                int id_elem_start = recipe.elements.indexOf(recipe.getActiveStep().element);
                               // int  id_end =  recipe.elements.indexOf(recipe.elements.size()-1) ;
                                int  id_end = recipe.elements.size()-1 ;


                                recipe.moveElements(id_elem_start,id_end,(float) DeltaX, (float) DeltaY);

                                List<JamPointCode> ListCodici = new ArrayList<>();
                                ListCodici = recipe.checkInvalidCodes(true);
                                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                recipe.clearInvalidCodes();
                                Aggiorna_canvas();
                                info_modifica.comando = Info_modifica.Comando.Null;
                                Set_Tutti_button_comandi_visibili();
                                TextView_info.setText(getString(R.string.Fatto));  //

                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case RADDRIZZA_LINEA:
                        try {
                            if (info_modifica.id_punto_inizio_modifica != recipe.getActiveStepIndex()) {
                                recipe.selectionStepEnd();
                                boolean result = recipe.joinSelectedStepsByLine();
                                recipe.selectionStepClear();
                                info_modifica.comando = Info_modifica.Comando.Null;
                                Aggiorna_canvas();
                                Set_Tutti_button_comandi_visibili();
                                if (result) {
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = recipe.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    recipe.clearInvalidCodes();

                                    TextView_info.setText(getString(R.string.Fatto));  //

                                } else
                                    TextView_info.setText(getString(R.string.Errore));  //

                            }




                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;


                    case RADDRIZZA_ARCO_ENTITA:

                        //entity
                        info_modifica.ElemSelezionati.clear();  //pulisco
                        double XMiddle = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double YMiddle = (Double) MultiCmd_posizione_Y.getValue() / 1000d;


                        recipe.transformSelectedEntityToArc(new PointF((float) XMiddle, (float) YMiddle));

                        List<JamPointCode> ListCodici_Ent = new ArrayList<>();
                        ListCodici_Ent = recipe.checkInvalidCodes(true);
                        if (ListCodici_Ent.size() > 0) ShowCodeToast(ListCodici_Ent);
                        recipe.clearInvalidCodes();


                        info_modifica.comando = Info_modifica.Comando.Null;
                        TextView_info.setText(getString(R.string.Fatto));
                        Set_Tutti_button_comandi_visibili();
                        Aggiorna_canvas();




                        break;

                    case RADDRIZZA_ARCO:

                        if (info_modifica.ElemSelezionati.size() == 0)
                        {
                           info_modifica.id_punto_middle_modifica = recipe.getActiveStepIndex();
                          //  recipe.selectionStepEnd();
                            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO1;
                            //TextView_info.setText(getString(R.string.EndPointStep));
                            // TextView_info.setText(getString(R.string.MiddlePointStep));
                                TextView_info.setText(getString(R.string.EndPointStep));  //Avanza con + e - allo step

                        }
                        else{
                            //ho selezionato una entità e non posso applicarci questo comando
                        }

                        break;

                    case RADDRIZZA_ARCO1:
                        try {
                            info_modifica.id_punto_fine_modifica = recipe.getActiveStepIndex();
                            recipe.joinSelectedStepsByArc(info_modifica.id_punto_inizio_modifica, info_modifica.id_punto_middle_modifica,info_modifica.id_punto_fine_modifica);
                            TextView_info.setText(getString(R.string.Fatto));
                            Aggiorna_canvas();
                            Set_Tutti_button_comandi_visibili();
                            info_modifica.comando = Info_modifica.Comando.Null;
                            List<JamPointCode> ListCodici = new ArrayList<>();
                            ListCodici = recipe.checkInvalidCodes(true);
                            if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                            recipe.clearInvalidCodes();
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    default:

                        break;
                }

            }
        }catch (Exception e)
        {}
        //  TextView_tot_punti.setText(""+  recipe.getPoints().size());
    }

    private boolean CheckPuntisovrapposti(double xCoordPosPinza, double yCoordPosPinza, float pcX, float pcY) {
        if(Coord_Pinza.XCoordPosPinza - recipe.pcX >-0.06 &&
                Coord_Pinza.XCoordPosPinza - recipe.pcX <0.06 &&
                Coord_Pinza.YCoordPosPinza - recipe.pcY >-0.06 &&
                Coord_Pinza.YCoordPosPinza - recipe.pcY <0.06)
            return  true;
        else return  false;


    }

    //*************************************************************************************************
    // on_click_0
    //*************************************************************************************************
    public void on_click_0(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"0");
        else
            TextView_info.setText("0");
    }
    //*************************************************************************************************
    // on_click_1
    //*************************************************************************************************
    public void on_click_1(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"1");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("1");
        }
    }
    //*************************************************************************************************
    // on_click_2
    //*************************************************************************************************
    public void on_click_2(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"2");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("2");
        }
    }
    //*************************************************************************************************
    // on_click_3
    //*************************************************************************************************
    public void on_click_3(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"3");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("3");
        }
    }
    //*************************************************************************************************
    // on_click_4
    //*************************************************************************************************
    public void on_click_4(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"4");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("4");
        }
    }
    //*************************************************************************************************
    // on_click_5
    //*************************************************************************************************
    public void on_click_5(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"5");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("5");
        }
    }
    //*************************************************************************************************
    // on_click_6
    //*************************************************************************************************
    public void on_click_6(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"6");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("6");
        }
    }
    //*************************************************************************************************
    // on_click_7
    //*************************************************************************************************
    public void on_click_7(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"7");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("7");
        }
    }
    //*************************************************************************************************
    // on_click_8
    //*************************************************************************************************
    public void on_click_8(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"8");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("8");
        }
    }
    //*************************************************************************************************
    // on_click_9
    //*************************************************************************************************
    public void on_click_9(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+"9");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("9");
        }
    }
    //*************************************************************************************************
    // on_click_punto
    //*************************************************************************************************
    public void on_click_punto(View view) throws IOException
    {

        if(isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText()+".");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("0.");
        }
    }
    //*************************************************************************************************
    // on_click_del
    //*************************************************************************************************
    public void on_click_del(View view) throws IOException
    {
        TextView_info.setText("");

    }
    //*************************************************************************************************
    // on_click_traform_toLine
    //*************************************************************************************************
    public void on_click_traform_toLine(View view) throws IOException {
        if (ElemSelezionati.size() > 0) {   //entity
            try {
                recipe.transformSelectedEntityToLine();

                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = recipe.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                recipe.clearInvalidCodes();


                ElemSelezionati = recipe.selectPreviousEntity();   //per aggiornare
                ElemSelezionati = recipe.selectNextEntity();   //per aggiornare

                Aggiorna_canvas();
            }catch (Exception e)
            {}
        }
    }
    //*************************************************************************************************
    // on_click_traform_toZigZag
    //*************************************************************************************************
    public void on_click_traform_toZigZag(View view) throws IOException {
        if (ElemSelezionati.size() > 0) {   //entity
            try {
                recipe.transformSelectedEntityToLineZigZag();

                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = recipe.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                recipe.clearInvalidCodes();

                ElemSelezionati = recipe.selectPreviousEntity();   //per aggiornare
                ElemSelezionati = recipe.selectNextEntity();   //per aggiornare
                Aggiorna_canvas();
            }catch (Exception e)
            {}
        }
    }
    //*************************************************************************************************
    // on_click_traform_toZigZag
    //*************************************************************************************************
    public void on_click_traform_toFeed(View view) throws IOException {
        if (ElemSelezionati.size() > 0) {   //entity
            recipe.transformSelectedEntityToFeed();


            List<JamPointCode> ListCodici = new ArrayList<>();
            ListCodici = recipe.checkInvalidCodes(false);
            if(ListCodici.size()>0) ShowCodeToast(ListCodici);
            recipe.clearInvalidCodes();

            ElemSelezionati = recipe.selectPreviousEntity();   //per aggiornare
            ElemSelezionati = recipe.selectNextEntity();   //per aggiornare
            Aggiorna_canvas();
        }
    }
    //*************************************************************************************************
    // on_click_traform_toZigZag
    //*************************************************************************************************
    public void on_click_explode(View view) throws IOException {
        if (ElemSelezionati.size() > 0) {   //entity
            recipe.explodeSelectedEntity();
            ElemSelezionati.clear();
            Aggiorna_canvas();
        }
    }

    //*************************************************************************************************
    // on_click_sposta
    //*************************************************************************************************
    public void on_click_sposta(View view) throws IOException {

        if (ElemSelezionati.size() == 0) {
            recipe.selectionStepStart();

            Set_Altri_button_comandi_invisibili(Button_sposta);
            info_modifica.Init_info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();
            info_modifica.comando = Info_modifica.Comando.SPOSTA1;
            info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();
            info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
            info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

            TextView_info.setText(getString(R.string.StringMove1));  //Premi tasti + - fino ultimo punto da spostare
        } else {
            Set_Altri_button_comandi_invisibili(Button_sposta);
            info_modifica.Init_info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.comando = Info_modifica.Comando.SPOSTA1;
            info_modifica.id_element_inizio_modifica = recipe.getSelectedEntityFirstElementIndex();
            info_modifica.DeltaX_inizio = (Double) MultiCmd_posizione_X.getValue() / 1000d;
            info_modifica.DeltaY_inizio = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
            TextView_info.setText(getString(R.string.StringMove3));  //Muovere usando le frecce poi premi Enter
        }
        Set_Altri_button_comandi_invisibili(Button_sposta);

    }
    //*************************************************************************************************
    // on_click_feed
    //*************************************************************************************************
    public void on_click_feed(View view) throws IOException {

        Set_Altri_button_comandi_invisibili(Button_feed);
        info_modifica.Init_info_modifica();
        info_modifica.comando = Info_modifica.Comando.FEED;
        info_modifica.DeltaX_inizio = (Double)MultiCmd_posizione_X.getValue()/1000d;
        info_modifica.DeltaY_inizio = (Double)MultiCmd_posizione_Y.getValue()/1000d;
        TextView_info.setText(getString(R.string.Feed));  //Muovere usando le frecce poi premi Enter
        info_modifica.QuoteRelativeAttive = true;

    }
    //*************************************************************************************************
    // on_click_codici
    //*************************************************************************************************
    public void on_click_codici(View view) throws IOException, JSONException {


  //      int punto_attuale = recipe.getActiveStepIndex();
  //      List<JamPointCode> codeStatus = recipe.getActiveStepCodes();
  //      Code_page.Lancia_Code_Page(this,codeStatus,punto_attuale);
        long startTime = System.nanoTime();

        int punto_attuale = recipe.getActiveStepIndex();
        ArrayList<JamPointCode> codeStatus = new ArrayList<JamPointCode>();

        for (JamPointCode code : recipe.codes) {
            codeStatus.add(code);
        }

        long stopTime = System.nanoTime();
        System.out.println(stopTime - startTime);
        Intent code = new Intent(this, Code_page.class);


        //Convert the codes to a JsonArray
        JSONArray array = new JSONArray();
        for (JamPointCode code1 : codeStatus) {
            array.put(code1.toJson());
        }

        String res = array.toString();
        code.putExtra("stepindex", recipe.activeStepIndex);
        code.putExtra("codes", res);
        stopTime = System.nanoTime();
        System.out.println(stopTime - startTime);
        startActivityForResult(code, RESULT_PAGE_CODE);

    }
    //*************************************************************************************************
    // on_click_raddrizza_arco
    //*************************************************************************************************
    public void on_click_raddrizza_arco(View view) throws IOException
    {
        Set_Altri_button_comandi_invisibili(Button_raddrizza_arco);
        info_modifica.Init_info_modifica();
        recipe.selectionStepStart();
        info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();
        info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO;
        info_modifica.ElemSelezionati = ElemSelezionati;
        if(ElemSelezionati.size() >0) {    //sto lavorando come entità
            TextView_info.setText(getString(R.string.MiddlePointStep));  //Premi frecce fino punto di mezzo dell'arco
            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO_ENTITA;
        } else    //sto lavorando come punti
            TextView_info.setText(getString(R.string.EndPointStep));  //Avanza con + e - allo step
    }
    //*************************************************************************************************
    // on_click_raddrizza_linea
    //*************************************************************************************************
    public void on_click_raddrizza_linea(View view) throws IOException
    {
        if(ElemSelezionati.size() >0 )
        {   //entity
            recipe.transformSelectedEntityToLine(); // changeSelectedEntityToLine();
            Aggiorna_canvas();
        }else
        {
            //steps

            Set_Altri_button_comandi_invisibili(Button_raddrizza_linea);
            info_modifica.Init_info_modifica();
            recipe.selectionStepStart();
            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_LINEA;
            info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();


            TextView_info.setText(getString(R.string.StringRaddrizzaLinea1));  //Premi tasti + - fino ultimo punto da allineare
        }



    }
    //*************************************************************************************************
    // on_click_linea
    //*************************************************************************************************
    public void on_click_linea(View view) throws IOException {


        Set_Altri_button_comandi_invisibili(Button_linea);
        info_modifica.Init_info_modifica();
        info_modifica.comando = Info_modifica.Comando.LINEA;
        info_modifica.DeltaX_inizio = (Double)MultiCmd_posizione_X.getValue()/1000d;
        info_modifica.DeltaY_inizio = (Double)MultiCmd_posizione_Y.getValue()/1000d;
        info_modifica.QuoteRelativeAttive = true;
        TextView_info.setText(getString(R.string.StringMove4));  //premi +- o Muovere usando le frecce poi premi Enter

        //daniele 18/06/2020, se arrivo sul punto di giunsione tra un elemento cucito e uno di step movendomi come entità non inseriva il nuovo elemento
        if(recipe.isActiveStepIntersection()){
            recipe.goToPreviousStep();
        }
        //fine modifica

        info_modifica.StepAttivo = recipe.getActiveStep() ;
        recipe.selectionStepStart();

    }
    //*************************************************************************************************
    // on_click_spline
    //*************************************************************************************************
    public void on_click_spline(View view) throws IOException {


    }
    //*************************************************************************************************
    // onclick debug
    //*************************************************************************************************
    public void on_click_debug(View view) throws IOException {

        recipe.repair();

    }
    //*************************************************************************************************
    // on_click_arco3p
    //*************************************************************************************************
    public void on_click_arco3p(View view) throws IOException {
        Set_Altri_button_comandi_invisibili(Button_arco3p);
        info_modifica.Init_info_modifica();
        info_modifica.comando = Info_modifica.Comando.ARCO3P_0;
        JamPointStep StepAttuale = recipe.getActiveStep();
        recipe.setDrawPosition(new PointF(StepAttuale.p.x,StepAttuale.p.y ));
        info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();   //daniele 18/06/20
        TextView_info.setText(getString(R.string.Arco3P_p2));  //Muovere usando le frecce poi premi Enter

    }
    //*************************************************************************************************
    // on_click_travetta
    //*************************************************************************************************
    public void on_click_travetta(View view) throws IOException {



        Set_Altri_button_comandi_invisibili(Button_travetta);
        info_modifica.Init_info_modifica();
        info_modifica.comando = Info_modifica.Comando.ZIGZAG;
        info_modifica.DeltaX_inizio = (Double)MultiCmd_posizione_X.getValue()/1000d;
        info_modifica.DeltaY_inizio = (Double)MultiCmd_posizione_Y.getValue()/1000d;
        info_modifica.QuoteRelativeAttive = true;
        TextView_info.setText(getString(R.string.ZigZag));


    }
    //*************************************************************************************************
    // on_click_cancella
    //*************************************************************************************************
    public void on_click_cancella(View view) throws IOException {

        if (ElemSelezionati.size() == 0) {  //steps
            recipe.selectionStepStart();
            Set_Altri_button_comandi_invisibili(Button_cancella);
            info_modifica.Init_info_modifica();
            info_modifica.comando = Info_modifica.Comando.CANCELLA;
            info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();
            TextView_info.setText(getString(R.string.StringCancella));  //Premi tasti + - fino ultimo punto da cancellare

        } else {    //entity

            recipe.deleteSelectedEntity();     //cancello entity
            List<JamPointCode> ListCodici = new ArrayList<>();
            ListCodici = recipe.checkInvalidCodes(false);
            if(ListCodici.size()>0) ShowCodeToast(ListCodici);
            recipe.clearInvalidCodes();

            info_StepPiuMeno.MacStati_StepSingolo = 10;
            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.TO_STEP_ATTIVO;

            TextView_info.setText(getString(R.string.Fatto));
            Aggiorna_canvas();
            info_modifica.comando = Info_modifica.Comando.Null;
        }


    }
    //*************************************************************************************************
    // on_click_stretchIntersection
    //*************************************************************************************************
    public void on_click_stretchIntersection(View view) throws IOException {

        Set_Altri_button_comandi_invisibili(Button_stretch_edge);
        info_modifica.comando = Info_modifica.Comando.STRETCH;
        //  info_modifica.id_punto_inizio_modifica = recipe.getActiveStepIndex();
        recipe.selectionStepStart();
        TextView_info.setText(getString(R.string.EndStretchStep));  //Muovere con + fino allo step finale dello stretch

    }
    //*************************************************************************************************
    // on_click_888M
    //*************************************************************************************************
    public void on_click_888M(View view) throws IOException
    {
        info_modifica.comando = Info_modifica.Comando.M888;

        TextView_info.setText(getString(R.string.DigitaNuovaLP));  //Digitare nuova lunghezza punto
        Set_Altri_button_comandi_invisibili(Button_888M);


    }
    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void ScrivoVbVnVq(MainActivity.Mci_write variabile) {

        if (variabile.write_flag == true) {
            variabile.write_flag = false;
            variabile.mci.setValue(variabile.valore);
            sl.WriteItem(variabile.mci);


        }


    }
    //*************************************************************************************************
    // on_click_move_all
    //*************************************************************************************************
    public void on_click_move_all(View view) throws IOException {

        Set_Altri_button_comandi_invisibili(Button_move_all);
        info_modifica.Init_info_modifica();
        info_modifica.comando = Info_modifica.Comando.SPOSTA_ALL;
        info_modifica.DeltaX_inizio = (Double)MultiCmd_posizione_X.getValue()/1000d;
        info_modifica.DeltaY_inizio = (Double)MultiCmd_posizione_Y.getValue()/1000d;
        TextView_info.setText(getString(R.string.StringMove2));  //Muovere usando le frecce poi premi Enter
        info_modifica.QuoteRelativeAttive = true;
    }
    //*************************************************************************************************
    // on_click_cancella_codice
    //*************************************************************************************************
    public void on_click_cancella_codice(View view) throws IOException {


        try {
            boolean result = recipe.clearActiveStepCodes();

            Aggiorna_canvas();

        }catch(Exception e){}

    }
    //*************************************************************************************************
    // GestiscoFreccia
    //*************************************************************************************************
    private void GestiscoFreccia(MainActivity.Mci_write Mci_write) {
        switch (Mci_write.mc_stati)
        {
            case 0:
                if (Mci_write.write_flag == true && Mci_write.valore == 1.0d) {
                    Mci_write.mci.setValue(1.0d);
                    sl.WriteItem(Mci_write.mci);

                    if (sl.getReturnCode() == 0){
                        Mci_write.mc_stati = 10;

                    }

                }
                break;

            case 10:    //leggo se il PLC ha cambiato altrimenti aspetto

                if((Double) Mci_write.mci.getValue() == 1.0d)
                    Mci_write.mc_stati = 20;
                else{
                    Mci_write.mci.setValue(1.0d);
                    sl.WriteItem(Mci_write.mci);

                }

                break;
            case 20:

                if(Mci_write.valore == 0.0d){
                    Mci_write.mci.setValue(0.0d);
                    sl.WriteItem(Mci_write.mci);
                    if (sl.getReturnCode() == 0){
                        Mci_write.mc_stati = 30;


                    }


                }

                break;


            case 30:    //leggo se il PLC ha cambiato altrimenti aspetto
                if((Double) Mci_write.mci.getValue() == 0.0d)
                    Mci_write.mc_stati = 0;         //riparto
                else{
                    Mci_write.mci.setValue(0.0d);
                    sl.WriteItem(Mci_write.mci);

                }

                break;




            default:

                break;


        }
    }

    //*************************************************************************************************
    // Mostra_Tutte_Icone
    //*************************************************************************************************
    private void Mostra_Tutte_Icone() {
        Set_button_frecce_visibili();
        Set_button_numeri_visibili();
        Set_Tutti_button_comandi_visibili();
        Set_button_PiuMeno_visibili();
        Button_tasto_enter.setVisibility(View.VISIBLE);
        info_modifica.comando = Info_modifica.Comando.Null;
        Button_exit.setVisibility(View.VISIBLE);
        Button_undo.setVisibility(View.VISIBLE);
        Button_redo.setVisibility(View.VISIBLE);
        Button_new.setVisibility(View.VISIBLE);
        Button_esc.setVisibility(View.VISIBLE);
    }
    //*************************************************************************************************
    // Mostra tutti button
    //*************************************************************************************************
    private void Visualizza_tutti_Button() {
        for (Button item: Lista_pulsanti_comandi) {
            item.setVisibility(View.VISIBLE);
        }
        Button_spline.setVisibility(View.GONE);   //nascondo fino a quando non li implemento
        Button_debug.setVisibility(View.GONE);   //nascondo fino a quando non li implemento
        info_modifica.comando = Info_modifica.Comando.Null;
    }

    //*************************************************************************************************
    // Set_Altri_button_comandi_invisibili
    //*************************************************************************************************
    private void Set_Altri_button_comandi_invisibili(Button button_attivo) {
        for (Button item: Lista_pulsanti_comandi) {
            if(item !=button_attivo)
            {
                item.setVisibility(View.GONE);
            }

        }

    }
    //*************************************************************************************************
    // Set_Tutti_button_comandi_visibili
    //*************************************************************************************************
    private void Set_Tutti_button_comandi_visibili() {
        for (Button item: Lista_pulsanti_comandi) {
            item.setVisibility(View.VISIBLE);
        }
        Button_spline.setVisibility(View.GONE);   //nascondo fino a quando non li implemento
        Button_debug.setVisibility(View.VISIBLE);   //nascondo fino a quando non li implemento


    }
    //**************************************************************************************************
    // SpegniTutteIcone
    //**************************************************************************************************
    private void SpegniTutteIcone() {
        Set_Tutti_button_comandi_invisibili();
        Set_button_frecce_invisibili();
        Set_button_numeri_invisibili();
        Set_button_PiuMeno_invisibili();
        ImageView.setVisibility(View.GONE);
        Button_traform_toLine.setVisibility(View.GONE);
        Button_traform_toZigZag.setVisibility(View.GONE);
        Button_traform_toFeed.setVisibility(View.GONE);
        Button_explode.setVisibility(View.GONE);
        Button_tasto_enter.setVisibility(View.GONE);
        Button_undo.setVisibility(View.GONE);
        Button_redo.setVisibility(View.GONE);
        Button_new.setVisibility(View.GONE);
        Button_esc.setVisibility(View.GONE);



    }

    //*************************************************************************************************
    // Set_Tutti_button_comandi_invisibili
    //*************************************************************************************************
    private void Set_Tutti_button_comandi_invisibili() {
        for (Button item: Lista_pulsanti_comandi) {
            item.setVisibility(View.GONE);
        }
        Button_debug.setVisibility(View.GONE);   //nascondo fino a quando non li implemento
    }
    //*************************************************************************************************
    // Set_button_numeri_visibili
    //*************************************************************************************************
    private void Set_button_numeri_visibili() {
        for (Button item: Lista_pulsanti_comandi_numeri) {

            item.setVisibility(View.VISIBLE);


        }

    }
    //*************************************************************************************************
    // Set_button_numeri_invisibili
    //*************************************************************************************************
    private void Set_button_numeri_invisibili() {
        for (Button item: Lista_pulsanti_comandi_numeri) {

            item.setVisibility(View.GONE);


        }

    }
    //*************************************************************************************************
    // Set_button_frecce_visibili
    //*************************************************************************************************
    private void Set_button_frecce_visibili() {
        for (Button item: Lista_pulsanti_comandi_frecce) {

            item.setVisibility(View.VISIBLE);


        }

    }
    //*************************************************************************************************
    // Set_button_frecce_invisibili
    //*************************************************************************************************
    private void Set_button_frecce_invisibili() {
        for (Button item: Lista_pulsanti_comandi_frecce) {

            item.setVisibility(View.GONE);


        }

    }
    //*************************************************************************************************
    // Set_button_PiuMeno_visibili
    //*************************************************************************************************
    private void Set_button_PiuMeno_visibili() {
        for (Button item: Lista_pulsanti_PiuMeno) {

            item.setVisibility(View.VISIBLE);


        }

    }
    //*************************************************************************************************
    // Set_button_PiuMeno_invisibili
    //*************************************************************************************************
    private void Set_button_PiuMeno_invisibili() {
        for (Button item: Lista_pulsanti_PiuMeno) {

            item.setVisibility(View.GONE);

        }


    }



    //*************************************************************************************************
    // Crea_Lista_pulsanti modifica
    //*************************************************************************************************
    private void Crea_Liste_pulsanti() {
        //comandi
        Lista_pulsanti_comandi.add(Button_tasto_home);
        Lista_pulsanti_comandi.add(Button_sposta);
        Lista_pulsanti_comandi.add(Button_feed);
        Lista_pulsanti_comandi.add(Button_codici);
        Lista_pulsanti_comandi.add(Button_888M);
        Lista_pulsanti_comandi.add(Button_linea);
        Lista_pulsanti_comandi.add(Button_arco3p);
        Lista_pulsanti_comandi.add(Button_spline);
        Lista_pulsanti_comandi.add(Button_travetta);
        Lista_pulsanti_comandi.add(Button_cancella);
        Lista_pulsanti_comandi.add(Button_stretch_edge);
        Lista_pulsanti_comandi.add((Button_move_all));
        Lista_pulsanti_comandi.add((Button_raddrizza_arco));
        Lista_pulsanti_comandi.add((Button_raddrizza_linea));
        Lista_pulsanti_comandi.add((Button_Sgancio_ago));
        Lista_pulsanti_comandi.add((Button_delete_code));



        //frecce
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up);
        Lista_pulsanti_comandi_frecce.add(Button_freccia_giu);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_left);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_down_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_down_left);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up_left);
        Lista_pulsanti_comandi_frecce.add(Button_jog_rot_sx);
        Lista_pulsanti_comandi_frecce.add(Button_jog_rot_dx);
        //numeri

        Lista_pulsanti_comandi_numeri.add(Button_tasto0);
        Lista_pulsanti_comandi_numeri.add(Button_tasto1);
        Lista_pulsanti_comandi_numeri.add(Button_tasto2);
        Lista_pulsanti_comandi_numeri.add(Button_tasto3);
        Lista_pulsanti_comandi_numeri.add(Button_tasto4);
        Lista_pulsanti_comandi_numeri.add(Button_tasto5);
        Lista_pulsanti_comandi_numeri.add(Button_tasto6);
        Lista_pulsanti_comandi_numeri.add(Button_tasto7);
        Lista_pulsanti_comandi_numeri.add(Button_tasto8);
        Lista_pulsanti_comandi_numeri.add(Button_tasto9);
        Lista_pulsanti_comandi_numeri.add(Button_tasto_punto);
        Lista_pulsanti_comandi_numeri.add(Button_tasto_del);
        // + -
        Lista_pulsanti_PiuMeno.add(Button_entita_piu);
        Lista_pulsanti_PiuMeno.add(Button_entita_meno);
        Lista_pulsanti_PiuMeno.add(Button_piu);
        Lista_pulsanti_PiuMeno.add(Button_meno);


    }
    //*************************************************************************************************
    //ShowCodeToast
    //*************************************************************************************************
    private void ShowCodeToast(List<JamPointCode> listCodici) {

        String testo= getString(R.string.Code_error)+": ";

        for(JamPointCode code : listCodici)
        {
            testo = testo+"\n"+code.codeType+ " "+ code.value;

        }
        Toast.makeText(getApplication(), testo, Toast.LENGTH_LONG).show();
    }
    //*******************************************************************************************
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    //*************************************************************************************************
    // struttura step + -
    //*************************************************************************************************
    public static class Info_StepPiuMeno
    {
        public enum Tipo_spostamento{NULL,SINGOLO,VELOCE,SINGOLO_RIPETUTO,N_SALTO,TO_STEP_ATTIVO};
        public enum Comando{NULL,GO,STOP};
        public enum Direzione{NULL, AVANTI,DIETRO};
        int MacStati_StepVeloce = 0;
        int MacStati_StepSingolo = 0;
        static int numeroRipetuto = 0;
        String last_testo_textView_info = "Info";

        public Tipo_spostamento tipo_spostamento = Tipo_spostamento.NULL;
        public Comando comando = Comando.NULL;
        public Direzione direzione = Direzione.NULL;



    }
    //*************************************************************************************************
    // struttura modifica
    //*************************************************************************************************
    public static class Info_modifica
    {

        int id_punto_inizio_modifica;
        int id_punto_fine_modifica;
        int id_entità_inizio_modifica;
        int id_entità_fine_modifica;
        int id_punto_middle_modifica;
        Double DeltaX_inizio;
        Double DeltaY_inizio;
        double X_Middle;
        double Y_Middle;
        double X_Start;
        double Y_Start;
        double X_End;
        double Y_End;
        boolean QuoteRelativeAttive;
        List<Element> ElemSelezionati;
        float LP,AltezzaZigZag;
        JamPointStep StepAttivo;
        boolean puntoCarico;
        int id_element_inizio_modifica;
        int id_element_fine_modifica;

        public enum Comando{ Null,HOME,HOME_DONE,SPOSTA1,SPOSTA2,CANCELLA,STRETCH,ZIGZAG,ZIGZAG_1,ZIGZAG_2,M888,SPOSTA_ALL,RADDRIZZA_LINEA,RADDRIZZA_ARCO,RADDRIZZA_ARCO1,
            LINEA,LINEA1,STRETCH1,STRETCH2,ARCO3P_0,ARCO3P_1,ARCO3P_2,FEED,RADDRIZZA_ARCO_ENTITA,ESCI,ESCI_DONE_AZZERAMENTO};

        public Comando comando = Comando.Null;

        private void Init_info_modifica() {
            id_punto_inizio_modifica = 0;
            id_punto_fine_modifica= 0;
            comando = Comando.Null;
            DeltaX_inizio= 0d;
            DeltaY_inizio= 0d;
            QuoteRelativeAttive = false;
            ElemSelezionati = new ArrayList<Element>();
            LP = 3.0f;
            X_Middle = 0d;
            Y_Middle = 0d;
            X_Start = 0d;
            Y_Start = 0d;
            X_End = 0;
            Y_End = 0;
            AltezzaZigZag = 3.0f;
            StepAttivo = null;
            id_entità_inizio_modifica = 0;
            id_entità_fine_modifica = 0;
            puntoCarico = false;
            id_element_inizio_modifica = 0;
            id_element_fine_modifica = 0;
        }


    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    //nascondo navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            int currentApiVersion;
            currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

                getWindow().getDecorView().setSystemUiVisibility(flags);

                // JamPointCode below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }

        }
    }
    Map<Integer, List<JamPointCode>> codesList = new LinkedHashMap<Integer, List<JamPointCode>>();
    private Map<Integer, List<JamPointCode>> GetCodes() {
        Map<Integer, List<JamPointCode>> codesList = new LinkedHashMap<Integer, List<JamPointCode>>();
        //Mi serve così per averli ordinati, altriemnti li devo riordianre dopo

     //   long startTime = System.nanoTime();

        for(Element elem : recipe.elements)
        {
            for(JamPointStep step : elem.steps)
            {
                if(step.element.recipe.codes.size() != 0) {
                    codesList.put(getIndex(step), getcodes(step));
                }
            }
        }

        //Metodo più veloce ma poi li devo riordinare per stepindex e devo aggiungerli se sono sullo stesso step
        /*for(JamPointCode code : recipe.codes){
            if(!codesList.containsKey(code.getStep().getIndex())) {
                codesList.put(code.getStep().getIndex(),
            }else{
                codesList.get(code).add(code);
            }
        }*/
      //  long stopTime = System.nanoTime();
      //  System.out.println(stopTime - startTime);

        return codesList;
    }
    public int getIndex(JamPointStep step)
    {
        return step.element.recipe.getStepIndex(step);
        //return this.element.recipe.getStepIndex(this);
    }
    //Martino
    public ArrayList<JamPointCode> getcodes(JamPointStep step)
    {
        ArrayList<JamPointCode> codes = new ArrayList<>();
        for(JamPointCode code : step.element.recipe.codes)
        {
            if(code.codeType != null) {
                if (code.getStep().equals(this)) {
                    codes.add(code);
                }
            }/*else{
                if (this.element.recipe.getStepNotDuplicated(0).equals(this)) {
                    codes.add(code);
                }
            }*/
        }
        return codes;
    }
    //*************************************************************************************************
    // ritorno da pagina x
    //*************************************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case RESULT_PAGE_LOAD_EEP:
                Intent databack = new Intent();
                databack.setData(Uri.parse("CARICATO"));
                setResult(RESULT_OK, databack);   //indico al prossimo activityResult che ho caricato un udf quindi va riletto
                KillThread();
                finish();
                //  Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //   startActivity(intent);

                break;

            case POPUPFOLDER:
                if(resultCode==0)
                {
                    Mostra_Tutte_Icone();
                    if (!Thread_Running) {


                        StopThread = false;
                        MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(Modifica_programma.this);
                        Thread t1 = new Thread(myTask, "Main myTask");
                        t1.start();
                        Log.d("JAM TAG", "Start Modifica_programma Thread");
                    }
                }
                else
                {
                    Folder = data.getExtras().getString("FolderPath");
                    try {
                        View v = new View(this);
                        onclickExit(v);
                    }catch (Exception e){}
                }
                break;
            case RESULT_PAGE_CODE:
                boolean esiste= false;
                Bundle bundle_code = data.getExtras();

                String str = bundle_code.getString("newCodes");
                recipe.PLCType =recipe.PLCType.M8;
                if (str != null) {
                    Gson g = new Gson();
                    JamPointCode[] codeReturn = g.fromJson(str, JamPointCode[].class);

                    if (codeReturn.length > 0) {
                        // recipe.clearActiveStepCodes();

                        for (JamPointCode code : codeReturn) {
                            if(code.codeType == JamPointCode.CodeType.RASAFILO1)
                                code.codeValueType = JamPointCode.CodeValueType.Integer;

                            if (code.codeType == JamPointCode.CodeType.SPEED_M8 ||
                                    code.codeType == JamPointCode.CodeType.OP1 ||
                                    code.codeType == JamPointCode.CodeType.OP2 ||
                                    code.codeType == JamPointCode.CodeType.OP3 |
                                    code.codeType == JamPointCode.CodeType.RASAFILO1 )

                                recipe.addActiveStepCode(code.codeType, code.value);




                        }
                    }

                    Aggiorna_canvas();

     //               codesList = GetCodes();
                }
                break;
            default:
                break;

        }



        super.onActivityResult(requestCode, resultCode, data);

    }
    //*************************************************************************************************
    // cambia_altezza_travetta
    //*************************************************************************************************
    private void cambia_altezza_travetta(float nuova_altezza) {

        try {
            if(ElemSelezionati.size() >0) {

                if(ElemSelezionati.get(0) instanceof ElementLineZigZag){
                    recipe.modifySelectedEntityZigZag(nuova_altezza);
                }

                Aggiorna_canvas();

            }
        } catch (NumberFormatException e) {
            System.out.println("numberStr is not a number");
        }

    }

    //*************************************************************************************************
    // cambia_lunghezzaPunto_elemento
    //*************************************************************************************************
    private void cambia_lunghezzaPunto_elemento(float nuovoPasso) {

        try {
            if(ElemSelezionati.size() >0) {

                if ((ElemSelezionati.get(0) instanceof ElementLine) || ElemSelezionati.get(0) instanceof ElementArc) {
                    recipe.modifySelectedEntity(nuovoPasso);


                }
                if (ElemSelezionati.get(0) instanceof ElementLineZigZag) {
                    recipe.modifySelectedEntity(nuovoPasso);
                }
                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = recipe.checkInvalidCodes(true);
                if(ListCodici.size()>0) ShowCodeToast(ListCodici);
                recipe.clearInvalidCodes();

                Aggiorna_canvas();
            }
        } catch (NumberFormatException e) {
            System.out.println("numberStr is not a number");
        }

    }

    //*************************************************************************************************
    //Broadcast Receiver per cambio lunghezza punto entità
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver_KeyDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Bundle bundle_txt = intent.getExtras();

            String valore = bundle_txt.getString("ret_valore");
            int id = bundle_txt.getInt("txtview_id");
            if(id == TextView_valore_A.getId()){
                TextView txtview = (TextView)findViewById(id);
                txtview.setText(valore);
                try{
                    float nuovoPasso = Float.parseFloat(valore);
                    cambia_lunghezzaPunto_elemento(nuovoPasso);
                }catch (Exception e)
                {}
            }
            if(id == TextView_valore_B.getId()){
                TextView txtview = (TextView)findViewById(id);
                txtview.setText(valore);
                try{
                    float nuovaAltezza = Float.parseFloat(valore);
                    cambia_altezza_travetta(nuovaAltezza);
                }catch (Exception e)
                {}
            }


        }
    };
    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {


      LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_KeyDialog);
        StopThread = true;
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "Stop Modifica_programma Thread");

    }
}
