package de.anycook.upload.imagesaver;

import de.anycook.conf.Configuration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class LocalImageSaver extends ImageSaver {
    private final static String imageRoot = Configuration.getInstance().getImageRoot();
    protected final File imagePath;

    public LocalImageSaver(String subPath){
        this.imagePath = new File(imageRoot, subPath);
    }

    @Override
    public void save(String path, String fileName, BufferedImage newImage) throws IOException {
        File imageFile = new File(imagePath, path + fileName);
        if (!imageFile.exists() && !imageFile.createNewFile())
            throw new IOException("failed to create file: " + imageFile.getAbsolutePath());

        ImageIO.write(newImage, "png", imageFile);
    }

    @Override
    public void save(String path, String fileName, byte[] bytes) throws IOException {
        File file = new File(imagePath, path +  fileName);
        if (!file.exists() && !file.createNewFile())
            throw new IOException("failed to create file: " + file.getAbsolutePath());
        try(FileOutputStream out = new FileOutputStream(file)){
            out.write(bytes);
        }
    }
}
