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
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.user.documents.ShowDocumentsView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentCoverImageUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentMetadataUseCase;
import org.tomfoolery.core.utils.helpers.verifiers.FileVerifier;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.UpdateDocumentContentController;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.UpdateDocumentCoverImageController;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.UpdateDocumentMetadataController;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

public class UpdateDocumentView {
    private @NonNull String currentDocumentContentPath = "";

    private final @NonNull UpdateDocumentMetadataController metadataController;
    private final @NonNull UpdateDocumentContentController contentController;
    private final @NonNull UpdateDocumentCoverImageController coverImageController;

    private final ShowDocumentsView.@NonNull DocumentViewModel documentViewModel;

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
    private Button updateDocumentButton;

    @FXML
    private Button cancelButton;

    public UpdateDocumentView(
            ShowDocumentsView.@NonNull DocumentViewModel documentViewModel,
            @NonNull DocumentRepository documentRepository,
            @NonNull DocumentContentRepository documentContentRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository,
            @NonNull FileVerifier fileVerifier
    ) {
        this.documentViewModel = documentViewModel;
        this.metadataController = UpdateDocumentMetadataController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.contentController = UpdateDocumentContentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
        this.coverImageController = UpdateDocumentCoverImageController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    @FXML
    public void initialize() {
        ISBN.setEditable(false);
        errorMessage.setVisible(false);
        loadInitialInfo();
        updateDocumentButton.setOnAction(event -> updateDocument());
        cancelButton.setOnAction(event -> closeView());
        coverImageChooserButton.setOnAction(event -> openCoverImageFileChooser());
        pdfChooserButton.setOnAction(event -> openPdfFileChooser());

        coverImageChooserButton.setGraphic(getDefaultCoverImage());
        chosePdfInfo.setText("No file selected");
    }

    private void loadInitialInfo() {
        ISBN.setText(documentViewModel.getISBN());
        title.setText(documentViewModel.getTitle());
        authors.setText(documentViewModel.getAuthors());
        genres.setText(documentViewModel.getGenres());
        description.setText(documentViewModel.getDescription());
        publisher.setText(documentViewModel.getPublisher());
        publishedYear.setText(documentViewModel.getPublishedYear());
    }

    private @NonNull ImageView getDefaultCoverImage() {
        ImageView coverImageView = new ImageView(new Image("/images/default/placeholder-book-cover.png"));
        coverImageView.setFitHeight(160);
        coverImageView.setFitWidth(160);

        return coverImageView;
    }

    private String getCurrentCoverImagePath() {
        Image buttonImage = ((ImageView) coverImageChooserButton.getGraphic()).getImage();
        String imageURL = buttonImage.getUrl();

        String absolutePath = null;

        if (imageURL != null) {
            try {
                absolutePath = Paths.get(new URI(imageURL)).toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("No image URL found");
        }

        return absolutePath;
    }

    public void openCoverImageFileChooser() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png"),
                new FileChooser.ExtensionFilter("JPG files", "*.jpg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = StageManager.getInstance().getPrimaryStage();

        File selectedFile = fileChooser.showOpenDialog(stage);

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

    public void updateDocument() {
        try {
            val metadataRequestObject = this.collectMetadataRequestObject();
            this.metadataController.accept(metadataRequestObject);

            val contentRequestObject = this.collectContentRequestObject();
            this.contentController.accept(contentRequestObject);

            val coverImageRequestObject = this.collectCoverImageRequestObject();
            this.coverImageController.accept(coverImageRequestObject);

            this.onSuccess();
        } catch (UpdateDocumentCoverImageUseCase.AuthenticationTokenNotFoundException |
                 UpdateDocumentCoverImageUseCase.AuthenticationTokenInvalidException exception) {
            StageManager.getInstance().openLoginMenu();
        } catch (DocumentPublishedYearInvalidException |
                 UpdateDocumentMetadataController.DocumentPublishedYearInvalidException exception) {
            this.onDocumentPublishedYearInvalidException();
        } catch (UpdateDocumentContentUseCase.DocumentISBNInvalidException |
                 UpdateDocumentCoverImageUseCase.DocumentISBNInvalidException |
                 UpdateDocumentMetadataUseCase.DocumentISBNInvalidException exception) {
            this.onDocumentISBNInvalid();
        } catch (UpdateDocumentContentUseCase.DocumentNotFoundException |
                 UpdateDocumentCoverImageUseCase.DocumentNotFoundException |
                 UpdateDocumentMetadataUseCase.DocumentNotFoundException exception) {
            System.err.println("This never happens btw");
        } catch (UpdateDocumentCoverImageUseCase.DocumentCoverImageInvalidException exception) {
            this.onDocumentCoverImageInvalidException();
        } catch (UpdateDocumentCoverImageController.DocumentCoverImageFilePathInvalidException exception) {
            this.onDocumentCoverImageFilePathInvalidException();
        } catch (UpdateDocumentContentUseCase.DocumentContentInvalidException exception) {
            this.onDocumentContentInvalidException();
        } catch (UpdateDocumentContentController.DocumentContentFilePathInvalidException exception) {
            this.onDocumentContentFilePathInvalidException();
        }
    }

    private UpdateDocumentMetadataController.@NonNull RequestObject collectMetadataRequestObject() throws DocumentPublishedYearInvalidException {
        String ISBN = this.ISBN.getText();

        String documentTitle = title.getText();
        String documentDescription = description.getText();
        String rawDocumentAuthors = authors.getText();
        String rawDocumentGenres = genres.getText();

        short documentPublishedYear = this.collectDocumentPublishedYear();
        String documentPublisher = publisher.getText();

        val documentAuthors = Arrays.asList(Arrays.stream(rawDocumentAuthors.split(",")).parallel().map(String::trim).toArray(String[]::new));
        val documentGenres = Arrays.asList(Arrays.stream(rawDocumentGenres.split(",")).parallel().map(String::trim).toArray(String[]::new));

        return UpdateDocumentMetadataController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher);
    }

    private @Unsigned short collectDocumentPublishedYear() throws DocumentPublishedYearInvalidException {
        val rawDocumentPublishedYear = publishedYear.getText();

        try {
            return Short.parseShort(rawDocumentPublishedYear);
        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private UpdateDocumentContentController.@NonNull RequestObject collectContentRequestObject() {
        String ISBN = this.ISBN.getText();
        String documentContentFilePath = this.currentDocumentContentPath;

        return UpdateDocumentContentController.RequestObject.of(ISBN, documentContentFilePath);
    }

    private UpdateDocumentCoverImageController.@NonNull RequestObject collectCoverImageRequestObject() {
        String ISBN = this.ISBN.getText();
        String coverImagePath = this.getCurrentCoverImagePath();

        return UpdateDocumentCoverImageController.RequestObject.of(ISBN, coverImagePath);
    }

    private void onSuccess() {
        errorMessage.setVisible(false);
        closeView();
        StageManager.getInstance().loadStaffView(StageManager.ContentType.STAFF_DOCUMENTS_MANAGEMENT);
    }

    private void showErrorMessage(String message) {
        errorMessage.setVisible(true);
        errorMessage.setText(message);
    }

    private void onDocumentISBNInvalid() {
        showErrorMessage("ISBN is invalid");
    }

    private void onDocumentPublishedYearInvalidException() {
        showErrorMessage("Document published year invalid");
    }

    private void onDocumentContentInvalidException() {
        showErrorMessage("Invalid pdf file");
    }

    private void onDocumentCoverImageInvalidException() {
        showErrorMessage("Invalid cover image!");
    }

    private void onDocumentContentFilePathInvalidException() {
        showErrorMessage("PDF file path is invalid!");
    }

    private void onDocumentCoverImageFilePathInvalidException() {
        showErrorMessage("Cover image is invalid!");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
