package org.tomfoolery.configurations.monolith.console.dataproviders.providers.io;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@NoArgsConstructor(staticName = "of")
public class BuiltinIOProvider implements IOProvider {
    // Use `ISO-8859-1` or `windows-1252` for compatibility with ANSI fonts
    private static final @NonNull Charset CHARSET = StandardCharsets.ISO_8859_1;

    private final @NonNull Scanner scanner = new Scanner(System.in, CHARSET);

    static {
        System.setOut(new PrintStream(System.out, true, CHARSET));
    }

    @Override
    public void write(@NonNull String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public @NonNull String readLine() {
        return this.scanner.nextLine();
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
