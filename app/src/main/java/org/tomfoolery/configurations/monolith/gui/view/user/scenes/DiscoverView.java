package org.tomfoolery.configurations.monolith.gui.view.user.scenes;

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
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents.PatronSingleDocumentView;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.common.documents.retrieval.GetDocumentByIdUseCase;
import org.tomfoolery.core.usecases.common.documents.search.abc.SearchDocumentsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.search.SearchDocumentsController;

public class DiscoverView {
    private final @NonNull SearchDocumentsController searchController;
    private final @NonNull GetDocumentByIdController getByIdController;

    private @NonNull ImageView getCoverImageFromPath(@NonNull String path) {
//        Image image = new Image("file:" + path);
        Image image = new Image("/images/default/placeholder-book-cover.png");
        ImageView coverImage = new ImageView(image);
        coverImage.setFitHeight(240);
        coverImage.setFitWidth(160);
        coverImage.setPreserveRatio(true);
        coverImage.setSmooth(true);
        coverImage.setCache(true);

        return coverImage;
    }

    public DiscoverView(
            @NonNull DocumentRepository documentRepository,
            @NonNull DocumentSearchGenerator documentSearchGenerator,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository) {

        this.searchController = SearchDocumentsController.of(
                documentSearchGenerator,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );

        this.getByIdController = GetDocumentByIdController.of(
                documentRepository,
                authenticationTokenGenerator,
                authenticationTokenRepository
        );
    }

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> criterionChooserBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane booksContainer;

    @FXML
    public void initialize() {
        searchField.setOnAction(event -> searchBooks());
        criterionChooserBox.setValue("Title");

        searchBooks();
    }

    private void searchBooks() {
        if (criterionChooserBox.getValue().equals("ISBN")) {
            val requestObject = this.collectGetByIdRequestObject();

            try {
                val viewModel = this.getByIdController.apply(requestObject);
                this.onGetByIdSuccess(viewModel);

            } catch (GetDocumentByIdUseCase.AuthenticationTokenNotFoundException | GetDocumentByIdUseCase.AuthenticationTokenInvalidException exception) {
                StageManager.getInstance().openLoginMenu();
            } catch (GetDocumentByIdUseCase.DocumentNotFoundException |
                     GetDocumentByIdUseCase.DocumentISBNInvalidException e) {
                booksContainer.getChildren().clear();
            }
        }
        else {
            try {
                val requestObject = this.collectSearchRequestObject();
                val viewModel = this.searchController.apply(requestObject);
                this.onSearchSuccess(viewModel);
            } catch (SearchDocumentsUseCase.AuthenticationTokenNotFoundException |
                     SearchDocumentsUseCase.AuthenticationTokenInvalidException e) {
                StageManager.getInstance().openLoginMenu();
            } catch (SearchDocumentsUseCase.PaginationInvalidException e) {
                booksContainer.getChildren().clear();
            }
        }
    }

    private void onGetByIdSuccess(GetDocumentByIdController.@NonNull ViewModel viewModel) {
        val documentCoverImageFilePath = viewModel.getDocumentCoverImageFilePath();

        booksContainer.getChildren().clear();

        String authors = String.join(", ", viewModel.getDocumentAuthors());
        String documentTitle = viewModel.getDocumentTitle();
        String isbn = viewModel.getDocumentISBN_13();
        booksContainer.getChildren().add(createDocumentTileWithImage(authors, documentTitle, isbn, getCoverImageFromPath(documentCoverImageFilePath)));
    }

    private GetDocumentByIdController.@NonNull RequestObject collectGetByIdRequestObject() {
        System.out.print(searchField.getText());
        return GetDocumentByIdController.RequestObject.of(searchField.getText());
    }

    private SearchDocumentsController.@NonNull RequestObject collectSearchRequestObject() {
        val searchCriterion = getChoseCriterion();
        String searchText = searchField.getText();

        return SearchDocumentsController.RequestObject.of(searchCriterion, searchText, 1, 1000);
    }

    private SearchDocumentsController.@NonNull SearchCriterion getChoseCriterion() {
        String criterion = criterionChooserBox.getValue();
        if (criterion.equals("Title")) {
            return SearchDocumentsController.SearchCriterion.TITLE;
        } else if (criterion.equals("Author")) {
            return SearchDocumentsController.SearchCriterion.AUTHOR;
        } else if (criterion.equals("Genre")) {
            return SearchDocumentsController.SearchCriterion.GENRE;
        }
        else return SearchDocumentsController.SearchCriterion.TITLE;
    }

    private void onSearchSuccess(SearchDocumentsController.@NonNull ViewModel viewModel) {
        booksContainer.getChildren().clear();
        viewModel.getPaginatedDocuments().forEach(document ->
        {
            String authors = String.join(", ", document.getDocumentAuthors());
            String documentTitle = document.getDocumentTitle();
            String isbn = document.getDocumentISBN_13();
            String coverImagePath = document.getDocumentCoverImageFilePath();

            ImageView coverImage = getCoverImageFromPath(coverImagePath);
            booksContainer.getChildren().add(createDocumentTileWithImage(authors, documentTitle, isbn, coverImage));
        });
    }

    private @NonNull VBox createDocumentTileWithImage(String authors, String title, String isbn, ImageView coverImage) {
        VBox documentTile = new VBox();
        documentTile.setMaxHeight(300);
        documentTile.setMaxWidth(210);
        documentTile.setMinHeight(300);
        documentTile.setMinWidth(210);
        documentTile.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label authorLabel = new Label(authors);
        authorLabel.setStyle("-fx-font-size: 12;");

        coverImage.setOnMouseEntered(event -> documentTile.setCursor(Cursor.HAND));
        coverImage.setOnMouseExited(event -> documentTile.setCursor(Cursor.DEFAULT));
        coverImage.setOnMouseClicked(event -> openDocumentViewOnClick(isbn));

        documentTile.getChildren().add(coverImage);

        documentTile.getChildren().addAll(titleLabel, authorLabel);
        return documentTile;
    }

    @SneakyThrows
    private void openDocumentViewOnClick(String isbn) {
        PatronSingleDocumentView documentView = new PatronSingleDocumentView(
                isbn,
                StageManager.getInstance().getResources().getDocumentRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getBorrowingSessionRepository()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Patron/PatronSingleDocumentView.fxml"));
        loader.setController(documentView);
        HBox hbox = loader.load();

        StackPane rootStackPane = StageManager.getInstance().getRootStackPane();
        rootStackPane.getChildren().add(hbox);
    }

    private static class DocumentCoverImageNotOpenableException extends Throwable {
    }
}