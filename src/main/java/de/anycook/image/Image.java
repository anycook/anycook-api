package de.anycook.image;

import de.anycook.conf.Configuration;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class Image{
    public final String small;
    public final String big;
    public final String original;

    public Image(String image, String path){
        String imageRoot = Configuration.getPropertyImageBasePath();
        imageRoot += path;
        small = imageRoot+"/small/"+image;
        big = imageRoot+"/big/"+image;
        original = imageRoot+"/original/"+image;
    }
}
