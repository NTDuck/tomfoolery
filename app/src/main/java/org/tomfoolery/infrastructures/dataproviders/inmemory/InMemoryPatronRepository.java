package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NonNull;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.ReadonlyUser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryPatronRepository implements PatronRepository {
    private final HashMap<ReadonlyUser.Id, Patron> patrons = new HashMap<>();

    @Override
    public void save(@NonNull Patron patron) {
        patrons.put(patron.getId(), patron);
    }

    @Override
    public void delete(@NonNull ReadonlyUser.Id entityId) {
        patrons.remove(entityId);
    }

    @Override
    public @Nullable Patron getById(ReadonlyUser.Id entityId) {
        return patrons.get(entityId);
    }

    @Override
    public @NonNull Collection<Patron> show() {
        return new ArrayList<>(patrons.values());
    }

    @Override
    public @org.checkerframework.checker.nullness.qual.Nullable Patron getByUsername(@org.checkerframework.checker.nullness.qual.NonNull String username) {
        return null;
    }

    @Override
    public @NonNull Class<Patron> getUserClass() {
        return Patron.class;
    }
}
