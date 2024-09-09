package org.tomfoolery.configurations.monolith_terminal_configurations.views;

import org.tomfoolery.configurations.monolith_terminal_configurations.contracts.TerminalContract;
import org.tomfoolery.configurations.monolith_terminal_configurations.presenters.TerminalPresenter;
import org.tomfoolery.infrastructures.repositories.InMemoryDictionaryEntryRepository;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The view does not acknowledge the existence of the domain, and vice versa.
 */
public class TerminalView implements TerminalContract.View {
    private final TerminalPresenter presenter;

    private volatile boolean isListening = true;

    public TerminalView(InMemoryDictionaryEntryRepository dictionaryEntryRepository) {
        this.presenter = new TerminalPresenter(this, dictionaryEntryRepository);
    }

    public void listenForUserInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput;

        displayPrompt();

        while (this.isListening) {
            userInput = scanner.nextLine();
            onUserInput(userInput);
        }

        scanner.close();
    }

    @Override
    public void displayResponse(String response) {
        System.out.println(response);
    }

    @Override
    public void onDestroy() {
        this.isListening = false;
    }

    private void onUserInput(String userInput) {
        presenter.onUserInput(userInput);
    }

    private void displayPrompt() {
        System.out.println("This is a dictionary!");
        System.out.println("Supported commands:");
        System.out.println("/add {word} {definition}");
        System.out.println("/get {word}");
        System.out.println("/quit");
        System.out.println();
    }
}
