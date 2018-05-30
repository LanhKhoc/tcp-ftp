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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.FilesUtil;
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
    
    public void run() {
        try {
            while (true) {
                String req = br.readLine();
                HashMap<String, String> reqPairs = new HashMap<String, String>();
                reqPairs = new Gson().fromJson(req, reqPairs.getClass());
                CONFIG.print("DTP while(true): " + req);
                
                String res = "";
                String payload = reqPairs.get("payload");
                switch (reqPairs.get("action").trim()) {
                    case "isFolder": {
                        res = isFolder(payload);
                        break;
                    }
                    
                    case "upload": {
                        upload(reqPairs.get("filename"), reqPairs.get("pathServer"));
                        break;
                    }
                    
                    case "verify": {
                        res = verify(reqPairs.get("user_session"), reqPairs.get("user_token"));
                        if (res.equals("fail")) { socket.close(); }
                        break;
                    }
                }
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
    
    private void upload(String filename, String pathServer) throws FileNotFoundException, IOException {
        String name = CONFIG.PATH_UPLOAD + "/" + user_session + pathServer + "/" + filename;
        File f = new File(name);
        
        // NOTE: Create other file if file already exist
        int i = 1;
        while (true){
            if (!f.exists()) { break; }
            name = CONFIG.PATH_UPLOAD + "/" + user_session + pathServer + "/" + Files.getNameWithoutExtension(filename);
            
        }
        FileOutputStream fout = new FileOutputStream(f);
        
        int c; String tmp;
        do {
            tmp = br.readLine();
            c = Integer.parseInt(tmp);
            if (c != -1) { fout.write(c); }
        } while (c != -1);
        fout.close();
    }
}
