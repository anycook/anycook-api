package de.anycook.graph.old.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.anycook.graph.old.GraphFactory;
import de.anycook.graph.old.Graphable;


/**
 * Servlet implementation class Recipe
 */
public class Graph extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Graph() {
        super();
        logger = Logger.getLogger(getClass());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		Enumeration<String> e = request.getParameterNames();
		Map<String, String> data = new HashMap<String, String>();
		while(e.hasMoreElements()){
			String pName = e.nextElement();
			data.put(pName, URLDecoder.decode(request.getParameter(pName), "UTF-8"));
		}
		logger.info("path:"+data.get("path")+
				"\tappid:"+data.get("appid")+
				"\tfrom:"+request.getRemoteAddr()+
				"\theader:"+request.getHeader("User-Agent")+
				"\treferer:"+request.getHeader("Referer"));
		
		
		
		Graphable graph = GraphFactory.create(data);
		
		if(graph == null){
			PrintWriter writer = response.getWriter();
			writer.write("null");
			writer.close();
			return;
		}
			
		
		if(data.containsKey("path") && data.get("path").contains("image")){
			File image =  graph.getImage();
			if(image==null){
				PrintWriter writer = response.getWriter();
				writer.write("null");
				writer.close();
				return;
			}
			response.setContentType("image/png");
				
			//Variante 1
			//response.sendRedirect(imagePath);
			
			//Variante 2
			response.setDateHeader("Last-Modified", image.lastModified());
			RandomAccessFile ra = new RandomAccessFile(image, "r");
			response.setContentLength( (int) ra.length() );
			ServletOutputStream so = response.getOutputStream();
			byte [] loader = new byte [ (int) ra.length() ];
            while ( (ra.read( loader )) > 0 ) {
              so.write( loader );
            }
            ra.close();
            so.flush();
            so.close();
            
			
			
		}else{
			response.setContentType("application/json");
				
			PrintWriter writer = response.getWriter();
			
			String json = graph.getJSON();
			String callback = request.getParameter("callback");
			String text;
			if(callback!=null){
				text = callback+"("+(json!=null ?json: "null")+")";
			}else{
				text = json!=null ?json: "null";
			}
			writer.write(text);
			writer.close();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
