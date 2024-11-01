package org.tomfoolery.core.dataproviders.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.Document;

@FunctionalInterface
public interface DocumentUrlGenerator {
    @NonNull String generateUrl(@NonNull Document document);
}
