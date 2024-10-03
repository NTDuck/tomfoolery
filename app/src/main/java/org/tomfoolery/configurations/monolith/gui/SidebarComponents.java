package org.tomfoolery.configurations.monolith.gui;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SidebarComponents {
    private HBox dashboard = new HBox();
    private HBox category = new HBox();
    private HBox contact = new HBox();

    public SidebarComponents() {
        Image homeIcon = new Image("/home_icon.png");
        ImageView homeIconView = new ImageView(homeIcon);
        homeIconView.setFitHeight(40);
        homeIconView.setFitWidth(40);
        Label homeText = new Label("Dashboard");
        homeText.setStyle("-fx-font-size: 16");
        dashboard.setStyle("-fx-background-color: red");
        dashboard.setSpacing(30);
        dashboard.getChildren().addAll(homeIconView, homeText);
        
        Image categoryIcon = new Image("/category_icon.png");
        ImageView categoryIconView = new ImageView(categoryIcon);
        categoryIconView.setFitHeight(40);
        categoryIconView.setFitWidth(40);
        Label categoryText = new Label("Category");
        category.getChildren().addAll(categoryIconView, categoryText);

        Image contactIcon = new Image("/contact_icon.png");
        ImageView contactIconView = new ImageView(contactIcon);
        contactIconView.setFitHeight(40);
        contactIconView.setFitWidth(40);
        Label contactText = new Label("Contact");
        contact.getChildren().addAll(contactIconView, contactText);
    }

    public HBox getDashboard() {
        return dashboard;
    } 

    public HBox getContact() {
        return contact;
    } 

    public HBox getCategory() {
        return category;
    } 
}
