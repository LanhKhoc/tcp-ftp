package gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import vendor.CONFIG;

/**
 *
 * @author Admin
 */
public class FileNode {

    protected File m_file;

    public FileNode(File file) { m_file = file; } 
    public FileNode() {}

    public File getFile() {
        return m_file;
    }

    public String toString() {
        return m_file.getName().length() > 0 ? m_file.getName()
                : m_file.getPath();
    }

    public boolean expand(DefaultMutableTreeNode parent) {
        // NOTE: Folder without child
        if (parent.getChildCount() == 0) { return false; }
        
        DefaultMutableTreeNode flag = (DefaultMutableTreeNode) parent.getFirstChild();
        if (flag == null) { return false; }
        Object obj = flag.getUserObject();
        
        // NOTE: Retrieving
        if ((obj instanceof String) == false || obj.equals(CONFIG.RETRIEVING_DATA) == false) {
            return false;      
        }
        
        // NOTE: Remove flag: CONFIG.RETRIEVING_DATA
        parent.removeAllChildren();

        File[] files = listFiles();
        if (files == null) { return true; }
        Vector v = new Vector();

        for (int k = 0; k < files.length; k++) {
            File f = files[k];
            if (!(f.isDirectory())) { continue; }
            
            FileNode newNode = new FileNode(f);
            boolean isAdded = false;
            
            // NOTE: Sort follow alphabet
            for (int i = 0; i < v.size(); i++) {
                FileNode nd = (FileNode) v.elementAt(i);
                if (newNode.compareTo(nd) < 0) {
                    v.insertElementAt(newNode, i);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) { v.addElement(newNode); }
        }

        for (int i = 0; i < v.size(); i++) {
            FileNode nd = (FileNode) v.elementAt(i);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nd);
            parent.add(node);

            if (nd.hasSubDirs()) {
                node.add(new DefaultMutableTreeNode(new String(CONFIG.RETRIEVING_DATA)));
            }
        }

        return true;
    }

    public boolean hasSubDirs() {
        File[] files = listFiles();
        if (files == null) { return false; }
        for (int k = 0; k < files.length; k++) {
            if (files[k].isDirectory()) { return true; }
        }
        return false;
    }

    public int compareTo(FileNode toCompare) {
        return m_file.getName().compareToIgnoreCase(toCompare.m_file.getName());
    }

    public File[] listFiles() {
        if (!m_file.isDirectory()) { return null; }
        try {
            return m_file.listFiles();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error reading directory " + m_file.getAbsolutePath(),
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
