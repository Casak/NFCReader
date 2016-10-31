package com.sysfort.nfc_reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfort.nfc_reader.R;



public class MainActivity extends Activity {
	
	String setDate="30/05/2016";
	
	boolean ispause=false;
	
	private long startTime = 1*01 * 1000;
	private final long interval = 1 * 1000;
	long s1=1*01 * 1000;
	File csvfile;
	Button btnDone;
	Dialog dialog;
	  EditText edtSearch,edtNO;
	 private CountDownTimer countDownTimer;
	 private boolean timerHasStarted = false;
	 public static final String MyPREFERENCES = "MyTAGS" ;
	 TextView txtBillLanding,txtUnlodingReciept;
	 SharedPreferences sharedpreferences;
	 MyApplication myApp ;
	 private boolean isShown = false;
	// String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        MyApplication myApp ;
        Log.d("Main Activity ===","onCreate");
  // 	 Log.d("Main Activity ===","onResume1"+isApplicationSentToBackground(getApplicationContext()));
 	
  // 	((MyApplication)this.getApplication()).startActivityTransitionTimer();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String extr = Environment.getExternalStorageDirectory().toString();
		  File newFolder = new File(extr + "/NFC");
	        if (!newFolder.exists()) {
	        	//Log.d("TAG-->newFolder.exists()",""+ newFolder.exists());
	        	newFolder.mkdir();
	        }
	        
//	        csvfile = new File(extr + "/NFC/nfcread.txt");
//	        if (!csvfile.exists()) {
//	        	Log.d("TAG-->aFolder.exists()",""+ csvfile.exists());
//	        	csvfile.mkdir();
//	        }
	        
	        
	        txtBillLanding=(TextView)findViewById(R.id.txtBillLanding);
	        txtUnlodingReciept=(TextView)findViewById(R.id.txtwashoutReciept);
	    String    unique_id = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
	        
	    SharedPreferences.Editor editor = sharedpreferences.edit();
        
        editor.putString("unique_id", unique_id);
       
      
        editor.commit();
      //  Log.d("unique_id string fileName1",""+unique_id);
        
        
        
        
        String namests = sharedpreferences.getString("namests", null);
    	//Log.d(" ROOT string path1  ",""+namests);
   	 
   		txtBillLanding.setVisibility(View.GONE);
        txtUnlodingReciept.setVisibility(View.GONE);
   		
        //Ankita
        
//        if (sharedpreferences.contains("name")) {
//        	
//        	 Toast.makeText(MainActivity.this, "sharedpreferences  set "+sharedpreferences.getString("name", null),
//					   Toast.LENGTH_LONG).show();
//        	 txtBillLanding.setVisibility(View.VISIBLE);
//             txtUnlodingReciept.setVisibility(View.VISIBLE);
//        }
//        else
//        {
        	//showCustumDialog();
       // }
      
       
        
       
        
        
       
   	        
        
        
        
	       String fileName1 = "nfcread.csv";
	        String path1 = Environment.getExternalStorageDirectory()+"/NFC/"+fileName1;
	       // Log.d("ROOT string fileName1",""+fileName1);
	        Log.d(" ROOT string path1",""+path1);
	       
