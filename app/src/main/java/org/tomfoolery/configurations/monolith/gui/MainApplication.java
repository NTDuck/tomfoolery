package org.tomfoolery.configurations.monolith.gui;

import org.tomfoolery.configurations.monolith.gui.view.Components.MenuLogo;
import org.tomfoolery.configurations.monolith.gui.view.Components.NavBar;
import org.tomfoolery.configurations.monolith.gui.view.Components.ProfileIcon;
import org.tomfoolery.configurations.monolith.gui.view.Components.SearchBar;
import org.tomfoolery.configurations.monolith.gui.view.Components.SidebarComponents;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        Scene scene = new Scene(root);

        MenuLogo menuLogo = new MenuLogo("/menu_logo.png", 40, 230);
        SearchBar searchBar = new SearchBar("/search_icon.png", 500, 60);
        ProfileIcon profileIcon = new ProfileIcon("/profile_icon.png", 60);

        NavBar navbar = new NavBar(80, 10, 10, menuLogo.getLogo(), searchBar.getSearchBar(), profileIcon.getIcon());

        HBox belowNavbar = new HBox();
        VBox.setVgrow(belowNavbar, Priority.ALWAYS);
        belowNavbar.setStyle("-fx-background-color: orange");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(300);
        sidebar.setAlignment(Pos.CENTER);
        sidebar.setSpacing(40);
        HBox.setMargin(sidebar, new Insets(20, 0, 0, 20));
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: lightgray; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 10;");
        SidebarComponents sidebarComponents = new SidebarComponents();
        sidebar.getChildren().addAll(sidebarComponents.getCategory(), sidebarComponents.getContact(),
                sidebarComponents.getDashboard());

        belowNavbar.getChildren().addAll(sidebar);
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        root.getChildren().addAll(navbar.getNavBar(), belowNavbar);

        Image appIcon = new Image(getClass().getResourceAsStream("/logo.png"));
        stage.getIcons().add(appIcon);
        stage.setHeight(900);
        stage.setWidth(1600);
        stage.setTitle("Tomfoolery");
        stage.setScene(scene);
        stage.show();
    }
}