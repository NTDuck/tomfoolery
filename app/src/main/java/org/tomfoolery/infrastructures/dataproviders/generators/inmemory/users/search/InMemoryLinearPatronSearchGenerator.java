package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.abc.InMemoryLinearUserSearchGenerator;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearPatronSearchGenerator extends InMemoryLinearUserSearchGenerator<Patron> implements PatronSearchGenerator {
}
