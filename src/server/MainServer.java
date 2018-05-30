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
    public static void main(String[] args) {
        try {
            ServerPI serverPI = new ServerPI(CONFIG.PORT_PI);
            ServerDTP serverDTP = new ServerDTP(CONFIG.PORT_DTP);
            new Thread() {
                @Override
                public void run() {
                    try {
                        serverDTP.listen();
                    } catch (IOException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
            
            new Thread() {
                @Override
                public void run() {
                    try {
                        serverPI.listen();
                    } catch (IOException ex) {
                        Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
