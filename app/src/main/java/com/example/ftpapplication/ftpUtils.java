package com.example.ftpapplication;

public class ftpUtils implements Runnable {
    private  String HOST_NAME;
    private  String USER_NAME;
    private  String PASSWORD;
    private  int    PORT;
    private  boolean ftpConnectSuccess;

    private MyFTPClientFunctions myFTPClientFunctions = MyFTPClientFunctions.getMyFTPClientFunctions();

    public ftpUtils(String hostname, String username, String password, int port){
        HOST_NAME = hostname;
        USER_NAME = username;
        PASSWORD = password;
        PORT = port;
    }
    public void run(){

    }
    public boolean ftpConnect(){
       ftpConnectSuccess = myFTPClientFunctions.ftpConnect(HOST_NAME,USER_NAME,PASSWORD,PORT);
       return ftpConnectSuccess;
    }
}
