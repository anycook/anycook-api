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
 
//user([userid], [callback])
$.anycook.api.user = function(){
	var userid;
	var callback;
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string" || type1 == "number")
			userid = arguments[0];
		else if(type1 == "function")
			callback = arguments[0];	
	}	
	
	var path = "/user";
	if(userid !== undefined)
		path+=("/"+userid);
	return $.anycook.api._get(path, {}, callback);
	
};

$.anycook.api.user.number = function(callback){
	var path = "/user/number"
	return $.anycook.api._get(path, {}, callback);
}

//discussionNum(userid [, callback])
$.anycook.api.user.discussionNum = function(userid, callback){
	var path = "/user/"+userid+"/discussionnum";
	return $.anycook.api._get(path, {}, callback);
}

//schmeckt(userid, [callback])
$.anycook.api.user.schmeckt = function(userid, callback){
	var path  = "/user/"+userid+"/schmeckt";
	return $.anycook.api._get(path,{}, callback);
}

//userImagePath(user [, type])
$.anycook.api.user.image = function(user, type){
	var settings = $.anycook.api._settings();
	if(!type)
		type = "small";
	return settings.baseUrl+"/user/"+encodeURIComponent(user)+"/image?type="+type+"&appid="+settings.appid+"+&"+Math.random();
};

//follow(userid)
$.anycook.api.user.follow = function(userid){
	var path = "/user/"+userid+"/follow";
	$.anycook.api._put(path);
}

//unfollow(userid)
$.anycook.api.user.unfollow = function(userid){
	var path = "/user/"+userid+"/follow";
	$.anycook.api._delete(path);
}

//recommendations([callback])
$.anycook.api.user.recommendations = function(callback){
	var path = "/user/recommendations";
	return $.anycook.api._get(path, callback);
}