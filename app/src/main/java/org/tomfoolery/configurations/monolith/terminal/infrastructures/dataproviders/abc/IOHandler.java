package org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface IOHandler {
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
