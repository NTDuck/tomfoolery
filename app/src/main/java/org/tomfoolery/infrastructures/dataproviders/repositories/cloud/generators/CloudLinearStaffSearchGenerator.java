package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudStaffRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudLinearStaffSearchGenerator implements StaffSearchGenerator {
    private final @NonNull CloudStaffRepository staffRepository;

    @Override
    public @NonNull List<Staff> searchByNormalizedUsername(@NonNull String normalizedUsername) {
        return staffRepository.show().stream()
                .filter(staff -> staff.getCredentials().getUsername().equalsIgnoreCase(normalizedUsername))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Staff savedEntity) {
        staffRepository.save(savedEntity);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Staff deletedEntity) {
        staffRepository.delete(deletedEntity.getId());
    }
}

