package com.jam_int.jt316_jt318_m8;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.recipes.Recipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Parametri_page extends Activity {
    ShoppingList sl;

    Thread th;
    TableLayout stk;
    TableRow tbrow;
    Thread thread_parametri;
    int Chiamante = 0;
    ArrayList<Parametro_mac> Lista_parametri = new ArrayList<>();
    Recipe recipe;
    File file_udf;
    boolean Cambiato_dato = false;
    Intent databack_parametri = new Intent();
    Boolean Aggiorna_lista_su_schermo = true,trigger_read_da_cn = false, Leggi_dati_da_Cn = true,Dati_CN_letti = false,Scrivi_dopo_Load= false,first_cycle =true,password_inserita = false;
    ArrayList<String> str = new ArrayList<String>();
    Integer id_erroreXgui = 0;


    ImageButton Button_exit, Button_Load, Button_Save;
    MultiCmdItem MultiCmd_CH1_in_emergenza;
    boolean Thread_Running = false, StopThread = false;

    final private static int PAGE_UDF = 200;

    final private static int PAGE_PARAM = 201;
    final private static int PAGE_PARAM_ADMIN = 206;


    MultiCmdItem MultiCmd_Vb1012,MultiCmd_Vq1701,MultiCmd_Vq1702,MultiCmd_Vq1703,MultiCmd_Vq1704,MultiCmd_Vq1705,MultiCmd_Vq1706,MultiCmd_Vq1707,
            MultiCmd_Vq1708,MultiCmd_Vq1709,MultiCmd_Vq1710,MultiCmd_Vq1711,MultiCmd_Vq1712,MultiCmd_Vq1713,MultiCmd_Vq1714,
            MultiCmd_Vq1715,MultiCmd_Vq1716,MultiCmd_Vq1717,
            MultiCmd_Vb2012,MultiCmd_Vq2701,MultiCmd_Vq2702,MultiCmd_Vq2703,MultiCmd_Vq2704,MultiCmd_Vq2705,MultiCmd_Vq2706,MultiCmd_Vq2707,
            MultiCmd_Vq2708,MultiCmd_Vq2709,MultiCmd_Vq2710,MultiCmd_Vq2711,MultiCmd_Vq2712,MultiCmd_Vq2713,MultiCmd_Vq2714,
            MultiCmd_Vq2715,MultiCmd_Vq2716,MultiCmd_Vq2717,MultiCmd_Vb4090_DisableRotturaFiloC1,MultiCmd_Vb4091_DisableRotturaFiloC2,
            MultiCmd_Vb4071_DisableContSpolaC1,MultiCmd_Vb4073_DisableContSpolaC2,Multicmd_vq1718,Multicmd_vq1719, Multicmd_vq1720,Multicmd_vq1721,
            Multicmd_vq2718,Multicmd_vq2719, Multicmd_vq2720,Multicmd_vq2721,Multicmd_vb4094,Multicmd_vb4095,Multicmd_vb4518,Multicmd_null,
            Multicmd_vb20 ,MultiCmd_Vq3591,MultiCmd_Vq3592,MultiCmd_Vq3593,MultiCmd_Vq3053,MultiCmd_Vq3063,MultiCmd_Vq3050,MultiCmd_Vq3051,MultiCmd_Vq3052,
            Multicmd_Vq3501,Multicmd_Vq3502,Multicmd_Vq3510,Multicmd_Vq3511,
            Multicmd_Vq3000,Multicmd_Vq3001,Multicmd_Vq3002,Multicmd_Vq3003,Multicmd_Vq3004,Multicmd_Vq3005,Multicmd_Vq3006,Multicmd_Vq3007,
            Multicmd_Vq3008,Multicmd_Vq3009,Multicmd_Vq3010,Multicmd_Vq3011,Multicmd_Vq3012,Multicmd_Vq3013,Multicmd_Vq3014,Multicmd_Vq3015,
            Multicmd_Vq3016,Multicmd_Vq3017,Multicmd_Vq3018,Multicmd_Vq3019,Multicmd_Vq3020,Multicmd_Vq3021,Multicmd_Vq3022,Multicmd_Vq3023,
            Multicmd_Vq3540,Multicmd_Vq3080,Multicmd_Vq3081,Multicmd_Vq3082,Multicmd_Vq3083,Multicmd_Vq3520,Multicmd_Vq3521,Multicmd_Vq3522,
            Multicmd_Vq3523,Multicmd_Vq3525,Multicmd_Vq3530,Multicmd_Vq3531,Multicmd_Vq3070, Multicmd_Vq1913,Multicmd_Vq1914,Multicmd_Vq1915,
            Multicmd_Vq1916,Multicmd_Vq1917,Multicmd_Vq1918,Multicmd_Vq2913,Multicmd_Vq2914,Multicmd_Vq2915,
            Multicmd_Vq2916,Multicmd_Vq2917,Multicmd_Vq2918,Multicmd_Vq2919,Multicmd_vb4580,Multicmd_vb4581,Multicmd_vb4582,Multicmd_vb4583,
            Multicmd_vb4584,Multicmd_vb4585,Multicmd_vb23,Multicmd_vq3057,Multicmd_vb4037,Multicmd_vq3058,Multicmd_vq3059,
            Multicmd_vb4036,Multicmd_vq3053,Multicmd_vb24,MultiCmd_Vn3804_pagina_touch,MultiCmd_Vb25,MultiCmd_Vb26,MultiCmd_Vq3090,MultiCmd_Vq3091,
            MultiCmd_Vq3564,MultiCmd_Vq3060,MultiCmd_Vq3061,MultiCmd_Vq3581,MultiCmd_Vn5,MultiCmd_Vb80RichiestaPCAxDaHmiC1

    ;


    MainActivity.Mci_write Mci_write_Vq1913_C1_UdfVelLavRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1914_C1_UdfPuntiVelIni = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1915_C1_UdfVelIniRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1916_C1_UdfPuntiVelRall = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1917_C1_UdfVelRallRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1918_C1_Udf_FeedG0 = new MainActivity.Mci_write();





    MainActivity.Mci_write Mci_write_Vq1923 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1924 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1925 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1926 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1927 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1928 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1929 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq1930 = new MainActivity.Mci_write();




    MainActivity.Mci_write Mci_write_Vq2913_C2_UdfVelLavRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2914_C2_UdfPuntiVelIni = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2915_C2_UdfVelIniRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2916_C2_UdfPuntiVelRall = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2917_C2_UdfVelRallRPM = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2918_C2_Udf_FeedG0 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2919_C1_Udf_ValTensioneT1 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2920_C2_Udf_20 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2921_C2_Udf_ValElettrocalamitaSopra = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2922_C2_Udf_ValElettrocalamitaSotto = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2923 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2924 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2925 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2926 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2927 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2928 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2929 = new MainActivity.Mci_write();
    MainActivity.Mci_write Mci_write_Vq2930 = new MainActivity.Mci_write();





    MultiCmdItem[] mci_array_read_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametri);

        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        Button_exit = (ImageButton) findViewById(R.id.imageButton_exit);

        sl = SocketHandler.getSocket();
        sl.Clear();
        stk = (TableLayout) findViewById(R.id.tableLayout_punti);


        Bundle extras = getIntent().getExtras();

        Init_Mci();


        if (extras != null) {

            Chiamante = extras.getInt("Chiamante");


        }

        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);

        if (!Thread_Running) {

            MyAndroidThread_Parametri myTask_emg = new MyAndroidThread_Parametri(Parametri_page.this);
            thread_parametri = new Thread(myTask_emg, "Emg myTask");
            thread_parametri.start();
            Log.d("JAM TAG", "Start Parametri_page Thread");
        }

        try {
            Load_parametri_udf();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Inizializza_TableRow();


        databack_parametri.setData(Uri.parse("NO"));
        setResult(RESULT_OK, databack_parametri);   //indico che non ho cambiato almeno un parametro


    }


    private void Init_Mci() {
        MultiCmd_Vb1012 = sl.Add("Io", 1, MultiCmdItem.dtVB, 1012, MultiCmdItem.dpNONE);
        MultiCmd_Vq1701 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1701, MultiCmdItem.dpNONE);
        MultiCmd_Vq1702 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1702, MultiCmdItem.dpNONE);
        MultiCmd_Vq1703 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1703, MultiCmdItem.dpNONE);
        MultiCmd_Vq1704 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1704, MultiCmdItem.dpNONE);
        MultiCmd_Vq1705 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1705, MultiCmdItem.dpNONE);
        MultiCmd_Vq1706 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1706, MultiCmdItem.dpNONE);
        MultiCmd_Vq1707 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1707, MultiCmdItem.dpNONE);
        MultiCmd_Vq1708 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1708, MultiCmdItem.dpNONE);
        MultiCmd_Vq1709 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1709, MultiCmdItem.dpNONE);
        MultiCmd_Vq1710 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1710, MultiCmdItem.dpNONE);
        MultiCmd_Vq1711 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1711, MultiCmdItem.dpNONE);
        MultiCmd_Vq1712 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1712, MultiCmdItem.dpNONE);
        MultiCmd_Vq1713 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1713, MultiCmdItem.dpNONE);
        MultiCmd_Vq1714 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1714, MultiCmdItem.dpNONE);
        MultiCmd_Vq1715 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1715, MultiCmdItem.dpNONE);
        MultiCmd_Vq1716 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1716, MultiCmdItem.dpNONE);
        MultiCmd_Vq1717 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1717, MultiCmdItem.dpNONE);


        MultiCmd_Vb2012 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2012, MultiCmdItem.dpNONE);
        MultiCmd_Vq2701 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2701, MultiCmdItem.dpNONE);
        MultiCmd_Vq2702 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2702, MultiCmdItem.dpNONE);
        MultiCmd_Vq2703 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2703, MultiCmdItem.dpNONE);
        MultiCmd_Vq2704 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2704, MultiCmdItem.dpNONE);
        MultiCmd_Vq2705 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2705, MultiCmdItem.dpNONE);
        MultiCmd_Vq2706 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2706, MultiCmdItem.dpNONE);
        MultiCmd_Vq2707 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2707, MultiCmdItem.dpNONE);
        MultiCmd_Vq2708 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2708, MultiCmdItem.dpNONE);
        MultiCmd_Vq2709 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2709, MultiCmdItem.dpNONE);
        MultiCmd_Vq2710 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2710, MultiCmdItem.dpNONE);
        MultiCmd_Vq2711 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2711, MultiCmdItem.dpNONE);
        MultiCmd_Vq2712 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2712, MultiCmdItem.dpNONE);
        MultiCmd_Vq2713 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2713, MultiCmdItem.dpNONE);
        MultiCmd_Vq2714 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2714, MultiCmdItem.dpNONE);
        MultiCmd_Vq2715 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2715, MultiCmdItem.dpNONE);
        MultiCmd_Vq2716 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2716, MultiCmdItem.dpNONE);
        MultiCmd_Vq2717 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2717, MultiCmdItem.dpNONE);
        MultiCmd_Vb4090_DisableRotturaFiloC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4090, MultiCmdItem.dpNONE);
        MultiCmd_Vb4091_DisableRotturaFiloC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4091, MultiCmdItem.dpNONE);
        MultiCmd_Vb4071_DisableContSpolaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4071, MultiCmdItem.dpNONE);
        MultiCmd_Vb4073_DisableContSpolaC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4073, MultiCmdItem.dpNONE);
        Multicmd_vq1718 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1718, MultiCmdItem.dpNONE);
        Multicmd_vq1719 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1719, MultiCmdItem.dpNONE);
        Multicmd_vq1720 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1720, MultiCmdItem.dpNONE);
        Multicmd_vq1721 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1721, MultiCmdItem.dpNONE);
        Multicmd_vq2718 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2718, MultiCmdItem.dpNONE);
        Multicmd_vq2719 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2719, MultiCmdItem.dpNONE);
        Multicmd_vq2720 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2720, MultiCmdItem.dpNONE);
        Multicmd_vq2721 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2721, MultiCmdItem.dpNONE);
        Multicmd_vb4094 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4094, MultiCmdItem.dpNONE);
        Multicmd_vb4095 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4095, MultiCmdItem.dpNONE);
        Multicmd_vb4518 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4518, MultiCmdItem.dpNONE);
        Multicmd_null = sl.Add("Io", 1, MultiCmdItem.dtVB, 1, MultiCmdItem.dpNONE);
        Multicmd_vb20 = sl.Add("Io", 1, MultiCmdItem.dtVB, 20, MultiCmdItem.dpNONE);
        MultiCmd_Vq3591 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3591, MultiCmdItem.dpNONE);
        MultiCmd_Vq3592 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3592, MultiCmdItem.dpNONE);
        MultiCmd_Vq3593 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3593, MultiCmdItem.dpNONE);
        MultiCmd_Vq3053 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3053, MultiCmdItem.dpNONE);
        MultiCmd_Vq3063 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3063, MultiCmdItem.dpNONE);
        MultiCmd_Vq3050 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3050, MultiCmdItem.dpNONE);
        MultiCmd_Vq3051 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3051, MultiCmdItem.dpNONE);
        MultiCmd_Vq3052 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3052, MultiCmdItem.dpNONE);


        Multicmd_Vq3501 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3501, MultiCmdItem.dpNONE);
        Multicmd_Vq3502 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3502, MultiCmdItem.dpNONE);
        Multicmd_Vq3510 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3510, MultiCmdItem.dpNONE);
        Multicmd_Vq3511 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3511, MultiCmdItem.dpNONE);
        Multicmd_Vq3000 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3000, MultiCmdItem.dpNONE);
        Multicmd_Vq3001 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3001, MultiCmdItem.dpNONE);
        Multicmd_Vq3002 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3002, MultiCmdItem.dpNONE);
        Multicmd_Vq3003 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3003, MultiCmdItem.dpNONE);
        Multicmd_Vq3004 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3004, MultiCmdItem.dpNONE);
        Multicmd_Vq3005 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3005, MultiCmdItem.dpNONE);
        Multicmd_Vq3006 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3006, MultiCmdItem.dpNONE);
        Multicmd_Vq3007 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3007, MultiCmdItem.dpNONE);
        Multicmd_Vq3008 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3008, MultiCmdItem.dpNONE);
        Multicmd_Vq3009 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3009, MultiCmdItem.dpNONE);
        Multicmd_Vq3010 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3010, MultiCmdItem.dpNONE);
        Multicmd_Vq3011 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3011, MultiCmdItem.dpNONE);
        Multicmd_Vq3012 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3012, MultiCmdItem.dpNONE);
        Multicmd_Vq3013 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3013, MultiCmdItem.dpNONE);
        Multicmd_Vq3014 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3014, MultiCmdItem.dpNONE);
        Multicmd_Vq3015 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3015, MultiCmdItem.dpNONE);
        Multicmd_Vq3016 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3016, MultiCmdItem.dpNONE);
        Multicmd_Vq3017 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3017, MultiCmdItem.dpNONE);
        Multicmd_Vq3018 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3018, MultiCmdItem.dpNONE);
        Multicmd_Vq3019 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3019, MultiCmdItem.dpNONE);
        Multicmd_Vq3020 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3020, MultiCmdItem.dpNONE);
        Multicmd_Vq3021 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3021, MultiCmdItem.dpNONE);
        Multicmd_Vq3022 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3022, MultiCmdItem.dpNONE);
        Multicmd_Vq3023 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3023, MultiCmdItem.dpNONE);
        Multicmd_Vq3540 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3540, MultiCmdItem.dpNONE);
        Multicmd_Vq3080 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3080, MultiCmdItem.dpNONE);
        Multicmd_Vq3081 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3081, MultiCmdItem.dpNONE);
        Multicmd_Vq3082 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3082, MultiCmdItem.dpNONE);
        Multicmd_Vq3083 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3083, MultiCmdItem.dpNONE);
        Multicmd_Vq3520 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3520, MultiCmdItem.dpNONE);
        Multicmd_Vq3521 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3521, MultiCmdItem.dpNONE);
        Multicmd_Vq3522 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3522, MultiCmdItem.dpNONE);
        Multicmd_Vq3523 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3523, MultiCmdItem.dpNONE);
        Multicmd_Vq3525 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3525, MultiCmdItem.dpNONE);
        Multicmd_Vq3530 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3530, MultiCmdItem.dpNONE);
        Multicmd_Vq3531 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3531, MultiCmdItem.dpNONE);
        Multicmd_Vq3070 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3070, MultiCmdItem.dpNONE);
        Multicmd_Vq1913 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1913, MultiCmdItem.dpNONE);
        Multicmd_Vq1914 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1914, MultiCmdItem.dpNONE);
        Multicmd_Vq1915 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1915, MultiCmdItem.dpNONE);
        Multicmd_Vq1916 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1916, MultiCmdItem.dpNONE);
        Multicmd_Vq1917 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1917, MultiCmdItem.dpNONE);
        Multicmd_Vq1918 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1918, MultiCmdItem.dpNONE);

        Multicmd_Vq2913 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2913, MultiCmdItem.dpNONE);
        Multicmd_Vq2914 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2914, MultiCmdItem.dpNONE);
        Multicmd_Vq2915 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2915, MultiCmdItem.dpNONE);
        Multicmd_Vq2916 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2916, MultiCmdItem.dpNONE);
        Multicmd_Vq2917 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2917, MultiCmdItem.dpNONE);
        Multicmd_Vq2918 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2918, MultiCmdItem.dpNONE);
        Multicmd_Vq2919 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2919, MultiCmdItem.dpNONE);

        Multicmd_vb4580 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4580, MultiCmdItem.dpNONE);
        Multicmd_vb4581 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4581, MultiCmdItem.dpNONE);
        Multicmd_vb4582 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4582, MultiCmdItem.dpNONE);
        Multicmd_vb4583 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4583, MultiCmdItem.dpNONE);
        Multicmd_vb4584 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4584, MultiCmdItem.dpNONE);
        Multicmd_vb4585 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4585, MultiCmdItem.dpNONE);

        Multicmd_vb23 = sl.Add("Io", 1, MultiCmdItem.dtVB, 23, MultiCmdItem.dpNONE);
        Multicmd_vq3057 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3057, MultiCmdItem.dpNONE);
        Multicmd_vb4037 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4037, MultiCmdItem.dpNONE);
        Multicmd_vq3058 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3058, MultiCmdItem.dpNONE);
        Multicmd_vb4036 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4036, MultiCmdItem.dpNONE);
        Multicmd_vq3053 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3053, MultiCmdItem.dpNONE);
        Multicmd_vb24 = sl.Add("Io", 1, MultiCmdItem.dtVB, 24, MultiCmdItem.dpNONE);
        MultiCmd_Vb25 = sl.Add("Io", 1, MultiCmdItem.dtVB, 25, MultiCmdItem.dpNONE);
        MultiCmd_Vb26 = sl.Add("Io", 1, MultiCmdItem.dtVB, 26, MultiCmdItem.dpNONE);
        MultiCmd_Vq3090 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3090, MultiCmdItem.dpNONE);
        MultiCmd_Vq3091 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3091, MultiCmdItem.dpNONE);
        MultiCmd_Vq3564 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3564, MultiCmdItem.dpNONE);
        MultiCmd_Vq3060 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3060, MultiCmdItem.dpNONE);
        MultiCmd_Vq3061 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3061, MultiCmdItem.dpNONE);
        MultiCmd_Vq3581 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3581, MultiCmdItem.dpNONE);
        Multicmd_vq3059 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3059, MultiCmdItem.dpNONE);
        MultiCmd_Vn5 = sl.Add("Io", 1, MultiCmdItem.dtVN, 5, MultiCmdItem.dpNONE);
        MultiCmd_Vb80RichiestaPCAxDaHmiC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 80, MultiCmdItem.dpNONE);

        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{
                MultiCmd_Vq1701,MultiCmd_Vq1702,MultiCmd_Vq1703,MultiCmd_Vq1704,MultiCmd_Vq1705,MultiCmd_Vq1706,MultiCmd_Vq1707,
                MultiCmd_Vq1708,MultiCmd_Vq1709,MultiCmd_Vq1710,MultiCmd_Vq1711,MultiCmd_Vq1712,MultiCmd_Vq1713,MultiCmd_Vq1714,MultiCmd_Vq1715,
                MultiCmd_Vq1716,MultiCmd_Vq1717,
                MultiCmd_Vq2701,MultiCmd_Vq2702,MultiCmd_Vq2703,MultiCmd_Vq2704,MultiCmd_Vq2705,MultiCmd_Vq2706,MultiCmd_Vq2707,
                MultiCmd_Vq2708,MultiCmd_Vq2709,MultiCmd_Vq2710,MultiCmd_Vq2711,MultiCmd_Vq2712,MultiCmd_Vq2713,MultiCmd_Vq2714,MultiCmd_Vq2715,
                MultiCmd_Vq2716,MultiCmd_Vq2717,MultiCmd_Vb4090_DisableRotturaFiloC1,MultiCmd_Vb4091_DisableRotturaFiloC2,MultiCmd_Vb4071_DisableContSpolaC1,
                MultiCmd_Vb4073_DisableContSpolaC2,Multicmd_vq1718,Multicmd_vq1719, Multicmd_vq1720,Multicmd_vq1721,Multicmd_vq2718,Multicmd_vq2719,
                Multicmd_vq2720,Multicmd_vq2721,Multicmd_vb4094,Multicmd_vb4095,Multicmd_vb4518,MultiCmd_Vq3591,MultiCmd_Vq3592,MultiCmd_Vq3593,MultiCmd_Vq3053,MultiCmd_Vq3063,
                MultiCmd_Vq3050,MultiCmd_Vq3051,MultiCmd_Vq3052,Multicmd_Vq3501,Multicmd_Vq3502,
                Multicmd_Vq3510,Multicmd_Vq3511,Multicmd_Vq3000,Multicmd_Vq3001,Multicmd_Vq3002,Multicmd_Vq3003,Multicmd_Vq3004,Multicmd_Vq3005,Multicmd_Vq3006,Multicmd_Vq3007,
                Multicmd_Vq3008,Multicmd_Vq3009,Multicmd_Vq3010,Multicmd_Vq3011,Multicmd_Vq3012,Multicmd_Vq3013,Multicmd_Vq3014,Multicmd_Vq3015,
                Multicmd_Vq3016,Multicmd_Vq3017,Multicmd_Vq3018,Multicmd_Vq3019,Multicmd_Vq3020,Multicmd_Vq3021,Multicmd_Vq3022,Multicmd_Vq3023,Multicmd_Vq3540,Multicmd_Vq3080,
                Multicmd_Vq3081,Multicmd_Vq3082,Multicmd_Vq3083,Multicmd_Vq3520,Multicmd_Vq3521,Multicmd_Vq3522,
                Multicmd_Vq3523,Multicmd_Vq3525,Multicmd_Vq3530,Multicmd_Vq3531,Multicmd_Vq3070,Multicmd_Vq1913,Multicmd_Vq1914,Multicmd_Vq1915,
                Multicmd_Vq1916,Multicmd_Vq1917,Multicmd_Vq1918,Multicmd_Vq2913,Multicmd_Vq2914,Multicmd_Vq2915,
                Multicmd_Vq2916,Multicmd_Vq2917,Multicmd_Vq2918,Multicmd_Vq2919,Multicmd_vb4580,Multicmd_vb4581,Multicmd_vb4582,Multicmd_vb4583,Multicmd_vb4584,Multicmd_vb4585,
                Multicmd_vb20,Multicmd_vb23,Multicmd_vq3057,Multicmd_vb4037,Multicmd_vq3058,
                Multicmd_vb4036,Multicmd_vq3053,Multicmd_vb24,MultiCmd_Vb25,MultiCmd_Vb26,MultiCmd_Vq3090,MultiCmd_Vq3091,MultiCmd_Vq3564,MultiCmd_Vq3060,MultiCmd_Vq3061,
                MultiCmd_Vq3581,Multicmd_vq3059,MultiCmd_Vn5,MultiCmd_Vb80RichiestaPCAxDaHmiC1


        };



    }


    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_parameter_ret"));

    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        KillThread();

    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();

    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();


    }
    //*************************************************************************************************
    // BroadcastReceiver per prendere la risposta dal KeyDialog
    //*************************************************************************************************
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            String linea = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea = br.readLine();
                br.close();
            }catch (Exception e)
            {}

            if(val.equals(linea))
            {
                ImageButton SendCmd = findViewById(R.id.imageButton_send_command);
                LinearLayout SaveLoad = (LinearLayout) findViewById(R.id.LinearLayoutSaveLoad);
                SaveLoad.setVisibility(View.VISIBLE);
                SendCmd.setVisibility(View.GONE);
                //Sblocco tutti i parametri
                for (int i = 0; i < Lista_parametri.size(); i++) {
                    Lista_parametri.get(i).password = false;
                }
                password_inserita = true;
                Inizializza_TableRow();
                Show_parametri();

            }else if(val.equals(""))
            {}
            else
            {
                Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
            }


        }
    };
    //**********************************************************************************************
    // Load_parametri_udf
    //**********************************************************************************************
    private void Load_parametri_udf() throws IOException {
        switch (Chiamante){

            case PAGE_UDF:
                String path;
                if(Values.File_XML_path == null)
                    path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram", getApplicationContext());
                else
                    path = Values.File_XML_path;
                file_udf = new File(path);
                if (file_udf.exists()) {

                    recipe = new Recipe();
                    try {
                        recipe.open(file_udf);
                    } catch (Exception e) {
                        Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
                    }
                    if (recipe.elements.size() != 0) {

                        Mci_write_Vq1913_C1_UdfVelLavRPM.valore = Double.valueOf(recipe.UdfVelLavRPM);
                        Mci_write_Vq1914_C1_UdfPuntiVelIni.valore = Double.valueOf(recipe.UdfPuntiVelIni);
                        Mci_write_Vq1915_C1_UdfVelIniRPM.valore = Double.valueOf(recipe.UdfVelIniRPM);
                        Mci_write_Vq1916_C1_UdfPuntiVelRall.valore = Double.valueOf(recipe.UdfPuntiVelRall);
                        Mci_write_Vq1917_C1_UdfVelRallRPM.valore = Double.valueOf(recipe.UdfVelRallRPM);
                        Mci_write_Vq1918_C1_Udf_FeedG0.valore = Double.valueOf(recipe.Udf_FeedG0);


                    } else {

                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Values.File_XML_path_T1_R missing", Toast.LENGTH_SHORT).show();
                }

                break;

            case PAGE_PARAM:
                break;

            case PAGE_PARAM_ADMIN:

                break;


            default:
                break;




        }
    }

    //**********************************************************************************************
    // MyAndroidThread_Emg
    //**********************************************************************************************
    class MyAndroidThread_Parametri implements Runnable {
        Activity activity;

        public MyAndroidThread_Parametri(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;

                try {
                    Thread.sleep((long) 200d);
                    if (StopThread) {
                        Thread_Running = false;
                      //  MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                     //   sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }
                if (sl.IsConnected()) {
                    if(first_cycle) {
                        MultiCmd_Vn3804_pagina_touch.setValue(1006.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        first_cycle = false;
                    }

                    sl.ReadItem(MultiCmd_CH1_in_emergenza);



                    if(Scrivi_dopo_Load)  InviaTuttiParametri();


                    if(Cambiato_dato){

                        sl.WriteItems(mci_array_read_all);
                        Cambiato_dato = false;



                        if(Chiamante ==PAGE_PARAM || Chiamante == PAGE_PARAM_ADMIN) {


                            MultiCmd_Vb1012.setValue(1.0d); //Vb per cambiare file Param
                            sl.WriteItem(MultiCmd_Vb1012);
                        }

                        trigger_read_da_cn = true;
                    }else
                    {
                        if(Leggi_dati_da_Cn) {
                            sl.ReadItems(mci_array_read_all);
                            if (sl.getReturnCode() == 0) {
                                Dati_CN_letti = true;
                            }
                        }
                    }



                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showErroriGui();
                            if(Aggiorna_lista_su_schermo && Dati_CN_letti){
                                Aggiorna_lista_su_schermo = false;
                                Leggi_dati_da_Cn = false;
                                Dati_CN_letti = false;

                                try {
                                    Lista_parametri = new ArrayList<>();
                                    Inizializza_TableRow();
                                    Inizializza_parametri();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }


                        }


                    });


                } else
                    sl.Connect();
            }


        }
        //**************************************************************************************************
        //
        //**************************************************************************************************
        private void showErroriGui() {
            switch (id_erroreXgui){

                case 0:
                    break;

                case 1:
                    Toast.makeText(getApplicationContext(),"error par.",Toast.LENGTH_LONG).show();

                    id_erroreXgui = 0;
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"Loaded, close and reopen parameters page!",Toast.LENGTH_LONG).show();
                    id_erroreXgui = 0;
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"Stored parameters number is not equal with parameters list ",Toast.LENGTH_LONG).show();
                    id_erroreXgui = 0;
                    break;

            }
        }
        //**************************************************************************************************
        //
        //**************************************************************************************************
        private void InviaTuttiParametri() {
            Scrivi_dopo_Load = false;
            if(Lista_parametri.size()==str.size()) {
                for (int i=0;i<=Lista_parametri.size()-1;i++){
                    try {
                        MultiCmdItem mci = Lista_parametri.get(i).mci;
                        mci.setValue(Double.parseDouble(str.get(i)));
                        sl.WriteItem(mci);
                    }
                    catch (Exception e)
                    {
                        id_erroreXgui =1;
                       // Toast.makeText(this,"error par:" +i,Toast.LENGTH_LONG).show();
                    }
                }
                id_erroreXgui = 2;
               // Toast.makeText(this,"Loaded, close and reopen parameters page!",Toast.LENGTH_LONG).show();
            }
            else
            {
                id_erroreXgui = 3;
               // Toast.makeText(this,"Stored parameters number is not equal with parameters list ",Toast.LENGTH_LONG).show();
            }
        }
    }


    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void Chiamo_Pagina_emergenza() {
        StopThread = true;
        try {
            Thread.sleep((long) 300d);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent intent_par = new Intent(getApplicationContext(), Emergency_page.class);

        startActivity(intent_par);
        finish();

    }

    //**************************************************************************************************
    // Inizializza_parametri
    //**************************************************************************************************
    private void Inizializza_parametri() throws FileNotFoundException {

        switch (Chiamante) {


            case PAGE_UDF:

                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1913, 1, 100, 2500, 2500, false, false, false,false, false, true, false, new ArrayList<>(),new ArrayList<>(), Mci_write_Vq1913_C1_UdfVelLavRPM,true));
                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1914, 2, 1, 10, 3, false, false, false,false, false, true, false, new ArrayList<>(),new ArrayList<>(), Mci_write_Vq1914_C1_UdfPuntiVelIni,true));
                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1915, 3, 100, 1000, 200, false, false, false, false,false, true, false, new ArrayList<>(),new ArrayList<>(), Mci_write_Vq1915_C1_UdfVelIniRPM,true));
                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1916, 4, 1, 10, 3, false, false, false, false,false, true, false, new ArrayList<>(),new ArrayList<>(), Mci_write_Vq1916_C1_UdfPuntiVelRall,true));
                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1917, 5, 100, 1000, 200, false, false, false,false, false, true, false, new ArrayList<>(), new ArrayList<>(),Mci_write_Vq1917_C1_UdfVelRallRPM,true));
                    Lista_parametri.add(new Parametro_mac(Multicmd_Vq1918, 6, 1000, 10000, 5000, false, false, false,false, false, true, false, new ArrayList<>(),new ArrayList<>(), Mci_write_Vq1918_C1_Udf_FeedG0,true));


                break;





            case  PAGE_PARAM :

                if(Values.Machine_model.contains("JT350M")) {
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb4090_DisableRotturaFiloC1,1,0,1,1,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb4071_DisableContSpolaC1,2,0,1,1,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1701, 3, 0, 360, 174, false, false, true,true, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1702, 4, 0, 360, 41, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1703, 5, -360, 360, -40, false, false, true,false, true, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1704, 6, 0, 360, 90, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1705, 7, 0, 360, 250, false, false, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1706, 8, 0, 360, 125, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1707, 9, 0, 360, 355, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1708, 10, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1709, 11, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1710, 12, 0, 4000, 4000, false, false, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1711, 13, 0, 10, 5, false, false, false,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1712, 14, 0, 4000, 300, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1713, 15, 0, 10, 2, false, false, false,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1714, 16, 0, 4000, 300, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1715, 17, 0, 360, 0, false, false, true,false, false, false, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1716, 18, 0, 360, 64, false, true, true,false, false, false, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1717, 19, 0, 50000, 25000, false, false, true,true, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb20,20,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4580,21,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4581,22,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4582,23,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4583,24,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4584,25,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3564, 26, 3, 20, 14, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    }

                if(Values.Machine_model.contains("JT316M") || Values.Machine_model.contains("JT318M") || Values.Machine_model.contains("JT318M_1000x800")) {
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb4090_DisableRotturaFiloC1,1,0,1,1,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb4071_DisableContSpolaC1,2,0,1,1,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1703, 3, -360, 360, -40, false, false, true,false, true, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1704, 4, 0, 360, 10, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1705, 5, 0, 360, 350, false, false, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1706, 6, 0, 360, 50, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1707, 7, 0, 360, 300, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1708, 8, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1709, 9, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1717, 10, 1000, 10000, 10000, false, false, true,true, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb20,11,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4580,12,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4581,13,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4582,14,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4583,15,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4584,16,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3564, 17, 3, 20, 14, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3591, 18, 0, 0, 0, false, false, false,false, false, false, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3592, 19, 0, 0, 0, false, false, false,false, false, false, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3593, 20, 0, 0, 0, false, false, false,false, false, false, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4036,21,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(Multicmd_vq3053, 22, 0, 100, 1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb24,23,0,1,1,true,false,false,false,true,true,true,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb25,24,0,1,1,true,false,false,false,true,true,true,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb26,25,0,1,1,true,false,false,false,true,true,true,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3090, 26, 0, 360, 125, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3091, 27, 0, 360, 355, false, false, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb4037,28,0,1,0,true,false,false,false,true,true,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(Multicmd_vq3058, 29, 0, 100, 1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3060, 30, 0, 100, 1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3061, 31, 0, 100, 1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3581, 32, 1, 20, 10, false, false, false,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(Multicmd_vq3059, 33, 0, 100, 1f, false, true, true,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vn5, 34, 0, 4, 0f, false, false, false,false, false, true, true, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add( new Parametro_mac(MultiCmd_Vb80RichiestaPCAxDaHmiC1,35,0,1,1,true,false,false,false,true,true,true,new ArrayList<>(),new ArrayList<>(),null,false));




                }






                break;

            case PAGE_PARAM_ADMIN:
                if(Values.Machine_model.contains("JT350M")) {


                }
                if(Values.Machine_model.contains("JT316M") ||Values.Machine_model.contains("JT318M") ||  Values.Machine_model.contains("JT318M_1000x800")  ) {
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1701, 1, 0, 360, 174, false, false, true,true, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1702, 2, 0, 360, 41, false, false, true,false, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1710, 3, 0, 2500, 2000, false, false, true,false, false, true, false, new ArrayList<>(),new ArrayList<>(), null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1711, 4, 0, 10, 5, false, false, false,false, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1712, 5, 0, 4000, 300, false, false, true,false, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1713, 6, 0, 10, 2, false, false, false,false, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1714, 7, 0, 1000, 300, false, false, true,false, false, true, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1715, 8, 0, 360, 0, false, false, true,false, false, false, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1716, 9, 0, 360, 64, false, true, true,false, false, false, false, new ArrayList<>(), new ArrayList<>(),null,false));
                    Lista_parametri.add( new Parametro_mac(Multicmd_vb23,10,0,1,1,true,false,false,false,true,false,false,new ArrayList<>(),new ArrayList<>(),null,false));
                    Lista_parametri.add(new Parametro_mac(Multicmd_vq3057, 11, 100, 4000, 3000, false, false, false,false, false, true, false, new ArrayList<>(),new ArrayList<>(), null,false));

                }


                break;



            default:
                break;


        }
        Show_parametri();
    }


    //**************************************************************************************************
    // Show_parametri
    //**************************************************************************************************
    private void Show_parametri()  {
        boolean d = false;
        Button_exit.setClickable(false);
        //Lista_parametri_mac.get(38).mci.setValue(Language);

        for (int i = 0; i < Lista_parametri.size(); i++) {
            tbrow = new TableRow(this);

            int y;

            final Parametro_mac item = Lista_parametri.get(i);


            //no.
            TextView t1v = new TextView(this);
            t1v.setText("" + item.numero_par);
            t1v.setTextSize(15);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.LEFT);
            t1v.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(t1v);


            //Descrizione parametro
            String Stringa_descrizione = "manca";
            try {
                String[] Descrizioni = new String[]{};


                switch (Chiamante) {


                    case PAGE_UDF:

                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Udf_Cucitura);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;


                    case PAGE_PARAM:
                        if(Values.Machine_model.contains("JT350M")) {
                            Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_JT350);
                            Stringa_descrizione = Descrizioni[item.numero_par];

                        }
                        if(Values.Machine_model.contains("JT316M") ||Values.Machine_model.contains("JT318M") || Values.Machine_model.contains("JT318M_1000x800")) {
                            Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_JT318M);
                            Stringa_descrizione = Descrizioni[item.numero_par];
                        }
                        break;

                    case PAGE_PARAM_ADMIN:
                        if(Values.Machine_model.contains("JT350M")) {
                            Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_JT350);
                            Stringa_descrizione = Descrizioni[item.numero_par];

                        }
                        if(Values.Machine_model.contains("JT316M") ||Values.Machine_model.contains("JT318M") || Values.Machine_model.contains("JT318M_1000x800")) {
                            Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_JT318M_admin);
                            Stringa_descrizione = Descrizioni[item.numero_par];
                        }
                        break;


                    default:

                        break;
                }


            } catch (Exception e) {
            }
            TextView t2v = new TextView(this);
            t2v.setText("" + Stringa_descrizione);
            t2v.setTextSize(15);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.LEFT);
            tbrow.addView(t2v);

            //Value mci (probabile proveniente da valori letti dalla shoppinList)
            String valore_string = "";
            Double valore_double= -1d;

            if(!item.udf_valore)
                valore_double = (Double) item.mci.getValue();
           else
              valore_double = item.mci_write.valore;


                try {
                    if (item.lista_str.size() > 0) {
                        try {

                            int idx_attuale = 0;
                            for (int ii = 0; ii < item.lista_str.size(); ii++) {

                                if(Double.compare(valore_double,item.ret_lista_str.get(ii)) ==0  )
                                {

                                    idx_attuale = ii;
                                    break;
                                }
                            }

                            valore_string = item.lista_str.get(idx_attuale);

                        } catch (Exception e) {

                            valore_string = "null";
                        }

                    } else {
                        if (item.var_bool == true) {
                            if (valore_double == 1.0d) {
                                valore_string = "ON";
                            } else {
                                valore_string = "OFF";
                            }
                        } else {
                            if (item.decimal == false && item.x1000 == false && item.Div1000 == false) {
                                String val = String.valueOf(valore_double);
                                valore_string = SubString.SubstringExtensions.Before(val, ".");         //tolgo la parte decimale

                            }
                            if (item.x1000 == true) {
                                Double valore = valore_double / 1000;
                                valore_string = String.valueOf(valore);
                                if (item.decimal == false) {
                                    valore_string = SubString.SubstringExtensions.Before(valore_string, ".");         //tolgo la parte decimale
                                }


                            }

                        }
                    }
                } catch (Exception e) {
                }


            final TextView edt = new TextView(this);
            if(!item.editabile) edt.setTextColor(Color.BLACK);
            else if(!item.password || password_inserita) edt.setTextColor(Color.BLUE);
            else edt.setTextColor(Color.RED);
            edt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            edt.setText(valore_string);
            edt.setId(item.numero_par);

            //eventi del edittext

            edt.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if(item.editabile == true && (item.password == false || password_inserita)) { //!(item.password == true && Values.UserLevel == 0)
                            if (item.lista_str.size() > 0) {
                                try {
                                    Double valore_double = (Double) item.mci.getValue();
                                    int indice = valore_double.intValue();
                                    indice++;
                                    if (indice >= item.lista_str.size()) indice = 0;
                                    edt.setText(item.lista_str.get(indice));

                                } catch (Exception e) {

                                    edt.setText("null");
                                }

                            } else {
                                if (item.var_bool) {
                                    try {
                                        String stato = edt.getText().toString();
                                        if (stato.contains("OFF")) {
                                            edt.setText("ON");
                                        } else {
                                            edt.setText("OFF");
                                        }
                                    } catch (Exception e) {
                                        PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                                    }
                                } else {
                                    //KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, edt, item.max, item.min, item.decimal, item.negativo, item.val_default, item.password, "");
                                    KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, edt, item.max, item.min, item.decimal, item.negativo, item.val_default, false , "",false);
                                }
                            }
                        }
                    }

                    return false;
                }
            });

            edt.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    try {
                        Cambiato_dato = true;
                        Gestisci_valore_inserito(edt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            //


            tbrow.addView(edt);

            stk.addView(tbrow);
        }

        Button_exit.setClickable(true);

    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void Gestisci_valore_inserito(TextView edt) throws IOException {

        int ID = edt.getId(); //perndo ID del textview modificato

        String new_valore = edt.getText().toString();

        Parametro_mac item = Lista_parametri.get(ID - 1);


       if(item.mci != null) {   //controllo se è un dato ShoppingList
           MultiCmdItem mci = item.mci;

           if (item.lista_str.size() > 0) {
               try {


               } catch (Exception e) {

               }

           } else {
               if (item.var_bool == true) {
                   if (new_valore.equals("OFF")) {
                       mci.setValue(0.0d);
                   } else {
                       mci.setValue(1.0d);
                   }

               } else {
                   try {
                       Double double_valoreNew = Double.parseDouble(new_valore);
                       if (item.x1000) {

                           mci.setValue(double_valoreNew * 1000d);



                       } else {
                           mci.setValue(double_valoreNew);



                       }


                       Inizializza_TableRow();

                   } catch (Exception e) {
                       PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                   }

               }
           }
       }
        if(Chiamante == PAGE_UDF) {   //controllo se è un dato da file udf



            if (item.var_bool == true) {
                if (new_valore.equals("OFF")) {
                    item.mci_write.valore = 0.0d;

                } else {
                    item.mci_write.valore = 1.0d;
                }


            } else {
                try {
                    Double double_valoreNew = Double.parseDouble(new_valore);
                    if (item.x1000) {

                        item.mci_write.valore = (double_valoreNew * 1000d);



                    } else {

                        if(item.Div1000){
                            item.mci_write.valore = (double_valoreNew / 1000d);
                        }else

                        item.mci_write.valore = (double_valoreNew);



                    }


                    recipe.UdfVelLavRPM = (int) Math.round(Mci_write_Vq1913_C1_UdfVelLavRPM.valore);
                    recipe.UdfPuntiVelIni = Mci_write_Vq1914_C1_UdfPuntiVelIni.valore;
                    recipe.UdfVelIniRPM = (int) Math.round(Mci_write_Vq1915_C1_UdfVelIniRPM.valore);
                    recipe.UdfPuntiVelRall = Mci_write_Vq1916_C1_UdfPuntiVelRall.valore;
                    recipe.UdfVelRallRPM = (int) Math.round(Mci_write_Vq1917_C1_UdfVelRallRPM.valore);
                    recipe.Udf_FeedG0 = (int) Math.round(Mci_write_Vq1918_C1_Udf_FeedG0.valore);




                    recipe.save(file_udf);
                    databack_parametri.setData(Uri.parse("SI"));
                    setResult(RESULT_OK, databack_parametri);   //indico che ho cambiato almeno un parametro

                    Inizializza_TableRow();

                } catch (Exception e) {
                    PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                }

            }


        }

        Leggi_dati_da_Cn = true;
        Aggiorna_lista_su_schermo = true;




    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void PrintToast(String Testo, int sfondo, int colore, float size) {
        Toast toast = Toast.makeText(Parametri_page.this, Testo, Toast.LENGTH_LONG);
        View view = toast.getView();

        //To change the Background of Toast
        view.setBackgroundColor(sfondo);    //Color.TRANSPARENT
        TextView text = (TextView) view.findViewById(android.R.id.message);

        //Shadow of the Of the Text Color
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setTextColor(colore);
        text.setTextSize(size);
        toast.show();
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    //nascondo navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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

                // Code below is to handle presses of Volume up or Volume down.
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

    //*************************************************************************************************
    //Inizializza graficamente il tablerow
    //*************************************************************************************************
    private void Inizializza_TableRow() {

        stk.removeAllViews();   //pulisco da eventuali righe

        TableRow tbrow0 = new TableRow(this);

        // intestazione No.
        TextView tv0 = new TextView(this);
        tv0.setText("No.  ");
        tv0.setTextColor(Color.RED);
        tv0.setTextSize(20);
        tbrow0.addView(tv0);
        // intestazione Descrizione
        TextView tv1 = new TextView(this);
        tv1.setText("Description       "
                + "		        	                          ");
        tv1.setTextColor(Color.RED);
        tv1.setTextSize(20);
        tbrow0.addView(tv1);

        // intestazione valore
        TextView tv6 = new TextView(this);
        tv6.setText("Value     ");
        tv6.setTextColor(Color.RED);
        tv6.setTextSize(20);
        tbrow0.addView(tv6);

        stk.addView(tbrow0);


    }

    //*************************************************************************************************
    // onclick_button_password
    //*************************************************************************************************
    public void onclick_send_command(View view) throws IOException
    {
        KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_parameter_ret",false);

    }
    //*************************************************************************************************
    // onclick_button_Exit
    //*************************************************************************************************
    public void onclick_button_Exit(View view) throws IOException {

        Esci();
    }

    //*************************************************************************************************
    // onclick_button_save
    //*************************************************************************************************
    public void onclick_button_save(View view) throws IOException
    {

        SaveParametriTofile();


    }

    public boolean SaveParametriTofile() {
        boolean ret = false;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "param.pts");
            // append text
            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
            for ( Parametro_mac item:Lista_parametri) {
                MultiCmdItem mci = item.mci;
                out.write(mci.getValue().toString() + "\n");
            }
            out.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
            ret= true;
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    //*************************************************************************************************
    // onclick_button_load
    //*************************************************************************************************
    public void onclick_button_load(View view) throws IOException
    {

        str = new ArrayList<String>();
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, "param.pts");
            // append text
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
                str.add(line);

            br.close();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        Scrivi_dopo_Load = true;   //chiedoal thred di inviare tutti i parametri letti al CN




    }
    //*************************************************************************************************
    //Esci
    //*************************************************************************************************
    private void Esci() {
        StopThread = true;

        try {

            thread_parametri.join();
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }

    //*************************************************************************************************
    // KillThread
    //*************************************************************************************************
    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopThread = true;

        try {
            //  Thread.sleep((long) 200d);
            if (!Thread_Running)
                thread_parametri.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "Stop Parametri_page Thread");

    }


    //*************************************************************************************************
    //Classe Parametro
    //*************************************************************************************************

    class Parametro_mac {

        public MultiCmdItem mci;
        public int numero_par;
        public float min, max, val_default;
        public boolean var_bool, decimal, x1000, negativo, editabile, password,Div1000,udf_valore;
        ArrayList<String> lista_str = new ArrayList<>();
        ArrayList<Double> ret_lista_str = new ArrayList<>();
        public MainActivity.Mci_write mci_write;


        public Parametro_mac(MultiCmdItem mci, int numero_par, float min, float max, float val_default, boolean var_bool, boolean decimal, boolean x1000, boolean Div1000, boolean negativo, boolean editabile, boolean password, ArrayList lista_str, ArrayList ret_lista_str, MainActivity.Mci_write mci_write, boolean udf_valore) {
            this.mci = mci;
            this.numero_par = numero_par;
            this.min = min;
            this.max = max;
            this.val_default = val_default;
            this.var_bool = var_bool;
            this.decimal = decimal;
            this.x1000 = x1000;
            this.negativo = negativo;
            this.editabile = editabile;
            this.password = password;
            this.lista_str = lista_str;
            this.mci_write = mci_write;
            this.Div1000 =Div1000;
            this.ret_lista_str = ret_lista_str;
            this.udf_valore = udf_valore;


        }

    }
    //*************************************************************************************************
// Background per Leggere file da CN
//*************************************************************************************************
    class Leggi_file_da_CN extends AsyncTask<String, Integer, Boolean> {
        private Activity activity;
        private ProgressDialog dialog;
        private Context context;

        public Leggi_file_da_CN(Activity activity) {
            this.activity = activity;
            this.context = activity;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");

        }


        @Override
        protected Boolean doInBackground(String... params) {

            String Path_CN = params[0];
            String Path_destinazione_HMI = params[1];




            Boolean r = sl.FileDownload(Path_CN,Path_destinazione_HMI,null);



            return r;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            super.onPostExecute(r);
            this.dialog.dismiss();

            if(r)
            {
                try {
                    Inizializza_parametri();        //carico i parametri sullo schermo
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


            else {
                Toast.makeText(getApplicationContext(), "C_Param receive error", Toast.LENGTH_SHORT).show();


            }

        }
    }
    //*************************************************************************************************
// Background per Leggere file da CN
//*************************************************************************************************
    class Scrivi_file_dentro_CN extends AsyncTask<String, Integer, Boolean> {
        private Activity activity;
        private ProgressDialog dialog;
        private Context context;

        public Scrivi_file_dentro_CN(Activity activity) {
            this.activity = activity;
            this.context = activity;
            this.dialog = new ProgressDialog(activity);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");

        }


        @Override
        protected Boolean doInBackground(String... params) {

            String Path_fonte = params[0];
            String Path_destinazione =  params[1];

            Boolean r_delete = sl.FileDelete(Path_destinazione);
            Boolean r = false;
            if(r_delete)
                r = sl.FileUpload(Path_destinazione, Path_fonte, null);


            return r;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            super.onPostExecute(r);
            this.dialog.dismiss();

            if(r)
            {
                Esci();

            }


            else {
                Toast.makeText(getApplicationContext(), "Send error", Toast.LENGTH_SHORT).show();


            }

        }
    }
}



