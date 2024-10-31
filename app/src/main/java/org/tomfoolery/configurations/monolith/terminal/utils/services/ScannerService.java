package org.tomfoolery.configurations.monolith.terminal.utils.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ScannerService {
    @Getter
    private static final @NonNull Scanner scanner = new Scanner(System.in);

    public static void close() {
        scanner.close();
    }
}
