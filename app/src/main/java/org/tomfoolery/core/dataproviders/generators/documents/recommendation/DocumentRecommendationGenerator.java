package org.tomfoolery.core.dataproviders.generators.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.abc.BaseSynchronizedGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public interface DocumentRecommendationGenerator extends BaseSynchronizedGenerator<Document, Document.Id> {
    @NonNull List<Document> getLatestDocumentRecommendation();
    @NonNull List<Document> getTopRatedDocumentRecommendation();
}
