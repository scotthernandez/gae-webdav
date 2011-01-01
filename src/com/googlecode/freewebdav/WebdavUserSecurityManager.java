package com.googlecode.freewebdav;

import javax.inject.Inject;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.http11.auth.DigestGenerator;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import com.googlecode.freewebdav.entities.WebdavUser;
import com.googlecode.objectify.Objectify;

public class WebdavUserSecurityManager implements SecurityManager {

	@Inject private Objectify ofy;
	
	@Inject
	public WebdavUserSecurityManager(Objectify ofy) {
		this.ofy = ofy;
	}
	
	@Override
	public Object authenticate(DigestResponse digestResp) {
		WebdavUser wu = getUser(digestResp.getUser());
		if (wu == null) 
			return null;
		
        String serverResponse = new DigestGenerator().generateDigest( digestResp, wu.getPassword() );
        String clientResponse = digestResp.getResponseDigest();

        return serverResponse.equals(clientResponse) ? wu : null;
	}
	
	@Override
    public Object authenticate(String user, String password) {
		WebdavUser wu = getUser(user);
		if (wu == null)
			return null;
		
		return wu.getPassword().equals(password) ? wu : null;
	}
	
	@Override
	public boolean authorise(Request request, Method method, Auth auth, Resource resource) {
		if (method.equals(Method.OPTIONS))
			return true;

		return auth != null && auth.getTag() != null;
	}
	
	@Override
	public String getRealm(String s) {
		return "freewebdav.appspot.com";
	}
	
	private WebdavUser getUser(String username) {
		return ofy.query(WebdavUser.class).filter("username", username).get();
	}
}
