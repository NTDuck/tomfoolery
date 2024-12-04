package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.gui.StageManager;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.administrator.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence.DeleteStaffAccountController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class StaffAccountsManagementView {
    private final @NonNull DeleteStaffAccountController deleteController;

    public StaffAccountsManagementView(@NonNull StaffRepository staffRepository,
                                       @NonNull AuthenticationTokenGenerator authenticationTokenGenerator,
                                       @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.deleteController = DeleteStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

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
        staffAccountsTable.setItems(getStaffAccounts());
    }

    public ObservableList<StaffAccountViewModel> getStaffAccounts() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        ObservableList<StaffAccountViewModel> result = FXCollections.observableArrayList();

        StaffRepository staffRepository = StageManager.getInstance().getStaffRepository();
        List<Staff> staffsList = staffRepository.show();
        for (Staff staff : staffsList) {
            String id = staff.getId().getUuid().toString();
            String username = staff.getCredentials().getUsername();
            String createdAdminId = staff.getAudit().getCreatedByAdminId().getUuid().toString();
            String lastModifiedAdminId = "";
            if (staff.getAudit().getLastModifiedByAdminId() != null) {
                lastModifiedAdminId = staff.getAudit().getLastModifiedByAdminId().getUuid().toString();
            }
            String createdAt = formatter.format(staff.getAudit().getTimestamps().getCreated());
            String lastModifiedAt = "";
            if (staff.getAudit().getTimestamps().getLastModified() != null) {
                lastModifiedAt = formatter.format(staff.getAudit().getTimestamps().getLastModified());
            }

            result.add(StaffAccountViewModel.of(id, username, createdAdminId, lastModifiedAdminId, createdAt, lastModifiedAt));
        }

        return result;
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
                StageManager.getInstance().getStaffRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository(),
                StageManager.getInstance().getPasswordEncoder()
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
                StageManager.getInstance().getStaffRepository(),
                StageManager.getInstance().getAuthenticationTokenGenerator(),
                StageManager.getInstance().getAuthenticationTokenRepository(),
                StageManager.getInstance().getPasswordEncoder()
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
        String createdAdminID;
        String lastModifiedAdminID;
        String createdAt;
        String lastModifiedAt;
    }
}
