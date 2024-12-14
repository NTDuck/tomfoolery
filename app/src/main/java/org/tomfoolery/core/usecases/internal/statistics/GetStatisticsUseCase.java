package org.tomfoolery.core.usecases.internal.statistics;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;

import java.util.function.Supplier;

@RequiredArgsConstructor(staticName = "of")
public final class GetStatisticsUseCase implements Supplier<GetStatisticsUseCase.Response> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull AdministratorRepository administratorRepository;
    private final @NonNull PatronRepository patronRepository;
    private final @NonNull StaffRepository staffRepository;

    @Override
    public @NonNull Response get() {
        val numberOfDocuments = this.documentRepository.size();
        val numberOfAdministrators = this.administratorRepository.size();
        val numberOfPatrons = this.patronRepository.size();
        val numberOfStaff = this.staffRepository.size();

        return Response.of(numberOfDocuments, numberOfAdministrators, numberOfPatrons, numberOfStaff);
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @Unsigned int numberOfDocuments;
        @Unsigned int numberOfAdministrators;
        @Unsigned int numberOfPatrons;
        @Unsigned int numberOfStaff;
    }
}
