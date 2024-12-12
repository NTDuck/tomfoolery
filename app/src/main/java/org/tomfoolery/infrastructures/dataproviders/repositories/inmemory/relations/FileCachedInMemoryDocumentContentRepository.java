package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.relations;

import lombok.Locked;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.utils.dataclasses.Page;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.infrastructures.utils.helpers.comparators.DocumentComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class FileCachedInMemoryDocumentContentRepository implements DocumentContentRepository {
    private final @NonNull FileStorageProvider fileStorageProvider;

    private final @NonNull Map<DocumentContent.Id, String> documentContentFilePathsByIds = new ConcurrentSkipListMap<>(Comparator.comparing(DocumentContent.Id::getEntityId, DocumentComparator.compareId()));

    @Override
    @SneakyThrows
    @Locked.Write
    public void save(@NonNull DocumentContent documentContent) {
        val rawDocumentContent = documentContent.getBytes();
        val documentContentFilePath = this.fileStorageProvider.save(rawDocumentContent);

        this.documentContentFilePathsByIds.put(documentContent.getId(), documentContentFilePath);
    }

    @Override
    @Locked.Write
    public void delete(DocumentContent.@NonNull Id documentContentId) {
        this.documentContentFilePathsByIds.remove(documentContentId);
    }

    @Override
    @SneakyThrows
    @Locked.Read
    public @Nullable DocumentContent getById(DocumentContent.@NonNull Id documentContentId) {
        val documentContentFilePath = this.documentContentFilePathsByIds.get(documentContentId);

        if (documentContentFilePath == null)
            return null;

        val rawDocumentContent = this.fileStorageProvider.read(documentContentFilePath);
        return DocumentContent.of(documentContentId, rawDocumentContent);
    }

    @Override
    @Locked.Read
    public @NonNull List<DocumentContent> show() {
        return this.documentContentFilePathsByIds.keySet().parallelStream()
            .map(this::getById)   // Guaranteed to be not null
            .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Locked.Read
    public boolean contains(DocumentContent.@NonNull Id documentContentId) {
        return this.documentContentFilePathsByIds.containsKey(documentContentId);
    }

    @Override
    @Locked.Read
    public @Unsigned int size() {
        return this.documentContentFilePathsByIds.size();
    }

    @Override
    @Locked.Read
    public @Nullable Page<DocumentContent> showPage(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val paginatedDocumentContentIds = this.showIdsPage(pageIndex, maxPageSize);

        if (paginatedDocumentContentIds == null)
            return null;

        return paginatedDocumentContentIds.map(this::getById);
    }

    @Override
    @Locked.Write
    public void synchronizeDeletedEntity(Document.@NonNull Id documentId) {
        val documentContentId = DocumentContent.Id.of(documentId);
        this.delete(documentContentId);
    }
}
