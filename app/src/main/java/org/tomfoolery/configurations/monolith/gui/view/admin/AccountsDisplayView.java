package org.tomfoolery.configurations.monolith.gui.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.Value;

public class AccountsDisplayView {
    @FXML
    private TableView<StaffAccount> staffAccountsTable;

    @FXML
    public void initialize() {
        loadAccounts();
    }

    public void loadAccounts() {
        ObservableList<StaffAccount> accountLists = getStaffAccounts();
        staffAccountsTable.setItems(accountLists);
    }

    public ObservableList<StaffAccount> getStaffAccounts() {
        return FXCollections.observableArrayList(
                StaffAccount.of("1", "hieupham123", "1", "1", "today", "now"),
                StaffAccount.of("2", "hieu2", "1", "1", "today", "now"),
                StaffAccount.of("3", "duy2", "1", "1", "today", "now"),
                StaffAccount.of("4", "duck1", "1", "1", "today", "now"),
                StaffAccount.of("5", "duck69", "1", "1", "today", "now"),
                StaffAccount.of("6", "duck3", "1", "1", "today", "now"),
                StaffAccount.of("7", "hieunguyen", "1", "1", "today", "now"),
                StaffAccount.of("8", "adnope", "1", "1", "today", "now"),
                StaffAccount.of("9", "duckduckgo", "1", "1", "today", "now")
        );
    }

    @Value(staticConstructor = "of")
    public static class StaffAccount {
        String id;
        String username;
        String createdAdminID;
        String lastModifiedAdminID;
        String createdAt;
        String lastModifiedAt;
    }
}
