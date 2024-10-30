package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.domain.Patron;

@NoArgsConstructor(staticName = "of")
public class InMemoryPatronRepository extends InMemoryUserRepository<Patron> implements PatronRepository {

}
