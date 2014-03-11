package de.anycook.image;

import de.anycook.conf.Configuration;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public  class Image{
    private String imageRoot;
    private String image;

    public Image(){}

    public Image(String path){
        setPath(path);
    }

    public Image(String image, String path){
        setPath(path);
        setImage(image);
    }

    public String getSmall() {
        return imageRoot+"/small/"+image;
    }

    public String getBig() {
        return imageRoot+"/big/"+image;
    }

    public String getOriginal() {
        return imageRoot+"/original/"+image;
    }

    public void setPath(String path){
        String imageRoot = Configuration.getPropertyImageBasePath();
        this.imageRoot = imageRoot+path;
    }

    public void setImage(String image){
        this.image = image;
    }
}
