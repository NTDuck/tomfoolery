package org.tomfoolery.infrastructures.dataproviders.inmemory;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.domain.Staff;

@NoArgsConstructor(staticName = "of")
public class InMemoryStaffRepository extends InMemoryUserRepository<Staff> implements StaffRepository {
}
