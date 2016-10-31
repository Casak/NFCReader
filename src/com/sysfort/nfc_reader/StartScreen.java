package com.sysfort.nfc_reader;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.sysfort.nfc_reader.R;



public class StartScreen extends Activity {
	
	
	
	private long startTime = 1*01 * 1000;
	private final long interval = 1 * 1000;
	long s1=1*01 * 1000;
	 
	 private CountDownTimer countDownTimer;
	 private boolean timerHasStarted = false;
	 SharedPreferences sharedpreferences;
	 public static final String MyPREFERENCES = "MyTAGS" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        
        startTime=s1;
		 countDownTimer = new MyCountDownTimer(startTime, interval);	
		
		  timerHasStarted = true;
		countDownTimer.start();
        
        
    }

   
    
    public class MyCountDownTimer extends CountDownTimer {
		  public MyCountDownTimer(long startTime, long interval) {
			   super(startTime, interval);
			   Log.d("start timer","interval"+interval);
			  }
			 
			  @Override
			  public void onFinish() {
			  
	        	
				  Log.d("start timer","Finish"+interval);
				  SimpleDateFormat dfDate  = new SimpleDateFormat("dd/MM/yyyy");
			        java.util.Date d = null;
			        java.util.Date d1 = null;
			        Calendar cal = Calendar.getInstance();
			      
          // startActivity(new Intent(getApplicationContext(), MainActivity.class));
        		 
        		    
        		    SharedPreferences.Editor editor = sharedpreferences.edit();
        	        
        	        editor.putString("flag", "1");
        	       
        	      
        	        editor.commit();
           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      		
      		startActivity(intent);
        		 
          	finish();
        	  
	        	
			  }
			 
			  @Override
			  public void onTick(long millisUntilFinished) {
				  
				 
				  //Clock.setText("" + millisUntilFinished / 1000);
				  int seconds = (int) (millisUntilFinished / 1000) % 60 ;
				  int minutes = (int) ((millisUntilFinished / (1000*60)) % 60);
				  int hours   = (int) ((millisUntilFinished / (1000*60*60)) % 24);
				  Log.d("start timer","interval"+seconds);
				  Log.d("start",""+millisUntilFinished);
				 
				//  Log.d(Tag,"seconds"+seconds);
				//  Log.d(Tag,"minutes"+minutes);
				//  Log.d(Tag,"hours"+hours);
				  s1=millisUntilFinished;
				  
			  }
			 
			 
			 }
}
