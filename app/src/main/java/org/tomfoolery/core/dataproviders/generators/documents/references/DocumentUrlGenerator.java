package org.tomfoolery.core.dataproviders.generators.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

public interface DocumentUrlGenerator {
    @NonNull String generateUrlFromDocumentPreview(Document.@NonNull Preview documentPreview);
}
