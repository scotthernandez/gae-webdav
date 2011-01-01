package com.googlecode.freewebdav;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.http11.auth.DigestHelper;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import com.bradmcevoy.http.http11.auth.NonceProvider;
import com.ettrema.gae.AppEngineMemcacheNonceProvider;
import com.googlecode.objectify.Objectify;

public class WebdavUserDigestAuthenticationHandler implements AuthenticationHandler {
	private static final Logger log = Logger.getLogger(WebdavUserDigestAuthenticationHandler.class.getName());
    private final NonceProvider np = new AppEngineMemcacheNonceProvider(3600);
    private final DigestHelper digestHelper = new DigestHelper(np);

    @Inject Objectify ofy;
    
	public Object authenticate(Resource r, Request request) {
        Auth auth = request.getAuthorization();
        DigestResponse resp = digestHelper.calculateResponse(auth, "freewebdav.appspot.com", request.getMethod());
        if( resp == null ) {
            log.finest("requested digest authentication is invalid or incorrectly formatted");
            return null;
        } else {
        	//TODO add authentication.
            Object o = null;
            return o;
        }
	}

	public boolean supports(Resource arg0, Request arg1) {
		return true;
	}
	
    public String getChallenge( Resource resource, Request request ) {

        String nonceValue = np.createNonce( resource, request );
        return digestHelper.getChallenge(nonceValue, request.getAuthorization(), resource.getRealm());
    }
	
	public boolean isCompatible(Resource resource) {
		return true;
	}

}
