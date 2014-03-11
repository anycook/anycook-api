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
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.scribe.model.Token;
//
//import de.anycook.service.Social;
//import de.anycook.service.Tumblr;
//import de.anycook.service.Twitter;
//import de.anycook.session.Session;
//
//
///**
// * Servlet implementation class ServiceCallback
// */
//public class ServiceCallback extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//	private Logger logger;
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
//    public ServiceCallback() {
//        super();
//        logger = Logger.getLogger(getClass());
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//			request.setCharacterEncoding("UTF-8");
//			response.setCharacterEncoding("UTF-8");
//			
//			String serviceid = request.getParameter("serviceId");
//			
//			String verifier = request.getParameter("oauth_verifier");
//			
//			Session session = Session.init(request.getSession());
//			Integer users_id = null;
//			if(session.checkLogin())
//				users_id = session.getUser().id;
//			
//			Social service = null;
//			switch (Integer.parseInt(serviceid)) {
//				case 1:	// Tumblr
//					service = Tumblr.init(request.getSession());
//					logger.info("New Tumblr Session");
//					break;
//				case 2: // Twitter
//					service = Twitter.init(request.getSession());
//					logger.info("New Twitter Session");
//					break;
//				case 3: // Evernote
//					break;
//	
//				default:
//					break;
//			}			
//			
//			Token accessToken = service.exchangeRequestForAccess(verifier, users_id);
//			PrintWriter pw = response.getWriter();
//			
//			pw.println(accessToken.getRawResponse());
//		
//			}
//}
