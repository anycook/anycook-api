package de.anycook.sitemap;

import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SiteMapGenerator {

    private final static Logger logger = LogManager.getLogger(SiteMapGenerator.class);
    private final static String CHARSET = StandardCharsets.UTF_8.toString();
    private final static File SITEMAP_ROOT = new File(Configuration.getInstance().getSitemapRoot());

    public static void generateAllSiteMaps() throws SQLException {
        generateDefaultSiteMap();
        generateRecipeSiteMap();
        generateTagSitemap();
        generateProfileSiteMap();
    }

    private static void writeURL(XMLStreamWriter writer, String url, String priority)
            throws XMLStreamException {
        writer.writeStartElement("url");
        writer.writeStartElement("loc");
        writer.writeCharacters(url);
        writer.writeEndElement();
        writer.writeStartElement("priority");
        writer.writeCharacters(priority);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private static void generateDefaultSiteMap() {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer;
        List<String> sites = new LinkedList<>();
        sites.add("about_us");
        sites.add("feedback");
        sites.add("impressum");
        sites.add("registration");
        sites.add("developer");

        File file = new File(SITEMAP_ROOT, "sitemap1.xml");

        try {
            writer = factory.createXMLStreamWriter(new FileOutputStream(file));
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
            writeURL(writer, "http://anycook.de/", "1");

            for (String site : sites) {
                try {
                    String url = String.format("http://anycook.de/#/%s",
                                               URLEncoder.encode(site, CHARSET));
                    writeURL(writer, url, "0.2");
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();

        } catch (IOException | XMLStreamException e1) {
            logger.error(e1);
        }
    }

    public static void generateRecipeSiteMap() throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            List<String> allRecipes = new ArrayList<>(db.getAllActiveRecipeNames());
            logger.info("Recipes: " + allRecipes.subList(0, 10).toString());

            final String priority = "0.8";
            File file = new File(SITEMAP_ROOT, "sitemap2.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;
            try {
                writer = factory.createXMLStreamWriter(new FileOutputStream(file));
                writer.writeStartDocument();
                writer.writeStartElement("urlset");
                writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

                for (String recipe : allRecipes) {
                    try {
                        String url = String.format("http://anycook.de/#/recipe/%s",
                                                   URLEncoder.encode(recipe, CHARSET));
                        writeURL(writer, url, priority);
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e);
                    }
                }

                writer.writeEndElement();
                writer.writeEndDocument();

            } catch (IOException | XMLStreamException e1) {
                logger.error(e1);
            }
        }


    }

    public static void generateTagSitemap() throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            List<String> allTags = db.getAllTags();

            final String priority = "0.4";
            File file = new File(SITEMAP_ROOT, "sitemap3.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;

            writer = factory.createXMLStreamWriter(new FileOutputStream(file));
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

            for (String tag : allTags) {
                try {
                    String url = String.format("http://anycook.de/#/search/tagged/%s",
                                               URLEncoder.encode(tag, CHARSET));
                    writeURL(writer, url, priority);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }

            writer.writeEndElement();
            writer.writeEndDocument();

        } catch (IOException | XMLStreamException e1) {
            logger.error(e1);
        }
    }

    public static void generateProfileSiteMap() throws SQLException {
        try (DBUser db = new DBUser()) {
            List<String> users = db.getActiveUsers();

            final String priority = "0.5";
            File file = new File(SITEMAP_ROOT, "sitemap4.xml");
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer;
            writer = factory.createXMLStreamWriter(new FileOutputStream(file));
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");

            for (String user : users) {
                try {
                    String url = String.format("http://anycook.de/#/profile/%s",
                                               URLEncoder.encode(user, CHARSET));
                    writeURL(writer, url, priority);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e);
                }
            }

            writer.writeEndElement();
            writer.writeEndDocument();

        } catch (IOException | XMLStreamException e1) {
            logger.error(e1);
        }
    }
}
