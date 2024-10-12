package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NonNull;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.ReadonlyUser;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryPatronRepository implements PatronRepository {
    private final HashMap<ReadonlyUser.Id, Patron> patrons = new HashMap<>();

    @Override
    public @Nullable Patron getByCredentials(ReadonlyUser.Credentials credentials) {
        return patrons.values().stream()
                .filter(patron -> patron.getCredentials().equals(credentials))
                .findFirst()
                .orElse(null);
    }

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
    public @NonNull Class<Patron> getUserClass() {
        return Patron.class;
    }
}
