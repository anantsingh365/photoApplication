package com.example.ftpapplication;

import static android.content.ContentValues.TAG;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText ftpUsername, ftpPassword, ftphostName;
    private EditText ftpPortNumber;
    private Button setLocationButton,connectButton;
    private TextView setLocationText;
    private ProgressDialog pd;
    private boolean ftpConnect;


  /* private  Handler handler = new Handler() {

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

    }

    public void setLocationButtonPressed(View view){

        Log.e("SetLocationButton", " pressed");
        Intent intent = new Intent(this, Recyclerview.class);
        startActivity(intent);

    }

    public void connectButton(View view){
        MyFTPClientFunctions myFTPClientFunctions = new MyFTPClientFunctions();

       /* Log.e("port String",ftpPortNumber.getText().toString());
        int portInt = Integer.parseInt(ftpPortNumber.getText().toString());
        Log.e("port After Parsing", String.valueOf(portInt));*/

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                String ftpHostName = ftphostName.getText().toString();
                String ftpUserName = ftpUsername.getText().toString();
                String ftpassword =  ftpPassword.getText().toString();
                int portInt = Integer.parseInt(ftpPortNumber.getText().toString());
                ftpConnect = myFTPClientFunctions.ftpConnect(ftpHostName, ftpUserName, ftpassword, portInt);

                String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
                String[] directoryListing = myFTPClientFunctions.ftpPrintFilesList(workingDirectory+"/Downloads");
                Log.e("FileListing",workingDirectory);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        if(ftpConnect ==true)  Toast.makeText(getApplicationContext(), "Connection Established ", Toast.LENGTH_SHORT).show();

                        else Toast.makeText(getApplicationContext(),"Error Connecting to the Server",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


       /*  new Thread(new Runnable(){

            @Override
            public void run() {
                String ftpHostName = ftphostName.getText().toString();
                String ftpUserName = ftpUsername.getText().toString();
                String ftpassword =  ftpPassword.getText().toString();
                int portInt = Integer.parseInt(ftpPortNumber.getText().toString());
                ftpConnect = myFTPClientFunctions.ftpConnect(ftpHostName, ftpUserName, ftpassword, portInt);

            }
        }).start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(ftpConnect ==true){
            Toast.makeText(getApplicationContext(), "Connection Established ", Toast.LENGTH_SHORT).show();
        }*/
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