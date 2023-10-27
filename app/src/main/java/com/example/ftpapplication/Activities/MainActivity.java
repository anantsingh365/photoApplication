package com.example.ftpapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ftpapplication.Compression.ImageCompressorImpl;
import com.example.ftpapplication.R;
import com.example.ftpapplication.Services.ImageUploadBackgroundService;
import com.example.ftpapplication.ftp.MyFTPClientFunctions;

import org.apache.commons.net.examples.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText ftpUsername, ftpPassword, ftphostName;
    private EditText ftpPortNumber;
    private Button setLocationButton,connectButton;
    private static  TextView setLocationText;
    private boolean ftpConnect;
    private MyFTPClientFunctions myFTPClientFunctions;
    private TextView connectStatusText;
    private String cacheFolderPath;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLocationText = findViewById(R.id.setLocationText);
        setLocationButton = findViewById(R.id.setLocationButton);
        ftpPortNumber = findViewById(R.id.ftpPortNumber);
        ftpUsername =   findViewById(R.id.ftpUsername);
        ftpPassword =   findViewById(R.id.ftpPassword);
        ftphostName = findViewById(R.id.hostName);
        setLocationButton = findViewById(R.id.setLocationButton);
        connectButton = findViewById(R.id.connectButton);
        myFTPClientFunctions = MyFTPClientFunctions.getMyFTPClientFunctions();
        connectStatusText = findViewById(R.id.connectStatusTextView);

       Context context = getApplicationContext();
       File file = new File(context.getApplicationContext().getDataDir()+"/cacheFolder");
       boolean bool = file.mkdir();
       if(bool) {
           System.out.println("cache folder Created " + file.getAbsolutePath());
           cacheFolderPath = file.getAbsolutePath();
       }else{
           Log.e("CacehFolder At ",file.getAbsolutePath());
           cacheFolderPath = file.getAbsolutePath();
       }
       //foreGroundService Starting
        if(!foregroundServiceRunning()){
            Intent i = new Intent(this, ImageUploadBackgroundService.class);
            startForegroundService(i);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> PMap  = preferences.getAll();
        PMap.forEach((k,v) ->  Log.d("Preferences", "key -> " + k + "value -> " + v));

        //intially set hostName from already saved value
        ftphostName.setText(loadHostName(preferences));

        //update value whenever preference are changed
        SharedPreferences.OnSharedPreferenceChangeListener updateHostNameOnPreferencesChangeListener =
                        (preference, Key) ->{
                            if(Key.equals("hostname")){
                               getPreferenceValue("hostname", preference);
                            }
                        };

        preferences.registerOnSharedPreferenceChangeListener(updateHostNameOnPreferencesChangeListener);
    }
    public String loadHostName(SharedPreferences preferences){
        return getPreferenceValue("hostname", preferences);
    }
    public String getPreferenceValue(String key, SharedPreferences preferences){
        Map<String, ?> m = preferences.getAll();
        String e = (String) m.get(key);
        if(e != null) {
            Log.i("saved "+ key + "loaded -> ,", e);
            ftphostName.setText(e);
            return e;
        }
        //if no value, return empty string
        return "";
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ImageUploadBackgroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> PMap  = preferences.getAll();
        PMap.forEach((k,v) ->  Log.d("Preferences", "key -> " + k + "value -> " + v));

        preferences.registerOnSharedPreferenceChangeListener((preference, Key) ->{
            if(Key.equals("hostname")){
                Map<String, ?> m = preference.getAll();
                String e = (String) m.get("hostname");
                if(e != null) {
                    Log.i("hostname changed -> ", e);
                    ftphostName.setText(e);
                }
            }
        });
    }

    protected void onPause(){
        super.onPause();
    }

    public boolean isFTPConnected(){
        return(myFTPClientFunctions.isStatus() && myFTPClientFunctions.isConnected());
    }

    public void setLocationButton(View view){
        Log.i("Starting Set local File Listing Activity", "");
        Intent i = new Intent(this, LocalFileListing.class);
        startActivity(i);
    }

    public void settingsButton(View view){
        Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
      myIntent.putExtra("key", "Hello world "); //Optional parameters
        startActivity(myIntent);
    }

    public void transferButton(View view){
        if(ftpConnect){
            if(ImageCompressorImpl.transferStatus()){
                Toast.makeText(this, "Transfer Already in Progress ", Toast.LENGTH_SHORT).show();
                // return;
            }else{
                ImageCompressorImpl compressor = ImageCompressorImpl.getCompressor();
                compressor.setCacheFolderPath(cacheFolderPath);
                new Thread(compressor::startTransfer).start();
            }
        }else{
            Toast.makeText(this, "Connect To Ftp First", Toast.LENGTH_SHORT).show();
        }
    }

    public void connectButton(View view) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background Thread
            if (isFTPConnected()){
                ftpDisconnect();
            }else{
                ftpConnect = myFTPClientFunctions.ftpConnect(ftphostName.getText().toString(), ftpUsername.getText().toString()
                        , ftpPassword.getText().toString()
                        ,Integer.parseInt(ftpPortNumber.getText().toString()));

                connectionSetup(); //connect and Create default folder in /Downloads

                //UI Thread work here
                handler.post(this::updateConnectStatusUI);
            }
        });
    }

    private void connectionSetup(){
        if (ftpConnect) {
            String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
            // String[] directoryListing = myFTPClientFunctions.ftpPrintFilesList(workingDirectory);
            Log.e("Working Directory", workingDirectory);

            if (myFTPClientFunctions.ftpChangeDirectory(workingDirectory + "Downloads")){
                Log.e("PWD is", workingDirectory + "Downloads");
                Log.e("Now working Directory is - ", myFTPClientFunctions.ftpGetCurrentWorkingDirectory());
            }

            myFTPClientFunctions.ftpPrintFilesList(myFTPClientFunctions.ftpGetCurrentWorkingDirectory());

            if(ftpMakeDefaultFolder("FtpApplicationFolder")){
                Log.e("Default Folder Created: ", "True");
            }else {
                Log.e("Default Folder Created: ", "False");
            }
        }else{
            Log.e("Connection Setup Failed","connectionSetup Method in MainActivity.java");
        }
    }

    private void updateConnectStatusUI(){
        if (ftpConnect) {
            Toast.makeText(getApplicationContext(), "Connection Established ", Toast.LENGTH_SHORT).show();
          // findViewById(R.id.connectStatusTextView);
            connectStatusText.setText("Connected");
            connectButton.setText("Disconnect");
        }else{
            Toast.makeText(getApplicationContext(), "Error Connecting to the Server", Toast.LENGTH_SHORT).show();
            Log.e("Error Connecting to the Server","updateConnectStatus Method in MainAcitivity.java");
        }
    }

    private void ftpDisconnect(){
        if(myFTPClientFunctions.ftpDisconnect()){
            connectButton.setText("Connect");
            connectStatusText.setText("Not Connected");
            Log.e("FTP Disconnected Successfully","");
        }else{
            Log.e("Could Not Disconnect To Ftp","");
        }
    }

    private boolean ftpMakeDefaultFolder(String defLocation){
        List<String> directoryListing = myFTPClientFunctions.ftpPrintFilesList(defLocation);
        for(String directory: directoryListing){
            if(directory.equals("FtpApplicationFolder")){
                return true;
            }
        }
        return myFTPClientFunctions.ftpMakeDirectory("FtpApplicationFolder");
    }
}