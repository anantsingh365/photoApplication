package com.example.ftpapplication.Compression;

import com.example.ftpapplication.utils.TransferList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface ImageCompressor {

    void setCompressionLevel(int compressionLevel);

//    void setupTransferList(String srcPath);

    void beginCompression(String CacheFile);

    void onTransferComplete();
}
