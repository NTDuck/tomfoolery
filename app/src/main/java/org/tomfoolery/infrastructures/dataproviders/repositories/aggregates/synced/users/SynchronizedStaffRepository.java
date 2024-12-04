package org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.aggregates.synced.users.abc.SynchronizedUserRepository;

import java.util.List;

public class SynchronizedStaffRepository extends SynchronizedUserRepository<Staff> implements StaffRepository {
    public static @NonNull SynchronizedStaffRepository of(@NonNull StaffRepository staffRepository, @NonNull List<? extends BaseSynchronizedGenerator<Staff, Staff.Id>> synchronizedStaffGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        return new SynchronizedStaffRepository(staffRepository, synchronizedStaffGenerators, borrowingSessionRepository, reviewRepository);
    }

    protected SynchronizedStaffRepository(@NonNull StaffRepository staffRepository, @NonNull List<? extends BaseSynchronizedGenerator<Staff, Staff.Id>> synchronizedStaffGenerators, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull ReviewRepository reviewRepository) {
        super(staffRepository, synchronizedStaffGenerators, borrowingSessionRepository, reviewRepository);
    }
}
