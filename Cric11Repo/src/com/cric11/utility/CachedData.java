//$Id$
package com.cric11.utility;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CachedData implements ServletContextListener {
	
	public static Configuration configuration = null;
	
	public static SessionFactory sessionFactory = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		sessionFactory.close();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			configuration = new Configuration().configure();
			sessionFactory = configuration.buildSessionFactory();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
