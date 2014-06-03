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

import de.anycook.conf.Configuration;
import de.anycook.session.Session;
import de.anycook.upload.RecipeUploader;
import de.anycook.upload.UploadHandler;
import de.anycook.upload.UserUploader;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;


@Path("upload")
public class UploadApi {
	private final Logger logger;
	
	public UploadApi() {
		logger = Logger.getLogger(getClass());
	}
	
	
	@POST
	@Path("image/{type}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadRecipeImage(@Context HttpServletRequest request,
			@Context HttpHeaders hh,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
			@PathParam("type") String type){
		
		UploadHandler upload;
		Session session = Session.init(request.getSession());
		
		switch (type) {
		case "recipe":
			upload = new RecipeUploader();
			break;
		case "user":
            session.checkLogin();
            upload = new UserUploader();
			break;
		default:
			throw new WebApplicationException(400);
		}
        File tempFile;
        try {
            tempFile = upload.uploadFile(uploadedInputStream);
        } catch (IOException | FileUploadException e) {
            logger.error(e,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                uploadedInputStream.close();
            } catch (IOException e) {
                logger.error(e, e);
            }
        }

        if(tempFile == null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

        try{
            String newFilename = upload.saveFile(tempFile);
            if(type.equals("user"))
                session.getUser().setImage(newFilename);

            String basePath = Configuration.getInstance().getImageBasePath();
            String path = String.format("%s%s/big/%s", basePath, type, newFilename);
            return  Response.created(new URI(path)).build();
        } catch (SQLException | IOException | URISyntaxException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	
}
