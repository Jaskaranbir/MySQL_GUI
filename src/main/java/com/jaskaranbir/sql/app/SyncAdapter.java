package com.jaskaranbir.sql.app;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.mysql.jdbc.Connection;

public class SyncAdapter {
	private SyncAdapter(Connection con, JTable table, String tableName, String refCol) {
		try {
			int row = table.getEditingRow();
			int col = table.getEditingColumn();
			int rowcount = table.getRowCount();
			int colcount = table.getColumnCount();

			con.createStatement().executeUpdate("Alter table " + tableName + " add  " + refCol + " int(30)");

			// Create the reference column
			for (int i = 0; i < rowcount; i++)
				con.createStatement().executeUpdate("update " + tableName + " set " + refCol + " = \"" + i + "\" where "
						+ refCol + " is null limit 1");

			// Check if the person is trying to create a new row
			// This works by checking max value of reference column (refCol)
			// If current row in edit > refCol, that means user is trying to
			// create a new row.
			int nullCheck = 0;
			String updateCell, values = "";
			String curValue;
			ResultSet rs = con.createStatement().executeQuery("select max(" + refCol + ") from " + tableName);

			// Helps to create relation with current database in MySQL and in
			// our table's editing row
			String refNumber = "";

			boolean isEmptyTable = false;

			// Throws NPE if table has empty row set in which case we need to
			// insert row
			try {
				while (rs.next())
					refNumber = rs.getString("max(" + refCol + ")");
				nullCheck = Integer.parseInt(refNumber);
			} catch (Exception e) {
				isEmptyTable = true;
			}

			if (nullCheck == (row - 1) || isEmptyTable) {

				// Form the centre of "insert into" table statement
				for (int i = 0; i < colcount; i++) {
					try {
						curValue = table.getModel().getValueAt(row, i).toString();
					} catch (IndexOutOfBoundsException | NullPointerException exc) {
						curValue = "NULL";
					}
					if (curValue.equals("") || curValue.equals(""))
						curValue = "NULL";
					else if (!curValue.equals("NULL"))
						curValue = "\"" + curValue + "\"";
					values += curValue + ",";
					curValue = "";
				}
				values += (nullCheck + 1);
				updateCell = "insert into " + tableName + " values(" + values + ")";

				// Else update data normally if there is no need to insert new
				// row
			} else
				updateCell = "Update " + tableName + " set " + table.getColumnName(col) + " = \""
						+ table.getValueAt(row, col) + "\" where " + refCol + " = " + row;
			
			con.createStatement().executeUpdate(updateCell);
			con.createStatement().executeUpdate("alter table " + tableName + " drop column " + refCol);
		} catch (SQLException exc) {
			try {
				con.createStatement().executeUpdate("alter table " + tableName + " drop column " + refCol);
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, exc.getMessage());
			}
			JOptionPane.showMessageDialog(null, exc.getMessage());
			exc.printStackTrace();
		}
	}
}
