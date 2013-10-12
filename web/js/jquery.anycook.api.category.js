/**
 * @author Jan Graßegger <jan@anycook.de>
 */
	
//category([category], [callback])
$.anycook.graph.category = function(){
	var category;
	var callback;
	switch(arguments.length){
	case 2:
		var type2 = typeof arguments[1];
		if(type2 == "function")
			callback = arguments[1];
	
	case 1:
		var type1 = typeof arguments[0];
		if(type1 == "string")
			category = arguments[0];
		else if(type1 == "function")
			callback = arguments[0];	
	}
	
	
	
	var dfd = $.Deferred();
	
	var graph = "/category";
	if(userid)
		graph+="/"+category;
	$.when($.anycook.graph._get(graph)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});			
	
	return dfd.promise();
};

//sorted([callback])
$.anycook.graph.category.sorted = function(callback){
	var dfd = $.Deferred();
	var graph = "/category/sorted"
	$.when($.anycook.graph._get(graph)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});
}

//number([callback])
$.anycook.graph.category.number = function(callback){
	var dfd = $.Deferred();
	var graph = "/category/number"
	$.when($.anycook.graph._get(graph)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});
}
