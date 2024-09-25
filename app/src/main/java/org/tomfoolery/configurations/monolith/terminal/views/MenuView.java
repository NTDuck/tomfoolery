package org.tomfoolery.configurations.monolith.terminal.views;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.MenuUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.MenuViewResponse;

@NoArgsConstructor(staticName = "of")
public class MenuView extends View {
    private static final String PROMPT = """
Welcome to My Application!
[0] Exit
[1] Add
[2] Remove
[3] Update
[4] Display
[5] Lookup
[6] Search
[7] Game
[8] Import from file
[9] Export to file
Your action:\s""";

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
