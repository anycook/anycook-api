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

package de.anycook.db.drafts.mongo;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

public class Mongo {
    private MongoClient client = null;
    protected final Logger logger;

    protected Mongo() {
        logger = Logger.getLogger(getClass());
        try {
            client = new MongoClient();
        } catch (UnknownHostException e) {
            logger.error(e);
        }

    }

    protected DBCollection getCollection(String collectionName) {
        return client.getDB("anycook").getCollection(collectionName);
    }

    public void close() {
        client.close();
    }

}
