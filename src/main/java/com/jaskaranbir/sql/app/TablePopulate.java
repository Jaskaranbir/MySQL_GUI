package com.jaskaranbir.sql.app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.mysql.jdbc.Connection;

public class TablePopulate implements ApplicationContextAware {
	private String refCol; // This column is used as reference when editing data
							// in table cell. The value should be something
							// which isn't a column name

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		applicationContext.getBean("setRefCol", refCol);
	}

	// curDB contains database name and table name with index locations varying
	// on how user makes selection in tree
	private TablePopulate(Connection con, DefaultTableModel tableModel, String[] curDB) {
		tableModel.setRowCount(0);
		tableModel.setColumnCount(0);
		refCol = "ref"; // Assign a starting value. We change it dynamically if
						// it clashes with some column's name
		try {
			populate(con, tableModel, curDB);
		} catch (SQLException e) {
			// Need to switch databases if user switches absurdly to another
			// tree node

			try {
				// If use just clicks on database name and comes back to same
				// child nodes.
				con.createStatement().executeQuery("use " + curDB[0]);
			} catch (SQLException e1) {
				// Switch databases if user selects a table in completely
				// another database. Populate table after switching to database

				String parentDB = curDB[1].split(" ")[1];
				try {
					// Switch database and populate again
					con.createStatement().executeQuery("use " + parentDB.substring(0, parentDB.length() - 1));
					populate(con, tableModel, curDB);
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null, e2.getMessage());
				}
			}
		}
	}

	private void populate(Connection con, DefaultTableModel tableModel, String[] curDB) throws SQLException {
		ResultSet rs = con.createStatement().executeQuery("desc " + curDB[0]);
		// Just to get the column names/number easier
		ArrayList<String> colList = new ArrayList<String>();
		while (rs.next()) {
			String col = rs.getString("Field");
			tableModel.addColumn(col);
			// Don't want the values of columns to clash. refCol must always
			// have a unique value. Hence keep adding random stuff to it
			// until its unique
			if (col.equals(refCol)) {
				refCol += new Random().nextInt(10);
			}
			colList.add(col);
		}

		// Add data to table
		int row = 0, col;
		for (col = 0; col < colList.size(); col++) {
			rs = con.createStatement().executeQuery("select * from " + curDB[0]);
			row = 0;
			while (rs.next()) {
				// Add empty rows so data can be set in them.
				// Dont want to add any more rows than required, hence add rows
				// only till first column is finished
				if (col == 0)
					tableModel.addRow(new Object[] {});

				tableModel.setValueAt(rs.getString(colList.get(col)), row, col);
				row++;
			}
		}

		// Lets give the user some additional empty rows to work
		// with
		for (int i = 0; i < 20; i++)
			tableModel.addRow(new Object[] { "" });
	}

}
