package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingRecord;
import org.tomfoolery.core.domain.users.Patron;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface BorrowingRecordRepository extends BaseBiRepository<BorrowingRecord, BorrowingRecord.Id, Document.Id, Patron.Id> {
    default @Unsigned int getNumberOfCurrentlyBorrowedDocumentsByPatron(Patron.@NonNull Id patronId) {
        return (int) this.show().parallelStream()
            .filter(borrowingRecord -> borrowingRecord.getId().getSecondEntityId().equals(patronId))
            .filter(BorrowingRecord::isCurrentlyBorrowed)
            .count();
    }

    default boolean containsCurrentlyBorrowedRecord(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        return this.show().parallelStream()
            .anyMatch(isCurrentlyBorrowed(documentId, patronId));
    }

    default @Nullable BorrowingRecord getMostRecentlyAndCurrentlyBorrowedRecord(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        return this.show().parallelStream()
            .filter(isCurrentlyBorrowed(documentId, patronId))
            .max(Comparator.comparing(borrowingRecord -> borrowingRecord.getId().getBorrowedTimestamp()))
            .orElse(null);
    }

    default @NonNull List<BorrowingRecord> showCurrentlyBorrowedRecordsByPatron(Patron.@NonNull Id patronId) {
        return this.show().parallelStream()
            .filter(borrowingRecord -> borrowingRecord.getId().getSecondEntityId().equals(patronId))
            .filter(BorrowingRecord::isCurrentlyBorrowed)
            .collect(Collectors.toUnmodifiableList());
    }

    private static Predicate<BorrowingRecord> isCurrentlyBorrowed(Document.@NonNull Id documentId, Patron.@NonNull Id patronId) {
        return borrowingRecord -> borrowingRecord.getId().getFirstEntityId().equals(documentId)
            && borrowingRecord.getId().getSecondEntityId().equals(patronId)
            && borrowingRecord.isCurrentlyBorrowed();
    }
}
