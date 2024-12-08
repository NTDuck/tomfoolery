package org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

@NoArgsConstructor(staticName = "of")
public class CdimascioDotenvProvider implements DotenvProvider {
    private static final @NonNull Dotenv dotenv = Dotenv.load();

    @Override
    public @Nullable CharSequence get(@NonNull String key) {
        return dotenv.get(key);
    }

    @Override
    public @NonNull CharSequence getOrDefault(@NonNull String key, @NonNull CharSequence defaultValue) {
        return dotenv.get(key, String.valueOf(defaultValue));
    }

    @Override
    public void close() {}
}
