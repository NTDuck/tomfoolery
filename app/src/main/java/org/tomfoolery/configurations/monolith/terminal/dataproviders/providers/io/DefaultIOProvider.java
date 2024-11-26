package org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;

import java.util.Scanner;

@NoArgsConstructor(staticName = "of")
public class DefaultIOProvider implements IOProvider {
    private final @NonNull Scanner scanner = new Scanner(System.in);

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
