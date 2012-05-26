package de.anycook.graph;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.server.spi.OAuthConsumer;
import com.sun.jersey.oauth.server.spi.OAuthToken;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;

import de.anycook.db.mysql.DBApps;
import de.anycook.graph.oauth.AnycookOAuthConsumer;
import de.anycook.graph.oauth.AnycookOAuthProvider;
import de.anycook.session.Session;
import de.anycook.user.User;

@Path("oauth")
public class OAuthGraph {
	
	private final Logger logger;
	private final static AnycookOAuthProvider provider;
	
	static{
		provider = new AnycookOAuthProvider();
	}
	
	
	public OAuthGraph(){
		logger = Logger.getLogger(getClass());
		
	}
	
	@POST
	@Path("request_token")
	public String getRequestToken(@Context HttpContext hc) throws UnsupportedEncodingException{
		OAuthServerRequest req = new OAuthServerRequest(hc.getRequest());		
		OAuthParameters params = new OAuthParameters();
		params.readRequest(req);
		
		String appID = params.getConsumerKey();
		OAuthConsumer consumer = AnycookOAuthConsumer.init(appID);
		if(consumer == null){
			throw new WebApplicationException(401);
		}
		String secret = consumer.getSecret();		
		OAuthSecrets secrets = new OAuthSecrets();
		secrets.setConsumerSecret(secret);
		
		try {
	        if(!OAuthSignature.verify(req, params, secrets)){
	        	logger.warn("verification failed for "+appID);
	        	throw new WebApplicationException(401);
	        }
	        	
	    }
	    catch (OAuthSignatureException ose) {
	    	
	    	throw new WebApplicationException(401);
	    }
		
		OAuthToken requestToken = provider.newRequestToken(appID, params.getCallback(), hc.getRequest().getQueryParameters());
		

		
		return requestToken.toString();
		
		
	}
	
	
	
	@GET
	@Path("authorize")
	public Response appLogin(@QueryParam("oauth_token") String oauthToken, 
			@QueryParam("accept") Boolean accept, @Context HttpServletRequest request) 
			throws URISyntaxException, UnsupportedEncodingException{
		
		//throw exception if theres no request token
		if(oauthToken == null )
			throw new WebApplicationException(400);
		
		Session session = Session.init(request.getSession());
		try{
			session.checkLogin();
		}catch(WebApplicationException e){
			StringBuilder redirectURL = new StringBuilder("http://test.anycook.de/login.html?redirect=");
			redirectURL.append(URLEncoder.encode("http://graph.anycook.de/oauth/authorize?oauth_token="+oauthToken, "UTF-8"));			
			return Response.temporaryRedirect(new URI(redirectURL.toString())).build();
		}
		
		OAuthToken requestToken = provider.getRequestToken(oauthToken);
		if(requestToken == null)
			throw new WebApplicationException(401);
		
		OAuthConsumer consumer = requestToken.getConsumer();
		logger.debug(consumer!=null);
		
		String appID = consumer.getKey();
		
		
		DBApps db = new DBApps();
		//if app seems to be accepted check referer
		if(accept != null && accept){
			String referer = request.getHeader("Referer");
			if(referer == null)
				throw new WebApplicationException(400);
			URI refererURI = new URI(referer);
			logger.info(refererURI.getHost());
			if(!refererURI.getHost().equals("graph.anycook.de"))
				throw new WebApplicationException(400);
			
			db.authorizeApp(session.getUser(), consumer.getKey());		
		}		
		
		if(db.checkUserForApp(session.getUser(), consumer.getKey())){
			String verifier = provider.getVerifier(consumer);
			String callbackURL = requestToken.getPrincipal().getName();
			
			if(callbackURL == null)
				return Response.ok("oauth_verifier="+verifier).build();
			callbackURL+="?oauth_token="+oauthToken+"&oauth_verifier="+verifier;
			return Response.temporaryRedirect(new URI(callbackURL)).build();
		}
		
		
		StringBuilder responseString = new StringBuilder();
		String appName = db.getAppName(appID);
		db.close();
		
		User user = session.getUser();
		responseString.append("Hello ").append(user.name).append("!<br>");
		
		responseString.append("Do you want to authorize \"").append(appName)
			.append("\"? <br>");
		responseString.append("<p><a href=\"/oauth/authorize?oauth_token=");
		responseString.append(oauthToken).append("&accept=true\">yes</a> ");		
		responseString.append("<a>no</a></p>");		
		responseString.append("<br>App:").append(consumer.getKey());
		
		return Response.ok(responseString.toString()).build();
	}
	
	@GET
	@Path("access_token")
	public Response getAccessToken(@Context HttpContext hc){
		return null;
	}
	
}
