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
'use strict';
//autocomplete(term, [excludedCategorie, excludedIngredients, excludedTags, excludedUsers], [callback])
AnycookAPI.autocomplete = function(term){
	var path = '/autocomplete';
	var data = {query : term};
	var callback;
	
	switch(arguments.length){
		case 6:
			var type6 = typeof arguments[5];
			if(type6 === 'function'){
				callback = arguments[5];
			}
			/* falls through */
		case 5:
			data.excludedCategory = arguments[1];
			
			if(arguments[2]){
				data.excludedIngredients = arguments[2].toString();
			}
			
			if(arguments[3]){
				data.excludedTags = arguments[3].toString();
			}
				
			if(arguments[4]){
				data.excludedUsers = arguments[4].toString();
			}
			break;
		
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			}
			break;
	}
	return AnycookAPI._get(path, data, callback);
};

//user(term [, exclude][, callback])
AnycookAPI.autocomplete.user = function(term){
	var callback;
	var data = {query : term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 === 'function'){
				callback = arguments[2];
			}
			/* falls through */
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			} else{
				data.exclude = arguments[1].toString();
			}
	}
	
	var path = '/autocomplete/user';
	return AnycookAPI._get(path, data, callback);
};

//ingredient(term [, exclude][, callback])
AnycookAPI.autocomplete.ingredient = function(term){
	var callback;
	var data = {query : term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 === 'function'){
				callback = arguments[2];
			}
			/* falls through */
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			} else{
				data.exclude = arguments[1].toString();
			}
	}

	var path = '/autocomplete/ingredient';
	return AnycookAPI._get(path, data, callback);
};

//tag(term [, exclude][, callback])
AnycookAPI.autocomplete.tag = function(term){
	var callback;
	var data = {query : term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 === 'function') {
				callback = arguments[2];
			}
			/* falls through */
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			}
			else{
				data.exclude = arguments[1].toString();
			}
	}

	var path = '/autocomplete/tag';
	return AnycookAPI._get(path, data, callback);
};