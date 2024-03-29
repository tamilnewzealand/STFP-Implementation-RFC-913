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

    public static boolean deleteFile(String name) {
        File file = new File(name);
        return file.delete();
    }

    public static boolean renameFile(String oldName, String newName) {
        File destination = new File(newName);
        if (destination.exists()) {
            return false;
        } else {
            File source = new File(oldName);
            if (source.renameTo(destination)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static long getFileSize(String name) {
        File file = new File(name);
        return file.length();
    }

    public static void sendFile(String fileName, BufferedWriter outToClient, String transferType) {
        File file = new File(fileName);
        if (transferType.equals("A")) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    outToClient.write(line + "\r\n");
                    outToClient.flush();
                }
                outToClient.write("\0");
                outToClient.flush();
            } catch (Exception e) {
                return;
            }
        } else {
            try (InputStream is = new FileInputStream(file)) {
                int temp;
                int count = 0;
                while ((temp = is.read()) !=  -1) {
                    outToClient.write(temp);
                    count++;
                    if (count > 1024) {
                        outToClient.flush();
                        count = 0;
                    }
                }
                outToClient.write("\0");
                outToClient.flush();
            } catch (Exception e) {
                return;
            }
            return;
        }
    }
}
