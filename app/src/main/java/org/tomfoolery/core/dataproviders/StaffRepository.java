package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.Staff;

public interface StaffRepository extends UserRepository<Staff> {
    @Override
    default @NonNull Class<Staff> getUserClass() {
        return Staff.class;
    }
}
