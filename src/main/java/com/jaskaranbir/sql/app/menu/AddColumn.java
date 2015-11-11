package com.jaskaranbir.sql.app.menu;

import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.Connection;

public class AddColumn {
	private AddColumn(Connection con, DefaultTableModel tableModel, String tableName) {
		try {
			// Create a new dialog for getting column data
			JPanel colPanel = new JPanel();
			colPanel.setLayout(new GridLayout(3, 2));
			colPanel.add(new JLabel("Enter column name: "));
			JTextField colName = new JTextField();
			colPanel.add(colName);
			JComboBox<String> dataType = new JComboBox<String>();
			JTextField colSizeField = new JTextField();
			dataType.addItem("Char");
			dataType.addItem("Variable Char");
			dataType.addItem("Int");
			dataType.addItem("Date");
			colPanel.add(new JLabel("Select Data Type:"));
			colPanel.add(dataType);
			colPanel.add(new JLabel("Enter Column Size:"));
			colPanel.add(colSizeField);

			String[] options = { "Add Column", "Cancel" };
			int x = JOptionPane.showOptionDialog(null, colPanel, "New Column Dialog", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, colSizeField);
			if (x != 0)
				return;

			int index = dataType.getSelectedIndex();

			if (index != 1 && index != 3)
				con.createStatement().executeUpdate("alter table " + tableName + " add column " + colName.getText()
						+ " " + dataType.getSelectedItem().toString().trim() + "(" + colSizeField.getText() + ")");

			// Its "varchar" and not "Variable Char"
			// So we can't get value of selected item
			else if (index == 1)
				con.createStatement().executeUpdate("alter table " + tableName + " add column " + colName.getText()
						+ " varchar" + "(" + colSizeField.getText() + ")");

			// Date type doesn't require size
			else if (index == 3)
				con.createStatement().executeUpdate("alter table " + tableName + " add column " + colName.getText()
						+ " " + dataType.getSelectedItem().toString());

			tableModel.addColumn(colName.getText());
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(null, exc.getMessage());
		}
	}
}
