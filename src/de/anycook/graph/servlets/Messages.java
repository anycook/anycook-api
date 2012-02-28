package de.anycook.graph.servlets;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import anycook.messages.Messagesession;
import anycook.messages.checker.NewMessageChecker;
import anycook.session.Session;
import anycook.user.User;

@WebServlet(urlPatterns = "/message/*", asyncSupported=true)
public class Messages extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String path = req.getRequestURI();
		String callback = req.getParameter("callback");
		
		String lastidString = req.getParameter("lastid");
		int lastid = lastidString == null ? -1 : Integer.parseInt(lastidString);
		
		Logger.getLogger(getClass()).info(path);
		Integer sessionid = Integer.parseInt(path.split("/")[3]);
		
		Session session = Session.init(req.getSession());
		User user = session.getUser();		
		AsyncContext async = req.startAsync();
		async.setTimeout(20000);
		NewMessageChecker.addContext(lastid, user.id, sessionid, async, callback);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String path = request.getRequestURI();
		Integer sessionid = Integer.parseInt(path.split("/")[3]);
		
		String message = request.getParameter("message");
		Session session = Session.init(request.getSession());
		User user = session.getUser();
		Messagesession.getSession(sessionid, user.id).newMessage(user.id, message);
	}

}
