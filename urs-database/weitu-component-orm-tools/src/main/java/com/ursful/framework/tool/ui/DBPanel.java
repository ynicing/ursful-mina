package com.ursful.framework.tool.ui;

import com.ursful.framework.tool.db.DBUtil;
import com.ursful.framework.tool.db.Information;
import com.ursful.framework.tool.listener.IListener;
import com.ursful.framework.tool.tree.TextRenderer;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

public class DBPanel extends JPanel{

    private IListener listener;


    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    DefaultTreeModel model = new DefaultTreeModel(root);

    JTree tree;

    public void load(Information info){
        List<String> tables =  DBUtil.getDBS(info);

        System.out.println(tables);

        root.removeAllChildren();

        for(String table : tables){
            root.add(new DefaultMutableTreeNode(table));
        }
        model.reload();
    }


	public DBPanel(final IListener listener){
        this.listener = listener;
	 
		this.setLayout(new BorderLayout());


        //root.add(new DefaultMutableTreeNode("a1"));
        //root.add(new DefaultMutableTreeNode("a2"));


        tree = new JTree(model);
        tree.setRootVisible(false);
        tree.setCellRenderer(null);
        tree.setCellRenderer(new TextRenderer());
        tree.setBounds(0,0, this.getWidth(), this.getHeight());


        tree.addTreeSelectionListener(new TreeSelectionListener() {
 
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getLastSelectedPathComponent();
 
                if (node == null)
                    return;
 
                Object object = node.getUserObject();
                if (node.isLeaf()) {
                    //listener.select(object.toString());
                    System.out.println(object);
                }
 
            }
        });
		
        JScrollPane scroll = new JScrollPane(tree);  
        this.add(scroll, BorderLayout.CENTER);  
     
	}
}
