package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.Administrator;

public interface AdministratorRepository extends UserRepository<Administrator> {
    @Override
    default @NonNull Class<Administrator> getUserClass() {
        return Administrator.class;
    }
}
