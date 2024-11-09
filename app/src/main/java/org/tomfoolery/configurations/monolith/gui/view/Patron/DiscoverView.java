package org.tomfoolery.configurations.monolith.gui.view.Patron;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;

public class DiscoverView {
    private final @NonNull StageManager stageManager;

    public DiscoverView(@NonNull StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    private Button sidebarDashboardButton;

    @FXML
    private Button sidebarDiscoverButton;

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
        stageManager.openMenu("/fxml/Patron/Dashboard.fxml", "Dashboard");
    }
}