package de.anycook;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBHandler;
import de.anycook.db.mysql.DBUser;
import de.anycook.location.GeoCode;
import de.anycook.location.Location;
import de.anycook.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.DefaultSessionManager;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {

    private final Logger logger;

    public Main() {
        logger = LogManager.getLogger();
    }

    private void initDB() throws SQLException {
        DBHandler.init();

        // get user locations
        try (DBUser dbUser = new DBUser()) {
            GeoCode geoCode = new GeoCode();
            for (User user : dbUser.getAllUsers()) {
                if (user.getPlace() == null) {
                    continue;
                }
                try {
                    Location location = geoCode.getLocation(user.getPlace());
                    user.setLocation(location);
                } catch (IOException | GeoCode.LocationNotFoundException | SQLException e) {
                    logger.warn(e, e);
                }
            }
        }
    }

    private void startServer() throws IOException {
        final var resourceConfig = new AnycookResourceConfig();

        final var sessionManager = DefaultSessionManager.instance();
        sessionManager.setSessionCookieName(Configuration.getInstance().getSessionCookieName());

        final var server = GrizzlyHttpServerFactory.createHttpServer(
                URI.create("http://localhost:8080"), resourceConfig);
        server.start();
    }

    public static void main(String[] args) throws SQLException, IOException {
        var main = new Main();
        main.initDB();
        main.startServer();
    }

}
