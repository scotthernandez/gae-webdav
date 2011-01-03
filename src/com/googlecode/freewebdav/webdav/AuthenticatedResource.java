package com.googlecode.freewebdav.webdav;

import java.util.Date;

import javax.inject.Inject;

import lombok.Data;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.DigestResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import com.google.inject.Injector;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

@Data
public class AuthenticatedResource implements Resource, DigestResource, PropFindableResource {
//	private static final Logger log = Logger.getLogger(AuthenticatedResource.class.getName() );
	
	@Inject protected Injector injector;
	@Inject protected Objectify ofy;
	protected String name;
	protected String id;
	protected Date dateModified = new Date();
	protected Date dateCreated = new Date();
	
	protected AuthenticatedResource() {}
	public AuthenticatedResource(String id, String name){
		this.name = name;
		this.id = id;
	}
	
	public AuthenticatedResource(String name){
		this(null,name);
	}

	public<T> T inject(T res){
		if (injector != null) injector.injectMembers(res);
		return res;
	}

	protected <T> Key<T> getKey(T entity) {
		return ofy.getFactory().getKey(entity);
	} 
	
	@Override
	public Object authenticate(String user, String password) {
		return null;
	}

	@Override
	public Object authenticate(DigestResponse dig) {
		return null;
	}

	@Override
	public boolean authorise(Request req, Method method, Auth auth) {
		if (method.equals(Method.OPTIONS))
			return true;
		boolean ret = (!(auth == null || auth.getUser() == null));
		return ret;
	}

	@Override
	public String checkRedirect(Request req) {
		//do redirect.
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return (dateModified == null) ? new Date() : dateModified;
	}
	
	@Override
	public Date getCreateDate() {
		return (dateCreated == null) ? new Date() : dateCreated;
	}

	@Override
	public String getName() {
		if(name == null) throw new IllegalStateException("Name is null");
		return name.replace("?", "_").replace('|', (char)124).replace('/', (char)47);
	}

	@Override
	public String getRealm() {
		return "freewebdav.appspot.com";
	}

	@Override
	public String getUniqueId() {
//		if(_id == null) throw new IllegalStateException("Id is null");
		return (id != null) ? id.replace("?", "_") : null;
	}

	@Override
	public boolean isDigestAllowed() {
		return true;
	}
}