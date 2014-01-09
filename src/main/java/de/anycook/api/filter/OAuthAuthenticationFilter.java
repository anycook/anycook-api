/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.api.filter;

import java.net.URI;
import org.apache.log4j.Logger;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class OAuthAuthenticationFilter implements ContainerRequestFilter {
	private final Logger logger;
	
	public OAuthAuthenticationFilter(){
		logger = Logger.getLogger(getClass());
	}
	

	@Override
	public void filter(ContainerRequestContext containerRequest) {
		URI uri = containerRequest.getUriInfo().getRequestUri();
//		URI referer;
//		try {
//			String refererStr = containerRequest.getHeaderValue("referer");
//			referer = new URI(refererStr);
//		} catch (URISyntaxException | NullPointerException e) {
//			throw new WebApplicationException(401);
//		}
//		String refererDomain = referer.getHost();
//		if(refererDomain.endsWith("anycook.de")){
//			return containerRequest;
//		}
		
		
		
		//use path to check if de.anycook.oauth is needed
		/*String path = uri.getPath();
		logger.debug(path);
		if(path.startsWith("oauth"))
			containerReque     */
		
//		logger.debug(uri);
		
//		DBApps db = new DBApps();
//		String appSecret = db.getAppSecretByDomain(refererDomain);
//		Integer appID = db.getAppIDbyDomain(refererDomain);
//		db.close();
//		if(appSecret == null || appID == null){
//			throw new WebApplicationException(401);
//		}
//		
//		String clientAppIDStr = containerRequest.getQueryParameters().getFirst("appid");
//		
//		if(clientAppIDStr == null){
//			throw new WebApplicationException(401);
//		}
//		
//		Integer clientAppID = Integer.parseInt(clientAppIDStr);
//		if(clientAppID != appID)
//			throw new WebApplicationException(401);
//		
//		
//		// Read the OAuth parameters from the request
//        OAuthServerRequest request = new OAuthServerRequest(containerRequest);
//        OAuthParameters params = new OAuthParameters();
//        params.readRequest(request);
//        
//        // Set the secret(s), against which we will verify the request
//        OAuthSecrets secrets = new OAuthSecrets();
//        secrets.setTokenSecret(appSecret);
//        secrets.setConsumerSecret(appID.toString());
//        
//        // TODO... secret setting code ...
//        
//        // Check that the timestamp has not expired
//        String timestampStr = params.getTimestamp();
//        // ... timestamp checking code ...
//        
//        // Verify the signature
////        try {
////            if(!OAuthSignature.verify(request, params, secrets)) {
////                throw new WebApplicationException(401);
////            }
////        } catch (OAuthSignatureException e) {
////            throw new WebApplicationException(e, 401);
////        }
//        
//        // Return the request
//        db.close();
        //return containerRequest;
	}

}
