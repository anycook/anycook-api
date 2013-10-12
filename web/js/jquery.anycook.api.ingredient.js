/**
 * @author Jan Graßegger <jan@anycook.de>
 */

if(!$.anycook.graph.ingredient) 
	$.anycook.graph.ingredient = {}

$.anycook.graph.ingredient.number = function(callback){
	var graph = "/ingredient/number";
	return $.anycook.graph._get(graph, {}, callback);
}

$.anycook.graph.ingredient.extract = function(query, callback){
	var graph = "/ingredient/extract";
	return $.anycook.graph._getJSON(graph,{q:query}, callback);
}
