/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.ClientPI;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import util.FilesUtil;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class ActionClientGUI {
    private ClientPI clientPI;
    private JTree treeDirs;
    private JTree treeFilesFolders;
    private JTextField txtPathClient;

    public ActionClientGUI(ClientPI _clientPI) {
        this.clientPI = _clientPI;
    }
    
    public void setJTreeDirs(JTree _treeDirs) {
        this.treeDirs = _treeDirs;
    }
    
    public void setJTreeFilesFolders(JTree _treeFilesFolders) {
        this.treeFilesFolders = _treeFilesFolders;
    }
    
    public void setTxtPathClient(JTextField txtPathClient) {
        this.txtPathClient = txtPathClient;
    }
    
    public void upload(String pathClient, String pathServer) {
        this.clientPI.getClientDTP().upload(pathClient, pathServer);
    }
    
    public void showJTreeClientFilesFolders(JTree jTree, String path) {
        ArrayList<String> listFiles = FilesUtil.listFiles(path);
        ArrayList<String> listFolders = FilesUtil.listFolders(path);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(path);
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        for (String folderName : listFolders) {
            rootNode.add(new DefaultMutableTreeNode(folderName));
        }
        for (String fileName : listFiles) {
            rootNode.add(new DefaultMutableTreeNode(fileName, false));
        }
        jTree.setModel(model);
    }
    
    public void showJTreeClientFilesFolders(JTree jTree, DefaultMutableTreeNode parentNode, File[] files) {
        // NOTE: rootNode reference to grand parent node (parentNode.getParent)
        // parent's rootNode reference to parent's granParent ====> IMPORTANT!!!
        // Hence we recursion so we need parent's rootNode has reference
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        DefaultMutableTreeNode grandNode = (DefaultMutableTreeNode) parentNode.getParent();
        if (grandNode != null) {
            rootNode.setUserObject(grandNode.getUserObject());
            
            // NOTE: IMPORTANT IS HERE!!!!!!!!!!!
            rootNode.setParent((MutableTreeNode) grandNode.getParent());
        }
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        
        Object obj = parentNode.getUserObject();
        if (obj instanceof FileNode) {
            rootNode.add(new DefaultMutableTreeNode((FileNode) obj));
        }
        
        for (File f : files) {
            if (f.isFile()) {
                rootNode.add(new DefaultMutableTreeNode(new FileNode(f), false));
            } else {
                rootNode.add(new DefaultMutableTreeNode(new FileNode(f)));
            }
        }
        jTree.setModel(model);
    }
    
    // NOTE: Handle jTreeFilesFoldersClient expand folder
    public void showJTreeFilesFoldersByFilesFolders(DefaultMutableTreeNode curNode, File[] files) {
        FileNode fNode = getFileNode(curNode);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fNode.getFile().getPath());
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        
        for (File f : files) {
            if (f.isFile()) {
                rootNode.add(new DefaultMutableTreeNode(new FileNode(f), false));
            } else {
                rootNode.add(new DefaultMutableTreeNode(new FileNode(f)));
            }
        }
        
        treeFilesFolders.setModel(model);
    }
    
    public void initJTreeClientDirs() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Computer");
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        treeDirs.addTreeExpansionListener(new DirsExpansionListener(model));
        
        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k = 0; k < roots.length; k++) {
            node = new DefaultMutableTreeNode(new FileNode(roots[k]));
            rootNode.add(node);
            node.add(new DefaultMutableTreeNode(new String(CONFIG.RETRIEVING_DATA)));
        }
        
        treeDirs.setModel(model);
    }
    
    public void initJTreeFilesFoldersClient() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Computer");
        DefaultTreeModel model = new DefaultTreeModel(rootNode, true);
        treeFilesFolders.addTreeExpansionListener(new FilesFoldersExpansionListener(model));
        treeFilesFolders.addTreeSelectionListener(handleTreeFilesFoldersSelection);

        File[] files;
        files = File.listRoots();
        for (File f : files) { rootNode.add(new DefaultMutableTreeNode(new FileNode(f))); }
        treeFilesFolders.setModel(model);
        
        if (!CONFIG.DEBUG) {
            treeFilesFolders.expandRow(0);
            treeFilesFolders.setRootVisible(false);
        }
    }
    
    DefaultMutableTreeNode getTreeNode(TreePath path) {
        return (DefaultMutableTreeNode) (path.getLastPathComponent());
    }

    FileNode getFileNode(DefaultMutableTreeNode node) {
        if (node == null) { return null; }
        Object obj = node.getUserObject();
        if (obj instanceof FileNode) {
            return (FileNode) obj;
        } else {
            return null;
        }
    }
    
    public TreeSelectionListener handleTreeFilesFoldersSelection = new TreeSelectionListener() {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = getTreeNode(e.getPath());
            FileNode f = getFileNode(node);
            if (f != null) {
                txtPathClient.setText(f.getFile().getPath());
                CONFIG.print(f.getFile().getPath());
            }
        }
        
    };
    
    class FilesFoldersExpansionListener implements TreeExpansionListener {
        DefaultTreeModel m_model;

        public FilesFoldersExpansionListener(DefaultTreeModel m_model) {
            this.m_model = m_model;
        }
        
        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            final DefaultMutableTreeNode node = getTreeNode(event.getPath());
            final FileNode fnode = getFileNode(node);
            
            // TODO: Show files + folder at treeFoldersClient
            File[] files = null;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            if (fnode != null) { files = fnode.listFiles(); }
            else { files = File.listRoots(); }
//            showJTreeClientFilesFolders(treeFilesFolders, parentNode, files);
            showJTreeFilesFoldersByFilesFolders(node, files);

            Thread runner = new Thread() {
                @Override
                public void run() {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            m_model.reload(node);
                        }
                    };
                    SwingUtilities.invokeLater(runnable);
                }
            };
            runner.start();
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class DirsExpansionListener implements TreeExpansionListener {
        DefaultTreeModel m_model;

        public DirsExpansionListener(DefaultTreeModel m_model) {
            this.m_model = m_model;
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            final DefaultMutableTreeNode node = getTreeNode(event.getPath());
            final FileNode fnode = getFileNode(node);
            
            // TODO: Show files + folder at treeFoldersClient
            File[] files = fnode.listFiles();
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            showJTreeFilesFoldersByFilesFolders(node, files);

            Thread runner = new Thread() {
                @Override
                public void run() {
                    if (fnode != null && fnode.expand(node)) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                m_model.reload(node);
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            };
            runner.start();
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }
}
