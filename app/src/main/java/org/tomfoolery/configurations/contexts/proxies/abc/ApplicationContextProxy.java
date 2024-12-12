package org.tomfoolery.configurations.contexts.proxies.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ApplicationContextProxy {
    @NonNull CompletableFuture<Void> intercept(@NonNull ApplicationContext applicationContext);
}
