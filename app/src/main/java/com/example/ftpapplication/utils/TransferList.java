package com.example.ftpapplication.utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class TransferList {
    private static ArrayList<String> transferList = new ArrayList<>();

    public static ArrayList<String> getTransferList() {
        return transferList;
    }
    public static void setTransferList(ArrayList<String> transferList) {
        TransferList.transferList = transferList;
    }
    public static void generateTransferList(String srcPath){

        File myDirectory = new File(srcPath);
        File[] directories = myDirectory.listFiles();
        if (directories != null) {
            for (File str : directories) {
                if (str.isFile() && str.getName().endsWith(".jpg")) {
                    transferList.add(String.valueOf(str));
                }
            }
            transferList.forEach((transferListLog) -> Log.i("transferList", transferListLog));
            Log.e("TransferList Set", "setTransferList Method MyListAdapterClass.java");
        }
    }
}
