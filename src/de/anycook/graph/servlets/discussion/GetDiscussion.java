package de.anycook.graph.servlets.discussion;

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

import de.aitools.aq.check.A;
import de.anycook.discussion.NewDiscussionChecker;
import de.anycook.misc.DaemonThreadFactory;
import de.anycook.session.Session;



/**
 * Servlet implementation class GetDiscussion
 */
@WebServlet(urlPatterns = "/discussion/*", asyncSupported=true)
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
		
		if(recipe!=null){
			recipe = recipe.toLowerCase();
			AsyncContext context = request.startAsync();
			A.CHECK_NOT_NULL(context);
			context.setTimeout(20000);
			NewDiscussionChecker.addContext(context, recipe, maxid, callback);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Session shandler = Session.init(request.getSession());
		if(shandler.checkLogin()){
			String text = request.getParameter("comment");
			String path = request.getRequestURI();
			String recipe = path.split("/")[3];
			recipe = URLDecoder.decode(recipe, "UTF-8");
			if(text!=null && recipe != null){
				String pid = request.getParameter("pid");
				if(pid == null)
					shandler.discuss(text, recipe);
				else
					shandler.answerDiscuss(text, recipe, pid);
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		exec.shutdownNow();
	}

}
