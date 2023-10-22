package com.example.ftpapplication.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ftpapplication.Compression.ImageCompressorImpl;
import com.example.ftpapplication.R;
import com.example.ftpapplication.ftp.MyFTPClientFunctions;
import com.example.ftpapplication.utils.TransferList;

import java.io.File;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageUploadBackgroundService extends Service {

    private final String defaultBackupLocation = "/storage/emulated/0/Download";
    private String cacheFolderPath;

    private MyFTPClientFunctions connectionObj = MyFTPClientFunctions.getMyFTPClientFunctions();

    private boolean doesConnectionExist(){
        return connectionObj.isConnected() && connectionObj.isStatus();
    }
    private void startTransfer() throws InterruptedException {
        // creating cache folder
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

        //starting the comprssor
        if(doesConnectionExist()){
            if(ImageCompressorImpl.transferStatus()){
              //  Toast.makeText(this, "Transfer Already in Progress ", Toast.LENGTH_SHORT).show();
                 return;
            }else{
                ImageCompressorImpl compressor = ImageCompressorImpl.getCompressor();
                compressor.setCacheFolderPath(cacheFolderPath);

                // setting the transferList
                //default backuplocation is just a placeholder
                List<String> transferList = TransferList.generateTransferList(defaultBackupLocation);
                compressor.setTransferList(transferList);
                Runnable runnable = compressor::startTransfer;
                Thread thread1 = new Thread(runnable);
                thread1.start();
                thread1.join();
                System.out.println("transfer thread Joined Here!!!!!!");
            }
        }else{
           // Toast.makeText(this, "Connect To Ftp First", Toast.LENGTH_SHORT).show();
        }
    }

    Runnable backgroundServiceTask = () -> {
            Log.e("Service", "Service is running...");
            try {
                if(doesConnectionExist()){
                    startTransfer();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(backgroundServiceTask, 5, 5, TimeUnit.SECONDS);

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is running")
                .setContentTitle("Service enabled")
                .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(1001, notification.build());

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }
}
