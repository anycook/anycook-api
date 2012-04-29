package de.anycook.graph.servlets.discussion;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import de.anycook.discussion.Discussion;
import de.anycook.session.Session;


/**
 * Servlet implementation class GetNewDiscussions
 */
public class GetNewDiscussions extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewDiscussions() {
        super();
        
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		
		String number = request.getParameter("number");
		Session sessionh = Session.init(request.getSession());
		if(sessionh.getUser().getLevel()==2 && number!=null){
			String json = JSONArray.toJSONString(Discussion.getLatestDiscussions(Integer.parseInt(number)));
			writer.write(json);
		}
		writer.close();
	}

}
