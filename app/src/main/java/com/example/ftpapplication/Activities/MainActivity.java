package com.example.ftpapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.ftpapplication.ftp.MyFTPClientFunctions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText ftpUsername, ftpPassword, ftphostName;
    private EditText ftpPortNumber;
    private Button setLocationButton,connectButton;
    private static  TextView setLocationText;
    private ProgressDialog pd;
    private boolean ftpConnect;
    private MyFTPClientFunctions myFTPClientFunctions;
    private TextView connectStatusText;
    private String[] cacheFolder;
    private File makeCacheFolder;
    private Button transferButton;
    private String cacheFolderPath;


    private static Set<String> srcFolder = new HashSet<>();
    public static void setsrcFolder(String src_Folder){
        srcFolder.add(src_Folder);
        setLocationText.setText(src_Folder);
    }



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
//       }else{
//           try {
//               File textFile = new File(context.getApplicationContext().getDataDir()+"/cacheFolder/testFile.txt");
//               if(textFile.createNewFile()){
//                   System.out.println("File Created -  testFile.txt");
//                   BufferedWriter writer = new BufferedWriter(new FileWriter(textFile));
//                   writer.write("Hello this is anant Writing from inside if else statements !!");
//                   writer.close();
//               }else{
//                   System.out.println("File Already Exists!!");
//                   BufferedReader reader = new BufferedReader(new FileReader(textFile));
//                   String textFileContents = reader.readLine();
//                   System.out.println(textFileContents);
//
//               }
//           } catch (IOException e) {
//               e.printStackTrace();
 //          }

    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("ftpData",MODE_PRIVATE);
        String hostName = sharedPreferences.getString("Hostname", "");
        String userName = sharedPreferences.getString("Username", "");
        String password = sharedPreferences.getString("Password", "");
        String portNumber = sharedPreferences.getString("portNumber", "");

       /* ftpPortNumber.setText(portNumber);
        ftphostName.setText(hostName);
        ftpUsername.setText(userName);
        ftpPassword.setText((password));*/
        ftpPortNumber.setText("10000");
        ftphostName.setText("192.168.1.6");
        ftpUsername.setText("pi");
        ftpPassword.setText(("7611"));

    }

    protected void onPause(){
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("ftpData",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString("setLocationText",setLocationText.toString());
        myEdit.putString("ftpUsername", ftpUsername.getText().toString());
        myEdit.putString("ftpPassword", ftpPassword.getText().toString());
        myEdit.putString("ftpHostname", ftphostName.getText().toString());
        myEdit.putString("ftpPortNumber",ftpPortNumber.getText().toString());
        myEdit.apply();

    }

    public boolean isFTPConnected(){
        return(myFTPClientFunctions.isStatus() && myFTPClientFunctions.isConnected());
    }

    public void setLocationButton(View view){

        Log.e("SetLocationButton", " pressed");
        Intent intent = new Intent(this, LocalFileListing.class);
        startActivity(intent);

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

//    public void transferButton(View view){
//        if(isFTPConnected()){
//            ArrayList<String> transferList = TransferList.getTransferList();
//            if(transferList!=null && transferList.size() !=0){
//
//                ExecutorService executorService = Executors.newSingleThreadExecutor();
//                Handler handler = new Handler(Looper.getMainLooper());
//                executorService.execute(() -> {
//                    //Background Thread
//                    Log.e("#################  TRANSFER FILE  #############","");
//
//                    String srcFile;
//                    String desName;
//                    boolean progress;
//                    boolean FtpDirectory = myFTPClientFunctions.ftpChangeDirectory("FtpApplicationFolder");
//
//                    String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
//                    List<String> desExistFiles = myFTPClientFunctions.getFileList(workingDirectory);
//
//                    desExistFiles.forEach((desFile) ->  Log.i("target Directory files",desFile));
//
//                    for(String transferFile: transferList) {
//                        Log.i("Transferring.............. ", transferFile);
//                        srcFile = transferFile;
//                        desName = transferFile.substring(srcFile.lastIndexOf("/") + 1);
//
//                        if (desExistFiles.contains(desName)) {
//                            Log.i("File already Exist in directory, Skipped", transferFile);
//                        } else {
//                            progress = myFTPClientFunctions.ftpUpload(transferFile, desName, "FtpApplicationFolder");
//                            if (progress) {
//                                Log.i("Transfer Of File: " + srcFile, "COMPLETE");
//                            } else Log.i("Transfer of File:", "Failed");
//                        }
//                    }
//                    Log.e("#################  FILE TRANSFER COMPLETE   ################","");
//
//                    handler.post(() -> {
//                        //UI Thread work here
//                        //  String Path = srcFile.substring(0,srcFile.lastIndexOf("/"));
//                        Toast.makeText(this, "Transfer Complete From Selected Folder ", Toast.LENGTH_SHORT).show();
//                    });
//                });
//            }else{
//                Log.e("There are No files in the Directory","");
//                Toast.makeText(this, "Directory is Empty", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(this, "Connect To Ftp First", Toast.LENGTH_SHORT).show();
//        }
//    }

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

                handler.post(() -> {
                    //UI Thread work here
                    updateConnectStatusUI();
                });
            }
        });
    }

    private void connectionSetup(){
        if (ftpConnect) {
            String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
            // String[] directoryListing = myFTPClientFunctions.ftpPrintFilesList(workingDirectory);
            Log.e("Working Directory", workingDirectory);

            if (myFTPClientFunctions.ftpChangeDirectory(workingDirectory + "Downloads")){
                Log.e("PWD Changed!!", "True");
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