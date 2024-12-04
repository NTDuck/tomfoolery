package org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.infrastructures.dataproviders.providers.abc.BaseProvider;

public interface IOProvider extends BaseProvider {
    void write(@NonNull String format, Object... args);
    @NonNull String readLine();

    default void writeLine(@NonNull String format, Object... args) {
        this.write(format, args);
        this.write(System.lineSeparator());
    }

    default char @NonNull [] readPassword() {
        return this.readLine().toCharArray();
    }

    default @NonNull String readLine(@NonNull String format, Object... args) {
        this.write(format, args);
        return this.readLine();
    }

    default char @NonNull [] readPassword(@NonNull String format, Object... args) {
        this.write(format, args);
        return this.readPassword();
    }
}
