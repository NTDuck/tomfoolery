package org.tomfoolery.infrastructures.dataproviders.generators.inmemory.documents.search;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.infrastructures.dataproviders.generators.linear.documents.search.LinearDocumentSearchGenerator;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class InMemoryLinearDocumentSearchGenerator extends LinearDocumentSearchGenerator {
    protected InMemoryLinearDocumentSearchGenerator(@NonNull Repository repository) {
        super(repository);
    }

    private static class Repository implements LinearDocumentSearchGenerator.Repository {
        private @NonNull Instant

        @Override
        public @NonNull Collection<FragmentaryDocument> showDocuments() {
            return List.of();
        }

        @Override
        public void saveDocument(@NonNull FragmentaryDocument fragmentaryDocument) {

        }

        @Override
        public void deleteDocument(Document.@NonNull Id documentId) {

        }

        @Override
        public @NonNull Instant getLastSynchronizedTimestamp() {
            return null;
        }

        @Override
        public void setLastSynchronizedTimestamp(@NonNull Instant lastSynchronizedTimestamp) {

        }
    }
}
