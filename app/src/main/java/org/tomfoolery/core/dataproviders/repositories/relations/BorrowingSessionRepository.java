package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.util.List;
import java.util.stream.Collectors;

public interface BorrowingSessionRepository extends BaseBiRepository<BorrowingSession, BorrowingSession.Id, Document.Id, Patron.Id> {
    default @NonNull List<BorrowingSession> show(BaseUser.@NonNull Id borrowingPatronId) {
        return this.show().parallelStream()
            .filter(borrowingSession -> borrowingSession.getId().getSecondEntityId().equals(borrowingPatronId))
            .collect(Collectors.toUnmodifiableList());
    }

    default @Unsigned int count(Patron.@NonNull Id borrowingPatronId) {
        return (int) this.show(borrowingPatronId).parallelStream().count();
    }
}
