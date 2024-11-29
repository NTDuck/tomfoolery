package org.tomfoolery.core.dataproviders.repositories.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;
import org.tomfoolery.core.utils.dataclasses.Page;

import java.util.List;

public interface DocumentRepository extends BaseSynchronizableRepository<Document, Document.Id> {
    @Nullable DocumentWithoutContent getByIdWithoutContent(Document.@NonNull Id documentId);
    @NonNull List<DocumentWithoutContent> showWithoutContent();

    default void save(@NonNull DocumentWithoutContent documentWithoutContent) {
        val documentId = documentWithoutContent.getId();
        val document = this.getById(documentId);

        if (document == null)
            this.save(documentWithoutContent.withContent(null));
        else
            this.save(documentWithoutContent.withContent(document.getContent()));
    }

    default @Nullable Page<DocumentWithoutContent> showPaginatedWithoutContent(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedDocumentsWithoutContent = this.showWithoutContent();
        return Page.fromUnpaginated(unpaginatedDocumentsWithoutContent, pageIndex, maxPageSize);
    }
}
