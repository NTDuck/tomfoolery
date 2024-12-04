package org.tomfoolery.configurations.monolith.gui.view.staff.actions.documents;

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
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.staff.documents.persistence.AddDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.AddDocumentController;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AddDocumentView {
    private @NonNull String currentDocumentContentPath;

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
            @NonNull DocumentContentRepository documentContentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull DocumentsManagementView parentView) {
        this.controller = AddDocumentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.parentView = parentView;
        currentDocumentContentPath = "";
    }

    @FXML
    public void initialize() {
        errorMessage.setVisible(false);
        addDocumentButton.setOnAction(event -> addDocument());
        cancelButton.setOnAction(event -> closeView());
        coverImageChooserButton.setOnAction(event -> openCoverImageFileChooser());
        pdfChooserButton.setOnAction(event -> openPdfFileChooser());

        coverImageChooserButton.setGraphic(getDefaultCoverImage());
        chosePdfInfo.setText("No file selected");
    }

    private ImageView getDefaultCoverImage() {
        ImageView coverImageView = new ImageView(new Image("/images/dayxi.png"));
        coverImageView.setFitHeight(160);
        coverImageView.setFitWidth(160);

        return coverImageView;
    }

    private String getCurrentCoverImagePath() {
        ImageView imageView = (ImageView) coverImageChooserButton.getGraphic();

        Image img = imageView.getImage();
        String path = img.getUrl();
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
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
        } catch (AddDocumentController.DocumentPublishedYearInvalidException e) {
            throw new RuntimeException(e);
        } catch (AddDocumentUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
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
        String documentCoverImagePath = getCurrentCoverImagePath();
        System.out.println("added document's cover image path: " + documentCoverImagePath);

        try {
            val documentPublishedYear = Short.parseShort(rawDocumentPublishedYear);
            return AddDocumentController.RequestObject.of(documentISBN, documentTitle, documentDescription, documentAuthors,
                    documentGenres, documentPublishedYear, documentPublisher, currentDocumentContentPath, documentCoverImagePath);

        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private void onSuccess() {
        errorMessage.setVisible(false);
        parentView.showDocuments();
        closeView();
    }

    private void showErrorMessage(String message) {
        errorMessage.setVisible(true);
        errorMessage.setText(message);
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
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
