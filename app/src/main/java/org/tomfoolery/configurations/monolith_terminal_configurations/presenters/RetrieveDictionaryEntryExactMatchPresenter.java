package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.usecases.RetrieveDictionaryEntryExactMatchUseCase;

import java.util.Optional;

public class RetrieveDictionaryEntryExactMatchPresenter extends DictionaryEntryPresenter {
    private final RetrieveDictionaryEntryExactMatchUseCase retrieveDictionaryEntryExactMatchUseCase;

    public RetrieveDictionaryEntryExactMatchPresenter(RetrieveDictionaryEntryExactMatchUseCase retrieveDictionaryEntryExactMatchUseCase) {
        super("Lookup", "word");
        this.retrieveDictionaryEntryExactMatchUseCase = retrieveDictionaryEntryExactMatchUseCase;
    }

    @Override
    public String generateResponse(String[] userInput) {
        assert(userInput.length == this.getParameterLabels().length);

        String headword = userInput[0];

        Optional<DictionaryEntry> dictionaryEntry = this.retrieveDictionaryEntryExactMatchUseCase.invoke(headword);

        return dictionaryEntry.map(DictionaryEntryPresenter::generateStringFromDictionaryEntry)
            .orElse("Word \"" + headword + "\" not found");
    }
}
