package org.tomfoolery.configurations.monolith.terminal.views;

import org.tomfoolery.configurations.monolith.terminal.utils.requests.MenuUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.MenuViewResponse;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

@NoArgsConstructor(staticName = "of")
public class MenuView extends View {
    private static final String PROMPT = """
Welcome to Tomfoolery!
[0] Exit
[1] Add Document
[2] Remove Document
[3] Update Document
[4] Find Document
[5] Display Document
[6] Add User
[7] Borrow Document
[8] Return Document
[9] Display User Info
""";

    public MenuUserSubmission waitForUserSubmission() {
        this.display(PROMPT);

        val selection = this.waitForUserInput();
        return MenuUserSubmission.of(selection);
    }

    public void displayResponse(@NonNull MenuViewResponse response) {
        val selection = response.getSelection();
        this.display("You chose selection: " + selection);
    }

    public void onValidationException() {
        this.display("Error: Action must be a valid number\n");
    }
}
