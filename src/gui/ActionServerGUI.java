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
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
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
    
    public ActionServerGUI(ClientPI _clientPI) {
        this.clientPI = _clientPI;
    }
    
    public HashMap<String, String> handleConnect(String host, String username, String password, String port) {
        HashMap<String, String> message = new HashMap<String, String>();
        if (host.equals("")) { message.put("error", "Host can't be empty!"); return message; }
        if (username.equals("")) { message.put("error", "Username can't be empty!"); return message; }
        if (password.equals("")) { message.put("error", "Password can't be empty!"); return message; }
        if (port.equals("")) { message.put("error", "Port can't be empty!"); return message; }

        return this.clientPI.connect(host, username, password, port);
    }
    
    public TreeWillExpandListener handleTreeFilesFoldersWillExpand = new TreeWillExpandListener () {
        @Override
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            String curPath = "";
            CONFIG.print("treeWillExpand: " + event.getPath().toString());
            
            Object[] paths = event.getPath().getPath();
            for (int i=0; i<paths.length; i++) {
                if (i != 0) { curPath += "/"; }
                curPath += paths[i];
            }
            CONFIG.print("treeWillExpand parent: " + curPath);
            showJTreeServerFilesFolders((JTree) event.getSource(), curPath);
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            
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
    
    public void showJTreeServerFilesFolders(JTree jTree, String parentPath) {
        String json = clientPI.listFilesAndFoldersFromServer(parentPath);
        CONFIG.print("showJTreeServerFiles :" + parentPath + " ---- " + json);
        HashMap<String, ArrayList<String>> pairs = new HashMap<>();
        pairs = new Gson().fromJson(json, pairs.getClass());
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(parentPath);
        if (!parentPath.equals("")) { rootNode.add(new DefaultMutableTreeNode("...")); } 
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        for (String folderName : pairs.get("folders")) {
            rootNode.add(new DefaultMutableTreeNode(folderName));
        }
        for (String fileName : pairs.get("files")) {
            rootNode.add(new DefaultMutableTreeNode(fileName, false));
        }
        jTree.setModel(model);
    }
    
    public void initJTreeFilesFoldersServer(JTree jTree) {
        if (!CONFIG.DEBUG) {
            jTree.expandRow(0);
            jTree.setRootVisible(false);
        }
    }
}
