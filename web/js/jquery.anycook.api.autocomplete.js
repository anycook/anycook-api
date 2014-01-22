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

//autocomplete(term, [excludedCategorie, excludedIngredients, excludedTags, excludedUsers], [callback])
$.anycook.api.autocomplete = function(term){
	var path = "/autocomplete"
	var data = {q:term};
	var callback;
	
	switch(arguments.length){
		case 6:
			var type6 = typeof arguments[5];
			if(type6 == "function")
				callback = arguments[5];
				
		case 5:
			data.excludedCategory = arguments[1];
			
			if(arguments[2])
				data.excludedIngredients = arguments[2].toString();
			
			if(arguments[3])
				data.excludedTags = arguments[3].toString();
				
			if(arguments[4])
				data.excludedUsers = arguments[4].toString();
			break;
		
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			break;
	}
	return $.anycook.api._get(path, data, callback);
}

//user(term [, exclude][, callback])
$.anycook.api.autocomplete.user = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}
	
	var dfd = $.Deferred();
	var path = "/autocomplete/user"
	return 	$.anycook.api._get(path, data, callback);
}

//ingredient(term [, exclude][, callback])
$.anycook.api.autocomplete.ingredient = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}
	
	
	var dfd = $.Deferred();
	var path = "/autocomplete/ingredient"
	return 	$.anycook.api._get(path, data, callback);
}

//tag(term [, exclude][, callback])
$.anycook.api.autocomplete.tag = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}

	var dfd = $.Deferred();
	var path = "/autocomplete/tag"
	return 	$.anycook.api._get(path, data, callback);
}