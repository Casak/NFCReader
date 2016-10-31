/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright (C) 2011 Adam Nybäck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sysfort.nfc_reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sysfort.nfc_reader.R;
import com.sysfort.nfc_reader.record.ParsedNdefRecord;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device just discovered.
 */
public class TagViewer extends Activity implements TextToSpeech.OnInitListener {

    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
    private LinearLayout mTagContent;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    
    Vibrator vibrator;
    TextToSpeech tts;
private Button btnBack;
Button save;
String tagId,  tagID1;
    private AlertDialog mDialog;
    private TextView tag_viewer_text;
    int sts=0;
    public static final String MyPREFERENCES = "MyTAGS" ;
    ImageView imgBus;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_viewer);
        mTagContent = (LinearLayout) findViewById(R.id.list);
        resolveIntent(getIntent());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        
        btnBack=(Button)findViewById(R.id.btnBack);
        save=(Button)findViewById(R.id.BtnSave);
        save.setVisibility(View.GONE);
        imgBus=(ImageView)findViewById(R.id.imageView);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
       //save=(TextView)findViewById(R.id.btnsave);
        sts=0;
        
        tag_viewer_text=(TextView)findViewById(R.id.tag_viewer_text);
        
        
        tts = new TextToSpeech(this, this);
        
        
     
        //btnBack.setVisibility(View.VISIBLE);
        
        if(sts==0)
        {
        tag_viewer_text.setText("Scan Front tag");
        //speakOut1();
      //  save.setVisibility(View.VISIBLE);
        
        imgBus.setImageResource(R.drawable.scanfront);
        }
        
        else if(sts==1)
        {
        tag_viewer_text.setText("Scan  Back tag");
        imgBus.setImageResource(R.drawable.scanback);
        save.setVisibility(View.VISIBLE);
        
        }
        
