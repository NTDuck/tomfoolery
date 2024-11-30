package org.tomfoolery.core.domain.relations.abc;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.contracts.ddd.ddd;

public interface PatronDocumentRelation extends ddd.Entity<PatronDocumentRelation.Id> {
    @Override
    PatronDocumentRelation.@NonNull Id getId();

    @Value(staticConstructor = "of")
    class Id implements ddd.EntityId {
        Patron.@NonNull Id patronId;
        Document.@NonNull Id documentId;
    }
}
