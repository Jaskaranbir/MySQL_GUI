package com.jaskaranbir.sql.app.menu;

import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.mysql.jdbc.Connection;

public class DeleteColumn {
	private DeleteColumn(Connection con, JTable table,int selectedTableCol, String tableName){
		String colDat = "";
			colDat += table.getColumnName(selectedTableCol);
		int x = JOptionPane.showConfirmDialog(null, "Delete table column(s):\n" + colDat + "?",
				"Column Deletion Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (x != 0)
			return;
			try {
				con.createStatement().executeUpdate("alter table " + tableName + " drop column " + table.getColumnName(selectedTableCol));
				table.removeColumn(table.getColumn(table.getColumnName(selectedTableCol)));
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
	}
}
