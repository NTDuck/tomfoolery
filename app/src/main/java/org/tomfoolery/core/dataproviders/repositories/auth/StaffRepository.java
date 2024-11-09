package org.tomfoolery.core.dataproviders.repositories.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.Staff;

public interface StaffRepository extends UserRepository<Staff> {
    @Override
    default @NonNull Class<Staff> getUserClass() {
        return Staff.class;
    }
}
