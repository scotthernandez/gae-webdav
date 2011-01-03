package com.googlecode.freewebdav.guice;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ServletContextListener extends GuiceServletContextListener {
	private static final Logger log = Logger.getLogger(ServletContextListener.class.getName());

	@Override
	protected Injector getInjector() {
		return Guice.createInjector( 
				new Module(),
				new ServletModule() );
	}
	
	//pattern taken from http://turbomanage.wordpress.com/2009/12/11/how-to-inject-guice-objects-in-a-jsp/
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		log.finest("Setting guice injector in the servlet-context attributes");
		// No call to super as it also calls getInjector()
		ServletContext sc = sce.getServletContext();
		sc.setAttribute(Injector.class.getName(), getInjector());
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		ServletContext sc = servletContextEvent.getServletContext();
		sc.removeAttribute(Injector.class.getName());
		super.contextDestroyed(servletContextEvent);
	}
}
