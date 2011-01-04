package com.googlecode.freewebdav.guice;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.googlecode.freewebdav.http.DownloadServlet;
import com.googlecode.freewebdav.http.HomeServlet;
import com.googlecode.freewebdav.http.LoginServlet;
import com.googlecode.freewebdav.http.WebdavServlet;

public class ServletModule extends com.google.inject.servlet.ServletModule {
	private static final Logger log = Logger.getLogger(ServletModule.class.getName());
	@Override 
	protected void configureServlets() {
		log.fine("configuring servlets");
		HashMap<String, String> appstatParam = new HashMap<String, String>();
		appstatParam.put("logMessage", "Appstats available: /stats/details?time={ID}");
		filter("/*").through(AppstatsFilter.class);
		serve("/stats/*").with(AppstatsServlet.class);
		
		serve("/get/*").with(DownloadServlet.class);
		serve("/login").with(LoginServlet.class);
		serve("/home").with(HomeServlet.class);
		serve("/webdav/*").with(WebdavServlet.class);
		serve("/webdav*").with(WebdavServlet.class);
	}
}
