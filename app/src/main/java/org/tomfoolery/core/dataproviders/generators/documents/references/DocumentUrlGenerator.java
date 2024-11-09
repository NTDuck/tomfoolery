package org.tomfoolery.core.dataproviders.generators.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.documents.FragmentaryDocument;

public interface DocumentUrlGenerator {
    @NonNull String generateUrlFromFragmentaryDocument(@NonNull FragmentaryDocument fragmentaryDocument);
}
