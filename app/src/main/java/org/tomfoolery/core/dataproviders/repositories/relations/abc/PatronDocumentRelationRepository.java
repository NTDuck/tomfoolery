package org.tomfoolery.core.dataproviders.repositories.relations.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.abc.PatronDocumentRelation;
import org.tomfoolery.core.domain.users.Patron;

public interface PatronDocumentRelationRepository extends BaseRepository<PatronDocumentRelation, PatronDocumentRelation.Id> {
    void synchronizeDeletedDocument(Document.@NonNull Id documentId);
    void synchronizeDeletedPatron(Patron.@NonNull Id patronId);
}
