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


import javax.ws.rs.container.ContainerRequestContext;


/**
 * Enumeration of the CORS request types.
 *
 * @author Vladimir Dzhuvinov (modified by Jan Graßegger)
 */
public enum CORSRequestType {

	
	/**
	 * Simple / actual CORS request.
	 */
	ACTUAL,
	
	
	/**
	 * Preflight CORS request.
	 */
	PREFLIGHT,
	
	
	/**
	 * Other (non-CORS) request.
	 */
	OTHER;
	
	
	/**
	 * Detects the CORS type of the specified HTTP request.
	 *
	 * @param requestContext The HTTP request to check. Must not be {@code null}.
	 *
	 * @return The CORS request type.
	 */
	public static CORSRequestType detect(final ContainerRequestContext requestContext) {
		
		// All CORS request have an Origin header
		if (requestContext.getHeaderString("Origin") == null)
			return OTHER;
		
		// We have a CORS request - determine type
		if (requestContext.getHeaderString("Access-Control-Request-Method") != null &&
            requestContext.getMethod() != null &&
            requestContext.getMethod().equals("OPTIONS"))
		    
			return PREFLIGHT;
			
		else
			return ACTUAL;
	}
}
