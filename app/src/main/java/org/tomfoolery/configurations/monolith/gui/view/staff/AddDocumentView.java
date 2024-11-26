package org.tomfoolery.configurations.monolith.gui.view.staff;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.AddDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.AddDocumentController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AddDocumentView {
    private final @NonNull ImageView defaultCoverImage = new ImageView(new Image("/images/dayxi.png"));
    private @NonNull String currentDocumentContentPath = "";
    private @NonNull String currentCoverImagePath = "";

    private final @NonNull AddDocumentController controller;
    private final @NonNull DocumentsManagementView parentView;

    @FXML
    private TextField title;

    @FXML
    private TextField ISBN;

    @FXML
    private TextField authors;

    @FXML
    private TextField genres;

    @FXML
    private TextArea description;

    @FXML
    private TextField publisher;

    @FXML
    private TextField publishedYear;

    @FXML
    private Button coverImageChooserButton;

    @FXML
    private Button pdfChooserButton;

    @FXML
    private Label chosePdfInfo;

    @FXML
    private Label errorMessage;

    @FXML
    private Button addDocumentButton;

    @FXML
    private Button cancelButton;

    public AddDocumentView(
            @NonNull DocumentRepository documentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull DocumentsManagementView parentView) {
        this.controller = AddDocumentController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.parentView = parentView;
    }

    @FXML
    public void initialize() {
        defaultCoverImage.setFitHeight(160);
        defaultCoverImage.setFitWidth(160);

        errorMessage.setVisible(false);
        addDocumentButton.setOnAction(event -> addDocument());
        cancelButton.setOnAction(event -> closeView());
        coverImageChooserButton.setOnAction(event -> openCoverImageFileChooser());
        pdfChooserButton.setOnAction(event -> openPdfFileChooser());

        coverImageChooserButton.setGraphic(defaultCoverImage);
        currentCoverImagePath = getDefaultCoverImageFilePath();
        chosePdfInfo.setText("No file selected");
    }

    public File getCoverImageFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png"),
                new FileChooser.ExtensionFilter("JPG files", "*.jpg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        return fileChooser.showOpenDialog(stage);
    }

    public void openCoverImageFileChooser() {
        File selectedFile = getCoverImageFile();

        if (selectedFile != null) {
            ImageView coverImage = new ImageView(new Image(selectedFile.toURI().toString()));
            coverImage.setFitHeight(160);
            coverImage.setFitWidth(160);
            coverImageChooserButton.setGraphic(coverImage);
            this.currentCoverImagePath = selectedFile.getAbsolutePath();
        }
    }

    private File getPdfFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF files", "*.pdf"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        return fileChooser.showOpenDialog(stage);
    }

    public void openPdfFileChooser() {
        File selectedFile = getPdfFile();

        if (selectedFile != null) {
            chosePdfInfo.setText(selectedFile.getName());
            this.currentDocumentContentPath = selectedFile.getAbsolutePath();
        } else {
            if (currentDocumentContentPath.isEmpty()) {
                chosePdfInfo.setText("No file selected");
            }
        }
    }

    public void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().getFirst().setMouseTransparent(false);
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    public void addDocument() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();
        } catch (DocumentPublishedYearInvalidException exception) {
            this.onDocumentPublishedYearInvalidException();
        } catch (AddDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (AddDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (AddDocumentUseCase.DocumentAlreadyExistsException exception) {
            this.onDocumentAlreadyExistsException();
        } catch (AddDocumentController.DocumentCoverImageFilePathInvalidException e) {
            this.onCoverImageFilePathInvalid();
        } catch (AddDocumentController.DocumentContentFilePathInvalidException e) {
            this.onContentFilePathInvalid();
        }
    }

    private AddDocumentController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException {
        String documentISBN = this.ISBN.getText();
        String documentTitle = this.title.getText();
        List<String> documentAuthors = Arrays.asList(authors.getText().split(","));
        List<String> documentGenres = Arrays.asList(genres.getText().split(","));
        String documentPublisher = this.publisher.getText();
        String rawDocumentPublishedYear = this.publishedYear.getText();
        String documentDescription = this.description.getText();

        try {
            val documentPublishedYear = Short.parseShort(rawDocumentPublishedYear);
            return AddDocumentController.RequestObject.of(documentISBN, documentTitle, documentDescription, documentAuthors,
                    documentGenres, documentPublishedYear, documentPublisher, currentDocumentContentPath, currentCoverImagePath);

        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private String getCurrentCoverImageFilePath() {
        ImageView imageView = (ImageView) coverImageChooserButton.getGraphic();
        Image image = imageView.getImage();
        String url = image.getUrl();
        if (url != null && url.startsWith("file:")) {
            return url.substring(5);
        }
        return url;
    }

    private @NotNull String getDefaultCoverImageFilePath() {
        Image tmp = defaultCoverImage.getImage();
        String url = tmp.getUrl();
        if (url.startsWith("file:")) {
            return url.substring(5);
        }
        return url;
    }

    private void onSuccess() {
        errorMessage.setVisible(false);
        closeView();
        parentView.showDocuments();
    }

    private void onDocumentAlreadyExistsException() {
        showErrorMessage("Document already exists");
    }

    private void onAuthenticationTokenInvalidException() {
        showErrorMessage("Authentication token invalid");
    }

    private void onAuthenticationTokenNotFoundException() {
        showErrorMessage("Authentication token not found");
    }

    private void onDocumentPublishedYearInvalidException() {
        showErrorMessage("Document published year invalid");
    }

    private void onContentFilePathInvalid() {
        showErrorMessage("Path to document's content is invalid");
    }

    private void onCoverImageFilePathInvalid() {
        showErrorMessage("Path to document's cover image is invalid");
        System.out.println(this.currentCoverImagePath);
    }

    private void showErrorMessage(String message) {
        errorMessage.setVisible(true);
        errorMessage.setText(message);
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
