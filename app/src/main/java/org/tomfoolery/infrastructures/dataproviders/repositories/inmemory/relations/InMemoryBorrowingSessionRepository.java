package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;
import org.tomfoolery.infrastructures.utils.helpers.comparators.UserComparator;

import java.util.Comparator;

@NoArgsConstructor(staticName = "of")
public class InMemoryBorrowingSessionRepository extends BaseInMemoryRepository<BorrowingSession, BorrowingSession.Id> implements BorrowingSessionRepository {
    @Override
    protected @NonNull Comparator<BorrowingSession.Id> getEntityIdComparator() {
        return Comparator.comparing(BorrowingSession.Id::getFirstEntityId, DocumentComparator.compareId())
            .thenComparing(BorrowingSession.Id::getSecondEntityId, UserComparator.compareId());
    }
}
