package org.tomfoolery;

public class App {
    public static final String NAME = "tomfoolery";

    static Mouse hieu = new Mouse("Logitech", "G102", 8000);
    static Mouse duy = new Mouse("Razer", "Viper 8Khz", 20000);

    public static void main(String[] args) {
        // System.out.println("Hello from " + NAME + " :3");
        System.out.println("Hieu's Mouse: " + hieu.getBrand() + " " + hieu.getModel());
        System.out.println("Duy's Mouse: " + duy.getBrand() + " " + duy.getModel());

        if(duy.compareDPI(hieu)) {
            System.out.println("Duy's mouse has higher DPI than Hieu's");
        }
        else System.out.println("Hieu's mouse has higher DPI than Duy's");
    }
};
