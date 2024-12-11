package org.tomfoolery.configurations.contexts.utils.containers;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.test.abc.ApplicationContextProxy;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class ApplicationContextProxies implements ApplicationContextProxy {
    private final @NonNull List<ApplicationContextProxy> applicationContextProxies;

    @Override
    public void intercept(@NonNull ApplicationContext applicationContext) {
        this.applicationContextProxies.parallelStream()
            .forEach(applicationContextProxy -> applicationContextProxy.intercept(applicationContext));
    }
}
