package org.tomfoolery.infrastructures.dataproviders.generators.apache.httpclient.documents.references.abc;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.documents.references.DocumentUrlGenerator;
import org.tomfoolery.core.domain.documents.Document;

import java.util.List;

public abstract class ApacheHttpClientDocumentUrlGenerator implements DocumentUrlGenerator {
    @Override
    @SneakyThrows
    public @NonNull String generateUrlFromDocument(@NonNull Document document) {
        val uri = new URIBuilder()
            .setScheme(this.getUrlScheme())
            .setHost(this.getUrlHost())
            .setPath(this.getUrlPath(document))

            .setParameters(this.getUrlParameterPairs(document))

            .build();

        return uri.toASCIIString();
    }

    protected abstract @NonNull String getUrlScheme();
    protected abstract @NonNull String getUrlHost();
    protected abstract @NonNull String getUrlPath(@NonNull Document document);

    protected @NonNull List<NameValuePair> getUrlParameterPairs(@NonNull Document document) {
        return List.of();
    }
}
