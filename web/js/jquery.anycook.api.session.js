/**
 * @author Jan Graßegger
 */

//session([callback])
$.anycook.graph.session = function(callback,error){
	var dfd = $.Deferred();
	var graph = "/session";
	return $.anycook.graph._get(graph, {}, callback, error);
}

//login(username, password, stayloggedin [,callback])
$.anycook.graph.session.login = function(username, password, stayLoggedIn, callback, error){
	var dfd = $.Deferred();
	var graph = "/session";
	var data = {username:username, password:password, stayLoggedIn : stayLoggedIn ? true : false};
	return $.anycook.graph._postJSON(graph, data, callback, error);
};

//logout([callback])
$.anycook.graph.session.logout = function(callback){
	var dfd = $.Deferred();
	var graph = "/session";
	return $.anycook.graph._delete(graph, {}, callback);
}

//settings


$.anycook.graph.session.settings = function(callback){
	var graph = "/session/settings";
	var dfd = $.Deferred();
	return $.anycook.graph._get(graph, {}, callback);
}


$.anycook.graph.session.addMailSettings = function(type){
	var graph = "/session/settings/mail/"+type;		
	return $.anycook.graph._put(graph);
}

$.anycook.graph.session.removeMailSettings = function(type){
	var graph = "/session/settings/mail/"+type;		
	return $.anycook.graph._delete(graph);
}


//changeAccountSettings(data)
$.anycook.graph.session.setAccount = function(type, value,callback){
	var graph = "/session/settings/account/"+type;
	var data = {value:value};		
	return $.anycook.graph._post(graph, data, callback);
}

// getMailProvider(domain, [callback])
$.anycook.graph.session.getMailProvider = function(domain, callback){
	var graph = "/session/mailprovider";
	return $.anycook.graph._get(graph, {domain:domain}, callback);
}

