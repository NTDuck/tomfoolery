package org.tomfoolery.core.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.tomfoolery.core.dataproviders.DictionaryEntryRepository;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Dictionary implements AggregateRoot<Dictionary, Dictionary.ID> {
    @NonNull private final ID id;
    @NonNull private final DictionaryEntryRepository entryRepository;

    public String getName() {
        return this.id.getName();
    }

    @Value(staticConstructor = "of")
    public static class ID implements Identifier {
        @NonNull String name;
    }
}
