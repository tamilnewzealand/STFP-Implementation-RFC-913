package server;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;


public class FileAccess {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


    public static String getFileList(String dir, String format) {
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        String fileList = "";

        if (format.equals("V")) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileList = fileList + listOfFiles[i].getName() + "\t\t" + listOfFiles[i].length()/1024 + "kB\t\t" + sdf.format(listOfFiles[i].lastModified()) + "\r\n";
                } else if (listOfFiles[i].isDirectory()) {
                    fileList = fileList + "/" + listOfFiles[i].getName() + "\t\t <DIR> \r\n";
                }
            }
        } else {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileList = fileList + listOfFiles[i].getName() + "\r\n";
                } else if (listOfFiles[i].isDirectory()) {
                    fileList = fileList + "/" + listOfFiles[i].getName() + "\r\n";
                }
            }
        }

        return fileList;
    }

    public static boolean checkDirectoryExists(String dir) {
        File folder = new File(dir);
        return folder.exists();
    }
}
