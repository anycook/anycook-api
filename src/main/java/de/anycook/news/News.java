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

package de.anycook.news;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class News implements Comparable<News> {
    private int id;
    private long datetime;

    public News(){}

    public News(int id, Date datetime) {
        this.id = id;
        this.datetime = datetime.getTime();
    }

    public int getId() {
        return id;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    @Override
    public int compareTo(News o) {
        return Long.compare(datetime, o.datetime);
    }
}
