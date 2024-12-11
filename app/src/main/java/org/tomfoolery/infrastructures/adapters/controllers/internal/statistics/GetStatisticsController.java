package org.tomfoolery.infrastructures.adapters.controllers.internal.statistics;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.usecases.internal.statistics.GetStatisticsUseCase;

import java.util.function.Supplier;

public final class GetStatisticsController implements Supplier<GetStatisticsController.ViewModel> {
    private final @NonNull GetStatisticsUseCase getStatisticsUseCase;

    public static @NonNull GetStatisticsController of(@NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        return new GetStatisticsController(documentRepository, administratorRepository, patronRepository, staffRepository);
    }

    private GetStatisticsController(@NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository) {
        this.getStatisticsUseCase = GetStatisticsUseCase.of(documentRepository, administratorRepository, patronRepository, staffRepository);
    }

    @Override
    public @NonNull ViewModel get() {
        val responseModel = this.getStatisticsUseCase.get();
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static @NonNull ViewModel mapResponseModelToViewModel(GetStatisticsUseCase.@NonNull Response responseModel) {
        return ViewModel.builder()
            .numberOfDocuments(responseModel.getNumberOfDocuments())
            .numberOfAdministrators(responseModel.getNumberOfAdministrators())
            .numberOfPatrons(responseModel.getNumberOfPatrons())
            .numberOfStaff(responseModel.getNumberOfStaff())

            .build();
    }

    @Value
    @Builder(access = AccessLevel.PRIVATE)
    public static class ViewModel {
        @Unsigned int numberOfDocuments;
        @Unsigned int numberOfAdministrators;
        @Unsigned int numberOfPatrons;
        @Unsigned int numberOfStaff;
    }
}
