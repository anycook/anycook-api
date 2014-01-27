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
AnycookAPI.discover = {
	//recommended([callback])
	recommended : function(callback){
		var path = '/discover/recommended';
		return AnycookAPI._get(path, {}, callback);
	},

	//tasty([callback])
	tasty : function(callback){
		var path = '/discover/tasty';
		return AnycookAPI._get(path, {}, callback);
	},

	//new([callback])
	new : function(callback){
		var path = '/discover/new';
		return AnycookAPI._get(path, {}, callback);
	}
};



