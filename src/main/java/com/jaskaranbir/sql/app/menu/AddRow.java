package com.jaskaranbir.sql.app.menu;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AddRow {
	private AddRow(DefaultTableModel tableModel) {
		// Just add rows to table
		// Class SQLSync handles the way rows are to be inserted or edited
		String rowCount = JOptionPane.showInputDialog("Enter number of rows to create:");
		if (rowCount == null)
			return;
		int x = Integer.parseInt(rowCount);
		if (x > 10) {
			JOptionPane.showMessageDialog(null, "Maximum empty rows recommended at a time are 10");
			return;
		}
		for (int i = 0; i < x; i++)
			tableModel.addRow(new Object[] { "" });
	}
}
