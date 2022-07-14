package com.example.ftpapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public  class Recyclerview extends AppCompatActivity {


    private static ArrayList<MyListData> myListData;
    private static MyListAdapter adapter;
    private static String pathStringBuffer;
    private static final int rootDirectoryLength = 19;
    private static androidx.recyclerview.widget.RecyclerView recyclerView;

    private static  ArrayList<Parcelable> scrollPositions = new ArrayList<>();
   public static  Bundle savedInstance;
   //private static int myListUpdateIndex = 0;
    private static Bundle state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        myListData = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MyListAdapter( myListDataGenerator("/storage/emulated/0", 0),"/storage/emulated/0");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    public static ArrayList<MyListData> myListDataGenerator(String pathString, int isBack){

        if (isBack == 0){
            pathStringBuffer = pathString;
        }
          //pathStringRecycler.add(pathString);
        File myDirectory = new File(pathString);   // pathStringRecycler.get(pathStringRecycler.size()-1)
        File[] directories = myDirectory.listFiles();
//        for(File list:directories){
//            Log.e("fileList", String.valueOf(list));
//        }
        myListData.clear();

            if(directories != null) {
                for (File str : directories) {
                    if (str.isDirectory())
                        myListData.add(new MyListData(String.valueOf(str), R.drawable.folder_icon));

                    else myListData.add(new MyListData(String.valueOf(str), R.drawable.file_icon));
                }
                Log.e("myListDataHashcode", String.valueOf(myListData.hashCode()));
                // myListUpdateIndex = myListData.size();
            }
            return myListData;

    }

    public static void scrollExtent(){

        int scroll = recyclerView.computeVerticalScrollOffset();
        Log.i("ScrollExtent ", String.valueOf(scroll));
    //    recyclerView.setScrollY();
    }


    @Override
    public void onBackPressed() {

        String string;
        string = pathStringBuffer.substring(0,pathStringBuffer.lastIndexOf("/"));
        Log.e("OnBackPressed String before cut ",pathStringBuffer);

        if(string.length()<rootDirectoryLength)  super.onBackPressed();

        else {
            float scroll;
            pathStringBuffer = string;
            myListData = myListDataGenerator(string,1);
            adapter.onBackUpdate(string);
            adapter.notifyDataSetChanged();
           // scroll = recyclerView.getScaleY();
           // recyclerView.scrollToPosition((int) scroll);
            //adapter.notifyDataSetChanged();
        }
    }
}

