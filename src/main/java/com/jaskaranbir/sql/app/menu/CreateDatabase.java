package com.jaskaranbir.sql.app.menu;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Connection;

public class CreateDatabase {
	public CreateDatabase(Connection con){
		String dbName = JOptionPane.showInputDialog(null, "Enter Database Name",
				"Create new Database Dialog", JOptionPane.PLAIN_MESSAGE);
		if(!dbName.equals("null"))
		try {
			con.createStatement().executeUpdate("Create database " + dbName);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
