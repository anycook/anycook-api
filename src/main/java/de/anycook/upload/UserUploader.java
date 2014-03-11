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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;


public class UserUploader extends UploadHandler {


    public UserUploader() {
        super(50, 200, 240, "user/");
    }


    @Override
    protected String makeAndCheckFilename() throws SQLException {
        boolean uniqueFileName;

        try (DBUpload dbupload = new DBUpload()) {
            String filename;
            do {
                filename = RandomStringUtils.randomAlphanumeric(20);
                uniqueFileName = dbupload.checkUserFilename(filename);
            } while (!uniqueFileName);

            return filename;
        }
    }

    public String saveFBURLImage(String fbpath) throws SQLException, IOException {
        String largePath = fbpath + "?type=large";
        String filename = makeAndCheckFilename() + ".png";


        byte[] smallData = getURLData(new URL(fbpath));
        byte[] largeData = getURLData(new URL(largePath));
        imageSaver.save("small/", filename, smallData);
        imageSaver.save("big/", filename, largeData);

        return filename;
    }

    private byte[] getURLData(URL url) {
        try {
            URLConnection c = url.openConnection();
            InputStream in = new BufferedInputStream(c.getInputStream());
            int contentLength = c.getContentLength();
            byte[] data = new byte[contentLength];
            int bytesRead = 0;
            int offset = 0;
            while (offset < contentLength) {
                bytesRead = in.read(data, offset, data.length - offset);
                if (bytesRead == -1)
                    break;
                offset += bytesRead;
            }

            in.close();

            return data;

        } catch (IOException e) {
            logger.error("failed to save FBImage", e);
        }

        return new byte[0];
    }

}
