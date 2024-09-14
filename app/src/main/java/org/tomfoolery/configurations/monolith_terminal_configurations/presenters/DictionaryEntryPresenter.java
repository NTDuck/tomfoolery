package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.core.domain.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class DictionaryEntryPresenter extends Presenter {
    private static final String DELIMITER = " | ";

    protected DictionaryEntryPresenter(String selectionLabel, String... parameterLabels) {
        super(selectionLabel, parameterLabels);
    }

    protected static DictionaryEntry generateDictionaryEntry(String headword, String[] definitions) {
        DictionaryEntry dictionaryEntry = new DictionaryEntry();

        dictionaryEntry.headword = headword;
        dictionaryEntry.meanings = new ArrayList<>();

        for (String definition : definitions) {
            DictionaryEntry.Meaning meaning = new DictionaryEntry.Meaning();
            meaning.definition = definition.trim();
            dictionaryEntry.meanings.add(meaning);
        }

        return dictionaryEntry;
    }

    protected static String generateStringFromDictionaryEntry(DictionaryEntry dictionaryEntry) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
            .append(dictionaryEntry.headword)
            .append(": ");

        for (DictionaryEntry.Meaning meaning : dictionaryEntry.meanings)
            stringBuilder.append(meaning.definition).append(DELIMITER);

        // Remove last delimiter
        if (!dictionaryEntry.meanings.isEmpty())
            stringBuilder.delete(stringBuilder.length() - DELIMITER.length(), stringBuilder.length());

        return stringBuilder.toString();
    }

    protected static String generateStringFromDictionaryEntries(List<DictionaryEntry> dictionaryEntries) {
        StringBuilder stringBuilder = new StringBuilder();

        for (DictionaryEntry dictionaryEntry : dictionaryEntries)
            stringBuilder.append(generateStringFromDictionaryEntry(dictionaryEntry));

        return stringBuilder.toString();
    }
}
