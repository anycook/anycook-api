package de.anycook.graph.filter;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.sun.jersey.oauth.server.OAuthServerRequest;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;


public class OAuthAuthenticationFilter implements ContainerRequestFilter{
	private final Logger logger;
	
	public OAuthAuthenticationFilter(){
		logger = Logger.getLogger(getClass());
	}
	

	@Override
	public ContainerRequest filter(ContainerRequest containerRequest) {
		logger.info(containerRequest.getRequestUri());
		 // Read the OAuth parameters from the request
        OAuthServerRequest request = new OAuthServerRequest(containerRequest);
        OAuthParameters params = new OAuthParameters();
        params.readRequest(request);
        
        // Set the secret(s), against which we will verify the request
        OAuthSecrets secrets = new OAuthSecrets();
//        secrets.s
        // ... secret setting code ...
        
        // Check that the timestamp has not expired
        String timestampStr = params.getTimestamp();
        // ... timestamp checking code ...
        
        // Verify the signature
        try {
            if(!OAuthSignature.verify(request, params, secrets)) {
                throw new WebApplicationException(401);
            }
        } catch (OAuthSignatureException e) {
            throw new WebApplicationException(e, 401);
        }
        throw new WebApplicationException(403);
        
        // Return the request
//        return containerRequest;
	}

}
