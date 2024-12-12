package org.tomfoolery.core.dataproviders.repositories.relations;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRelationRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;
import java.util.stream.Collectors;

public interface BorrowingSessionRepository extends BaseBiRelationRepository<BorrowingSession, BorrowingSession.Id, Document.Id, Patron.Id> {
    default @NonNull List<BorrowingSession> showByPatronId(Patron.@NonNull Id borrowingPatronId) {
        return this.show().parallelStream()
            .filter(borrowingSession -> borrowingSession.getId().getSecondEntityId().equals(borrowingPatronId))
            .collect(Collectors.toUnmodifiableList());
    }

    default @Nullable Page<BorrowingSession> showPageByPatronId(Patron.@NonNull Id borrowingPatronId, @Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedBorrowingSessions = this.showByPatronId(borrowingPatronId);

        return Page.fromUnpaginated(unpaginatedBorrowingSessions, pageIndex, maxPageSize);
    }

    default @Unsigned int count(Patron.@NonNull Id borrowingPatronId) {
        return (int) this.showByPatronId(borrowingPatronId).parallelStream().count();
    }
}
