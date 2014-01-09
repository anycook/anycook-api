/**
 * @author Jan Graßegger <jan@anycook.de>
 */

$.anycook.api.ingredient = function(callback){
	var path = "/ingredient";
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
