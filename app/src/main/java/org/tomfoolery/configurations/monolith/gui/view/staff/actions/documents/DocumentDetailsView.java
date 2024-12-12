package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;

public class DocumentDetailsView {
    private final ShowDocumentsView.@NonNull DocumentViewModel documentViewModel;

    public DocumentDetailsView(ShowDocumentsView.@NonNull DocumentViewModel documentViewModel) {
        this.documentViewModel = documentViewModel;
    }

    @FXML
    private TextField isbn10;

    @FXML
    private TextField isbn13;

    @FXML
    private TextField title;

    @FXML
    private TextField authors;

    @FXML
    private TextField genres;

    @FXML
    private TextField publisher;

    @FXML
    private TextField yearPublished;

    @FXML
    private TextField rating;

    @FXML
    private TextField created;

    @FXML
    private TextField lastModified;

    @FXML
    private TextArea description;

    @FXML
    private ImageView coverImage;

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        this.isbn10.setText("- ISBN 10: " + documentViewModel.getDocumentISBN_10());
        this.isbn13.setText("- ISBN 13: " + documentViewModel.getDocumentISBN_13());
        this.title.setText("- Title: " + documentViewModel.getDocumentTitle());
        this.authors.setText("- Authors: " + documentViewModel.getDocumentAuthors());
        this.genres.setText("- Genres: " + documentViewModel.getDocumentGenres());
        this.publisher.setText("- Publisher: " + documentViewModel.getDocumentPublisher());
        this.yearPublished.setText("- Year Published: " + documentViewModel.getDocumentPublishedYear());
        this.rating.setText("- Rating: " + documentViewModel.getAverageRating() + " / 5 (" + documentViewModel.getNumberOfRatings() + " total)");

        if (documentViewModel.getCreatedTimestamp() != "null" && documentViewModel.getCreatedTimestamp() != null) {
            this.created.setText("- Added: " + documentViewModel.getCreatedTimestamp() + " by staff " + documentViewModel.getCreatedByStaffId());
        } else {
            this.created.setText("- Added: null");
        }

        if (documentViewModel.getLastModifiedTimestamp() != "null" && documentViewModel.getLastModifiedTimestamp() != null) {
            this.lastModified.setText("- Last Modified: " + documentViewModel.getLastModifiedTimestamp() + " by staff " + documentViewModel.getLastModifiedByStaffId());
        } else {
            this.lastModified.setText("- Last Modified: null");
        }

        this.description.setText("Description:\n" + documentViewModel.getDocumentDescription());
        this.coverImage.setImage(new Image("file:" + documentViewModel.getDocumentCoverImageFilePath()));

        this.closeButton.setOnAction(event -> {
            StageManager.getInstance().getRootStackPane().getChildren().removeLast();
        });
    }
}
