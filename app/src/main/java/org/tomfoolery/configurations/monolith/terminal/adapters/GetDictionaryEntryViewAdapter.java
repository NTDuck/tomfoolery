package org.tomfoolery.configurations.monolith.terminal.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.GetDictionaryEntryUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.GetDictionaryEntryViewResponse;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.utils.requests.GetDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.GetDictionaryEntryResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetDictionaryEntryViewAdapter {
    public static GetDictionaryEntryRequest adapt(@NonNull GetDictionaryEntryUserSubmission submission) {
        val headword = submission.getHeadword();
        val entryID = DictionaryEntry.ID.of(headword);
        return GetDictionaryEntryRequest.of(entryID);
    }

    public static GetDictionaryEntryViewResponse adapt(@NonNull GetDictionaryEntryResponse response) {
        val entry = response.getEntry();

        val headword = entry.getId().getHeadword();
        val meanings = entry.getMeanings();

        val definitions = meanings.stream()
            .map(DictionaryEntry.Meaning::getDefinition)
            .toArray(String[]::new);

        return GetDictionaryEntryViewResponse.of(headword, definitions);
    }
}
