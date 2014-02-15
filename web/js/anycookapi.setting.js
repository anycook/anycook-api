/**
 * @license This file is part of anycook. The new internet cookbook
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
 * 
 * @author Jan Graßegger <jan@anycook.de>
 */
'use strict';
 
AnycookAPI.setting = {
	//confirmMail(code, callback)
	confirmMail : function(code, callback, error){
		var path = '/setting/email';
		return AnycookAPI._postJSON(path, code, callback, error);
	},
	//setMail(newMail [, callback])
	setMail : function(newMail, callback){
		var path = '/setting/email';
		return AnycookAPI._putJSON(path, newMail, callback);
	},
	setName : function(newName, callback){
		var path = '/setting/name';
		return AnycookAPI._putJSON(path, newName, callback);
	},
	//setPlace(newPlace [, callback])
	setPlace : function(newPlace, callback){
		var path = '/setting/place';
		return AnycookAPI._putJSON(path, newPlace, callback);
	},
	//setText(newText [, callback])
	setText : function(newText, callback){
		var path = '/setting/text';
		return AnycookAPI._putJSON(path, newText, callback);
	},
	//notification([callback])
	notification : function(callback){
		var path = '/setting/notification';
		return AnycookAPI._get(path, {}, callback);
	},
	saveNotifications : function(settings, callback, error){
		var path = '/setting/notification';
		return AnycookAPI._putJSON(path, settings, callback, error);
	}
};