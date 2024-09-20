//package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;
//
//import org.tomfoolery.core.domain.ReadonlyDictionaryEntry;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class DictionaryEntryPresenter extends Presenter {
//    private static final String DELIMITER = " | ";
//
//    protected DictionaryEntryPresenter(String selectionLabel, String... parameterLabels) {
//        super(selectionLabel, parameterLabels);
//    }
//
//    protected static ReadonlyDictionaryEntry generateDictionaryEntry(String headword, String[] definitions) {
//        ReadonlyDictionaryEntry dictionaryEntry = new ReadonlyDictionaryEntry();
//
//        dictionaryEntry.headword = headword;
//        dictionaryEntry.meanings = new ArrayList<>();
//
//        for (String definition : definitions) {
//            ReadonlyDictionaryEntry.Meaning meaning = new ReadonlyDictionaryEntry.Meaning();
//            meaning.definition = definition.trim();
//            dictionaryEntry.meanings.add(meaning);
//        }
//
//        return dictionaryEntry;
//    }
//
//    protected static String generateStringFromDictionaryEntry(ReadonlyDictionaryEntry dictionaryEntry) {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        stringBuilder
//            .append(dictionaryEntry.headword)
//            .append(": ");
//
//        for (ReadonlyDictionaryEntry.Meaning meaning : dictionaryEntry.meanings)
//            stringBuilder.append(meaning.definition).append(DELIMITER);
//
//        // Remove last delimiter
//        if (!dictionaryEntry.meanings.isEmpty())
//            stringBuilder.delete(stringBuilder.length() - DELIMITER.length(), stringBuilder.length());
//
//        return stringBuilder.toString();
//    }
//
//    protected static String generateStringFromDictionaryEntries(List<ReadonlyDictionaryEntry> dictionaryEntries) {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        for (ReadonlyDictionaryEntry dictionaryEntry : dictionaryEntries)
//            stringBuilder.append(generateStringFromDictionaryEntry(dictionaryEntry));
//
//        return stringBuilder.toString();
//    }
//}
