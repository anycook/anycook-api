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
//message([lastdate [, callback]])
AnycookAPI.message = function(){
	var lastdate;
	var callback;
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 === 'function'){
			callback = arguments[1];
		}
		/* falls through */
	case 1:
		var type1 = typeof arguments[0];
		if(type1 === 'string' || type1 === 'number'){
			lastdate = Number(arguments[0]);
		}
		else if(type1 === 'function'){
			callback = arguments[0];
		}
	}

	var path = '/message';
	var data = {lastChange:lastdate};
	return AnycookAPI._get(path, data, callback);
};

$.extend(AnycookAPI.message, {
	//session(sessionid [,lastid] [,callback])
	session : function(sessionid){
		var callback;
		var lastid = -1;
		
		switch(arguments.length){
		case 3:
			var type2 = typeof arguments[2];
			if(type2 === 'function'){
				callback = arguments[2];
			}
			/* falls through */
		case 2:
			var type1 = typeof arguments[1];
			if(type1 === 'number' || type1 === 'string'){
				lastid = Number(arguments[1]);
			}
			else if(type1 === 'function'){
				callback = arguments[1];
			}
		}
		
		var path = '/message/'+sessionid;
		var data = {lastId : lastid};
		return AnycookAPI._get(path, data, callback);
	},
	//number([lastnum] [,callback])
	number : function(){
		var lastnum = -1;
		var callback;
		
		switch(arguments.length){
		case 2:
			var type2 = typeof arguments[1];
			if(type2 === 'function'){
				callback = arguments[1];
			}
			/* falls through */
		case 1:
			var type1 = typeof arguments[0];
			if(type1 === 'number' || type1 === 'string'){
				lastnum = Number(arguments[0]);
			}
			else if(type1 === 'function'){
				callback = arguments[1];
			}
		}

		var path = '/message/number';
		var data = {lastNum : lastnum};

		var settings = AnycookAPI._settings();
		$.extend(data, {appid : settings.appid});
		
		return AnycookAPI._get(path, data, callback);
	},
	//writeMessage(sessionid, text, [,callback])
	answer : function(sessionid, text, callback){
		var path = '/message/'+sessionid;
		var data = {text : text};
		AnycookAPI._postJSON(path, data, callback);
	},
	//writeNewMessage(recipients, text [, callback])
	writeNew : function(recipients, text, callback){
		var path = '/message';
		var data = {text:text, recipients:recipients};
		AnycookAPI._postJSON(path, data, callback);
	},
	//readMessage(sessionid, messageid [,callback])
	read : function(sessionid, messageid, callback){
		var path = '/message/'+sessionid+'/'+messageid;
		AnycookAPI._put(path, {}, callback);
	}
});