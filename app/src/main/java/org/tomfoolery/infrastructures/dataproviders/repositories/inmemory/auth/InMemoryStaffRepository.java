package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryStaffRepository extends InMemoryUserRepository<Staff> implements StaffRepository {
}
