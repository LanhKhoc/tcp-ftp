/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Admin
 */
import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

class TreeNodeVector<E> extends Vector<E> {
  String name;

  TreeNodeVector(String name) {
    this.name = name;
  }

  TreeNodeVector(String name, E elements[]) {
    this.name = name;
    for (int i = 0, n = elements.length; i < n; i++) {
      add(elements[i]);
    }
  }

  public String toString() {
    return "[" + name + "]";
  }
}

public class ScrollTree {
  public static void main(final String args[]) {
    JFrame frame = new JFrame("JTreeSample");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Vector<String> v1 = new TreeNodeVector<String>("Two", new String[] { "Mercury", "Venus",
        "Mars" });
    Vector<Object> v2 = new TreeNodeVector<Object>("Three");
    v2.add(System.getProperties());
    v2.add(v1);
    Object rootNodes[] = {v1, v2 };
    Vector<Object> rootVector = new TreeNodeVector<Object>("Root", rootNodes);
    JTree tree = new JTree(rootVector);
    frame.add(new JScrollPane(tree), BorderLayout.CENTER);

//    tree.expandRow(1);
//    tree.scrollRowToVisible(2);
    
    tree.revalidate();
    
    frame.setSize(300, 300);
    frame.setVisible(true);
    
  }
}
