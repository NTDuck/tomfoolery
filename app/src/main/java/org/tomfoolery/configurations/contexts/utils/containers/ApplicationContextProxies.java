package org.tomfoolery.configurations.contexts.utils.containers;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.proxies.abc.ApplicationContextProxy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class ApplicationContextProxies implements ApplicationContextProxy {
    private final @NonNull List<ApplicationContextProxy> applicationContextProxies;

    @Override
    public @NonNull CompletableFuture<Void> intercept(@NonNull ApplicationContext applicationContext) {
        val futures = this.applicationContextProxies.parallelStream()
            .map(proxy -> proxy.intercept(applicationContext))
            .collect(Collectors.toUnmodifiableList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
