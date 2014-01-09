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

//session([callback])
$.anycook.api.session = function(callback,error){
	var dfd = $.Deferred();
	var path = "/session";
	return $.anycook.api._get(path, {}, callback, error);
}

//login(username, password, stayloggedin [,callback])
$.anycook.api.session.login = function(username, password, stayLoggedIn, callback, error){
	var dfd = $.Deferred();
	var path = "/session";
	var data = {username:username, password:password, stayLoggedIn : stayLoggedIn ? true : false};
	return $.anycook.api._postJSON(path, data, callback, error);
};

//logout([callback])
$.anycook.api.session.logout = function(callback){
	var dfd = $.Deferred();
	var path = "/session";
	return $.anycook.api._delete(path, {}, callback);
}

//settings
$.anycook.api.session.settings = function(callback){
	var path = "/session/settings";
	var dfd = $.Deferred();
	return $.anycook.api._get(path, {}, callback);
}


$.anycook.api.session.addMailSettings = function(type){
	var path = "/session/settings/mail/"+type;		
	return $.anycook.api._put(path);
}

$.anycook.api.session.removeMailSettings = function(type){
	var path = "/session/settings/mail/"+type;		
	return $.anycook.api._delete(path);
}


//changeAccountSettings(data)
$.anycook.api.session.setAccount = function(type, value,callback){
	var path = "/session/settings/account/"+type;
	var data = {value:value};		
	return $.anycook.api._post(path, data, callback);
}

// getMailProvider(domain, [callback])
$.anycook.api.session.getMailProvider = function(domain, callback){
	var path = "/session/mailprovider";
	return $.anycook.api._get(path, {domain:domain}, callback);
}

