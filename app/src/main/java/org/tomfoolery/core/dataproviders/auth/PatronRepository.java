package org.tomfoolery.core.dataproviders.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.Patron;

public interface PatronRepository extends UserRepository<Patron> {
    @Override
    default @NonNull Class<Patron> getUserClass() {
        return Patron.class;
    }
}
