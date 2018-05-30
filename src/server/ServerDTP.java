/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ServerDTP {
    private ServerSocket serverSocket;
    private Socket socket = null;
    
    public ServerDTP(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        CONFIG.print("Server DTP started at port: " + port);
    }
    
    public void listen() throws IOException {
        while (true) {
            socket = serverSocket.accept();
            CONFIG.print("ServerDTP: New client connected!");
            ServerThreadDTP st = new ServerThreadDTP(socket);
            st.start();
        }
    }
    
    public static void write(BufferedWriter bw, String res) throws IOException {
        bw.write(res);
        bw.newLine();
        bw.flush();
    }
}
