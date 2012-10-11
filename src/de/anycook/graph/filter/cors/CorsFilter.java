/**
 * 
 */
package de.anycook.graph.filter.cors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Adds Access-Control-Allow headers to response
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
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
	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse resp) {
		logger.debug("filtering: "+request.getMethod()+":"+request.getPath());
		
		CORSRequestType corsType = CORSRequestType.detect(request);
		
		switch(corsType){
		case PREFLIGHT:
			return filterPreflight(request, resp);
			
		case ACTUAL:
			return filterActualRequest(request, resp);
			
		default:
			return resp;
		}
	}
	
	/**
	 * @param request
	 * @param resp
	 * @return
	 */
	private ContainerResponse filterActualRequest(ContainerRequest request,
			ContainerResponse resp) {
		String origin = request.getHeaderValue("Origin");
		if(!checkOrigin(origin))
			throw new WebApplicationException(401);
		
//		String method = request.getMethod();
		ResponseBuilder respBuilder = Response.fromResponse(resp.getResponse());
		respBuilder.header("Access-Control-Allow-Origin", origin);
		respBuilder.header("Access-Control-Allow-Credentials", "true");		
		resp.setResponse(respBuilder.build());
		
		return resp;
	}

	/**
	 * @param request
	 * @param resp
	 * @return
	 */
	private ContainerResponse filterPreflight(ContainerRequest request,
			ContainerResponse resp) {
		String origin = request.getHeaderValue("Origin");
		if(!checkOrigin(origin))
			throw new WebApplicationException(401);
		
		String methodHeader = request.getHeaderValue("Access-Control-Request-Method");
		if(methodHeader == null)
			throw new WebApplicationException(400);
		
		String requestHeaders = 
				request.getHeaderValue("Access-Control-Request-Headers");
		
		ResponseBuilder respBuilder = Response.fromResponse(resp.getResponse());
		respBuilder.header("Access-Control-Allow-Origin", origin);
		respBuilder.header("Access-Control-Allow-Credentials", "true");
		respBuilder.header("Access-Control-Allow-Methods", methodHeader);
		if(requestHeaders != null && requestHeaders.length() > 0)
			respBuilder.header("Access-Control-Allow-Headers", requestHeaders);
		resp.setResponse(respBuilder.build());
		
		return resp;
	}


	public static boolean checkOrigin(String origin){
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
