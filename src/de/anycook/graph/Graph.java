package de.anycook.graph;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class Graph extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<>();
		classes.add(UserGraph.class);
		
		
		return classes;
	}
}
