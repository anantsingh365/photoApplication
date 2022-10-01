package com.example.ftpapplication.Compression;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import static java.nio.file.StandardCopyOption.*;

import com.example.ftpapplication.ftp.FTPTransfer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageCompressorImpl implements ImageCompressor{

    private static ImageCompressorImpl compressor = new ImageCompressorImpl();
    private int compressionLevel;
    private String srcPath;
    private static boolean transferStatus;
    private List<String> transferList;
    public String cacheFolderPath;

    @Override
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public void beginCompression(String cacheFile) {
       // generateTransferList(srcPath);
            /*
            To do - Image is in cache folder, compress it,
            make a compressed copy and return the path of the compress copy
            * */
    }

    public void deleteCacheAndCompressed(String cache, String compressedCache){

    }

    public void moveToCacheFolder(String srcFile){
        Path src= Paths.get(srcFile);
        Path des = Paths.get(cacheFolderPath +srcFile.substring(srcFile.lastIndexOf("/") + 1));

        try {
            Files.copy(src,des,REPLACE_EXISTING);
            Log.e("CacheImage Copied","true");
        } catch (IOException e) {
            e.printStackTrace();
        }

        beginCompression(des.toAbsolutePath().toString());

        /*
        *BeginCompression function should return path to compressed copy,
        *transfer compress copy top ftp and delete both the uncompressed and compressed copy
        *from cache folder.
         */

        FTPTransfer ftpTransfer = new FTPTransfer();
        ftpTransfer.transferFile(des.toAbsolutePath().toString());

        try {
            Files.delete(des);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Image Transfer From Cache Directory Complete: ", "true");
    }

    public void startTransfer(){
        transferStatus = true;
        transferList.forEach(this::moveToCacheFolder);
        File cacheFolder = new File(cacheFolderPath);
        String[] cacheFolderShoudldBeEmpty = cacheFolder.list();

        if(cacheFolderShoudldBeEmpty.length == 0){
           System.out.println("Cache Folder has been emptied");
       }
        transferStatus = false;
    }

    private ImageCompressorImpl(){}

    public static ImageCompressorImpl getCompressor(){
        return compressor;
    }

    @Override
    public void onTransferComplete(){
        this.transferStatus = false;
    }

    public static boolean transferStatus(){
        return transferStatus;
    }

    public void setTransferList(List<String> transferList){
        this.transferList = transferList;
    }

    public void setCacheFolderPath(String cacheFolderPath){
        this.cacheFolderPath = cacheFolderPath;
    }
}
