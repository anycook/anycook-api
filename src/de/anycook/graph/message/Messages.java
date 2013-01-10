package de.anycook.graph.message;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.anycook.messages.checker.NewMessageChecker;
import de.anycook.session.Session;
import de.anycook.user.User;


@WebServlet(urlPatterns = "/getmessage/*", asyncSupported=true, name="Messages")
public class Messages extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Logger logger;
	
	/**
	 * 
	 */
	public Messages() {
		logger = Logger.getLogger(getClass());
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.debug("calling OPTIONS: "+req.getRequestURI());
		resp.getWriter().close();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		
		String path = req.getRequestURI();
		String callback = req.getParameter("callback");
		
		String lastidString = req.getParameter("lastid");
		int lastid = lastidString == null ? -1 : Integer.parseInt(lastidString);

		Integer sessionid = Integer.parseInt(path.split("/")[2]);
		
		Session session = Session.init(req.getSession());
		User user = session.getUser();		
		AsyncContext async = req.startAsync();
		async.setTimeout(20000);
		NewMessageChecker.addContext(lastid, user.getId(), sessionid, async, callback);
	}
	
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse resp)
//			throws ServletException, IOException {
//		request.setCharacterEncoding("UTF-8");
//		
//		String[] path = request.getRequestURI().split("/");
//		logger.debug("anwering message. path: "+request.getRequestURI());
//		
//		if(path.length == 3){
//			Integer sessionid = Integer.parseInt(path[3]);
//			String message = request.getParameter("message");
//			Session session = Session.init(request.getSession());
//			User user = session.getUser();
//			Messagesession.getSession(sessionid, user.id).newMessage(user.id, message);
//		}else if(path.length ==4){
//			Integer sessionid = Integer.parseInt(path[3]);
//			Integer messageid = Integer.parseInt(path[4]);
//			Session session = Session.init(request.getSession());
//			User user = session.getUser();
//			Message.read(sessionid, messageid, user.id);
//		}		
//	}

}
