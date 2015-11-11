package com.jaskaranbir.sql.app;

import java.sql.ResultSet;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import com.mysql.jdbc.Connection;

public class TreePopulate {

	public TreePopulate(Connection con, DefaultTreeModel treeModel, DefaultMutableTreeNode root) {
		root.removeAllChildren();
		try {
			ResultSet rsDB = con.createStatement().executeQuery("show databases");
			int index = 0;
			while (rsDB.next()) {
				String DB = rsDB.getString("Database");
				root.insert(new DefaultMutableTreeNode(DB), index);
				con.createStatement().executeQuery("use " + DB);
				ResultSet rsTable = con.createStatement().executeQuery("show tables");
				int tableIndex = 0;
				while (rsTable.next())
					treeModel.insertNodeInto(new DefaultMutableTreeNode(rsTable.getString("Tables_in_" + DB)),
							(MutableTreeNode) root.getChildAt(index), tableIndex);
				
				index++;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}

}
