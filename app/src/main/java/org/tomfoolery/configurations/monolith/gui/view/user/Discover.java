package org.tomfoolery.configurations.monolith.gui.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.extern.java.Log;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.user.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.user.documents.SearchDocumentsController;

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
//        Platform.runLater(this::loadBooks);
        searchField.setOnAction(event -> searchBooks());
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

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchPattern, searchText, 1, Integer.MAX_VALUE);
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

    private void onSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
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
            Image image = new Image("/images/jayce97.png", 160, 240, true, true, true);
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