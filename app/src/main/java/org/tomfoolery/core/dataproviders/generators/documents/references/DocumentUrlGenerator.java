package org.tomfoolery.core.dataproviders.generators.documents.references;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseGenerator;
import org.tomfoolery.core.domain.documents.Document;

public interface DocumentUrlGenerator extends BaseGenerator {
    @NonNull String generateUrlFromDocument(@NonNull Document document);
}
