package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.Administrator;

public interface AdministratorRepository extends UserRepository<Administrator> {
    @Override
    default @NonNull Class<Administrator> getUserClass() {
        return Administrator.class;
    }
}
