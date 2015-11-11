package com.jaskaranbir.sql.login;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class loginDialog implements ApplicationContextAware {

	private String loginValidatorBean;

	private ApplicationContext applicationContext;

	public void setLoginValidatorBean(String loginValidatorBean) {
		this.loginValidatorBean = loginValidatorBean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
		loginDialog() {
		JDialog loginBox = new JDialog();

		JButton loginButton;
		JButton exitButton;

		JTextField userName;
		JPasswordField password;
		JTextField port;

		loginBox.getContentPane().setLayout(new GridLayout(5, 2, 0, 3));
		loginBox.setUndecorated(true);
		loginBox.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
		loginBox.setDefaultCloseOperation(2);

		loginButton = new JButton("Login");
		exitButton = new JButton("Exit");

		userName = new JTextField();
		password = new JPasswordField();
		port = new JTextField();

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

		loginBox.setLocationRelativeTo(null);
		loginBox.setVisible(true);

		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				applicationContext.getBean(loginValidatorBean, loginBox, port, userName, password);
			}
		});

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] exitOptions = { "Yes", "Cancel" };
				int x = JOptionPane.showOptionDialog(loginBox, "Are you sure you want to exit", "Confirm Exit",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, exitOptions, exitOptions[1]);
				if (x == 0)
					System.exit(0);
			}
		});
	}
}
