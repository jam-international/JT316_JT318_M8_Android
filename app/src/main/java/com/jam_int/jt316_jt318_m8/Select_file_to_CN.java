package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jamint.recipes.Element;
import com.jamint.recipes.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import communication.Protocol;
import communication.ShoppingList;


public class Select_file_to_CN extends Activity {

   // private static boolean TascaLeft;
    ListView LvListFile;
    ImageButton IButton_Back, IButton_Confirm, IButton_Exit, IButton_LoadFromUsb;
    String estensione_file = "XML";
    String Folder_corrente = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette";
    File FileSelezionato;
    Intent databack_load_udf = new Intent();

    final private static int RESULT_USB = 101;


    ShoppingList sl;

    ArrayList<Byte> Listatobyte;
    ProgressBar Progress_Bar;
    Protocol.OnProgressListener pl;
    int ProgressBar_value = 0;
   // Boolean Provenientza_Testa1 = true;

    String Machine_model, operazione = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");
        if (!sl.IsConnected()) { sl.Connect();}

        databack_load_udf.setData(Uri.parse("NO"));
        setResult(RESULT_OK, databack_load_udf);   //nel caso esco senza caricare...

        IButton_Confirm = (ImageButton) findViewById(R.id.imageButton_confirm);
        IButton_Exit = (ImageButton) findViewById(R.id.imageButton_exit);
        IButton_Back = (ImageButton) findViewById(R.id.imageButton_back);
        IButton_LoadFromUsb = (ImageButton) findViewById(R.id.imageButton_LoadFromUsb);
        LvListFile = (ListView) findViewById(R.id.LvList_file);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            operazione = extras.getString("operazione");

