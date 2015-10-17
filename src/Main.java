import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class Main extends JFrame {

	private static Main frame;

	private static DefaultTableModel tableModel;

	private static DefaultListModel dbListModel;
	private static DefaultListModel tableListModel;
	private static DefaultListModel shModel;

	private static JDialog loginBox;

	private JPanel contentPane;

	private static JTable table;

	private static JList table_list;
	private static JList db_list;
	private static JList shList;

	private static JTextField userName;
	private static JTextField colRenStore;
	private static JPasswordField password;
	private static JTextField port;

	private static JButton loginButton;
	private static JButton exitButton;

	private JCheckBox showTables;
	private JCheckBox hideTables;

	private ButtonGroup shButtonGroup;

	private static String now; // current time
	private static String fieldo; // table name from sql database
	private static String commlog; // commandLog
	private static String operationlog; // operationLog
	private static String tabstruct; // temporary stores tables' infos in string
									// format for manipulation
	private static String tempCol; // temporary column for using in
									// alter-table statements
	private static boolean logallow1, updateauth = false;

	private static Connection con;

	private static TableColumn curSelCol;
	private static JTableHeader tableHeader; 
	
	JPopupMenu colRename;
	
	private String size;
	
	ImageIcon switchWindow = new ImageIcon("assets\\switchL.png");

	private void initComponents() {
		loginBox = new JDialog();
		loginBox.getContentPane().setLayout(new GridLayout(5, 2, 0, 3));
		loginBox.setUndecorated(true);
		loginBox.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
		loginBox.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		loginButton = new JButton("Login");
		exitButton = new JButton("Exit");

		tableModel = new DefaultTableModel();

		dbListModel = new DefaultListModel();
		tableListModel = new DefaultListModel();
		shModel = new DefaultListModel();

		table = new JTable(getTableModel());

		table_list = new JList(getTableListModel());
		db_list = new JList(getdbListModel());
		shList = new JList(getshModel());

		showTables = new JCheckBox("Show selected Tables");
		hideTables = new JCheckBox("Hide selected Tables");

		shButtonGroup = new ButtonGroup();

		userName = new JTextField();
		password = new JPasswordField();
		port = new JTextField();
		colRenStore = new JTextField();
		
		colRename = new JPopupMenu();
		
		tempCol = "temp";
	}

	// <fold>
	public static DefaultTableModel getTableModel() {
		return tableModel;
	}

	public static DefaultListModel getdbListModel() {
		return dbListModel;
	}

	public static DefaultListModel getTableListModel() {
		return tableListModel;
	}

	public static DefaultListModel getshModel() {
		return shModel;
	}// </fold>

	private static void firstRunLogin() {

		Cursor c = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
		userName.setCursor(c);
		password.setCursor(c);
		port.setCursor(c);
		loginBox.getContentPane().add(new JLabel("Username:"));
		loginBox.getContentPane().add(userName);
		loginBox.getContentPane().add(new JLabel("Password:"));
		loginBox.getContentPane().add(password);
		loginBox.getContentPane().add(new JLabel("Port:"));
		loginBox.getContentPane().add(port);
		loginBox.getContentPane().add(new JLabel("(Default Port: 3306)", JLabel.CENTER));
		loginBox.getContentPane().add(loginButton);
		loginBox.getContentPane().add(exitButton);
		loginBox.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		loginBox.setLocation(dim.width / 2 - loginBox.getSize().width / 2,
				dim.height / 2 - loginBox.getSize().height / 2);
		loginBox.setVisible(true);
		userName.requestFocusInWindow();
	}

	// Get current time
	public static void timeget() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		now = (dateFormat.format(cal.getTime()));
	}

	// Get database list
	private static void dbFill(DefaultListModel listModel) {
		try {
			if (logallow1) {
				timeget();
				operationlog += now + ":   "
						+ "/-------------------Basic Pre-Operation instance 2 Begin---------------------- \n \n";
				commlog += now + ":   "
						+ "/-------------------Basic Pre-Operation instance 2 Begin---------------------- \n \n";
			}
			listModel.removeAllElements();
			if (logallow1) {
				timeget();
				operationlog += now + ":   " + "/ Connection to MySQL remote established / \n \n";
			}
			commlog += now + ":   " + "/ Show Databases; / \n \n";
			ResultSet rs = con.createStatement().executeQuery("show databases");
			while (rs.next()) {
				listModel.addElement(rs.getString("Database"));
				if (logallow1) {
					timeget();
					operationlog += now + ":   " + "/ Getting String[ ] \"Database\" / \n \n";
					timeget();
					operationlog += now + ":   " + "/ Adding Item '" + rs.getString("Database") + "' / \n \n";
				}
			}
			if (logallow1) {
				timeget();
				operationlog += now + ":   " + "/ Syncing with List / \n \n";
			}
		} catch (SQLException e) {
			timeget();
			operationlog += now + ":   " + "/ Failed in establishing MySQL remote connection \n Error Message: "
					+ e.getMessage() + " \n SQL state: " + e.getSQLState() + " \n Class: " + e.getClass()
					+ " \n Error Code: " + e.getErrorCode()
					+ "  / \n It is recommended that you DO NOT PROCEED ANY FURTHER \n \n";
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		if (logallow1) {
			timeget();
			operationlog += now + ":   "
					+ "/-------------------Basic Pre-Operation instance 2 End---------------------- \n \n";
			commlog += now + ":   "
					+ "/-------------------Basic Pre-Operation instance 2 End---------------------- \n \n";
		}
		logallow1 = false;
	}

	// Fill table
	private static void tableFill(String colNames, int row, int col, String... pCol) {
		tableHeader = table.getTableHeader();
		if (pCol.length == 1) {
			tabstruct = pCol[0];
		} else {
			tabstruct = "";
		}
		getTableModel().setColumnCount(0);
		getTableModel().setRowCount(0);
		String tableName = (String) table_list.getSelectedValue();

		try {
			Statement stmnt = (Statement) con.createStatement();
			if (tabstruct.equals("")) {
				String query1 = "Use " + (String) db_list.getSelectedValue();
				stmnt.executeQuery(query1);
				String query2 = "desc " + (String) table_list.getSelectedValue();
				ResultSet rs = stmnt.executeQuery(query2);
				while (rs.next()) {
					if (rs.getString("Field") != null) {
						tabstruct += rs.getString("Field") + "\n";
					}
				}
			}

			Scanner scanner = new Scanner(tabstruct);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (pCol.length != 1) {
					getshModel().addElement(line);
				}
				getTableModel().addColumn(line);

				int dupIncrement = 0;
				while (line.equals(tempCol)) {
					tempCol += "" + dupIncrement;
					dupIncrement += 8;
				}

				String tdataget = "Select " + colNames + " from " + tableName;
				ResultSet rs2 = stmnt.executeQuery(tdataget);
				row = 0;
				while (rs2.next()) {
					getTableModel().addRow(new Object[] { "" });
					String field = rs2.getString(line);
					getTableModel().setValueAt(field, row, col);
					row++;
				}
				col++;
				int end = tabstruct.indexOf("\n");
				tabstruct.replace(tabstruct.substring(0, end), "");
			}
			stmnt.close();
		} catch (Exception e) {
			// if (!(e.getMessage().contains("You have an error in your SQL
			// syntax")))
			// JOptionPane.showMessageDialog(null, e.getMessage() + "\n" +
			// tableName);
			// exceptionHolder = e.getMessage();
		}
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Main();
					firstRunLogin();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	public JList<String> createTempDialog(String text) {
		JPanel tempDialog = new JPanel(new GridLayout(1, 2));
		tempDialog.add(new JLabel(text));
		JTextField tempStringContainer = new JTextField();

		tempDialog.add(tempStringContainer);
		JList<String> tempString = new JList<String>();
		DefaultListModel<String> tempModel2 = new DefaultListModel<String>();
		String[] options = { "Next", "Cancel" };
		int x = JOptionPane.showOptionDialog(frame, tempDialog, "Create new Table", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, tempStringContainer);
		if (x == 0) {
			tempDialog.removeAll();
			String[] options2 = { "Create Table", "Cancel" };
			JScrollPane tempScrollPane = new JScrollPane();
			JScrollPane colScrollPane = new JScrollPane();
			JList colName = new JList();
			colScrollPane.setViewportView(colName);
			DefaultListModel tempModel = new DefaultListModel();
			colName.setModel(tempModel);
			// tempScrollPane.add(colName);
			JButton addCol = new JButton("+");
			JButton remCol = new JButton("-");
			// tempDialog.add(tempScrollPane);
			tempDialog.add(colScrollPane);
			tempDialog.add(addCol);
			tempDialog.add(remCol);

			addCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// New window here
					JPanel colIdentifier = new JPanel(new GridLayout(2, 2));
					JComboBox dataType = new JComboBox();
					JTextField colNameField = new JTextField();
					dataType.addItem("Char");
					dataType.addItem("Variable Char");
					dataType.addItem("Int");
					dataType.addItem("Date");
					colIdentifier.add(new JLabel("Enter Column Name:"));
					colIdentifier.add(colNameField);
					colIdentifier.add(new JLabel("Enter Data Type:"));
					colIdentifier.add(dataType);
					String[] options = { "Add Column", "Cancel" };
					int x = JOptionPane.showOptionDialog(frame, colIdentifier, "Column Name Dialog",
							JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, colNameField);
					if (x == 0) {
						int type = dataType.getSelectedIndex();
						String typeString = dataType.getModel().getSelectedItem().toString();
						if (type == 0 || type == 1 || type == 2) {
							size = JOptionPane
									.showInputDialog("Enter size for " + dataType.getSelectedItem().toString());
							if(size==null) return;
							if (typeString.equals("Variable Char"))
								typeString = "varchar";
							tempModel.addElement(colNameField.getText() + " " + typeString + "(" + size + ")");
						} else {
							tempModel.addElement(colNameField.getText() + " " + typeString);
						}
					}
				}
			});
			colName.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (colName.getSelectedIndex() == -1)
						remCol.setEnabled(false);
					else {
						remCol.setEnabled(true);
					}
				}
			});
			remCol.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tempModel.removeElementAt(colName.getSelectedIndex());
				}
			});

			remCol.setEnabled(false);
			x = JOptionPane.showOptionDialog(frame, tempDialog, "Create new Table", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);
			if (x == 0) {
				tempString.setModel(tempModel2);
				tempModel2.add(0, tempStringContainer.getText());
				for (int i = 0; i <= (tempModel.getSize() - 1); i++)
					tempModel2.add(i + 1, tempModel.getElementAt(i).toString());
				return tempString;
			} else
				return null;
		} else
			return null;
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		initComponents();

		tableHeader = table.getTableHeader();
		// <fold>

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 931, 481);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem menu_Exit = new JMenuItem("Exit");
		fileMenu.add(menu_Exit);

		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		JMenuItem menu_Create = new JMenuItem("Create new Database");
		editMenu.add(menu_Create);

		JMenuItem menu_Delete = new JMenuItem("Delete Database " + db_list.getSelectedValue());
		editMenu.add(menu_Delete);
		
		JMenu altTableMenu = new JMenu("Alter-Table");
		menuBar.add(altTableMenu);
		
		JMenuItem menu_addRow = new JMenuItem("Add Row");
		altTableMenu.add(menu_addRow);
		
		JMenuItem menu_addColumn = new JMenuItem("Add Column");
		altTableMenu.add(menu_addColumn);
		
		JMenuItem menu_RemRow = new JMenuItem("Delete Row");
		altTableMenu.add(menu_RemRow);
		
		JMenuItem menu_RemCol = new JMenuItem("Delete Column");
		altTableMenu.add(menu_RemCol);

		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		JMenuItem menu_SHColumns = new JMenuItem("Show/Hide Columns");
		viewMenu.add(menu_SHColumns);

		JMenuItem menu_ViewLog = new JMenuItem("View Log");
		viewMenu.add(menu_ViewLog);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JTabbedPane listTabs = new JTabbedPane(JTabbedPane.TOP);
		listTabs.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		listTabs.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		listTabs.setPreferredSize(new Dimension(263, 158));

		JPanel dbPanel = new JPanel();
		listTabs.addTab("Databases", null, dbPanel, null);

		JScrollPane db_scrollPane = new JScrollPane();
		db_scrollPane.setViewportView(db_list);
		GroupLayout gl_dbPanel = new GroupLayout(dbPanel);
		gl_dbPanel.setHorizontalGroup(gl_dbPanel.createParallelGroup(Alignment.LEADING).addComponent(db_scrollPane,
				GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE));
		gl_dbPanel.setVerticalGroup(gl_dbPanel.createParallelGroup(Alignment.LEADING).addComponent(db_scrollPane,
				GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE));
		dbPanel.setLayout(gl_dbPanel);

		JPanel tablePanel = new JPanel();
		tablePanel.setPreferredSize(new Dimension(263, 158));
		listTabs.addTab("Tables", null, tablePanel, null);

		JScrollPane tables_scrollPane = new JScrollPane();

		tables_scrollPane.setViewportView(table_list);
		GroupLayout gl_tablePanel = new GroupLayout(tablePanel);
		gl_tablePanel.setHorizontalGroup(gl_tablePanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tables_scrollPane, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE));
		gl_tablePanel.setVerticalGroup(gl_tablePanel.createParallelGroup(Alignment.LEADING)
				.addComponent(tables_scrollPane, GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE));
		tablePanel.setLayout(gl_tablePanel);

		JScrollPane table_scrollPane = new JScrollPane();
		table_scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		table_scrollPane.setViewportView(table);

		JButton viewSwitch = new JButton(switchWindow);
		viewSwitch.setToolTipText("Extended Table View");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(
						gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(listTabs, GroupLayout.PREFERRED_SIZE, 195,
												GroupLayout.PREFERRED_SIZE)
										.addGap(2)
										.addComponent(viewSwitch, GroupLayout.PREFERRED_SIZE, 19,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(table_scrollPane,
												GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(listTabs, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE).addGroup(Alignment.TRAILING,
						gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(viewSwitch, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411,
												Short.MAX_VALUE)
								.addComponent(table_scrollPane, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
						.addGap(0)));
		contentPane.setLayout(gl_contentPane);

		shButtonGroup.add(showTables);
		shButtonGroup.add(hideTables);
		shButtonGroup.setSelected(showTables.getModel(), true); // </fold>

		// Actions Performed. Yeah, the real thing begins here

		// Login Button
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Class.forName("java.sql.DriverManager");
					con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:" + port.getText() + "/",
							userName.getText(), password.getText());
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setLocation(dim.width / 2 - frame.getSize().width / 2,
							dim.height / 2 - frame.getSize().height / 2);
					frame.setVisible(true);
					frame.toFront();
					dbFill(dbListModel);
					tableHeader = table.getTableHeader();
					loginBox.dispose();
				} catch (SQLException | HeadlessException | ClassNotFoundException e) {
					String err = "Access denied for user '" + userName.getText()
							+ "'@'localhost' (using password: YES)";
					if (e.getMessage().equals(String.valueOf(err))) {
						JOptionPane.showMessageDialog(loginBox, "Incorrect Password or Username");
						password.requestFocusInWindow();
					} else if (e.getMessage().contains("Communications link failure")) {
						JOptionPane.showMessageDialog(loginBox, "Incorrect port or Service not running");
						userName.setText(null);
						password.setText(null);
						port.setText(null);
						userName.requestFocusInWindow();
					} else if (password.getText().equals("") && userName.getText().equals("")) {
						JOptionPane.showMessageDialog(loginBox, "Blank Password and Username");
						userName.requestFocusInWindow();
					} else if (password.getText().equals("")) {
						JOptionPane.showMessageDialog(loginBox, "Blank Password");
						password.requestFocusInWindow();
					} else if (port.getText().equals("")) {
						JOptionPane.showMessageDialog(loginBox, "Blank Connection Port");
						port.requestFocusInWindow();
					} else if (userName.getText().equals("")) {
						JOptionPane.showMessageDialog(loginBox, "Blank Username");
						userName.requestFocusInWindow();
					} else {
						JOptionPane.showMessageDialog(loginBox, e.getMessage());
						userName.setText(null);
						password.setText(null);
						port.setText(null);
						userName.requestFocusInWindow();
					}
				}
			}
		});

		// Exit Button
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] exitOptions = { "Yes", "Cancel" };
				int x = JOptionPane.showOptionDialog(loginBox, "Are you sure you want to exit", "Confirm Exit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, exitOptions, exitOptions[1]);
				if (x == 0) {
					System.exit(0);
				}
			}
		});

		// Sync changes with SQL
		getTableModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (table.isEditing()) {
					try {
						int row = e.getFirstRow();
						int col = e.getColumn();
						int rowcount = table.getRowCount();
						int colcount = table.getColumnCount();
						int curCell = table.getEditingRow();

						con.createStatement().executeUpdate("Alter table " + (String) table_list.getSelectedValue() + " add  " + tempCol
								+ " int(30)");

						Statement stmnt2 = (Statement) con.createStatement();
						for (int i = 0; i < rowcount; i++)
							con.createStatement().executeUpdate("update " + (String) table_list.getSelectedValue() + " set " + tempCol
									+ " = \"" + i + "\" where " + tempCol + " is null limit 1");

						// Check if the person is trying to create a new row
						int nCheck = 0;
						Statement stmnt3 = (Statement) con.createStatement();
						String updateCell, values = "";
						String curValue;
						ResultSet rs = stmnt3.executeQuery(
								"select max(" + tempCol + ") from " + (String) table_list.getSelectedValue());
						while (rs.next())
							nCheck = Integer.parseInt(rs.getString("max(" + tempCol + ")"));
						stmnt3.close();

						if (nCheck == (curCell - 1)) {
							for (int i = 0; i < colcount; i++) {
								try {
									curValue = table.getModel().getValueAt(curCell, i).toString();
								} catch (IndexOutOfBoundsException | NullPointerException exc) {
									curValue = "NULL";
								}
								if (curValue.equals("") || curValue.equals(null))
									curValue = "NULL";
								else {
									if (!curValue.equals("NULL"))
										curValue = "\"" + curValue + "\"";
								}
								values += curValue + ",";
								curValue = "";
							}
							values += (nCheck + 1);
							updateCell = "insert into " + (String) table_list.getSelectedValue() + " values(" + values
									+ ")";
						} else {
							updateCell = "Update " + (String) table_list.getSelectedValue() + " set "
									+ table.getColumnName(col) + " = \"" + table.getValueAt(row, col) + "\" where "
									+ tempCol + " = " + row;
						}

						Statement stmnt4 = (Statement) con.createStatement();
						stmnt4.executeUpdate(updateCell);
						stmnt4.close();

						Statement stmnt5 = (Statement) con.createStatement();
						stmnt5.executeUpdate(
								"alter table " + (String) table_list.getSelectedValue() + " drop column " + tempCol);
						stmnt5.close();
					} catch (Exception exc/*
											 * SQLException |
											 * java.lang.NullPointerException ex
											 */) {
						// if
						// (!(ex.getMessage().equals("java.lang.NullPointerException")))
						// {
						JOptionPane.showMessageDialog(null, exc.getMessage());
						// }
					}
				}
			}
		});

		db_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				getTableListModel().removeAllElements();
				try {
					Statement stmnt = (Statement) con.createStatement();
					String querytable = "Use " + (String) db_list.getSelectedValue();
					stmnt.executeQuery(querytable);
					querytable = "Show tables";
					ResultSet rs = stmnt.executeQuery(querytable);
					while (rs.next()) {
						String tname = rs.getString("Tables_in_" + (String) db_list.getSelectedValue());
						getTableListModel().addElement(tname);
					}
					table_list.setSelectedIndex(0);
					stmnt.close();
				} catch (Exception ex) {
					timeget();
					// operationlog += now + ": " + "/ Failed in establishing
					// MySQL remote connection \n Error Message: " +
					// e.getMessage() + " \n Class: " + e.getClass() + " / \n
					// \n";
					JOptionPane.showMessageDialog(null, ex.getMessage());
				}
			}
		});

		table_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				getshModel().removeAllElements();
				tableFill("*", 0, 0);
			}
		});

		menu_Exit.addActionListener(exitButton.getActionListeners()[0]);

		viewSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (listTabs.isVisible()) {
					listTabs.setVisible(false);
					switchWindow = new ImageIcon("assets\\switchR.png");
					viewSwitch.setIcon(switchWindow);
					viewSwitch.setToolTipText("Contracted Table View");
				} else {
					listTabs.setVisible(true);
					switchWindow = new ImageIcon("assets\\switchL.png");
					viewSwitch.setIcon(switchWindow);
					viewSwitch.setToolTipText("Extended Table View");
				}
			}
		});

		menu_SHColumns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Object[] jOptions = { "Apply changes", "Cancel" };
				int shChoice = JOptionPane.showOptionDialog(null,
						new Object[] { new JLabel("Select tables to for the operation:"), shList, showTables,
								hideTables },
						"Column Filter Dialog", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, jOptions,
						jOptions[0]);
				if (shButtonGroup.getSelection().equals(showTables.getModel())) {
					String val = shList.getSelectedValuesList().toString();
					val = val.substring(1, (val.length() - 1));
					String val2 = val;
					val = val.replaceAll(" ", "");
					for (int i = 0; i < val.length(); i++) {
						val = val.replaceFirst(",", "\n");
					}
					tableFill(val2, 0, 0, val);
				}
			}
		});

		menu_Create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (listTabs.getSelectedIndex() == 0) {
					String dbName = JOptionPane.showInputDialog(frame, "Enter Database Name",
							"Create new Database Dialog", JOptionPane.PLAIN_MESSAGE);
					try {
						con.createStatement().executeUpdate("Create database " + dbName);
						dbFill(dbListModel);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(frame, e.getMessage());
					}
				} else if (listTabs.getSelectedIndex() == 1) {
					try {
						JList<String> colList = createTempDialog("Enter table name:");
						if(colList == null) return;
						String tab_name = colList.getModel().getElementAt(0) + "(";
						for (int i = 1; i <= (colList.getModel().getSize() - 1); i++) {
							tab_name += colList.getModel().getElementAt(i) + ",";
						}
						tab_name = tab_name.substring(0, (tab_name.length() - 1));
						tab_name += ")";
						if (!colList.getModel().getElementAt(0).isEmpty()) {
							con.createStatement().executeUpdate("Create table " + tab_name);
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(frame, e.getMessage());
					}
				}
			}
		});

		menu_Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm;
				if (listTabs.getSelectedIndex() == 0) {
					confirm = JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to delete the database " + "\"" + db_list.getSelectedValue() + "\""
									+ "? Changes cannot be reverted.",
							"Delete Database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (confirm == 0) {
						try {
							con.createStatement().executeUpdate("drop database " + db_list.getSelectedValue());
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(frame, e1.getMessage());
						}
					}
				} else if (listTabs.getSelectedIndex() == 1) {
					confirm = JOptionPane.showConfirmDialog(frame,
							"Are you sure you want to delete the table " + "\"" + table_list.getSelectedValue() + "\""
									+ "? Changes cannot be reverted.",
							"Delete Table", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (confirm == 0) {
						try {
							con.createStatement().executeUpdate("drop table " + table_list.getSelectedValue());
							// Refresh Table List
							int dbIndex = db_list.getSelectedIndex();
							int tableIndex = table_list.getSelectedIndex();
							db_list.setSelectedIndex(dbIndex - 1);
							db_list.setSelectedIndex(dbIndex);
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(frame, e1.getMessage());
						}
					}
				}
			}
		});

		editMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent arg0) {				
			}

			public void menuDeselected(MenuEvent arg0) {	
			}
			
			public void menuSelected(MenuEvent arg0) {
				if (listTabs.getSelectedIndex() == 0){
					menu_Create.setText("Create new Database");
					menu_Delete.setText("Delete database" + " \"" + db_list.getSelectedValue() + "\"");
				}
				else if (listTabs.getSelectedIndex() == 1){
					menu_Create.setText("Create new Table");
					menu_Delete.setText("Delete table " + " \"" + table_list.getSelectedValue() + "\"");
				}
			}
		});
		
		menu_addRow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String rowCount = JOptionPane.showInputDialog("Enter number of rows to create:");
				if(rowCount==null)return;
				int x = Integer.parseInt(rowCount);
				//if(x.to=null)return;
				for(int i=0; i<x; i++)
				tableModel.addRow(new Object[] {""});
			}
			
		});
		
		menu_addColumn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
				JPanel colPanel = new JPanel();
				colPanel.setLayout(new GridLayout(1,2));
				colPanel.add(new JLabel("Enter column name: "));
				JTextField colName = new JTextField();
				colPanel.add(colName);
				JComboBox dataType = new JComboBox();
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
				int x = JOptionPane.showOptionDialog(frame, colPanel, "New Column Dialog",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, colSizeField);
				if(x!=0) return;
				con.createStatement().executeUpdate("alter table " + (String) table_list.getSelectedValue() + " add column " + colName.getText() + " " + dataType.getSelectedItem().toString().trim() + "(" + colSizeField.getText() + ")");
				tableModel.addColumn(colName.getText());
				}
				catch(Exception exc){
					JOptionPane.showMessageDialog(null, exc.getMessage());
				}
			}
		});
		
		if(!tableHeader.equals("")){
		tableHeader.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int x = tableHeader.columnAtPoint(arg0.getPoint());
				if(x!=-1){
				Rectangle columnRectangle = tableHeader.getHeaderRect(x);
				curSelCol = tableHeader.getColumnModel().getColumn(x);
				colRename.add(colRenStore);
			    colRename.setPreferredSize(
			              new Dimension(columnRectangle.width, columnRectangle.height +15));
				colRename.show(tableHeader, columnRectangle.x, 0);
				colRenStore.requestFocusInWindow();
			}
				}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		colRenStore.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String prevValue = (String) curSelCol.getHeaderValue();
				try{
				curSelCol.setHeaderValue(colRenStore.getText());
				
				JPanel colIdentifier = new JPanel(new GridLayout(2, 2));
				JComboBox dataType = new JComboBox();
				JTextField colSizeField = new JTextField();
				dataType.addItem("Char");
				dataType.addItem("Variable Char");
				dataType.addItem("Int");
				dataType.addItem("Date");
				colIdentifier.add(new JLabel("Select Data Type:"));
				colIdentifier.add(dataType);
				colIdentifier.add(new JLabel("Enter Column Size:"));
				colIdentifier.add(colSizeField);
				String[] options = { "Add Column", "Cancel" };
				if(!colRenStore.getText().isEmpty()){
				int x = JOptionPane.showOptionDialog(frame, colIdentifier, "Column Name Dialog",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, colSizeField);
				if (x == 0) {
					int type = dataType.getSelectedIndex();
					if (type == 0 || type == 1 || type == 2) {
						con.createStatement().executeUpdate("Alter table " +table_list.getSelectedValue()+ " change column "+prevValue+ " " + colRenStore.getText()+" " +dataType.getSelectedItem().toString().trim()+"("+colSizeField.getText()+")");
					} else {
						con.createStatement().executeUpdate("Alter table " +table_list.getSelectedValue()+ " change column "+prevValue+ " " + colRenStore.getText()+" " +dataType.getSelectedItem().toString().trim());
					}
				}
				}
				colRename.setVisible(false);
				}
				catch(SQLException exc){
					curSelCol.setHeaderValue(prevValue);
					JOptionPane.showMessageDialog(null, exc.getMessage());
				}
				}
		});
		}
		
		menu_RemRow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					String rowDat = "";
					for(int i=0; i<table.getColumnCount(); i++)
						if(i!=(table.getColumnCount()-1))
							rowDat += table.getValueAt(table.getSelectedRow(), i) + "\n";
						else
							rowDat += table.getValueAt(table.getSelectedRow(), i);	
					int x = JOptionPane.showConfirmDialog(frame, "Delete table row:\n\"" + rowDat+"\"?");
					if(x!=1) return;
					con.createStatement().executeUpdate("Alter table " + (String) table_list.getSelectedValue() + " add  " + tempCol
							+ " int(30)");
					for (int i = 0; i < table.getRowCount(); i++)
						con.createStatement().executeUpdate("update " + (String) table_list.getSelectedValue() + " set " + tempCol
								+ " = \"" + i + "\" where " + tempCol + " is null limit 1");
					con.createStatement().executeUpdate("delete from " + (String) table_list.getSelectedValue() + " where " + tempCol + "=" + table.getSelectedRow());
					con.createStatement().executeUpdate("alter table " + (String) table_list.getSelectedValue() + " drop column " + tempCol);
					repaint();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		
		menu_RemCol.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
				int x = JOptionPane.showConfirmDialog(frame, "Delete table column:\"" + table.getColumnName(table.getSelectedColumn())+"\"?");
				if(x==-1) return;
				con.createStatement().executeUpdate("alter table " + (String) table_list.getSelectedValue() + " drop column " + table.getColumnName(table.getSelectedColumn()));
				repaint();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
			}
			}
		});
		
	}

}
