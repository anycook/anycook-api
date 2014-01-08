package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.ingredient.Ingredient;
import de.anycook.newrecipe.NewRecipe;
import de.anycook.recipe.Recipe;
import de.anycook.session.Session;
import de.anycook.step.Step;
import de.anycook.tag.Tag;
import de.anycook.user.User;
import de.anycook.utils.enumerations.ImageType;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;


@Path("/recipe")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeApi {
	Logger logger = Logger.getLogger(getClass());

	@GET
	public List<String> getAll(@QueryParam("userId") Integer userId){
        try{
            if(userId != null)
                return Recipe.getRecipeNamesFromUser(userId);
            else
                return Recipe.getAll();
        } catch (Exception e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Number of recipes
	 * @return
	 */
	@GET
	@Path("number")
	public Integer getNum(){
        try {
            return Recipe.getTotal();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * returns the recipe of the day
	 * @return
	 */
	@GET
	@Path("oftheday")
	public Recipe getRecipeOfTheDay(){
        try {
            return Recipe.getRecipeOfTheDay();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@GET
	@Path("{recipeName}")
	public Recipe getRecipe(@PathParam("recipeName") String recipeName){
        try {
            return Recipe.init(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@GET
	@Path("{recipeName}/ingredients")
	public List<Ingredient> getRecipeIngredients(@PathParam("recipeName") String recipeName){
        try {
            return Ingredient.loadByRecipe(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/tags")
	public List<String> getRecipeTags(@PathParam("recipeName") String recipeName){
        try {
            return Tag.loadTagsFromRecipe(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("{recipeName}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    public void suggestTags(@Context HttpServletRequest request,
                            @PathParam("recipeName") String recipeName,
                            List<String> tags) {
        Session session = Session.init(request.getSession());
        int userId = session.getUser().getId();

        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()){
            for(String tag : tags)
                dbSaveRecipe.suggestTag(recipeName, tag, userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/steps")
	public List<Step> getRecipeSteps(@PathParam("recipeName") String recipeName){
        try {
            return Step.loadRecipeSteps(recipeName);
        } catch (SQLException e) {
            logger.error(e ,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	

    //version
	@GET
	@Path("{recipeName}/{versionid}")
	public Recipe getVersion(@PathParam("recipeName") String recipeName,
			@PathParam("versionid") int versionid){
        try {
            return Recipe.init(recipeName, versionid);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/{versionId}/ingredients")
    public List<Ingredient> getVersionIngredients(@PathParam("recipeName") String recipeName,
                               @PathParam("versionId") int versionId){
        try {
            return Ingredient.loadByRecipe(recipeName, versionId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/{versionId}/steps")
    public List<Step> getVersionSteps(@PathParam("recipeName") String recipeName,
                                    @PathParam("versionId") int versionId){
        try {
            return Step.loadRecipeSteps(recipeName, versionId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("recipeName") String recipeName,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		try {
			return Response.temporaryRedirect(Recipe.getRecipeImage(recipeName, type)).build();
		} catch (URISyntaxException e) {
			logger.error(e, e);
			throw new WebApplicationException(400);
		} catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/schmeckt")
	public Boolean checkSchmeckt(@PathParam("recipeName") String recipeName,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin();
        try {
            return session.checkSchmeckt(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PUT
	@Path("{recipeName}/schmeckt")
	public void schmeckt(@PathParam("recipeName") String recipeName,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            boolean schmeckt = session.checkSchmeckt(recipeName);
            if(!schmeckt)
                session.makeSchmeckt(recipeName);
        } catch (IOException | SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

		
	}
	
	@DELETE
	@Path("{recipeName}/schmeckt")
	public void schmecktNicht(@PathParam("recipeName") String recipeName,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());


        try {
            session.checkLogin(hh.getCookies());
            boolean schmeckt = session.checkSchmeckt(recipeName);
            if(schmeckt)
                session.removeSchmeckt(recipeName);
        } catch (IOException | SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveRecipe(@Context HttpHeaders hh,
			@Context HttpServletRequest request,			
			NewRecipe newRecipe){
		logger.info("want to save recipe");
		Session  session = Session.init(request.getSession());

		
		if(newRecipe == null)
			throw new WebApplicationException(400);


        try {
            try {
                session.checkLogin(hh.getCookies());
                User user = session.getUser();
                if(!newRecipe.save(user.getId()))
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
            } catch (WebApplicationException e){
                    logger.debug("user is not authentificated");
                if(e.getResponse().getStatus() == 401)
                    if(!newRecipe.save()){
                        logger.warn("bad request");
                        throw new WebApplicationException(Response.Status.BAD_REQUEST);
                    }
                else
                    throw new WebApplicationException(e);

            }
        } catch (SQLException | IOException | ParseException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }
}