//        String strbackserialID = sharedpreferences.getString("backserialID", null);
//        Log.d( "strbackserialID ","strbackserialID"+strbackserialID);
//        if(strbackserialID != null && strbackserialID.equalsIgnoreCase(tagId))
//        {
//        	 Log.d( "strbackserialID if ","strbackserialID"+strbackserialID);
//        	
//        }
//        else
//        {
//        	 Log.d( "strbackserialID else  ","strbackserialID"+strbackserialID);
//        }
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        
//        editor.putString("frontserialID", "0");
//        editor.putString("backserialID", "0");
//    
//      
//        editor.commit();
        
        
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true) });
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }
    

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    
                 //  tagId="111";
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
                
             
            }
            // Setup the views
            
           
            vibrator.vibrate(1000);
            
            buildTagViews(msgs);
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        
        Log.d( "dumpTagData ","p"+p);
        Log.d( "dumpTagData HEX ","p"+getHex(id));
        Log.d( "dumpTagData DEC","p"+getDec(id));
        Log.d( "dumpTagData ","p"+getReversed(id));
        tagId=getHex(id);
        tagID1=Long.toString(getDec(id));
       // writeTextFile("HEX_"+tagId,"----DEC_"+tagID1);
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        //sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
      //  sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                case MifareClassic.TYPE_CLASSIC:
                    type = "Classic";
                    break;
                case MifareClassic.TYPE_PLUS:
                    type = "Plus";
                    break;
                case MifareClassic.TYPE_PRO:
                    type = "Pro";
                    break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                case MifareUltralight.TYPE_ULTRALIGHT:
                    type = "Ultralight";
                    break;
                case MifareUltralight.TYPE_ULTRALIGHT_C:
                    type = "Ultralight C";
                    break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;

        // Parse the first message in the list
        // Build views for all of the sub records
        Date now = new Date();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            //TextView timeView = new TextView(this);
            
            Calendar c = Calendar.getInstance();
	        System.out.println("Current time => "+c.getTime());

	       // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");//MM/dd/yyyy
	        String formattedDate = df.format(c.getTime());
	        SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
	        String formattedTime = df1.format(c.getTime());
	        
	        Log.d( "STS--- ","Om click"+sts);
          //  timeView.setText(TIME_FORMAT.format(now)+"----TAGID----"+tagId);
            if(sts==0)
            {
            	
            	String strbackserialID = sharedpreferences.getString("backserialID", null);
            	 if(strbackserialID != null && strbackserialID.equalsIgnoreCase(tagId))
            	//if(strbackserialID.equalsIgnoreCase(tagId))
            	{
            		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Please scan another NFC TAG !")
            		       .setCancelable(false)
            		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		           public void onClick(DialogInterface dialog, int id) {
            		                //do things
            		           }
            		       });
            		AlertDialog alert = builder.create();
            		alert.show();

            	}
            	else{
            	
            	   Log.d( " if STS--- ","Om click"+sts);
SharedPreferences.Editor editor = sharedpreferences.edit();
            
            editor.putString("frontserialID", tagId);
            editor.putString("frontdate", formattedDate);
            editor.putString("fronttime", formattedTime);
          
            editor.commit();
            
            sts=1;
            imgBus.setImageResource(R.drawable.scanback);
            
            tag_viewer_text.setText("Scan  Back tag");
          //  save.setVisibility(View.VISIBLE);
            
           speakOut();
            	}
            }
            
            else  if(sts==1)
            {
            	String strfrontserialID = sharedpreferences.getString("frontserialID", null);
            	 if(strfrontserialID.equalsIgnoreCase(tagId))
            		//if(strbackserialID.equalsIgnoreCase(tagId))
            	// if(strfrontserialID != null && strfrontserialID.equalsIgnoreCase(tagId))
            	{
            		AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Please scan another NFC TAG !")
            		       .setCancelable(false)
            		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            		           public void onClick(DialogInterface dialog, int id) {
            		                //do things
            		           }
            		       });
            		AlertDialog alert = builder.create();
            		alert.show();

            	}
            	else{
            	
            	
            	Log.d( " else if STS--- ","Om click"+sts);
SharedPreferences.Editor editor = sharedpreferences.edit();
            
            editor.putString("backserialID", tagId);
            editor.putString("backdate", formattedDate);
            editor.putString("backtime", formattedTime);
          
            editor.commit();
            sts=0;
            tag_viewer_text.setText("Scan Front tag");
          save.setVisibility(View.GONE);
          imgBus.setImageResource(R.drawable.scanfront);
           speakOut1();
           
           
       	String name = sharedpreferences.getString("name", null);
    	String empid = sharedpreferences.getString("empid", null);
    	String deviceId = sharedpreferences.getString("unique_id", null);
           
       	String frontserialID = sharedpreferences.getString("frontserialID", null);
       	String frontdate = sharedpreferences.getString("frontdate", null);
       	String fronttime = sharedpreferences.getString("fronttime", null);
       	
       	String backserialID = sharedpreferences.getString("backserialID", null);
       	String backdate = sharedpreferences.getString("backdate", null);
       	String backtime = sharedpreferences.getString("backtime", null);
       	Log.d( " frontserialID  if STS--- ","Om click"+frontserialID);
       	Log.d( " fonttime if STS--- ","Om click"+fronttime);
       	Log.d( " backtime if STS--- ","Om click"+backtime);
       	Log.d( " else if STS--- ","Om click"+sts);
       	
       	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
       	Date startDate = null;
   		try {
   			startDate = simpleDateFormat.parse(""+fronttime);
   		} catch (ParseException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
       	Date endDate = null;
   		try {
   			endDate = simpleDateFormat.parse(""+backtime);
   		} catch (ParseException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   	//	long difference = startDate.getTime() - endDate.getTime(); 
       	long difference = endDate.getTime() - startDate.getTime(); 
       	long diffInSec = TimeUnit.MILLISECONDS.toSeconds(difference);
       	int in = (int) diffInSec;
       	if(in<0)
       	{
       	in = Math.abs(in);
       	Log.d( "if Difference "," difference"+in);
       	}
       	
       	//Log.d( "else Difference "," difference"+i);
       	//Log.d( "Difference "," difference"+diffInSec);
       	
       	Log.d( "Sec ","Om click"+i);
   		
   		
       	
       	//int seconds = (int) (difference / 1000) % 60 ;
       	writeTextFile(""+name+","+empid+","+deviceId+","+frontserialID+","+frontdate+","+fronttime+","+backserialID+","+backdate+","+backtime+","+in+" sec");
       	
       //	writeTextFile(""+frontserialID+","+fonttime+","+backserialID+","+backtime);
       	save.setVisibility(View.GONE);
       	
    //   	imgBus.setImageResource(R.drawable.scanback);
       	
      		finish();
      		Log.d( "onTriggerDefenceClick ","Om click");
      		Intent intent = new Intent(this, TagViewer.class);
      	  startActivity(intent);
           
           
            	}
            }
            
            
           // writeTextFile("HEX_"+tagId,"----DEC_"+tagID1);
           // content.addView(timeView, 0);
            ParsedNdefRecord record = records.get(i);
            content.addView(record.getView(this, inflater, content, i), 1 + i);
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_clear:
              menuMainClearClick();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void menuMainClearClick() {
        for (int i = mTagContent.getChildCount() -1; i >= 0 ; i--) {
            View view = mTagContent.getChildAt(i);
            if (view.getId() != R.id.tag_viewer_text) {
                mTagContent.removeViewAt(i);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }
    
    public void back(View v)
   	{
   		finish();

 	   if(tts!=null){
	           
	            
 		  tts.stop();
 		 tts.shutdown();
	        }
   		Log.d( "onTriggerDefenceClick ","Om click");
   		Intent intent = new Intent(this, MainActivity.class);
   		intent.putExtra("screen", "tagviewer");
   	  startActivity(intent);
   		
   	}
    
    public void scan(View v)
   	{
    	sts=1;
        
    	
    	
    	
    	
    	
    	
    	
        // tag_viewer_text.setText("Scanning Back tag");
    	//save.setVisibility(View.GONE);
         
        
         
   		
   	}
    
    public void onSaveClick(View v)
   	{
    	  sts=0;
    	  
    	
    	  
//    	  
//    	String frontserialID = sharedpreferences.getString("frontserialID", null);
//    	String frontdate = sharedpreferences.getString("frontdate", null);
//    	String fronttime = sharedpreferences.getString("fronttime", null);
//    	
//    	String backserialID = sharedpreferences.getString("backserialID", null);
//    	String backdate = sharedpreferences.getString("backdate", null);
//    	String backtime = sharedpreferences.getString("backtime", null);
//    	Log.d( " frontserialID  if STS--- ","Om click"+frontserialID);
//    	Log.d( " fonttime if STS--- ","Om click"+fronttime);
//    	Log.d( " backtime if STS--- ","Om click"+backtime);
//    	Log.d( " else if STS--- ","Om click"+sts);
//    	
//    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//    	Date startDate = null;
//		try {
//			startDate = simpleDateFormat.parse(""+fronttime);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	Date endDate = null;
//		try {
//			endDate = simpleDateFormat.parse(""+backtime);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	//	long difference = startDate.getTime() - endDate.getTime(); 
//    	long difference = endDate.getTime() - startDate.getTime(); 
//    	long diffInSec = TimeUnit.MILLISECONDS.toSeconds(difference);
//    	int i = (int) diffInSec;
//    	if(i<0)
//    	{
//    	i = Math.abs(i);
//    	Log.d( "if Difference "," difference"+i);
//    	}
//    	
//    	//Log.d( "else Difference "," difference"+i);
//    	//Log.d( "Difference "," difference"+diffInSec);
//    	
//    	Log.d( "Sec ","Om click"+i);
//		
//		
//    	
//    	//int seconds = (int) (difference / 1000) % 60 ;
//    	writeTextFile(""+frontserialID+","+frontdate+","+fronttime+","+backserialID+","+backdate+","+backtime+","+i+" sec");
//    	
//    //	writeTextFile(""+frontserialID+","+fonttime+","+backserialID+","+backtime);
//    	save.setVisibility(View.GONE);
//    	
// //   	imgBus.setImageResource(R.drawable.scanback);
//    	
//   		finish();
//   		Log.d( "onTriggerDefenceClick ","Om click");
//   		Intent intent = new Intent(this, TagViewer.class);
//   	  startActivity(intent);
   		
   	}
  
       @Override
   	public void onBackPressed()
   	{
   	     // code here to show dialog
    	   
    	   if(tts!=null){
	           
	            
    		   tts.stop();
    		   tts.shutdown();
	        }
    	   
    	   SharedPreferences.Editor editor = sharedpreferences.edit();
	        
	        editor.putString("flag", "0");
	       
	      
	        editor.commit();
   		finish();
   		Intent intent = new Intent(this, MainActivity.class);
   		//intent.putExtra("screen", "tagviewer");
     	  startActivity(intent);
   		
   		
   		
   		
   		Log.d( "BACK FINISH ","Om click");
//   		Intent intent = new Intent(this,
//   		        MidwestDocType.class);
//   		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//   		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//   		startActivity(intent);
   	//	getActivity().finish();
   	     super.onBackPressed();  // optional depending on your needs
   	}
       public static void writeTextFile(String data){
			try {
				//Log.d(key, data);
		//
				 Calendar c = Calendar.getInstance();
			        System.out.println("Current time => "+c.getTime());

			        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
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
		@Override
	    public void onDestroy() {
	        // Don't forget to shutdown tts!
	        if (tts != null) {
	            tts.stop();
	            tts.shutdown();
	        }
	        super.onDestroy();
	    }
		@Override
	    public void onInit(int status) {
	 
	        if (status == TextToSpeech.SUCCESS) {
	 
	            int result = tts.setLanguage(Locale.US);
	 
	            if (result == TextToSpeech.LANG_MISSING_DATA
	                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	                Log.e("TTS", "This Language is not supported");
	            } else {
	               // btnSpeak.setEnabled(true);
	            	speakOut2();
	            }
	 
	        } else {
	            Log.e("TTS", "Initilization Failed!");
	        }
	 
	    }
	 
	    private void speakOut() {
	    	String text = "Front tag scanned successfully now Please scan back tag";
	    	 
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	       
	    }
	    private void speakOut1() {
	    	String text = "Back tag scanned successfully";
	    	 
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	       
	    }
	    private void speakOut2() {
	    	String text = "Please scan front tag";
	    	 
	        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	       
	    }
		
}
