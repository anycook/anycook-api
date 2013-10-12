/**
 * @author Jan Graßegger <jan@anycook.de>
 */

if(!$.anycook.graph.tag) $.anycook.graph.tag ={}

$.anycook.graph.tag.number = function(callback){
	var graph = "/tag/number"
	return $.anycook.graph._get(graph, {}, callback);
}

$.anycook.graph.tag.suggest = function(recipename, tags, userid, callback){
	var graph = "/recipe/"+recipename;
	var data = {tags:JSON.stringify(tags), userid:userid};
	$.anycook.graph._postMessage(graph, data);
}

//popularTags([recipe], [callback])
$.anycook.graph.tag.popular = function(){
	var callback;
	var data ={};
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string")
			data.recipe = arguments[0];
		else if(type1 == "function")
			callback = arguments[1];
	}
	var graph = "/tag/popular";
	var dfd = $.Deferred();
	$.when($.anycook.graph._get(graph,data)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});		
	return dfd.promise();
}