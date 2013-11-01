/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2013 Jan Graßegger
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

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@ApplicationPath("/*")
public class Api extends ResourceConfig{
    public Api(){
        packages("de.anycook.api");
    }
}

/*@ApplicationPath("/")
public class Api extends Application{


    @Override
    public Set<Class<?>> getClasses() {
        final HashSet<Class<?>> classes = new HashSet<>();
        classes.add(AutocompleteGraph.class);
        classes.add(CategoryGraph.class);
        classes.add(DiscoverGraph.class);
        classes.add(DiscussionGraph.class);
        classes.add(DraftGraph.class);
        classes.add(IngredientGraph.class);
        classes.add(LifeGraph.class);
        classes.add(MessageGraph.class);
        classes.add(RecipeGraph.class);
        classes.add(SearchGraph.class);
        classes.add(SessionGraph.class);
        classes.add(TagGraph.class);
        classes.add(UploadGraph.class);
        classes.add(UserGraph.class);



        return classes;
    }

}  */
