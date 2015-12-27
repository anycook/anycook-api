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

package de.anycook.stopwords;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Stopwords {
    private static Logger logger = LogManager.getLogger(Stopwords.class);
    private final static String deStopWordPath = "/var/www/sites/anycook.de/stopwords/deStopWordList.txt";

    public static Set<String> getStopWords() {
        Set<String> stopwords = new HashSet<String>();
        try {
            Scanner scanner = new Scanner(new FileInputStream(deStopWordPath));
            while (scanner.hasNextLine()) {
                String stopword = scanner.nextLine();
                stopwords.add(stopword);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            logger.error(e);
        }
        return stopwords;
    }
}
