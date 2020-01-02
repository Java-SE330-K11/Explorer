/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer;

import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author DungHK
 */
class TreeNodeRender extends DefaultTreeCellRenderer{
        private JLabel label;

        TreeNodeRender() {
            label = new JLabel();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            JLabel label=new JLabel();
            
            File[] mypaths=java.io.File.listRoots();
            String oC="/explorer/image/oC.png";
            String oK="/explorer/image/oK.png";
            ImageIcon imgC=new ImageIcon(getClass().getResource(oC));
            ImageIcon imgK=new ImageIcon(getClass().getResource(oK));
                
            if (o instanceof String) {
                String str = (String) o;
                                   
                if(str.length()==3)
                {
                    label.setText(str);
                    if(str.equals(mypaths[0].getAbsolutePath()))
                    label.setIcon(imgC);
                    else
                        label.setIcon(imgK);
                }
                        
                    else
                {
                    label.setText(new File(str).getName());
                    if(!str.equals("ThisPC"))
                    //label.setIcon(new ImageIcon("..\\icons\\ThisPC.ico"));
                    //else
                        label.setIcon(new ImageIcon(getClass().getResource("/explorer/image/folder.png")));
                    else
                        label.setIcon(new ImageIcon(getClass().getResource("/explorer/image/ico16.png")));
                }
                                          
                }
            return label;
        }
    }
