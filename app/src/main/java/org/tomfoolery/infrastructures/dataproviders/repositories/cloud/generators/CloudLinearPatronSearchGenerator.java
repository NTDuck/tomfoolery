package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.generators;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users.CloudPatronRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class CloudLinearPatronSearchGenerator implements PatronSearchGenerator {
    private final @NonNull CloudPatronRepository patronRepository;

    @Override
    public @NonNull List<Patron> searchByNormalizedUsername(@NonNull String normalizedUsername) {
        return patronRepository.show().stream()
                .filter(patron -> patron.getCredentials().getUsername().equalsIgnoreCase(normalizedUsername))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void synchronizeSavedEntity(@NonNull Patron savedEntity) {
        patronRepository.save(savedEntity);
    }

    @Override
    public void synchronizeDeletedEntity(@NonNull Patron deletedEntity) {
        patronRepository.delete(deletedEntity.getId());
    }
}

