package com.googlecode.freewebdav.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.ServletHttpManager;
import com.bradmcevoy.http.ServletRequest;
import com.bradmcevoy.http.ServletResponse;
import com.bradmcevoy.http.http11.DefaultHttp11ResponseHandler.BUFFERING;
import com.bradmcevoy.http.http11.auth.NonceProvider;
import com.bradmcevoy.http.http11.auth.PreAuthenticationFilter;
import com.bradmcevoy.http.http11.auth.SecurityManagerBasicAuthHandler;
import com.bradmcevoy.http.http11.auth.SecurityManagerDigestAuthenticationHandler;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.ettrema.gae.AppEngineMemcacheNonceProvider;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.freewebdav.WebdavUserSecurityManager;
import com.googlecode.freewebdav.webdav.ResourceFactory;
import com.googlecode.objectify.Objectify;

public class WebdavServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final ThreadLocal<HttpServletRequest> originalRequest = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> originalResponse = new ThreadLocal<HttpServletResponse>();
	private static final ThreadLocal<Request> request = new ThreadLocal<Request>();
    private static final ThreadLocal<Response> response = new ThreadLocal<Response>();

    protected ServletHttpManager httpManager;
    private Injector inj; 
    
    @Inject
    protected WebdavServlet(ResourceFactory fact, Injector injector, Objectify ofy) {
    	inj = injector;
    	WebdavUserSecurityManager sm = inject(new WebdavUserSecurityManager(ofy));
    	NonceProvider np = new AppEngineMemcacheNonceProvider(10*60*60);
    	List<AuthenticationHandler> authHandlers = new ArrayList<AuthenticationHandler>();
    	authHandlers.add(new SecurityManagerBasicAuthHandler(sm));
    	authHandlers.add(new SecurityManagerDigestAuthenticationHandler(np, sm));
    	AuthenticationService authSvc = new AuthenticationService(authHandlers);

    	DefaultWebDavResponseHandler respHandler = new DefaultWebDavResponseHandler(authSvc);
    	respHandler.setBuffering(BUFFERING.always);
    	httpManager = new ServletHttpManager(fact, respHandler, authSvc);
    	httpManager.addFilter(0, new PreAuthenticationFilter(respHandler, sm, np));
    }

    public <T> T inject(T res){
		inj.injectMembers(res);
		return res;
	}

    public static HttpServletRequest rawRequest() {
        return originalRequest.get();
    }

    public static HttpServletResponse rawResponse() {
        return originalResponse.get();
    }
    public static Request request() {
        return request.get();
    }

    public static Response response() {
        return response.get();
    }

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Request request = new ServletRequest( req );
            Response response = new ServletResponse( resp );
            WebdavServlet.request.set(request);
            WebdavServlet.response.set(response);
        	WebdavServlet.originalRequest.set( req );
        	WebdavServlet.originalResponse.set( resp );
            httpManager.process( request, response );
        } finally {
            originalRequest.remove();
            originalResponse.remove();
            request.remove();
            response.remove();
            resp.getOutputStream().flush();
            resp.flushBuffer();
        }

	}
	
}
