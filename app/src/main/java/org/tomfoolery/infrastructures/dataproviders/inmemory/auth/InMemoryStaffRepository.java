package org.tomfoolery.infrastructures.dataproviders.inmemory.auth;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.auth.StaffRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.infrastructures.dataproviders.inmemory.auth.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryStaffRepository extends InMemoryUserRepository<Staff> implements StaffRepository {
}
