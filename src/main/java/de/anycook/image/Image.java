package de.anycook.image;

import de.anycook.conf.Configuration;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public  class Image{
    private String small, big, original;

    public Image(){}

    public Image(String image, String path){
        String imageRoot = Configuration.getPropertyImageBasePath();
        imageRoot += path;
        this.small = imageRoot+"/small/"+image;
        this.big =  imageRoot+"/big/"+image;
        this.original = imageRoot+"/original/"+image;

    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getBig() {
        return big;
    }

    public void setBig(String big) {
        this.big = big;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
