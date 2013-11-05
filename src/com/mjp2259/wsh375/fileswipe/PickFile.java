package com.mjp2259.wsh375.fileswipe;


import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

public class PickFile extends Activity {
	private static final int REQUEST_CODE = 6384; // onActivityResult request code
	String chosenFile;
	TextView fileDisplay;
	String extension;
	EditText fileName;
	Spinner durationValsp, durationUnsp;
	String singl[]={"day", "hour"};
	String plur[]={"days", "hours"};
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pick_file);
            // Create a simple button to start the file chooser process
            Button chooseButton = (Button) findViewById(R.id.chooseButton);
            Button toUpload = (Button)findViewById(R.id.toUploadButton);
            fileDisplay =(TextView) findViewById(R.id.file_location);
            fileName = (EditText) findViewById(R.id.userFileName);
            
            //First spinner
            durationValsp = (Spinner) findViewById(R.id.durationValue);
            ArrayAdapter<CharSequence> adapterVal = ArrayAdapter.createFromResource(this, 
            		R.array.duration_values, android.R.layout.simple_spinner_item);
            adapterVal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            durationValsp.setAdapter(adapterVal);
            
            //Second spinner
            durationUnsp = (Spinner) findViewById(R.id.durationUnit);
            
            durationValsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
            		Object item = parent.getItemAtPosition(pos);
            		if(Integer.valueOf((String) item) == 1){
            			int s2p = durationUnsp.getSelectedItemPosition();
            			ArrayAdapter<String> a1 = new ArrayAdapter <String> (PickFile.this, android.R.layout.simple_spinner_item, new String[]{"day","hour"} );
                        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        durationUnsp.setAdapter(a1);
                        durationUnsp.setSelection(s2p);
            		}
            		else{
            			int s2p = durationUnsp.getSelectedItemPosition();
						ArrayAdapter<String> a2 = new ArrayAdapter <String> (PickFile.this, android.R.layout.simple_spinner_item, new String[]{"days","hours"} );
                        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        durationUnsp.setAdapter(a2);
                        durationUnsp.setSelection(s2p);
            		}
            	
            	}
            	public void onNothingSelected(AdapterView<?> parent){
            	}
            });
            
            
            chooseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            // Display the file chooser dialog
                            showChooser();
                    }
            });
            
            toUpload.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                		String ufn = fileName.getText().toString();
                		String dval = durationValsp.getSelectedItem().toString();
                		String duni = durationUnsp.getSelectedItem().toString();
                		Log.d("check",dval);
                		
                		//Check for valid input
                		if(validUpload(ufn, dval, duni)){
                			Intent i = new Intent(getBaseContext(), FileUpload.class);
                			i.putExtra("ChosenFile", chosenFile);
                			i.putExtra("FileType", extension );
                			i.putExtra("UserFileName", ufn);
                            i.putExtra("DurationValue", dval);
                            i.putExtra("DurationValue", duni);
                			startActivity(i);
                		}
                		else{
                			Toast.makeText(PickFile.this, 
                                "INVALID INPUT (write a smarter function)", Toast.LENGTH_LONG).show();
                		}
                       }
                });
            }
    
    public boolean validUpload(String ufn, String dval, String duni){
    	return true;
    }
    
    private void showChooser() {
            // Use the GET_CONTENT intent from the utility class
            Intent target = FileUtils.createGetContentIntent();
            // Create the chooser Intent
            Intent intent = Intent.createChooser(
                            target, getString(R.string.chooser_title));
            try {
                    startActivityForResult(intent, REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                    // The reason for the existence of aFileChooser
            }                                
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
            case REQUEST_CODE:        
                    // If the file selection was successful
                    if (resultCode == RESULT_OK) {                
                            if (data != null) {
                                    // Get the URI of the selected file
                                    final Uri uri = data.getData();


                                    try {
                                            // Create a file instance from the URI
                                            final File file = FileUtils.getFile(uri);
                                            if(!file.exists()){
                                            	Log.d("test", "True");
                                            	chosenFile = getRealPathFromURI(uri);
                                            }
                                            else
                                            	chosenFile = file.getAbsolutePath();
                                            
                                            extension = chosenFile.substring(chosenFile.lastIndexOf('.'));
                                            int tempIndex = chosenFile.lastIndexOf('/');
                                            fileDisplay.setText(chosenFile.substring(tempIndex + 1));
                                            //Toast.makeText(PickFile.this, 
                                             //       "File Selected: "+getRealPathFromURI(uri), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                            Log.e("FileSelectorTestActivity", "File select error", e);
                                    }
                            }
                    } 
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(PickFile.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
