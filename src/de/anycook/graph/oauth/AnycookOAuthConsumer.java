package de.anycook.graph.oauth;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import com.sun.jersey.oauth.server.spi.OAuthConsumer;

import de.anycook.db.mysql.DBApps;

public class AnycookOAuthConsumer implements OAuthConsumer {
	private final String key;
	private final String secret;
	private final Principal principal;
	private final Set<String> roles;
	
	protected AnycookOAuthConsumer(String key, String secret, Set<String> roles){
		this(key, secret, null, roles);
	}
	
	protected AnycookOAuthConsumer(String key, String secret, Principal principal,
			Set<String> roles){
		this.key = key;
		this.secret = secret;
		this.principal = principal;
		this.roles = roles;
	}
	
	public static AnycookOAuthConsumer init(String consumerKey){
		DBApps apps = new DBApps();
		String secret = apps.getAppSecret(consumerKey);
		apps.close();
		if(secret != null)
			return new AnycookOAuthConsumer(consumerKey, secret, new HashSet<String>());
		return null;
			
	}
	
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Principal getPrincipal() {
		return principal;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public boolean isInRole(String role) {
		return roles.contains(role);
	}

}
