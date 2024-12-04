package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryAdministratorRepository extends InMemoryUserRepository<Administrator> implements AdministratorRepository {
}
