package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.abc.InMemoryLinearUserSearchGenerator;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearAdministratorSearchGenerator extends InMemoryLinearUserSearchGenerator<Administrator> implements AdministratorSearchGenerator {
}
