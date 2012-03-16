package de.anycook.graph;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import anycook.session.Session;
import anycook.upload.RecipeUploader;
import anycook.upload.UploadHandler;
import anycook.upload.UserUploader;

@Path("upload")
public class UploadGraph {
	
	@OPTIONS
	@Path("image/{type}")
	public Response setAllowOrigins(){
		ResponseBuilder response = Response.ok();
		response.header("Access-Control-Allow-Origin", "*");
		response.header("Access-Control-Allow-Methods", "POST, OPTIONS");
		response.header("Access-Control-Allow-Headers", "X-Requested-With, X-File-Name, Content-Type");
		response.header("Access-Control-Max-Age", "180");
		return response.build();
	}
	
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
				return Response.status(400).entity("unknown type")
						.header("Access-Control-Allow-Origin", "*").build();
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
		return response.header("Access-Control-Allow-Origin", "*").build();
		
	}
	
	
}
