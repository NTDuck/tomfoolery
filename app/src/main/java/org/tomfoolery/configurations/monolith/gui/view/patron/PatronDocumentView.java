package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.BorrowDocumentUseCase;
import org.tomfoolery.core.usecases.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.persistence.BorrowDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.persistence.ReturnDocumentController;

import java.io.File;

public class PatronDocumentView {
    private final @NonNull GetDocumentByIdController getDocumentByIdController;
    private final @NonNull BorrowDocumentController borrowDocumentController;
    private final @NonNull ReturnDocumentController returnDocumentController;
    private final @NonNull String documentISBN;

    public PatronDocumentView(
            @NonNull String documentISBN,
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull BorrowingSessionRepository borrowingSessionRepository
            ) {
        this.documentISBN = documentISBN;
        this.borrowDocumentController = BorrowDocumentController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.getDocumentByIdController = GetDocumentByIdController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.returnDocumentController = ReturnDocumentController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
    private Label message;

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
    private ScrollPane descriptionScrollPane;

    @FXML
    private Label descriptionArea;

    @FXML
    public void initialize() {
        descriptionScrollPane.setFitToWidth(true);
        loadInfo();
        borrowButton.setOnAction(event -> borrowDocument());
        returnButton.setOnAction(event -> returnDocument());
        closeButton.setOnAction(event -> closeView());
        message.setVisible(false);
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
        } catch (GetDocumentByIdUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private GetDocumentByIdController.@NonNull RequestObject collectRequestObject() {
        return GetDocumentByIdController.RequestObject.of(documentISBN);
    }

    private void onSuccess(GetDocumentByIdController.@NonNull ViewModel viewModel) {
        ratingLabel.setText(String.format("Rating: %.2f / 5 (%d)", viewModel.getAverageRating(), viewModel.getNumberOfRatings()));
        titleLabel.setText("Title: " + viewModel.getDocumentTitle());
        authorsLabel.setText("Authors: " + String.join(", ", viewModel.getDocumentAuthors()));
        genresLabel.setText("Genres: " + String.join(", ", viewModel.getDocumentGenres()));
        isbnLabel.setText("ISBN: " + this.documentISBN);
        publisherLabel.setText("Publisher: " + viewModel.getDocumentPublisher());
        yearPublishedLabel.setText("Year Published: " + String.valueOf(viewModel.getDocumentPublishedYear()));
        descriptionArea.setText(viewModel.getDocumentDescription());

        String coverImagePath = viewModel.getDocumentCoverImageFilePath();
        File coverImageFile = new File(coverImagePath);
        coverImage.setImage(new Image(coverImageFile.toURI().toString()));
        coverImage.setFitHeight(540);
        coverImage.setFitWidth(310);
    }

    private void borrowDocument() {
        val requestObject = this.collectBorrowDocumentRequestObject();

        try {
            this.borrowDocumentController.accept(requestObject);
            this.onBorrowingSuccess();

        } catch (BorrowDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("how is auth token not found!?");
        } catch (BorrowDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (BorrowDocumentUseCase.DocumentNotFoundException exception) {
            System.err.println("document not found");
        } catch (BorrowDocumentUseCase.DocumentAlreadyBorrowedException exception) {
            this.onDocumentAlreadyBorrowedException();
        } catch (BorrowDocumentUseCase.DocumentBorrowLimitExceeded e) {
            throw new RuntimeException(e);
        } catch (BorrowDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private BorrowDocumentController.@NonNull RequestObject collectBorrowDocumentRequestObject() {
        return BorrowDocumentController.RequestObject.of(documentISBN);
    }

    private void onBorrowingSuccess() {
        message.setVisible(true);
        setSuccessStyleForMessage(message);
        message.setText("Document borrowed!");
    }

    private void onDocumentAlreadyBorrowedException() {
        message.setVisible(true);
        setErrorStyleForMessage(message);
        message.setText("Document already borrowed!");
    }

    private void onAuthenticationTokenInvalidException() {
        message.setVisible(true);
        setErrorStyleForMessage(message);
        message.setText("You have to be a patron to borrow and return documents");
    }

    private void returnDocument() {
        val requestObject = this.collectReturnDocumentRequestObject();

        try {
            this.returnDocumentController.accept(requestObject);
            this.onReturningSuccess();
        } catch (ReturnDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("how is auth token not found!?");
        } catch (ReturnDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReturnDocumentUseCase.DocumentNotFoundException exception) {
            System.err.println("document not found");
        } catch (ReturnDocumentUseCase.DocumentNotBorrowedException exception) {
            this.onDocumentNotBorrowedException();
        } catch (ReturnDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private void onReturningSuccess() {
        message.setVisible(true);
        setSuccessStyleForMessage(message);
        message.setText("Document returned!");
    }

    private void onDocumentNotBorrowedException() {
        message.setVisible(true);
        setErrorStyleForMessage(message);
        message.setText("You haven't borrowed this document.");
    }

    private ReturnDocumentController.@NonNull RequestObject collectReturnDocumentRequestObject() {
        return ReturnDocumentController.RequestObject.of(documentISBN);
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    private void setSuccessStyleForMessage(@NotNull Label label) {
        label.setStyle("-fx-text-fill: green; -fx-font-family: \"Segoe UI Variable\"; -fx-font-size: 16; -fx-background-color: transparent;");
    }

    private void setErrorStyleForMessage(@NotNull Label label) {
        label.setStyle("-fx-text-fill: red; -fx-font-family: \"Segoe UI Variable\"; -fx-font-size: 16; -fx-background-color: transparent;");
    }
}
