package org.tomfoolery.infrastructures.dataproviders.providers.httpclient.abc;

import lombok.Builder;
import lombok.Singular;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.dataproviders.providers.abc.BaseProvider;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface HttpClientProvider extends BaseProvider {
    @NonNull String sendSynchronousGET(@NonNull String url, @NonNull Headers headers) throws Exception;
    byte @NonNull [] sendSynchronousGETForBytes(@NonNull String url, @NonNull Headers headers) throws Exception;

    @NonNull String sendSynchronousPOST(@NonNull String url, @NonNull Headers headers, @NonNull String body) throws Exception;

    default @NonNull CompletableFuture<String> sendAsynchronousGET(@NonNull String url, @NonNull Headers headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendSynchronousGET(url, headers);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    default @NonNull CompletableFuture<String> sendAsynchronousPOST(@NonNull String url, @NonNull Headers headers, @NonNull String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendSynchronousPOST(url, headers, body);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    @Builder(toBuilder = true, setterPrefix = "set")
    class Headers {
        private @NonNull @Singular Map<String, String> headers;

        public void forEach(@NonNull BiConsumer<? super String, ? super String> action) {
            this.headers.forEach(action);
        }
    }
}
