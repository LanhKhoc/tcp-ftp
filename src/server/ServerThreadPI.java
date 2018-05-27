/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UserDAO;
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
    
    public ServerThreadPI(Socket _socket) throws IOException {
        this.socket = _socket;
        this.br = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
    }
    
    public void run() {
        try {
            String userInfo = br.readLine();
            HashMap<String, String> pairs = new HashMap<String, String>();
            if (checkLogin(userInfo) == true) {
                // NOTE: Handshake
                pairs.put("status", "success");
                pairs.put("message", CONFIG.PORT_DTP + "");
                ServerPI.write(bw, new Gson().toJson(pairs));
                
            } else {
                pairs.put("status", "fail");
                pairs.put("message", "Username/Password incorrect!");
                ServerPI.write(bw, new Gson().toJson(pairs));
                socket.close();
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(ServerThreadPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean checkLogin(String userInfo) throws NoSuchAlgorithmException, SQLException {
        HashMap<String, String> pairs = new HashMap<>();
        pairs = new Gson().fromJson(userInfo, pairs.getClass());
        String username = pairs.get("username");
        String password = pairs.get("password");
        UserDAO ud = new UserDAO();
        ResultSet rs = ud.get("*", "username = ? AND password = ?", new String[] {username, common_util.md5(password)});
        
        return rs.next() != false;
    }
    
    private void handShaking() throws IOException {
        
    }
}
