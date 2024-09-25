package org.tomfoolery.configurations.monolith.terminal.views;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.AddDictionaryEntryUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.AddDictionaryEntryViewResponse;

@NoArgsConstructor(staticName = "of")
public class AddDictionaryEntryView extends View {
    public AddDictionaryEntryUserSubmission waitForUserSubmission() {
        this.display("Enter word: ");
        val headword = this.waitForUserInput();

        this.display("Enter definitions (separate with \"|\"): ");
        val definitions = this.waitForUserInput();

        return AddDictionaryEntryUserSubmission.of(headword, definitions);
    }

    public void displayResponse(@NonNull AddDictionaryEntryViewResponse response) {
        val headword = response.getHeadword();

        this.display("Added word \"" + headword + "\"\n");
    }

    public void onAlreadyExistsException() {
        this.display("Word already exists\n");
    }
}
