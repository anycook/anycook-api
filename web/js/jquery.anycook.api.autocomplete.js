/**
 * @author Jan Graßegger <jan@anycook.de>
 */

//autocomplete(term, [excludedCategorie, excludedIngredients, excludedTags, excludedUsers], [callback])
$.anycook.graph.autocomplete = function(term){
	var graph = "/autocomplete"
	var data = {q:term};
	var callback;
	
	switch(arguments.length){
		case 6:
			var type6 = typeof arguments[5];
			if(type6 == "function")
				callback = arguments[5];
				
		case 5:
			data.excludedcategorie = arguments[1];
			
			if(arguments[2])
				data.excludedingredients = arguments[2].toString();
			
			if(arguments[3])
				data.excludedtags = arguments[3].toString();
				
			if(arguments[4])
				data.excludedusers = arguments[4].toString();
			break;
		
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			break;
	}
	return $.anycook.graph._get(graph, data, callback);
}

//user(term [, exclude][, callback])
$.anycook.graph.autocomplete.user = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}
	
	var dfd = $.Deferred();
	var graph = "/autocomplete/user"
	return 	$.anycook.graph._get(graph, data, callback);
}

//ingredient(term [, exclude][, callback])
$.anycook.graph.autocomplete.ingredient = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}
	
	
	var dfd = $.Deferred();
	var graph = "/autocomplete/ingredient"
	return 	$.anycook.graph._get(graph, data, callback);
}

//tag(term [, exclude][, callback])
$.anycook.graph.autocomplete.tag = function(term){
	var exclude;
	var callback;
	var data = {q:term};
	
	switch(arguments.length){
		case 3:
			var type3 = typeof arguments[2];
			if(type3 == "function")
				callback = arguments[2];
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
			else{
				data.exclude = arguments[1].toString();
			}
	}

	var dfd = $.Deferred();
	var graph = "/autocomplete/tag"
	return 	$.anycook.graph._get(graph, data, callback);
}