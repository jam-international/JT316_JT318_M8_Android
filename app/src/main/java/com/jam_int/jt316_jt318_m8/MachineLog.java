package com.jam_int.jt316_jt318_m8;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import communication.MultiCmdItem;

public class MachineLog {
    ArrayList<String> MachineLog_prec = new ArrayList<String>(Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
    ArrayList<String> MachineLog = new ArrayList();
    Boolean FirstRunMachineLog = true;
    String numero_operatore = "0";

    public void MachineLog(){

    }




    public void MachineLog_write( MultiCmdItem MultiCmd_Vb30_Incucitura, MultiCmdItem multiCmd_Vq3591_CNT_produzione, MultiCmdItem MultiCmd_Vb32_Stop_cucitura,MultiCmdItem Multicmd_dtDB_prog_name){
        /////////////////////////////////////////
        //*************************************************************************************************
        //var 1: Data (anno, mese, gg)
        //var 2: operatore
        //var 3: Vb16 in cucitura
        //var 4: Vq184 contatore produzione
        //var 5: vb902 Sfilatura (da sensore rottura filo o stop macchina)
        //var 6: vn18 allarme
        //var 7: vn105 velocità cucitura
        //var 8: nome programma dx
        //var 9: nomr programma sx
        //var 10: vn134
        //var 11: vn135
        //var 12: vn132
        //var 13: vn136
        //var 14: vn137
        //var 15: vn375
        //var 16: vn376
        //var 17: vn345
        //var 18: vn346
        //var 19:
        //var 20:
        //var 21:
        //var 22:


        try {

            if (!FirstRunMachineLog) {
                Date now = new Date();

                String Date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(now);
                String Time = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(now);

                //var 1: Data
                if (!MachineLog_prec.get(0).equals(Date)) {
                    MachineLog_prec.set(0, Date);
                    MachineLog.add("1|" + Date + "|" + Values.Username);
                }

                //var 2: operatore
                if (!MachineLog_prec.get(1).equals(numero_operatore)) {
                    MachineLog_prec.set(1, numero_operatore);
                    MachineLog.add("2|" + numero_operatore + "|" + Time);
                }

                //var 3: Vb18 in cucitura
                Double vb18_d = (Double) MultiCmd_Vb30_Incucitura.getValue();
                Integer vb16_int = vb18_d.intValue();

                if (!MachineLog_prec.get(2).equals(String.valueOf(vb16_int))) {
                    MachineLog_prec.set(2, String.valueOf(vb16_int));
                    MachineLog.add("3|" + vb16_int + "|" + Time);
                }

                //var 4: Vq184 contatore produzione
                Double bq3951_d = (Double) multiCmd_Vq3591_CNT_produzione.getValue() / 1000;
                Integer vq3951_int = bq3951_d.intValue();
                if (!MachineLog_prec.get(3).equals(String.valueOf(vq3951_int))) {
                    MachineLog_prec.set(3, String.valueOf(vq3951_int));
                    MachineLog.add("4|" + vq3951_int + "|" + Time);
                    Values.ProductionCount = vq3951_int;
                }

                //var 5: Vb1010 Sfilatura (da sensore rottura filo o stop macchina)

                Double Vb32_d = (Double) MultiCmd_Vb32_Stop_cucitura.getValue();
                Integer vb32_int = Vb32_d.intValue();

                if (!MachineLog_prec.get(4).equals(String.valueOf(vb32_int))) {
                    MachineLog_prec.set(4, String.valueOf(vb32_int));
                    if (vb32_int == 1)
                        MachineLog.add("5|" + vb32_int + "|" + Time);
                }
/*
                //var 6: vn18 allarme
                Double vn18d = (Double) MultiCmd_Vn18_allarmi.getValue();
                Integer vn18i = vn18d.intValue();
                if (!MachineLog_prec.get(5).equals(String.valueOf(vn18i))) {
                    MachineLog_prec.set(5, String.valueOf(vn18i));
                    if (vn18i > 0)
                        MachineLog.add("6|" + vn18i + "|" + Time);

                }

                //var 7: vn105 velocità cucitura
                if (!MachineLog_prec.get(6).equals(TextView_speed_val.getText().toString())) {
                    {
                        MachineLog_prec.set(6, TextView_speed_val.getText().toString());
                        MachineLog.add("7|" + TextView_speed_val.getText().toString() + "|" + Time);
                    }
                }
*/
                //var 8: nome programma dx
                String nome_prog =(String) Multicmd_dtDB_prog_name.getValue().toString();
                if (!MachineLog_prec.get(7).equals(nome_prog)) {
                    {
                        MachineLog_prec.set(7, nome_prog);
                        MachineLog.add("8|" + nome_prog + "|" + Time);
                    }
                }
/*
                //var 9: nomr programma sx
                if (!MachineLog_prec.get(8).equals(TextView_nomeprog_L_val.getText().toString())) {
                    {
                        MachineLog_prec.set(8, TextView_nomeprog_L_val.getText().toString());
                        MachineLog.add("9|" + TextView_nomeprog_L_val.getText().toString() + "|" + Time);
                    }
                }

                //var 10: vn134
                Double vn134d = (Double) MultiCmd_Vn134.getValue();
                Integer vn134i = vn134d.intValue();
                if (!MachineLog_prec.get(9).equals(String.valueOf(vn134i))) {
                    MachineLog_prec.set(9, String.valueOf(vn134i));
                    if (vn134i > 0)
                        MachineLog.add("10|" + vn134i + "|" + Time);

                }

                //var 11: vn135
                Double vn135d = (Double) MultiCmd_Vn135.getValue();
                Integer vn135i = vn135d.intValue();
                if (!MachineLog_prec.get(10).equals(String.valueOf(vn135i))) {
                    MachineLog_prec.set(10, String.valueOf(vn135i));
                    if (vn135i > 0)
                        MachineLog.add("11|" + vn135i + "|" + Time);

                }

                //var 12: vn132
                Double vn132d = (Double) MultiCmd_Vn132.getValue();
                Integer vn132i = vn132d.intValue();
                if (!MachineLog_prec.get(11).equals(String.valueOf(vn132i))) {
                    MachineLog_prec.set(11, String.valueOf(vn132i));
                    if (vn132i > 0)
                        MachineLog.add("12|" + vn132i + "|" + Time);

                }

                //var 13: vn136
                Double vn136d = (Double) MultiCmd_Vn136.getValue();
                Integer vn136i = vn136d.intValue();
                if (!MachineLog_prec.get(12).equals(String.valueOf(vn136i))) {
                    MachineLog_prec.set(12, String.valueOf(vn136i));
                    if (vn136i > 0)
                        MachineLog.add("13|" + vn136i + "|" + Time);

                }

                //var 14: vn137
                Double vn137d = (Double) MultiCmd_Vn137.getValue();
                Integer vn137i = vn137d.intValue();
                if (!MachineLog_prec.get(13).equals(String.valueOf(vn137i))) {
                    MachineLog_prec.set(13, String.valueOf(vn137i));
                    if (vn137i > 0)
                        MachineLog.add("14|" + vn137i + "|" + Time);

                }
                //var 15: vn375
                Double vn375d = (Double) MultiCmd_Vn375Errore.getValue();
                Integer vn375i = vn375d.intValue();
                if (!MachineLog_prec.get(14).equals(String.valueOf(vn375i))) {
                    MachineLog_prec.set(14, String.valueOf(vn375i));
                    if (vn375i > 0)
                        MachineLog.add("15|" + vn375i + "|" + Time);

                }
                //var 16: vn376
                Double vn376d = (Double) MultiCmd_Vn376.getValue();
                Integer vn376i = vn376d.intValue();
                if (!MachineLog_prec.get(15).equals(String.valueOf(vn376i))) {
                    MachineLog_prec.set(15, String.valueOf(vn376i));
                    if (vn376i > 0)
                        MachineLog.add("16|" + vn376i + "|" + Time);

                }
                //var 17: vn345
                Double vn345d = (Double) MultiCmd_Vn345Errore.getValue();
                Integer vn345i = vn345d.intValue();
                if (!MachineLog_prec.get(16).equals(String.valueOf(vn345i))) {
                    MachineLog_prec.set(16, String.valueOf(vn345i));
                    if (vn345i > 0)
                        MachineLog.add("17|" + vn345i + "|" + Time);

                }
                //var 18: vn346
                Double vn346d = (Double) MultiCmd_Vn346Errore.getValue();
                Integer vn346i = vn346d.intValue();
                if (!MachineLog_prec.get(17).equals(String.valueOf(vn346i))) {
                    MachineLog_prec.set(17, String.valueOf(vn346i));
                    if (vn346i > 0)
                        MachineLog.add("18|" + vn346i + "|" + Time);

                }

*/
            } else {
                FirstRunMachineLog = false;
            }


        } catch (Exception e) {
            System.out.printf("" + e);
        }

        if (MachineLog.size() > 0 ) {

            AddToLogFile(MachineLog);

            MachineLog.clear();
        }
    }
    public void AddToLogFile(ArrayList<String> str){
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt", true));
            for (String riga : str) {

                out.write(riga + "\n");

            }


            out.close();
        } catch (IOException e) {

        }

    }
    public static void AddToLogFile(String str){
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt", true));

                out.write(str);

            out.close();
        } catch (IOException e) {

        }

    }



}
