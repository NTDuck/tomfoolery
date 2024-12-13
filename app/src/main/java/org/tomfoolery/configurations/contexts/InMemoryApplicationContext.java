package org.tomfoolery.configurations.contexts;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.StandardApplicationContext;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.search.DocumentSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.AdministratorSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.PatronSearchGenerator;
import org.tomfoolery.core.dataproviders.generators.users.search.StaffSearchGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.RetrievalDocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.InMemoryLinearAdministratorSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.InMemoryLinearPatronSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.users.search.InMemoryLinearStaffSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.hathitrust.documents.HathiTrustBibliographyApiRetrievalDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.openlibrary.documents.OpenLibraryApiRetrievalDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.InMemoryBorrowingSessionRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.InMemoryDocumentContentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations.InMemoryReviewRepository;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.recommendation.InMemoryIndexedDocumentRecommendationGenerator;
import org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search.InMemoryIndexedDocumentSearchGenerator;
import org.tomfoolery.infrastructures.dataproviders.repositories.api.rest.google.documents.GoogleApiRetrievalDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents.InMemoryDocumentRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryAdministratorRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryPatronRepository;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.InMemoryStaffRepository;

import java.util.List;

public class InMemoryApplicationContext extends StandardApplicationContext {
    public static @NonNull InMemoryApplicationContext of() {
        return new InMemoryApplicationContext();
    }

    @Override
    protected @NonNull DocumentRepository createDocumentRepository() {
        return InMemoryDocumentRepository.of();
    }

    @Override
    protected @NonNull List<RetrievalDocumentRepository> createRetrievalDocumentRepositories() {
        val httpClientProvider = this.getHttpClientProvider();

        return List.of(
            GoogleApiRetrievalDocumentRepository.of(httpClientProvider),
            OpenLibraryApiRetrievalDocumentRepository.of(httpClientProvider),
            HathiTrustBibliographyApiRetrievalDocumentRepository.of(httpClientProvider)
        );
    }

    @Override
    protected @NonNull AdministratorRepository createAdministratorRepository() {
        return InMemoryAdministratorRepository.of();
    }

    @Override
    protected @NonNull PatronRepository createPatronRepository() {
        return InMemoryPatronRepository.of();
    }

    @Override
    protected @NonNull StaffRepository createStaffRepository() {
        return InMemoryStaffRepository.of();
    }

    @Override
    protected @NonNull DocumentContentRepository createDocumentContentRepository() {
        return InMemoryDocumentContentRepository.of();
    }

    @Override
    protected @NonNull BorrowingSessionRepository createBorrowingSessionRepository() {
        return InMemoryBorrowingSessionRepository.of();
    }

    @Override
    protected @NonNull ReviewRepository createReviewRepository() {
        return InMemoryReviewRepository.of();
    }

    @Override
    protected @NonNull DocumentSearchGenerator createDocumentSearchGenerator() {
        return InMemoryIndexedDocumentSearchGenerator.of();
    }

    @Override
    protected @NonNull DocumentRecommendationGenerator createDocumentRecommendationGenerator() {
        return InMemoryIndexedDocumentRecommendationGenerator.of();
    }

    @Override
    protected @NonNull AdministratorSearchGenerator createAdministratorSearchGenerator() {
        return InMemoryLinearAdministratorSearchGenerator.of();
    }

    @Override
    protected @NonNull PatronSearchGenerator createPatronSearchGenerator() {
        return InMemoryLinearPatronSearchGenerator.of();
    }

    @Override
    protected @NonNull StaffSearchGenerator createStaffSearchGenerator() {
        return InMemoryLinearStaffSearchGenerator.of();
    }
}
