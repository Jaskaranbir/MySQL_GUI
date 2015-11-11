package com.jaskaranbir.sql.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class UI extends JFrame implements ApplicationContextAware {

	private static final long serialVersionUID = 1L;

	private DefaultTableModel tableModel;

	private DefaultTreeModel treeModel;

	private JTable table;

	private JTree listTree;

	private DefaultMutableTreeNode root;

	private ApplicationContext applicationContext;

	private String treePopulatorBean;
	private String tablePopulatorBean;

	private ArrayList<String> dbSelectionArray;
	private ArrayList<String> tableSelectionArray;

	private ArrayList<Integer> tableRowData;
	private int selectedTableCol;
	
	public String refCol;
	
	public int firstTableRow, curEditingRow;
	
	private JTableHeader tableHeader; 
	private int curSelColPoint;

	public void setTreePopulatorBean(String treePopulatorBean) {
		this.treePopulatorBean = treePopulatorBean;
	}

	public void setTablePopulatorBean(String tablePopulatorBean) {
		this.tablePopulatorBean = tablePopulatorBean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		applicationContext.getBean(treePopulatorBean);
		listTree.expandPath(new TreePath(root.getPath()));
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public String setRefCol(String refCol) {
		this.refCol = refCol;
		return null;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public String getRefCol() {
		return refCol;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public String getCurDBStr() {
		return listTree.getSelectionPath().getLastPathComponent().toString();
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public String[] getCurDB() {
		return new String[] { listTree.getSelectionPath().getLastPathComponent().toString(),
				listTree.getSelectionPath().getParentPath().toString() };
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public ArrayList<String> getSelectionArray() {
		return dbSelectionArray;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public ArrayList<String> getTableSelectionArray() {
		return tableSelectionArray;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public JTable getTable() {
		return table;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	public ArrayList<Integer> getTableRowData() {
		return tableRowData;
	}
	
	@Lazy
	@Bean
	@Scope("prototype")
	public int getSelectedTableCol() {
		return selectedTableCol;
	}
	
	@Lazy
	@Bean
	@Scope("prototype")
	public int getCurEditingRow() {
		return table.getEditingRow();
	}
	
	@Lazy
	@Bean
	@Scope("prototype")
	public JTableHeader getTableHeader(){
		return tableHeader;
	}
	
	@Lazy
	@Bean
	@Scope("prototype")
	public int getCurSelColPoint(){
		return curSelColPoint;
	}

	public UI() {
		tableModel = new DefaultTableModel();
		root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		listTree = new JTree(treeModel);
		listTree.setRootVisible(false);

		table = new JTable(tableModel);

		dbSelectionArray = new ArrayList<String>();
		tableSelectionArray = new ArrayList<String>();

		tableRowData = new ArrayList<Integer>();
		
		tableHeader = table.getTableHeader();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 931, 481);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem menu_Refresh = new JMenuItem("Refresh");
		fileMenu.add(menu_Refresh);
		
		JMenuItem menu_About = new JMenuItem("About");
		fileMenu.add(menu_About);

		JMenuItem menu_Exit = new JMenuItem("Exit");
		fileMenu.add(menu_Exit);

		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);

		JMenu editCreate_menu = new JMenu("Create");
		editMenu.add(editCreate_menu);

		JMenu editDelete_menu = new JMenu("Delete");
		editMenu.add(editDelete_menu);

		JMenuItem menu_CreateDatabase = new JMenuItem("Create new Database");
		editCreate_menu.add(menu_CreateDatabase);

		JMenuItem menu_DeleteDatabase = new JMenuItem("Delete Database");
		editDelete_menu.add(menu_DeleteDatabase);

		JMenuItem menu_CreateTable = new JMenuItem("Create new Table");
		editCreate_menu.add(menu_CreateTable);

		JMenuItem menu_DeleteTable = new JMenuItem("Delete Table");
		editDelete_menu.add(menu_DeleteTable);

		JMenu altTableMenu = new JMenu("Alter-Table");
		menuBar.add(altTableMenu);

		JMenuItem menu_addRow = new JMenuItem("Add Row");
		altTableMenu.add(menu_addRow);

		JMenuItem menu_addColumn = new JMenuItem("Add Column");
		altTableMenu.add(menu_addColumn);

		JMenuItem menu_DeleteRow = new JMenuItem("Delete Row");
		altTableMenu.add(menu_DeleteRow);

		JMenuItem menu_DeleteCol = new JMenuItem("Delete Column");
		altTableMenu.add(menu_DeleteCol);

		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		JMenuItem menu_SHColumns = new JMenuItem("Show/Hide Columns");
		viewMenu.add(menu_SHColumns);

		JMenuItem menu_ViewLog = new JMenuItem("View Log");
		viewMenu.add(menu_ViewLog);

		JScrollPane listContainer = new JScrollPane();
		listContainer.setViewportView(listTree);

		JScrollPane table_scrollPane = new JScrollPane();
		table_scrollPane.setViewportView(table);

		GroupLayout gl_contentPane = new GroupLayout(super.getContentPane());
		gl_contentPane
				.setHorizontalGroup(
						gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(listContainer, GroupLayout.PREFERRED_SIZE, 195,
												GroupLayout.PREFERRED_SIZE)
										.addGap(2)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(table_scrollPane,
												GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)));
		gl_contentPane
				.setVerticalGroup(
						gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(listContainer, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
								.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
										.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(table_scrollPane, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
								.addGap(0)));
		super.getContentPane().setLayout(gl_contentPane);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		menu_Refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		menu_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});

		menu_CreateDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createDatabase();
			}
		});

		menu_DeleteDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteDatabase();
			}
		});

		menu_CreateTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTable();
			}
		});

		menu_DeleteTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTable();
			}
		});

		menu_addRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRow();
			}
		});

		menu_addColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addColumn();
			}
		});

		menu_DeleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow();
			}
		});
		
		menu_DeleteCol.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteCol();
			}
		});

		menu_SHColumns.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				filterColumns();
			}
		});
		
		menu_About.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAboutInfo();
			}
		});
		
		listTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				if (listTree.getLeadSelectionRow() != -1) {
					tableHeader = table.getTableHeader();
					applicationContext.getBean(tablePopulatorBean);
				}
			}
		});
		
		tableHeader.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				renCol(mouseEvent);
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
		
		tableModel.addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent tableModelEvent) {
				syncAdapter();				
			}
		});
	}

	// Refresh Button Action (File->Refresh)
	private void refresh() {
		applicationContext.getBean(treePopulatorBean);
		treeModel.reload();
	}
	
	// Just a small dialog for credits ;)
	private void showAboutInfo(){
		String x = "MySQL GUI\nCreated by Jaskaran\nBuild Status: Alpha Builds";
		JOptionPane.showMessageDialog(null, x);
	}

	// Exit Button Action (File->Exit)
	private void exit() {
		Object[] exitOptions = { "Yes", "Cancel" };
		int x = JOptionPane.showOptionDialog(null, "Are you sure you want to exit", "Confirm Exit",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, exitOptions, exitOptions[1]);
		if (x == 0)
			System.exit(0);
	}

	// Create Database Action (Edit->Create Database)
	private void createDatabase() {
		applicationContext.getBean("createDatabase");
		refresh();
	}

	// Delete Database Action (Edit->Delete Database)
	private void deleteDatabase() {
		for (TreePath curPath : listTree.getSelectionPaths())
			dbSelectionArray.add(curPath.toString().substring(3, curPath.toString().length() - 1));
		applicationContext.getBean("deleteDatabase");
		refresh();
		dbSelectionArray.clear();
	}

	// Create Table Action (Edit->Create->Create Table)
	private void createTable() {
		applicationContext.getBean("createTable");
		refresh();
	}

	// Delete Table Action (Edit->Delete->Delete Table)
	private void deleteTable() {
		for (TreePath curPath : listTree.getSelectionPaths()) {
			String[] relTableDB = curPath.toString().substring(3, curPath.toString().length() - 1).replace(" ", "")
					.split(",");
			tableSelectionArray.add(relTableDB[0]);
			tableSelectionArray.add(relTableDB[1]);
		}
		applicationContext.getBean("deleteTable");
		refresh();
		tableSelectionArray.clear();
	}

	// Add Row Action (Alter-Table->Add Row)
	private void addRow() {
		applicationContext.getBean("addRow");
	}

	// Add Row Action (Alter-Table->Add Row)
	private void addColumn() {
		applicationContext.getBean("addColumn");
	}

	// Delete selected row from table (Alter-Table->Delete Row)
	private void deleteRow() {
		tableRowData.clear();
		for (int selRow : table.getSelectedRows())
			tableRowData.add(selRow);
		tableRowData.add(table.getColumnCount());
		tableRowData.add(table.getModel().getRowCount());
		applicationContext.getBean("deleteRow");
	}
	
	// Delete selected column from table (Alter-Table->Delete Column(
	private void deleteCol(){
		selectedTableCol = table.getSelectedColumn();
		applicationContext.getBean("deleteCol");
	}
	
	// Call popup bean for column renaming
	private void renCol(MouseEvent e){
		if(tableHeader != null){
			curSelColPoint = tableHeader.columnAtPoint(e.getPoint());
			applicationContext.getBean("columnRename");
		}
	}
	
	// Synchronizes changes from local table to MySQL
	private void syncAdapter(){
		if(table.isEditing())
			applicationContext.getBean("syncAdapter");
	}
	
	//Show/Hide selected columns from view (
	private void filterColumns(){
		applicationContext.getBean("SHColumns");
	}

}
