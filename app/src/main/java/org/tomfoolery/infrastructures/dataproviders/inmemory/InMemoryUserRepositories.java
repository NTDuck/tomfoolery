package org.tomfoolery.infrastructures.dataproviders.inmemory;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.dataproviders.UserRepository;

import java.util.Iterator;
import java.util.List;

public class InMemoryUserRepositories implements UserRepositories {
    private final @NonNull List<UserRepository<?>> userRepositories;

    private InMemoryUserRepositories(@NonNull AdministratorRepository administratorRepository, @NonNull StaffRepository staffRepository, @NonNull PatronRepository patronRepository) {
        this.userRepositories = List.of(administratorRepository, staffRepository, patronRepository);
    }

    public static @NonNull InMemoryUserRepositories of(@NonNull AdministratorRepository administratorRepository, @NonNull StaffRepository staffRepository, @NonNull PatronRepository patronRepository) {
        return new InMemoryUserRepositories(administratorRepository, staffRepository, patronRepository);
    }

    @Override
    public @NonNull Iterator<UserRepository<?>> iterator() {
        return userRepositories.iterator();
    }
}
