package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jamint.recipes.Recipe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Copy_Files_Activity extends Activity {

    ListView LvList;
    ImageButton Button_copy;
    ImageButton Button_delete;
    ImageButton Button_rename;
    ImageButton Button_move;
    ImageButton IButton_exit;
    Boolean Thread_Running = false,  StopThread = false, Folder = false;

    final private static int POPUPFOLDER = 104;

    String estensione_list = "xml";
    String Folder_corrente = "storage/emulated/0/ricette";

    File FileSelezionato;

    String Folder2;
    String FileName;
    Thread_LoopEmergenza thread_LoopEmergenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_files);

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);


        //scrive in un file "*.stacktrace" eventuale cause di crash
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");


            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(
                    dir.getAbsolutePath(), "null"));
        }

        //findViewById
        Button_delete = (ImageButton) findViewById(R.id.btn_delete);
        Button_copy = (ImageButton) findViewById(R.id.btn_copy);
        Button_rename = (ImageButton) findViewById(R.id.btn_rename);
        Button_move = (ImageButton) findViewById(R.id.btn_move);
        IButton_exit = (ImageButton) findViewById(R.id.imageButton_exit);
        Button_delete.setEnabled(false);
        Button_copy.setEnabled(false);
        Button_rename.setEnabled(false);
        Button_move.setEnabled(false);
        Button_delete.setImageResource(R.drawable.gomma_disable);
        Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
        Button_rename.setImageResource(R.drawable.ic_rename_disable);
        Button_move.setImageResource(R.drawable.move_file_disable);
        LvList = (ListView) findViewById(R.id.LvList);
        Inizializzo_eventi();
        ShowList(LvList,Folder_corrente, estensione_list);

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

        String[] textlist = new String[listItems.size()];
        listItems.toArray(textlist);

        adapter = new CustomMyAdapter(Copy_Files_Activity.this, textlist);

        /*adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                /*String str = textView.getText().toString();
                if (str.endsWith("/")) {
                    textView.setTextColor(Color.BLUE);
                }

                return view;
            }
        };*/
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }
    //*************************************************************************************************
    // eventi List
    //*************************************************************************************************
    private void Inizializzo_eventi() {

        LvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                    if (entryName.endsWith("/")) {  //è un file o folder?
                        //folder
                        if (Folder && FileSelezionato.getPath().equals(Folder_corrente + "/" + SubString.SubstringExtensions.Before(entryName, "/"))) {
                            Folder_corrente = Folder_corrente + "/" + SubString.SubstringExtensions.Before(entryName, "/");
                            ShowList(LvList, Folder_corrente, estensione_list);
                            Button_copy.setEnabled(false);
                            Button_delete.setEnabled(false);
                            Button_rename.setEnabled(false);
                            Button_move.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                            Button_move.setImageResource(R.drawable.move_file_disable);
                            Folder = false;
                        } else {
                            String file_selezionato = Folder_corrente + "/" + entryName;
                            String file_precedente_selezionato = "";
                            if (FileSelezionato != null)
                                file_precedente_selezionato = FileSelezionato.getPath();
                            if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                                TolgoSelezioneVisiva(adapterView);
                                Button_copy.setEnabled(false);
                                Button_delete.setEnabled(false);
                                Button_rename.setEnabled(false);
                                Button_move.setEnabled(false);
                                Button_delete.setImageResource(R.drawable.gomma_disable);
                                Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                Button_move.setImageResource(R.drawable.move_file_disable);
                                FileSelezionato = null;

                            } else {
                                //selezionato nuovo file
                                FileSelezionato = new File(Folder_corrente + "/" + entryName);
                                SelezionaGrigio(adapterView, view);
                                Button_copy.setEnabled(true);
                                Button_delete.setEnabled(true);
                                Button_rename.setEnabled(true);
                                Button_move.setEnabled(true);
                                Button_delete.setImageResource(R.drawable.gomma);
                                Button_copy.setImageResource(R.drawable.ic_copy_file);
                                Button_rename.setImageResource(R.drawable.ic_rename);
                                Button_move.setImageResource(R.drawable.move_file);
                            }
                            Folder = true;
                        }

                    } else {   //file
                        String file_selezionato = Folder_corrente + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato != null)
                            file_precedente_selezionato = FileSelezionato.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisiva(adapterView);
                            Button_copy.setEnabled(false);
                            Button_delete.setEnabled(false);
                            Button_rename.setEnabled(false);
                            Button_move.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                            Button_move.setImageResource(R.drawable.move_file_disable);
                            FileSelezionato = null;


                        } else {
                            //selezionato nuovo file
                            FileSelezionato = new File(Folder_corrente + "/" + entryName);
                            SelezionaGrigio(adapterView, view);
                            Button_copy.setEnabled(true);
                             String str1 = FileSelezionato.getAbsolutePath();
                            String str2 = Values.File_XML_path_R;
                            if(!str1.equals(str2)){ //controllo se ho cancellato il file carico nel CN
                                Button_delete.setEnabled(true);
                                Button_delete.setImageResource(R.drawable.gomma);
                            }else{
                                Button_delete.setEnabled(false);
                                Button_delete.setImageResource(R.drawable.gomma_disable);
                            }
                            Button_rename.setEnabled(true);
                            Button_move.setEnabled(true);
                      
                            Button_copy.setImageResource(R.drawable.ic_copy_file);
                            Button_rename.setImageResource(R.drawable.ic_rename);
                            Button_move.setImageResource(R.drawable.move_file);
                        }

                        Folder = false;

                    }
            }

        });
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
    //On click del button copia
    //*************************************************************************************************
    public void On_click_btn_copy(View view) throws IOException {

        if(!FileSelezionato.isDirectory()) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.Save));

            // Setting Dialog Message
            /*try {
                //Tolgo la parte di storage/emulated/0/ricette
                String newFolder = "";
                String[] str = Folder2.split("/");
                int i = 0;
                for (String st : str) {
                    if (i == 1) {
                        newFolder = newFolder + "/" + st;
                    }
                    if (st.equals("ricette")) {
                        i = 1;
                    }
                }

            } catch (Exception e) {}*/

            alertDialog.setMessage(getResources().getString(R.string.FileName_));

            final EditText input = new EditText(this);
            input.setFocusable(false);
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(23)});
            input.setText(FileSelezionato.getName().replace(".xml",""));
            input.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
                    }

                    return false;
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (FileSelezionato != null) {

                                if (!input.getText().toString().matches("")) {
                                    boolean esiste = ControlloSeEsiste(LvList, input.getText().toString() + ".xml");
                                    if (esiste) {
                                        // chiamo il messaggio Yes/No per sovrascrivere
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Copy_Files_Activity.this);

                                        builder.setTitle("File already exists");
                                        builder.setMessage("do you want overwrite existing file?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                // Premuto Yes
                                                File dir;
                                                //if(Folder2 == null) {
                                                dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                            /*}
                                            else
                                            {
                                                dir = new File(Folder2);
                                            }*/

                                                String Path_HMI = dir.getPath() + "/" + input.getText().toString() + ".xml";
                                                File DestinationLocation = new File(Path_HMI);

                                                try {
                                                    copyFile(FileSelezionato, DestinationLocation);
                                                    Values.File_XML_path_R = DestinationLocation.getPath();

                                                    Recipe r = new Recipe();
                                                    try {
                                                        r.open(FileSelezionato);
                                                        TolgoSelezioneVisiva(LvList);

                                                    } catch (Exception e) {
                                                        Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    r.exportToUsr(new File(DestinationLocation.getPath().replace(input.getText().toString() + ".xml", input.getText().toString() + ".usr")));
                                                    ShowList(LvList, Folder_corrente, estensione_list);
                                                    Button_copy.setEnabled(false);
                                                    Button_delete.setEnabled(false);
                                                    Button_rename.setEnabled(false);
                                                    Button_move.setEnabled(false);
                                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                                    Button_move.setImageResource(R.drawable.move_file_disable);

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();

                                                }
                                                dialog.dismiss();
                                            }

                                        });

                                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Premuto no

                                                dialog.dismiss();
                                            }
                                        });

                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    } else {
                                        File dir;
                                        //if(Folder2 == null) {
                                        dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                    /*}
                                    else
                                    {
                                        dir = new File(Folder2);
                                    }*/
                                        String Path_HMI = dir.getPath() + "/" + input.getText().toString() + ".xml";
                                        File DestinationLocation = new File(Path_HMI);

                                        try {
                                            copyFile(FileSelezionato, DestinationLocation);
                                            Values.File_XML_path_R = DestinationLocation.getPath();

                                            Recipe r = new Recipe();
                                            try {
                                                r.open(FileSelezionato);
                                                TolgoSelezioneVisiva(LvList);

                                            } catch (Exception e) {
                                                Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                            }
                                            r.exportToUsr(new File(DestinationLocation.getPath().replace(input.getText().toString() + ".xml", input.getText().toString() + ".usr")));
                                            ShowList(LvList, Folder_corrente, estensione_list);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    TolgoSelezioneVisiva(LvList);
                                    Button_copy.setEnabled(false);
                                    Button_delete.setEnabled(false);
                                    Button_rename.setEnabled(false);
                                    Button_move.setEnabled(false);
                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                    Button_move.setImageResource(R.drawable.move_file_disable);
                                }
                                else
                                {
                                    Toast.makeText(Copy_Files_Activity.this,"File name not valid",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.setNeutralButton(getResources().getString(R.string.Folder),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FileName = input.getText().toString();
                            Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                            startActivityForResult(intent_par,POPUPFOLDER);
                        }
                    });

            alertDialog.show();
        }
        else
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.Save));

            // Setting Dialog Message
            try {
                //Tolgo la parte di storage/emulated/0/ricette
                String newFolder = "";
                String[] str = Folder2.split("/");
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


            final EditText input = new EditText(this);
            input.setFocusable(false);
            input.setText(FileSelezionato.getName());
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(23)});
            input.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
            alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            try {
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir1 = new File(root.getAbsolutePath() + "/ricette");
                                File dir;
                                if(Folder2 == null) {
                                    dir = new File(root.getAbsolutePath() + "/ricette");
                                }
                                else
                                {
                                    dir = new File(Folder2);
                                }
                                File dest = new File(dir, input.getText().toString());
                                File file = new File(dir1, FileSelezionato.getName());
                                dir.mkdirs();
                                copyFile(file, dest);
                                ShowList(LvList, Folder_corrente, estensione_list);
                            }catch (Exception e){}
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.setNeutralButton(getResources().getString(R.string.Folder),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                            startActivityForResult(intent_par,POPUPFOLDER);
                        }
                    });


            AlertDialog d = alertDialog.show();
        }
        Folder = false;
    }

    private void InviaFileA_Cn(File fileSelezionato) {
        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent.putExtra("File_path", fileSelezionato.getPath());
        intent.putExtra("operazione", "Saving....");
        startActivity(intent);

    }

    //*************************************************************************************************
    //
    //*************************************************************************************************
    private boolean ControlloSeEsiste(ListView lvList, String filename) {
        Adapter lista = lvList.getAdapter();
        for (int i = 0; i < lista.getCount(); i++) {
            String item = (String) lista.getItem(i);
            if(item.equals(filename)) return true;

        }
        return false;
    }
    //*************************************************************************************************
    //
    //*************************************************************************************************
    public Boolean copyFile(File sourceFile, File destFile)
            throws IOException {
        if(destFile.exists()){
            destFile.delete();
        }

        if (!destFile.exists()) {

            if(sourceFile.isDirectory())
            {
                destFile.mkdir();
                File f = new File(destFile.getPath());
                if (f != null) f.delete();

                f.mkdir();

                File[] allEntries = new File(sourceFile.getPath()).listFiles();

                for(File file : allEntries) {
                    if(file.isDirectory())
                    {
                        Copy(file,f);
                    }
                    else {
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(file).getChannel();
                            File destFile1 = new File(f,file.getName());
                            destination = new FileOutputStream(destFile1).getChannel();
                            destination.transferFrom(source, 0, source.size());
                        } finally {
                            if (source != null)
                                source.close();
                            if (destination != null)
                                destination.close();
                        }
                    }
                }
            }
            else {
                destFile.createNewFile();
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(sourceFile).getChannel();
                    destination = new FileOutputStream(destFile).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } finally {
                    if (source != null)
                        source.close();
                    if (destination != null)
                        destination.close();
                }
            }
            return true;

        }
        return false;
    }


    public void Copy(File sourceFile, File destFile) throws IOException
    {
        File f = new File(destFile.getPath()+"/"+sourceFile.getName());
        if (f != null) f.delete();

        f.mkdir();

        File[] allEntries = new File(sourceFile.getPath()).listFiles();

        for(File file : allEntries) {
            if(file.isDirectory())
            {
                Copy(file,f);
            }
            else {
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(file).getChannel();
                    File destFile1 = new File(f,file.getName());
                    destination = new FileOutputStream(destFile1).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } finally {
                    if (source != null)
                        source.close();
                    if (destination != null)
                        destination.close();
                }
            }
        }
    }
    //*************************************************************************************************
    //On click del button delete sx
    //*************************************************************************************************
    public void On_click_btn_delete(View view) throws IOException {

        if ((FileSelezionato != null)) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm delete");
            builder.setMessage("Are you sure to delete file?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Premuto Yes
                    deleteRecursive(FileSelezionato);
                    if(!FileSelezionato.isDirectory()) {
                        File file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                        if (file.exists()) {
                            Cancella_File(file);
                        }
                    }
                    //Cancella_File(FileSelezionato_dx , LvListDX ,estensione_list_dx);
                    dialog.dismiss();
                    ShowList(LvList,Folder_corrente,estensione_list);
                    
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Premuto no

                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            Button_delete.setEnabled(false);
            Button_copy.setEnabled(false);
            Button_rename.setEnabled(false);
            Button_move.setEnabled(false);
            Button_delete.setImageResource(R.drawable.gomma_disable);
            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
            Button_rename.setImageResource(R.drawable.ic_rename_disable);
            Button_move.setImageResource(R.drawable.move_file_disable);
            ShowList(LvList,Folder_corrente, estensione_list);
            Folder = false;
        }

    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    //**********************************************************************
    //
    //**********************************************************************
    private File CreaProgCucituraVuoto() {



        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/JamData");

        dir.mkdirs();
        File file = new File(dir,  "file_empty.xml");
        File file1 = new File(dir, "file_empty.usr");

        if(!file.exists() && !file1.exists()){
            Recipe r = new Recipe();
            r.setDrawPosition(new PointF(0.1f, 0f));
            r.drawFeedTo(new PointF(10f,10f));
            r.drawFeedTo(new PointF(0.1f, 0f));
            try {
                r.save(file);
                try {
                    r.exportToUsr(file1);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
            }
        }

        return file;


    }
    //**********************************************************************
    //
    //**********************************************************************
    public void On_click_btn_rename(View view) throws IOException {

        if ((FileSelezionato != null)) {
            if(!FileSelezionato.isDirectory()) {
                final Recipe r = new Recipe();

                try {
                    r.open(FileSelezionato);
                } catch (Exception e) {
                    Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                }

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.FileName_));
                final EditText input = new EditText(this);
                input.setFocusable(false);
                int i = FileSelezionato.getName().lastIndexOf('.');
                String name = FileSelezionato.getName().substring(0, i);
                input.setText(name);
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(23)});
                input.setOnTouchListener(new View.OnTouchListener() {

                    //	@SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
                alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir = new File(root.getAbsolutePath() + "/ricette");
                                dir.mkdirs();
                                File file = new File(dir, input.getText().toString() + ".xml");
                                File file1 = new File(dir, input.getText().toString() + ".usr");
                                if (file.exists() || file1.exists()) {
                                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Copy_Files_Activity.this);

                                    // Setting Dialog Title
                                    alertDialog1.setTitle("overWrite");

                                    // Setting Dialog Message
                                    alertDialog1.setMessage("overWrite?");

                                    // Setting Positive "Yes" Button
                                    alertDialog1.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    File root = android.os.Environment.getExternalStorageDirectory();
                                                    File dir = new File(root.getAbsolutePath() + "/ricette");
                                                    dir.mkdirs();
                                                    File file = new File(dir, input.getText().toString() + ".xml");
                                                    File file1 = new File(dir, input.getText().toString() + ".usr");
                                                    try {
                                                        r.save(file);
                                                        try {
                                                            r.exportToUsr(file1);
                                                            Button_copy.setEnabled(false);
                                                            Button_delete.setEnabled(false);
                                                            Button_rename.setEnabled(false);
                                                            Button_move.setEnabled(false);
                                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                                            InviaFileA_Cn(file);

                                                        } catch (Exception e) {
                                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    Cancella_File(FileSelezionato);
                                                    file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                                                    if (file.exists()) {
                                                        Cancella_File(file);
                                                    }
                                                    ShowList(LvList, Folder_corrente, estensione_list);
                                                }

                                            });

                                    alertDialog1.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    alertDialog1.show();

                                } else {
                                    try {
                                        r.save(file);
                                        try {
                                            r.exportToUsr(file1);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                            InviaFileA_Cn(file);

                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                        }
                                        Cancella_File(FileSelezionato);
                                        file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                                        if (file.exists()) {
                                            Cancella_File(file);
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    ShowList(LvList, Folder_corrente, estensione_list);
                                }
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                AlertDialog d = alertDialog.show();
            }
            else
            {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage("Folder Name:");
                final EditText input = new EditText(this);
                input.setFocusable(false);
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(23)});
                input.setText(FileSelezionato.getName());
                input.setOnTouchListener(new View.OnTouchListener() {

                    //	@SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
                alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                try {
                                    File root = android.os.Environment.getExternalStorageDirectory();
                                    File dir = new File(root.getAbsolutePath() + "/ricette");
                                    File dest = new File(dir, input.getText().toString());
                                    File file = new File(dir, FileSelezionato.getName());
                                    dir.mkdirs();
                                    copyFile(file, dest);
                                    deleteRecursive(FileSelezionato);
                                    ShowList(LvList, Folder_corrente, estensione_list);
                                }catch (Exception e){}
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                AlertDialog d = alertDialog.show();
            }
            Folder = false;

        }
    }

    //*************************************************************************************************
    //Cancella file dal list
    //*************************************************************************************************
    private void Cancella_File(File fileSelezionato) {

        boolean deleted = fileSelezionato.delete();

    }

    //*************************************************************************************************
    //On click del button Back
    //*************************************************************************************************
    public void On_click_button_back(View view) throws IOException {

        if (!SubString.SubstringExtensions.After(Folder_corrente, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.SubstringExtensions.BeforeLast(Folder_corrente, "/");
            Folder_corrente = Folder_back;
            ShowList(LvList, Folder_corrente, estensione_list);
            Button_delete.setEnabled(false);
            Button_rename.setEnabled(false);
            Button_copy.setEnabled(false);
            Button_move.setEnabled(false);
            Button_delete.setImageResource(R.drawable.gomma_disable);
            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
            Button_rename.setImageResource(R.drawable.ic_rename_disable);
            Button_move.setImageResource(R.drawable.move_file_disable);

            TolgoSelezioneVisiva(LvList);
            Folder = false;
        }
    }

    //*************************************************************************************************
    //On click del button Move
    //*************************************************************************************************
    public void On_click_button_move(View view) throws IOException {

        //Parte il PopUp per selezionare la cartella
        Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
        startActivityForResult(intent_par,1);
        //Parte la fase di copia/cancella

    }

    //*************************************************************************************************
    //On click del button Create Folder
    //*************************************************************************************************
    public void On_click_btn_createfolder(View view) throws IOException {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Create Folder");

        // Setting Dialog Message
        alertDialog.setMessage("Folder Name:");
        final EditText input = new EditText(this);
        input.setFocusable(false);
        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(23)});
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
        alertDialog.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            File Folder = new File(Folder_corrente,input.getText().toString());
                            Folder.mkdir();
                            ShowList(LvList, Folder_corrente, estensione_list);
                        }catch (Exception e){}
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog d = alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(resultCode==0)
            {

            }
            else
            {
                String Folder = data.getExtras().getString("FolderPath");
                if(!Folder.equals(FileSelezionato.getPath())) {
                    File f = new File(Folder + "/" + FileSelezionato.getName());
                    try {
                        copyFile(FileSelezionato, f);
                    } catch (Exception e) {
                    }
                    deleteRecursive(FileSelezionato);
                    ShowList(LvList, Folder_corrente, estensione_list);
                }
                if(!Folder.equals(FileSelezionato.getPath())) {
                    File fnew = new File(Folder + "/" + FileSelezionato.getName().replace(".xml", ".usr"));
                    File fold = new File(FileSelezionato.getPath().replace(FileSelezionato.getName(), FileSelezionato.getName().replace(".xml", ".usr")));
                    try {
                        copyFile(fold, fnew);
                    } catch (Exception e) {
                    }
                    deleteRecursive(fold);
                    ShowList(LvList, Folder_corrente, estensione_list);
                }
                File f = new File(Folder + "/" + FileSelezionato.getName());
                InviaFileA_Cn(f);
            }
        } else if(requestCode==POPUPFOLDER) {
            if (resultCode == 0) {

            } else {
                Folder2 = data.getExtras().getString("FolderPath");
                try {
                    if (FileSelezionato != null) {

                        if (!FileName.matches("")) {
                            boolean esiste = ControlloSeEsiste(LvList, FileName + ".xml");
                            if (esiste) {
                                // chiamo il messaggio Yes/No per sovrascrivere
                                AlertDialog.Builder builder = new AlertDialog.Builder(Copy_Files_Activity.this);

                                builder.setTitle("File already exists");
                                builder.setMessage("do you want overwrite existing file?");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Premuto Yes
                                        File dir;
                                        if (Folder2 == null) {
                                            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                        } else {
                                            dir = new File(Folder2);
                                        }

                                        String Path_HMI = dir.getPath() + "/" + FileName + ".xml";
                                        File DestinationLocation = new File(Path_HMI);

                                        try {
                                            copyFile(FileSelezionato, DestinationLocation);
                                            Values.File_XML_path_R = DestinationLocation.getPath();

                                            Recipe r = new Recipe();
                                            try {
                                                r.open(FileSelezionato);
                                                TolgoSelezioneVisiva(LvList);

                                            } catch (Exception e) {
                                                Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                            }
                                            r.exportToUsr(new File(DestinationLocation.getPath().replace(FileName + ".xml", FileName + ".usr")));
                                            ShowList(LvList, Folder_corrente, estensione_list);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();

                                        }
                                        dialog.dismiss();
                                    }

                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Premuto no

                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                File dir;
                                if (Folder2 == null) {
                                    dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                } else {
                                    dir = new File(Folder2);
                                }
                                String Path_HMI = dir.getPath() + "/" + FileName + ".xml";
                                File DestinationLocation = new File(Path_HMI);

                                try {
                                    copyFile(FileSelezionato, DestinationLocation);
                                    Values.File_XML_path_R = DestinationLocation.getPath();

                                    Recipe r = new Recipe();
                                    try {
                                        r.open(FileSelezionato);
                                        TolgoSelezioneVisiva(LvList);

                                    } catch (Exception e) {
                                        Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    r.exportToUsr(new File(DestinationLocation.getPath().replace(FileName + ".xml", FileName + ".usr")));
                                    ShowList(LvList, Folder_corrente, estensione_list);
                                    Button_copy.setEnabled(false);
                                    Button_delete.setEnabled(false);
                                    Button_rename.setEnabled(false);
                                    Button_move.setEnabled(false);
                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                    Button_move.setImageResource(R.drawable.move_file_disable);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            TolgoSelezioneVisiva(LvList);
                            Button_copy.setEnabled(false);
                            Button_delete.setEnabled(false);
                            Button_rename.setEnabled(false);
                            Button_move.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                            Button_move.setImageResource(R.drawable.move_file_disable);
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"File name not valid",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "ABCActivity");

        }
    }
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        try {
            thread_LoopEmergenza.KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //*************************************************************************************************
    //On click del button info
    //*************************************************************************************************
    public void on_click_Button_Info(View view) throws IOException {
        /*
        Intent intent_par = new Intent(getApplicationContext(), PopUpPdf.class);
        intent_par.putExtra("FileName", "pagina_files.pdf");
        startActivity(intent_par);
        */

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
    //On click del button Exit
    //*************************************************************************************************
    public void onClick_Button_exit(View view) throws IOException {

     finish();
    }

class CustomMyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];

        CustomMyAdapter(Context c, String text[]) {
            super(c, R.layout.filelist_row, R.id.title, text);
            this.context = c;
            this.rTitle = text;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.filelist_row, parent, false);
            ImageView image = row.findViewById(R.id.image);
            TextView title = row.findViewById(R.id.title);

            title.setText(rTitle[position]);

            if(rTitle[position].endsWith("/"))
            {
                image.setImageResource(R.drawable.folder_icon);
            }else{
                image.setImageResource(R.drawable.file_icon);
            }

            return row;
        }
    }
}

