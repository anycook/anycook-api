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

import de.anycook.social.Social;
import de.anycook.social.Tumblr;
import de.anycook.social.Twitter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Servlet implementation class ServiceAuthURL
 */
public class ServiceAuthURL extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServiceAuthURL() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");


        String serviceid = request.getParameter("serviceId");
        Social service = null;
        switch (Integer.parseInt(serviceid)) {
            case 1:
                service = Tumblr.init(request.getSession());
                break;
            case 2:
                service = Twitter.init(request.getSession());
            default:
                break;
        }
        PrintWriter pw = response.getWriter();
        pw.print(service.getAuthUrl());
    }

}
