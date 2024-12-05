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
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence.AddDocumentActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.AddDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.AddDocumentController;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AddDocumentView {
    private @NonNull String currentDocumentContentPath;

    private final @NonNull AddDocumentController addDocumentController;

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
        this.addDocumentController = AddDocumentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
            this.addDocumentController.accept(requestObject);
            this.onSuccess();

        } catch (DocumentPublishedYearInvalidException |
                 AddDocumentController.DocumentPublishedYearInvalidException e) {
            this.onDocumentPublishedYearInvalidException();
        } catch (AddDocumentController.DocumentCoverImageFilePathInvalidException e) {
            this.onCoverImageFilePathInvalid();
        } catch (AddDocumentController.DocumentContentFilePathInvalidException e) {
            this.onContentFilePathInvalid();
        } catch (AddDocumentUseCase.DocumentISBNInvalidException e) {
            this.onDocumentISBNInvalid();
        } catch (AddDocumentUseCase.AuthenticationTokenInvalidException e) {
            this.onAuthenticationTokenInvalidException();
        } catch (AddDocumentUseCase.DocumentAlreadyExistsException e) {
            this.onDocumentAlreadyExistsException();
        } catch (AddDocumentUseCase.AuthenticationTokenNotFoundException e) {
            this.onAuthenticationTokenNotFoundException();
        }
    }

    private AddDocumentController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException {
        String ISBN = this.ISBN.getText();

        String documentTitle = title.getText();
        String documentDescription = description.getText();
        String rawDocumentAuthors = authors.getText();
        String rawDocumentGenres = genres.getText();

        short documentPublishedYear = this.collectDocumentPublishedYear();
        String documentPublisher = publisher.getText();

        val documentAuthors = Arrays.asList(Arrays.stream(rawDocumentAuthors.split(",")).parallel().map(String::trim).toArray(String[]::new));
        val documentGenres = Arrays.asList(Arrays.stream(rawDocumentGenres.split(",")).parallel().map(String::trim).toArray(String[]::new));

        val documentContentFilePath = this.currentDocumentContentPath;
        val documentCoverImageFilePath = this.getCurrentCoverImagePath();

        return AddDocumentController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher, documentContentFilePath, documentCoverImageFilePath);
    }

    private @Unsigned short collectDocumentPublishedYear() throws DocumentPublishedYearInvalidException {
        val rawDocumentPublishedYear = publishedYear.getText();

        try {
            return Short.parseShort(rawDocumentPublishedYear);
        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
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

    private void onDocumentAlreadyExistsException() {
        showErrorMessage("Document already exists");
    }

    private void onDocumentISBNInvalid() {
        showErrorMessage("ISBN is invalid");
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
