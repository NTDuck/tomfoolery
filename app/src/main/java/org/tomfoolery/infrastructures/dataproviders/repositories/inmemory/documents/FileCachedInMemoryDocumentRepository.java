package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.documents;

import lombok.Locked;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.utils.contracts.ddd;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.abc.BaseInMemoryRepository;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class FileCachedInMemoryDocumentRepository implements DocumentRepository {
    private final @NonNull FileStorageProvider fileStorageProvider;

    private final @NonNull BaseRepository<MinimalDocument, Document.Id> minimalDocumentRepository = new BaseInMemoryRepository<>() {
        @Override
        protected @NonNull Comparator<Document.Id> getEntityIdComparator() {
        return DocumentComparator.compareId();
        }
    };

    private final @NonNull Map<Document.Id, String> documentCoverImageFilePathsByIds = new ConcurrentSkipListMap<>(DocumentComparator.compareId());

    @Override
    @SneakyThrows
    @Locked.Write
    public void save(@NonNull Document document) {
        val minimalDocument = mapDocumentToMinimalDocument(document);
        this.minimalDocumentRepository.save(minimalDocument);

        val documentCoverImage = document.getCoverImage();

        if (documentCoverImage == null)
            return;

        val rawDocumentCoverImage = documentCoverImage.getBytes();
        val documentCoverImageFilePath = this.fileStorageProvider.save(rawDocumentCoverImage);

        this.documentCoverImageFilePathsByIds.put(document.getId(), documentCoverImageFilePath);
    }

    @Override
    @Locked.Write
    public void delete(Document.@NonNull Id documentId) {
        this.minimalDocumentRepository.delete(documentId);
        this.documentCoverImageFilePathsByIds.remove(documentId);
    }

    @Override
    @SneakyThrows
    @Locked.Read
    public @Nullable Document getById(Document.@NonNull Id documentId) {
        val minimalDocument = this.minimalDocumentRepository.getById(documentId);

        if (minimalDocument == null)
            return null;

        val documentCoverImage = this.getDocumentCoverImageById(documentId);

        return mapMinimalDocumentAndCoverImageToDocument(minimalDocument, documentCoverImage);
    }

    @Override
    @Locked.Read
    public @NonNull List<Document> show() {
        return this.minimalDocumentRepository.show().parallelStream()
            .map(minimalDocument -> {
                val documentId = minimalDocument.getId();
                val documentCoverImage = this.getDocumentCoverImageById(documentId);
                return mapMinimalDocumentAndCoverImageToDocument(minimalDocument, documentCoverImage);
        })
        .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Locked.Read
    public boolean contains(Document.@NonNull Id documentId) {
        return this.minimalDocumentRepository.contains(documentId);
    }

    @Override
    @Locked.Read
    public @Unsigned int size() {
        return this.minimalDocumentRepository.size();
    }

    @Override
    @Locked.Read
    public @Nullable Page<Document> showPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val minimalDocumentsPage = this.minimalDocumentRepository.showPage(pageIndex, maxPageSize);

        if (minimalDocumentsPage == null)
            return null;

        return minimalDocumentsPage
            .map(minimalDocument -> {
                val documentId = minimalDocument.getId();
                val documentCoverImage = this.getDocumentCoverImageById(documentId);

                return mapMinimalDocumentAndCoverImageToDocument(minimalDocument, documentCoverImage);
            });
    }

    private static @NonNull MinimalDocument mapDocumentToMinimalDocument(@NonNull Document document) {
        return MinimalDocument.of(document.getId(), document.getAudit(), document.getMetadata(), document.getRating());
    }

    private static @NonNull Document mapMinimalDocumentAndCoverImageToDocument(@NonNull MinimalDocument minimalDocument, Document.@Nullable CoverImage coverImage) {
        return Document.of(
            minimalDocument.getId(), minimalDocument.getAudit(), minimalDocument.getMetadata(), minimalDocument.getRating(),
            coverImage
        );
    }

    @SneakyThrows
    @Locked.Read
    private Document.@Nullable CoverImage getDocumentCoverImageById(Document.@NonNull Id documentId) {
        val documentCoverImageFilePath = this.documentCoverImageFilePathsByIds.get(documentId);

        if (documentCoverImageFilePath == null)
            return null;

        val rawDocumentCoverImage = this.fileStorageProvider.read(documentCoverImageFilePath);
        return Document.CoverImage.of(rawDocumentCoverImage);
    }

    @Value(staticConstructor = "of")
    private static class MinimalDocument implements ddd.Entity<Document.Id> {
        Document.@NonNull Id id;

        Document.@NonNull Audit audit;
        Document.@NonNull Metadata metadata;
        Document.@Nullable Rating rating;
    }
}
