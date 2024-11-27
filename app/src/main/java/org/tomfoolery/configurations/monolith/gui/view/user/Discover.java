package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.patron.PatronDocumentView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.SearchDocumentsController;

import java.io.File;

public class Discover {
    private final @NonNull SearchDocumentsController controller;

    public Discover(
            @NonNull DocumentRepository documentRepository,
            @NonNull DocumentSearchGenerator documentSearchGenerator,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository) {

        this.controller = SearchDocumentsController.of(
                documentRepository,
                documentSearchGenerator,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );
    }

    @FXML
    private TextField searchField;

    @FXML
    private CheckBox criteriaTitleCheckBox;

    @FXML
    private CheckBox criteriaGenreCheckBox;

    @FXML
    private CheckBox criteriaAuthorCheckBox;

    @FXML
    private CheckBox patternPrefixCheckBox;

    @FXML
    private CheckBox patternSuffixCheckBox;

    @FXML
    private CheckBox patternSubsequenceCheckBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane booksContainer;

    @FXML
    public void initialize() {
        booksContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        searchField.setOnAction(event -> searchBooks());
        criteriaTitleCheckBox.setOnAction(event -> checkOnTitleBox());
        criteriaGenreCheckBox.setOnAction(event -> checkOnGenreBox());
        criteriaAuthorCheckBox.setOnAction(event -> checkOnAuthorBox());
        searchBooks();
    }

    private void searchBooks() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);
        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException |
                 SearchDocumentsUseCase.AuthenticationTokenInvalidException e) {
            System.err.println("Authentication invalid or not found");
        } catch (SearchDocumentsUseCase.PaginationInvalidException e) {
            System.err.println("Pagination Invalid");
            booksContainer.getChildren().clear();
        }
    }

    private SearchDocumentsController.@NonNull RequestObject collectRequestObject() {
        val searchCriterion = getChoseCriterion();
        val searchPattern = getChosePattern();
        String searchText = searchField.getText();

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchPattern,
                searchText, 1, Integer.MAX_VALUE);
    }

    private SearchDocumentsController.@NonNull SearchCriterion getChoseCriterion() {
        if (criteriaTitleCheckBox.isSelected()) {
            checkOnTitleBox();
            return SearchDocumentsController.SearchCriterion.TITLE;
        }
        if (criteriaGenreCheckBox.isSelected()) {
            checkOnGenreBox();
            return SearchDocumentsController.SearchCriterion.GENRE;
        }
        if (criteriaAuthorCheckBox.isSelected()) {
            checkOnAuthorBox();
            return SearchDocumentsController.SearchCriterion.AUTHOR;
        }
        checkOnTitleBox();
        return SearchDocumentsController.SearchCriterion.TITLE;
    }

    private SearchDocumentsController.@NonNull SearchPattern getChosePattern() {
        if (patternPrefixCheckBox.isSelected()) {
            checkOnPrefixBox();
            return SearchDocumentsController.SearchPattern.PREFIX;
        }
        if (patternSuffixCheckBox.isSelected()) {
            checkOnSuffixBox();
            return SearchDocumentsController.SearchPattern.SUFFIX;
        }
        if (patternSubsequenceCheckBox.isSelected()) {
            checkOnSubsequenceBox();
            return SearchDocumentsController.SearchPattern.SUBSEQUENCE;
        }
        checkOnSubsequenceBox();
        return SearchDocumentsController.SearchPattern.SUBSEQUENCE;
    }

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        booksContainer.getChildren().clear();

        viewModel.getPaginatedFragmentaryDocuments()
                .forEach(fragmentaryDocument -> {
                    String authors = String.join(", ", fragmentaryDocument.getDocumentAuthors());
                    String documentTitle = fragmentaryDocument.getDocumentTitle();
                    String isbn = fragmentaryDocument.getISBN();
                    String coverImagePath = fragmentaryDocument.getDocumentCoverImageFilePath();
                    booksContainer.getChildren().add(createDocumentTileWithImage(authors, documentTitle, isbn, coverImagePath));
                });
    }

    private VBox createDocumentTileWithImage(String authors, String title, String isbn, String coverImagePath) {
        VBox documentTile = new VBox();
        documentTile.setMaxHeight(300);
        documentTile.setMaxWidth(210);
        documentTile.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");

        Label authorLabel = new Label(authors);
        authorLabel.setStyle("-fx-font-size: 14;");

        Label isbnLabel = new Label(isbn);
        isbnLabel.setStyle("-fx-font-size: 1;");
        isbnLabel.setVisible(false);

        try {
            System.out.println("found document's cover image path: " + coverImagePath);
            File imgFile = new File(coverImagePath);
            Image image = new Image(imgFile.toURI().toString(), 160, 240, true, true, true);
            ImageView coverImage = new ImageView(image);
            coverImage.setFitWidth(160);
            coverImage.setFitHeight(240);
            coverImage.setPreserveRatio(true);
            coverImage.setSmooth(true);
            coverImage.setCache(true);

            coverImage.setOnMouseEntered(event -> documentTile.setCursor(Cursor.HAND));
            coverImage.setOnMouseExited(event -> documentTile.setCursor(Cursor.DEFAULT));
            coverImage.setOnMouseClicked(event -> openDocumentViewOnClick(isbn));

            documentTile.getChildren().add(coverImage);
        } catch (Exception e) {
            Rectangle placeholder = new Rectangle(160, 240);
            documentTile.getChildren().add(placeholder);
        }

        documentTile.getChildren().addAll(titleLabel, authorLabel);
        return documentTile;
    }

    @SneakyThrows
    private void openDocumentViewOnClick(String isbn) {
        PatronDocumentView documentView = new PatronDocumentView(
                isbn,
                StageManager.getInstance().getDocumentRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/PatronDocumentView.fxml"));
        loader.setController(documentView);
        HBox hbox = loader.load();

        StackPane rootStackPane = StageManager.getInstance().getRootStackPane();
        rootStackPane.getChildren().getFirst().setMouseTransparent(true);
        rootStackPane.getChildren().add(hbox);
    }

    private void checkOnTitleBox() {
        criteriaTitleCheckBox.setSelected(true);
        criteriaGenreCheckBox.setSelected(false);
        criteriaAuthorCheckBox.setSelected(false);
    }

    private void checkOnGenreBox() {
        criteriaGenreCheckBox.setSelected(true);
        criteriaAuthorCheckBox.setSelected(false);
        criteriaTitleCheckBox.setSelected(false);
    }

    private void checkOnAuthorBox() {
        criteriaAuthorCheckBox.setSelected(true);
        criteriaTitleCheckBox.setSelected(false);
        criteriaGenreCheckBox.setSelected(false);
    }

    private void checkOnPrefixBox() {
        patternPrefixCheckBox.setSelected(true);
        patternSubsequenceCheckBox.setSelected(false);
        patternSuffixCheckBox.setSelected(false);
    }

    private void checkOnSuffixBox() {
        patternSuffixCheckBox.setSelected(true);
        patternSubsequenceCheckBox.setSelected(false);
        patternPrefixCheckBox.setSelected(false);
    }

    private void checkOnSubsequenceBox() {
        patternSubsequenceCheckBox.setSelected(true);
        patternPrefixCheckBox.setSelected(false);
        patternSuffixCheckBox.setSelected(false);
    }
}