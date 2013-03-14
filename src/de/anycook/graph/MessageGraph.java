package de.anycook.graph;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import de.anycook.messages.MessageSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.anycook.graph.filter.cors.CorsFilter;
import de.anycook.messages.Message;
import de.anycook.messages.checker.MessageChecker;
import de.anycook.messages.checker.MessagesessionChecker;
import de.anycook.messages.checker.NewMessageChecker;
import de.anycook.utils.DaemonThreadFactory;
import de.anycook.session.Session;

@Path("/writemessage")
public class MessageGraph  {
	
	private static ExecutorService exec;
    private static final int numThreads = 30;
	
	public static void init() {
		exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads/3; i++){
        	exec.execute(new NewMessageChecker());
        	exec.execute(new MessageChecker());
        	exec.execute(new MessagesessionChecker());
        }
	}
	
	private final Logger logger;
	
	/**
	 * 
	 */
	public MessageGraph() {
		logger = Logger.getLogger(getClass());
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newMessage(
			@HeaderParam("Origin") String origin, 
			@FormParam("message") String message,
			@FormParam("recipients") String recipientsString,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request
			){
		
		if(!CorsFilter.checkOrigin(origin))
			throw new WebApplicationException(401);
		
		if(message == null){
			logger.info("message was null");
			throw new WebApplicationException(400);
		}
		
		if(recipientsString == null){
			logger.info("recipients was null");
			throw new WebApplicationException(400);
		}
		
		try {
			message = URLDecoder.decode(message, "UTF-8");
			JSONParser parser = new JSONParser();
			JSONArray recipientsJSON = (JSONArray)parser.parse(recipientsString);
			Session session = Session.init(request.getSession());
			session.checkLogin(hh.getCookies());
			List<Integer> recipients = new LinkedList<>();
			for(Object recipientString : recipientsJSON)
				recipients.add(Integer.parseInt(recipientString.toString()));
			int userid = session.getUser().getId();
			recipients.add(userid);
			MessageSession.getSession(recipients).newMessage(userid, message);
		} catch (ParseException | UnsupportedEncodingException e) {
			logger.error(e);
			throw new WebApplicationException(400);
		}
//		return CorsFilter.buildResponse(origin);
	}
	
	@PUT
	@Path("{sessionid}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void answerSession(@PathParam("sessionid") int sessionid,
			@HeaderParam("Origin") String origin,
			@FormParam("message") String message,
			@Context HttpHeaders hh,
			@Context HttpServletRequest req){
		if(!CorsFilter.checkOrigin(origin))
			throw new WebApplicationException(401);
		
		if(message == null){
			logger.info("message was null");
			throw new WebApplicationException(400);
		}
		
		Session session = Session.init(req.getSession());
		session.checkLogin(hh.getCookies());
		int userid = session.getUser().getId();
		MessageSession.getSession(sessionid, userid).newMessage(userid, message);
	}
	
	@PUT
	@Path("{sessionid}/{messageid}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void readMessage(@PathParam("sessionid") int sessionid,
			@PathParam("messageid") int messageid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest req){
		Session session = Session.init(req.getSession());
		session.checkLogin(hh.getCookies());
		int userid = session.getUser().getId();
		Message.read(sessionid, messageid, userid);
	}
	
//	private static Response addAllowOriginHeaders(ResponseBuilder response, 
//			String origin){
//		response.header("Access-Control-Allow-Origin", origin)
////			.header("Access-Control-Allow-Methods", "POST, PUT,DELETE,GET, OPTIONS")
//			.header("Access-Control-Allow-Credentials", "true");
//		return response.type(MediaType.APPLICATION_XML).build();
//			
//	}


	public static void stop() {
		exec.shutdownNow();		
	}
}
