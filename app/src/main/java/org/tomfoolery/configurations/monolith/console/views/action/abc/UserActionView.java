package org.tomfoolery.configurations.monolith.console.views.action.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;

public abstract class UserActionView extends BaseActionView {
    protected UserActionView(@NonNull IOProvider ioProvider) {
        super(ioProvider);
    }
}
