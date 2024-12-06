package org.tomfoolery.configurations.monolith.gui.view.patron.actions.documents;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.core.usecases.patron.documents.review.persistence.RemoveDocumentReviewUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.review.persistence.AddDocumentReviewController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.review.persistence.RemoveDocumentReviewController;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.hybrid.documents.HybridDocumentRepository;

import java.util.ArrayList;
import java.util.List;

public class RateDocumentView {
    private final @NonNull Image EMPTY_STAR = new Image("/images/star.png");
    private final @NonNull Image FULL_STAR = new Image("/images/star_full.png");
    private final List<ImageView> stars = new ArrayList<>();

    private final @NonNull String documentISBN;
    private final @NonNull AddDocumentReviewController controller;
    private final @NonNull RemoveDocumentReviewController removeDocumentRatingController;

    private int currentRating;

    public RateDocumentView(@NonNull String isbn,
                            @NonNull HybridDocumentRepository documentRepository,
                            @NonNull ReviewRepository reviewRepository,
                            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                            @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = AddDocumentReviewController.of(documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.removeDocumentRatingController = RemoveDocumentReviewController.of(documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.documentISBN = isbn;
    }

    @FXML
    private ImageView star1;

    @FXML
    private ImageView star2;

    @FXML
    private ImageView star3;

    @FXML
    private ImageView star4;

    @FXML
    private ImageView star5;

    @FXML
    private Button removeRatingButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        this.stars.add(star1);
        this.stars.add(star2);
        this.stars.add(star3);
        this.stars.add(star4);
        this.stars.add(star5);
        this.resetStars();

        removeRatingButton.setOnAction(event -> removeDocumentRating());
        cancelButton.setOnAction(event -> closeView());
        confirmButton.setOnAction(event -> rateDocument());

        this.setUpStarsUpdate();
    }

    private void rateDocument() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.closeView();
        } catch (AddDocumentReviewUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("user doesn't exist?");
        } catch (AddDocumentReviewUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("user isn't invalid?");
        } catch (AddDocumentReviewUseCase.ReviewAlreadyExistsException e) {
            this.onPatronRatingAlreadyExistsException();
        } catch (AddDocumentReviewUseCase.DocumentISBNInvalidException |
                 AddDocumentReviewUseCase.DocumentNotFoundException |
                 AddDocumentReviewUseCase.RatingInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private void onPatronRatingAlreadyExistsException() {
        errorMessage.setText("You have already rated this document");
        errorMessage.setVisible(true);
    }

    private AddDocumentReviewController.@NonNull RequestObject collectRequestObject() {
        double rating = this.collectRating();
        return AddDocumentReviewController.RequestObject.of(documentISBN, rating);
    }

    private double collectRating() {
        return (double) currentRating;
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    private void removeDocumentRating() {
        val requestObject = RemoveDocumentReviewController.RequestObject.of(documentISBN);

        try {
            this.removeDocumentRatingController.accept(requestObject);
            closeView();
        } catch (RemoveDocumentReviewUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("user doesn't exist?");
        } catch (RemoveDocumentReviewUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("user isn't invalid?");
        } catch (RemoveDocumentReviewUseCase.DocumentNotFoundException |
                 RemoveDocumentReviewUseCase.DocumentISBNInvalidException e) {
            throw new RuntimeException(e);
        } catch (RemoveDocumentReviewUseCase.ReviewNotFoundException exception) {
            errorMessage.setText("You haven't rated this document");
            errorMessage.setVisible(true);
        }
    }

    private void setUpStarsUpdate() {
        for (int i = 0; i < stars.size(); i++) {
            final int index = i;
            ImageView star = stars.get(i);

            star.setOnMouseEntered(event -> updateStarsOnHover(index));
            star.setOnMouseExited(event -> updateStarsOnHoverExit());
            star.setOnMouseClicked(event -> setRating(index + 1));
        }
    }

    private void resetStars() {
        for (ImageView star : stars) {
            star.setImage(EMPTY_STAR);
        }
    }

    private void updateStarsOnHover(int hoveredIndex) {
        for (int i = 0; i <= hoveredIndex; i++) {
            stars.get(i).setImage(FULL_STAR);
        }

        for (int i = hoveredIndex + 1; i < stars.size(); i++) {
            stars.get(i).setImage(EMPTY_STAR);
        }
    }

    private void updateStarsOnHoverExit() {
        for (int i = 0; i < stars.size(); i++) {
            if (i < currentRating) {
                stars.get(i).setImage(FULL_STAR);
            } else {
                stars.get(i).setImage(EMPTY_STAR);
            }
        }
    }

    private void setRating(int newRating) {
        currentRating = newRating;
        for (int i = 0; i < stars.size(); i++) {
            if (i < currentRating) {
                stars.get(i).setImage(FULL_STAR);
            } else {
                stars.get(i).setImage(EMPTY_STAR);
            }
        }
    }
}
