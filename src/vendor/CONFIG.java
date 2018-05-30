package vendor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class CONFIG {
    public static boolean DEBUG = true;
    public static String PATH_UPLOAD = "__UPLOAD__";
    public static String HOST_PI = "localhost";
    public static int PORT_PI = 2121;
    public static int PORT_DTP = 2020;
    
    public static String RETRIEVING_DATA = "Retrieving data...";
    
    public static String DATABASE_HOST = "localhost";
    public static int DATABASE_PORT = 3306;
    public static String DATABASE_USER = "root";
    public static String DATABASE_PASS = "";
    public static String DATABASE_DB = "dut__dacsnm";
    
    public static void print(String str) {
        if (DEBUG == true) { System.out.println(">> DEBUG: " + str); }
    }
}
