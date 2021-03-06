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

import com.mongodb.client.MongoCollection;

import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.drafts.RecipeDraft;
import de.anycook.drafts.RecipeDraftWrapper;
import de.anycook.newrecipe.DraftNumberProvider;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan Graßegger <jan@anycook.de>
 */
public class MongoDBRecipeDraftsStore extends Mongo implements RecipeDraftsStore, AutoCloseable {

    private MongoCollection<Document> coll;

    public MongoDBRecipeDraftsStore() {
        super();
        coll = getCollection("recipedrafts");
    }

    @Override
    public List<RecipeDraft> getDrafts(final int user_id) throws IOException {
        //language=JavaScript 1.6
        String map = "function(){var percentage = 0;\n"
                     + "    for(var key in this) {\n"
                     + "        percentage++;\n"
                     + "    }\n"
                     + "    percentage/=14;\n"
                     + "    var out = { \n"
                     + "        _id : this._id,\n"
                     + "        percentage : percentage,\n"
                     + "        timestamp : this.timestamp\n"
                     + "    };\n"
                     + "    \n"
                     + "    if (this.name) {\n"
                     + "        out.name = this.name;\n"
                     + "    }\n"
                     + "    \n"
                     + "    if (this.description) {\n"
                     + "        out.description = this.description;\n"
                     + "    }\n"
                     + "    \n"
                     + "    if (this.image) {\n"
                     + "        out.image = this.image;\n"
                     + "    }\n"
                     + "    \n"
                     + "    emit(this._id, out);\n"
                     + "}";
        //language=JavaScript 1.6
        String reduce = "function(key, values) {\n"
                        + "    return values.map(function(value) {\n"
                        + "        return value.value;\n"
                        + "    });\n"
                        + "}";
        Document query = getQuery(user_id);

        return coll.mapReduce(map, reduce, RecipeDraftWrapper.class).filter(query)
                .map(RecipeDraftWrapper::getRecipeDraft).into(new ArrayList<>());
    }

    @Override
    public RecipeDraft getDraft(final String draft_id, final int user_id)
            throws DraftNotFoundException {
        final Document query = getQuery(user_id, draft_id);
        final RecipeDraft draft = coll.find(query, RecipeDraft.class).iterator().tryNext();
        if (draft == null) {
            throw new DraftNotFoundException(draft_id, user_id);
        }

        return draft;
    }

    @Override
    public int countDrafts(final int userId) {
        final Document query = getQuery(userId);
        return (int) coll.count(query);
    }

    @Override
    public String newDraft(final int userId) throws SQLException {
        final long time = System.currentTimeMillis();
        final Document obj = new Document("userId", userId)
                .append("timestamp", time);
        coll.insertOne(obj);
        final ObjectId id = obj.getObjectId("_id");

        DraftNumberProvider.INSTANCE.wakeUpSuspended(userId);
        return id.toString();
    }

    @Override
    public void updateDraft(final String id, final RecipeDraft data) {
        update(data, data.getUserId(), id);
    }

    private void update(final RecipeDraft updateObj, final int user_id, final String draft_id) {
        final Document query = getQuery(user_id, draft_id);
        final Document set = new Document("$set", updateObj);
        coll.findOneAndUpdate(query, set);
    }

    @Override
    public void deleteDraft(final String id, final int userId) throws SQLException {
        final Document query = getQuery(userId, id);
        coll.findOneAndDelete(query);
        DraftNumberProvider.INSTANCE.wakeUpSuspended(userId);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        coll = null;
    }

    private static Document getQuery(int userId) {
        return new Document("userId", userId);
    }

    private static Document getQuery(int userId, String draftId) {
        return new Document("_id", new ObjectId(draftId)).append("userId", userId);
    }
}
