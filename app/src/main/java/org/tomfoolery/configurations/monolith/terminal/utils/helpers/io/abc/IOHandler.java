package org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface IOHandler extends AutoCloseable {
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

    @Override
    default void close() throws Exception {}
}
