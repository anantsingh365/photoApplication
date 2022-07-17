package com.example.ftpapplication;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;


class MyListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<MyListAdapter.ViewHolder>{

    private  String trimString;
    private  ArrayList<MyListData> listdata;
    private Recyclerview recyclerview = new Recyclerview();
    private ArrayList<Integer> scrollPosition;
    private File isFile;




    // Constructor Recyclerview Recyclerview;
    public MyListAdapter(ArrayList<MyListData> listdata, String trimString) {
        this.listdata = listdata;
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
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("Recyclerview") int position) {
        final MyListData myListData = listdata.get(position);
        holder.textView.setText(listdata.get(position).getDescription().substring(trimString.length() + 1));
        holder.imageView.setImageResource(listdata.get(position).getImgId());
        holder.flipSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("Switch Checked", String.valueOf(isChecked));
                if (isChecked) {
                    String currentPath = listdata.get(position).getDescription();
                    Log.e("currentPath_Flip_Button", currentPath);
                    MainActivity.setsrcFolder(currentPath);
                    setTransferList(position);
                }
            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Context context = view.getContext();
                String string = listdata.get(position).getDescription();
                Log.e("listDataPositionString",string);
                recyclerview.scrollPositions();
                Recyclerview.scrollExtent();*/
                String currentPath = listdata.get(position).getDescription();
                isFile = new File(currentPath);
                if (!isFile.isFile()) {
                    trimString = currentPath;
                    listdata = Recyclerview.myListDataGenerator(currentPath, 0);
                    Log.e("listdateInstance", String.valueOf(listdata.hashCode()));
                    notifyDataSetChanged();
                }
            }
        });
    }
    public void setTransferList(int position){
        ArrayList<String> transferList = new ArrayList<>();
        String srcPath = listdata.get(position).getDescription();
        File myDirectory = new File(srcPath);
        File[] directories = myDirectory.listFiles();
        if(directories != null) {
            for (File str : directories) {
                if(str.isFile()) transferList.add(String.valueOf(str));
            }
            TransferList.setTransferList(transferList);
            Log.e("TransferList Set","setTransferList Method MyListAdapterClass.java");
        }
    }

    public void onBackUpdate(String trimString){
        this.trimString = trimString;
    }


    @Override
    public int getItemCount() {
        return listdata.size();
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