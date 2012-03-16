package de.anycook.graph;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.log4j.Logger;

import anycook.upload.RecipeUploader;
import anycook.upload.UploadHandler;

@Path("upload")
public class UploadGraph {
	
	@OPTIONS
	@Path("recipeimage")
	public Response setAllowOrigins(){
		ResponseBuilder response = Response.ok();
		response.header("Access-Control-Allow-Origin", "*");
		response.header("Access-Control-Allow-Methods", "POST, OPTIONS");
		response.header("Access-Control-Allow-Headers", "X-Requested-With, X-File-Name, Content-Type");
		response.header("Access-Control-Max-Age", "180");
		return response.build();
	}
	
	@POST
	@Path("recipeimage")
	public Response uploadImage(@Context HttpServletRequest request){
		Logger logger = Logger.getLogger(getClass());
		logger.info("uploading image");
		ResponseBuilder response = null;
		UploadHandler upload = new RecipeUploader();
		File tempfile = upload.uploadFile(request);		
		if(tempfile!=null){
			String newFilename = upload.saveFile(tempfile);
			logger.info("finished uploading");
			response =  Response.ok("{success:\""+newFilename+"\"}");
									
		}
		else
			response = Response.status(400).entity("{error:\"upload failed\"}");
		return response.header("Access-Control-Allow-Origin", "*").build();
		
	}
}
