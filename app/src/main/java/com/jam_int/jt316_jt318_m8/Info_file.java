package com.jam_int.jt316_jt318_m8;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class Info_file {

    //********************************************************************************************************************************************
    //
    // !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
    //
    //********************************************************************************************************************************************
    public static  boolean Scrivi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, String valore_da_scrivere, Context context) throws IOException {

        boolean done = false;
        if(livello1 == null) livello1=livello2=livello3= livello0;
        if(livello2 == null) livello2=livello3 = livello1;
        if(livello3 == null) livello3 = livello2;
        ArrayList<struct_Campo> Campi_scomposti = new ArrayList<>();
        Campi_scomposti = Scomponi_File (null,path);
        if(Campi_scomposti.size() > 0)
        {
            for (struct_Campo item: Campi_scomposti) {
                if(item.livello0.contains(Pulisci(livello0)) &&
                        item.livello1.contains(Pulisci(livello1)) &&
                        item.livello2.contains(Pulisci(livello2)) &&
                        item.livello3.contains(Pulisci(livello3)) /*&&
                       item.nome_variabile.equals(Pulisci(nome_variabile))*/
                        )
                {
                    if(item.nome_variabile.equals(Pulisci(nome_variabile))) {
                        item.valore = valore_da_scrivere;
                        ScrivoTuttiCampiSuFile(Campi_scomposti);
                        return true;
                    }
                }
            }
        }


        return  done;
    }
    //********************************************************************************************************************************************
    //
    // !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
    //
    //********************************************************************************************************************************************
 /*   public static  boolean Scrivi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, String valore_da_scrivere, Context context) throws IOException {

        boolean done = false;

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String estensione = SubstringExtensions.After(path,".");
        String path_appoggio = SubstringExtensions.Before(path, ".") + "_tmp." + estensione;
        FileOutputStream fostream = new FileOutputStream(path_appoggio);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fostream));


        String line;

        try {
            while ((line = br.readLine()) != null)
            {
                writer.write(line + "\r\n");
                if (line.contains("[Start" + livello0 + "]"))
                {   //livello 0

                    if (livello1 != null && livello1 != "")
                    {
                        while ((line = br.readLine()) != null)
                        {
                            writer.write(line + "\r\n");
                            if (line.contains("[Start" + livello1 + "]"))
                            {   //livello 1.

                                if (livello2 != null && livello2 != "")
                                {
                                    while ((line = br.readLine()) != null)
                                    {
                                        writer.write(line + "\r\n");
                                        if (line.contains("[Start" + livello2 + "]"))
                                        {   //livello 2.

                                            if (livello3 != null && livello3 != "")
                                            {

                                                while ((line = br.readLine()) != null)
                                                {
                                                    writer.write(line + "\r\n");
                                                    if (line.contains("[Start" + livello3 + "]"))
                                                    {   //livello 3.
                                                        while ((line = br.readLine()) != null)
                                                        {
                                                            if (line.contains(nome_variabile) && !done)
                                                            {

                                                                line = SubstringExtensions.Before(line,"=") + "= " + valore_da_scrivere;
                                                                done = true;

                                                            }
                                                            writer.write(line + "\r\n");
                                                        }

                                                    }
                                                }
                                            }
                                            else
                                            { //non c'è livello3
                                                while ((line = br.readLine()) != null)
                                                {

                                                    if (line.contains(nome_variabile) && !done)
                                                    {
                                                        line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                                        done = true;


                                                    }
                                                    writer.write(line + "\r\n");
                                                }
                                            }



                                        }

                                    }
                                }
                                else
                                { //non c'è livello2
                                    while ((line = br.readLine()) != null)
                                    {

                                        if (line.contains(nome_variabile) && !done)
                                        {
                                            line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                            done = true;
                                        }
                                        writer.write(line + "\r\n");
                                    }
                                }
                            }

                        }
                    }
                    else
                    { //non c'è livello1
                        while ((line = br.readLine()) != null)
                        {

                            if (line.contains(nome_variabile) && !done)
                            {
                                line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                done = true;
                            }
                            writer.write(line + "\r\n");
                        }
                    }
                }

            }
        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (writer != null)
                    writer.close();

                if (writer != null)
                    writer.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }


        if (done)
        {
            String file_bak = SubstringExtensions.Before(path, ".");
            try{

                File file = new File(file_bak + ".bak");
                file.delete(); //cancello eventuale file back
                Info_file.makecopy_file(path, file_bak + ".bak",null);  //sposto path originale nel bak

                File file_originale = new File(path);
                file_originale.delete(); //cancello file orginale

                Info_file.makecopy_file(path_appoggio, path,null);

                File file_appoggio = new File(path_appoggio);
                file_appoggio.delete(); //cancello file di appoggio


            }catch(Exception e){

                e.printStackTrace();
                Toast.makeText(context, "error write txt field files", Toast.LENGTH_SHORT).show();

            }



        }

        return  done;
    }
    */
    //********************************************************************************************************************************************
    //
    // !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
    //
    //********************************************************************************************************************************************
    public static String Leggi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, Context context) throws IOException {

        String risultato="";

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

      //  BufferedReader reader = new BufferedReader(new FileReader(path));
       // StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try{
            while ((line = br.readLine()) != null) {

                if (line.contains("[Start" + livello0 + "]"))
                {   //livello 0

                    if (livello1 != null && livello1 != "" && !livello1.contains("null"))
                    {
                        while ((line = br.readLine()) != null)
                        {
                            if (line.contains("[Start" + livello1 + "]"))
                            {   //livello 1.

                                if (livello2 != null && livello2 != ""&& !livello2.contains("null"))
                                {
                                    while ((line = br.readLine()) != null)
                                    {
                                        if (line.contains("[Start" + livello2 + "]"))
                                        {   //livello 2.

                                            if (livello3 != null && livello3 != ""&& !livello3.contains("null"))
                                            {

                                                while ((line = br.readLine()) != null)
                                                {

                                                    if (line.contains("[Start" + livello3 + "]"))
                                                    {   //livello 3.
                                                        while ((line = br.readLine()) != null)
                                                        {
                                                            if (line.contains(nome_variabile))
                                                            {
                                                                risultato = SubstringExtensions.After(line, "=");
                                                                char first = risultato.charAt(0);
                                                                String tmp = risultato;
                                                                if(first == ' ')
                                                                    risultato  = tmp.substring(1);
                                                                if (risultato.length() > 0) break;
                                                            }
                                                        }

                                                    }
                                                    if (risultato.length() > 0) break;
                                                }
                                            }
                                            else
                                            { //non c'è livello3
                                                while ((line = br.readLine()) != null)
                                                {
                                                    if (line.contains("[Start")) { risultato = "errore"; break; }  //trovato lo start del livello successivo, non va bene
                                                    if (line.contains(nome_variabile))
                                                    {
                                                        risultato = SubstringExtensions.After(line, "=");
                                                        char first = risultato.charAt(0);
                                                        String tmp = risultato;
                                                        if(first == ' ')
                                                            risultato  = tmp.substring(1);
                                                        if (risultato.length() > 0) break;
                                                    }
                                                }
                                            }



                                        }
                                        if (risultato.length() > 0) break;
                                    }
                                }
                                else
                                { //non c'è livello2
                                    while ((line = br.readLine()) != null)
                                    {
                                        if (line.contains("[Start")) { risultato = "errore"; break; }  //trovato lo start del livello successivo, non va bene
                                        if (line.contains(nome_variabile))
                                        {
                                            risultato = SubstringExtensions.After(line, "=");
                                            char first = risultato.charAt(0);
                                            String tmp = risultato;
                                            if(first == ' ')
                                             risultato  = tmp.substring(1);

                                            if (risultato.length() > 0) break;
                                        }
                                    }
                                }
                            }
                            if (risultato.length() > 0) break;
                        }
                    }
                    else
                    { //non c'è livello1
                        while ((line = br.readLine()) != null)
                        {
                            if (line.contains("[Start")) { risultato = "errore"; break; }  //trovato lo start del livello successivo, non va bene
                            if (line.contains(nome_variabile))
                            {
                                risultato = SubstringExtensions.After(line, "=");
                                char first = risultato.charAt(0);
                                String tmp = risultato;
                                if(first == ' ')
                                    risultato  = tmp.substring(1);
                                if (risultato.length() > 0) break;
                            }
                        }
                    }
                }
                if (risultato.length() > 0) break;
            }
        }catch(Exception e){

            e.printStackTrace();
            Toast.makeText(context, "error reading txt field files", Toast.LENGTH_SHORT).show();

        }


        return  risultato;
    }

    //********************************************************************************************************************************************

