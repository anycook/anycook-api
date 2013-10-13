/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2013 Jan Graßegger
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

package de.anycook.api.filter.cors;

import de.anycook.conf.Configuration;
import org.apache.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Adds Access-Control-Allow headers to response
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
	public final static Pattern hostPattern = Pattern.compile("http://(.*)anycook\\.de");
	private final Logger logger;
	
	/**
	 * 
	 */
	public CorsFilter() {
		logger = Logger.getLogger(getClass());
	}
	
	/* (non-Javadoc)
	 * @see com.sun.jersey.spi.container.ContainerResponseFilter#filter(com.sun.jersey.spi.container.ContainerRequest, com.sun.jersey.spi.container.ContainerResponse)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
		logger.debug(String.format("filtering: %s:%s", method, path));
		
		CORSRequestType corsType = CORSRequestType.detect(requestContext);
		
		switch(corsType){
		case PREFLIGHT:
			filterPreFlight(requestContext, responseContext);
			
		case ACTUAL:
			filterActualRequest(requestContext, responseContext);
		}
	}

    private void filterPreFlight(ContainerRequestContext requestContext,
                                 ContainerResponseContext responseContext) {
        String origin = requestContext.getHeaderString("Origin");
        if(!checkOrigin(origin))
            responseContext.setStatusInfo(Response.Status.FORBIDDEN);

        String methodHeader = requestContext.getHeaderString("Access-Control-Request-Method");
        if(methodHeader == null)
            responseContext.setStatusInfo(Response.Status.FORBIDDEN);

        String requestHeaders =
                requestContext.getHeaderString("Access-Control-Request-Headers");
        logger.info("requestHeader: "+requestHeaders);

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", origin);
        headers.putSingle("Access-Control-Allow-Credentials", "true");
        headers.putSingle("Access-Control-Allow-Methods", methodHeader);
        headers.putSingle("Access-Control-Allow-Headers", "x-requested-with," +
               (requestHeaders == null ? "" : requestHeaders));
    }
	
	private void filterActualRequest(ContainerRequestContext requestContext,
                                     ContainerResponseContext responseContext) {
		String origin = requestContext.getHeaderString("Origin");
		if(!checkOrigin(origin))
            responseContext.setStatusInfo(Response.Status.FORBIDDEN);

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", origin);
        headers.putSingle("Access-Control-Allow-Credentials", "true");
	}




	public static boolean checkOrigin(String origin){
        if(Configuration.isInDeveloperMode()) return true;

		if(origin == null)
			return false;
		Matcher hostMatcher = hostPattern.matcher(origin);
		return hostMatcher.matches();
	}

	/**
	 * @param hh
	 * @return
	 */
//	public static Response buildResponse(String origin) {
////		if(!checkOrigin(origin))
////			throw new WebApplicationException(401);
//		
//		ResponseBuilder resp = Response.ok();
//		resp.header("Access-Control-Allow-Origin", origin);
//		resp.header("Access-Control-Allow-Credentials", "true");
//		return resp.build();
//	}

	
	
	

}
