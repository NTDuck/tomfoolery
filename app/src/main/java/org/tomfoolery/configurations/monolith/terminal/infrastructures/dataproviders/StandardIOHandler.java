package org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.infrastructures.dataproviders.abc.IOHandler;

import java.util.Scanner;

@NoArgsConstructor(staticName = "of")
public class StandardIOHandler implements IOHandler {
    private final @NonNull Scanner scanner = new Scanner(System.in);

    @Override
    public void write(@NonNull String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public @NonNull String readLine() {
        return this.scanner.nextLine();
    }
}
