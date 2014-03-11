package de.anycook.oauth;
//package de.anycook.test.de.anycook.oauth;
//
//import org.scribe.builder.ServiceBuilder;
//import org.scribe.model.Token;
//import org.scribe.model.Verifier;
//import de.anycook.service.AnycookApi;
//import de.anycook.service.Social;
//import de.anycook.service.db.DBService;
//
//public class AnycookOauthTest extends Social{
//	private final static String consumer_key = "2";
//	private final static String secret_key = "Mj10WoufcTGfQLks";
//	
//	private Token requestToken;
//	private Token accessToken = null;
//	private DBService db;
//	
//	public AnycookOauthTest() {
//		super();
//		service = new ServiceBuilder().provider(AnycookApi.class).
//				apiKey(consumer_key).apiSecret(secret_key).build();
//		requestToken = service.getRequestToken();
//		System.out.println(requestToken);
//		
//		db = new DBService();
//	}
//	
//	
//	@Override
//	public Token exchangeRequestForAccess(String veri_token, Integer users_id) {
//		Verifier verifier = new Verifier(veri_token);
//		accessToken = service.getAccessToken(requestToken, verifier);
//		if(users_id != null)
//			db.addTumblrUser(users_id, accessToken.getToken(), accessToken.getSecret());
//		return accessToken;
//	}
//	
//	@Override
//	public String getAuthUrl() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	public static void main(String[] args) {
//		AnycookOauthTest test = new AnycookOauthTest();
//		System.out.println(test.requestToken);
//	}
//}
