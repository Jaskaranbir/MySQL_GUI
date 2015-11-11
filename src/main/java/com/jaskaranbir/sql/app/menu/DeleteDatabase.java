package com.jaskaranbir.sql.app.menu;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;

public class DeleteDatabase {
	public DeleteDatabase(Connection con, ArrayList<String> DBList){
		int confirm = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to delete the database(s) " + "\"" + DBList + "\""
						+ "? Changes cannot be reverted.",
				"Delete Database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (confirm == 0)
			try {
				for(String database : DBList)
				con.createStatement().executeUpdate("drop database " + database);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
			}
		}
}
