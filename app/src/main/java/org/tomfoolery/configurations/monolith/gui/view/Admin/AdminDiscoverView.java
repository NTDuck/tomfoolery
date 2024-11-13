package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.View;

public class AdminDiscoverView extends View {
    public AdminDiscoverView() {
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

    @FXML
    private Button sidebarControlCenterButton;

    @FXML
    private Button notificationButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane booksContainer;

    @FXML
    public void initialize() {
        booksContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sidebarDashboardButton.setOnAction(event -> {goToDashboard();});
        sidebarControlCenterButton.setOnAction(event -> {goToControlCenter();});
        Platform.runLater(this::loadBooks);
    }

    private void loadBooks() {
        try {
            for (int i = 0; i < 100; i++) {
                VBox bookTile = createBookTileWithImage();
                booksContainer.getChildren().add(bookTile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createBookTileWithImage() {
        VBox tile = new VBox();

        try {
            Image image = new Image("/images/book-cover.jpg", 160, 240, true, true, true);
            ImageView coverImage = new ImageView(image);
            coverImage.setFitWidth(160);
            coverImage.setFitHeight(240);
            coverImage.setPreserveRatio(true);
            coverImage.setSmooth(true);
            coverImage.setCache(true);

            tile.getChildren().add(coverImage);
        } catch (Exception e) {
            Rectangle placeholder = new Rectangle(160, 240);
            tile.getChildren().add(placeholder);
        }

        return tile;
    }

    private void goToDashboard() {
        StageManager.getInstance().openMenu("/fxml/Admin/Dashboard.fxml");
    }

    private void goToControlCenter() {
        StageManager.getInstance().openMenu("/fxml/Admin/ControlCenter.fxml");
    }
}