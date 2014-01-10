/**
 * @author Jan Graßegger <jan@anycook.de>
 */

//ingredient([ingredientName or parent] [callback])
$.anycook.api.ingredient = function(){
	var ingredientName;
	var parent;
	var callback;
	switch(arguments.length){

	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string")
			ingredientName = arguments[0];
		else if(type1 == "function")
			callback = arguments[0];
		else if(type1 == "boolean")
			parent = arguments[0];	
	}
	
	

	var path = "/ingredient";
	if(ingredientName)
		path+="/"+encodeURIComponent(ingredientName);

	var data = {};

	if(parent)
		$.extend(data, {parent : parent});

	return $.anycook.api._get(path, {}, callback);
}

$.anycook.api.ingredient.number = function(callback){
	var path = "/ingredient/number";
	return $.anycook.api._get(path, {}, callback);
}

$.anycook.api.ingredient.extract = function(query, callback){
	var path = "/ingredient/extract";
	return $.anycook.api._get(path,{q:query}, callback);
}
