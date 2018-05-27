/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.ClientPI;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import util.FilesUtil;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ActionClientGUI {
    private ClientPI clientPI;
    
    public ActionClientGUI(ClientPI _clientPI) {
        this.clientPI = _clientPI;
    }
    
    public static TreeWillExpandListener handleTreeWillExpand = new TreeWillExpandListener () {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            System.out.println("Expand");
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    };
    
    public HashMap<String, String> handleConnect(String host, String username, String password, String port) {
        HashMap<String, String> message = new HashMap<String, String>();
        if (host.equals("")) { message.put("error", "Host can't be empty!"); return message; }
        if (username.equals("")) { message.put("error", "Username can't be empty!"); return message; }
        if (password.equals("")) { message.put("error", "Password can't be empty!"); return message; }
        if (port.equals("")) { message.put("error", "Port can't be empty!"); return message; }

        return this.clientPI.connect(host, username, password, port);
    }
    
    private DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, String directoryName, String folderName) {
        File directory = new File(directoryName);
        File[] fList = directory.listFiles();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(folderName);
        if (curTop != null) { curTop.add(curDir); }
        
        for (File file : fList) {
            if (file.isDirectory()) {
                addNodes(curDir, file.getAbsolutePath(), file.getName());
            }
        }
        
        return curDir;
    }
    
    public void initJTreeServerDirs(JTree jTree, String userPath) {
        File directory = new File(CONFIG.PATH_UPLOAD + userPath);
        File[] fList = directory.listFiles();
        
        DefaultMutableTreeNode rootNode = addNodes(null, CONFIG.PATH_UPLOAD, "/");
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        jTree.setModel(model);
    }
    
    public void initJTreeServerFiles(JTree jTree, String userPath) {
        ArrayList<String> listFiles = FilesUtil.listFiles(CONFIG.PATH_UPLOAD + userPath);
        ArrayList<String> listFolders = FilesUtil.listFolders(CONFIG.PATH_UPLOAD + userPath);
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("/");
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        for (String folderName : listFolders) {
            rootNode.add(new DefaultMutableTreeNode(folderName));
        }
        
        for (String fileName : listFiles) {
            rootNode.add(new DefaultMutableTreeNode(fileName, false));
        }
        jTree.setModel(model);
    }
}
