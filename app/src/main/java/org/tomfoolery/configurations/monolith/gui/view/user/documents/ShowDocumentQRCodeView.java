package org.tomfoolery.configurations.monolith.gui.view.user.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.utils.ErrorDialog;
import org.tomfoolery.core.usecases.external.common.documents.references.GetDocumentQrCodeUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.documents.references.GetDocumentQrCodeController;

public class ShowDocumentQRCodeView {
    private final @NonNull GetDocumentQrCodeController controller = GetDocumentQrCodeController.of(
            StageManager.getInstance().getResources().getHybridDocumentRepository(),
            StageManager.getInstance().getResources().getDocumentQrCodeGenerator(),
            StageManager.getInstance().getResources().getDocumentUrlGenerator(),
            StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
            StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
            StageManager.getInstance().getResources().getFileStorageProvider()
    );

    private final @NonNull String title;
    private final @NonNull String isbn;

    public ShowDocumentQRCodeView(String title, String isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView qrCode;

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        closeButton.setOnMouseEntered(event -> closeButton.setStyle("-fx-background-color: #5E80AB; -fx-cursor: hand; -fx-background-radius: 10; -fx-text-fill: white;"));
        closeButton.setOnMouseExited(event -> closeButton.setStyle("-fx-background-color: #80A0C0; -fx-cursor: default; -fx-background-radius: 10; -fx-text-fill: white;"));
        closeButton.setOnAction(event -> {
            StageManager.getInstance().getRootStackPane().getChildren().removeLast();
        });

        this.titleLabel.setText(title);
        this.showQRCode();
    }

    private void showQRCode() {
        val requestObject = GetDocumentQrCodeController.RequestObject.of(isbn);
        try {
            val viewModel = this.controller.apply(requestObject);
            qrCode.setImage(new Image("file:" + viewModel.getDocumentQrCodeFilePath()));
        } catch (GetDocumentQrCodeUseCase.AuthenticationTokenNotFoundException |
                 GetDocumentQrCodeUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (GetDocumentQrCodeUseCase.DocumentISBNInvalidException |
                 GetDocumentQrCodeUseCase.DocumentNotFoundException e) {
            System.err.println();
        } catch (GetDocumentQrCodeController.DocumentQrCodeFileWriteException e) {
            ErrorDialog.open("Something is wrong with the QR code!");
        }
    }
}
