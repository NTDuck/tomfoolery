package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.auth.AdministratorRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryAdministratorRepository extends InMemoryUserRepository<Administrator> implements AdministratorRepository {
}
