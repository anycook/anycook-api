/**
 * @author Jan Graßegger
 */

	
//discover([callback])
$.anycook.graph.discover = function(callback){
	var graph = "/discover";
	return $.anycook.graph._get(graph, {}, callback);
}

//recommended([callback])
$.anycook.graph.discover.recommended = function(callback){
	var graph = "/discover/recommended";
	return $.anycook.graph._get(graph, {}, callback);	
}

//tasty([callback])
$.anycook.graph.discover.tasty = function(callback){
	var graph = "/discover/tasty";
	return $.anycook.graph._get(graph, {}, callback);	
}

//new([callback])
$.anycook.graph.discover.new = function(callback){
	var graph = "/discover/new";
	return $.anycook.graph._get(graph, {}, callback);	
}

