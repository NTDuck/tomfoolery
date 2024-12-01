package org.tomfoolery.core.domain.relations;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

@Data(staticConstructor = "of")
public final class Review implements ddd.Relation<Document.Id, Patron.Id> {
    private final Document.@NonNull Id firstId;
    private final Patron.@NonNull Id secondId;

    private @Unsigned double rating;
}
