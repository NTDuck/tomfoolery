package org.tomfoolery.core.dataproviders.repositories.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.abc.BaseSynchronizableRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;
import org.tomfoolery.core.utils.dataclasses.common.Page;

import java.util.List;
import java.util.stream.Collectors;

public interface DocumentRepository extends BaseSynchronizableRepository<Document, Document.Id> {
    default void saveFragment(@NonNull FragmentaryDocument fragmentaryDocument) {
        val documentId = fragmentaryDocument.getId();
        val document = this.getById(documentId);

        if (document == null)
            return;

        document.setMetadata(fragmentaryDocument.getMetadata());
        document.setAudit(fragmentaryDocument.getAudit());

        this.save(document);
    }

    default @Nullable FragmentaryDocument getFragmentById(Document.@NonNull Id documentId) {
        val document = this.getById(documentId);

        if (document == null)
            return null;

        return FragmentaryDocument.of(document);
    }

    default @NonNull List<FragmentaryDocument> showFragments() {
        val documents = this.show();

        return documents.parallelStream()
            .map(FragmentaryDocument::of)
            .collect(Collectors.toUnmodifiableList());
    }

    default @Nullable Page<FragmentaryDocument> showPaginatedFragments(@Unsigned int pageIndex, @Unsigned int maxPageSize) {
        val unpaginatedFragmentaryDocuments = this.showFragments();
        return Page.fromUnpaginated(unpaginatedFragmentaryDocuments, pageIndex, maxPageSize);
    }
}
