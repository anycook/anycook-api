package de.anycook.graph.servlets.message;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.anycook.messages.Messagesession;
import de.anycook.messages.checker.MessagesessionChecker;
import de.anycook.misc.DateParser;
import de.anycook.session.Session;
import de.anycook.user.User;


@WebServlet(urlPatterns = "/message", asyncSupported=true)
public class GetMessagesession extends HttpServlet{
	private final Logger logger;
	
	public GetMessagesession() {
		logger = Logger.getLogger(getClass());
	}
	
	
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
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String message = request.getParameter("message");
		String recipientsString = request.getParameter("recipients");
		
		if(message == null){
			logger.info("message was null");
			return;
		}
		
		if(recipientsString == null){
			logger.info("recipients was null");
			return;
		}
		
		message = URLDecoder.decode(message, "UTF-8");
		
		try {
			JSONParser parser = new JSONParser();
			JSONArray recipientsJSON = (JSONArray)parser.parse(recipientsString);
			Session session = Session.init(request.getSession());
			session.checkLogin();
			List<Integer> recipients = new LinkedList<>();
			for(Object recipientString : recipientsJSON)
				recipients.add(Integer.parseInt(recipientString.toString()));
			int userid = session.getUser().id;
			recipients.add(userid);
			Messagesession.getSession(recipients).newMessage(userid, message);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
