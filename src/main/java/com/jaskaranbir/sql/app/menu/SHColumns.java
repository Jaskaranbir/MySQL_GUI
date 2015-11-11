package com.jaskaranbir.sql.app.menu;

import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.Connection;

public class SHColumns {
	private SHColumns(Connection con, String tableName, DefaultTableModel tableModel) {
		JList<String> colList = new JList<String>();
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		colList.setModel(listModel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(colList);
		ResultSet rs;
		// Get columns in table
		try {
			rs = con.createStatement().executeQuery("desc " + tableName);
			while (rs.next())
				listModel.addElement(rs.getString("Field"));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		Object[] jOptions = { "Apply changes", "Cancel" };
		int shChoice = JOptionPane.showOptionDialog(null, new Component[] {new JLabel("Select tables to show:"),scrollPane}, "Column Filter Dialog", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, jOptions, jOptions[0]);
		if (shChoice != 0)
			return;

		String tableExp = "";
		ArrayList<String> selectedCols = new ArrayList<String>();
		
		// Empty out existing table model to fill in new stuff
		tableModel.setRowCount(0);
		tableModel.setColumnCount(0);
		
		// Form the table expression
		for (String x : colList.getSelectedValuesList()){
			tableExp += x + ",";
			selectedCols.add(x);
			tableModel.addColumn(x);
		}
		tableExp = tableExp.substring(0, (tableExp.length() - 1));

		try {
			int row = 0, col;
			rs = con.createStatement().executeQuery("select " + tableExp + " from " + tableName);

				for (col = 0; col < selectedCols.size(); col++) {
					JOptionPane.showMessageDialog(null, "select " + tableExp + " from " + tableName);
					rs = con.createStatement().executeQuery("select " + tableExp + " from " + tableName);
					JOptionPane.showMessageDialog(null, "fin");
					row = 0;
					// Add empty rows so data can be set in them.
					// Don't want to add any more rows than required, hence add
					// rows
					// only till first column is finished
					while (rs.next()) {
						if (col == 0)
							tableModel.addRow(new Object[] {});
						tableModel.setValueAt(rs.getString(tableModel.getColumnName(col)), row, col);
						row++;
					}
				}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
