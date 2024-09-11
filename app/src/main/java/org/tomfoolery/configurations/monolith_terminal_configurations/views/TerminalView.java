package org.tomfoolery.configurations.monolith_terminal_configurations.views;

import org.tomfoolery.configurations.monolith_terminal_configurations.contracts.TerminalContract;
import org.tomfoolery.configurations.monolith_terminal_configurations.presenters.TerminalPresenter;
import org.tomfoolery.infrastructures.repositories.InMemoryDictionaryEntryRepository;

import java.util.Scanner;

public class TerminalView implements TerminalContract.View {
    private final TerminalPresenter presenter;

    private final Scanner scanner;
    private boolean isActive = false;

    public TerminalView(InMemoryDictionaryEntryRepository dictionaryEntryRepository) {
        this.presenter = new TerminalPresenter(this, dictionaryEntryRepository);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        this.presenter.onStart();
        this.isActive = true;

        while (this.isActive) {
            requestUserActionSelection();
        }
    }

    @Override
    public void requestUserActionSelection() {
        display("\n\nYour action: ");

        String userActionSelection = this.scanner.nextLine();
        this.presenter.onUserActionSelection(userActionSelection);
    }

    @Override
    public void requestUserActionInputs(String[] labels) {
        String[] userInputs = new String[labels.length];

        for (int index = 0; index < labels.length; index++)
            userInputs[index] = this.requestUserActionInput(labels[index]);

        this.presenter.onUserActionInputs(userInputs);
    }

    private String requestUserActionInput(String label) {
        this.display(label);
        return this.scanner.nextLine();
    }

    @Override
    public void display(String content) {
        System.out.print(content);
    }

    @Override
    public void onDestroy() {
        this.scanner.close();
        this.isActive = false;
    }
}
