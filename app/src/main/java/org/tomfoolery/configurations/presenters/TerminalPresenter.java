package org.tomfoolery.configurations.presenters;

import org.tomfoolery.configurations.contracts.TerminalContract;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.AddDictionaryEntryUseCase;
import org.tomfoolery.core.usecases.GetDictionaryEntryUseCase;
import org.tomfoolery.infrastructures.adapters.DictionaryEntryAdapter;

public class TerminalPresenter implements TerminalContract.Presenter {
    private final TerminalContract.View view;

    private final AddDictionaryEntryUseCase addDictionaryEntryUseCase;
    private final GetDictionaryEntryUseCase getDictionaryEntryUseCase;

    public TerminalPresenter(DictionaryEntryRepository dictionaryEntryRepository) {
        this.view = new Object();
        this.addDictionaryEntryUseCase = new AddDictionaryEntryUseCase(dictionaryEntryRepository);
        this.getDictionaryEntryUseCase = new GetDictionaryEntryUseCase(dictionaryEntryRepository);
    }

    @Override
    public void onUserInput(String userInput) {
        if (userInput.startsWith("/add")) {
            addDictionaryEntryUseCase.invoke(DictionaryEntryAdapter.toDictionaryEntry(userInput));
        } else if (userInput.startsWith("/get")) {
            getDictionaryEntryUseCase.invoke(DictionaryEntryAdapter.getHeadword(userInput));
        } else if (userInput.startsWith("/quit")) {
            view.onDestroy();
        }
    }
}
