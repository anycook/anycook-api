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

package de.anycook.upload;


import de.anycook.db.mysql.DBUpload;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.SQLException;


public class RecipeUploader extends UploadHandler {

    public RecipeUploader() {
        super(100, 450, "recipe/");
    }

    /**
     * erzeugt einen zufaelligen eindeutigen Dateinamen
     *
     * @return erzeugter Dateiname
     */
    protected String makeAndCheckFilename() throws SQLException {
        try (DBUpload dbUpload = new DBUpload()) {
            boolean uniqueFileName;
            String filename;
            do {
                filename = RandomStringUtils.randomAlphanumeric(20);
                uniqueFileName = dbUpload.checkRecipeFilename(filename);
            } while (!uniqueFileName);

            return filename;
        }
    }
}
