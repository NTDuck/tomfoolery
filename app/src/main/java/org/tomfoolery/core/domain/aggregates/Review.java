package org.tomfoolery.core.domain.aggregates;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.documents.Document;

@Value(staticConstructor = "of")
public class Review {
    @NonNull Id id;

    @Unsigned double rating;

    @Value(staticConstructor = "of")
    public static class Id {
        Patron.@NonNull Id patronId;
        Document.@NonNull Id documentId;
    }
}
