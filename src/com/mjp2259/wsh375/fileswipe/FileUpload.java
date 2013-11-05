package com.mjp2259.wsh375.fileswipe;


/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FileUpload extends Activity {

        private MenuItem item;
        private String url = "http://166.78.154.117:5000/upload";
        ProgressBar mProgress = null;
        String fileLoc, fileType, fileName;
        
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_upload);
                
                mProgress = (ProgressBar) findViewById(R.id.progressBar1);
                
                
                Intent myIntent= getIntent();
                fileLoc = myIntent.getStringExtra("ChosenFile");
                fileType = myIntent.getStringExtra("FileType");
                fileName = myIntent.getStringExtra("UserFileName");
                
                
                
                //final EditText edtTxt1 = (EditText) findViewById(R.id.editTextUpl1);
                //final EditText edtTxt2 = (EditText) findViewById(R.id.editTextUpl2);
                Button btnUpl = (Button) findViewById(R.id.upload);
                
                //edtTxt1.setText(fileLoc);
                //edtTxt2.setText(myIntent.getStringExtra("FileType"));
                btnUpl.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                               // String param1 = edtTxt1.getText().toString();
                               // String param2 = edtTxt2.getText().toString();
                                
                                //item.setActionView(R.layout.progress);
                                SendHttpRequestTask t = new SendHttpRequestTask();
                                
                                String[] params = new String[]{url, fileLoc, fileType, fileName};
                                t.execute(params);
                        }
                });
                

        }
        
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.main, menu);
                item = menu.getItem(0);
                return true;
        }

        
        
        private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
        	HttpURLConnection con;
            DataOutputStream os;
            String delimiter = "--", lineEnd = "\r\n", hashedUrl;
            String boundary =  "SwA"+Long.toString(System.currentTimeMillis())+"SwA";
            int bytesAvailable, bufferSize;
            byte[] buffer;
            int bytesRead;
            

            
                @Override
                protected String doInBackground(String... params) {
                        String url = params[0];
                        String fileloc = params[1];
                        String filetype = params[2];
                        String filename = params[3];
                        File upFile = new File(fileloc);
                        
                        
                        Log.d("upload", "oh");
                                                
                        try {
                        	 FileInputStream inStream = new FileInputStream(upFile);
                        	 Log.d("url", url);
                        	 con = (HttpURLConnection) ( new URL(url)).openConnection();
                             con.setRequestMethod("POST");
                             con.setDoInput(true);
                             con.setDoOutput(true);
                             con.setRequestProperty("Connection", "Keep-Alive");
                             con.setRequestProperty("ENCTYPE", "multipart/form-data");
                             con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                             con.setRequestProperty("upload", filename +filetype);
                             
                             os = new DataOutputStream(con.getOutputStream());
                             os.writeBytes(delimiter + boundary + lineEnd);
                             os.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\""
                                     + filename + filetype + "\"" + lineEnd);
                             os.writeBytes(lineEnd);
                             
                             bytesAvailable = inStream.available();
                             bufferSize = (int) upFile.length()/200;
                             mProgress.setMax(bufferSize);
                             buffer = new byte[bufferSize];
                             int sentBytes=0;
                             
                             bytesRead = inStream.read(buffer, 0, bufferSize);
                             
                             while (bytesRead > 0) {
                                 os.write(buffer, 0, bufferSize);
                                 // Update progress dialog
                                 sentBytes += bufferSize;
                                 mProgress.setProgress((int)(sentBytes * 100 / bytesAvailable));
                                 bytesAvailable = inStream.available();
                                 bytesRead = inStream.read(buffer, 0, bufferSize);
                             }
                             os.writeBytes(lineEnd);
                             os.writeBytes(delimiter + boundary + delimiter + lineEnd);
                             
                             DataInputStream intest = new DataInputStream(con.getInputStream());
                             final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                             byte[] buff2= new byte[256];
                             intest.read(buff2);
                             bo.write(buff2);
                             hashedUrl = bo.toString().trim();
                             Log.d("test", hashedUrl);
                             bo.close();
                             
                             
                             
                             int serverResponseCode = con.getResponseCode();
                             String serverResponseMessage = con.getResponseMessage();
                             Log.d("response" , Integer.toString(serverResponseCode) +" : " + serverResponseMessage);
                             inStream.close();
                             os.flush();
                             os.close();
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
						return hashedUrl;
                            
                }
                
                

                @Override
                protected void onPostExecute(String data) {                        
                        //item.setActionView(null);
                        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();

                        
                }
                
                
                
        }


}