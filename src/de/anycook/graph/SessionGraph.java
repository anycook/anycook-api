package de.anycook.graph;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import anycook.misc.JsonpBuilder;
import anycook.session.Session;
import anycook.user.User;

@Path("session")
public class SessionGraph {
	
	@GET
	public Response getSession(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession(true));
		session.checkLogin(hh.getCookies());
		User user = session.getUser();
		return JsonpBuilder.buildResponse(callback, user);
	}
	
	@GET
	@Path("login")
	public Response login(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@QueryParam("callback") String callback,
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("stayloggedin") boolean stayloggedin){
		Session session = Session.init(request.getSession(true));
		session.login(username, password);
		User user = session.getUser();
		ResponseBuilder response = Response.ok(JsonpBuilder.build(callback, user));
		if(stayloggedin){
			NewCookie cookie = new NewCookie("anycook", session.makePermanentCookieId(user.id), "/", "anycook.de", "", 14 * 24 * 60 *60, true);
			response.cookie(cookie);
			
		}
		return response.build();
	}
	
	@GET
	@Path("logout")
	public Response logout(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		Map<String, Cookie> cookies = hh.getCookies();
		session.checkLogin(hh.getCookies());
		
		ResponseBuilder response = Response.ok();
		if(cookies.containsKey("anycook")){
			Cookie cookie = cookies.get("anycook");
			session.deleteCookieID(cookie.getValue());
			NewCookie newCookie = new NewCookie(cookie, "", -1, false);
			response.cookie(newCookie);
		}
		session.logout();
		return response.entity(JsonpBuilder.build(callback, "true")).build();
	}
}
