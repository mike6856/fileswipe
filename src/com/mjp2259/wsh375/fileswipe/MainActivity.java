package com.mjp2259.wsh375.fileswipe;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 Button btnUpload = (Button) findViewById(R.id.uploadFile);
		 
		 btnUpload.setOnClickListener(new View.OnClickListener() {
             
             @Override
             public void onClick(View v) {
                     Intent i = new Intent(MainActivity.this, PickFile.class);
                     startActivity(i);
             }
     });
	}
}
