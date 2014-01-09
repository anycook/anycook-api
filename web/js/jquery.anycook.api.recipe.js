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


if(!$.anycook.api.recipe)
	$.anycook.api.recipe = {};
		
//recipe([recipename,[versionnum]],[data] [callback])
$.anycook.api.recipe = function(){
	
	var recipe;
	var callback;
	var data = {};
	var version;
	switch(arguments.length){
	case 4:
		var type4 = typeof arguments[3];
		if(type4 == "function")
			callback = arguments[3];
	case 3:
		var type3 = typeof arguments[2];
		if(type3 == "function")
			callback = arguments[2];
		else if(type3 == "object")
			data = arguments[2];
		
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
		else if(type2 == "string" || type2 == "number")
			version = arguments[1];
		else if(type2 == "object")
			data = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string")
			recipe = arguments[0];
		else if(type1 == "function")
			callback = arguments[0];
		else if(type1 == "object")
			data = arguments[0];	
	}
	
	
	
	
	var path = "/recipe";
	if(recipe)
		path+="/"+recipe;
	if(version)
		path+="/"+version;
	return $.anycook.api._get(path, data, callback);
};
	
//ofTheDay([callback])
$.anycook.api.recipe.ofTheDay = function(callback){
	var path = "/recipe/oftheday"
	return $.anycook.api._get(path, {}, callback);
}

//ingredients(recipename, [versionid], [callback])
$.anycook.api.recipe.ingredients = function(recipe){
	var versionid;
	var callback;
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else if(type2 == "number" || type2 == "string")
				versionid = arguments[1];
	}
	
	var path = "/recipe/"+recipe;
	
	if(versionid)
		path += "/"+versionid;
	path+="/ingredients"
	
	return $.anycook.api._get(path, {}, callback);
	
}

//tags(recipename, [callback])
$.anycook.api.recipe.tags = function(recipe, callback){	
	var path = "/recipe/"+recipe+"/tags";	
	return $.anycook.api._get(path, {}, callback);
	
}

//steps(recipename, [versionid], [callback])
$.anycook.api.recipe.steps = function(recipe){
	var versionid;
	var callback;
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else if(type2 == "number" || type2 == "string")
				versionid = arguments[1];
	}
	
	var path = "/recipe/"+recipe;
	
	if(versionid)
		path += "/"+versionid;
	path+="/steps"
	
	return $.anycook.api._get(path, {}, callback);
	
}

//number([callback])
$.anycook.api.recipe.number = function(callback){
	var path = "/recipe/number"
	return $.anycook.api._get(path, {}, callback);
}

//save(recipename, dataJSON [, callback])
$.anycook.api.recipe.save = function(data, callback){
	var path = "/recipe/";
	return $.anycook.api._postJSON(path, data, callback);
}

$.anycook.api.recipe.image = function(recipe, type){
	var settings = $.anycook.api._settings();
	if(!type)
		type = "small";
	return settings.baseUrl+"/recipe/"+encodeURIComponent(recipe)+"/image?type="+type+"&appid="+settings.appid;
};

//schmeckt(recipename, [callback])
$.anycook.api.recipe.schmeckt = function(recipename, callback){
	var path = "/recipe/"+recipename+"/schmeckt";
	return $.anycook.api._get(path, callback);
}

//makeSchmeckt(recipename, [callback])
$.anycook.api.recipe.makeSchmeckt = function(recipename, callback){
	var path = "/recipe/"+recipename+"/schmeckt";
	$.anycook.api._put(path,{},callback);
}

//unmakeSchmeckt(recipename,[callback])
$.anycook.api.recipe.unmakeSchmeckt = function(recipename,callback){
	var path = "/recipe/"+recipename+"/schmeckt";
	$.anycook.api._delete(path,{},callback);
}