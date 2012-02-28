package de.anycook.graph.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import anycook.messages.checker.MessagesessionChecker;
import anycook.misc.DateParser;
import anycook.session.Session;
import anycook.user.User;

@WebServlet(urlPatterns = "/message", asyncSupported=true)
public class GetMessagesession extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		String lastchange = req.getParameter("lastchange");
		String callback = req.getParameter("callback");
		Date lastChange = null;
		if(lastchange != null)
			lastChange = DateParser.parseDateTime(lastchange);		
		Session session = Session.init(req.getSession());
		User user = session.getUser();		
		AsyncContext async = req.startAsync();
		async.setTimeout(20000);
		MessagesessionChecker.addContext(lastChange, user.id, async, callback);
	}
}
