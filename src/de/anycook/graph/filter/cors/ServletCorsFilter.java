package de.anycook.graph.filter.cors;

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
import javax.ws.rs.WebApplicationException;

@WebFilter(servletNames={"Messages", "MessageSession", "MessageNumber", "GetDraftNumber"})
public class ServletCorsFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		CORSRequestType corsType = CORSRequestType.detect(request);
		switch(corsType){
		case PREFLIGHT:
			response =  filterPreflight(request, response);
			break;
			
		case ACTUAL:
			response = filterActualRequest(request, response);
			break;
		default:
			break;
			
		}
		
		chain.doFilter(request, response);

	}


	private HttpServletResponse filterPreflight(HttpServletRequest request,
			HttpServletResponse response) {
		String origin = request.getHeader("Origin");
		if(!CorsFilter.checkOrigin(origin))
			throw new WebApplicationException(401);
		
		String methodHeader = request.getHeader("Access-Control-Request-Method");
		if(methodHeader == null)
			throw new WebApplicationException(400);
		
		String requestHeaders = 
				request.getHeader("Access-Control-Request-Headers");
		
		response.setHeader("Access-Control-Allow-Origin", origin);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", methodHeader);
		if(requestHeaders != null && requestHeaders.length() > 0)
			response.setHeader("Access-Control-Allow-Headers", requestHeaders);
		
		return response;
	}
	
	private HttpServletResponse filterActualRequest(HttpServletRequest request,
			HttpServletResponse response) {
		String origin = request.getHeader("Origin");
		if(!CorsFilter.checkOrigin(origin))
			throw new WebApplicationException(401);
		
//		String method = request.getMethod();+
		response.setHeader("Access-Control-Allow-Origin", origin);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		return response;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
