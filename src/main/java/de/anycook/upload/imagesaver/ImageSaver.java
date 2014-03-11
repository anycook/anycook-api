package de.anycook.upload.imagesaver;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public abstract class ImageSaver {
    public abstract void save(String path, String fileName, byte[] bytes) throws IOException;
    public abstract void save(String path, String fileName, BufferedImage newImage) throws IOException;
}
