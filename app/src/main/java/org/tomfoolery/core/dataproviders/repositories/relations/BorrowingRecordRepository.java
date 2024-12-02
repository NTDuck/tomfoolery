package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingRecord;
import org.tomfoolery.core.domain.users.Patron;

public interface BorrowingRecordRepository extends BaseBiRepository<BorrowingRecord, BorrowingRecord.Id, Document.Id, Patron.Id> {
    default @Unsigned int getNumberOfCurrentlyBorrowedByPatron(Patron.@NonNull Id patronId) {
        return (int) this.show().parallelStream()
            .filter(borrowingRecord -> borrowingRecord.getId().getSecondEntityId().equals(patronId))
            .filter(BorrowingRecord::isCurrentlyBorrowed)
            .count();
    }

    default boolean containsCurrentlyBorrowedRecord(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        return this.show().parallelStream()
            .anyMatch(borrowingRecord -> {
                return borrowingRecord.getId().getFirstEntityId().equals(documentId)
                    && borrowingRecord.getId().getSecondEntityId().equals(patronId)
                    && borrowingRecord.isCurrentlyBorrowed();
            });
    }
}
