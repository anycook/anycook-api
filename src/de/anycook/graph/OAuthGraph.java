package de.anycook.graph;

import java.io.UnsupportedEncodingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
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

import de.anycook.graph.oauth.AnycookOAuthConsumer;
import de.anycook.graph.oauth.AnycookOAuthProvider;

@Path("oauth")
public class OAuthGraph {
	
	private final Logger logger;
	private final OAuthProvider provider;
	
	
	public OAuthGraph(){
		logger = Logger.getLogger(getClass());
		provider = new AnycookOAuthProvider();
	}
	
	@GET
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
	        if(!OAuthSignature.verify(req, params, secrets))
	        	throw new WebApplicationException(401);
	    }
	    catch (OAuthSignatureException ose) {
	    	logger.warn(ose);
	    	throw new WebApplicationException(401);
	    }
		
		OAuthToken requestToken = provider.newRequestToken(appID, null, hc.getRequest().getQueryParameters());
		

		
		return requestToken.toString();
		
		
	}
	
}
