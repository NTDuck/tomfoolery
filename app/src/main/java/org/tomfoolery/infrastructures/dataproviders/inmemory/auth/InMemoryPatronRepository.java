package org.tomfoolery.infrastructures.dataproviders.inmemory.auth;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.infrastructures.dataproviders.inmemory.auth.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryPatronRepository extends InMemoryUserRepository<Patron> implements PatronRepository {
}
