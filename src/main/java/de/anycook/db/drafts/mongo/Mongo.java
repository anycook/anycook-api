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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

public class Mongo {

    private MongoClient client = null;
    protected final Logger logger;

    protected Mongo() {
        logger = LogManager.getLogger(getClass());
        client = new MongoClient();
    }

    protected MongoCollection<Document> getCollection(String collectionName) {
        return client.getDatabase("anycook").getCollection(collectionName);
    }

    public void close() {
        client.close();
    }

}
