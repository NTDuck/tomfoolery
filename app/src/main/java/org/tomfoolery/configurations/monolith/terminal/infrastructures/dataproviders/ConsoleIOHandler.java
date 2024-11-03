package org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;

import java.io.Console;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleIOHandler implements IOHandler {
    private final @NonNull Console console;

    public static @Nullable ConsoleIOHandler of() {
        val console = System.console();

        if (console == null)
            return null;

        return new ConsoleIOHandler(console);
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
}
