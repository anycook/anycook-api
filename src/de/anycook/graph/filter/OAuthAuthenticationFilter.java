package de.anycook.graph.filter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import de.anycook.db.mysql.DBApps;
import de.anycook.misc.A;


public class OAuthAuthenticationFilter implements ContainerRequestFilter{
	private final Logger logger;
	
	public OAuthAuthenticationFilter(){
		logger = Logger.getLogger(getClass());
	}
	

	@Override
	public ContainerRequest filter(ContainerRequest containerRequest) {
		URI uri = containerRequest.getRequestUri();
		URI referer;
		try {
			String refererStr = containerRequest.getHeaderValue("referer");
			logger.info(refererStr);
			referer = new URI(refererStr);
		} catch (URISyntaxException | NullPointerException e) {
			throw new WebApplicationException(401);
		}
		String refererDomain = referer.getHost();
		
		//use path to check if oauth is needed
		String path = uri.getPath();
//		logger.debug(uri);
		
		DBApps db = new DBApps();
		String appSecret = db.getAppSecretByDomain(refererDomain);
		String appID = db.getAppIDbyDomain(refererDomain);
		if(appSecret == null || appID == null){
			db.close();
			throw new WebApplicationException(401);
		}
		
		
		// Read the OAuth parameters from the request
        OAuthServerRequest request = new OAuthServerRequest(containerRequest);
        OAuthParameters params = new OAuthParameters();
        params.readRequest(request);
        
        // Set the secret(s), against which we will verify the request
        OAuthSecrets secrets = new OAuthSecrets();
        secrets.setTokenSecret(appSecret);
        secrets.setConsumerSecret(appID);
        
        // TODO... secret setting code ...
        
        // Check that the timestamp has not expired
        String timestampStr = params.getTimestamp();
        // ... timestamp checking code ...
        
        // Verify the signature
//        try {
//            if(!OAuthSignature.verify(request, params, secrets)) {
//                throw new WebApplicationException(401);
//            }
//        } catch (OAuthSignatureException e) {
//            throw new WebApplicationException(e, 401);
//        }
        
        // Return the request
        db.close();
        return containerRequest;
	}

}
