package de.anycook.api;

import de.anycook.db.mysql.DBUser;
import de.anycook.mailprovider.MailProvider;
import de.anycook.session.Session;
import de.anycook.user.User;
import de.anycook.user.settings.MailSettings;
import de.anycook.user.settings.Settings;
import de.anycook.utils.JsonpBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@Path("session")
public class SessionGraph {
	
	private final Logger logger;
	
	public SessionGraph() {
		logger = Logger.getLogger(getClass());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getSession(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession(true));
        session.checkLogin(hh.getCookies());
        return session.getUser();
	}
	
	@GET
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response login(@Context HttpServletRequest request,
			@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("stayLoggedIn") boolean stayLoggedIn){
		
		Session session = Session.init(request.getSession(true));
        try{
            session.login(username, password);
            User user = session.getUser();
            ResponseBuilder response = Response.ok(user);
            logger.info(String.format("stayLoggedIn is %s", stayLoggedIn));
            if(stayLoggedIn){
                NewCookie cookie = new NewCookie("anycook", session.makePermanentCookieId(user.getId()), "/", ".anycook.de", "", 7 * 24 * 60 *60, false);
                response.cookie(cookie);
            }

            return response.build();
        }catch(DBUser.UserNotFoundException e){
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("logout")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response logout(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		Map<String, Cookie> cookies = hh.getCookies();
        session.checkLogin(hh.getCookies());

        ResponseBuilder response = Response.ok();
		if(cookies.containsKey("anycook")){
			Cookie cookie = cookies.get("anycook");
            try {
                session.deleteCookieID(cookie.getValue());
            } catch (SQLException e) {
                logger.error(e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
            NewCookie newCookie = new NewCookie(cookie, "", -1, false);
			response.cookie(newCookie);
		}
		session.logout();
		return response.entity(JsonpBuilder.build(callback, "true")).build();
	}
	
	@POST
	@Path("activate")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response activateAccount(@FormParam("activationkey") String activationKey){
        boolean check;
        try {
            check = User.activateById(activationKey);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.ok(Boolean.toString(check)).build();
	}
	
	
	//settings
	
	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getSettings(@Context HttpServletRequest request,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		User user = session.getUser();
        MailSettings mailsettings;
        try {
            mailsettings = MailSettings.init(user.getId());
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        Map<String, Settings> settings = new HashMap<>();
		settings.put("mail", mailsettings);
		return JsonpBuilder.buildResponse(callback, settings);
	}
	
	@GET
	@Path("mailprovider")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response checkMailAnbieter(@QueryParam("domain") String domain){
		if(domain == null) 
			throw new WebApplicationException(401);
        MailProvider provider = null;
        try {
            provider = MailProvider.getMailanbieterfromDomain(domain);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        if(provider != null)
			return JsonpBuilder.buildResponse(null, provider);
		return JsonpBuilder.buildResponse(null, ""); 
	}
	
	@POST
	@Path("settings/account/{type}")
	public Response changeAccountSettings(@Context HttpServletRequest request,
			@Context HttpHeaders hh,
			@PathParam("type") String type,
			@FormParam("value") String value){
		Session session = Session.init(request.getSession());
		
		session.checkLogin(hh.getCookies());
		
		
		User user = session.getUser();
		boolean check = false;

        try {
            switch (type) {
                case "text":
                    check = user.setText(value);
                    break;
                case "place":
                    check = user.setPlace(value);
                    break;
                case "name":
                    check = user.setName(value);
                    break;

                default:
                    throw new WebApplicationException(404);
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


        if(!check){
			logger.warn("check failed");
			throw new WebApplicationException(400);
		}
		
		return Response.ok("true").build();
		
	}
	
	@PUT
	@Path("settings/mail/{type}")
	public Response addMailSettings(@Context HttpServletRequest request,
			@Context HttpHeaders hh, 
			@PathParam("type") String type){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());

        try{
            MailSettings settings = MailSettings.init(session.getUser().getId());
            logger.debug("add mailtype:"+type);

            if(type.equals("all")){
                settings.changeAll(true);
            }else{
                switch (type.toLowerCase()) {
                    case "recipeactivation":
                        settings.setRecipeactivation(true);
                        break;
                    case "recipediscussion":
                        settings.setRecipediscussion(true);
                        break;
                    case "tagaccepted":
                        settings.setTagaccepted(true);
                        break;
                    case "tagdenied":
                        settings.setTagdenied(true);
                        break;
                    case "discussionanswer":
                        settings.setDiscussionanswer(true);
                        break;
                    case "schmeckt":
                        settings.setSchmeckt(true);
                        break;

                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


        return Response.ok().build();
	}
	
	@DELETE
	@Path("settings/mail/{type}")
	public Response removeMailSettings(@Context HttpServletRequest request,
			@Context HttpHeaders hh, 
			@PathParam("type") String type){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
        try{
            MailSettings settings = MailSettings.init(session.getUser().getId());
            logger.debug("remove mailtype:"+type);

            if(type.equals("all")){
                settings.changeAll(false);
            }else{
                switch (type.toLowerCase()) {
                case "recipeactivation":
                    settings.setRecipeactivation(false);
                    break;
                case "recipediscussion":
                    settings.setRecipediscussion(false);
                    break;
                case "tagaccepted":
                    settings.setTagaccepted(false);
                    break;
                case "tagdenied":
                    settings.setTagdenied(false);
                    break;
                case "discussionanswer":
                    settings.setDiscussionanswer(false);
                    break;
                case "schmeckt":
                    settings.setSchmeckt(false);
                    break;

                default:
                    break;
                }
            }
        } catch (SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
		
		return Response.ok().build();
	}
}
