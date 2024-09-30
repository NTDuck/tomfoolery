package org.tomfoolery.core.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;

import java.util.HashSet;
import java.util.UUID;

@Data(staticConstructor = "of")
public class Patron {
    private final @NonNull ID id = ID.of();

    private final @NonNull HashSet<Document.ID> borrowedDocumentIds = new HashSet<>();

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull UUID value = UUID.randomUUID();
    }
}
