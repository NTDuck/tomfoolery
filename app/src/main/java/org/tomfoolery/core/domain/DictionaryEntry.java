package org.tomfoolery.core.domain;

import java.util.List;

public class DictionaryEntry {
    public String headword;
    public List<String> definitions;

    public DictionaryEntry(String headword, List<String> definitions) {
        this.headword = headword;
        this.definitions = definitions;
    }
}
