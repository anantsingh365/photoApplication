package com.example.ftpapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransferList {
    private static ArrayList<String> transferList;

    public static ArrayList<String> getTransferList() {
        return transferList;
    }
    public static void setTransferList(ArrayList<String> transferList) {
        TransferList.transferList = transferList;
    }

}
