/**
 * 
 */
package de.anycook.graph.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

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
		String origin = request.getHeaderValue("Origin");
		String allowMethods = request.getHeaderValue("Access-Control-Request-Method");
		String header = request.getHeaderValue("Access-Control-Request-Headers");
		logger.debug("filtering: "+request.getMethod()+":"+request.getPath());
		
		
		if(checkOrigin(origin)){
//			String refHost = referer.getHost();
			
			//Quelle: http://stackoverflow.com/questions/5406350/access-control-allow-origin-has-no-influence-in-rest-web-service
			MultivaluedMap<String, Object> headers =resp.getHttpHeaders();
			headers.putSingle("Access-Control-Allow-Origin", origin); 
			
			if(allowMethods != null && !"".equals(allowMethods))
				headers.putSingle("Access-Control-Allow-Methods", "OPTIONS,"+allowMethods);
		    headers.putSingle("Access-Control-Allow-Credentials", "true");
		    headers.putSingle("Access-Control-Max-Age", "600");
		    
		    if(header != null && !"".equals(header))
		    	headers.putSingle("Access-Control-Allow-Headers", header);
		}
		
		return resp;
	}
	
	public static boolean checkOrigin(String origin){
		if(origin == null)
			return false;
		Matcher hostMatcher = hostPattern.matcher(origin);
		return hostMatcher.matches();
	}

	
	
	

}
