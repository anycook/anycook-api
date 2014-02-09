/**
 * @license This file is part of anycook. The new internet cookbook
 * Copyright (C) 2013 Jan Graßegger
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 * 
 * @author Jan Graßegger <jan@anycook.de>
 */
'use strict';
//user([userid], [callback])
AnycookAPI.upload = {
	//recipeImage(file, progressCallback, complete [, uploadedCallback])
	recipeImage : function(file, progressCallback, complete, uploaded){
		AnycookAPI._postFile('/upload/image/recipe', {file : file}, progressCallback, complete, uploaded);
	},
	//userImage(file, progressCallback, complete [, uploadCallback])
	userImage : function(file, progressCallback, complete, uploaded){
		AnycookAPI._postFile('/upload/image/user', {file : file}, progressCallback, complete, uploaded);
	},
	//imagePath(fileName[, type[, size]])
	imagePath : function(fileName, type, size){
		var settings = AnycookAPI._settings();
		size = size || 'big';
		type = type || 'recipe';
		return settings.baseUrl+'/images/'+type+'/'+size+'/'+fileName;
	}
}