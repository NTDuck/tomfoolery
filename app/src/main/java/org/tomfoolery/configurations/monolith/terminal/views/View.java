package org.tomfoolery.configurations.monolith.terminal.views;

import lombok.NonNull;

import java.util.Scanner;

public abstract class View implements AutoCloseable {
    private static int referenceCount = 0;
    private static final @NonNull Scanner scanner = new Scanner(System.in);

    protected final void display(@NonNull String content) {
        System.out.println(content);
    }

    protected final String waitForUserInput() {
        return scanner.nextLine();
    }

    protected View() {
        ++referenceCount;
    }

    @Override
    public void close() {
        --referenceCount;

        if (referenceCount == 0)
            scanner.close();
    }
}
