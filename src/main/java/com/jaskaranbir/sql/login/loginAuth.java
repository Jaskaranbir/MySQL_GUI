package com.jaskaranbir.sql.login;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.mysql.jdbc.Connection;

class loginAuth implements ApplicationContextAware{

	private ApplicationContext applicationContext;
	
	private String postLoginBean;
	
	private Connection con;

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void setPostLoginBean(String postLoginBean) {
		this.postLoginBean = postLoginBean;
	}

	@Lazy
	@Bean
	public Connection getCon() {
		return con;
	}

	@Lazy
	@Bean
	@Scope("prototype")
	private Connection loginValidate(JDialog loginBox, JTextField port, JTextField userName,
			JPasswordField password) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setUsername(userName.getText());
		ds.setPassword(new String(password.getPassword()));
		ds.setUrl("jdbc:mysql://localhost:" + port.getText() + "/");
		try {
			con = (Connection) ds.getConnection();
			loginBox.dispose();
			port = null;
			userName = null;
			password = null;
			loginBox = null;
			applicationContext.getBean(postLoginBean);
			System.gc();
		} catch (Exception e) {
			String err = "Access denied for user '" + userName.getText() + "'@'localhost' (using password: YES)";
			if (e.getMessage().equals(err)) {
				JOptionPane.showMessageDialog(loginBox, "Incorrect Password or Username");
				password.requestFocusInWindow();
			} else if (e.getMessage().contains("Communications link failure")) {
				JOptionPane.showMessageDialog(loginBox, "Incorrect port or Service not running");
				userName.setText(null);
				password.setText(null);
				port.setText(null);
				userName.requestFocusInWindow();
			} else if (new String(password.getPassword()).equals("") && userName.getText().equals("")) {
				JOptionPane.showMessageDialog(loginBox, "Blank Password and Username");
				userName.requestFocusInWindow();
			} else if (new String(password.getPassword()).equals("")) {
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
		return null;
	}

}
