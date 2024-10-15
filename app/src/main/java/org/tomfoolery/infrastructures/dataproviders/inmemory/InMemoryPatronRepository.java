package org.tomfoolery.infrastructures.dataproviders.inmemory;

import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;

public class InMemoryPatronRepository extends InMemoryUserRepository<Patron> implements PatronRepository {
}
