/**
 * @author Jan Graßegger
 */


if(!$.anycook.graph.recipe)
	$.anycook.graph.recipe = {};
		
//recipe([recipename,[versionnum]],[data] [callback])
$.anycook.graph.recipe = function(){
	
	var recipe;
	var callback;
	var data = {};
	var version;
	switch(arguments.length){
	case 4:
		var type4 = typeof arguments[3];
		if(type4 == "function")
			callback = arguments[3];
	case 3:
		var type3 = typeof arguments[2];
		if(type3 == "function")
			callback = arguments[2];
		else if(type3 == "object")
			data = arguments[2];
		
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
		else if(type2 == "string" || type2 == "number")
			version = arguments[1];
		else if(type2 == "object")
			data = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string")
			recipe = arguments[0];
		else if(type1 == "function")
			callback = arguments[0];
		else if(type1 == "object")
			data = arguments[0];	
	}
	
	
	
	
	var graph = "/recipe";
	if(recipe)
		graph+="/"+recipe;
	if(version)
		graph+="/"+version;
	return $.anycook.graph._get(graph, data, callback);
};
	
//ofTheDay([callback])
$.anycook.graph.recipe.ofTheDay = function(callback){
	var graph = "/recipe/oftheday"
	return $.anycook.graph._get(graph, {}, callback);
}

//ingredients(recipename, [versionid], [callback])
$.anycook.graph.recipe.ingredients = function(recipe){
	var versionid;
	var callback;
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else if(type2 == "number" || type2 == "string")
				versionid = arguments[1];
	}
	
	var graph = "/recipe/"+recipe;
	
	if(versionid)
		graph += "/"+versionid;
	graph+="/ingredients"
	
	return $.anycook.graph._get(graph, {}, callback);
	
}

//tags(recipename, [callback])
$.anycook.graph.recipe.tags = function(recipe, callback){	
	var graph = "/recipe/"+recipe+"/tags";	
	return $.anycook.graph._get(graph, {}, callback);
	
}

//steps(recipename, [versionid], [callback])
$.anycook.graph.recipe.steps = function(recipe){
	var versionid;
	var callback;
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else if(type2 == "number" || type2 == "string")
				versionid = arguments[1];
	}
	
	var graph = "/recipe/"+recipe;
	
	if(versionid)
		graph += "/"+versionid;
	graph+="/steps"
	
	return $.anycook.graph._get(graph, {}, callback);
	
}

//number([callback])
$.anycook.graph.recipe.number = function(callback){
	var graph = "/recipe/number"
	return $.anycook.graph._get(graph, {}, callback);
}

//save(recipename, dataJSON [, callback])
$.anycook.graph.recipe.save = function(data, callback){
	var graph = "/recipe/";
	// var data = {};
	// data.ingredients = JSON.stringify(data.ingredient);
	// data.tags = JSON.stringify(tags);
	return $.anycook.graph._postJSON(graph, data, callback);
}

$.anycook.graph.recipe.image = function(recipe, type){
	var settings = $.anycook.graph._settings();
	if(!type)
		type = "small";
	return settings.baseUrl+"/recipe/"+encodeURIComponent(recipe)+"/image?type="+type+"&appid="+settings.appid;
};

//schmeckt(recipename, [callback])
$.anycook.graph.recipe.schmeckt = function(recipename, callback){
	var graph = "/recipe/"+recipename+"/schmeckt";
	var dfd = $.Deferred();
	$.when($.anycook.graph._get(graph)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});		
	return dfd.promise();
}

//makeSchmeckt(recipename, [callback])
$.anycook.graph.recipe.makeSchmeckt = function(recipename, callback){
	var graph = "/recipe/"+recipename+"/schmeckt";
	$.anycook.graph._put(graph,{},callback);
}

//unmakeSchmeckt(recipename,[callback])
$.anycook.graph.recipe.unmakeSchmeckt = function(recipename,callback){
	var graph = "/recipe/"+recipename+"/schmeckt";
	$.anycook.graph._delete(graph,{},callback);
}