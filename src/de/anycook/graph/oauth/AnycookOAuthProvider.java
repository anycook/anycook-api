package de.anycook.graph.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.oauth.server.api.providers.DefaultOAuthProvider;
import com.sun.jersey.oauth.server.spi.OAuthConsumer;
import com.sun.jersey.oauth.server.spi.OAuthProvider;
import com.sun.jersey.oauth.server.spi.OAuthToken;

@Provider
public class AnycookOAuthProvider implements OAuthProvider{
	

	@Override
	public OAuthToken getAccessToken(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuthConsumer getConsumer(String consumerKey) {
		return AnycookOAuthConsumer.init(consumerKey);
	}

	@Override
	public OAuthToken getRequestToken(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuthToken newAccessToken(OAuthToken requestToken, String verifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OAuthToken newRequestToken(String consumerKey, String callbackUrl,
			Map<String, List<String>> attributes) {
		String token = newUUIDString();
		String secret = newUUIDString();
		OAuthToken request_token = 
				new AnycookOAuthToken(token, secret, consumerKey, attributes);
		return request_token;
	}
	
	public static MultivaluedMap<String, String> newImmutableMultiMap(
			Map<String, List<String>> source){
		if (source == null) {
			return ImmutableMultiMap.EMPTY;
		}
		return new ImmutableMultiMap(source);
	}
	
	protected String newUUIDString() {
		String tmp = UUID.randomUUID().toString();
		return tmp.replaceAll("-", "");
	}
	
	private static class ImmutableMultiMap extends MultivaluedMapImpl {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public static final ImmutableMultiMap EMPTY = new ImmutableMultiMap();
		
		private ImmutableMultiMap() {}
		 
		ImmutableMultiMap(Map<String, List<String>> source) {
			for (Map.Entry<String, List<String>> e : source.entrySet()) {
				super.put(e.getKey(), e.getValue() == null ? 
						Collections.<String>emptyList() : 
						Collections.unmodifiableList(new ArrayList<String>(e.getValue())));
			}
		}
		 
		@Override
		public List<String> put(String k, List<String> v) {
			throw new UnsupportedOperationException();
		}
		 
		@Override
		public Set<Entry<String, List<String>>> entrySet() {
			return Collections.unmodifiableSet(super.entrySet());
		}
		 
		@Override
		public Set<String> keySet() {
			return Collections.unmodifiableSet(super.keySet());
		}
		 
		@Override
		public List<String> remove(Object o) {
			throw new UnsupportedOperationException();
		}
		 
		@Override
		public void putAll(Map<? extends String, ? extends List<String>> map) {
			throw new UnsupportedOperationException();
		}
		 
		@Override
		public Collection<List<String>> values() {
			return Collections.unmodifiableCollection(super.values());
		}
	}

}
