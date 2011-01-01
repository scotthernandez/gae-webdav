package com.googlecode.freewebdav.http;

import java.io.IOException;
import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Ostermiller.util.RandPass;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.freewebdav.entities.WebdavFolder;
import com.googlecode.freewebdav.entities.WebdavUser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

public class HomeServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		Principal p = req.getUserPrincipal();
		User u = userService.getCurrentUser();
		if (p != null) {
			WebdavUser wu = ofy.find(WebdavUser.class, u.getUserId());
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			wu.setUsername(username);
			wu.setPassword(password);
			ofy.put(wu);
			resp.sendRedirect("home");
		} else {
			resp.sendRedirect("login");			
		}		
	}

	private static final long serialVersionUID = 1L;
	@Inject Objectify ofy;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		String thisURL = req.getRequestURI();
		Principal p = req.getUserPrincipal();
		User u = userService.getCurrentUser();
		if (p != null) {
			WebdavUser wu = ofy.find(WebdavUser.class, u.getUserId());
			if (wu == null) {
				wu = new WebdavUser();
				wu.setPassword((new RandPass(RandPass.NONCONFUSING_ALPHABET).getPass(6)));
				wu.setUserId(u.getUserId());
				wu.setUsername(u.getNickname());
				Key<WebdavUser> wuKey = ofy.put(wu);
				WebdavFolder root = new WebdavFolder();
				root.setUser(wuKey);
				ofy.put(root);
			}
			
			resp.getWriter().println(
					String.format(
					"<p>Hello, %s! You can log in to http://freewebdav.appspot.com/webdav/ as %s/%s" +
					"<br><br> You can <a href=\"%s\">sign out</a>.</p>", 
					u.toString(),
					wu.getUsername(),
					wu.getPassword(),					
					userService.createLogoutURL(thisURL) ));
		} else {
			resp.sendRedirect("login");
		}
	}
	
}
