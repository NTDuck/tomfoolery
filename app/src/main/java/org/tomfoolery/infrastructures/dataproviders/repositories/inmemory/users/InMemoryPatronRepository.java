package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryPatronRepository extends InMemoryUserRepository<Patron> implements PatronRepository {
}
