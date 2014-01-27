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
AnycookAPI.registration = function(mail, username, password,callback){
	var api = '/user';
	var data = {
		username : username,
		mail : mail,
		password : password
	};
	return AnycookAPI._post(api, data, callback);
};
	
$.extend(AnycookAPI.registration, {
	checkMail : function(mail, callback){
		var api = '/user/mail';
		var data = {mail:mail};
		return AnycookAPI._get(api, data, callback);
	},
	checkUsername : function(username, callback){
		var api = '/user/name';
		var data = {username:username};
		return AnycookAPI._get(api, data, callback);
	},
	activate : function(activationKey, callback){
		var api = '/session/activate';
		var data = {activationkey:activationKey};
		return AnycookAPI._post(api, data, callback);
	}
});