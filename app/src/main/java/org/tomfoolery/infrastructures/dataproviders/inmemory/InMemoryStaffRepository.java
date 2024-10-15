package org.tomfoolery.infrastructures.dataproviders.inmemory;

import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.domain.Staff;

public class InMemoryStaffRepository extends InMemoryUserRepository<Staff> implements StaffRepository {
}
