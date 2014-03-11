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

package de.anycook.social.servlets;
//package de.anycook.service.servlets;
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
//import org.scribe.model.Token;
//
//import de.anycook.service.Tumblr;
//import de.anycook.session.Session;
//
//
///**
// * Servlet implementation class PostRecipe
// */
//public class PostRecipe extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
//    public PostRecipe() {
//        super();
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		request.setCharacterEncoding("UTF-8");
//		response.setCharacterEncoding("UTF-8");
//		String recipeName = request.getParameter("recipeName");
//		Tumblr tumblr = Tumblr.init(request.getSession());
//		Token accessToken = tumblr.getAccessToken();
//		if(accessToken == null){
//			Session session = Session.init(request.getSession());
//			accessToken = tumblr.getAccessToken(session.getUser().id);
//		}
//		PrintWriter pw = response.getWriter();
//		List<String> blognames = Tumblr.getUserBlogs(accessToken);
//		pw.println(recipeName + " "+ blognames.toString());
//		pw.println(Tumblr.postRecipe(accessToken, recipeName, blognames.getName(0)+".tumblr.com"));
//	}
//
//}
