package com.ursful.framework.tool.ui;
import com.ursful.framework.tool.Tool;
import com.ursful.framework.tool.checkbox.CheckBoxTreeCellRenderer;
import com.ursful.framework.tool.checkbox.CheckBoxTreeNode;
import com.ursful.framework.tool.checkbox.CheckBoxTreeNodeSelectionListener;
import com.ursful.framework.tool.db.DBUtil;
import com.ursful.framework.tool.db.Information;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class TablePanel2 extends JPanel{

    private CheckBoxTreeNode root = new CheckBoxTreeNode("ALL");

    public List<String> getNodes(){
        Enumeration<CheckBoxTreeNode> nodes = root.children();
        List<String> tmp = new ArrayList<String>();
        while(nodes.hasMoreElements()){
            CheckBoxTreeNode node = nodes.nextElement();
            if(node.isSelected()){
                tmp.add(node.getUserObject().toString());
            }
        }
        return tmp;
    }

    private DefaultTreeModel model = new DefaultTreeModel(root);

    public void reload(Information info, String db){
        if (info != null){
            List<String> tables = DBUtil.getTables(info, db);
            root.removeAllChildren();
            for(String table : tables){
                root.add(new CheckBoxTreeNode(table));
            }
            model.reload();
        }
    }





	public TablePanel2(){
		this.setLayout(new BorderLayout());
		JTree tree = new JTree();
        tree.setBackground(Color.white);

        //CheckBoxTreeNode node1 = new CheckBoxTreeNode("node_1");


        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener());
        tree.setModel(model);  
        tree.setCellRenderer(new CheckBoxTreeCellRenderer());
        JScrollPane scroll = new JScrollPane(tree);  
        this.add(scroll, BorderLayout.CENTER);  
	}
	
}
