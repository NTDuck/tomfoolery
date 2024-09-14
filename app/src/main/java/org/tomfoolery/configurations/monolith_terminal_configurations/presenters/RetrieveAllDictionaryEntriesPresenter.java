package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.usecases.RetrieveAllDictionaryEntriesUseCase;

import java.util.List;

public class RetrieveAllDictionaryEntriesPresenter extends DictionaryEntryPresenter {
    private final RetrieveAllDictionaryEntriesUseCase retrieveAllDictionaryEntriesUseCase;

    public RetrieveAllDictionaryEntriesPresenter(RetrieveAllDictionaryEntriesUseCase retrieveAllDictionaryEntriesUseCase) {
        super("Display");
        this.retrieveAllDictionaryEntriesUseCase = retrieveAllDictionaryEntriesUseCase;
    }

    @Override
    public String generateResponse(String[] userInput) {
        assert(userInput.length == this.getParameterLabels().length);

        List<DictionaryEntry> dictionaryEntries = this.retrieveAllDictionaryEntriesUseCase.invoke();

        return new StringBuilder()
            .append("Found ")
            .append(dictionaryEntries.size())
            .append(" entries:\n")
            .append(generateStringFromDictionaryEntries(dictionaryEntries))
            .toString();
    }
}
