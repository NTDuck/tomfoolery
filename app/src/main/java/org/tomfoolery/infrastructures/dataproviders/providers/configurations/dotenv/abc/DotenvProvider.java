package org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.infrastructures.dataproviders.providers.abc.BaseProvider;

public interface DotenvProvider extends BaseProvider {
    @Nullable CharSequence get(@NonNull String key);

    default @NonNull CharSequence getOrDefault(@NonNull String key, @NonNull CharSequence defaultValue) {
        val value = this.get(key);
        return value != null ? value : defaultValue;
    }
}
