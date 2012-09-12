package de.anycook.graph;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import de.anycook.session.Session;
import de.anycook.upload.RecipeUploader;
import de.anycook.upload.UploadHandler;
import de.anycook.upload.UserUploader;


@Path("upload")
public class UploadGraph {
	
	@POST
	@Path("image/{type}")
	public Response uploadRecipeImage(@Context HttpServletRequest request,
			@Context HttpHeaders hh,
			@PathParam("type") String type){
		
		
		ResponseBuilder response = null;
		UploadHandler upload = null;
		try{
			switch (type) {
			case "recipe":
				upload = new RecipeUploader();
				break;
			case "user":
				Session session = Session.init(request.getSession());
				session.checkLogin(hh.getCookies());
				upload = new UserUploader(session.getUser());
				break;
			default:
				return Response.status(400).entity("unknown type").build();
			}
			File tempfile = upload.uploadFile(request);		
			if(tempfile!=null){
				String newFilename = upload.saveFile(tempfile);
				response =  Response.ok("{success:\""+newFilename+"\"}");									
			}
			else
				response = Response.status(400).entity("{error:\"upload failed\"}");
		}catch(WebApplicationException e){
			response = Response.status(401);
		}
		return response.build();
		
	}
	
	
}
