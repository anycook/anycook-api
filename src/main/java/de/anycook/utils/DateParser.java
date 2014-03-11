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

package de.anycook.utils;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {
    private final static Logger logger = Logger.getLogger(DateParser.class);
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String DATETIME_FORMAT = DATE_FORMAT + " HH:mm:ss";
    private final static DateFormat datetimeparser = new SimpleDateFormat(DATETIME_FORMAT);
    private final static DateFormat dateparser = new SimpleDateFormat(DATE_FORMAT);

    public static Date parseDateTime(String datetime) {
        try {
            return datetimeparser.parse(datetime);
        } catch (ParseException e) {
            logger.error("failed to parse datetime from " + datetime, e);
            return null;
        }
    }

    public static Date parseDate(String date) {
        try {
            return dateparser.parse(date);
        } catch (ParseException e) {
            logger.error("failed to parse date from " + date, e);
            return null;
        }
    }

    public static String dateToString(Date date) {
        if (date == null) return null;
        return dateparser.format(date);
    }

    public static String datetimeToString(Date datetime) {
        if (datetime == null) return null;
        return datetimeparser.format(datetime);
    }
}
