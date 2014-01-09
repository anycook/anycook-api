/**
 * @license This file is part of anycook. The new internet cookbook
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
 * 
 * @author Jan Graßegger <jan@anycook.de>
 */

//discussion(recipename [, callback])
$.anycook.api.discussion = function(recipename, lastid, callback){
	var path = "/discussion/"+recipename;
	var data = {lastid:lastid};
	return $.anycook.api._get(path, data, callback);
}

//answer(recipename, text, [, parentid] [, callback])
$.anycook.api.discussion.answer = function(recipename, text){
	var path = "/discussion/"+recipename;
	var data = {comment:text};
	var callback;
	switch(arguments.length){
	case 4:
		callback = arguments[3];
	case 3:
		var type = typeof arguments[2];
		if(type == "function")
			callback = arguments[2];
		else
			data.pid = Number(arguments[2]);
	}
	
	return $.anycook.api._post(path, data, callback);
}

$.anycook.api.discussion.like = function(recipename, id, callback){
	recipename = encodeURIComponent(recipename);
	var path = "/discussion/like/"+recipename+"/"+id;
	return $.anycook.api._put(path, {}, callback);
}

$.anycook.api.discussion.unlike = function(recipename, id, callback){
	recipename = encodeURIComponent(recipename);
	var path = "/discussion/like/"+recipename+"/"+id;
	return $.anycook.api._delete(path, {}, callback);
}
