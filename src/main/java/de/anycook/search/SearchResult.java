/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
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
 */

package de.anycook.search;

import de.anycook.recipe.Recipe;

import java.util.List;

public class SearchResult {
    private int size;
    private List<Recipe> results;

    public SearchResult() {};

    public SearchResult(int size, List<Recipe> results) {
        this.size = size;
        this.results = results;
    }

    public int getSize() {
        return size;
    }

    public List<Recipe> getResults() {
        return results;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}
