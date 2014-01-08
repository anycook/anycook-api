package de.anycook.api.util;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class MediaType extends javax.ws.rs.core.MediaType {
    public final static String APPLICATION_JSON = javax.ws.rs.core.MediaType.APPLICATION_JSON+";charset=utf-8";
    public final static MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");

    public final static String TEXT_PLAIN = javax.ws.rs.core.MediaType.TEXT_PLAIN+";charset=utf-8";
    public final static MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");

    public MediaType(String type, String subtype){
        super(type, subtype, "utf-8");
    }
}
