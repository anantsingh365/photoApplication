package com.example.ftpapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.ftpapplication.Activities.Adapters.localListingAdapter;
import com.example.ftpapplication.utils.LocalFileListView;
import com.example.ftpapplication.R;

import java.io.File;
import java.util.ArrayList;


public class LocalFileListing extends AppCompatActivity {


    private static ArrayList<LocalFileListView> localFileListData;
    private static localListingAdapter adapter;
    private static String pathStringBuffer;
    private static final int rootDirectoryLength = 19;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        localFileListData = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new localListingAdapter( forwardListingGenerator("/storage/emulated/0"),"/storage/emulated/0");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public static ArrayList<LocalFileListView> forwardListingGenerator(String pathString){
        pathStringBuffer = pathString;
        return fileListingGenerator(pathString);
    }

    public static ArrayList<LocalFileListView> backListingGenerator(String pathString){
        return fileListingGenerator(pathString);
    }

    private static ArrayList<LocalFileListView> fileListingGenerator(String pathString){

        File myDirectory = new File(pathString);   // pathStringRecycler.get(pathStringRecycler.size()-1)
        File[] directories = myDirectory.listFiles();
        localFileListData.clear();

        if(directories != null) {
            for (File str : directories) {
                if (str.isDirectory()){
                    localFileListData.add(new LocalFileListView(String.valueOf(str), R.drawable.folder_icon));
                } else {
                    localFileListData.add(new LocalFileListView(String.valueOf(str), R.drawable.file_icon));
                }
            }
        }
        return localFileListData;
    }

    @Override
    public void onBackPressed() {

        String string;
        string = pathStringBuffer.substring(0,pathStringBuffer.lastIndexOf("/"));
        Log.e("OnBackPressed String before cut ",pathStringBuffer);

        if(string.length()<rootDirectoryLength){
            super.onBackPressed();
        }else {
            pathStringBuffer = string;
            localFileListData = backListingGenerator(string);
            adapter.onBackUpdate(string);
            adapter.notifyDataSetChanged();
        }
    }
}

