package com.googlecode.freewebdav.guice;

import java.util.logging.Logger;

import com.googlecode.freewebdav.http.DownloadServlet;
import com.googlecode.freewebdav.http.HomeServlet;
import com.googlecode.freewebdav.http.LoginServlet;
import com.googlecode.freewebdav.http.WebdavServlet;

public class ServletModule extends com.google.inject.servlet.ServletModule {
	private static final Logger log = Logger.getLogger(ServletModule.class.getName());
	@Override 
	protected void configureServlets() {
		log.fine("configuring servlets");
		serve("/get/*").with(DownloadServlet.class);
		serve("/login").with(LoginServlet.class);
		serve("/home").with(HomeServlet.class);
		serve("/webdav/*").with(WebdavServlet.class);
		serve("/webdav*").with(WebdavServlet.class);
	}
}