	        File FILE1 = new  File(""+path1);
	        Log.d(" FILE EXISTS FILE1",""+FILE1.exists());
	        if(!FILE1.exists())
	        {
	        try {
				FILE1.createNewFile();
				//writeTextFile("Front"+frontserialID+","FrontTime"+fonttime+","+backserialID+","+backtime+","+difference);
				writeTextFile("Name"+","+"EmployeeID"+","+"MobileDeviceID"+","+"FrontserialID"+","+"FrontScanDate"+","+"FrontScanTime"+","+"BackserialID"+","+"BackScanDate"+","+"BackScanTime"+","+"TimeDiffrence");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
//	        if(FILE1.exists()){
//	        	 Log.d("FILE ---- FOUND LOOKING ON LOCAL DRIVE ","No Exception= ");
//	          String splitBy = ",";
//	          String[] b = null;
//	          BufferedReader br = null;
//	  		try {
//	  			br = new BufferedReader(new FileReader(path1));
//	  		} catch (FileNotFoundException e3) {
//	  			// TODO Auto-generated catch block
//	  			e3.printStackTrace();
//	  		}
//	          String line;
//	  		try {
//	  			line = br.readLine();
//	  		} catch (IOException e1) {
//	  			// TODO Auto-generated catch block
//	  			e1.printStackTrace();
//	  		}
//	  		try {
//	  			while ((line = br.readLine()) !=null) {
//	  				b = line.split(splitBy);
//	  			    newSignalList.add((b[0]));
//	  			    newTempList.add((b[1]));
//	  			    
//	  			    temp1.add((b[2]));
//				    temp2.add((b[3]));
//				    temp3.add((b[4]));
//				    temp4.add((b[5]));
//	  			   //  System.out.println(b[0]);
//	  			    
//	  			     
//	  			}
//	  			
//	  			// Log.d("audioBuf size ","Exception1= "+signalDataArr.length);
//	  		} catch (IOException e2) {
//	  			// TODO Auto-generated catch block
//	  			e2.printStackTrace();
//	  		}
//	          try {
//	  			br.close();
//	  		} catch (IOException e1) {
//	  			// TODO Auto-generated catch block
//	  			e1.printStackTrace();
//	  		}
       
        
    }

    private void showCustumDialog() {
		// TODO Auto-generated method stub
    	
    	
	    dialog=new Dialog(MainActivity.this);
			dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.email_dialog);
			dialog.setCancelable(true);
		
		
		
		
		
		
			edtSearch=(EditText)dialog.findViewById(R.id.edtSearch);
			edtNO=(EditText)dialog.findViewById(R.id.edtNO);//	edtNO
	final Button	btnDialogOk=(Button) dialog.findViewById(R.id.btnDialogOk);

	  isShown = true;
		dialog.show();
		
		
		
		
//		String emailID=  CommonDb.midwest_getUserInfo(getApplicationContext(), "email", "userManager", 1);
//		
//		
//		 Log.d("emailID", "PDF Path:emailID " + emailID);
//
//		if((emailID.equalsIgnoreCase("0"))||(emailID.equalsIgnoreCase("null")))
//		  {
//	    
//		edtSearch.setText("");
//		
//		  }
//		else
//		{
//			edtSearch.setText(""+emailID);
//		}
	      //  System.out.println("*****JARRAY*****comments "+jDialogArray.length());
	   
	   
	       
	   
	   

	       
	        
	        

		  
		
		btnDialogOk.setOnClickListener(new OnClickListener() {
			
			 @Override
				public void onClick(View v) {
				
				 
				 if( edtSearch.getText().toString().trim().length() > 0 && edtNO.getText().toString().trim().length() > 0)
				 {
					 dialog.cancel();
						
					 SharedPreferences.Editor editor = sharedpreferences.edit();
					   editor.putString("namests", "1");
				        editor.putString("name", edtSearch.getText().toString());
				        
				        editor.putString("empid", edtNO.getText().toString());
						   
				        editor.commit();
				        
				        Log.d("Main Activity ===","btnDialogOk");
				        ispause=false;
				        
				        txtBillLanding.setVisibility(View.VISIBLE);
				        txtUnlodingReciept.setVisibility(View.VISIBLE);
				        isShown = false;
						
						
					}
//						
				
				 else
	      		{
					 
					 Toast.makeText(MainActivity.this, "Please enter your name ! ",
							   Toast.LENGTH_LONG).show();
	      		}
			 }
		});
	 	
		
	
				 
		
	}

