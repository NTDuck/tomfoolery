package org.tomfoolery.core.domain.relations;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.domain.relations.abc.PatronDocumentRelation;

@Data(staticConstructor = "of")
public final class Review implements PatronDocumentRelation {
    private final @NonNull Id id;

    private @Unsigned double rating;
}
