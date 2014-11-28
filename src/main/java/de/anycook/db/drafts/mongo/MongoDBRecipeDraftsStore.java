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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.drafts.RecipeDraft;
import de.anycook.newrecipe.DraftNumberProvider;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages access to mongodb recipedraft collection
 * Document structure:
 * {
 * _id:unique_id,
 * user_id:number,
 * draft_id:number,
 * name:string,
 * <p/>
 * }
 * <p/>
 * index: {user_id:1, draft_id:-1}
 *
 * @author Jan Graßegger <jan@anycook.de>
 */
public class MongoDBRecipeDraftsStore extends Mongo implements RecipeDraftsStore, AutoCloseable {

    private DBCollection coll;

    public MongoDBRecipeDraftsStore() {
        super();
        coll = getCollection("recipedrafts");
    }

    @Override
    public List<RecipeDraft> getDrafts(int user_id) throws IOException {
        String map = "function(){" +
                "var percentage = 0;" +
                "for(var key in this){" +
                "percentage++;" +
                "}" +
                "percentage/=14;" +
                "var out = { percentage : percentage," +
                "timestamp:this.timestamp," +
                "description:this.description," +
                "image:this.image," +
                "name : this.name," +
                "percentage : percentage" +
                "};" +
                "emit(this._id, out);" +
                "}";
        String reduce = "function(key, values){" +
                "values.forEach(function(doc){" +
                "return doc;" +
                "});}";
        DBObject query = getQuery(user_id);

        MapReduceCommand mapReduce =
                new MapReduceCommand(coll, map, reduce, null, MapReduceCommand.OutputType.INLINE, query);

        MapReduceOutput out = coll.mapReduce(mapReduce);
        List<RecipeDraft> drafts = new LinkedList<>();

        for (DBObject res : out.results()) {
            String id = res.get("_id").toString();

            /*Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("data", res.get("value"));
            drafts.add(data);*/
            drafts.add(new RecipeDraft(res));
        }
        return drafts;
    }

    @Override
    public RecipeDraft getDraft(String draft_id, int user_id) throws DraftNotFoundException {
        DBObject query = getQuery(user_id, draft_id);
        DBObject draftObject = coll.findOne(query);
        if (draftObject == null) {
            throw new DraftNotFoundException(draft_id, user_id);
        }
        return new RecipeDraft(draftObject);
    }

    @Override
    public int countDrafts(int userId) {
        DBObject query = getQuery(userId);
        try (DBCursor results = coll.find(query)) {
            return results.count();
        }
    }

    @Override
    public String newDraft(int user_id) throws SQLException {
        long time = System.currentTimeMillis();
        DBObject obj = new BasicDBObject("user_id", user_id)
                .append("timestamp", time);
        coll.insert(obj);
        ObjectId id = (ObjectId) obj.get("_id");

        DraftNumberProvider.INSTANCE.wakeUpSuspended(user_id);
        return id.toString();
    }

    @Override
    public void updateDraft(String id, RecipeDraft data) {
        DBObject updateObj = new BasicDBObject();
        data.write(updateObj);
        update(updateObj, data.getUserId(), id);
    }

    private void update(DBObject updateObj, int user_id, String draft_id) {
        DBObject query = getQuery(user_id, draft_id);
        DBObject set = new BasicDBObject("$set", updateObj);
        coll.update(query, set);
    }

    @Override
    public void deleteDraft(String id, int userId) throws SQLException {
        DBObject query = getQuery(userId, id);
        coll.remove(query);
        DraftNumberProvider.INSTANCE.wakeUpSuspended(userId);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        coll = null;
    }

    private static DBObject getQuery(int user_id) {
        return new BasicDBObject("user_id", user_id);
    }

    private static DBObject getQuery(int user_id, String draft_id) {
        return new BasicDBObject("_id", new ObjectId(draft_id)).append("user_id", user_id);
    }
}
