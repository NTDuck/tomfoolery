package org.tomfoolery.configurations.monolith_terminal_configurations;

import org.tomfoolery.configurations.monolith_terminal_configurations.views.TerminalView;
import org.tomfoolery.infrastructures.repositories.InMemoryDictionaryEntryRepository;

public class MonolithApplication {
    private final TerminalView view;
    private final InMemoryDictionaryEntryRepository dictionaryEntryRepository;

    private MonolithApplication() {
        this.dictionaryEntryRepository = new InMemoryDictionaryEntryRepository();
        this.view = new TerminalView(this.dictionaryEntryRepository);
    }

    private void start() {
        this.view.start();
    }

    public static void main(String[] args) {
        MonolithApplication application = new MonolithApplication();
        application.start();
    }
}
