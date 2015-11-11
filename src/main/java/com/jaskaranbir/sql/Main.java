package com.jaskaranbir.sql;

import javax.swing.UIManager;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		new ClassPathXmlApplicationContext("com/jaskaranbir/sql/InterfaceBeans.xml","com/jaskaranbir/sql/MenuBeans.xml");
		
	}
	
}
