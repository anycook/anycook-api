//package de.anycook.graph.oauth;
//
//import java.security.Principal;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.ws.rs.core.MultivaluedMap;
//
//import com.sun.jersey.de.anycook.oauth.server.spi.OAuthConsumer;
//import com.sun.jersey.de.anycook.oauth.server.spi.OAuthToken;
//
//public class AnycookOAuthToken implements OAuthToken {
//	private final String consumerKey;
//	private final String token;
//	private final String secret;
//	private final Principal principal;
//	private final MultivaluedMap<String, String> attributes;
//	private final Set<String> roles;
//	
//	
////	protected AnycookOAuthToken(String token, String secret, String consumerKey, 
////			Principal principal, Map<String, List<String>> attributes, 
////			Set<String> roles) {
////		this.token = token;
////		this.secret = secret;
////		this.consumerKey = consumerKey;
////		this.principal = principal;
////		this.attributes = AnycookOAuthProvider.newImmutableMultiMap(attributes);
////		this.roles = roles;
////	}
//	
//	public AnycookOAuthToken(String token, String secret, String consumerKey, 
//			String callbackURL, Map<String, List<String>> attributes){
//		this.token = token;
//		this.secret = secret;
//		this.consumerKey = consumerKey;
//		this.principal = new CallbackPrincipal(callbackURL);
//		this.attributes = AnycookOAuthProvider.newImmutableMultiMap(attributes);
//		this.roles = new HashSet<>();
//	}
//
//	@Override
//	public MultivaluedMap<String, String> getAttributes() {
//		return attributes;
//	}
//
//	@Override
//	public OAuthConsumer getConsumer() {
//		return AnycookOAuthConsumer.init(consumerKey);
//	}
//
//	@Override
//	public Principal getPrincipal() {
//		return principal;
//	}
//
//	@Override
//	public String getSecret() {
//		return secret;
//	}
//
//	@Override
//	public String getToken() {		
//		return token;
//	}
//
//	@Override
//	public boolean isInRole(String role) {
//		return roles.contains(role);
//	}
//	
//	@Override
//	public String toString() {
////		 The response contains the following REQUIRED parameters:
////
////			   oauth_token
////			         The temporary credentials identifier.
////
////			   oauth_token_secret
////			         The temporary credentials shared-secret.
////
////			   oauth_callback_confirmed
////			         MUST be present and set to "true".  The parameter is used to
////			         differentiate from previous versions of the protocol.
//		
//		StringBuilder responseBuilder = new StringBuilder("oauth_token=");
//		responseBuilder.append(getToken());
//		responseBuilder.append("&oauth_token_secret=").append(getSecret());
//		responseBuilder.append("&oauth_callback_confirmed=true");
//		return responseBuilder.toString();
//		
//	}
//	
//	public static class CallbackPrincipal implements Principal{
//		private final String callbackURL;
//		
//		public CallbackPrincipal(String callbackURL) {
//			this.callbackURL = callbackURL;
//		}
//		
//		@Override
//		public String getName() {
//			return callbackURL;
//		}
//		
//	}
//
//}
