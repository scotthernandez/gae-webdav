package com.googlecode.freewebdav.http;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Objectify;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Inject Objectify ofy;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		String thisURL = req.getRequestURI();
		if (req.getUserPrincipal() != null) {
			resp.sendRedirect("home");
		} else {
			resp.getWriter().println("<p>Please <a href=\"" + userService.createLoginURL(thisURL) + 
					"\">sign in</a>.</p>");
		}
	}
	
}
