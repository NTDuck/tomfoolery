package org.tomfoolery.configurations.monolith.gui.view.patron;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.rating.AddDocumentRatingUseCase;
import org.tomfoolery.core.usecases.patron.documents.rating.RemoveDocumentRatingUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating.AddDocumentRatingController;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating.RemoveDocumentRatingController;

import java.util.ArrayList;
import java.util.List;

public class RateDocumentView {
    private final @NonNull Image EMPTY_STAR = new Image("/images/star.png");
    private final @NonNull Image FULL_STAR = new Image("/images/star_full.png");
    private final List<ImageView> stars = new ArrayList<>();

    private final @NonNull String documentISBN;
    private final @NonNull AddDocumentRatingController controller;
    private final @NonNull RemoveDocumentRatingController removeDocumentRatingController;

    private int currentRating;

    public RateDocumentView(@NonNull String isbn,
                            @NonNull DocumentRepository documentRepository,
                            @NonNull PatronRepository patronRepository,
                            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                            @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = AddDocumentRatingController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.removeDocumentRatingController = RemoveDocumentRatingController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
        } catch (AddDocumentRatingUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("user doesn't exist?");
        } catch (AddDocumentRatingUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("user isn't invalid?");
        } catch (AddDocumentRatingUseCase.PatronNotFoundException |
                 AddDocumentRatingUseCase.RatingValueInvalidException |
                 AddDocumentRatingUseCase.DocumentNotFoundException exception) {
            System.err.println("This cannot happen...");
        } catch (AddDocumentRatingUseCase.PatronRatingAlreadyExistsException exception) {
            this.onPatronRatingAlreadyExistsException();
        }
    }

    private void onPatronRatingAlreadyExistsException() {
        errorMessage.setText("You have already rated this document");
        errorMessage.setVisible(true);
    }

    private AddDocumentRatingController.@NonNull RequestObject collectRequestObject() {
        double rating = this.collectRating();
        return AddDocumentRatingController.RequestObject.of(documentISBN, rating);
    }

    private double collectRating() {
        return (double) currentRating;
    }

    private void closeView() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }

    private void removeDocumentRating() {
        val requestObject = RemoveDocumentRatingController.RequestObject.of(documentISBN);

        try {
            this.removeDocumentRatingController.accept(requestObject);
            closeView();
        } catch (RemoveDocumentRatingUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("user doesn't exist?");
        } catch (RemoveDocumentRatingUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("user isn't invalid?");
        } catch (RemoveDocumentRatingUseCase.PatronNotFoundException |
                 RemoveDocumentRatingUseCase.DocumentNotFoundException exception) {
        } catch (RemoveDocumentRatingUseCase.PatronRatingNotFoundException exception) {
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
