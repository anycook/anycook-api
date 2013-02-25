package de.anycook.graph.drafts;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.anycook.session.Session;
import de.anycook.user.User;

/**
 * Servlet implementation class GetMessageNumber
 */
@WebServlet(urlPatterns = "/drafts/num", asyncSupported=true, name="GetDraftNumber")
public class GetDraftNumber extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		Integer lastnum = Integer.parseInt(request.getParameter("lastnum"));
		String callback = request.getParameter("callback");
		
		Session session = Session.init(request.getSession(true));
		session.checkLogin();
		User user = session.getUser();
		AsyncContext async = request.startAsync();
		async.setTimeout(60000);
		DraftChecker.addContext(async, lastnum, user.getId(), callback);
	}

}