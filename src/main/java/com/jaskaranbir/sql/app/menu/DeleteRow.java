package com.jaskaranbir.sql.app.menu;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.Connection;

public class DeleteRow {

	// A controller for terminating thread when operation is done
	private boolean areRowsDeleted;

	// refCol is used for reference. It will always have a unique value that
	// never clashes with any column name. Allows to delete a specific row even
	// if multiple rows have same value in table and there is just one column

	// table -> The table from user Interface
	// tableName-> Table name from sql database

	// activeRows array contains indexes of selected rows and the column and
	// row count of table as last two elements

	// Good luck trying to reduce this class' dependencies without messing up
	// array
	private DeleteRow(Connection con, JTable table, ArrayList<Integer> activeRows, String tableName, String refCol) {
		areRowsDeleted = false;
		try {
			int arrSize = activeRows.size();
			int selectedRowsCount = arrSize - 2;
			int rowCount = activeRows.get(arrSize - 1);
			int colCount = activeRows.get(arrSize - 2);
			String rowDat = "";

			for (int x = 0; x < selectedRowsCount; x++) {
				for (int i = 0; i < colCount; i++){
					rowDat += table.getValueAt(activeRows.get(x), i) + " | ";
				}
				rowDat += "\n";
			}

			int x = JOptionPane.showConfirmDialog(null, "Delete table row(s):\n" + rowDat + "?",
					"Row Deletion Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (x != 0)
				return;
			con.createStatement().executeUpdate("Alter table " + tableName + " add " + refCol + " int(30)");
			Thread thread = new Thread() {
				public void run() {
					for (int i = 0; !areRowsDeleted && i < rowCount; i++)
						try {
							con.createStatement().executeUpdate("update " + tableName + " set " + refCol + " = \"" + i
									+ "\" where " + refCol + " is null limit 1");
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getMessage());
						}
				}
			};
			thread.start();
			for (int i = 0; i < selectedRowsCount; i++){
				con.createStatement().executeUpdate("delete from " + tableName + " where " + refCol + "=" + activeRows.get(i));
				((DefaultTableModel)table.getModel()).removeRow(activeRows.get(0));
			}
			areRowsDeleted = true;
			con.createStatement().executeUpdate("alter table " + tableName + " drop column " + refCol);
			table.repaint();
		} catch (Exception e1) {
			try {
				con.createStatement().executeUpdate("alter table " + tableName + " drop column " + refCol);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
	}
}
