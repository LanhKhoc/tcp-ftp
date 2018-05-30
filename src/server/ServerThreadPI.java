/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UserDAO;
import util.FilesUtil;
import util.common_util;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ServerThreadPI extends Thread {
    private Socket socket = null;
    private BufferedReader br = null;
    private BufferedWriter bw = null;
    private String user_token;
    private String user_session;
    
    private static int idListFile = 1;
    
    public ServerThreadPI(Socket _socket) throws IOException {
        this.socket = _socket;
        this.br = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
    }
    
    public void run() {
        try {
            // NOTE: Get user info client send then check login
            String userInfo = br.readLine();
            HashMap<String, String> pairs = new HashMap<String, String>();
            pairs = new Gson().fromJson(userInfo, pairs.getClass());
            String username = pairs.get("username");
            String password = pairs.get("password");
            
            HashMap<String, String> resPairs = new HashMap<String, String>();
            if (checkLogin(username, password) == true) {
                user_session = username;
                user_token = common_util.md5(username + common_util.md5(password));
                
                // NOTE: Handshake CONFIG.PORT_DTP
                resPairs.put("status", "success");
                resPairs.put("message", CONFIG.PORT_DTP + "");
                resPairs.put("user_token", user_token);
                ServerPI.write(bw, new Gson().toJson(resPairs));
            } else {
                resPairs.put("status", "fail");
                resPairs.put("message", "Username/Password incorrect!");
                ServerPI.write(bw, new Gson().toJson(resPairs));
                socket.close();
            }
            
            while (true) {
                String req = br.readLine();
                HashMap<String, String> reqPairs = new HashMap<String, String>();
                reqPairs = new Gson().fromJson(req, reqPairs.getClass());
                CONFIG.print("PI while(true): " + req);
                
                if (reqPairs.get("user_token").equals(user_token) == false) { socket.close(); break; }
                String res = "";
                String payload = reqPairs.get("payload");
                switch (reqPairs.get("action").trim()) {
                    case "listFilesAndFolders": {
                        res = listFilesAndFolders(payload);
                        break;
                    }
                    case "listDirs": {
                        res = listDirs(payload);
                        break;
                    }
                }
                ServerPI.write(bw, res);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThreadPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean checkLogin(String username, String password) throws Exception {
        UserDAO ud = new UserDAO();
        ResultSet rs = ud.get("*", "username = ? AND password = ?", new String[] {username, common_util.md5(password)});
        return rs.next() != false;
    }
    
    private String listDirs(String path) {
        CONFIG.print("listDirs: " + CONFIG.PATH_UPLOAD + "/" + user_session + path);
        idListFile = 1;
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        addNode(result, CONFIG.PATH_UPLOAD + "/" + user_session + path, 0);
        return new Gson().toJson(result);
    }
    
    private void addNode(ArrayList<HashMap<String, String>> result, String path, int parentId) {
        File directory = new File(path);
        File[] fList = directory.listFiles();
        
        for (int i=0; i<fList.length; i++) {
            if (fList[i].isDirectory()) {
                HashMap<String, String> pairs = new HashMap<>();
                result.add(pairs);
                result.get(parentId).put(idListFile + "", fList[i].getName());
                addNode(result, fList[i].getAbsolutePath(), idListFile++);
            }
        }
    }
    
    private String listFilesAndFolders(String path) {
        CONFIG.print("listFilesAndFolders: " + CONFIG.PATH_UPLOAD + "/" + user_session + path);
        ArrayList<String> listFiles = FilesUtil.listFiles(CONFIG.PATH_UPLOAD + "/" + user_session + path);
        ArrayList<String> listFolderes = FilesUtil.listFolders(CONFIG.PATH_UPLOAD + "/" + user_session + path);
        
        HashMap<String, ArrayList<String>> pairs = new HashMap<>();
        pairs.put("files", listFiles);
        pairs.put("folders", listFolderes);
        
        CONFIG.print("listFilesAndFolders: " + new Gson().toJson(pairs));
        return new Gson().toJson(pairs);
    }
}