	public void readtag(View v)
	{
		finish();
		Log.d( "onTriggerDefenceClick ","Om click");
		
		  
		 Intent intent = new Intent(this, TagViewer.class);
		  startActivity(intent);
		   
		
	}
    public void viewfile(View v)
	{
		//finish();
		Log.d( "onTriggerDefenceClick ","viewfile");
		Intent intent = new Intent(this, FileRead.class);
		 startActivity(intent);
		
	}
    public void sendfile(View v)
	{

		finish();
		Log.d( "onTriggerDefenceClick ","Om click");
		
		
		 String fileName1 = "nfcread.csv";
	        String path1 = Environment.getExternalStorageDirectory()+"/NFC/"+fileName1;
	        Log.d("ROOT string fileName1",""+fileName1);
	        Log.d(" ROOT string path1",""+path1);
	       
	        File FILE1 = new  File(""+path1);
		
		
		Uri path = Uri.fromFile(FILE1); 
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		// set the type to 'email'
		emailIntent .setType("vnd.android.cursor.dir/email");
		String to[] = {""};
		emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
		// the attachment
		emailIntent .putExtra(Intent.EXTRA_STREAM, path);
		// the mail subject
		emailIntent .putExtra(Intent.EXTRA_SUBJECT, "NFC Tags [Attached CSV File]");
		emailIntent .putExtra(Intent.EXTRA_TEXT, "Hi Attached CSV file \n \n \n  Thanks");
		startActivity(Intent.createChooser(emailIntent , "Send email..."));
			
		
		// Intent intent = new Intent(this, MidwestNotification.class);
		//  startActivity(intent);
		
	}
    public void aboutus(View v)
	{
		finish();
		Log.d( "onTriggerDefenceClick ","Om click");
		// Intent intent = new Intent(this, MidwestNotification.class);
		//  startActivity(intent);
		
	}
    public void back(View v)
	{
		finish();
		Log.d( "onTriggerDefenceClick ","Om click");
		// Intent intent = new Intent(this, MidwestNotification.class);
		//  startActivity(intent);
		
	}
    @Override
	public void onBackPressed()
	{
    	
    	 super.onBackPressed();  // optional depending on your needs
    	 ispause=true;
	     // code here to show dialog
		finish();
		
		
		
		
		Log.d( "BACK FINISH ","Om click");
//		Intent intent = new Intent(this,
//		        MidwestDocType.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
	//	getActivity().finish();
	    
	    
	}
    public static void writeTextFile(String data){
		try {
			//Log.d(key, data);
	//
			 Calendar c = Calendar.getInstance();
		        System.out.println("Current time => "+c.getTime());

		        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        String formattedDate = df.format(c.getTime());
		        // formattedDate have current date/time
		        String fileName1 = "nfcread.csv";
		        String path1 = Environment.getExternalStorageDirectory()+"/NFC/"+fileName1;
			//myFile.createNewFile();
		        
		        File FILE1 = new  File(""+path1);
			FileWriter fw = new FileWriter(FILE1,true);
			fw.write(data +"\r\n");//appends the string to the file
			fw.close();
	//
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("Custom Stack trace ", getStackTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("Custom Stack trace ", getStackTrace(e));
		}
	}
    public static String getStackTrace(Throwable aThrowable) {
		aThrowable.printStackTrace();
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
    
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//    	// TODO Auto-generated method stub
//    	super.onWindowFocusChanged(hasFocus);
//    	
//    	
//    	if(ispause)
//    	{
//    		ispause=false;
//    		
//    		 Log.d("Main Activity ===","onWindowFocusChanged");
//    		showCustumDialog();
//        	
//        	
//    		
//    	}
//    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	
    	
    	 ((MyApplication)this.getApplication()).startActivityTransitionTimer();
    	 SharedPreferences.Editor editor = sharedpreferences.edit();
	        
	        editor.putString("flag", "1");
	       
	      
	        editor.commit();
	        
    	 
    	// Log.d("=========>>>>>>MyApplication  Activity ===","1 PAUSE");
    	
    	
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	
   	 //Log.d("Main Activity ===","onResume3 "+isApplicationSentToBackground(getApplicationContext()));
    	
    	
    	MyApplication myApp = (MyApplication)this.getApplication();
        if (myApp.wasInBackground)
        {
            //Do specific came-here-from-background code
        	
        	 Log.d("=========>>>>>>MyApplication  Activity ===","IF");
        	//showCustumDialog();
        }
        else
        {
        	 Log.d("=========>>>>>>MyApplication  Activity ===","ELSE");
        	// showCustumDialog();
        	 
        	
        	
        	
        }
        String flag = sharedpreferences.getString("flag", null);
        Log.d("==flag=======>>>>>>MyApplication  isShown ===","flag"+flag);
    	//intent.putExtra("screen", "tagviewer");
  	// Log.d("=========>>>>>>tag  Activity ===",""+tag);
       if(flag.equalsIgnoreCase("0"))
       {
    	 //  Log.d("Main Activity ===","tagviewer"+extras.getString("screen"));
    	   
    	   
    	
    		   //Do Nothing
        	   txtBillLanding.setVisibility(View.VISIBLE);
		        txtUnlodingReciept.setVisibility(View.VISIBLE);
		        
		       
		    //    Log.d("Main Activity ===","tagviewer"+extras.getString("screen"));
		     
		      
         
    	   }
    	 
       else   if(flag.equalsIgnoreCase("1"))
       {
    	   
    	   
    	 //  Log.d("Main Activity ===","screen ");
    	   
    	
    		   Log.d("==ELSE=======>>>>>>MyApplication  isShown ===","isShown"+isShown);
    		   
    		   if(!isShown)
               {
    			   if (dialog != null) {
    			   dialog.dismiss();
    			   showCustumDialog();
    			   }
    			   else
    			   {
    		   showCustumDialog();
    			   }
               }
    	 //  }
       }
    	
        myApp.stopActivityTransitionTimer();
    	
    }
    
//    public static boolean isApplicationSentToBackground(final Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//          ComponentName topActivity = tasks.get(0).topActivity;
//          if (!topActivity.getPackageName().equals(context.getPackageName())) {
//            return true;
//          }
//        }
//
//        return false;
//      }
//   
}
