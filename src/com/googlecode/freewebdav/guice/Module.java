package com.googlecode.freewebdav.guice;

import java.util.logging.Logger;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.googlecode.freewebdav.entities.WebdavFile;
import com.googlecode.freewebdav.entities.WebdavFileData;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.entities.WebdavUser;
import com.googlecode.freewebdav.http.DownloadServlet;
import com.googlecode.freewebdav.http.HomeServlet;
import com.googlecode.freewebdav.http.LoginServlet;
import com.googlecode.freewebdav.http.WebdavServlet;
import com.googlecode.freewebdav.webdav.ResourceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;

public class Module extends AbstractModule {
	private static final Logger log = Logger.getLogger(Module.class.getName());
	
	@Override
	protected void configure() {
		log.fine("Setting up bindings for guice...");
		bind(ResourceFactory.class);
		bind(com.bradmcevoy.http.ResourceFactory.class).to(ResourceFactory.class);
		bind(DownloadServlet.class).asEagerSingleton();
		bind(HomeServlet.class).asEagerSingleton();
		bind(LoginServlet.class).asEagerSingleton();
		bind(WebdavServlet.class).asEagerSingleton();
	}
	
	@Provides @Singleton
	ObjectifyFactory getOfyFact() {
		log.fine("Configuring ObjectifyFactory");
		ObjectifyFactory fact = new ObjectifyFactory();
		fact.register(WebdavUser.class);
		fact.register(WebdavFileData.class);
		fact.register(WebdavFolder.class);
		fact.register(WebdavFile.class);
		return fact;
	}
	
	@Provides
	Objectify getOfy(ObjectifyFactory ofyFact) {
		log.finest("Creating Objectify (non-transactional)");
		return ofyFact.begin();
	}
}