            if (operazione.equals("Loading....")) {
                FileSelezionato = null;
               // TascaLeft = extras.getBoolean("Tasca_Left");
               // Provenientza_Testa1 = extras.getBoolean("Testa_selezionata");

            } else {
                IButton_Confirm.setVisibility(View.GONE);
                IButton_Back.setVisibility(View.GONE);
                IButton_Exit.setVisibility(View.GONE);
                IButton_LoadFromUsb.setVisibility(View.GONE);
                String filepath = extras.getString("File_path");

               // TascaLeft = extras.getBoolean("Tasca_Left");
              //  Provenientza_Testa1 = extras.getBoolean("Testa_selezionata");
                FileSelezionato = new File(filepath);
                InviaFileSlezionato(FileSelezionato);

            }
        }

        Progress_Bar = (ProgressBar) findViewById(R.id.progressBarSave); // initiate the progress bar
        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "info_Jam.txt is missing", Toast.LENGTH_SHORT).show();
        }
        Inizializzo_eventi();

        IButton_Confirm.setEnabled(false);


        try {
            if (operazione.equals("Loading....")) {
                ShowList(LvListFile, Folder_corrente, estensione_file);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "USB memory error", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

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


    }






    //*************************************************************************************************
    // ShowList
    //*************************************************************************************************
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {
        ArrayAdapter<String> adapter;
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();
        File[] allEntries = new File(path_folder).listFiles();

        for (int i = 0; i < allEntries.length; i++) {
            if (allEntries[i].isDirectory()) {
                 folders.add(allEntries[i].getName());
            } else if (allEntries[i].isFile()) {
                String name = allEntries[i].getName();
                String estensione = "";
                try {
                    estensione = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {

                }

                if (estensione.equalsIgnoreCase(filtro_estensione))
                    files.add(allEntries[i].getName());
            }
        }

        Collections.sort(folders, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        listItems.clear();

        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        for (int i = 0; i < files.size(); i++) {
            listItems.add(files.get(i));
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                String str = textView.getText().toString();
                if(str.endsWith("/")) {
                    textView.setTextColor(Color.BLUE);
                }

                return view;
            }
        };
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }

    //*************************************************************************************************
    // eventi List
    //*************************************************************************************************
    private void Inizializzo_eventi() {

        //Evento alla pressione di una riga del List di DX
        LvListFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (entryName.endsWith("/")) {  //è un file o folder?
                    //folder
                        Folder_corrente = Folder_corrente + "/" + SubString.SubstringExtensions.Before(entryName, "/");
                        ShowList(LvListFile,Folder_corrente,estensione_file);
                } else {   //file
                    String file_selezionato = Folder_corrente + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato != null)
                        file_precedente_selezionato = FileSelezionato.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva(adapterView);
                        IButton_Confirm.setEnabled(false);
                        FileSelezionato = null;

                    } else {
                        //selezionato nuovo file
                        FileSelezionato = new File(Folder_corrente + "/" + entryName);
                        SelezionaGrigio(adapterView, view);
                        IButton_Confirm.setEnabled(true);

                    }

                }


            }
        });

         pl = new Protocol.OnProgressListener() {
            @Override
            public void onProgressUpdate(int Completion) {

                // Display Progress value (Completion of 100)
                ProgressBar_value = Completion;
                Progress_Bar.setProgress(ProgressBar_value);

            }
        };
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void TolgoSelezioneVisiva(AdapterView<?> adapterView) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
    }

    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void SelezionaGrigio(AdapterView<?> adapterView, View view) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

        // change the background color of the selected element
        view.setBackgroundColor(Color.RED);

    }

    //*************************************************************************************************
    //On click del button Exit
    //*************************************************************************************************
    public void Onclick_button_Exit(View view) throws IOException {


        Esci();

    }

    //*************************************************************************************************
    //On click Confirm
    //*************************************************************************************************
    public void onclick_confirm(View view) throws IOException {


        if (FileSelezionato != null) {
            databack_load_udf.setData(Uri.parse("CARICATO"));
            setResult(RESULT_OK, databack_load_udf);   //indico al prossimo activityResult che ho caricato un udf quindi va riletto
            DisabilitaListView();
            IButton_Back.setVisibility(View.GONE);
            IButton_LoadFromUsb.setVisibility(View.GONE);
            IButton_Exit.setVisibility(View.GONE);
            IButton_Confirm.setVisibility(View.GONE);
            InviaFileSlezionato(FileSelezionato);
        }
    }

    //*************************************************************************************************
    //InviaFileSlezionato
    //*************************************************************************************************
    private void InviaFileSlezionato(File fileSelezionato) {


        Recipe recipe = new Recipe();
        try
        {
            recipe.open(FileSelezionato);
            if(recipe.elements.size()!=0) {
                String path_xml = FileSelezionato.getPath();
                String path_udf = path_xml.replace(".xml", ".udf");

                try {
                    File fileUdf = new File(path_udf);


                    if (recipe.getPoints().size() == 0) {
                        for (Element elem : recipe.elements)
                            elem.createSteps();

                        recipe.save(FileSelezionato);
                    }
                    try {
                        recipe.recipeNumber = 1;

                        recipe.exportToUdf(fileUdf);



                        if (!sl.IsConnected()) { sl.Connect();}
                        if (sl.IsConnected()) {
                            new Scrivi_file_dentro_CN (Select_file_to_CN.this).execute(fileUdf.getAbsolutePath(), fileUdf.getName());

                        } else {
                            Toast.makeText(this,
                                    "CN not connected",
                                    Toast.LENGTH_SHORT).show();
                        }



                    } catch (Exception e) {
                        IButton_Exit.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "error saving XML file " + fileSelezionato.getPath(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    IButton_Exit.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "error creating USR file ", Toast.LENGTH_SHORT).show();


                }
            }else
            {
                Toast.makeText(this, "XML is Empty", Toast.LENGTH_SHORT).show();
                Esci();
            }

        }
        catch(Exception e)

        {
            IButton_Exit.setVisibility(View.VISIBLE);
            Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
        }
    }




    //*************************************************************************************************
    //DisabilitaListView
    //*************************************************************************************************
    private void DisabilitaListView() {
        if(LvListFile!=null) {
            for (int i = 0; i < LvListFile.getCount(); i++) {
                View v = getViewByPosition(i, LvListFile);
                v.setEnabled(false);
            }
        }
    }
    //*************************************************************************************************
    //DisabilitaListView
    //*************************************************************************************************
    private void AbilitaListView() {
        if(LvListFile!=null) {
            for (int i = 0; i < LvListFile.getCount(); i++) {
                View v = getViewByPosition(i, LvListFile);
                v.setEnabled(true);
            }
        }
    }
    //*************************************************************************************************
    //DisabilitaListView
    //*************************************************************************************************
    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition =firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }


    //*************************************************************************************************
    //EsrToUsr
    //*************************************************************************************************
    public File XmlToUsr(File file_xml) throws IOException {

        Recipe r = new Recipe();
        try {
            r.open(file_xml);
        }catch(Exception e)
        {
            Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
        }
        File file = new File(file_xml.getPath().replace(".xml",".usr"));
        r.exportToUsr(file);
        return file;

    }

    //**************************************************************************************************
    // Pulisco la stringa da \t \r \n
    //**************************************************************************************************
    private static String Pulisci(String line) {

        String ret = line.replaceAll("\t|\r|\n", "");
        ret = ret + "\r\n";

        return ret;
    }



    public  void onclick_back(View v)
    {
        if (!SubString.SubstringExtensions.After(Folder_corrente, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.SubstringExtensions.BeforeLast(Folder_corrente, "/");
            Folder_corrente = Folder_back;
            ShowList(LvListFile, Folder_corrente, estensione_file);
            TolgoSelezioneVisiva(LvListFile);
        }
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

    public void setListatobyte(ArrayList<Byte> listatobyte) {
        this.Listatobyte = listatobyte;
    }

    public ArrayList getListatobyte() {
        return Listatobyte;
    }


    //**************************************************************************************************
    //
    //**************************************************************************************************
    private void Esci() {

            this.finish();

    }


    //*************************************************************************************************
    //On click del button Exit
    //*************************************************************************************************
    public void onclick_load_from_usb(View view) throws IOException {

        Intent intent_par = new Intent(getApplicationContext(), Usb_Files_Activity.class);
        startActivityForResult(intent_par, RESULT_USB);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent databack) {
        super.onActivityResult(requestCode, resultCode, databack);
        switch (requestCode) {

            case RESULT_USB: {
                ShowList(LvListFile, Folder_corrente, estensione_file);
                break;
            }
            default:
                break;
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
            String Path_destinazione = "C:\\cnc\\userdata\\" + params[1];

            Boolean r = sl.FileUpload(Path_destinazione, Path_fonte, null);


            return r;
        }

        @Override
        protected void onPostExecute(Boolean r) {
            super.onPostExecute(r);
            this.dialog.dismiss();

            if(r)
            {
                if(FileSelezionato != null) {

                    Values.File_XML_path = FileSelezionato.getPath();
                }


                finish();
            }


            else {
                Toast.makeText(getApplicationContext(), "Send error", Toast.LENGTH_SHORT).show();

                AbilitaListView();
                IButton_Confirm.setVisibility(View.VISIBLE);
                IButton_Back.setVisibility(View.VISIBLE);
                IButton_Exit.setVisibility(View.VISIBLE);
                IButton_LoadFromUsb.setVisibility(View.VISIBLE);
            }

        }
    }
}
