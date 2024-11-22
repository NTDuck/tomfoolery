package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.SearchDocumentsController;
import java.util.Objects;


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
    private ComboBox<String> criteriaChooser;

    @FXML
    private ComboBox<String> patternChooser;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane booksContainer;

    @FXML
    public void initialize() {
        booksContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        Platform.runLater(this::loadBooks);
        criteriaChooser.getSelectionModel().select("Title");
        patternChooser.getSelectionModel().select("Prefix");
        searchField.setOnAction(event -> searchBooks());
    }

    private void searchBooks() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);
        } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException |
                 SearchDocumentsUseCase.AuthenticationTokenInvalidException |
                 SearchDocumentsUseCase.PaginationInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private SearchDocumentsController.@NonNull RequestObject collectRequestObject() {
        val searchCriterion = collectSearchCriterion();
        val searchPattern = collectSearchPattern();
        String searchText = searchField.getText();
        int pageIndex = 1;

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchPattern, searchText, pageIndex, 30);
    }

    private SearchDocumentsController.@NonNull SearchCriterion collectSearchCriterion() {
        String selectedCriteria = criteriaChooser.getValue();
        if (Objects.equals(selectedCriteria, "Title")) {
            return SearchDocumentsController.SearchCriterion.TITLE;
        }
        else return SearchDocumentsController.SearchCriterion.AUTHOR;
    }

    private SearchDocumentsController.@NonNull SearchPattern collectSearchPattern() {
        String selectedPattern = patternChooser.getValue();
        if (Objects.equals(selectedPattern, "Prefix")) {
            return SearchDocumentsController.SearchPattern.PREFIX;
        } else if(Objects.equals(selectedPattern, "Suffix")) {
            return SearchDocumentsController.SearchPattern.SUFFIX;
        } else return SearchDocumentsController.SearchPattern.SUBSEQUENCE;
    }

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        int pageIndex = viewModel.getPageIndex();
        int maxPageIndex = viewModel.getMaxPageIndex();
        booksContainer.getChildren().clear();

        viewModel.getPaginatedFragmentaryDocuments()
                .forEach(fragmentaryDocument -> {
                    String ISBN = fragmentaryDocument.getISBN();
                    String documentTitle = fragmentaryDocument.getDocumentTitle();
                    booksContainer.getChildren().add(createBookTileWithImage(ISBN, documentTitle));
                    System.out.println(ISBN + " " + documentTitle);
                });
    }

    private VBox createBookTileWithImage(String ISBN, String title) {
        VBox tile = new VBox();

        Label titleLabel = new Label(title);
        Label ISBNLabel = new Label(ISBN);

        tile.getChildren().addAll(titleLabel, ISBNLabel);

        try {
            Image image = new Image("/images/book-cover.jpg", 160, 240, true, true, true);
            ImageView coverImage = new ImageView(image);
            coverImage.setFitWidth(160);
            coverImage.setFitHeight(240);
            coverImage.setPreserveRatio(true);
            coverImage.setSmooth(true);
            coverImage.setCache(true);

            tile.getChildren().add(coverImage);
        } catch (Exception e) {
            Rectangle placeholder = new Rectangle(160, 240);
            tile.getChildren().add(placeholder);
        }

        return tile;
    }
}