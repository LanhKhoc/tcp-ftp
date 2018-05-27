/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class MainServer {
    private ServerSocket serverSocket;
    
    public static void main(String[] args) {
        try {
            ServerPI serverPI = new ServerPI(CONFIG.PORT_PI);
            serverPI.listen();
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
