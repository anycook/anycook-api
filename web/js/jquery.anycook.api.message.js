/**
 * @author Jan Graßegger <jan@anycook.de>
 */

//message([lastdate [, callback]])
$.anycook.graph.message = function(){
	var lastdate;
	var callback;
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string" || type1 == "number")
			lastdate = Number(arguments[0]);
		else if(type1 == "function")
			callback = arguments[0];	
	}

	var graph = "/message";
	var data = {lastChange:lastdate};
	return $.anycook.graph._get(graph, data, callback);
}

//session(sessionid [,lastid] [,callback])
$.anycook.graph.message.session = function(sessionid){
	var callback;
	var lastid = -1;
	
	switch(arguments.length){
	case 3:
		var type2 = typeof arguments[2];
		if(type2 == "function")
			callback = arguments[2];
	case 2:
		var type1 = typeof arguments[1];
		if(type1 == "number" || type1 == "string")
			lastid = Number(arguments[1]);
		else if(type1 == "function")
			callback = arguments[1];
	}
	
	var graph = "/message/"+sessionid;
	var data = {lastid : lastid};
	return $.anycook.graph._get(graph, data, callback);
}

//number([lastnum] [,callback])
$.anycook.graph.message.number = function(){
	var lastnum = -1;
	var callback = function(){};
	
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "number" || type1 == "string")
			lastnum = Number(arguments[0]);
		else if(type1 == "function")
			callback = arguments[1];
	}

	var graph = "/message/number";
	var data = {lastnum : lastnum};

	var settings = $.anycook.graph._settings();
	//data[settings.callbackName] = "?";		
	$.extend(data, {appid : settings.appid});
	
	return $.anycook.graph._get(graph, data, callback);
};

//writeMessage(sessionid, text, [,callback])
$.anycook.graph.message.answer = function(sessionid, text, callback){
	var graph = "/message/"+sessionid;
	var data = {message:text};
	$.anycook.graph._put(graph, data);
}

//writeNewMessage(recipients, text [, callback])
$.anycook.graph.message.writeNew = function(recipients, text, callback){
	var graph = "/message";
	var data = {message:text, recipients:JSON.stringify(recipients)};
	$.anycook.graph._put(graph, data);
	// $.anycook.graph._put(graph);
}

//readMessage(sessionid, messageid [,callback])
$.anycook.graph.message.read = function(sessionid, messageid, callback){
	var graph = "/message/"+sessionid+"/"+messageid;
	$.anycook.graph._put(graph);
}