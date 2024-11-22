package org.tomfoolery.infrastructures.dataproviders.providers.httpclient.builtin;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor(staticName = "of")
public class BuiltinHttpClientProvider implements HttpClientProvider {
    private final @Unsigned int SUCCESS_STATUS_CODE = 200;

    private final @NonNull HttpClient client = HttpClient.newHttpClient();

    @Override
    public @NonNull String sendSynchronousGET(@NonNull String url, @NonNull Headers headers) throws Exception {
        val request = constructRequest(url, headers);
        val response = this.executeSynchronousRequest(request);
        return parseResponse(response);
    }

    @Override
    public @NonNull String sendSynchronousPOST(@NonNull String url, @NonNull Headers headers, @NonNull String body) throws RequestExecutionException {
        val request = constructRequest(url, headers, body);
        val response = this.executeSynchronousRequest(request);
        return parseResponse(response);
    }

    @Override
    public @NonNull CompletableFuture<String> sendAsynchronousGET(@NonNull String url, @NonNull Headers headers) {
        val request = constructRequest(url, headers);
        return this.executeAsynchronousRequest(request)
            .thenApply(BuiltinHttpClientProvider::parseResponse);
    }

    @Override
    public @NonNull CompletableFuture<String> sendAsynchronousPOST(@NonNull String url, @NonNull Headers headers, @NonNull String body) {
        val request = constructRequest(url, headers, body);
        return this.executeAsynchronousRequest(request)
            .thenApply(BuiltinHttpClientProvider::parseResponse);
    }

    @Override
    public void close() {
        this.client.close();
    }

    private static @NonNull HttpRequest constructRequest(@NonNull String url, @NonNull Headers headers) {
        val builder = HttpRequest.newBuilder();

        builder.uri(URI.create(url));
        headers.forEach(builder::header);

        return builder.build();
    }

    private static @NonNull HttpRequest constructRequest(@NonNull String url, @NonNull Headers headers, @NonNull String body) {
        val builder = HttpRequest.newBuilder();

        builder.uri(URI.create(url));
        headers.forEach(builder::header);
        builder.POST(HttpRequest.BodyPublishers.ofString(body));

        return builder.build();
    }

    private @NonNull HttpResponse<String> executeSynchronousRequest(@NonNull HttpRequest request) throws RequestExecutionException {
        try {
            val response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != SUCCESS_STATUS_CODE)
                throw new RequestExecutionException();

            return response;

        } catch (IOException | InterruptedException exception) {
            throw new RequestExecutionException();
        }
    }

    private @NonNull CompletableFuture<HttpResponse<String>> executeAsynchronousRequest(@NonNull HttpRequest request) {
        return this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private static @NonNull String parseResponse(@NonNull HttpResponse<String> response) {
        return response.body();
    }

    public static class RequestExecutionException extends Exception {}
}
