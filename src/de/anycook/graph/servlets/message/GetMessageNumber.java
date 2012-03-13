package de.anycook.graph.servlets.message;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import anycook.messages.checker.MessageChecker;
import anycook.session.Session;
import anycook.user.User;

/**
 * Servlet implementation class GetMessageNumber
 */
@WebServlet(urlPatterns = "/message/number", asyncSupported=true)
public class GetMessageNumber extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer lastnum = Integer.parseInt(request.getParameter("lastnum"));
		String callback = request.getParameter("callback");
		
		Session session = Session.init(request.getSession(true));
		session.checkLogin();
		User user = session.getUser();
		AsyncContext async = request.startAsync();
		async.setTimeout(60000);
		MessageChecker.addContext(async, lastnum, user.id, callback);
	}

}
