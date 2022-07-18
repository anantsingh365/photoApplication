package com.example.ftpapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
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

    private static Set<String> srcFolder = new HashSet<>();
    public static void setsrcFolder(String src_Folder){
        srcFolder.add(src_Folder);
        setLocationText.setText(src_Folder);
    }


 /* private  Handler handler = new Handler(getMainLooper()) {

        public void handleMessage(android.os.Message msg) {

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (msg.what == 0) {
                getFTPFileList();
            } else if (msg.what == 1) {
                showCustomDialog(fileList);
            } else if (msg.what == 2) {
                Toast.makeText(MainActivity.this, "Uploaded Successfully!",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(MainActivity.this, "Disconnected Successfully!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Unable to Perform Action!",
                        Toast.LENGTH_LONG).show();
            }

        }

    };*/


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
        myFTPClientFunctions = new MyFTPClientFunctions();
        connectStatusText = findViewById(R.id.connectStatusTextView);
    }

    public void setLocationButton(View view){

        Log.e("SetLocationButton", " pressed");
        Intent intent = new Intent(this, Recyclerview.class);
        startActivity(intent);

    }

    public void transferButton(View view){
        if(myFTPClientFunctions.isStatus() && myFTPClientFunctions.isConnected()){
            ArrayList<String> transferList = TransferList.getTransferList();
            if(transferList!=null && transferList.size() !=0){

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executorService.execute(() -> {
                    //Background Thread
                    Log.e("#################  TRANSFER FILE PLACEHOLDER   #############","");

                    String srcFile;
                    String desName;
                    boolean progress;

                    for(String file: transferList){
                        Log.i("Transferring ", file);
                        srcFile = file;
                        desName = file.substring(srcFile.lastIndexOf("/")+1);
                        progress = myFTPClientFunctions.ftpUpload(file, desName, "C:\\Users\\sanan\\Downloads",getApplicationContext());
                        if(progress){
                            Log.i("Transfer Of File: "+srcFile,"Complete");
                        }
                    }
                    Log.e("#################  FILE TRANSFER COMPLETE   ################","");

                        handler.post(() -> {
                            //UI Thread work here
                          //  String Path = srcFile.substring(0,srcFile.lastIndexOf("/"));
                            Toast.makeText(this, "Transfer Complete From Selected Folder ", Toast.LENGTH_SHORT).show();
                        });

                });

            }else{
                Log.e("There are no files in the Directory","");
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
            if (myFTPClientFunctions.isStatus() && myFTPClientFunctions.isConnected()){
               ftpDisconnect();
            }else{
            ftpConnect = myFTPClientFunctions.ftpConnect(ftphostName.getText().toString(), ftpUsername.getText().toString()
                                                            , ftpPassword.getText().toString()
                                                            ,Integer.parseInt(ftpPortNumber.getText().toString()));

            connectionSetup(); //connect and Create default folder in /Downloads

            handler.post(() -> {
                //UI Thread work here
                updateConnectStatus();
            });
        }
        });
    }

    private void connectionSetup(){
        if (ftpConnect) {
            String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
            // String[] directoryListing = myFTPClientFunctions.ftpPrintFilesList(workingDirectory);
            Log.e("Working Directory", workingDirectory);

            if (myFTPClientFunctions.ftpChangeDirectory(workingDirectory + "Downloads")) {
                Log.e("PWD Changed!!", "True");
                Log.e("Now working Directory is - ", myFTPClientFunctions.ftpGetCurrentWorkingDirectory());
            }
            myFTPClientFunctions.ftpPrintFilesList(myFTPClientFunctions.ftpGetCurrentWorkingDirectory());

            if (ftpMakeDefaultFolder("FtpApplicationFolder"))   Log.e("Default Folder Created: ", "True");
            else    Log.e("Default Folder Created: ", "False");
            return;
        }
        Log.e("Connection Setup Failed","connectionSetup Method in MainActivity.java");
    }

    private void updateConnectStatus(){
        if (ftpConnect) {
            Toast.makeText(getApplicationContext(), "Connection Established ", Toast.LENGTH_SHORT).show();
            findViewById(R.id.connectStatusTextView);
            connectStatusText.setText("Connected");
            connectButton.setText("Disconnect");
        } else{
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
        String[] directoryListing = myFTPClientFunctions.ftpPrintFilesList(defLocation);
        for(String directory: directoryListing){
            if(directory.equals("FtpApplicationFolder")){
                return true;
            }
        }
        return myFTPClientFunctions.ftpMakeDirectory("FtpApplicationFolder");
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

   /* private void connectToFTPAddress() {

        final String host = ftphostName.getText().toString().trim();
        final String username = ftpUsername.getText().toString().trim();
        final String password = ftpPassword.getText().toString().trim();

        if (host.length() < 1) {
            Toast.makeText(MainActivity.this, "Please Enter Host Address!",
                    Toast.LENGTH_LONG).show();
        } else if (username.length() < 1) {
            Toast.makeText(MainActivity.this, "Please Enter User Name!",
                    Toast.LENGTH_LONG).show();
        } else if (password.length() < 1) {
            Toast.makeText(MainActivity.this, "Please Enter Password!",
                    Toast.LENGTH_LONG).show();
        } else {

            new Thread(new Runnable() {
                public void run() {
                    boolean status = false;
                    status = ftpclient.ftpConnect(host, username, password, 21);
                    if (status == true) {
                        Log.d(TAG, "Connection Success");
                        handler.sendEmptyMessage(0);
                    } else {
                        Log.d(TAG, "Connection failed");
                        handler.sendEmptyMessage(-1);
                    }
                }
            }).start();
        }
    }*/
}