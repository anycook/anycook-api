//package de.anycook.graph.oauth;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.ext.Provider;
//
//import com.sun.jersey.core.util.MultivaluedMapImpl;
//import com.sun.jersey.de.anycook.oauth.server.spi.OAuthConsumer;
//import com.sun.jersey.de.anycook.oauth.server.spi.OAuthProvider;
//import com.sun.jersey.de.anycook.oauth.server.spi.OAuthToken;
//
//import de.anycook.db.mysql.DBApps;
//
//@Provider
//public class AnycookOAuthProvider implements OAuthProvider{
//	private static final Map<String, OAuthToken> requestTokens;
//	private static final Map<String ,OAuthToken> accessTokens;
//	private static final Map<String , OAuthConsumer> verifierTokens;
//	private static final Map<String, Integer> verifierUser;
//	
//	static{
//		requestTokens = new HashMap<>();
//		accessTokens = new HashMap<>();
//		verifierTokens = new HashMap<>();
//		verifierUser = new HashMap<>();
//	}
//	
//
//	@Override
//	public OAuthToken getAccessToken(String token) {
//		return accessTokens.get(token);
//	}
//
//	@Override
//	public OAuthConsumer getConsumer(String consumerKey) {
//		return AnycookOAuthConsumer.init(consumerKey);
//	}
//
//	@Override
//	public OAuthToken getRequestToken(String token) {
//		if(token == null)
//			return null;
//		return requestTokens.get(token);
//	}
//
//	@Override
//	public OAuthToken newAccessToken(OAuthToken requestToken, String verifier) {
//		OAuthConsumer consumer = verifierTokens.get(verifier);
//		if(consumer !=null && requestToken.getConsumer().equals(consumer)){
//			verifierTokens.remove(verifier);
//			int userId = verifierUser.get(verifier);
//			verifierUser.remove(verifier);
//			String token = newUUIDString();
//			String secret = newUUIDString();
//			DBApps dbapps = new DBApps();
//			dbapps.setUserOAuthToken(userId, consumer.getKey(), token, secret);
//			dbapps.close();
//			return new AnycookOAuthToken(token, secret, consumer.getKey(), null, null);
//		}
//		
//		return null;
//	}
//	
////	public boolean verify(String verifier, String appID){
////		OAuthConsumer consumer = verifierTokens.get(verifier);
////		if(consumer == null)
////			return false;
////		
////		verifierTokens.remove(verifier);
////		return appID.equals(consumer.getKey());
////	}
//	
//	public String getVerifier(OAuthConsumer consumer, int userId){
//		String verifier = newUUIDString();
//		verifierTokens.put(verifier, consumer);
//		verifierUser.put(verifier, userId);
//		return verifier;
//	}
//	
//	
//
//	@Override
//	public OAuthToken newRequestToken(String consumerKey, String callbackUrl,
//			Map<String, List<String>> attributes) {
//		String token = newUUIDString();
//		String secret = newUUIDString();
//		OAuthToken request_token = 
//				new AnycookOAuthToken(token, secret, consumerKey, callbackUrl, attributes);
//		requestTokens.put(token, request_token);
//		return request_token;
//	}
//	
//	public static MultivaluedMap<String, String> newImmutableMultiMap(
//			Map<String, List<String>> source){
//		if (source == null) {
//			return ImmutableMultiMap.EMPTY;
//		}
//		return new ImmutableMultiMap(source);
//	}
//	
//	protected String newUUIDString() {
//		String tmp = UUID.randomUUID().toString();
//		return tmp.replaceAll("-", "");
//	}
//	
//	private static class ImmutableMultiMap extends MultivaluedMapImpl {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//		public static final ImmutableMultiMap EMPTY = new ImmutableMultiMap();
//		
//		private ImmutableMultiMap() {}
//		 
//		ImmutableMultiMap(Map<String, List<String>> source) {
//			for (Map.Entry<String, List<String>> e : source.entrySet()) {
//				super.put(e.getKey(), e.getValue() == null ? 
//						Collections.<String>emptyList() : 
//						Collections.unmodifiableList(new ArrayList<String>(e.getValue())));
//			}
//		}
//		 
//		@Override
//		public List<String> put(String k, List<String> v) {
//			throw new UnsupportedOperationException();
//		}
//		 
//		@Override
//		public Set<Entry<String, List<String>>> entrySet() {
//			return Collections.unmodifiableSet(super.entrySet());
//		}
//		 
//		@Override
//		public Set<String> keySet() {
//			return Collections.unmodifiableSet(super.keySet());
//		}
//		 
//		@Override
//		public List<String> remove(Object o) {
//			throw new UnsupportedOperationException();
//		}
//		 
//		@Override
//		public void putAll(Map<? extends String, ? extends List<String>> map) {
//			throw new UnsupportedOperationException();
//		}
//		 
//		@Override
//		public Collection<List<String>> values() {
//			return Collections.unmodifiableCollection(super.values());
//		}
//	}
//
//}
