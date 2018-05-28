/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ClientPI {
    private Socket clientSocket;
    private BufferedReader br = null;
    private BufferedWriter bw = null;
    private int portDTP;
    private String user_token;
    private String user_session;
    
    public String getUserSession() { return this.user_session; }
    public String getUserToken() { return this.user_token; }
    
    public static void write(BufferedWriter bw, String res) throws IOException {
        bw.write(res);
        bw.newLine();
        bw.flush();
    }
    
    public HashMap<String, String> connect(String host, String username, String password, String port) {
        HashMap<String, String> message = new HashMap<String, String>();
        
        try {
            CONFIG.print("connect " + host + ": " + port);
            clientSocket = new Socket(host, Integer.parseInt(port));
            this.br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            
            // NOTE: Send username/password to server check login
            verifyLogin(username, password);
        } catch (Exception ex) {
            Logger.getLogger(ClientPI.class.getName()).log(Level.SEVERE, null, ex);
            message.put("error", ex.getMessage());
        }
        
        return message;
    }
    
    public void verifyLogin(String username, String password) throws Exception {
        // NOTE: Send user info to server check login
        HashMap<String, String> pairs = new HashMap<String, String>();
        pairs.put("username", username);
        pairs.put("password",password);
        String json = new Gson().toJson(pairs);
        ClientPI.write(bw, json);
        
        // NOTE: Get response from server about login fail/success
        String res = br.readLine();
        HashMap<String, String> resPairs = new HashMap<String, String>();
        resPairs = new Gson().fromJson(res, resPairs.getClass());
        
        String status = resPairs.get("status");
        if (status.equals("fail")) { 
            throw new Exception(resPairs.get("message"));
        } else {
            user_token = resPairs.get("user_token");
            user_session = username;
            
            // NOTE: Receive portDTP from handshake
            portDTP = Integer.parseInt(resPairs.get("message"));
            CONFIG.print("verifyLogin port: " + portDTP);
            CONFIG.print("verifyLogin user_token: " + user_token);
        }
    }

    public String listFilesAndFoldersFromServer(String path) {
        String res = "";
        
        try {
            HashMap<String, String> pairs = new HashMap<>();
            pairs.put("user_token", user_token);
            pairs.put("action", "listFilesAndFolders");
            pairs.put("payload", path);
            ClientPI.write(bw, new Gson().toJson(pairs));
            
            // NOTE: Receive response for request
            res = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ClientPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }

    public String listDirsFromServer(String path) {
        String res = "";
        
        try {
            HashMap<String, String> pairs = new HashMap<>();
            pairs.put("user_token", user_token);
            pairs.put("action", "listDirs");
            pairs.put("payload", path);
            ClientPI.write(bw, new Gson().toJson(pairs));
            
            // NOTE: Receive response for request
            res = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ClientPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
}
