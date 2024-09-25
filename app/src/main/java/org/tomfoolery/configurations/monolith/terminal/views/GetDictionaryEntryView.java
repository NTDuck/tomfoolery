package org.tomfoolery.configurations.monolith.terminal.views;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.GetDictionaryEntryUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.GetDictionaryEntryViewResponse;

@NoArgsConstructor(staticName = "of")
public class GetDictionaryEntryView extends View {
    public GetDictionaryEntryUserSubmission waitForUserSubmission() {
        this.display("Enter word: ");
        val headword = this.waitForUserInput();

        return GetDictionaryEntryUserSubmission.of(headword);
    }

    public void displayResponse(@NonNull GetDictionaryEntryViewResponse response) {
        val headword = response.getHeadword();
        val definitions = response.getDefinitions();

        val contentBuilder = new StringBuilder()
            .append("Found ").append(definitions.length).append(" definition(s) of \"")
            .append(headword).append("\":\n");

        for (val definition : definitions)
            contentBuilder.append(definition).append("\n");

        this.display(contentBuilder.toString());
    }

    public void onNotFoundException() {
        this.display("Word not found\n");
    }
}
