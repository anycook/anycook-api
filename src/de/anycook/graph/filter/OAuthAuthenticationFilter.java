package de.anycook.graph.filter;

import java.net.URI;

import org.apache.log4j.Logger;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;


public class OAuthAuthenticationFilter implements ContainerRequestFilter{
	private final Logger logger;
	
	public OAuthAuthenticationFilter(){
		logger = Logger.getLogger(getClass());
	}
	

	@Override
	public ContainerRequest filter(ContainerRequest containerRequest) {
		URI uri = containerRequest.getRequestUri();
		URI uri2 = containerRequest.getBaseUri();
//		String domain = uri.getHost();
		String path = uri.getPath();
		logger.debug(uri+" "+uri2);
		
//		DBApps db = new DBApps();
//		String appSecret = db.getAppSecretByDomain(domain);
//		if(appSecret == null){
//			db.close();
//			throw new WebApplicationException(401);
//		}
		// Read the OAuth parameters from the request
//        OAuthServerRequest request = new OAuthServerRequest(containerRequest);
//        OAuthParameters params = new OAuthParameters();
//        params.readRequest(request);
        
        // Set the secret(s), against which we will verify the request
//        OAuthSecrets secrets = new OAuthSecrets();
//        secrets.setTokenSecret(appSecret);
        
        // TODO... secret setting code ...
        
        // Check that the timestamp has not expired
//        String timestampStr = params.getTimestamp();
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
        return containerRequest;
	}

}
