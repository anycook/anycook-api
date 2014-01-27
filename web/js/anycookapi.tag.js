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
AnycookAPI.tag = {
	number : function(callback){
		var path  = '/tag/number';
		return AnycookAPI._get(path, {}, callback);
	},
	suggest : function(recipename, tags, callback){
		var path  = '/recipe/'+recipename+'/tags';
		return AnycookAPI._postJSON(path,  tags, callback);
	},
	//popular([recipe], [callback])
	popular : function(){
		var callback;
		var data = {};
		switch(arguments.length){
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			}
			/* falls through */
		case 1:
			var type1 = typeof arguments[0];
			if(type1 === 'string'){
				data.recipe = arguments[0];
			}
			else if(type1 === 'function'){
				callback = arguments[1];
			}
		}
		var path  = '/tag/popular';
		return AnycookAPI._get(path, data, callback);
	}
};
