package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.domain.Administrator;

@NoArgsConstructor(staticName = "of")
public class InMemoryAdministratorRepository extends InMemoryUserRepository<Administrator> implements AdministratorRepository {
}
