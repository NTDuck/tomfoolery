package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.abc.InMemoryLinearUserSearchGenerator;

@NoArgsConstructor(staticName = "of")
public class InMemoryLinearStaffSearchGenerator extends InMemoryLinearUserSearchGenerator<Staff> implements StaffSearchGenerator {
}
