/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.MainClientGUI;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class MainClient {
    public static void main(String[] args) {
        ClientPI clientPI = new ClientPI();
        new MainClientGUI(clientPI).setVisible(true);   
    }
}
