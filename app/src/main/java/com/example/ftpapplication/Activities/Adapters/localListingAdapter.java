package com.example.ftpapplication.Activities.Adapters;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ftpapplication.Activities.LocalFileListing;
import com.example.ftpapplication.Activities.MainActivity;
import com.example.ftpapplication.utils.MyListData;
import com.example.ftpapplication.R;
import com.example.ftpapplication.utils.TransferList;

import java.io.File;
import java.util.ArrayList;


public class localListingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<localListingAdapter.ViewHolder>{

    private  String trimString;
    private  ArrayList<MyListData> listData;
   // private File isFile;


    public localListingAdapter(ArrayList<MyListData> listData, String trimString) {
        this.listData = listData;
        this.trimString = trimString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final MyListData myListData = listData.get(position);
        holder.textView.setText(listData.get(position).getDescription().substring(trimString.length() + 1));
        holder.imageView.setImageResource(listData.get(position).getImgId());

        holder.flipSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.e("Switch Checked", String.valueOf(isChecked));
                if (isChecked) {
                    String currentPath = listData.get(position).getDescription();
                    Log.e("currentPath_Flip_Button", currentPath);
                    MainActivity.setsrcFolder(currentPath);
                    String srcPath = listData.get(position).getDescription();
                    TransferList.generateTransferList(srcPath);
                }

        });
        holder.relativeLayout.setOnClickListener(view -> {
            String currentPath = listData.get(position).getDescription();
            File file = new File(currentPath);
            if (!file.isFile()) {
                trimString = currentPath;
                listData = LocalFileListing.forwardListingGenerator(currentPath);
                notifyDataSetChanged();
            }
        });
    }

    public void onBackUpdate(String trimString){
        this.trimString = trimString;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder  {
        public Switch flipSwitch;
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.flipSwitch = itemView.findViewById(R.id.flipSwitch);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}