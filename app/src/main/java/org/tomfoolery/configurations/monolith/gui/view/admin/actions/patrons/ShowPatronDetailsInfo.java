package org.tomfoolery.configurations.monolith.gui.view.admin.actions.patrons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.GetPatronByIdUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.GetPatronByIdController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public class ShowPatronDetailsInfo {
    private final @NonNull GetPatronByIdController controller;
    private final @NonNull String patronId;

    public ShowPatronDetailsInfo(
            @NonNull String id,
            @NonNull PatronRepository patronRepository,
            @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
            @NonNull AuthenticationTokenRepository authenticationTokenRepository
            ) {
        this.patronId = id;
        this.controller = GetPatronByIdController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private TextField id;

    @FXML
    private TextField username;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField city;

    @FXML
    private TextField country;

    @FXML
    private TextField createdAt;

    @FXML
    private TextField lastLogin;

    @FXML
    private TextField lastLogout;

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> close());
        loadInfo();
    }

    private void loadInfo() {
        val requestObject = GetPatronByIdController.RequestObject.of(patronId);
        try {
            val viewModel = controller.apply(requestObject);

            id.setText("Id: " + viewModel.getPatronUuid());
            username.setText("Username: " + viewModel.getPatronUsername());
            firstName.setText("First Name: " + viewModel.getPatronFirstName());
            lastName.setText("Last Name: " + viewModel.getPatronLastName());
            email.setText("Email: " + viewModel.getPatronEmail());
            phoneNumber.setText("Phone Number: " + viewModel.getPatronPhoneNumber());
            city.setText("City: " + viewModel.getPatronCity());
            country.setText("Country: " + viewModel.getPatronCountry());
            createdAt.setText("Created at: " + viewModel.getCreationTimestamp());
            lastLogin.setText("Last login: " + viewModel.getLastLoginTimestamp());
            lastLogout.setText("Last logout: " + viewModel.getLastLogoutTimestamp());
        } catch (UserIdBiAdapter.UserUuidInvalidException e) {
            System.err.println("Invalid user id");
        } catch (GetPatronByIdUseCase.AuthenticationTokenNotFoundException |
                 GetPatronByIdUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (GetPatronByIdUseCase.UserNotFoundException e) {
            System.err.println("Patron not found");
        }
    }

    private void close() {
        StageManager.getInstance().getRootStackPane().getChildren().removeLast();
    }
}
