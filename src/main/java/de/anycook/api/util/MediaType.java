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
