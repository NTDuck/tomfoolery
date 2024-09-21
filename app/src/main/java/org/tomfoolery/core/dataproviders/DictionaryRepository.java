package org.tomfoolery.core.dataproviders;

import lombok.NonNull;
import org.jmolecules.ddd.types.Repository;
import org.tomfoolery.core.domain.Dictionary;

import java.util.List;

public interface DictionaryRepository extends Repository<Dictionary, Dictionary.ID> {
    void save(@NonNull Dictionary dictionary);
    void delete(@NonNull Dictionary.ID dictionaryID);

    Dictionary select(@NonNull Dictionary.ID dictionaryID);
    List<Dictionary> show();

    boolean contains(@NonNull Dictionary.ID dictionaryID);
}
