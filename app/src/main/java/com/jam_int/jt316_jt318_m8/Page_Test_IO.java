package com.jam_int.jt316_jt318_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Page_Test_IO extends Activity {
	TableLayout stk_out;
	TableLayout stk_input;
	TableRow tbrow;
	ShoppingList sl;
	Activity ctx = null;
	int numero_out = 32;
	int numero_in = 32;
	Thread t1;
	Double[] Stato_uscite = new Double[numero_out];
	Double[] Stato_ingressi = new Double[numero_in];
	ArrayList<Button> Lista_button_out = new ArrayList<Button>();
	ArrayList<Button> Lista_button_input = new ArrayList<Button>();
	Boolean out_Background_Pausa = false;
	Boolean Thread_Running = false,  StopThread = false, Debug_mode = false, first_cycle = true;
    MultiCmdItem[] multi_out;
    MultiCmdItem[] multi_in;
	MultiCmdItem MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb312_AzzOK,MultiCmd_Vb88,MultiCmd_Vb165,MultiCmd_Vb170,MultiCmd_Vb363_asse_elettrico,
			MultiCmd_Vb323_PID_crochet,MultiCmd_Vn3804_pagina_touch;
	MultiCmdItem[] mci_array_read_all;
	Integer Timeout_emergenza = 5,timeout_counter;
	String Machine_model = "";
	MainActivity.Mci_write Mci_write_Vb165,Mci_write_Vb170,Mci_output;
	Handler HandlerDialog = new Handler();
	Context context = this;

	private static final int REQUEST_CODE_TESTIO = 1010;
	final private static int RESULT_PAGE_EMG = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_io);
		ctx = this;


		LocalBroadcastManager.getInstance(this).registerReceiver(MessagePasswordReceiver, new IntentFilter("KeyDialog_password_ret"));

		this.getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera
		
		stk_out = (TableLayout) findViewById(R.id.TabletLayout_out);
		stk_input = (TableLayout) findViewById(R.id.TabletLayout_in);
		 
		ScrollView  ScrollBar_output = (ScrollView) findViewById(R.id.scrollView_out);
		ScrollBar_output.setScrollbarFadingEnabled(false);	//faccio sempre vedere lo scrollbar verticale
		
		ScrollView  ScrollBar_input = (ScrollView) findViewById(R.id.scrollView_in);
		ScrollBar_input.setScrollbarFadingEnabled(false);	//faccio sempre vedere lo scrollbar verticale

		TextView TextView_info = (TextView)findViewById(R.id.textView_info);
		TextView_info.setText(R.string.Press_emergency);
		TextView_info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		TextView TextView_output = (TextView)findViewById(R.id.textView_output);



		//se tengo premuto a lungo la scritta output faccio partire il keylog password per sganciare l'asse elettrico
		TextView_output.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View arg0) {
					KeyDialog.Lancia_KeyDialogo(null, Page_Test_IO.this, null, 99999d, 0d, false, false, 0d,true,"KeyDialog_password_ret",false);


				return true;    // <- set to true
			}
		});


		try {
			Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());

			init_Out(numero_out);
			init_Input(numero_in);
		} catch (IOException e) {
			e.printStackTrace();
		}




		if(sl !=null ) {
			sl.Clear("Io");
		}else
		{
			sl = SocketHandler.getSocket();
			sl.Clear("Io");
		}

		ctx = this;

		MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
		MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
		MultiCmd_Vb312_AzzOK =  sl.Add("Io", 1, MultiCmdItem.dtVB, 312, MultiCmdItem.dpNONE);
		MultiCmd_Vb88 = sl.Add("Io", 1, MultiCmdItem.dtVB, 88, MultiCmdItem.dpNONE);
		MultiCmd_Vb165 = sl.Add("Io", 1, MultiCmdItem.dtVB, 165, MultiCmdItem.dpNONE);
		MultiCmd_Vb170 = sl.Add("Io", 1, MultiCmdItem.dtVB, 170, MultiCmdItem.dpNONE);
		MultiCmd_Vb363_asse_elettrico = sl.Add("Io", 1, MultiCmdItem.dtVB, 363, MultiCmdItem.dpNONE);
		MultiCmd_Vb323_PID_crochet= sl.Add("Io", 1, MultiCmdItem.dtVB, 323, MultiCmdItem.dpNONE);
		MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);

		mci_array_read_all = new MultiCmdItem[]{MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb312_AzzOK,MultiCmd_Vb88};



		multi_out = new MultiCmdItem[numero_out];

		for (int i = 0; i < numero_out; i = i + 1) {
			MultiCmdItem mci_stato_out = sl.Add("Io", 1, MultiCmdItem.dtDO, i+1, MultiCmdItem.dpNONE);
			multi_out[i] = mci_stato_out;

		}

		multi_in = new MultiCmdItem[numero_in];
		for (int i = 0; i < numero_out; i = i + 1) {
			MultiCmdItem mci_stato_input = sl.Add("Io", 1, MultiCmdItem.dtDI, i+1, MultiCmdItem.dpNONE);
			multi_in[i]=mci_stato_input;

		}


		Mci_write_Vb165 = new MainActivity.Mci_write(); Mci_write_Vb165.mci = MultiCmd_Vb165; Mci_write_Vb165.write_flag = false;
		Mci_write_Vb170 = new MainActivity.Mci_write(); Mci_write_Vb170.mci = MultiCmd_Vb170; Mci_write_Vb170.write_flag = false;
		MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtDO,1, MultiCmdItem.dpNONE);	//instanzio una generica
		Mci_output = new MainActivity.Mci_write(); Mci_output.mci = mci; Mci_output.write_flag = false;	//instanzio una generica

		if (!Thread_Running ) {


			StopThread = false;
			MyAndroidThread_TestIO myTask = new MyAndroidThread_TestIO(Page_Test_IO.this);
			t1 = new Thread(myTask, "Main myTask");
			t1.start();
			Log.d("JAM TAG","Start Test_IO Thread");
		}

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

		if (Thread_Running)
			KillThread();


	}

	//*************************************************************************************************
	// BroadcastReceiver per prendere la risposta dal KeyDialog password
	//*************************************************************************************************
	private BroadcastReceiver MessagePasswordReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {


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




			//	SganciaAsseElettrico(Machine_model);
				CallHandlerDialog();
				//dialog_restart = true;







			}else if(val.equals(""))
			{}
			else
			{
				Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
			}


		}
	};
	//**************************************************************************************************
	//
	//**************************************************************************************************
	class MyAndroidThread_TestIO implements Runnable {
		Activity activity;

		public MyAndroidThread_TestIO(Activity activity) {
			this.activity = activity;
			timeout_counter = Timeout_emergenza;

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
						//MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
						//sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
						return;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "MyAndroidThread_TestIO catch", Toast.LENGTH_SHORT).show();
				}

				if (sl.IsConnected()) {

					sl.ReadItems(mci_array_read_all);


					if(first_cycle){
						MultiCmd_Vn3804_pagina_touch.setValue(1005.0d);
						sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
						first_cycle = false;

					}




					if(!out_Background_Pausa) {
                        sl.WriteQueued();


                        sl.ReadItems(multi_out);

                        if (sl.getReturnCode() == 0) {
							for (int i = 0; i < numero_out; i = i + 1) {
								MultiCmdItem elem = multi_out[i];
                                Double stato = (Double) elem.getValue();
                                Stato_uscite[i] = stato;
                            }
                        }
                        sl.ReadItems(multi_in);
                        if (sl.getReturnCode() == 0) {
                            for (int i = 0; i < numero_in; i = i + 1) {
                                MultiCmdItem elem = multi_in[i];
                                Double stato_input = (Double) elem.getValue();
                                Stato_ingressi[i] = stato_input;
                            }
                        }




                    }


					if (Mci_write_Vb165.write_flag == true) {
						Mci_write_Vb165.mci.setValue(Mci_write_Vb165.valore);
						sl.WriteItem(Mci_write_Vb165.mci);
						Mci_write_Vb165.write_flag = false;

					}
					if (Mci_write_Vb170.write_flag == true) {
						Mci_write_Vb170.mci.setValue(Mci_write_Vb170.valore);
						sl.WriteItem(Mci_write_Vb170.mci);
						Mci_write_Vb170.write_flag = false;

					}
					if (Mci_output.write_flag == true) {
						Mci_output.mci.setValue(Mci_output.valore);
						sl.WriteItem(Mci_output.mci);
						Mci_output.write_flag = false;
						out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite

					}



					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//Input

							Emergenza();

							for (int i = 0; i < Lista_button_input.size(); i=i+1) {
								Button button_stao_input;
								button_stao_input= Lista_button_input.get(i);

								try{
									if (Stato_ingressi[i] == 1) {
										button_stao_input.setBackgroundColor(Color.RED);
									} else {
										button_stao_input.setBackgroundColor(Color.GRAY);
									}
								}
								catch(ArrayIndexOutOfBoundsException exception) {

								}

							}


							//output
							for (int i = 0; i < Lista_button_out.size(); i=i+1) {
								Button button_out;
								button_out= Lista_button_out.get(i);

								try{
									if (Stato_uscite[i] == 1) {
										button_out.setBackgroundColor(Color.RED);
									} else {
										button_out.setBackgroundColor(Color.GREEN);
									}
								}
								catch(ArrayIndexOutOfBoundsException exception) {

								}

							}



						}

					});


				} else {
					first_cycle = true;
					sl.Connect();


				}
			}
		}
	}

	private void SganciaAsseElettrico(String machine_model) {

		//sgancio asse elettrico per azzerare analogica del crochet

		MultiCmd_Vb363_asse_elettrico.setValue(0.0d);
		sl.WriteItem(MultiCmd_Vb363_asse_elettrico);

		//sgancio PID crochet

		MultiCmd_Vb323_PID_crochet.setValue(1.0d);
		sl.WriteItem(MultiCmd_Vb323_PID_crochet);

		MultiCmd_Vb88.setValue(1.0d);
		sl.WriteItem(MultiCmd_Vb88);
		switch (machine_model) {

			case "JT862":
				MultiCmd_Vb170.setValue(1.0d);
				sl.WriteItem(MultiCmd_Vb170);
				break;
			case "JT882Master":
			case "JT882Slave":
				MultiCmd_Vb165.setValue(1.0d);
				sl.WriteItem(MultiCmd_Vb165);
				break;
		}

	}

	//*************************************************************************************************
	// inizializzo pulsanti out
	//*************************************************************************************************
	private void
	init_Out(int numero_out) throws IOException {
		String[] Descrizioni = new String[]{};

		switch (Machine_model) {
			case "JT350M":
				Descrizioni = getResources().getStringArray(R.array.output_JT350);
				break;
			case "JT318M":
			case "JT316M":
			case "JT318M_1000x800":
				Descrizioni = getResources().getStringArray(R.array.output_JT318M);
				break;

			default:
				break;

		}



		for (int i = 1; i <= numero_out; i=i+3) {
			tbrow = new TableRow(this);		//creo il tableRow 
			tbrow.setPadding(0, 0, 0, 7);	// spazio tra una riga e l'altra del tableRow
			
			TableRow.LayoutParams p = new TableRow.LayoutParams();
			p.rightMargin = 20; // imposta lo spazio tra una colonna di button e l'altra
		
		//Prima colonna		
			Button btnTag = new Button(this);
			btnTag.setLayoutParams(p);
            btnTag.setBackgroundColor(Color.GREEN);
            btnTag.setGravity(Gravity.LEFT);
            //btnTag.setText("Output" + i);
			btnTag.setText("Out"+i+": " +Descrizioni[i]);

            btnTag.setId(i);
            btnTag.setWidth(150);		//larghezza button
            btnTag.setHeight(15);		//altezza button
            btnTag.setPadding(15, 5, 0, 0);	//posizione scritta "outputxx" all'interno del button
			btnTag.setTextSize(10);
            btnTag.setOnClickListener(buttonClickListener);

			Lista_button_out.add(btnTag);

            tbrow.addView(btnTag);	 //aggiungo la riga al TableRow

        
      //Seconda colonna
			if(i+1 <= numero_out) {
				Button btnTag1 = new Button(this);
				btnTag1.setLayoutParams(p);
				btnTag1.setBackgroundColor(Color.GREEN);
				btnTag1.setGravity(Gravity.LEFT);
				//btnTag1.setText("Output" + (i + 1));
				btnTag1.setText("Out"+(i+1)+": " +Descrizioni[i + 1]);
				btnTag1.setId(i + 1);
				btnTag1.setWidth(150);
				btnTag1.setHeight(15);
				btnTag1.setPadding(15, 5, 0, 0);
				btnTag1.setTextSize(10);
				btnTag1.setOnClickListener(buttonClickListener);
				Lista_button_out.add(btnTag1);

				tbrow.addView(btnTag1);    //aggiungo la riga al TableRow
			}
       //Terza colonna
			if(i+2 <= numero_out) {
				Button btnTag2 = new Button(this);
				btnTag2.setLayoutParams(p);
				btnTag2.setBackgroundColor(Color.GREEN);
				btnTag2.setGravity(Gravity.LEFT);
				//btnTag2.setText("Output" + (i + 2));
				btnTag2.setText("Out"+(i+2)+": " + Descrizioni[i + 2]);
				btnTag2.setId(i + 2);
				btnTag2.setWidth(150);
				btnTag2.setHeight(15);
				btnTag2.setPadding(15, 5, 0, 0);
				btnTag2.setTextSize(10);
				btnTag2.setOnClickListener(buttonClickListener);
				Lista_button_out.add(btnTag2);

				tbrow.addView(btnTag2);    //aggiungo la riga al TableRow

			}
			stk_out.addView(tbrow);    //aggiungo il tablerow al tableLayout
		}




//*************************************************************************************************
// inizializzo test input
//*************************************************************************************************
	}
	private void init_Input(int numero_in)
	{
		String[] Descrizioni = new String[]{};



		switch (Machine_model) {
			case "JT350M":
				Descrizioni = getResources().getStringArray(R.array.input_JT350);
				break;
			case "JT318M":
			case "JT316M":
			case "JT318M_1000x800":
				Descrizioni = getResources().getStringArray(R.array.input_JT318M);
				break;

		}




		for (int i = 1; i <= numero_in; i=i+3) {
			tbrow = new TableRow(this);        //creo il tableRow
			tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

			TableRow.LayoutParams p = new TableRow.LayoutParams();
			p.rightMargin = 20; // imposta lo spazio tra una colonna text e button e l'altra

			//Prima colonna
			Button btnTag = new Button(this);
			btnTag.setLayoutParams(p);
			btnTag.setBackgroundColor(Color.GRAY);
			btnTag.setGravity(Gravity.LEFT);
			btnTag.setText("In"+i+": " +Descrizioni[i]);

			btnTag.setId(i);
			btnTag.setWidth(150);		//larghezza button
			btnTag.setHeight(15);		//altezza button
			btnTag.setPadding(15, 5, 0, 0);	//posizione scritta "outputxx" all'interno del button
			btnTag.setTextSize(10);
			Lista_button_input.add(btnTag);



			tbrow.addView(btnTag);	 //aggiungo la riga al TableRow


			//Seconda colonna
			if(i+1 <= numero_in) {
				Button btnTag1 = new Button(this);
				btnTag1.setLayoutParams(p);
				btnTag1.setBackgroundColor(Color.GRAY);
				btnTag1.setGravity(Gravity.LEFT);
				btnTag1.setText("In"+(i+1)+": " +Descrizioni[i + 1]);
				btnTag1.setId(i + 1);
				btnTag1.setWidth(150);
				btnTag1.setHeight(15);
				btnTag1.setPadding(15, 5, 0, 0);
				btnTag1.setTextSize(10);
				Lista_button_input.add(btnTag1);

				tbrow.addView(btnTag1);    //aggiungo la riga al TableRow
			}
			//Terza colonna
			if(i+2 <= numero_in) {
				Button btnTag2 = new Button(this);
				btnTag2.setLayoutParams(p);
				btnTag2.setBackgroundColor(Color.GRAY);
				btnTag2.setGravity(Gravity.LEFT);
				btnTag2.setText("In"+(i+2)+": " + Descrizioni[i + 2]);
				btnTag2.setId(i + 2);
				btnTag2.setWidth(150);
				btnTag2.setHeight(15);
				btnTag2.setPadding(15, 5, 0, 0);
				btnTag2.setTextSize(10);
				Lista_button_input.add(btnTag2);

				tbrow.addView(btnTag2);    //aggiungo la riga al TableRow

			}


			stk_input.addView(tbrow);

		}
	}



	//*************************************************************************************************
	// ritorno da pagina x
	//*************************************************************************************************
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode ==REQUEST_CODE_TESTIO ) {
			if (resultCode == RESULT_OK) {
			
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}	
	//*************************************************************************************************
	// onclick dei buttom output
	//*************************************************************************************************
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
		public void onClick(View v) {

			out_Background_Pausa = true; //metto in pausa la lettura dello stato delle uscite

		   Button b = (Button)v;
		   int button_id = b.getId();			  
		   ColorDrawable buttonColor = (ColorDrawable) b.getBackground();
		   int colorId = buttonColor.getColor();
		   if (colorId != Color.RED) {

			   MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtDO,button_id, MultiCmdItem.dpNONE);
			   Mci_output = new MainActivity.Mci_write(); Mci_output.mci = mci; Mci_output.write_flag = true;
			   Mci_output.valore = 1.0d;
			   /*
			   MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVB,4100+button_id, MultiCmdItem.dpNONE);
			   Mci_output = new MainActivity.Mci_write(); Mci_output.mci = mci; Mci_output.write_flag = true;
			   Mci_output.valore = 1.0d;
*/

			   
		   }
		   else
		   {
			   MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtDO,button_id, MultiCmdItem.dpNONE);
			   Mci_output = new MainActivity.Mci_write(); Mci_output.mci = mci; Mci_output.write_flag = true;
			   Mci_output.valore = 0.0d;
			   /*
			   MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVB,4100+button_id, MultiCmdItem.dpNONE);
			   Mci_output = new MainActivity.Mci_write(); Mci_output.mci = mci; Mci_output.write_flag = true;
			   Mci_output.valore = 0.0d;
*/

		   }


        }
    };


		@Override
	public void onBackPressed() {
		super.onBackPressed();

		KillThread();

		finish();

	}

	private void CallHandlerDialog() {

		HandlerDialog.post(new Runnable() {
			@Override
			public void run() {


				//if(dialog_restart){
					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

					// Setting Dialog Title
					alertDialog.setTitle("Restart machine");

					alertDialog.setMessage("Gauntry axis are off, restart machine!");


					alertDialog.show();
				//	dialog_restart = false;
				//}

			}
		});
	}

	//*********************************************************************************************
	//Emergenza
	//*********************************************************************************************
	private void Emergenza() {

		//if((Double)MultiCmd_tasto_verde.getValue()==0.0d || (Double)MultiCmd_CH1_in_emergenza.getValue()==1.0d)
		if((Double)MultiCmd_tasto_verde.getValue()==0.0d)
		{
			// StopThread = true;
			KillThread();
			Intent intent_emergenza = new Intent(getApplicationContext(),Emergency_page.class);

			startActivity(intent_emergenza);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

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
	//*************************************************************************************************
	// KillThread
	//*************************************************************************************************
	private void KillThread() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(MessagePasswordReceiver);
		StopThread = true;
		try {
			t1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.d("JAM TAG","End Test_IO Thread");

	}

}


