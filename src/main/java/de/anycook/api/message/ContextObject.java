package de.anycook.api.message;

import javax.servlet.AsyncContext;

public class ContextObject{
	public final AsyncContext context;
	
	public ContextObject(AsyncContext context) {
		this.context = context;
	}
}