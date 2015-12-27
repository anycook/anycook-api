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

import com.google.common.base.Preconditions;

import de.anycook.conf.Configuration;
import de.anycook.upload.imagesaver.AmazonS3ImageSaver;
import de.anycook.upload.imagesaver.ImageSaver;
import de.anycook.upload.imagesaver.LocalImageSaver;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;


/**
 * Die Klasse ist fuer den Dateiupload zustaendig.
 *
 * @author Jan Grassegger
 */
public abstract class UploadHandler {

    //private static ServletFileUpload upload;


    protected final Logger logger;
    private final static String temppath = "/tmp";
    //private final static int maxFileSize = 10000000;
    protected final ImageSaver imageSaver;
    private final int smallSize;
    private final int bigWidth;
    private final int bigHeight;


    public UploadHandler(int smallSize, int bigWidth, String imagePath) {
        this(smallSize, bigWidth, -1, imagePath);
    }

    /**
     * Konstruktor.
     */
    public UploadHandler(int smallSize, int bigWidth, int bigHeight, String imagePath) {
        logger = LogManager.getLogger(getClass());

        this.smallSize = smallSize;
        this.bigWidth = bigWidth;
        this.bigHeight = bigHeight;

        this.imageSaver =
                Configuration.getInstance().isImageS3Upload() ? new AmazonS3ImageSaver(imagePath) :
                new LocalImageSaver(imagePath);
    }

    /**
     * ermoeglicht Upload einer Datei. Die entstandene File wird zurueckgegeben
     *
     * @return uploaded file
     */
    public File uploadFile(InputStream inputStream) throws IOException, FileUploadException {
        String filename;

        filename = RandomStringUtils.randomAlphanumeric(15);
        File file = new File(temppath, filename);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        }
        logger.info(file.getName() + " uploaded");
        return file;
    }


    /**
     * speichert eine gegebene Bilddatei ab, indem saveSmallImage und saveBigImage genutzt werden
     *
     * @param file bilddatei, die gepeichert werden soll
     * @return Namen der gespeicherten Dateien
     */
    public String saveFile(File file) throws SQLException, IOException {
        String newFilename = makeAndCheckFilename() + ".png";
        BufferedImage image = ImageIO.read(file);

        Preconditions.checkNotNull(image);
        Preconditions.checkNotNull(newFilename);

        saveSmallImage(image, newFilename);
        saveBigImage(image, newFilename);
        saveOriginalImage(image, newFilename);
        file.delete();
        logger.debug("successfully uploaded file " + newFilename);
        return newFilename;
    }

    /**
     * speichert eine kleine Version des Bildes
     *
     * @param image    BufferedImage
     * @param filename Name der zu erzeugenden Datei
     */
    private void saveSmallImage(BufferedImage image, String filename) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();
        double imageRatio = (double) width / (double) height;

        int xtranslate = 0;
        int ytranslate = 0;

        if (imageRatio > 1) {
            xtranslate = (width - height) / 2;
        } else {
            ytranslate = (height - width) / 2;
        }

        BufferedImage
                tempImage =
                image.getSubimage(xtranslate, ytranslate, width - xtranslate * 2,
                                  height - ytranslate * 2);
        BufferedImage
                newImage =
                new BufferedImage(smallSize, smallSize, BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics()
                .drawImage(tempImage.getScaledInstance(smallSize, smallSize, Image.SCALE_SMOOTH), 0,
                           0, null);

        imageSaver.save("small/", filename, newImage);
    }

    /**
     * speichert eine grosse Version des Bildes
     *
     * @param image    BufferedImage
     * @param filename Name der zu erzeugenden Datei
     */
    private void saveBigImage(BufferedImage image, String filename) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();

        int newWidth = bigWidth;
        int newHeight = new Double(((double) height / (double) width) * newWidth).intValue();

        if (bigHeight > -1 && newHeight > bigHeight) {
            newHeight = bigHeight;
            newWidth = new Double(((double) width / (double) height) * newHeight).intValue();
        }

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics()
                .drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0,
                           null);

        imageSaver.save("big/", filename, newImage);

    }

    /**
     * speichert eine grosse Version des Bildes
     *
     * @param image    BufferedImage
     * @param filename Name der zu erzeugenden Datei
     */
    private void saveOriginalImage(BufferedImage image, String filename) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        newImage.getGraphics().drawImage(image, 0, 0, null);

        imageSaver.save("original/", filename, newImage);

    }

    protected abstract String makeAndCheckFilename() throws SQLException;

}
