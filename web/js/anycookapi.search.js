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
//search(querymap, [callback])
AnycookAPI.search = function(data, callback, error){
	var path = '/search';
	return AnycookAPI._postJSON(path, data, callback, error);
};

//validate(term [,callback])
AnycookAPI.search.validate = function(term, callback){
	var path = '/search/validate';
	var data = {q:term};
	return AnycookAPI._get(path, data, callback);
};
