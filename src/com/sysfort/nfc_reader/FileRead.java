package com.sysfort.nfc_reader;

import android.app.Activity;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class FileRead extends Activity {
    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_main);
        listView = (ListView) findViewById(R.id.listView);
        itemArrayAdapter = new ItemArrayAdapter(getApplicationContext(), R.layout.item_layout);

    	Log.d( "onCreate ","viewfile");
        
        
        
        
        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        String fileName1 = "nfcread.csv";
        String path1 = Environment.getExternalStorageDirectory()+"/NFC/"+fileName1;
        Log.d("ROOT string fileName1 onCreate",""+fileName1);
        Log.d(" ROOT string path1  onCreate",""+path1);
       
        File FILE1 = new  File(""+path1);
        
        InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(FILE1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        CSVFile csvFile = new CSVFile(inputStream);
        List<String[]> scoreList = csvFile.read();

        for(String[] scoreData:scoreList ) {
            itemArrayAdapter.add(scoreData);
        }
    }
}