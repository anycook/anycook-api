package de.anycook.graph.discussion;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import com.google.common.base.Preconditions;

import de.anycook.graph.discussion.checker.NewDiscussionChecker;
import de.anycook.session.Session;
import de.anycook.utils.DaemonThreadFactory;



/**
 * Servlet implementation class GetDiscussion
 */
@WebServlet(urlPatterns = "/getdiscussion/*", asyncSupported=true, name="Discussion")
public class GetDiscussion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ExecutorService exec;
    private final int numThreads = 10;
    
    @Override
    public void init() throws ServletException {
    	exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads; i++){
        	exec.execute(new NewDiscussionChecker());
        }
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String path = request.getRequestURI();
		String recipe = path.split("/")[2];
		recipe = URLDecoder.decode(recipe, "UTF-8");
		
		String maxidString = request.getParameter("lastid");
		String callback = request.getParameter("callback");
		int maxid = maxidString == null ? -1 : Integer.parseInt(maxidString);
		
		Session session = Session.init(request.getSession());		
		int userid;
		try{
			session.checkLogin(request.getCookies());
			userid = session.getUser().getId();
		}catch(WebApplicationException e){
			userid = -1;
		}
		
		
		if(recipe!=null){
			recipe = recipe.toLowerCase();
			AsyncContext context = request.startAsync();
			Preconditions.checkNotNull(context);
			context.setTimeout(20000);
			NewDiscussionChecker.addContext(context, recipe, maxid, userid,callback);
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		exec.shutdownNow();
	}

}
