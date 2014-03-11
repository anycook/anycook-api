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

package de.anycook.social.tumblr.servlets;
//package de.anycook.tumblr.servlets;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.scribe.model.Token;
//
//import de.anycook.service.Tumblr;
//
//
///**
// * Servlet implementation class TumblrAuth
// */
//public class TumblrAuth extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//	private Logger logger;
//	
//    public TumblrAuth() {
//        super();
//        logger = Logger.getLogger(getClass());
//    }
//
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		Tumblr tumblr = Tumblr.init(request.getSession());
//		
//		String verifier = request.getParameter("oauth_verifier");
//		String token = request.getParameter("oauth_token");
//		
//		logger.info(verifier + " " + token);
//		
//		Token rawResponse = tumblr.exchangeRequestForAccess(verifier, null);
//		PrintWriter pw = response.getWriter();
//		pw.println(rawResponse);
//		
//		List<String> blogs = Tumblr.getUserBlogs(null);
//		pw.println(blogs);
//		pw.println("");
//		String blogHostname = blogs.getName(0)+".tumblr.com";
//		String bla = Tumblr.post(null, blogHostname, "Hallo Tumblr!", "de.anycook.de", "http://api.anycook.de/recipe/Yakitori/image?type=large",
//				"de.anycook, recipe");
//		pw.println(bla);
//	}
//
//}
