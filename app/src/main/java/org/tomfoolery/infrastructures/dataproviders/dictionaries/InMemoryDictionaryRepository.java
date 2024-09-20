package org.tomfoolery.infrastructures.dataproviders.dictionaries;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.tomfoolery.core.dataproviders.DictionaryRepository;
import org.tomfoolery.core.domain.Dictionary;

import java.util.HashMap;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class InMemoryDictionaryRepository implements DictionaryRepository {
    private final HashMap<Dictionary.ID, Dictionary> dictionaries = new HashMap<>();

    @Override
    public void save(@NonNull Dictionary dictionary) {
        this.dictionaries.put(dictionary.getId(), dictionary);
    }

    @Override
    public void delete(@NonNull Dictionary.ID dictionaryID) {
        this.dictionaries.remove(dictionaryID);
    }

    @Override
    public Dictionary select(@NonNull Dictionary.ID dictionaryID) {
        return this.dictionaries.get(dictionaryID);
    }

    @Override
    public List<Dictionary> show() {
        return List.copyOf(this.dictionaries.values());
    }

    @Override
    public boolean contains(@NonNull Dictionary.ID dictionaryID) {
        return this.dictionaries.containsKey(dictionaryID);
    }
}
