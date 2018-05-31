/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Admin
 */
public class FilesUtil {
    public static int id = 1;

    /**
     * List all the files and folders from a directory
     *
     * @param directoryName to be listed
     * @return 
     */
    public static ArrayList<String> listFilesAndFolders(String directoryName) {
        ArrayList<String> list = new ArrayList<>();
        
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            list.add(file.getName());
        }
        
        return list;
    }
    
    public static boolean isFolder(String path) {
        File f = new File(path);
        return f.isDirectory();
    }
    
    public static boolean isFile(String path) {
        File f = new File(path);
        return f.isFile();
    }

    /**
     * List all the files under a directory
     *
     * @param directoryName to be listed
     */
    public static ArrayList<String> listFiles(String directoryName) {
        ArrayList<String> list = new ArrayList<>();
        
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                list.add(file.getName());
            }
        }
        
        return list;
    }
    
    public static ArrayList<File> listFilesV2(String directoryName) {
        ArrayList<File> list = new ArrayList<>();
        
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                list.add(file);
            }
        }
        
        return list;
    }

    /**
     * List all the folder under a directory
     *
     * @param directoryName to be listed
     */
    public static ArrayList<String> listFolders(String directoryName) {
        ArrayList<String> list = new ArrayList<>();
        
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        
        for (File file : fList) {
            if (file.isDirectory()) {
                list.add(file.getName());
            }
        }
        
        return list;
    }

    /**
     * List all files from a directory and its subdirectories
     *
     * @param directoryName to be listed
     */
    public void listFilesAndFilesSubDirectories(String directoryName) {
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }
    
    public static ArrayList<HashMap<String, String>> listFolderAndSub(ArrayList<HashMap<String, String>> result, String directoryName, int parentId) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        System.out.println(fList.length);
        
        if (fList.length > 0 || fList != null) {
            for (int i=0; i<fList.length; i++) {
                if (fList[i].isDirectory()) {
                    System.out.println("curId: " + id +  ", parentId: " + parentId + " -- " + fList[i].getName());

                    HashMap<String, String> pairs = new HashMap<>();
                    result.add(pairs);
                    result.get(parentId).put(id+"", fList[i].getName());

                    listFolderAndSub(result, fList[i].getAbsolutePath(), id++);
                }
            }
        }
        
        return result;
    }

    public void listDrivers() {
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        paths = File.listRoots();
        for (File path : paths) {
            // prints file and directory paths
            System.out.print("Drive Name: " + path);
            System.out.println(" - Description: " + fsv.getSystemTypeDescription(path));
        }
    }
    
    public static void main(String[] args) throws IOException {
        ArrayList<HashMap<String, String>> res = new ArrayList<>();
        listFolderAndSub(res, "D:\\DUT", 0);
    }
}
