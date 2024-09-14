package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.usecases.UpdateDictionaryEntryUseCase;

public class UpdateDictionaryEntryPresenter extends DictionaryEntryPresenter {
    private final UpdateDictionaryEntryUseCase updateDictionaryEntryUseCase;

    public UpdateDictionaryEntryPresenter(UpdateDictionaryEntryUseCase updateDictionaryEntryUseCase) {
        super("Update", "word", "definitions");
        this.updateDictionaryEntryUseCase = updateDictionaryEntryUseCase;
    }

    @Override
    public String generateResponse(String[] userInput) {
        assert(userInput.length == this.getParameterLabels().length);

        String headword = userInput[0];
        String[] definitions = userInput[1].split("\\|");

        DictionaryEntry dictionaryEntry = generateDictionaryEntry(headword, definitions);

        this.updateDictionaryEntryUseCase.invoke(headword, dictionaryEntry);

        return new StringBuilder()
                .append("Updated word \"")
                .append(headword)
                .append("\" with ")
                .append(definitions.length)
                .append(" definition(s)")
                .toString();
    }
}
