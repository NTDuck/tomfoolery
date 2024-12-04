package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users;

import lombok.NoArgsConstructor;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.abc.InMemoryUserRepository;

@NoArgsConstructor(staticName = "of")
public class InMemoryStaffRepository extends InMemoryUserRepository<Staff> implements StaffRepository {
}
