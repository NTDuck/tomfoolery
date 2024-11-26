package org.tomfoolery.core.dataproviders.repositories.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.Patron;

public interface PatronRepository extends UserRepository<Patron> {
    @Override
    default @NonNull Class<Patron> getUserClass() {
        return Patron.class;
    }
}
