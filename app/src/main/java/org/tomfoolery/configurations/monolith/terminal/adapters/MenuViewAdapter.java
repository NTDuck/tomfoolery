package org.tomfoolery.configurations.monolith.terminal.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.exceptions.ValidationException;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.MenuUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.MenuViewResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuViewAdapter {
    public static MenuViewResponse adapt(@NonNull MenuUserSubmission submission) throws ValidationException {
        val selectionAsString = submission.getSelection();

        try {
            val selectionAsInt = Integer.parseInt(selectionAsString);
            return MenuViewResponse.of(selectionAsInt);
        } catch (NumberFormatException exception) {
            throw new ValidationException();
        }
    }
}
