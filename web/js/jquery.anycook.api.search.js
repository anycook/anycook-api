/**
 * @author Jan Graßegger <jan@anycook.de>
 */
//search(querymap, [callback])
$.anycook.graph.search = function(data, callback){	
	var dfd = $.Deferred();
	var graph = "/search";
	$.when($.anycook.graph._get(graph, data)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});
	
	return dfd.promise();
};

//validate(term [,callback])
$.anycook.graph.search.validate = function(term, callback){
	var dfd = $.Deferred();
	var graph = "/search/validate";
	var data = {q:term}
	$.when($.anycook.graph._get(graph, data)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});
	
	return dfd.promise();
}
