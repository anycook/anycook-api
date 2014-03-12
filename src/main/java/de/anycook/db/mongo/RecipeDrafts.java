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

package de.anycook.db.mongo;

import com.mongodb.*;
import de.anycook.drafts.DraftRecipe;
import de.anycook.newrecipe.DraftNumberProvider;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class RecipeDrafts extends Mongo implements AutoCloseable {

    private DBCollection coll;

    public RecipeDrafts() {
        super();
        coll = getCollection("recipedrafts");
    }

    public List<DraftRecipe> getAll(int user_id) throws IOException {
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
        List<DraftRecipe> drafts = new LinkedList<>();

        for (DBObject res : out.results()) {
            String id = res.get("_id").toString();

            /*Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("data", res.get("value"));
            drafts.add(data);*/
            drafts.add(new DraftRecipe(res));
        }
        return drafts;
    }

    public int count(int user_id) {
        DBObject query = getQuery(user_id);
        DBCursor results = coll.find(query);
        int draftNum = results.count();
        results.close();
        return draftNum;
    }

    public String newDraft(int user_id) throws SQLException {
        long time = System.currentTimeMillis();
        DBObject obj = new BasicDBObject("user_id", user_id)
                .append("timestamp", time);
        coll.insert(obj);
        ObjectId id = (ObjectId) obj.get("_id");

        DraftNumberProvider.INSTANCE.wakeUpSuspended(user_id);
        return id.toString();
    }

    private void update(DBObject updateObj, int user_id, String draft_id) {
        DBObject query = getQuery(user_id, draft_id);
        DBObject set = new BasicDBObject("$set", updateObj);
        coll.update(query, set);
    }

    public void update(DraftRecipe data, int user_id, String draft_id) {
        DBObject updateObj = new BasicDBObject();
        data.write(updateObj);
        update(updateObj, user_id, draft_id);
    }

    public void update(Map<String, Object> data, int user_id, String draft_id) {
        DBObject updateObj = new BasicDBObject(data);
        update(updateObj, user_id, draft_id);
    }

//	public void update(String key, List<?> valueList, int user_id, String draft_id){
//		DBObject updateObj = new BasicDBObject(key, valueList);
//		update(updateObj, user_id, draft_id);
//	}

//	public void update(String key, String value, int user_id, String draft_id){
//		DBObject updateObj = new BasicDBObject(key, value);
//		update(updateObj, user_id, draft_id);
//	}

    public void update(String key, Object value, int user_id, String draft_id) {
        DBObject updateObj = new BasicDBObject(key, value);
        update(updateObj, user_id, draft_id);
    }

    public void remove(int user_id, String draft_id) throws SQLException {
        DBObject query = getQuery(user_id, draft_id);
        coll.remove(query);
        DraftNumberProvider.INSTANCE.wakeUpSuspended(user_id);
    }

    private static DBObject getQuery(int user_id) {
        return new BasicDBObject("user_id", user_id);
    }

    private static DBObject getQuery(int user_id, String draft_id) {
        return new BasicDBObject("_id", new ObjectId(draft_id)).append("user_id", user_id);
    }

    public DraftRecipe loadDraft(String draft_id, int user_id) {
        DBObject query = getQuery(user_id, draft_id);
        return new DraftRecipe(coll.findOne(query));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        coll = null;
    }


}
