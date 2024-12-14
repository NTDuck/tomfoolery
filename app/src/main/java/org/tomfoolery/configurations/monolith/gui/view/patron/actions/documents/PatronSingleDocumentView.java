package org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.MessageLabelFactory;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentQrCodeGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.core.usecases.external.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.persistence.BorrowDocumentUseCase;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.persistence.ReturnDocumentUseCase;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.GetDocumentBorrowStatusUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.references.GetDocumentQrCodeController;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.persistence.BorrowDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.persistence.ReturnDocumentController;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval.GetDocumentBorrowStatusController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatronSingleDocumentView {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final @NonNull GetDocumentByIdController getDocumentByIdController;
    private final @NonNull BorrowDocumentController borrowDocumentController;
    private final @NonNull ReturnDocumentController returnDocumentController;
    private final @NonNull GetDocumentQrCodeController getDocumentQrCodeController;
    private final @NonNull GetDocumentBorrowStatusController getDocumentBorrowStatusController;
    private final @NonNull String documentISBN;

    public PatronSingleDocumentView(
            @NonNull String documentISBN,
            @NonNull HybridDocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull BorrowingSessionRepository borrowingSessionRepository,
            @NonNull DocumentQrCodeGenerator documentQrCodeGenerator,
            @NonNull DocumentUrlGenerator documentUrlGenerator,
            @NonNull FileStorageProvider fileStorageProvider
            ) {
        this.documentISBN = documentISBN;
        this.getDocumentBorrowStatusController = GetDocumentBorrowStatusController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.borrowDocumentController = BorrowDocumentController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.getDocumentByIdController = GetDocumentByIdController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
        this.returnDocumentController = ReturnDocumentController.of(documentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.getDocumentQrCodeController = GetDocumentQrCodeController.of(documentRepository, documentQrCodeGenerator, documentUrlGenerator, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
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
    private Button showQRButton;

    @FXML
    private Label message;

    @FXML
    private TextField titleLabel;

    @FXML
    private TextField authorsLabel;

    @FXML
    private TextField genresLabel;

    @FXML
    private TextField isbnLabel;

    @FXML
    private TextField publisherLabel;

    @FXML
    private TextField yearPublishedLabel;

    @FXML
    private ScrollPane descriptionScrollPane;

    @FXML
    private TextArea descriptionArea;

    @FXML
    public void initialize() {
        descriptionScrollPane.setFitToWidth(true);
        descriptionScrollPane.setFitToHeight(true);
        borrowButton.setOnAction(event -> borrowDocument());
        returnButton.setOnAction(event -> returnDocument());
        closeButton.setOnAction(event -> closeView());
        showQRButton.setOnAction(event -> showQRCode());
        message.setVisible(false);
        this.loadInfo();
        this.setStatusLabel();
    }

    private void setStatusLabel() {
        val requestObject = GetDocumentBorrowStatusController.RequestObject.of(documentISBN);
        try {
            val viewModel = this.getDocumentBorrowStatusController.apply(requestObject);
            statusLabel.setText("Borrowed");
        } catch (GetDocumentBorrowStatusUseCase.AuthenticationTokenNotFoundException |
                 GetDocumentBorrowStatusUseCase.AuthenticationTokenInvalidException |
                 GetDocumentBorrowStatusUseCase.DocumentNotBorrowedException e) {
            statusLabel.setText("Not borrowed");
        } catch (GetDocumentBorrowStatusUseCase.DocumentISBNInvalidException |
                 GetDocumentBorrowStatusUseCase.DocumentNotFoundException e) {
            System.err.println("Document ISBN invalid or document not found.");
        }
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
        Image image = new Image("file:" + coverImagePath, 260, 400, false, true);
        coverImage.setImage(image);
        coverImage.setFitHeight(400);
        coverImage.setFitWidth(260);
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
            this.onDocumentBorrowLimitExceeded();
        } catch (BorrowDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private BorrowDocumentController.@NonNull RequestObject collectBorrowDocumentRequestObject() {
        return BorrowDocumentController.RequestObject.of(documentISBN);
    }

    private void onBorrowingSuccess() {
        MessageLabelFactory.createSuccessLabel("Document borrowed successfully", 16, message);
        setStatusLabel();
    }

    private void onDocumentAlreadyBorrowedException() {
        MessageLabelFactory.createErrorLabel("Document already borrowed", 16, message);
    }

    private void onAuthenticationTokenInvalidException() {
        MessageLabelFactory.createErrorLabel("You have to be a patron to borrow and return documents", 16, message);
    }

    private void onDocumentBorrowLimitExceeded() {
        MessageLabelFactory.createErrorLabel("You have borrowed this document too much!", 16, message);

    }

    private void returnDocument() {
        val requestObject = this.collectReturnDocumentRequestObject();

        try {
            this.returnDocumentController.accept(requestObject);
            this.onReturningSuccess();
        } catch (ReturnDocumentUseCase.AuthenticationTokenNotFoundException |
                 ReturnDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReturnDocumentUseCase.DocumentNotBorrowedException exception) {
            this.onDocumentNotBorrowedException();
        } catch (ReturnDocumentUseCase.DocumentNotFoundException |
                 ReturnDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private void onReturningSuccess() {
        MessageLabelFactory.createSuccessLabel("Document returned successfully", 16, message);
        setStatusLabel();
    }

    private void onDocumentNotBorrowedException() {
        MessageLabelFactory.createErrorLabel("You haven't borrowed this document!", 16, message);
    }

    private ReturnDocumentController.@NonNull RequestObject collectReturnDocumentRequestObject() {
        return ReturnDocumentController.RequestObject.of(documentISBN);
    }

    private void showQRCode() {
        val requestObject = this.collectQRCodeRequestObject();

        try {
            val viewModel = this.getDocumentQrCodeController.apply(requestObject);
            this.displayQRCode(viewModel);

        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException | GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException exception) {
            StageManager.getInstance().openLoginMenu();
        } catch (GetDocumentQrCodeUseCase.DocumentNotFoundException |
                 GetDocumentQrCodeUseCase.DocumentISBNInvalidException e) {
            System.err.println("never happens");
        } catch (GetDocumentQrCodeController.DocumentQrCodeFileWriteException e) {
            MessageLabelFactory.createErrorLabel("QR code unavailable.", 16, message);

        }
    }

    private GetDocumentQrCodeController.@NonNull RequestObject collectQRCodeRequestObject() {
        return GetDocumentQrCodeController.RequestObject.of(documentISBN);
    }

    private void displayQRCode(GetDocumentQrCodeController.@NonNull ViewModel viewModel) {
        val documentQrCodeFilePath = viewModel.getDocumentQrCodeFilePath();

        executor.submit(() -> {
            File pdfFile = new File(documentQrCodeFilePath);
            if (pdfFile.exists()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        System.err.println("Error opening pdf: " + e.getMessage());
                    });
                }
            } else {
                Platform.runLater(() -> {
                    System.err.println("File not found" + documentQrCodeFilePath);
                });
            }
        });
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }
}
