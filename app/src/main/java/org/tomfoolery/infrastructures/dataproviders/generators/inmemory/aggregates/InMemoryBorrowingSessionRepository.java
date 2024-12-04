package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.aggregates;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryBorrowingSessionRepository extends BaseInMemoryRepository<BorrowingSession, BorrowingSession.Id> implements BorrowingSessionRepository {
}
