package de.anycook.graph.filter.cors;


import com.sun.jersey.spi.container.ContainerRequest;


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
	 * @param request The HTTP request to check. Must not be {@code null}.
	 *
	 * @return The CORS request type.
	 */
	public static CORSRequestType detect(final ContainerRequest request) {
	
		if (request == null)
			throw new NullPointerException("The HTTP request must not be null");
		
		// All CORS request have an Origin header
		if (request.getHeaderValue("Origin") == null)
			return OTHER;
		
		// We have a CORS request - determine type
		if (request.getHeaderValue("Access-Control-Request-Method") != null &&
		    request.getMethod()                                != null &&
		    request.getMethod().equals("OPTIONS")                         )
		    
			return PREFLIGHT;
			
		else
			return ACTUAL;
	}
}
