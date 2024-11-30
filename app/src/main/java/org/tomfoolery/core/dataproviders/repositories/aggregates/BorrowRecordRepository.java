package org.tomfoolery.core.dataproviders.repositories.aggregates;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.aggregates.BorrowRecord;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.documents.Document;

public interface BorrowRecordRepository extends BaseRepository<BorrowRecord, BorrowRecord.Id> {
    void synchronizeDeletedPatron(Patron.@NonNull Id patronId);
    void synchronizeDeletedDocument(Document.@NonNull Id documentId);


}
