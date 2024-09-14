package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.core.usecases.DeleteDictionaryEntryUseCase;

public class DeleteDictionaryEntryPresenter extends DictionaryEntryPresenter {
    private final DeleteDictionaryEntryUseCase deleteDictionaryEntryUseCase;

    public DeleteDictionaryEntryPresenter(DeleteDictionaryEntryUseCase deleteDictionaryEntryUseCase) {
        super("Remove", "word");
        this.deleteDictionaryEntryUseCase = deleteDictionaryEntryUseCase;
    }

    @Override
    public String generateResponse(String[] userInput) {
        assert(userInput.length == this.getParameterLabels().length);

        String headword = userInput[0];

        this.deleteDictionaryEntryUseCase.invoke(headword);

        return "Deleted word \"" + headword + "\"";
    }
}
