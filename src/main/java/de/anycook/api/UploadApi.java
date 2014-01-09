/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.api;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;

import de.anycook.session.Session;
import de.anycook.upload.RecipeUploader;
import de.anycook.upload.UploadHandler;
import de.anycook.upload.UserUploader;


@Path("upload")
public class UploadApi {
	private final Logger logger;
	
	public UploadApi() {
		logger = Logger.getLogger(getClass());
	}
	
	
	@POST
	@Path("image/{type}")
	public Response uploadRecipeImage(@Context HttpServletRequest request,
			@Context HttpHeaders hh,
			@PathParam("type") String type){
		
		UploadHandler upload;
		Session session = Session.init(request.getSession());
		
		switch (type) {
		case "recipe":
			upload = new RecipeUploader();
			break;
		case "user":
            try {
                session.checkLogin(hh.getCookies());
            } catch (IOException e) {
                logger.error(e, e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
            upload = new UserUploader();
			break;
		default:
			throw new WebApplicationException(400);
		}
        File tempFile;
        try {
            tempFile = upload.uploadFile(request);
        } catch (IOException | FileUploadException e) {
            logger.error(e,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        if(tempFile!=null){
            try{
                String newFilename = upload.saveFile(tempFile);
                if(type.equals("user"))
                    session.getUser().setImage(newFilename);

                return  Response.ok("{success:\""+newFilename+"\"}").build();
            } catch (SQLException | IOException e) {
                logger.error(e, e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }

        }
		else{
			logger.warn("upload failed");
			return Response.status(400).entity("{error:\"upload failed\"}").build();
		}
			
		
	}
	
	
}
