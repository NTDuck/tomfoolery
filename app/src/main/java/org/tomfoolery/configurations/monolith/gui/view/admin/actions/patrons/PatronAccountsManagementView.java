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
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.administrator.users.retrieval.ShowPatronAccountsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.retrieval.ShowPatronAccountsController;

import java.util.function.Consumer;

public class PatronAccountsManagementView {
    private final @NonNull ShowPatronAccountsController showController;

    public PatronAccountsManagementView(@NonNull PatronRepository patronRepository,
                                       @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                       @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.showController = ShowPatronAccountsController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @FXML
    private Label counterLabel;

    @FXML
    private TableView<PatronAccountViewModel> patronAccountsTable;

    @FXML
    private TableColumn<PatronAccountViewModel, Void> viewDetailsColumn;

    @FXML
    public void initialize() {
        addButtonToColumn(viewDetailsColumn, "View details", (PatronAccountViewModel patronAccount) -> {
            openShowPatronDetailsInfoView(patronAccount.getId());
        });

        loadAccounts();
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
        counterLabel.setText(String.valueOf(patronAccounts.size()) + " patrons");
        patronAccountsTable.getItems().clear();
        patronAccountsTable.setItems(patronAccounts);
    }

    private ObservableList<PatronAccountViewModel> getPatronAccounts() {
        ObservableList<PatronAccountViewModel> patronAccounts = FXCollections.observableArrayList();

        val requestObject = ShowPatronAccountsController.RequestObject.of(1, Integer.MAX_VALUE);

        try {
            val viewModel = showController.apply(requestObject);
            viewModel.getPatrons().forEach(patronAccount -> {
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