//*************************************************************************************************
    // copy file
    //*************************************************************************************************

    public static void makecopy_file(String source, String dest, Context context) {

        File dirOri = new File(source);
        File dirDest = new File(dest);


        FileChannel src = null;
        FileChannel dst = null;

        try {
            src = new FileInputStream(dirOri).getChannel();
            dst = new FileOutputStream(dirDest).getChannel();
            long res = dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            // PrintToast("copy done", Color.BLACK, Color.YELLOW, 30);
        } catch (Exception e) {
            if(context != null)
            Toast.makeText(context, "Uneable to make parameter file copy", Toast.LENGTH_SHORT).show();

        }
    }
    //*************************************************************************************************
    // crea cartella se non esiste
    //*************************************************************************************************
    public static boolean Crea_cartella(String path)
    {
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            success = true;
        } else {
            success = false;
        }

        return  success;

    }

    //*************************************************************************************************
    // crea file bak
    //esempio Info_file.CreaFileBak(file_ESR_path, "storage/emulated/0/JamData/bak/", context);   //file back
    //*************************************************************************************************
    public static boolean CreaFileBak(String path_origine, String path_destinazione, Context context)
    {
        File folder = new File("storage/emulated/0/JamData/bak");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

        String path_bak_folder="";
        boolean ret = false;
        try {
            if (path_destinazione == null)   //se non ho passato la cartella di destinazione per il file back, ne creo una nella root del file d origine
            {
                String file_bak = SubstringExtensions.Before(path_origine, ".") + ".bak";  //cambio estenzione file
                String[] path_split = file_bak.split("/");
                if( path_split.length >= 2) {
                    for (int i = 0; i < path_split.length - 1; i++) {
                        path_bak_folder = path_bak_folder  + path_split[i]+"/";
                    }
                    path_bak_folder = path_bak_folder + "bak/" + path_split[path_split.length - 1];


                }else
                {
                    path_bak_folder = "bak/" + path_split[path_split.length - 1];

                }
                Info_file.makecopy_file(path_origine, path_bak_folder, context);        //eseguo la copia
                ret = true;


            }
            else
                {
                    String file_bak = SubstringExtensions.Before(path_origine, ".") + ".bak";  //cambio estenzione file
                    String[] path_split = file_bak.split("/");
                    String Nome_file = path_split[path_split.length-1];

                    Info_file.makecopy_file(path_origine, path_destinazione+Nome_file, context);        //eseguo la copia
                    ret = true;
                }
        }catch (Exception e)
        {
            ret = false;
        }
        return ret;

    }

    //*************************************************************************************************
    //*************************************************************************************************
    public static boolean ScriviFileTxt(String Path_Destinazione, ArrayList<String> Lista_stringhe)
    {
        boolean ret = false;


        try {
            Writer fileWriter = new FileWriter(Path_Destinazione, false); //overwrites file

            for (String elem_array: Lista_stringhe)
            {
                fileWriter.write(elem_array + "\r\n");
            }
            fileWriter.close();
            ret = true;



        }  catch(FileNotFoundException ex) {
           ret = false;

        }  catch(IOException ex) {
            ret = false;
        }


        return ret;
    }
    //*************************************************************************************************
    // cancello file
    //*************************************************************************************************
    public static Boolean DeleteFile(String file_esr_path) {
        File file = new File(file_esr_path);
        Boolean ret = file.delete();  //cancello file

        return  ret;
    }

    //*************************************************************************************************
    //*************************************************************************************************



    static class SubstringExtensions
    {
        /// <summary>
        /// Get string value between [first] a and [last] b.
        /// </summary>
        public static String Between(String value, String a, String b)
        {
            int posA = value.indexOf(a);
            int posB = value.lastIndexOf(b);
            if (posA == -1)
            {
                return "";
            }
            if (posB == -1)
            {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= posB)
            {
                return "";
            }
            return value.substring(adjustedPosA, posB - adjustedPosA);
        }

        /// <summary>
        /// Get string value after [first] a.
        /// </summary>
        public static String Before(String value, String a)
        {
            int posA = value.indexOf(a);
            if (posA == -1)
            {
                return "";
            }
            return value.substring(0, posA);
        }

        /// <summary>
        /// Get string value after [last] a.
        /// </summary>
        public static String After(String value, String a)
        {
            int posA = value.lastIndexOf(a);
            if (posA == -1)
            {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= value.length())
            {
                return "";
            }
            return value.substring(adjustedPosA);
        }
    }

