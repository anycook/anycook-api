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
import com.sun.jersey.oauth.server.spi.OAuthProvider;
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
	private final OAuthProvider provider;
	
	
	public OAuthGraph(){
		logger = Logger.getLogger(getClass());
		provider = new AnycookOAuthProvider();
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
		
		OAuthToken requestToken = provider.newRequestToken(appID, null, hc.getRequest().getQueryParameters());
		

		
		return requestToken.toString();
		
		
	}
	
	@GET
	@Path("authorize")
	public Response appLogin(@QueryParam("oauth_token") String oauthToken, 
			@Context HttpServletRequest request) throws URISyntaxException, UnsupportedEncodingException{
		if(oauthToken == null )
			throw new WebApplicationException(401);
		Session session = Session.init(request.getSession());
		try{
			session.checkLogin();
		}catch(WebApplicationException e){
			StringBuilder redirectURL = new StringBuilder("http://test.anycook.de/login.html?redirect=");
			redirectURL.append(URLEncoder.encode("http://graph.anycook.de/oauth/authorize", "UTF-8"));
			redirectURL.append("&oauth_token=").append(oauthToken);			
			
			return Response.temporaryRedirect(new URI(redirectURL.toString())).build();
		}
		OAuthToken requestToken = provider.getRequestToken(oauthToken);
		OAuthConsumer consumer = requestToken.getConsumer();
		String appID = consumer.getKey();
		
		StringBuilder responseString = new StringBuilder();
		DBApps dbApps = new DBApps();
		String appName = dbApps.getAppName(appID);
		dbApps.close();
		
		User user = session.getUser();
		responseString.append("Hello ").append(user.name).append("!<br>");
		
		responseString.append("Do you want to authorize \"").append(appName)
			.append("\"? <br>");
		
		
		
		responseString.append("<br>App:").append(consumer.getKey());
		return Response.ok(responseString.toString()).build();
	}
	
}
