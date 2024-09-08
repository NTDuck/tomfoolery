package org.tomfoolery.configurations.views;

import org.tomfoolery.configurations.contracts.TerminalContract;

import java.util.Scanner;

/**
 * The view does not acknowledge the existence of the domain, and vice versa.
 */
public class TerminalView implements TerminalContract.View {
    private final Scanner scanner;
    private final TerminalContract.Presenter presenter;

    private boolean isListening = true;

    public TerminalView(TerminalContract.Presenter presenter) {
        this.scanner = new Scanner(System.in);
        this.presenter = presenter;
    }

    public void listenForUserInput() {
        String userInput;

        while (this.isListening) {
            while (this.scanner.hasNextLine()) {
                userInput = this.scanner.nextLine();
                this.presenter.onUserInput(userInput);
            }
        }
    }

    @Override
    public void displayResult(String result) {
        System.out.println(result);

        clear();
        displayPrompt();
    }

    private void displayPrompt() {
        System.out.println("This is a dictionary!");
        System.out.println("Supported commands:");
        System.out.println("/add {word} {definition}");
        System.out.println("/get {word}");
        System.out.println("/quit");
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public void onDestroy() {
        this.isListening = false;
    }
}
