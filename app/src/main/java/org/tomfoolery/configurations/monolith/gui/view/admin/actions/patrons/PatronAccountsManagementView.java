package org.tomfoolery.configurations.monolith.gui.view.admin.actions.patrons;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.configurations.monolith.gui.view.admin.actions.staffs.StaffAccountsManagementView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.external.administrator.users.retrieval.ShowPatronAccountsUseCase;
import org.tomfoolery.core.usecases.external.administrator.users.search.SearchPatronsByUsernameUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.retrieval.ShowPatronAccountsController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search.SearchPatronsByUsernameController;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.search.SearchStaffByUsernameController;
import org.tomfoolery.infrastructures.adapters.controllers.internal.statistics.GetStatisticsController;

import java.util.function.Consumer;

public class PatronAccountsManagementView {
    private final @NonNull GetStatisticsController getStatisticsController = GetStatisticsController.of(
            StageManager.getInstance().getResources().getDocumentRepository(),
            StageManager.getInstance().getResources().getAdministratorRepository(),
            StageManager.getInstance().getResources().getPatronRepository(),
            StageManager.getInstance().getResources().getStaffRepository()
    );

    private final @NonNull ShowPatronAccountsController showController;

    public PatronAccountsManagementView(@NonNull PatronRepository patronRepository,
                                       @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                       @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showController = ShowPatronAccountsController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private Label counterLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PatronAccountViewModel> patronAccountsTable;

    @FXML
    private TableColumn<PatronAccountViewModel, Void> viewDetailsColumn;

    @FXML
    public void initialize() {
        addButtonToColumn(viewDetailsColumn, "View details", (PatronAccountViewModel patronAccount) -> {
            openShowPatronDetailsInfoView(patronAccount.getId());
        });

        searchField.setOnAction(event -> searchPatrons());

        loadAccounts();
    }

    private void searchPatrons() {
        SearchPatronsByUsernameController controller = SearchPatronsByUsernameController.of(
                StageManager.getInstance().getResources().getPatronSearchGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );

        if (searchField.getText().isEmpty()) {
            loadAccounts();
            return;
        }

        try {
            val requestObject = SearchPatronsByUsernameController.RequestObject.of(searchField.getText(), 1, Integer.MAX_VALUE);
            val viewModel = controller.apply(requestObject);

            val accounts = viewModel.getPaginatedPatrons();

            ObservableList<PatronAccountViewModel> accountList = FXCollections.observableArrayList();

            accounts.forEach(account -> {
                String uuid = account.getPatronUuid();
                String username = account.getPatronUsername();
                String created = account.getCreationTimestamp();
                String lastLogin = account.getLastLoginTimestamp();
                String lastLogout = account.getLastLogoutTimestamp();

                PatronAccountViewModel accountViewModel = PatronAccountViewModel.of(uuid, username, created, lastLogin, lastLogout);
                accountList.add(accountViewModel);
            });

            patronAccountsTable.getItems().clear();
            patronAccountsTable.setItems(accountList);
            counterLabel.setText(this.getStatisticsController.get().getNumberOfPatrons() + " patrons");
        } catch (SearchPatronsByUsernameUseCase.AuthenticationTokenNotFoundException |
                 SearchPatronsByUsernameUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (SearchPatronsByUsernameUseCase.PaginationInvalidException _) {
        }
    }

    private void addButtonToColumn(@NonNull TableColumn<PatronAccountViewModel, Void> column, String buttonText, Consumer<PatronAccountViewModel> action) {
        column.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    PatronAccountViewModel patronAccount = getTableView().getItems().get(getIndex());
                    action.accept(patronAccount);
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

    @SneakyThrows
    private void openShowPatronDetailsInfoView(String id) {
        ShowPatronDetailsInfo controller = new ShowPatronDetailsInfo(
                id,
                StageManager.getInstance().getResources().getPatronRepository(),
                StageManager.getInstance().getResources().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getResources().getAuthenticationTokenRepository()
        );
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Admin/ShowPatronDetailsInfo.fxml"));
        loader.setController(controller);

        HBox h = loader.load();
        StageManager.getInstance().getRootStackPane().getChildren().add(h);
    }

    public void loadAccounts() {
        val patronAccounts = getPatronAccounts();
        patronAccountsTable.getItems().clear();
        patronAccountsTable.setItems(patronAccounts);
        counterLabel.setText(this.getStatisticsController.get().getNumberOfPatrons() + " patrons");
    }

    private ObservableList<PatronAccountViewModel> getPatronAccounts() {
        ObservableList<PatronAccountViewModel> patronAccounts = FXCollections.observableArrayList();

        val requestObject = ShowPatronAccountsController.RequestObject.of(1, Integer.MAX_VALUE);

        try {
            val viewModel = showController.apply(requestObject);
            viewModel.getPaginatedPatrons().forEach(patronAccount -> {
                String id = patronAccount.getPatronUuid();
                String username = patronAccount.getPatronUsername();
                String created = patronAccount.getCreationTimestamp();
                String lastLogin = patronAccount.getLastLoginTimestamp();
                String lastLogout = patronAccount.getLastLogoutTimestamp();

                patronAccounts.add(new PatronAccountViewModel(id, username, created, lastLogin, lastLogout));
            });
        } catch (ShowPatronAccountsUseCase.AuthenticationTokenNotFoundException |
                 ShowPatronAccountsUseCase.AuthenticationTokenInvalidException e) {
            StageManager.getInstance().openLoginMenu();
        } catch (ShowPatronAccountsUseCase.PaginationInvalidException _) {
        }

        return patronAccounts;
    }

    @Value(staticConstructor = "of")
    public static class PatronAccountViewModel {
        String id;
        String username;
        String created;
        String lastLogin;
        String lastLogout;
    }
}
