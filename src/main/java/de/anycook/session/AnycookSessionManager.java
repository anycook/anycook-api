package de.anycook.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.grizzly.http.server.SessionManager;


// TODO Maybe implement own session manager?
public class AnycookSessionManager implements SessionManager {

    private String sessionCookieName = "anycook_session";

    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session getSession(
            Request request, String requestedSessionId
    ) {
        return session;
    }

    @Override
    public Session createSession(Request request) {
        return null;
    }

    @Override
    public String changeSessionId(Request request, Session session) {
        return null;
    }

    @Override
    public void configureSessionCookie(Request request, Cookie cookie) {

    }

    @Override
    public void setSessionCookieName(String name) {
        this.sessionCookieName = name;
    }

    @Override
    public String getSessionCookieName() {
        return sessionCookieName;
    }
}
