package org.tomfoolery.configurations.monolith_terminal_configurations.controllers;

import org.tomfoolery.configurations.monolith_terminal_configurations.presenters.*;
import org.tomfoolery.configurations.monolith_terminal_configurations.views.*;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.*;

public class MainController {
    private static final Integer selectionOfExitPresenter = 0;

    private final CreateDictionaryEntryUseCase createDictionaryEntryUseCase;
    private final RetrieveAllDictionaryEntriesUseCase retrieveAllDictionaryEntriesUseCase;
    private final RetrieveDictionaryEntriesPartialMatchUseCase retrieveDictionaryEntriesPartialMatchUseCase;
    private final RetrieveDictionaryEntryExactMatchUseCase retrieveDictionaryEntryExactMatchUseCase;
    private final UpdateDictionaryEntryUseCase updateDictionaryEntryUseCase;
    private final DeleteDictionaryEntryUseCase deleteDictionaryEntryUseCase;

    private final DictionaryEntryRepository dictionaryEntryRepository;

    private final MainView view;
    private final MainPresenter presenter;

    public MainController(DictionaryEntryRepository dictionaryEntryRepository) {
        this.dictionaryEntryRepository = dictionaryEntryRepository;

        this.createDictionaryEntryUseCase = new CreateDictionaryEntryUseCase(this.dictionaryEntryRepository);
        this.retrieveAllDictionaryEntriesUseCase = new RetrieveAllDictionaryEntriesUseCase(this.dictionaryEntryRepository);
        this.retrieveDictionaryEntriesPartialMatchUseCase = new RetrieveDictionaryEntriesPartialMatchUseCase(this.dictionaryEntryRepository);
        this.retrieveDictionaryEntryExactMatchUseCase = new RetrieveDictionaryEntryExactMatchUseCase(this.dictionaryEntryRepository);
        this.updateDictionaryEntryUseCase = new UpdateDictionaryEntryUseCase(this.dictionaryEntryRepository);
        this.deleteDictionaryEntryUseCase = new DeleteDictionaryEntryUseCase(this.dictionaryEntryRepository);

        this.view = new MainView();
        this.presenter = new MainPresenter();

        this.addPresenters();
    }

    public void start() {
        this.displayMenu();

        while (true) {
            this.view.display("\nYour action: ");
            String selectionAsString = this.view.requestUserInput();

            try {
                Integer selection = Integer.parseInt(selectionAsString);

                if (selection.equals(selectionOfExitPresenter)) {
                    this.view.display("From the ground they come,\n");
                    this.view.display("and to it, they return.\n");
                    break;
                }

                this.presenter.setSelection(selection);
                String[] parameterLabels = this.presenter.getParameterLabels();
                String[] userInput = new String[parameterLabels.length];

                for (int index = 0; index < parameterLabels.length; index++) {
                    this.view.display(parameterLabels[index]);
                    userInput[index] = this.view.requestUserInput();
                }

                String response = this.presenter.generateResponse(userInput);
                this.view.display(response);
                this.view.display("\n");
            } catch (NumberFormatException exception) {
                this.view.display("Action not supported\n");
            } catch (Exception exception) {
                System.err.println(exception);
                this.view.display("Something went wrong.");
                break;
            }
        }

        close();
    }

    private void addPresenters() {
        this.presenter.addPresenter(selectionOfExitPresenter, new ExitPresenter());

        this.presenter.addPresenter(1, new CreateDictionaryEntryPresenter(this.createDictionaryEntryUseCase));
        this.presenter.addPresenter(2, new DeleteDictionaryEntryPresenter(this.deleteDictionaryEntryUseCase));
        this.presenter.addPresenter(3, new UpdateDictionaryEntryPresenter(this.updateDictionaryEntryUseCase));
        this.presenter.addPresenter(4, new RetrieveAllDictionaryEntriesPresenter(this.retrieveAllDictionaryEntriesUseCase));
        this.presenter.addPresenter(5, new RetrieveDictionaryEntryExactMatchPresenter(this.retrieveDictionaryEntryExactMatchUseCase));
        this.presenter.addPresenter(6, new RetrieveDictionaryEntriesPartialMatchPresenter(this.retrieveDictionaryEntriesPartialMatchUseCase));
    }

    private void displayMenu() {
        String menu = this.presenter.generateMenu();
        this.view.display(menu);
    }

    private void close() {
        this.view.close();
    }
}
