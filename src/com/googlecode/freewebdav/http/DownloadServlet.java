package com.googlecode.freewebdav.http;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bradmcevoy.http.AbstractRequest;
import com.bradmcevoy.http.Request.Header;
import com.bradmcevoy.http.Utils;
import com.googlecode.freewebdav.entities.WebdavFile;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.webdav.ResourceFactory;
import com.googlecode.objectify.Objectify;

public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DownloadServlet.class.getName());
	@Inject Objectify ofy;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String url = AbstractRequest.stripToPath(Utils.decodePath(req.getRequestURL().toString())).substring(4);
		String fileName = url.substring(url.lastIndexOf('/') + 1);
		WebdavFolder wf = ResourceFactory.getFolder(
							ofy, 
							ResourceFactory.getUserKeyFromHost(ofy, req.getHeader(Header.HOST.code)), 
							url.substring(0, url.length() - fileName.length()-1));
		if(wf == null) {
			resp.sendError(404);
			return;
		}
		log.finer("looking up file:" + fileName + " with parent:" + wf);
		WebdavFile file = ofy.query(WebdavFile.class).filter("parent", wf).filter("name", fileName).get();

		if(file == null) {
			resp.sendError(404);
			return;
		}
			
		log.fine("Sending file:" + fileName);
		String contentType = file.getContentType();
		String name = file.getName();

		resp.setContentType(contentType);
		//return images inline... by skipping this
		if (contentType == null || !contentType.toLowerCase().startsWith("image"))
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
		
		//TODO add cache headers.
		resp.getOutputStream().write(ofy.get(file.getData()).getData());
		resp.flushBuffer();
	}
}
