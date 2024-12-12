package org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.users.abc.SynchronizedUserRepository;

import java.util.List;

public class SynchronizedPatronRepository extends SynchronizedUserRepository<Patron> implements PatronRepository {
    public static @NonNull SynchronizedPatronRepository of(@NonNull PatronRepository patronRepository, @NonNull List<? extends BaseSynchronizedGenerator<Patron, Patron.Id>> synchronizedPatronGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedPatronRepository(patronRepository, synchronizedPatronGenerators, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedPatronRepository(@NonNull PatronRepository patronRepository, @NonNull List<? extends BaseSynchronizedGenerator<Patron, Patron.Id>> synchronizedPatronGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(patronRepository, synchronizedPatronGenerators, borrowingSessionRepository, reviewRepository);
    }
}
