package de.anycook.graph;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.mysql.jdbc.Messages;

import de.anycook.graph.listener.StartListener;
import de.anycook.graph.servlets.discussion.GetDiscussion;
import de.anycook.graph.servlets.message.GetMessageNumber;
import de.anycook.graph.servlets.message.GetMessagesession;

public class Graph extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(StartListener.class);
		classes.add(UserGraph.class);
		classes.add(RecipeGraph.class);
		classes.add(CategoryGraph.class);
		classes.add(IngredientGraph.class);
		classes.add(SearchGraph.class);
		classes.add(TagGraph.class);
		classes.add(MessageGraph.class);
		classes.add(SessionGraph.class);
		classes.add(GetMessagesession.class);
		classes.add(GetMessageNumber.class);
		classes.add(Messages.class);
		classes.add(DiscoverGraph.class);
		classes.add(GetDiscussion.class);
		classes.add(UploadGraph.class);
		return classes;
	}
}
