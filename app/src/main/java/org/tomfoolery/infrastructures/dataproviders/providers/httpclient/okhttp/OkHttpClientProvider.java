package org.tomfoolery.infrastructures.dataproviders.providers.httpclient.okhttp;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import okhttp3.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc.HttpClientProvider;

import java.io.IOException;
import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public class OkHttpClientProvider implements HttpClientProvider {
    private final @NonNull OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build();

    @Override
    public @NonNull String sendSynchronousGET(@NotNull String url, @NonNull Headers headers) throws RequestExecutionException, ResponseParsingException {
        val request = constructRequest(url, headers);
        @Cleanup val response = this.executeRequest(request);
        return parseResponse(response, ResponseBody::string);
    }

    @Override
    public byte @NonNull [] sendSynchronousGETForBytes(@NonNull String url, @NonNull Headers headers) throws RequestExecutionException, ResponseParsingException {
        val request = constructRequest(url, headers);
        @Cleanup val response = this.executeRequest(request);
        return parseResponse(response, ResponseBody::bytes);
    }

    @Override
    public @NonNull String sendSynchronousPOST(@NonNull String url, @NonNull Headers headers, @NonNull String body) throws RequestExecutionException, ResponseParsingException {
        val request = constructRequest(url, headers, body);
        @Cleanup val response = this.executeRequest(request);
        return parseResponse(response, ResponseBody::string);
    }

    /**
     * @see <a href="https://square.github.io/okhttp/5.x/okhttp/okhttp3/-ok-http-client/">OKHttpClient documentation</a>
     */
    @Override
    public void close() {
        this.shutdownExecutorService();
        this.clearConnectionPool();
        this.clearClientCache();
    }

    private static @NonNull Request constructRequest(@NonNull String url, @NonNull Headers headers) {
        val builder = new Request.Builder();

        builder.url(url);
        headers.forEach(builder::addHeader);

        return builder.build();
    }

    private static @NonNull Request constructRequest(@NonNull String url, @NonNull Headers headers, @NonNull String body) {
        val request = constructRequest(url, headers);
        val requestBody = RequestBody.create(body, MediaType.get("application/json"));

        return request.newBuilder()
            .post(requestBody)
            .build();
    }

    private @NonNull Response executeRequest(@NonNull Request request) throws RequestExecutionException {
        try {
            return this.client.newCall(request).execute();
        } catch (IOException exception) {
            throw new RequestExecutionException();
        }
    }

    private static <T> @NonNull T parseResponse(@NonNull Response response, @NonNull ThrowableFunction<ResponseBody, T> mapper) throws ResponseParsingException {
        val responseBody = response.body();

        if (responseBody == null)
            throw new ResponseParsingException();

        try {
            return mapper.apply(responseBody);
        } catch (Exception exception) {
            throw new ResponseParsingException();
        }
    }

    private void shutdownExecutorService() {
        @Cleanup val executorService = this.client.dispatcher().executorService();
        executorService.shutdown();
    }

    private void clearConnectionPool() {
        this.client.connectionPool().evictAll();
    }

    @SneakyThrows
    private void clearClientCache() {
        @Cleanup val cache = this.client.cache();

        if (cache != null && !cache.isClosed())
            cache.close();
    }

    public static class RequestExecutionException extends Exception {}
    public static class ResponseParsingException extends Exception {}
}
