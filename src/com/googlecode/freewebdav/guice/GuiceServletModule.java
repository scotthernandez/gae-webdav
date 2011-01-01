package com.googlecode.freewebdav.guice;

import java.util.logging.Logger;

import com.google.inject.servlet.ServletModule;
import com.googlecode.freewebdav.http.LoginServlet;
import com.googlecode.freewebdav.http.HomeServlet;
import com.googlecode.freewebdav.http.WebdavServlet;

public class GuiceServletModule extends ServletModule {
	private static final Logger log = Logger.getLogger(GuiceServletModule.class.getName());
	@Override 
	protected void configureServlets() {
		log.fine("configuring servlets");
		serve("/login").with(LoginServlet.class);
		serve("/home").with(HomeServlet.class);
		serve("/webdav/*").with(WebdavServlet.class);
	}
}
