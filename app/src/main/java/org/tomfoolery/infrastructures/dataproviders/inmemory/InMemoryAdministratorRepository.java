package org.tomfoolery.infrastructures.dataproviders.inmemory;

import org.tomfoolery.core.dataproviders.AdministratorRepository;
import org.tomfoolery.core.domain.Administrator;

public class InMemoryAdministratorRepository extends InMemoryUserRepository<Administrator> implements AdministratorRepository {
}
