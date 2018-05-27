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
    private boolean isLogged = false;
    
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
    
    public void verifyLogin(String username, String password) throws IOException {
        HashMap<String, String> pairs = new HashMap<String, String>();
        pairs.put("username", username);
        pairs.put("password",password);
        String json = new Gson().toJson(pairs);
        
        ClientPI.write(bw, json);
        
        String res = br.readLine();
        HashMap<String, String> resPairs = new HashMap<String, String>();
        resPairs = new Gson().fromJson(res, resPairs.getClass());
        
        String status = resPairs.get("status");
        if (status.equals("fail")) { 
            throw new IOException(resPairs.get("message"));
        } else {
            isLogged = true;
            // NOTE: Receive port from handshake
            portDTP = Integer.parseInt(resPairs.get("message"));
            CONFIG.print("verifyLogin port: " + portDTP);
        }
    }
}
