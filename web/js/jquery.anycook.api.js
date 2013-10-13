/**
 * 
 * @author Jan Graßegger <jan@anycook.de>
 */

(function( $ ){
	
	if(!$.anycook)
		$.anycook = {};
	
	if(!$.anycook.graph)
		$.anycook.graph = {};
	
	$.anycook.graph._settings = function(settings){
		if(settings)
			$(document).data("anycook.graph", settings);			
		else
			return $(document).data("anycook.graph");
	}
	
	$.anycook.graph._getJSON  = function(graph, data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		if(!callback) callback = function(){};
		
		var settings = $.anycook.graph._settings();
		//data[settings.callbackName] = "?";		
		$.extend(data, {appid : settings.appid});
		
		return $.ajax({
		  url: settings.baseUrl+graph+"?callback=?",
		  async:true,
		  dataType: 'json',
		  data: data,
		  success: callback
		});
		// return $.getJSON(settings.baseurl+graph+"?callback=?", data);
		
	}
	
	$.anycook.graph._get = function(graph, data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		var callback = callback || function(){};
		
		var settings = $.anycook.graph._settings();
		//data[settings.callbackName] = "?";		
		$.extend(data, {appid : settings.appid});
		return $.ajax({
		    url: settings.baseUrl+graph,
		    type: 'GET',
		    dataType:"json",
		    data:data,
		    xhrFields:{
                withCredentials: true
           },
		    success: callback,
		    error: function(xhr) {
              console.error(xhr.getAllResponseHeaders());
           	}
		});
	}
	
	$.anycook.graph._post = function(graph, data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		var callback = callback || function(){};
		var settings = $.anycook.graph._settings();
		$.extend(data, {appid : settings.appid});


		return $.ajax({
		    url: settings.baseUrl+graph,
		    type: 'POST',
		    data:data,
		    dataType:"json",
		    contentType: 'application/x-www-form-urlencoded',
		    xhrFields:{
                withCredentials: true
           },
		    success: callback,
		    error: function(xhr) {
              console.error(xhr.getAllResponseHeaders());
          	}
		});
	}

	$.anycook.graph._postJSON = function(graph, data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		var callback = callback || function(){};
		var settings = $.anycook.graph._settings();
		// $.extend(data, {appid : settings.appid});


		return $.ajax({
		    url: settings.baseUrl+graph,
		    type: 'POST',
		    data:JSON.stringify(data),
		    dataType:"json",
		    contentType: "application/json; charset=utf-8",
		    xhrFields:{
                withCredentials: true
           },
		    success: callback,
		    error: function(xhr) {
              console.error(xhr.getAllResponseHeaders());
          	}
		});
	}
	
	$.anycook.graph._put = function(graph,data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		var callback = callback || function(){};
		
		var settings = $.anycook.graph._settings();
		var url = settings.baseUrl+graph;
		$.extend(data, {appid : settings.appid});
		
		return $.ajax({
		    url: url,
		    type: 'PUT',
		    data:data,
		    contentType: 'application/x-www-form-urlencoded',
		    xhrFields:{
                withCredentials: true
           },
		    success: callback,
	     	error: function(xhr) {
              console.error(xhr.getAllResponseHeaders());
           }
		});
	}
	
	$.anycook.graph._delete = function(graph,data, callback){
		if(!graph) graph = "";
		if(!data) data = {};
		var callback = callback || function(){};
		
		var settings = $.anycook.graph._settings();
		//data[settings.callbackName] = "?";		
		$.extend(data, {appid : settings.appid});
		return $.ajax({
		    url: settings.baseUrl+graph,
		    type: 'DELETE',
		    data:data,
		    xhrFields:{
                withCredentials: true
           	},
		    success: callback
		});
	}
	
	$.anycook.graph.init = function(options){
		var dfd = $.Deferred();
		var settings = {
			appid: -1,
			baseUrl: "http://api.anycook.de",
			callbackName: "callback",
			// frameId:"anycook-graph-frame"
			scripts: ["autocomplete","category","discover","discussion", "ingredient", "life","message", "recipe", "search", "session", "tag", "user"]
		};
		
		if(options)
			$.extend(settings, options);
		
		$.anycook.graph._settings(settings);
		
		var numScripts = settings.scripts.length;
		var numLoaded = 0;
		for(var i in settings.scripts){
			var script = settings.scripts[i];
			$.getScript(settings.baseUrl+'/js/jquery.anycook.api.'+script+'.js',function(){
				numLoaded++;
				if(numLoaded == numScripts) dfd.resolve();
			});
			
		}
			
		return dfd.promise();
	};
	
})( jQuery );