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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.anycook.recipe.Time;

import java.util.Set;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Query {
    private Set<String> tags;
    private Set<String> ingredients;
    private Set<String> excludedIngredients;
    private String terms;
    private int user;
    private String category;
    private int skill;
    private int calorie;
    private Time time;

    private int start;
    private int num;

    public Query() {
        user = -1;
        tags = null;
        ingredients = null;
        category = null;
        excludedIngredients = null;
        skill = 0;
        calorie = 0;

        //default values
        start = 0;
        num = 10;
    }


    //tags
    public boolean hasTags() {
        return tags != null && tags.size() > 0;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }


    //ingredients
    public boolean hasIngredients() {
        return ingredients != null && ingredients.size() > 0;
    }

    public Set<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<String> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean hasExcludedIngredients() {
        return excludedIngredients != null && excludedIngredients.size() > 0;
    }

    public Set<String> getExcludedIngredients() {
        return excludedIngredients;
    }

    public void setExcludedIngredients(Set<String> excludedIngredients) {
        this.excludedIngredients = excludedIngredients;
    }


    //terms
    public boolean hasTerms() {
        return terms != null;
    }

    public String getTerms(){
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    //user
    public boolean hasUser() {
        return user != -1;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    //category
    public boolean hasCategory() {
        return category != null;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    //skill
    public boolean hasSkill() {
        return skill > 0;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    //calorie
    public boolean hasCalorie() {
        return calorie != 0;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    //time
    public boolean hasTime() {
        return time != null && time.std + time.min > 0;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


    //start
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }


    //num
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isValid() {
        return hasCalorie() || hasCategory() || hasExcludedIngredients() || hasUser() || hasTags() || hasIngredients()
                || hasSkill() || hasTime();
    }
}
