package com.knx.postergenerator;

public class Product {

    public enum TEMP {
        square,
        landspaceMedium,
        landspaceLage,
        portrait
    };

    private String title;
    private Double price;
    private String imgLocation;
    private String imgName;
    private TEMP size;
    
    public TEMP getSize() {
        return size;
    }

    public String getSizeString(){
        switch (size) {
            case square:
                return "square";
            case landspaceMedium:
                return "landspaceMedium";
            case landspaceLage:
                return "landspaceLage";
            case portrait:
                return "portrait";
            default:
                return "square";
        }
    }


    public void setSize(String inSize) {
        switch (inSize) {
            case "square":
                size = TEMP.square;
                break;
            case "landspaceMedium":
                size = TEMP.landspaceMedium;
                break;
            case "landspaceLage":
                size = TEMP.landspaceLage;
                break;
            case "portrait":
                size = TEMP.portrait;
                break;
            default:
                size = TEMP.square;
                break;
        }
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getImgLocation() {
        return imgLocation;
    }
    public void setImgLocation(String imgLocation) {
        this.imgLocation = imgLocation;
    }
    public String getImgName() {
        return imgName;
    }
    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

}
