package org.tomfoolery;

public class Mouse {
    private String brand;
    private String model;
    private int DPI;

    public Mouse(String brand, String model, int DPI){
        this.DPI = DPI;
        this.model = model;
        this.brand = brand;
    }
    
    public void setModel(String model) {this.model = model;}
    public void setBrand(String brand) {this.brand = brand;}
    public void setDPI(int DPI) {this.DPI = DPI;}

    public String getModel() {return this.model;}
    public String getBrand() {return this.brand;}
    public int getDPI() {return this.DPI;}

    public boolean compareDPI(Mouse other) {
        return this.DPI > other.getDPI();
    }

}