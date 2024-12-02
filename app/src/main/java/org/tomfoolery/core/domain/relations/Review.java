package org.tomfoolery.core.domain.relations;

import lombok.Data;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.contracts.ddd;

@Data(staticConstructor = "of")
public final class Review implements ddd.BiRelation<Review.Id, Document.Id, Patron.Id> {
    private final @NonNull Id id;

    private @Unsigned double rating;

    @Value(staticConstructor = "of")
    public static class Id implements ddd.BiRelationId<Document.Id, Patron.Id> {
        Document.@NonNull Id firstEntityId;
        Patron.@NonNull Id secondEntityId;
    }
}
