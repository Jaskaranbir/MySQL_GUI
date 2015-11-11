package com.jaskaranbir.sql.app.menu;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mysql.jdbc.Connection;

public class CreateTable {
	private CreateTable(Connection con, String DBName) {
		try {
			ArrayList<String> colList = getTableData();
			if (colList == null)
				return;

			// Form the table name creation string with data types and sizes
			String tab_name = colList.get(0) + "(";
			for (int i = 1; i <= (colList.size() - 1); i++) {
				tab_name += colList.get(i) + ",";
			}
			tab_name = tab_name.substring(0, (tab_name.length() - 1));
			tab_name += ")";
			con.createStatement().execute("use " + DBName);
			con.createStatement().execute("Create table " + tab_name);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	private ArrayList<String> getTableData() {
		try {
			// Create a new form that will hold table name and start processes
			JPanel tableDialog = new JPanel(new GridLayout(1, 2));
			tableDialog.add(new JLabel("Enter Table Name"));
			JTextField tableNameField = new JTextField();
			tableDialog.add(tableNameField);

			// This is the final array thats returned. Contains table name at
			// index 0 and columns data at subsequent indexes
			ArrayList<String> tempString = new ArrayList<String>();
			String[] options = { "Next", "Cancel" };
			int x = JOptionPane.showOptionDialog(null, tableDialog, "Create new Table", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, tableNameField);
			if (x == 1)
				return null;
			tableDialog.removeAll();
			String[] options2 = { "Create Table", "Cancel" };
			JScrollPane colScrollPane = new JScrollPane();

			// List that holds column names, types, size etc for new table
			JList<String> colList = new JList<String>();
			colScrollPane.setViewportView(colList);

			// Model for previous list
			DefaultListModel<String> columnHolder = new DefaultListModel<String>();
			colList.setModel(columnHolder);
			JButton addCol = new JButton("+");
			JButton remCol = new JButton("-");
			tableDialog.add(colScrollPane);
			tableDialog.add(addCol);
			tableDialog.add(remCol);

			remCol.setEnabled(false);
			x = JOptionPane.showOptionDialog(null, tableDialog, "Create new Table", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);
			if (x == 1)
				return null;
			tempString.add(0, tableNameField.getText());
			for (int i = 0; i <= (columnHolder.getSize() - 1); i++) {
				tempString.add(i + 1, columnHolder.getElementAt(i).toString());
			}

			// The action for "+" button (add column)
			addCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Create a new panel which has ui for adding/removing
					// columns
					JPanel colIdentifier = new JPanel(new GridLayout(3, 3));
					JComboBox<String> dataType = new JComboBox<String>();
					JTextField colListField = new JTextField();
					dataType.addItem("Char");
					dataType.addItem("Variable Char");
					dataType.addItem("Int");
					dataType.addItem("Date");
					colIdentifier.add(new JLabel("Enter Column Name:"));
					colIdentifier.add(colListField);
					colIdentifier.add(new JLabel("Enter Data Type:"));
					colIdentifier.add(dataType);
					colIdentifier.add(new JLabel("Enter Data Size:"));
					JTextField dataSize = new JTextField();
					colIdentifier.add(dataSize);
					int type = dataType.getSelectedIndex();
					String size, typeString = "";

					dataType.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							// Disable the "size" TextField when "date" type
							// is selected
							int selectedType = dataType.getSelectedIndex();
							if (selectedType > 2) {
								dataSize.setEnabled(false);
								dataSize.setEditable(false);
							} else {
								dataSize.setEnabled(true);
								dataSize.setEditable(true);
							}
						}
					});
					String[] options = { "Add Column", "Cancel" };
					int x = JOptionPane.showOptionDialog(null, colIdentifier, "Column Name Dialog",
							JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, colListField);
					if (x == 0)
						if (type != 3) {
							typeString = dataType.getModel().getSelectedItem().toString();
							dataSize.setEnabled(true);
							if (typeString.equals("Variable Char"))
								typeString = "varchar";
							size = dataSize.getText();
							if (size == null)
								return;
							columnHolder.addElement(colListField.getText() + " " + typeString + "(" + size + ")");
						} else
							// Since we dont need to specify size for
							// date
							columnHolder.addElement(colListField.getText() + " " + typeString);
				}
			});

			// Disable "-" (remove column) button if nothing has been selected
			// on column list
			colList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (colList.getSelectedIndex() == -1)
						remCol.setEnabled(false);
					else {
						remCol.setEnabled(true);
					}
				}
			});

			// The action for "-" button (remove selected column)
			remCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					columnHolder.removeElementAt(colList.getSelectedIndex());
				}
			});
			return tempString;
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}