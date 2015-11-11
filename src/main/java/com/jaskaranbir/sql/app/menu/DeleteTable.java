package com.jaskaranbir.sql.app.menu;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;

public class DeleteTable {
	private DeleteTable(Connection con, ArrayList<String> tableNames) {
		String tableDBRel = "";
		for(int i=0; i<tableNames.size(); i+=2){
			tableDBRel += tableNames.get(i) + " -> " + tableNames.get(i+1) + "\n";
		}
		int confirm = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to delete the table(s) " + "\n" + tableDBRel + "? Changes cannot be reverted.",
				"Delete Table", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (confirm == 1)
			return;
		try {
			for (int i =0; i<tableNames.size(); i+=2){
				con.createStatement().executeQuery("use " + tableNames.get(i));
				con.createStatement().executeUpdate("drop table " + tableNames.get(i+1));
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
	}
}
