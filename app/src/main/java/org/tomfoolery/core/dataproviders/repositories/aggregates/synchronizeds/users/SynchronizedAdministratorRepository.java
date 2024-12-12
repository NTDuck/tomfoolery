package org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.dataproviders.repositories.aggregates.synchronizeds.users.abc.SynchronizedUserRepository;

import java.util.List;

public class SynchronizedAdministratorRepository extends SynchronizedUserRepository<Administrator> implements AdministratorRepository {
    public static @NonNull SynchronizedAdministratorRepository of(@NonNull AdministratorRepository administratorRepository, @NonNull List<? extends BaseSynchronizedGenerator<Administrator, Administrator.Id>> synchronizedAdministratorGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedAdministratorRepository(administratorRepository, synchronizedAdministratorGenerators, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedAdministratorRepository(@NonNull AdministratorRepository administratorRepository, @NonNull List<? extends BaseSynchronizedGenerator<Administrator, Administrator.Id>> synchronizedAdministratorGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(administratorRepository, synchronizedAdministratorGenerators, borrowingSessionRepository, reviewRepository);
    }
}
