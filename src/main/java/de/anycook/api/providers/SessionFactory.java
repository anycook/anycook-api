package de.anycook.api.providers;

import de.anycook.session.Session;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class SessionFactory implements Factory<Session> {

    private final HttpServletRequest request;

    @Inject
    public SessionFactory(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Session provide() {
        return Session.init(request);
    }

    @Override
    public void dispose(Session instance) {

    }
}
