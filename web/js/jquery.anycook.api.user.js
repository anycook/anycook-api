/**
 * @author Jan Graßegger <jan@anycook.de>
 */
//user([userid], [callback])
	$.anycook.graph.user = function(){
		var userid;
		var callback;
		switch(arguments.length){
		case 2:
			var type2 = typeof arguments[1];
			if(type2 == "function")
				callback = arguments[1];
		
		case 1:
			var type1 = typeof arguments[0];
			if(type1 == "string" || type1 == "number")
				userid = arguments[0];
			else if(type1 == "function")
				callback = arguments[0];	
		}	
		
		var graph = "/user";
		if(userid !== undefined)
			graph+=("/"+userid);
		return $.anycook.graph._get(graph, {}, callback);
		
	};
	
	$.anycook.graph.user.number = function(callback){
		var graph = "/user/number"
		return $.anycook.graph._get(graph, {}, callback);
	}
	
	//discussionNum(userid [, callback])
	$.anycook.graph.user.discussionNum = function(userid, callback){
		var graph = "/user/"+userid+"/discussionnum";
		return $.anycook.graph._get(graph, {}, callback);
	}
	
	//schmeckt(userid, [callback])
	$.anycook.graph.user.schmeckt = function(userid, callback){
		var graph  = "/user/"+userid+"/schmeckt";
		return $.anycook.graph._get(graph,{}, callback);
	}
	
	//userImagePath(user [, type])
	$.anycook.graph.user.image = function(user, type){
		var settings = $.anycook.graph._settings();
		if(!type)
			type = "small";
		return settings.baseUrl+"/user/"+encodeURIComponent(user)+"/image?type="+type+"&appid="+settings.appid+"+&"+Math.random();
	};
	
	//follow(userid)
	$.anycook.graph.user.follow = function(userid){
		var graph = "/user/"+userid+"/follow";
		$.anycook.graph._put(graph);
	}
	
	//unfollow(userid)
	$.anycook.graph.user.unfollow = function(userid){
		var graph = "/user/"+userid+"/follow";
		$.anycook.graph._delete(graph);
	}
	
	//recommendations([callback])
	$.anycook.graph.user.recommendations = function(callback){
		var graph = "/user/recommendations";
		var dfd = $.Deferred();
		$.when($.anycook.graph._get(graph)).then(function(json){
			dfd.resolve(json);
			if(callback)
				callback(json);
		});		
		return dfd.promise();
	}