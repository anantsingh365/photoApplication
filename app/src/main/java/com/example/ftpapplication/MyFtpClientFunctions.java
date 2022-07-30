package com.example.ftpapplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.util.Log;

class MyFTPClientFunctions {

    // Now, declare a public FTP client object.
    private static MyFTPClientFunctions myFTPClientFunctions;


    private MyFTPClientFunctions(){

    }

    public static MyFTPClientFunctions getMyFTPClientFunctions(){
        if(myFTPClientFunctions == null){
            return new MyFTPClientFunctions();
        }
        return myFTPClientFunctions;
    }


    private static final String TAG = "MyFTPClientFunctions";
    private FTPClient mFTPClient = null;

    private boolean status = false;
    private boolean isConnected = false;


    public boolean isStatus() {
        return status;
    }

   /* public void setStatus(boolean status) {
        this.status = status;
    }*/

    public boolean isConnected() {
        return isConnected;
    }

   /* public void setConnected(boolean connected) {
        isConnected = connected;
    }*/


    // Method to connect to FTP server:
    public boolean ftpConnect(String host, String username, String password,
                              int port) {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);

            isConnected = FTPReply.isPositiveCompletion(mFTPClient.getReplyCode());
            // now check the reply code, if positive mean connection success
            if (isConnected) {
                // login using username & password
                status = mFTPClient.login(username, password);

                /*
                 * Set File Transfer Mode
                 *
                 * To avoid corruption issue you must specified a correct
                 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                 * transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                return status;
            }
        } catch (Exception e) {
            Log.e("FTPConnectFAil",String.valueOf(e));
            Log.d(TAG, "Error: could not connect to host " + host);
        }

        return false;
    }

    // Method to disconnect from FTP server:

    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            status = false;
            isConnected = false;
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
            status = true;
            isConnected = true;
        }
        return false;
    }

    // Method to get current working directory:

    public String ftpGetCurrentWorkingDirectory() {
        try {
            String workingDir = mFTPClient.printWorkingDirectory();

            return workingDir;
        } catch (Exception e) {
            Log.d(TAG, "Error: could not get current working directory.");
            Log.e("Error: Gettings Working Directory",e.toString());
        }

        return null;
    }

    // Method to change working directory:

    public boolean ftpChangeDirectory(String directory_path) {
        try {
           return mFTPClient.changeWorkingDirectory(directory_path);
        } catch (Exception e) {
            Log.d(TAG, "Error: could not change directory to " + directory_path);
            Log.e("Could not create folder",e.toString());
        }

        return false;
    }

    // Method to list all files in a directory:

    public List<String> ftpPrintFilesList(String dir_path) {
        List<String> fileList = null;
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;
            fileList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    fileList.add("File :: " + name);
                    Log.i(TAG, "File : " + name);
                } else {
                    fileList.add("Directory :: " + name);
                    Log.i(TAG, "Directory : " + name);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
    }

    public List<String> getFileList(String dir_path){
        List<String> fileList = null;
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;
            fileList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    fileList.add(name.trim());
                    Log.i(TAG, "File : " + name);
                } else {
                    fileList.add(name.trim());
                    Log.i(TAG, "Directory : " + name);
                }
            }
            return fileList;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
    }

    // Method to create new directory:

    public boolean ftpMakeDirectory(String new_dir_path) {
        try {
            boolean status = mFTPClient.makeDirectory(new_dir_path);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Error: could not create new directory named "
                    + new_dir_path);
        }

        return false;
    }

    // Method to delete/remove a directory:

    public boolean ftpRemoveDirectory(String dir_path) {
        try {
            boolean status = mFTPClient.removeDirectory(dir_path);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Error: could not remove directory named " + dir_path);
        }

        return false;
    }

    // Method to delete a file:

    public boolean ftpRemoveFile(String filePath) {
        try {
            boolean status = mFTPClient.deleteFile(filePath);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Method to rename a file:

    public boolean ftpRenameFile(String from, String to) {
        try {
            boolean status = mFTPClient.rename(from, to);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Could not rename file: " + from + " to: " + to);
        }

        return false;
    }

    // Method to download a file from FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: path to the source file in FTP server desFilePath: path to
     * the destination file to be saved in sdcard
     */
    public boolean ftpDownload(String srcFilePath, String desFilePath) {
        boolean status = false;
        try {
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);
            ;
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();

            return status;
        } catch (Exception e) {
            Log.d(TAG, "download failed");
        }

        return status;
    }

    // Method to upload a file to FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: source file path in sdcard desFileName: file name to be
     * stored in FTP server desDirectory: directory path where the file should
     * be upload to
     */
    public boolean ftpUpload(String srcFilePath, String desFileName,
                             String desDirectory, Context context) {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);

            // change working directory to the destination directory
             //if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
           //  }

            srcFileStream.close();

            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload failed: " + e);
        }

        return status;
    }
}