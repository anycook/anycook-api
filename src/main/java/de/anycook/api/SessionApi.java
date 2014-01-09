package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBUser;
import de.anycook.mailprovider.MailProvider;
import de.anycook.mailprovider.db.DBMailProvider;
import de.anycook.session.LoginAttempt;
import de.anycook.session.Session;
import de.anycook.user.User;
import de.anycook.user.settings.MailSettings;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


@Path("session")
public class SessionApi {
	
	private final Logger logger;
	
	public SessionApi() {
		logger = Logger.getLogger(getClass());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getSession(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession(true));
        try {
            session.checkLogin(hh.getCookies());
            return session.getUser();
        } catch (IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response login(@Context HttpServletRequest request,
			Session.UserAuth auth){
		
		Session session = Session.init(request.getSession(true));
        LoginAttempt loginAttempt = null;

        try{
            int userId = User.getUserId(auth.username);
            if(!LoginAttempt.isLoginAllowed(userId)) {
                logger.warn("too many login attempts for "+userId);
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }

            loginAttempt = new LoginAttempt(userId, request.getRemoteAddr(), System.currentTimeMillis());


            session.login(userId, auth.password);
            loginAttempt.setSuccessful(true);
            User user = session.getUser();
            ResponseBuilder response = Response.ok(user);

            if(auth.stayLoggedIn){
                logger.debug(String.format("stayLoggedIn"));
                NewCookie cookie = new NewCookie("anycook", session.makePermanentCookieId(user.getId()), "/", ".anycook.de", "", 7 * 24 * 60 *60, false);
                response.cookie(cookie);
            }

            return response.build();
        }catch(User.LoginException|DBUser.UserNotFoundException e){
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            if(loginAttempt != null) try {
                loginAttempt.save();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
    }
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response logout(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		Map<String, Cookie> cookies = hh.getCookies();
        try {
            session.checkLogin(hh.getCookies());
        } catch (IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

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
		return response.entity("true").build();
	}
	
	@POST
	@Path("activate")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public void activateAccount(@FormParam("activationkey") String activationKey){
        try {
            User.activateById(activationKey);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.ActivationFailedException e) {
            logger.warn(e,e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
	
	
	//settings
	
	@GET
	@Path("settings")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public MailSettings getSettings(@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		User user = session.getUser();
        MailSettings mailsettings;
        try {
            return MailSettings.init(user.getId());
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("mailprovider")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public MailProvider checkMailAnbieter(@QueryParam("domain") String domain){
		if(domain == null) 
			throw new WebApplicationException(401);
        try {
            return MailProvider.getMailanbieterfromDomain(domain);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBMailProvider.ProviderNotFoundException e) {
            logger.debug(e);
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }


	}
	
	@POST
	@Path("settings/account/{type}")
	public Response changeAccountSettings(@Context HttpServletRequest request,
			@Context HttpHeaders hh,
			@PathParam("type") String type,
			@FormParam("value") String value){
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            User user = session.getUser();
            boolean check;

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

            if(!check){
                logger.warn("check failed");
                throw new WebApplicationException(400);
            }

            return Response.ok("true").build();
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }



		
	}
	
	@PUT
	@Path("settings/mail/{type}")
	public Response addMailSettings(@Context HttpServletRequest request,
			@Context HttpHeaders hh, 
			@PathParam("type") String type){
		Session session = Session.init(request.getSession());


        try{
            session.checkLogin(hh.getCookies());
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
        } catch (IOException | SQLException e) {
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

        try{
            session.checkLogin(hh.getCookies());
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
        } catch (IOException | SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
		
		return Response.ok().build();
	}
}