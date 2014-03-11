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

package de.anycook.news.life;

import de.anycook.news.News;
import de.anycook.user.User;

import java.util.Date;


public class Life extends News {
    private final String syntax;
    private final String recipe;
    private final User user;

    //	public Life(int id, int userId, String syntax, String recipe, Date datetime) {
//		this(id, userId, User.getUsername(userId), syntax, recipe, datetime);
//	}
//	
    public Life(int id, User user, String syntax, String recipe, Date datetime) {
        super(id, datetime);
        this.syntax = syntax;
        this.recipe = recipe;
        this.user = user;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getRecipe() {
        return recipe;
    }

    public User getUser() {
        return user;
    }
}
