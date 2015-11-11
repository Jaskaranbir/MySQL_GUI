package com.jaskaranbir.sql.app.menu;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.mysql.jdbc.Connection;

public class RenameColumn {
	private RenameColumn(Connection con, JTableHeader tableHeader, int curSelColPoint, String tableName) {
		JPopupMenu colRenPopup = new JPopupMenu();
		JButton propEditor = new JButton("Edit Properties");
		JTextField colNameStore = new JTextField(); // Stores the new column name
		TableColumn curSelCol; // Currently Selected Column

		if (curSelColPoint != -1) {
			// Create base panel (appears on clicking table header)
			Rectangle columnRectangle = tableHeader.getHeaderRect(curSelColPoint);
			curSelCol = tableHeader.getColumnModel().getColumn(curSelColPoint);
			JPanel basePanel = new JPanel();
			basePanel.setLayout(new GridLayout(2, 1));
			basePanel.add(colNameStore);
			basePanel.add(propEditor);
			colRenPopup.setPreferredSize(new Dimension(columnRectangle.width, columnRectangle.height + 45));
			colRenPopup.show(tableHeader, columnRectangle.x, columnRectangle.y + 20);
			colNameStore.requestFocusInWindow();

			// Selected column name from table
			String prevValue = (String) curSelCol.getHeaderValue();

			// For detailed editing (changing data type/size etc)
			propEditor.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						// Panel for getting input for data type and size
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

						String[] options = { "Rename Column", "Cancel" };
						int x = JOptionPane.showOptionDialog(null, colPanel, "Column Name Dialog",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, colSizeField);
						if (x != 0)
							return;

						int index = dataType.getSelectedIndex();

						if (index != 1 && index != 3)
							con.createStatement()
									.executeUpdate("Alter table " + tableName + " change column " + prevValue + " "
											+ colNameStore.getText() + " "
											+ dataType.getSelectedItem().toString().trim() + "("
											+ colSizeField.getText() + ")");

						// Its "varchar" and not "Variable Char"
						// So we cant get value of selected item
						else if (index == 1)
							con.createStatement()
									.executeUpdate("Alter table " + tableName + " change column " + prevValue + " "
											+ colNameStore.getText() + " varchar" + "(" + colSizeField.getText() + ")");

						// Date type doesn't require size
						else
							con.createStatement()
									.executeUpdate("Alter table " + tableName + " change column " + prevValue + " "
											+ colNameStore.getText() + " " + dataType.getSelectedItem().toString());
						curSelCol.setHeaderValue(colName.getText());
						tableHeader.repaint();
						colRenPopup.setVisible(false);
					} catch (SQLException exc) {
						// Should set previous header value if some exception occurs
						curSelCol.setHeaderValue(prevValue);
						tableHeader.repaint();
						JOptionPane.showMessageDialog(null, exc.getMessage());
					}
				}
			});

			// If user just wants to rename column
			// Auto get data type and size
			colNameStore.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						ResultSet rs = con.createStatement().executeQuery("desc " + tableName);

						// Move ResultSet to the column selected in table
						rs.absolute(curSelColPoint + 1);

						// Get Data Type
						con.createStatement().executeUpdate("Alter table " + tableName + " change column " + prevValue
								+ " " + colNameStore.getText() + " " + rs.getString(2));

						curSelCol.setHeaderValue(colNameStore.getText());
						tableHeader.repaint();
						colRenPopup.setVisible(false);
					} catch (Exception exc) {
						curSelCol.setHeaderValue(prevValue);
						tableHeader.repaint();
						JOptionPane.showMessageDialog(null, exc.getMessage());
					}
				}
			});

		}
	}
}
