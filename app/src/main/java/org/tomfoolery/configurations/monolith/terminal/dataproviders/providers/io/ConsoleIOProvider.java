package org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;

import java.io.Console;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleIOProvider implements IOProvider {
    private final @NonNull Console console;

    @SneakyThrows
    public static @NonNull ConsoleIOProvider of() {
        val console = System.console();

        if (console == null)
            throw new InstantiationException();

        return new ConsoleIOProvider(console);
    }

    @Override
    public void write(@NonNull String format, Object... args) {
        this.console.printf(format, args);
    }

    @Override
    public @NonNull String readLine() {
        return this.console.readLine();
    }

    @Override
    public char @NonNull [] readPassword() {
        return this.console.readPassword();
    }

    @Override
    public @NonNull String readLine(@NonNull String format, Object... args) {
        return this.console.readLine(format, args);
    }

    @Override
    public char @NonNull [] readPassword(@NonNull String format, Object... args) {
        return this.console.readPassword(format, args);
    }

    @Override
    public void close() {}
}
