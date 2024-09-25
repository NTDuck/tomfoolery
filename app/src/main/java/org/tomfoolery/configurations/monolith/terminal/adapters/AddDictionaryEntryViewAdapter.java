package org.tomfoolery.configurations.monolith.terminal.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.requests.AddDictionaryEntryUserSubmission;
import org.tomfoolery.configurations.monolith.terminal.utils.responses.AddDictionaryEntryViewResponse;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.utils.requests.AddDictionaryEntryRequest;
import org.tomfoolery.core.utils.responses.AddDictionaryEntryResponse;

import java.util.Arrays;
import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddDictionaryEntryViewAdapter {
    public static AddDictionaryEntryRequest adapt(@NonNull AddDictionaryEntryUserSubmission submission) {
        val headword = submission.getHeadword();
        val definitionsConcatenated = submission.getDefinitions();

        val entryID = DictionaryEntry.ID.of(headword);

        val definitions = definitionsConcatenated.split("\\|");
        val meanings = Arrays.stream(definitions)
            .map(String::trim)
            .map(definition -> DictionaryEntry.Meaning.of(definition, "", new HashMap<>()))
            .toList();

        val entry = DictionaryEntry.of(entryID, meanings);
        return AddDictionaryEntryRequest.of(entry);
    }

    public static AddDictionaryEntryViewResponse adapt(@NonNull AddDictionaryEntryResponse response) {
        val entryID = response.getEntryID();
        val headword = entryID.getHeadword();
        return AddDictionaryEntryViewResponse.of(headword);
    }
}
