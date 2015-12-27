package de.anycook.sitemap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBUser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SiteMapGenerator {
	private static Logger logger = LogManager.getLogger(SiteMapGenerator.class);

	public static void generateAllSiteMaps() throws SQLException {
		generateDefaultSiteMap();
		generateRecipeSiteMap();
		generateTagSitemap();
		generateProfileSiteMap();
	}

	private static void writeURL(XMLStreamWriter writer, String url, String priority) throws XMLStreamException{
		writer.writeStartElement("url");
			writer.writeStartElement("loc");
				writer.writeCharacters(url);
			writer.writeEndElement();
			writer.writeStartElement("priority");
				writer.writeCharacters(priority);
			writer.writeEndElement();
		writer.writeEndElement();
	}

	public static void generateDefaultSiteMap(){
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer;
		List<String> sites = new LinkedList<>();
		sites.add("about_us");
		sites.add("feedback");
		sites.add("impressum");
		sites.add("registration");
		sites.add("developer");

        File file = new File("/tmp/sitemap1.xml");

		try {
			writer = factory.createXMLStreamWriter(new FileOutputStream(file));
			writer.writeStartDocument();
			writer.writeStartElement("urlset");
			writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
			writeURL(writer, "http://anycook.de/", "1");

			for(String site : sites){
				try {
					String url = "http://anycook.de/#/"+URLEncoder.encode(site, "UTF-8");
					writeURL(writer, url, "0.2");
				} catch (UnsupportedEncodingException e) {
					logger.error(e);
				}
			}
			writer.writeEndElement();
			writer.writeEndDocument();
            if (Configuration.getInstance().isSiteMapS3Upload()) {
                uploadSiteMap(file);
            }

		} catch (IOException | XMLStreamException e1) {
			logger.error(e1);
		}
    }

	public static void generateRecipeSiteMap() throws SQLException {
		try(DBGetRecipe db = new DBGetRecipe()){
            List<String> allRecipes = new ArrayList<>(db.getAllActiveRecipeNames());
            logger.info("Recipes: "+allRecipes.subList(0, 10).toString());

            final String priority = "0.8";
            File file = new File("/tmp/sitemap2.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;
            try {
                writer = factory.createXMLStreamWriter(new FileOutputStream(file));
                writer.writeStartDocument();
                writer.writeStartElement("urlset");
                writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

                for(String recipe : allRecipes){
                    try {
                        String url = "http://anycook.de/#/recipe/"+URLEncoder.encode(recipe, "UTF-8");
                        writeURL(writer, url, priority);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                    }
                }

                writer.writeEndElement();
                writer.writeEndDocument();
                if (Configuration.getInstance().isSiteMapS3Upload()) {
                    uploadSiteMap(file);
                }

            } catch (IOException | XMLStreamException e1) {
                logger.error(e1);
            }
        }



	}

	public static void generateTagSitemap() throws SQLException {
		try(DBGetRecipe db = new DBGetRecipe()){
            List<String> allTags = db.getAllTags();

            final String priority = "0.4";
            File file = new File("/tmp/sitemap3.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;

            writer = factory.createXMLStreamWriter(new FileOutputStream(file));
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

            for(String tag : allTags){
                try {
                    String url = "http://anycook.de/#/search/tagged/"+URLEncoder.encode(tag, "UTF-8");
                    writeURL(writer, url, priority);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            if (Configuration.getInstance().isSiteMapS3Upload()) {
                uploadSiteMap(file);
            }

		} catch (IOException | XMLStreamException e1) {
			logger.error(e1);
		}
    }

	public static void generateProfileSiteMap() throws SQLException {
		try(DBUser db = new DBUser()){
            List<String> users = db.getActiveUsers();

            final String priority = "0.5";
            File file =  new File("/tmp/sitemap4.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;
            writer = factory.createXMLStreamWriter(new FileOutputStream(file));
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

            for(String user : users){
                try {
                    String url = "http://anycook.de/#/profile/"+URLEncoder.encode(user,
                            StandardCharsets.UTF_8.toString());
                    writeURL(writer, url, priority);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }

            writer.writeEndElement();
            writer.writeEndDocument();
            if (Configuration.getInstance().isSiteMapS3Upload()) {
                uploadSiteMap(file);
            }

		} catch (IOException | XMLStreamException e1) {
			logger.error(e1);
		}
    }

    private static void uploadSiteMap(File siteMap) throws IOException {
        AWSCredentials awsCredentials = new BasicAWSCredentials(Configuration.getInstance().getSiteMapS3AccessKey(),
            Configuration.getInstance().getSiteMapS3AccessSecret());
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);
        String bucketName = Configuration.getInstance().getSiteMapS3Bucket();

        byte[] md5 = DigestUtils.md5(new FileInputStream(siteMap));
        InputStream is = new FileInputStream(siteMap);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(siteMap.length());

        //setting max-age to 15 days
        //metadata.setCacheControl("max-age=1296000");
        metadata.setContentMD5(new String(Base64.encodeBase64(md5)));
        PutObjectRequest request = new PutObjectRequest(bucketName, "sitemaps/" + siteMap.getName(), is, metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = s3Client.putObject(request);
        logger.debug("Etag:" + result.getETag() + "-->" + result);
    }


}
