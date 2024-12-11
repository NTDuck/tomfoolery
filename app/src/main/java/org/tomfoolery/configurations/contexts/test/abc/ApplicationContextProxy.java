package org.tomfoolery.configurations.contexts.test.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;

@FunctionalInterface
public interface ApplicationContextProxy {
    void intercept(@NonNull ApplicationContext applicationContext);
}
