package org.tomfoolery.infrastructures.adapters;

import org.tomfoolery.core.domain.DictionaryEntry;

public class DictionaryEntryAdapter {
    public static DictionaryEntry toDictionaryEntry(String userInput) {
        String payload = userInput.substring(5);
        int firstSpaceIndex = payload.indexOf(" ");

        String headword = payload.substring(0, firstSpaceIndex);
        String definition = payload.substring(firstSpaceIndex + 1);

        DictionaryEntry dictionaryEntry = new DictionaryEntry();
        dictionaryEntry.headword = headword;
        dictionaryEntry.definitions.add(definition);

        return dictionaryEntry;
    }

    public static String getHeadword(String userInput) {
        return userInput.substring(5);
    }
}
