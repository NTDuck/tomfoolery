package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.utils.id.CompactID;

import java.util.HashSet;

@Data(staticConstructor = "of")
public class Patron {
    private final @NonNull ID id = ID.of();

    private final @NonNull HashSet<Document.ID> borrowedDocumentIds = new HashSet<>();

    @Value(staticConstructor = "of")
    public static class ID implements CompactID {
        private static int size = 0;
        int value = ++size;
    }
}
