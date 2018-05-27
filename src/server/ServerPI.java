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
import java.util.logging.Level;
import java.util.logging.Logger;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ServerPI {
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    public ServerPI(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        CONFIG.print("Server started at port: " + port);
    }
    
    public void listen() throws IOException {
        while (true) {
            socket = serverSocket.accept();
            ServerThreadPI st = new ServerThreadPI(socket);
            st.start();
        }
    }
    
    public static void write(BufferedWriter bw, String res) throws IOException {
        bw.write(res);
        bw.newLine();
        bw.flush();
    }
}
