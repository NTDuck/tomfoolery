package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudAdministratorRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudLinearAdministratorSearchGenerator implements AdministratorSearchGenerator {
    private final @NonNull CloudAdministratorRepository administratorRepository;

    @Override
    public @NonNull List<Administrator> searchByNormalizedUsername(@NonNull String normalizedUsername) {
        return administratorRepository.show().stream()
                .filter(admin -> admin.getCredentials().getUsername().equalsIgnoreCase(normalizedUsername))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Administrator savedEntity) {
        administratorRepository.save(savedEntity);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Administrator deletedEntity) {
        administratorRepository.delete(deletedEntity.getId());
    }
}
