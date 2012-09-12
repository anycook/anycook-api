/**
 * 
 */
package de.anycook.graph.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@WebFilter(servletNames={"MessageSession", "Messages"})
public class CorsServletFilter implements Filter {

	private final Logger logger;
	
	/**
	 * 
	 */
	public CorsServletFilter() {
		logger = Logger.getLogger(getClass());
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse)resp;
		HttpServletRequest request = (HttpServletRequest)req;
		String origin = request.getHeader("Origin");
		String allowMethods = request.getHeader("Access-Control-Request-Method");
		String header = request.getHeader("Access-Control-Request-Headers");
		logger.debug("filtering: "+request.getMethod()+":"+request.getRequestURI());
		
		
		if(CorsFilter.checkOrigin(origin)){
//			String refHost = referer.getHost();
			
			//Quelle: http://stackoverflow.com/questions/5406350/access-control-allow-origin-has-no-influence-in-rest-web-service
			response.addHeader("Access-Control-Allow-Origin", origin); 
			if(header != null && !"".equals(header))
		    	response.addHeader("Access-Control-Allow-Headers", header);
			
			if(allowMethods != null && !"".equals(allowMethods))
				response.addHeader("Access-Control-Allow-Methods", allowMethods);
			response.addHeader("Access-Control-Max-Age", "86400");
			response.addHeader("Access-Control-Allow-Credentials", "true");
		    
		}
		
		logger.debug(request.getMethod()+" "+request.getRequestURI());
		chain.doFilter(request, response);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