//*************************************************************************************************
    //*************************************************************************************************

    public static class struct_Campo
    {
        public String path;
        public String livello0;
        public String livello1;
        public String livello2;
        public String livello3;
        public String nome_variabile;
        public String valore;
        public String versione;

        public struct_Campo(String Path,String Livello0,String Livello1, String Livello2,String Livello3,String Nome_variabile,String Valore,String Versione) {
            path = Path;
            livello0 = Livello0;
            livello1 = Livello1;
            livello2 = Livello2;
            livello3 = Livello3;
            nome_variabile = Nome_variabile;
            valore = Valore;
            versione = Versione;

        }


    }

    //*************************************************************************************************
    //*************************************************************************************************
    private static ArrayList<struct_Campo> Scomponi_File(ArrayList<struct_Campo> arrayCampi,String percorso) throws IOException {
        ArrayList<struct_Campo> arrayCampi_file = new ArrayList<>();
        boolean finito = false;
        String livello0="",livello1="",livello2="",livello3="",nome_variabile="",valore="",versione ="";
        String livello = "inizio";
        if(arrayCampi != null || percorso !=null)
        {
            String path;
            if(arrayCampi != null)
                path = arrayCampi.get(0).path;
            else
                path = percorso;

            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));


            String line = null;

            while ((line = br.readLine()) != null && !finito) {
                if(!line.equals("")) {
                    switch (livello) {
                        case "inizio":
                            if (line.contains("Start")) {
                                livello0 = line;
                                livello = "Versione";
                                struct_Campo Campo = new struct_Campo(path, "Livello0", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Versione":
                            if (line.contains("Version")) {
                                versione = line;
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Versione", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello0Start":
                            if (line.contains("Start")) {
                                livello = "Livello1Dentro";
                                livello1 = line;
                                struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello0Dentro":
                            if (line.contains("End")) {
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Livello0", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello1Dentro";
                                    livello2 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello0), Pulisci(livello0), Pulisci(livello0), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }

                            break;
                        case "Livello1Dentro":
                            if (line.contains("End")) {
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello2Dentro";
                                    livello2 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello2", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello1), Pulisci(livello1), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello2Dentro":
                            if (line.contains("End")) {
                                livello = "Livello1Dentro";
                                struct_Campo Campo = new struct_Campo(path, "Livello2", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello3Dentro";
                                    livello3 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello3", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello2), Pulisci(livello2), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;

                        case "Livello3Dentro":
                            if (line.contains("End")) {
                                livello = "Livello2Dentro";
                                struct_Campo Campo = new struct_Campo(path, "Livello3", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {

                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello2), Pulisci(livello3), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;


                        default:
                            break;
                    }
                }
            }
            //ultima riga
            //  String End_tmp = (Pulisci( arrayCampi_file.get(0).livello1.toString()));
            //   String End = End_tmp.replace("Start","End");
            //  struct_Campo Campo = new struct_Campo(path,"Livello0",End,"","","","","") ;
            //  arrayCampi_file.add(Campo);


        }





        return arrayCampi_file;
    }
    //*************************************************************************************************
    //*************************************************************************************************
    private static String Pulisci(String line) {
        String ret1 = line.replaceAll("\t|\r|\n|", "");
        String ret  = ret1.replaceAll("\\s","");        //tolgo spazi
        //ret = ret + "\r\n";

        return ret;
    }
    //*************************************************************************************************
    // prendo l'array dei campi e lo scrivo dentro il file
    //*************************************************************************************************
    private static void ScrivoTuttiCampiSuFile(ArrayList<struct_Campo> arrayCampi) throws FileNotFoundException {
        String path = arrayCampi.get(0).path;

        // path = path+"1";
        FileOutputStream fostream = new FileOutputStream(path);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fostream));

        try{
            /* x debuggare
            for (int i = 0; i<arrayCampi.size(); i++){
                struct_Campo item = arrayCampi.get(i);
                writer.write(item.livello0+","+item.livello1+","+item.livello2+","+item.livello3+","+item.nome_variabile+","+item.valore+","+item.versione+"\r\n");
            }
            writer.write("\r\n");
            writer.write("\r\n");
            // fine x debuggare
             */
            for (struct_Campo item:arrayCampi) {
                if(item.livello0.contains("Versione"))  {writer.write("\t"+item.livello1 +"\r\n");}
                else {
                    if (!item.livello0.contains("[") && !item.livello0.contains("]")) {
                        if (item.livello0.contains("0")) {
                            writer.write(item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("1")) {
                            writer.write("\t" + item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("2")) {
                            writer.write("\t\t" + item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("3")) {
                            writer.write("\t\t\t" + item.livello1 + "\r\n");
                        }
                    } else {
                        //Non servono i pulisci altrimenti mi toglie gli spazi e non funziona
                        if (item.livello0.equals(item.livello1) && item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && !item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && !item.livello1.equals(item.livello2) && !item.livello2.equals(item.livello3)) {
                            writer.write("\t\t\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }

                    }
                }




            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {

                if (writer != null)
                    writer.close();

                if (writer != null)
                    writer.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

}
