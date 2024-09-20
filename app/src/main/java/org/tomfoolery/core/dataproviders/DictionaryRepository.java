package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.jmolecules.ddd.annotation.Repository;
import org.tomfoolery.core.domain.Dictionary;

import java.util.List;

@Repository
public interface DictionaryRepository {
    void save(@NonNull Dictionary dictionary);
    void delete(@NonNull Dictionary.ID dictionaryID);

    Dictionary select(@NonNull Dictionary.ID dictionaryID);
    List<Dictionary> show();

    boolean contains(@NonNull Dictionary.ID dictionaryID);
}
