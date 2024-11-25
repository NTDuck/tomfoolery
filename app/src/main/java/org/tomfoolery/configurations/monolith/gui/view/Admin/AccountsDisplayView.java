package org.tomfoolery.configurations.monolith.gui.view.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class AccountsDisplayView {
    @FXML
    private TableView<Account> accountsTable;

    @FXML
    public void initialize() {
        loadAccounts();
    }

    public void loadAccounts() {
        ObservableList<Account> accountLists = getAccounts();
        accountsTable.setItems(accountLists);
    }

    public ObservableList<Account> getAccounts() {
        return FXCollections.observableArrayList(
                new Account("John", "Smith", "john.smith@email.com", "jsmith", "pass123", "123 Oak Street, Boston, MA 02108"),
                new Account("Emma", "Johnson", "emma.j@email.com", "emmaj", "secure456", "45 Maple Ave, Seattle, WA 98101"),
                new Account("Michael", "Davis", "michael.d@email.com", "mdavis", "davis789", "789 Pine Road, Austin, TX 78701"),
                new Account("Sarah", "Wilson", "sarah.w@email.com", "swilson", "wilson321", "567 Elm Court, Chicago, IL 60601"),
                new Account("James", "Brown", "james.b@email.com", "jbrown", "brown567", "890 Cedar Lane, San Francisco, CA 94101"),
                new Account("Lisa", "Anderson", "lisa.a@email.com", "landerson", "lisa789", "234 Birch Street, Denver, CO 80201"),
                new Account("Robert", "Taylor", "rob.t@email.com", "rtaylor", "taylor456", "678 Willow Drive, Miami, FL 33101"),
                new Account("Maria", "Garcia", "maria.g@email.com", "mgarcia", "garcia123", "345 Palm Avenue, Phoenix, AZ 85001"),
                new Account("David", "Martinez", "david.m@email.com", "dmartinez", "david890", "901 Rose Boulevard, Portland, OR 97201"),
                new Account("Jennifer", "Lee", "jen.lee@email.com", "jlee", "lee567", "432 Sunset Road, Atlanta, GA 30301"),
                new Account("John", "Smith", "john.smith@email.com", "jsmith", "pass123", "123 Oak Street, Boston, MA 02108"),
                new Account("Emma", "Johnson", "emma.j@email.com", "emmaj", "secure456", "45 Maple Ave, Seattle, WA 98101"),
                new Account("Michael", "Davis", "michael.d@email.com", "mdavis", "davis789", "789 Pine Road, Austin, TX 78701"),
                new Account("Sarah", "Wilson", "sarah.w@email.com", "swilson", "wilson321", "567 Elm Court, Chicago, IL 60601"),
                new Account("James", "Brown", "james.b@email.com", "jbrown", "brown567", "890 Cedar Lane, San Francisco, CA 94101"),
                new Account("Lisa", "Anderson", "lisa.a@email.com", "landerson", "lisa789", "234 Birch Street, Denver, CO 80201"),
                new Account("Robert", "Taylor", "rob.t@email.com", "rtaylor", "taylor456", "678 Willow Drive, Miami, FL 33101"),
                new Account("Maria", "Garcia", "maria.g@email.com", "mgarcia", "garcia123", "345 Palm Avenue, Phoenix, AZ 85001"),
                new Account("David", "Martinez", "david.m@email.com", "dmartinez", "david890", "901 Rose Boulevard, Portland, OR 97201"),
                new Account("Jennifer", "Lee", "jen.lee@email.com", "jlee", "lee567", "432 Sunset Road, Atlanta, GA 30301"),
                new Account("John", "Smith", "john.smith@email.com", "jsmith", "pass123", "123 Oak Street, Boston, MA 02108"),
                new Account("Emma", "Johnson", "emma.j@email.com", "emmaj", "secure456", "45 Maple Ave, Seattle, WA 98101"),
                new Account("Michael", "Davis", "michael.d@email.com", "mdavis", "davis789", "789 Pine Road, Austin, TX 78701"),
                new Account("Sarah", "Wilson", "sarah.w@email.com", "swilson", "wilson321", "567 Elm Court, Chicago, IL 60601"),
                new Account("James", "Brown", "james.b@email.com", "jbrown", "brown567", "890 Cedar Lane, San Francisco, CA 94101"),
                new Account("Lisa", "Anderson", "lisa.a@email.com", "landerson", "lisa789", "234 Birch Street, Denver, CO 80201"),
                new Account("Robert", "Taylor", "rob.t@email.com", "rtaylor", "taylor456", "678 Willow Drive, Miami, FL 33101"),
                new Account("Maria", "Garcia", "maria.g@email.com", "mgarcia", "garcia123", "345 Palm Avenue, Phoenix, AZ 85001"),
                new Account("David", "Martinez", "david.m@email.com", "dmartinez", "david890", "901 Rose Boulevard, Portland, OR 97201"),
                new Account("Jennifer", "Lee", "jen.lee@email.com", "jlee", "lee567", "432 Sunset Road, Atlanta, GA 30301"),
                new Account("John", "Smith", "john.smith@email.com", "jsmith", "pass123", "123 Oak Street, Boston, MA 02108"),
                new Account("Emma", "Johnson", "emma.j@email.com", "emmaj", "secure456", "45 Maple Ave, Seattle, WA 98101"),
                new Account("Michael", "Davis", "michael.d@email.com", "mdavis", "davis789", "789 Pine Road, Austin, TX 78701"),
                new Account("Sarah", "Wilson", "sarah.w@email.com", "swilson", "wilson321", "567 Elm Court, Chicago, IL 60601"),
                new Account("James", "Brown", "james.b@email.com", "jbrown", "brown567", "890 Cedar Lane, San Francisco, CA 94101"),
                new Account("Lisa", "Anderson", "lisa.a@email.com", "landerson", "lisa789", "234 Birch Street, Denver, CO 80201"),
                new Account("Robert", "Taylor", "rob.t@email.com", "rtaylor", "taylor456", "678 Willow Drive, Miami, FL 33101"),
                new Account("Maria", "Garcia", "maria.g@email.com", "mgarcia", "garcia123", "345 Palm Avenue, Phoenix, AZ 85001"),
                new Account("David", "Martinez", "david.m@email.com", "dmartinez", "david890", "901 Rose Boulevard, Portland, OR 97201"),
                new Account("Jennifer", "Lee", "jen.lee@email.com", "jlee", "lee567", "432 Sunset Road, Atlanta, GA 30301")
        );
    }

    @AllArgsConstructor
    @Getter @Setter
    public static class Account {
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private String address;
    }
}
