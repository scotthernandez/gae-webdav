package com.googlecode.freewebdav.webdav;

import java.util.logging.Logger;

import com.bradmcevoy.http.Resource;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.freewebdav.entities.WebdavFile;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.entities.WebdavUser;
import com.googlecode.freewebdav.http.WebdavServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

public class ResourceFactory implements com.bradmcevoy.http.ResourceFactory {
	private static final Logger log = Logger.getLogger(ResourceFactory.class.getName() );
	@Inject Injector inj;
	@Inject Objectify ofy;
	
	@Override
	public Resource getResource(String host, String url) {
		if (shouldIgnore(url)) {
			log.finest("ignoring request for crap -- " + url);
			return null;
		}

		String servletPath = WebdavServlet.rawRequest().getContextPath() + WebdavServlet.rawRequest().getServletPath();
		String betterUrl = (url.length() <= servletPath.length()) ? "" : url.substring(servletPath.length() + 1);
		if (betterUrl == null ) betterUrl = "";
		if (betterUrl.endsWith("/")) betterUrl = betterUrl.substring(0,betterUrl.length()-1);
		if (betterUrl.startsWith("/")) betterUrl = betterUrl.substring(1);
		betterUrl = betterUrl.replace("//", "/");
		
		log.fine("Resolving resource needed for '" + betterUrl + "' ("+ url + ")");
		
		String[] i = betterUrl.split("/");
//		int c = i.length;
		
		WebdavUser wu = (WebdavUser)WebdavServlet.request().getAuthorization().getTag();
		
		if (wu == null)
			wu = getUserFromHost(host);
		
		if (wu == null)
			return null;
		RootResource root = inject(new RootResource(wu));
		if ("".equals(betterUrl)) 
			return root;
		
		Resource res = getLastItem(getKey(root), i, 1);
		return res;
	}

	@SuppressWarnings("rawtypes")
	private Resource getLastItem(Key parent, String[] path, int pos) {
		String name = path[pos];
		
		WebdavFile wfile = null;
		WebdavFolder wfolder = ofy.query(WebdavFolder.class).ancestor(parent).filter("name", name).get();
		if (wfolder == null)
			wfile = ofy.query(WebdavFile.class).ancestor(parent).filter("name", name).get();;
		
		if (pos < path.length)
			return getLastItem(getKey(wfolder), path, pos+1);
		
		return inject((wfolder == null) ? new FileResource(wfile) : new FolderResource(wfolder));
	}

	private WebdavUser getUserFromHost(String host) {
		String username = host.split(".")[0];
		return ofy.query(WebdavUser.class).filter("username", username).get();
	}

	protected <T> Key<T> getKey(T entity) {
		return ofy.getFactory().getKey(entity);
	} 

	public <T> T inject(T res){
		inj.injectMembers(res);
		return res;
	}
	
	//Mac/Windows/Linux crap -- ignore!
	public static boolean shouldIgnore(String url) {
		boolean ignore = url.endsWith("thumbs.db") || url.endsWith("Thumbs.db") || url.endsWith("desktop.ini") || 
				url.endsWith("folder.gif") || url.endsWith("Thumbs") || url.endsWith("._refresh") ||  //url.endsWith("folder.jpg") || win7=unc-format connection;  
				url.endsWith(".DS_Store") || url.endsWith("Contents") || url.endsWith("Contents/PkgInfo") || 
				url.endsWith(".Trashes") || url.endsWith(".TemporaryItems");
		if (!ignore) {
			String[] i = url.split("/");
			if (i.length > 0) {
				String lastPart = i[i.length-1];
				ignore = lastPart.startsWith("._");
			}
		}
		return ignore;
	}

	
	
}
