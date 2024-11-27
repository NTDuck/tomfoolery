package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.GetDocumentByIdController;

import java.io.File;

public class PatronDocumentView {
    private final @NonNull GetDocumentByIdController getDocumentByIdController;
    private final @NonNull String documentISBN;

    public PatronDocumentView(
            @NonNull String documentISBN,
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository
    ) {
        this.documentISBN = documentISBN;
        this.getDocumentByIdController = GetDocumentByIdController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView coverImage;

    @FXML
    private Label ratingLabel;

    @FXML
    private Button borrowButton;

    @FXML
    private Button returnButton;

    @FXML
    private Button closeButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label authorsLabel;

    @FXML
    private Label genresLabel;

    @FXML
    private Label isbnLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label yearPublishedLabel;

    @FXML
    private Label descriptionArea;

    @FXML
    public void initialize() {
        loadInfo();
        borrowButton.setOnAction(event -> borrowDocument());
        returnButton.setOnAction(event -> returnDocument());
        closeButton.setOnAction(event -> closeView());
    }

    public void loadInfo() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.getDocumentByIdController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (GetDocumentByIdUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("the user does not even exist??");
        } catch (GetDocumentByIdUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("some how the auth token is invalid");
        } catch (GetDocumentByIdUseCase.DocumentNotFoundException e) {
            System.err.println("this can't be...");
        }
    }

    private GetDocumentByIdController.@NonNull RequestObject collectRequestObject() {
        return GetDocumentByIdController.RequestObject.of(documentISBN);
    }

    private void onSuccess(GetDocumentByIdController.@NonNull ViewModel viewModel) {
        val fragmentaryDocument = viewModel.getFragmentaryDocument();

        ratingLabel.setText(String.format("Rating: %.2f / 5 (%d)", fragmentaryDocument.getAverageRating(), fragmentaryDocument.getNumberOfRatings()));
        titleLabel.setText("Title: " + fragmentaryDocument.getDocumentTitle());
        authorsLabel.setText("Authors: " + String.join(", ", fragmentaryDocument.getDocumentAuthors()));
        genresLabel.setText("Genres: " + String.join(", ", fragmentaryDocument.getDocumentGenres()));
        isbnLabel.setText("ISBN: " + this.documentISBN);
        publisherLabel.setText("Publisher: " + fragmentaryDocument.getDocumentPublisher());
        yearPublishedLabel.setText("Year Published: " + String.valueOf(fragmentaryDocument.getDocumentPublishedYear()));
        descriptionArea.setText("Description: " + fragmentaryDocument.getDocumentDescription());

        String coverImagePath = fragmentaryDocument.getDocumentCoverImageFilePath();
        System.out.println("image path in patron document view: " + coverImagePath);
        File coverImageFile = new File(coverImagePath);
        coverImage.setImage(new Image(coverImageFile.toURI().toString()));
        coverImage.setFitHeight(540);
        coverImage.setFitWidth(310);
    }

    private void borrowDocument() {

    }

    private void returnDocument() {

    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
        StageManager.getInstance().getRootStackPane().getChildren().getFirst().setMouseTransparent(false);
    }
}
