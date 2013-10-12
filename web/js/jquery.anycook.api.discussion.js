/**
 * @author Jan Graßegger <jan@anycook.de>
 */

//discussion(recipename [, callback])
$.anycook.graph.discussion = function(recipename, lastid, callback){
	var graph = "/getdiscussion/"+recipename;
	var data = {lastid:lastid};
	return $.anycook.graph._getJSON(graph, data, callback);
}

//answer(recipename, text, [, parentid] [, callback])
$.anycook.graph.discussion.answer = function(recipename, text){
	var graph = "/discussion/"+recipename;
	var data = {comment:text};
	var callback;
	switch(arguments.length){
	case 4:
		callback = arguments[3];
	case 3:
		var type = typeof arguments[2];
		if(type == "function")
			callback = arguments[2];
		else
			data.pid = Number(arguments[2]);
	}
	
	return $.anycook.graph._post(graph, data, callback);
}

$.anycook.graph.discussion.like = function(recipename, id, callback){
	recipename = encodeURIComponent(recipename);
	var graph = "/discussion/like/"+recipename+"/"+id;
	return $.anycook.graph._put(graph, {}, callback);
}

$.anycook.graph.discussion.unlike = function(recipename, id, callback){
	recipename = encodeURIComponent(recipename);
	var graph = "/discussion/like/"+recipename+"/"+id;
	return $.anycook.graph._delete(graph, {}, callback);
}
