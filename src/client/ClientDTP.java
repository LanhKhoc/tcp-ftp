/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

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
public class ClientDTP {
    private int portDTPServer;
    private Socket clientSocket;
    private BufferedReader br = null;
    private BufferedWriter bw = null;
    String user_session = null;
    String user_token = null;
    
    public ClientDTP(String host, int portDTPServer, String user_session, String user_token) throws IOException {
        this.portDTPServer = portDTPServer;
        
        clientSocket = new Socket(host, portDTPServer);
        this.br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.user_session = user_session;
        this.user_token = user_token;
    }
    
    private boolean verify() throws IOException {
        if (user_session == null || user_token == null) { return false; }
        
        CONFIG.print("ClientDTP verify");
        HashMap<String, String> pairs = new HashMap<>();
        pairs.put("action", "verify");
        pairs.put("user_session", user_session);
        pairs.put("user_token", user_token);
        
        common_util.write(bw, new Gson().toJson(pairs));
        String res = br.readLine();
        return res.equals("success");
    }
    
    private boolean checkPathServerIsFolder(String pathServer) throws IOException {
        CONFIG.print("ClientDTP checkPathIsFolderInServer");
        HashMap<String, String> pairs = new HashMap<>();
        pairs.put("action", "isFolder");
        pairs.put("payload", pathServer);
        
        common_util.write(bw, new Gson().toJson(pairs));
        String res = br.readLine();
        return res.equals("success");
    }
    
    private boolean checkPathServerIsFile(String pathServer) throws IOException {
        CONFIG.print("ClientDTP checkPathServerIsFile");
        HashMap<String, String> pairs = new HashMap<>();
        pairs.put("action", "isFile");
        pairs.put("payload", pathServer);
        
        common_util.write(bw, new Gson().toJson(pairs));
        String res = br.readLine();
        return res.equals("success");
    }
    
    public String upload(String pathClient, String pathServer) {
        HashMap<String, String> pairs = new HashMap<>();
        
        try {
            if (verify() == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Verify fail!");
                return new Gson().toJson(pairs);
            }
            
            if (checkPathServerIsFolder(pathServer) == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Path server isn't a folder!");
                return new Gson().toJson(pairs);
            }
            
            if (FilesUtil.isFile(pathClient) == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Path client isn't a file!");
                return new Gson().toJson(pairs);
            }
            
            File f = new File(pathClient);
            FileInputStream fin = new FileInputStream(f);

            CONFIG.print("ClientDTP upload");
            pairs.put("action", "upload");
            pairs.put("pathServer", pathServer);
            pairs.put("filename", f.getName());
            common_util.write(bw, new Gson().toJson(pairs));
                    
            int c;
            do {
                c = fin.read();
                common_util.write(bw, String.valueOf(c));
            } while (c != -1);
            fin.close();

            String res = br.readLine();
            CONFIG.print("DTP upload: " + res);
            return res;
        } catch (IOException ex) {
            Logger.getLogger(ClientDTP.class.getName()).log(Level.SEVERE, null, ex);
            
            pairs.put("status", "fail");
            pairs.put("message", ex.getMessage());
            return new Gson().toJson(pairs);
        }
    }
    
    public String download(String pathClient, String pathServer) {
        HashMap<String, String> pairs = new HashMap<>();
        
        try {
            if (verify() == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Verify fail!");
                return new Gson().toJson(pairs);
            }
            
            if (FilesUtil.isFolder(pathClient) == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Path client isn't a folder!");
                return new Gson().toJson(pairs);
            }
            
            if (checkPathServerIsFile(pathServer) == false) {
                pairs.put("status", "fail");
                pairs.put("message", "Path client isn't a file!");
                return new Gson().toJson(pairs);
            }
            
            CONFIG.print("ClientDTP download");
            pairs.put("action", "download");
            pairs.put("pathServer", pathServer);
            common_util.write(bw, new Gson().toJson(pairs));
            
            String filename = br.readLine();
            File f = new File(pathClient + "/" + filename);
            FileOutputStream fout = new FileOutputStream(f);
            
            int ch; String temp;
            do {
                temp = br.readLine();
                if (temp.equals("")) { break; }
                ch = Integer.parseInt(temp);
                if(ch != -1) { fout.write(ch); }
            } while (ch != -1);
            fout.close();
            
            String res = br.readLine();
            CONFIG.print("DTP upload: " + res);
            return res;
        } catch (IOException ex) {
            Logger.getLogger(ClientDTP.class.getName()).log(Level.SEVERE, null, ex);
            
            pairs.put("status", "fail");
            pairs.put("message", ex.getMessage());
            return new Gson().toJson(pairs);
        }
    }
}
