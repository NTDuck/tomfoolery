package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;

import java.util.List;

public interface DocumentRecommendationGenerator extends BaseSynchronizedGenerator<Document, Document.Id> {
    @NonNull List<DocumentWithoutContent> getLatestDocumentRecommendation();
    @NonNull List<DocumentWithoutContent> getPopularDocumentRecommendation();
    @NonNull List<DocumentWithoutContent> getTopRatedDocumentRecommendation();
}
