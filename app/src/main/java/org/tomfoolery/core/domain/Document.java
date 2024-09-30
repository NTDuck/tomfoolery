package org.tomfoolery.core.domain;

import lombok.*;

import java.util.HashSet;
import java.util.List;

@Data(staticConstructor = "of")
public class Document {
    @Setter(value = AccessLevel.NONE)
    private final @NonNull ID id;

    private @NonNull String title;
    private @NonNull String description;
    private @NonNull List<String> authors = List.of();

    private final @NonNull HashSet<Patron.ID> patronIds = new HashSet<>();

    @Value(staticConstructor = "of")
    public static class ID {
        @NonNull String value;
    }
}
