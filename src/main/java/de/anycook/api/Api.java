/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.api;

import de.anycook.api.listener.ExceptionListener;
import de.anycook.api.providers.SessionFactory;
import de.anycook.conf.Configuration;
import de.anycook.session.Session;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@ApplicationPath("/*")
public class Api extends ResourceConfig{
    public Api(){

        packages("de.anycook.api");

        property("sessionCookieDomain", Configuration.getInstance().getCookieDomain());
        register(MultiPartFeature.class);
        register(EntityFilteringFeature.class);

        if (Configuration.getInstance().isDeveloperMode()) {
            register(ExceptionListener.class);
        }

        // makes Session available with @Context
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(SessionFactory.class).to(Session.class);
            }
        });
    }
}
