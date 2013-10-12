/**
 * @author Jan Graßegger <jan@anycook.de>
 */

//life(data, [callback])
$.anycook.graph.life = function(data, callback){
	var graph = "/life";
	var dfd = $.Deferred();
	$.when($.anycook.graph._get(graph,data)).then(function(json){
		dfd.resolve(json);
		if(callback)
			callback(json);
	});		
	return dfd.promise();
}