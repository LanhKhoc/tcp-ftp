package util;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TestFileTree extends JPanel {

    private File projectFile;

    public TestFileTree(File dir) {
        //Create file explorer
        //Need to add setup for root folder change.
        //Config.getProject(); //This gets the current file from the config file.
        //Begin choose File
        if (projectFile == null) {
            JFileChooser chooser;
            String choosertitle = "Please Choose a Root Folder";
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle(choosertitle);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //
            // disable the "All files" option.
            //
            chooser.setAcceptAllFileFilterUsed(false);
            //    
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                projectFile = new File(chooser.getSelectedFile().getAbsolutePath());
                //projectFile = chooser.getSelectedFile();
            } else {
                System.out.println("No Selection ");
            }
        } else {
            // Figure out where in the filesystem to start displaying
        }

        //End choose file
        setLayout(new BorderLayout());

        // Make a tree list with all the nodes, and make it a JTree
        JTree tree = new JTree(addNodes(null, projectFile));
        tree.setCellRenderer(new MyTreeCellRenderer());

        // Add a listener
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                        .getPath().getLastPathComponent();
                System.out.println("You selected " + node);
            }
        });

        // Lastly, put the JTree into a JScrollPane.
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        add(BorderLayout.CENTER, scrollpane);
    }

    /**
     * Add nodes from under "dir" into curTop. Highly recursive.
     */
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(dir);
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }
        File[] tmp = dir.listFiles();
        Vector<File> ol = new Vector<File>();
        ol.addAll(Arrays.asList(tmp));
        Collections.sort(ol, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {

                int result = o1.getName().compareTo(o2.getName());

                if (o1.isDirectory() && o2.isFile()) {
                    result = -1;
                } else if (o2.isDirectory() && o1.isFile()) {
                    result = 1;
                }

                return result;
            }
        });
        // Pass two: for files.
        for (int fnum = 0; fnum < ol.size(); fnum++) {
            File file = ol.elementAt(fnum);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
            if (file.isDirectory()) {
                addNodes(node, file);
            }
            curDir.add(node);
        }
        return curDir;
    }

    public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }

    public static void main(String[] av) {

        JFrame frame = new JFrame("FileTree");
        frame.setForeground(Color.black);
        frame.setBackground(Color.lightGray);
        Container cp = frame.getContentPane();

        if (av.length == 0) {
            cp.add(new TestFileTree(new File(".")));
        } else {
            cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
            for (int i = 0; i < av.length; i++) {
                cp.add(new TestFileTree(new File(av[i])));
            }
        }

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        private FileSystemView fsv = FileSystemView.getFileSystemView();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            System.out.println(value);
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                value = ((DefaultMutableTreeNode)value).getUserObject();
                if (value instanceof File) {
                    File file = (File) value;
                    if (file.isFile()) {
                        setIcon(fsv.getSystemIcon(file));
                        setText(file.getPath());
                    } else {
                        setIcon(fsv.getSystemIcon(file));
                        setText(file.getName());
                    }
                }
            }
            return this;
        }
    }
}