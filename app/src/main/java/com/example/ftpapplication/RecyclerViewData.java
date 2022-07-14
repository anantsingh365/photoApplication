package com.example.ftpapplication;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewData {

    public  ArrayList<String> string;
    public  MyListData[] myListData;
    public  int strLen;
    public  File myDirectory;
    public  File[] directories;
    public String dirString;



    public  MyListData[] start(String myDirectoryString){


        myDirectory = new File(myDirectoryString);
        directories = myDirectory.listFiles();
        string  = new ArrayList<>();

        for(File str : directories){
            string.add(String.valueOf(str));
        }


        strLen = string.size();
        myListData = new MyListData[strLen];

        for(int i=0;i<strLen;i++){
            if(directories[i].isDirectory())
                myListData[i] = new MyListData(string.get(i).substring(20),R.drawable.folder_icon);
            else
                myListData[i] = new MyListData(string.get(i).substring(20),R.drawable.file_icon);
        }
        return myListData;
    }
}

