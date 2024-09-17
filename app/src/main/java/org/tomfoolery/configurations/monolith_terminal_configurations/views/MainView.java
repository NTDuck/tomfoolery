package org.tomfoolery.configurations.monolith_terminal_configurations.views;

import java.util.Scanner;

public class MainView implements AutoCloseable {
    private final Scanner scanner;

    public MainView() {
        this.scanner = new Scanner(System.in);
    }

    public void display(String content) {
        System.out.print(content);
    }

    public String requestUserInput() {
        return this.scanner.nextLine();
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
