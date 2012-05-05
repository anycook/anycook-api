package de.anycook.graph;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.server.api.providers.DefaultOAuthProvider;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthRequest;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;

import de.anycook.db.mysql.DBApps;

@Path("oauth")
public class OAuthGraph {
	
	private final Logger logger;
	private final OAuthProvider provider;
	
	
	public OAuthGraph(){
		logger = Logger.getLogger(getClass());
		provider = new DefaultOAuthProvider();
	}
	
	@POST
	@Path("request_token")
	public String getRequestToken(@Context HttpContext hc){
		OAuthServerRequest req = new OAuthServerRequest(hc.getRequest());
		OAuthParameters params = new OAuthParameters();
		params.readRequest(req);
		
		for(String key : params.keySet())
			logger.debug(key+"="+params.get(key));
		
		String appID = params.getConsumerKey();
		logger.debug("appid: "+appID);
		DBApps db = new DBApps();
		String secret = db.getAppSecret(appID);
		logger.debug("secret: "+secret);
		if(secret == null){
			db.close();
			throw new WebApplicationException(401);
		}
		
		OAuthSecrets secrets = new OAuthSecrets();
		secrets.setConsumerSecret(secret);
		
		
		try {
	        String bla = Boolean.toString(OAuthSignature.verify(req, params, secrets));
	        logger.debug(bla);
	        return bla;
	    }
	    catch (OAuthSignatureException ose) {
	    	logger.warn(ose);
	        return "false";
	    }
		
		
		
		
	}
	
}
