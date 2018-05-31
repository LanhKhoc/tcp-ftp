/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.ClientPI;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ActionServerGUI {
    private ClientPI clientPI;
    private JTree treeDirs;
    private JTree treeFilesFolders;
    private JTextField txtPath;
    DefaultTreeModel modelDirs;
    DefaultTreeModel modelFilesFolders;

    public ActionServerGUI(ClientPI _clientPI) {
        this.clientPI = _clientPI;
    }

    public void setTreeDirs(JTree treeDirs) {
        this.treeDirs = treeDirs;
    }

    public void setTreeFilesFolders(JTree treeFilesFolders) {
        this.treeFilesFolders = treeFilesFolders;
    }
    
    public void setTxtPath(JTextField txtPath) {
        this.txtPath = txtPath;
    }
    
    public HashMap<String, String> handleConnect(String host, String username, String password, String port) {
        HashMap<String, String> message = new HashMap<>();
        if (host.equals("")) { message.put("error", "Host can't be empty!"); return message; }
        if (username.equals("")) { message.put("error", "Username can't be empty!"); return message; }
        if (password.equals("")) { message.put("error", "Password can't be empty!"); return message; }
        if (port.equals("")) { message.put("error", "Port can't be empty!"); return message; }

        return this.clientPI.connect(host, username, password, port);
    }
    
    public String upload(String pathClient, String pathServer) {
        if (this.clientPI.getClientDTP() == null) {
            HashMap<String, String> pairs = new HashMap<>();
            pairs.put("status", "fail");
            pairs.put("message", "Haven't login yet!");
            return new Gson().toJson(pairs);
        }
        
        String res = this.clientPI.getClientDTP().upload(pathClient, pathServer);
        showJTreeServerFilesFolders(pathServer);
        
        return res;
    }
    
    public void download(String pathClient, String pathServer) {
        this.clientPI.getClientDTP().download(pathClient, pathServer);
    }
    
    private String getStringPath(Object[] paths) {
        if (paths.length == 1) { return paths[0].toString(); }
        
        String curPath = "";
        for (int i=0; i<paths.length; i++) {
           if (i != 0) { curPath += "/"; }
           if (paths[i].toString().equals("/") == false) curPath += paths[i];
       }
        
        return curPath;
    }
    
    public TreeWillExpandListener handleTreeDirsWillExpand = new TreeWillExpandListener () {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            String curPath = "";
            CONFIG.print("treeWillExpand: " + event.getPath().toString());
            
            Object[] paths = event.getPath().getPath();
            for (int i=0; i<paths.length; i++) {
                if (i != 0) { curPath += "/"; }
                if (paths[i].toString().equals("/") == false) curPath += paths[i];
            }
            CONFIG.print("treeWillExpand parent: " + curPath);
            showJTreeServerFilesFolders(curPath);
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }
    };
    
    public TreeWillExpandListener handleTreeFilesFoldersWillExpand = new TreeWillExpandListener () {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            String curPath = "";
            CONFIG.print("treeWillExpand: " + event.getPath().toString());
            
            Object[] paths = event.getPath().getPath();
            for (int i=0; i<paths.length; i++) {
                if (i != 0) { curPath += "/"; }
                if (paths[i].toString().equals("/") == false) curPath += paths[i];
            }
            CONFIG.print("treeWillExpand parent: " + curPath);
            showJTreeServerFilesFolders(curPath);
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        }
    };

    public TreeSelectionListener handleTreeFilesFoldersSelection = new TreeSelectionListener() {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            String curPath = "";
            CONFIG.print("treeWillExpand: " + e.getPath().toString());
            
            curPath = getStringPath(e.getPath().getPath());
            txtPath.setText(curPath);
        }
    };
    
    public TreeSelectionListener handleTreeDirsSelection = new TreeSelectionListener() {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            String curPath = "";
            CONFIG.print("treeWillExpand: " + e.getPath().toString());
            curPath = getStringPath(e.getPath().getPath());
            showJTreeServerFilesFolders(curPath);
            
            txtPath.setText(getStringPath(e.getPath().getPath()));
        }
    
    };
    
    private DefaultMutableTreeNode addNodesServer(DefaultMutableTreeNode curTop, ArrayList<LinkedTreeMap<String, String>> list, int index) {
        DefaultMutableTreeNode curDir = null;
        
        if (list.size() > 0) {
            for (LinkedTreeMap.Entry<String, String> entry : list.get(index).entrySet()) {
                int key = Integer.parseInt(entry.getKey());
                String value = entry.getValue();

                curDir = new DefaultMutableTreeNode(value);
                if (curTop != null) { curTop.add(curDir); }
                if (key < list.size() && list.get(key).size() != 0) {
                    addNodesServer(curDir, list, key);
                }
            }
        }
        
        return curTop;
    }
    
    public void showJTreeServerDirs(JTree jTree, String path) {
        String res = clientPI.listDirsFromServer(path);
        ArrayList<LinkedTreeMap<String, String>> list = new ArrayList<>();
        list = new Gson().fromJson(res, list.getClass());
        CONFIG.print("showJTreeServerDirs - res: " + res);
        
        DefaultMutableTreeNode rootNode = addNodesServer(new DefaultMutableTreeNode("/"), list, 0);
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        jTree.setModel(model);
    }
    
    public void showJTreeServerFilesFolders(String parentPath) {
        String json = clientPI.listFilesAndFoldersFromServer(parentPath);
        CONFIG.print("showJTreeServerFiles :" + parentPath + " ---- " + json);
        HashMap<String, ArrayList<String>> pairs = new HashMap<>();
        pairs = new Gson().fromJson(json, pairs.getClass());
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(parentPath);
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
//        for (String folderName : pairs.get("folders")) {
//            rootNode.add(new DefaultMutableTreeNode(folderName));
//        }
        for (String fileName : pairs.get("files")) {
            rootNode.add(new DefaultMutableTreeNode(fileName, false));
        }
        treeFilesFolders.setModel(model);
    }
    
    public void initJTreeFilesFoldersServer(JTree jTree) {
        String json = clientPI.listFilesAndFoldersFromServer("/");
        HashMap<String, ArrayList<String>> pairs = new HashMap<>();
        pairs = new Gson().fromJson(json, pairs.getClass());
        treeFilesFolders.addTreeSelectionListener(handleTreeFilesFoldersSelection);
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("/");
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
//        for (String folderName : pairs.get("folders")) {
//            rootNode.add(new DefaultMutableTreeNode(folderName));
//        }
        for (String fileName : pairs.get("files")) {
            rootNode.add(new DefaultMutableTreeNode(fileName, false));
        }
        treeFilesFolders.setModel(model);
        
        if (!CONFIG.DEBUG) {
            jTree.expandRow(0);
            jTree.setRootVisible(false);
        }
    }
}
