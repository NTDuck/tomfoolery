package org.tomfoolery.core.dataproviders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.Patron;

public interface PatronRepository extends UserRepository<Patron> {
    @Override
    default @NonNull Class<Patron> getUserClass() {
        return Patron.class;
    }
}
