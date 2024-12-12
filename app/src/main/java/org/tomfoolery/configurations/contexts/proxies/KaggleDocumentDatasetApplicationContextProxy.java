package org.tomfoolery.configurations.contexts.proxies;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.proxies.abc.ApplicationContextProxy;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.resources.ResourceProvider;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class KaggleDocumentDatasetApplicationContextProxy implements ApplicationContextProxy {
    private static final @NonNull String DOCUMENT_DATASET_PATH = "datasets/kaggle/saurabhbagchi/books.csv";

    private static final @Unsigned int NUMBER_OF_DOCUMENTS = 4444;
    
    // Prevents 403 response code caused by Amazon's anti-scraping mechanisms
    private static final HttpClientProvider.@NonNull Headers requestHeaders = HttpClientProvider.Headers.builder()
        .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/244.178.44.111 Safari/537.36")
        .setHeader("Referer", "https://www.kaggle.com/datasets/saurabhbagchi/books")
        .build();

    private final @NonNull Executor executor = ForkJoinPool.commonPool();

    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();

    @Override
    @SneakyThrows
    public @NonNull CompletableFuture<Void> intercept(@NonNull ApplicationContext applicationContext) {
        val documentRepository = applicationContext.getDocumentRepository();
        val httpClientProvider = applicationContext.getHttpClientProvider();

        val csvPath = Path.of(ResourceProvider.getResourceAbsolutePath(DOCUMENT_DATASET_PATH));

        @Cleanup
        val csvLines = Files.lines(csvPath, StandardCharsets.ISO_8859_1);

        val futures = csvLines
            .parallel()
            .skip(1)   // Skip header row
            .limit(NUMBER_OF_DOCUMENTS)
            .map(csvRow -> CompletableFuture
                .supplyAsync(() -> mapCsvRowToPayload(csvRow), this.executor)
                .thenApply(KaggleDocumentDatasetApplicationContextProxy::normalize)
                .thenApply(payload -> this.mapPayloadToDocument(payload, httpClientProvider))
                .thenAccept(documentRepository::save)
            )
            .collect(Collectors.toUnmodifiableList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static @NonNull String normalize(@NonNull String value) {
        return value.startsWith("\"") && value.endsWith("\"")
            ? value.substring(1, value.length() - 1)
            : value;
    }

    private static @NonNull Payload mapCsvRowToPayload(@NonNull String csvRow) {
        val csvColumns = csvRow.split(";");

        return Payload.builder()
            .ISBN_10(csvColumns[0])
            .title(csvColumns[1])
            .author(csvColumns[2])
            .rawPublishedYear(csvColumns[3])
            .publisher(csvColumns[4])
            .coverImageUrl(csvColumns[7])
            .build();
    }

    private static @NonNull Payload normalize(@NonNull Payload payload) {
        return Payload.builder()
            .ISBN_10(normalize(payload.getISBN_10()))
            .title(normalize(payload.getTitle()))
            .author(normalize(payload.getAuthor()))
            .rawPublishedYear(normalize(payload.getRawPublishedYear()))
            .publisher(normalize(payload.getPublisher()))
            .coverImageUrl(normalize(payload.getCoverImageUrl()))
            .build();
    }

    private @NonNull Document mapPayloadToDocument(@NonNull Payload payload, @NonNull HttpClientProvider httpClientProvider) {
        val documentId = Document.Id.of(payload.getISBN_10());
        assert documentId != null;

        val fullyMockedDocument = this.documentMocker.createMockEntityWithId(documentId);

        val metadata = Document.Metadata.of(
            payload.getTitle(),
            fullyMockedDocument.getMetadata().getDescription(),
            List.of(payload.getAuthor()),
            fullyMockedDocument.getMetadata().getGenres(),
            Year.of(Integer.parseInt(payload.getRawPublishedYear())),
            payload.getPublisher()
        );

        val coverImage = this.getDocumentCoverImageFromUrl(payload.getCoverImageUrl(), httpClientProvider);

        fullyMockedDocument.setMetadata(metadata);
        fullyMockedDocument.setCoverImage(coverImage);

        return fullyMockedDocument;
    }

    private Document.@Nullable CoverImage getDocumentCoverImageFromUrl(@NonNull String coverImageUrl, @NonNull HttpClientProvider httpClientProvider) {
        try {
            val rawCoverImage = httpClientProvider
                .sendSynchronousGETForBytes(coverImageUrl, requestHeaders);
            return Document.CoverImage.of(rawCoverImage);

        } catch (Exception exception) {
            return null;
        }
    }

    @Value
    @Builder(access = AccessLevel.PRIVATE)
    private static class Payload {
        @NonNull String ISBN_10;
        @NonNull String title;
        @NonNull String author;
        @NonNull String rawPublishedYear;
        @NonNull String publisher;
        @NonNull String coverImageUrl;
    }
}
