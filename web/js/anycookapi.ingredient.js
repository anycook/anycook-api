/**
 * @author Jan Graßegger <jan@anycook.de>
 */
'use strict';
//ingredient([ingredientName or parent] [callback])
AnycookAPI.ingredient = function(){
	var ingredientName;
	var parent;
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
		if(type1 === 'string'){
			ingredientName = arguments[0];
		}
		else if(type1 === 'function'){
			callback = arguments[0];
		}
		else if(type1 === 'boolean'){
			parent = arguments[0];
		}
	}
	
	var path = '/ingredient';
	if(ingredientName){
		path += '/'+encodeURIComponent(ingredientName);
	}

	var data = {};

	if(parent){
		$.extend(data, {parent : parent});
	}

	return AnycookAPI._get(path, {}, callback);
};

AnycookAPI.ingredient.number = function(callback){
	var path = '/ingredient/number';
	return AnycookAPI._get(path, {}, callback);
};

AnycookAPI.ingredient.extract = function(query, callback){
	var path = '/ingredient/extract';
	return AnycookAPI._get(path,{q:query}, callback);
};
