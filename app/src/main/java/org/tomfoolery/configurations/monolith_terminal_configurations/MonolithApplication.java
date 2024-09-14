package org.tomfoolery.configurations.monolith_terminal_configurations;

import org.tomfoolery.configurations.monolith_terminal_configurations.controllers.MainController;
import org.tomfoolery.infrastructures.repositories.InMemoryDictionaryEntryRepository;

public class MonolithApplication {
    private final InMemoryDictionaryEntryRepository dictionaryEntryRepository;
    private final MainController mainController;

    private MonolithApplication() {
        this.dictionaryEntryRepository = new InMemoryDictionaryEntryRepository();
        this.mainController = new MainController(this.dictionaryEntryRepository);
    }

    private void start() {
        this.mainController.start();
    }

    public static void main(String[] args) {
        MonolithApplication application = new MonolithApplication();
        application.start();
    }
}
