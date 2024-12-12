package org.tomfoolery.configurations.monolith.gui.view.admin.actions.staffs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowStaffAccountsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.persistence.DeleteStaffAccountController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.ShowStaffAccountsController;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

import java.util.function.Consumer;

public class StaffAccountsManagementView {
    private final @NonNull GetStatisticsController getStatisticsController = GetStatisticsController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getAdministratorRepository(),
            StageManager.getInstance().getResources().getPatronRepository(),
            StageManager.getInstance().getResources().getStaffRepository()
    );

    private final @NonNull DeleteStaffAccountController deleteController;
    private final @NonNull ShowStaffAccountsController showController;

    public StaffAccountsManagementView(@NonNull StaffRepository staffRepository,
                                       @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                       @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.deleteController = DeleteStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
        this.showController = ShowStaffAccountsController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private Label counterLabel;

    @FXML
    private Button createStaffAccountButton;

    @FXML
    private TableView<StaffAccountViewModel> staffAccountsTable;

    @FXML
    private TableColumn<StaffAccountViewModel, Void> updateColumn;

    @FXML
    private TableColumn<StaffAccountViewModel, Void> deleteColumn;

    @FXML
    public void initialize() {
        addButtonToColumn(updateColumn, "Update", (StaffAccountViewModel staffAccount) -> {
            openUpdateStaffAccountView(staffAccount.getId());
        });

        addButtonToColumn(deleteColumn, "Delete", (StaffAccountViewModel staffAccount) -> {
            deleteStaffAccount(staffAccount.getId());
        });

        createStaffAccountButton.setOnAction(event -> openCreateStaffAccountView());

        loadAccounts();
    }

    private void addButtonToColumn(@NonNull TableColumn<StaffAccountViewModel, Void> column, String buttonText, Consumer<StaffAccountViewModel> action) {
        column.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    StaffAccountViewModel staffAccount = getTableView().getItems().get(getIndex());
                    action.accept(staffAccount);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
    }

    public void loadAccounts() {
        val requestObject = ShowStaffAccountsController.RequestObject.of(1, Integer.MAX_VALUE);
        try {
            val viewModel = this.showController.apply(requestObject);
            this.onShowSuccess(viewModel);
        } catch (ShowStaffAccountsUseCase.AuthenticationTokenNotFoundException |
                 ShowStaffAccountsUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowStaffAccountsUseCase.PaginationInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    private void onShowSuccess(ShowStaffAccountsController.@NonNull ViewModel viewModel) {
        val accounts = viewModel.getPaginatedStaff();

        ObservableList<StaffAccountViewModel> accountList = FXCollections.observableArrayList();

        accounts.forEach(account -> {
            String uuid = account.getStaffUuid();
            String username = account.getStaffUsername();
            String created = account.getCreationTimestamp();
            String lastLogin = account.getLastLoginTimestamp();
            String lastLogout = account.getLastLogoutTimestamp();

            StaffAccountViewModel accountViewModel = StaffAccountViewModel.of(uuid, username, created, lastLogin, lastLogout);
            accountList.add(accountViewModel);
        });

        counterLabel.setText(this.getStatisticsController.get().getNumberOfStaff() + " staffs");
        staffAccountsTable.getItems().clear();
        staffAccountsTable.setItems(accountList);
    }

    private void deleteStaffAccount(String staffId) {
        val requestObject = DeleteStaffAccountController.RequestObject.of(staffId);

        try {
            this.deleteController.accept(requestObject);
            loadAccounts();
        } catch (DeleteStaffAccountUseCase.AuthenticationTokenNotFoundException exception) {
            System.err.println("You don't even exist?!");
        } catch (DeleteStaffAccountUseCase.AuthenticationTokenInvalidException exception) {
            System.err.println("You are not an admin!");
        } catch (DeleteStaffAccountUseCase.StaffNotFoundException exception) {
            System.err.println("This never happens btw");
        } catch (UserIdBiAdapter.UserUuidInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void openUpdateStaffAccountView(String staffId) {
        UpdateStaffAccountView controller = new UpdateStaffAccountView(
                this,
                staffId,
                StageManager.getInstance().getResources().getStaffRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getPasswordEncoder()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/UpdateStaffAccountView.fxml"));
        loader.setController(controller);
        VBox root = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(root);
    }

    @SneakyThrows
    private void openCreateStaffAccountView() {
        CreateStaffAccountView controller = new CreateStaffAccountView(
                this,
                StageManager.getInstance().getResources().getStaffRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository(),
                StageManager.getInstance().getResources().getPasswordEncoder()
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/CreateStaffAccountView.fxml"));
        loader.setController(controller);
        VBox root = loader.load();

        StageManager.getInstance().getRootStackPane().getChildren().add(root);
    }

    @Value(staticConstructor = "of")
    public static class StaffAccountViewModel {
        String id;
        String username;
        String created;
        String lastLogin;
        String lastLogout;
    }
}
