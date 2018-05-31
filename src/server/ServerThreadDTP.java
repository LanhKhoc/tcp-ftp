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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.FilesUtil;
import util.common_util;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ServerThreadDTP extends Thread {
    private Socket socket = null;
    private BufferedReader br = null;
    private BufferedWriter bw = null;
    private String user_session = null;
    private String user_token = null;
    
    public ServerThreadDTP(Socket _socket) throws IOException {
        this.socket = _socket;
        this.br = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String req = br.readLine();
                HashMap<String, String> reqPairs = new HashMap<>();
                reqPairs = new Gson().fromJson(req, reqPairs.getClass());
                CONFIG.print("DTP while(true): " + req);
                
                String res = "";
                String payload = reqPairs.get("payload");
                switch (reqPairs.get("action").trim()) {
                    case "isFile": {
                        res = isFile(payload);
                        break;
                    }
                    
                    case "isFolder": {
                        res = isFolder(payload);
                        break;
                    }
                    
                    case "upload": {
                        res = upload(reqPairs.get("filename"), reqPairs.get("pathServer"));
                        break;
                    }
                    
                    case "download": {
                        res = download(reqPairs.get("pathServer"));
                        break;
                    }
                    
                    case "verify": {
                        res = verify(reqPairs.get("user_session"), reqPairs.get("user_token"));
                        if (res.equals("fail")) { socket.close(); }
                        break;
                    }
                }
                System.out.println("DTP WRITE: " + res);
                ServerDTP.write(bw, res);
            }
        } catch(Exception ex) {
            Logger.getLogger(ServerThreadPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String verify(String session, String token) throws IOException {
        // TODO: Send request to database then verify
        user_session = session;
        user_token = token;
        
        return "success"; // fail
    }
    
    private String isFolder(String path) throws IOException {
        boolean res = FilesUtil.isFolder(CONFIG.PATH_UPLOAD + "/" + user_session + path);
        CONFIG.print("DTP: isFolder: " + CONFIG.PATH_UPLOAD + "/" + user_session + path + ": " + res);
        return res ? "success" : "fail";
    }
    
    private String isFile(String path) {
        boolean res = FilesUtil.isFile(CONFIG.PATH_UPLOAD + "/" + user_session + path);
        CONFIG.print("DTP: isFile: " + CONFIG.PATH_UPLOAD + "/" + user_session + path + ": " + res);
        return res ? "success" : "fail";
    }
    
    private String upload(String filename, String pathServer) {
        HashMap<String, String> pairs = new HashMap<>();
        
        try {
            String name = CONFIG.PATH_UPLOAD + "/" + user_session + pathServer + "/" + filename;
            File f = new File(name);
            
            // NOTE: Create other file if file already exist
            int i = 1;
            while (true){
                if (!f.exists()) { break; }
                name = CONFIG.PATH_UPLOAD + "/" + user_session + pathServer + "/" + filename;
            }
            
            FileOutputStream fout = new FileOutputStream(f);
            int c; String tmp;
            do {
                tmp = br.readLine();
                c = Integer.parseInt(tmp);
                if (c != -1) { fout.write(c); }
            } while (c != -1);
            fout.close();
            
            CONFIG.print("DTP: upload file done! " + tmp);
        } catch (Exception ex) {
            Logger.getLogger(ServerThreadDTP.class.getName()).log(Level.SEVERE, null, ex);
            
            pairs.put("status", "fail");
            pairs.put("message", ex.getMessage());
            return new Gson().toJson(pairs);
        }
        
        pairs.put("status", "success");
        pairs.put("message", "Upload file done");
        return new Gson().toJson(pairs);
    }
    
    private String download(String pathServer) {
        HashMap<String, String> pairs = new HashMap<>();
        
        try {
            String name = CONFIG.PATH_UPLOAD + "/" + user_session + pathServer;
            File f = new File(name);
            
            // NOTE: Send file name to client save
            common_util.write(bw, f.getName());
            
            // NOTE: Send file
            FileInputStream fin = new FileInputStream(f);
            int c;
            do {
                c = fin.read();
                common_util.write(bw, String.valueOf(c));
            } while(c != -1);
            CONFIG.print("DTP download done: " + c);
            fin.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThreadDTP.class.getName()).log(Level.SEVERE, null, ex);
            
            pairs.put("status", "fail");
            pairs.put("message", ex.getMessage());
            return new Gson().toJson(pairs);
        }
        
        pairs.put("status", "success");
        pairs.put("message", "Download file done");
        return new Gson().toJson(pairs);
    }
}
