package com.example.ftpapplication.ftp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.ftpapplication.utils.TransferList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    public class FTPTransfer {

        MyFTPClientFunctions myFTPClientFunctions = MyFTPClientFunctions.getMyFTPClientFunctions();

    public void transferFile(String srcFile) {

          //  executorService.execute(() -> {
                //Background Thread
                Log.e("#################  TRANSFER FILE  #############", "");

                String desName;
                boolean progress;
                boolean FtpDirectory = myFTPClientFunctions.ftpChangeDirectory("FtpApplicationFolder");

                String workingDirectory = myFTPClientFunctions.ftpGetCurrentWorkingDirectory();
                 List<String> desExistFiles = myFTPClientFunctions.getFileList(workingDirectory);

               //  if(!desExistFiles.isEmpty()){
                    // desExistFiles.forEach((desFile) ->  Log.i("target Directory files",desFile));
               //  }
                desName = srcFile.substring(srcFile.lastIndexOf("/") + 1);
                if(desExistFiles == null){
                    progress = myFTPClientFunctions.ftpUpload(srcFile, desName, "FtpApplicationFolder");
                    if (progress) {
                        Log.i("Transfer Of File: " + srcFile, "COMPLETE");
                    } else Log.i("Transfer of File:", "Failed");
                    return;
                }
                 if (desExistFiles.contains(desName)) {
                    Log.i("File already Exist in directory, Skipped", srcFile);
                 } else {
                progress = myFTPClientFunctions.ftpUpload(srcFile, desName, "FtpApplicationFolder");
                if (progress) {
                    Log.i("Transfer Of File: " + srcFile, "COMPLETE");
                } else Log.i("Transfer of File:", "Failed");
    }
}
}