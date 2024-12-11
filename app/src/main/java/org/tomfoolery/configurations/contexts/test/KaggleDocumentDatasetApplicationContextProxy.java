package org.tomfoolery.configurations.contexts.test;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.test.abc.ApplicationContextProxy;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class KaggleDocumentDatasetApplicationContextProxy implements ApplicationContextProxy {
    private static final @NonNull String DOCUMENT_DATASET_PATH = "datasets/kaggle/saurabhbagchi/books.csv";

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();

    @Override
    @SneakyThrows
    public void intercept(@NonNull ApplicationContext applicationContext) {
        val documentRepository = applicationContext.getDocumentRepository();
        val httpClientProvider = applicationContext.getHttpClientProvider();

        val csvPath = Path.of(ResourceProvider.getResourceAbsolutePath(DOCUMENT_DATASET_PATH));

        @Cleanup
        val csvLines = Files.lines(csvPath);

        csvLines
            .skip(1)   // Skip header row
            // .parallel()
            .map(csvRow -> this.parseCsvRow(httpClientProvider, csvRow))
            .forEach(documentRepository::save);
    }

    private @NonNull Document parseCsvRow(@NonNull HttpClientProvider httpClientProvider, @NonNull String csvRow) {
        val csvColumns = csvRow.split(";");

        val ISBN_10 = trimQuotes(csvColumns[0]);
        val title = trimQuotes(csvColumns[1]);
        val author = trimQuotes(csvColumns[2]);
        val rawPublishedYear = trimQuotes(csvColumns[3]);
        val publisher = trimQuotes(csvColumns[4]);
        val coverImageUrl = trimQuotes(csvColumns[7]);

        val documentId = Document.Id.of(ISBN_10);
        assert documentId != null;

        val fullyMockedDocument = this.documentMocker.createMockEntityWithId(documentId);

        val metadata = Document.Metadata.of(
            title,
            fullyMockedDocument.getMetadata().getDescription(),
            List.of(author),
            fullyMockedDocument.getMetadata().getGenres(),
            Year.of(Integer.parseInt(rawPublishedYear)),
            publisher
        );

        val coverImage = this.loadCoverImage(httpClientProvider, coverImageUrl);

        fullyMockedDocument.setMetadata(metadata);
        fullyMockedDocument.setCoverImage(coverImage);

        return fullyMockedDocument;
    }

    private Document.@Nullable CoverImage loadCoverImage(@NonNull HttpClientProvider httpClientProvider, @NonNull String coverImageUrl) {
        try {
            System.out.println(coverImageUrl);
            val rawCoverImage = httpClientProvider
                .sendSynchronousGETForBytes(coverImageUrl, HttpClientProvider.Headers.builder().build());
            return Document.CoverImage.of(rawCoverImage);

        } catch (Exception exception) {
            return null;
        }
    }

    private static @NonNull String trimQuotes(@NonNull String value) {
        return value.startsWith("\"") && value.endsWith("\"")
            ? value.substring(1, value.length() - 1)
            : value;
    }
}
