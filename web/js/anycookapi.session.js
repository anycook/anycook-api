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
//session([callback])
AnycookAPI.session = function(callback, error){
	var path = '/session';
	return AnycookAPI._get(path, {}, callback, error);
};

$.extend(AnycookAPI.session, {
    //id(callback)
    id : function(callback){
        return AnycookAPI._get('/session/id', {}, callback);
    },
	//login(username, password, stayloggedin [,callback])
	login : function(username, password, stayLoggedIn, callback, error){
		var path = '/session';
		var data = {
			username:username,
			password:password,
			stayLoggedIn : stayLoggedIn ? true : false
		};
		return AnycookAPI._postJSON(path, data, callback, error);
	},
	//logout([callback])
	logout : function(callback){
		var path = '/session';
		return AnycookAPI._delete(path, {}, callback);
	},
	// getMailProvider(domain, [callback])
	getMailProvider : function(domain, callback){
		var path = '/session/mailprovider';
		return AnycookAPI._get(path, {domain:domain}, callback);
	},
	// resetPassword(mail, callback)
	resetPasswordRequest : function(mail, callback, error){
		AnycookAPI._postJSON('/session/resetPassword', mail, callback, error);
	},
	resetPassword : function(id, newPassword, callback, error){
		var data = {
			id : id,
			newPassword : newPassword
		};
		AnycookAPI._putJSON('/session/resetPassword', data, callback, error);
	}
});

