package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.configurations.monolith_terminal_configurations.contracts.TerminalContract;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.AddDictionaryEntryUseCase;
import org.tomfoolery.core.usecases.GetDictionaryEntryUseCase;
import org.tomfoolery.infrastructures.adapters.DictionaryEntryAdapter;

import java.util.Optional;

public class TerminalPresenter implements TerminalContract.Presenter {
    private final TerminalContract.View view;

    private final AddDictionaryEntryUseCase addDictionaryEntryUseCase;
    private final GetDictionaryEntryUseCase getDictionaryEntryUseCase;

    public TerminalPresenter(TerminalContract.View view, DictionaryEntryRepository dictionaryEntryRepository) {
        this.view = view;

        this.addDictionaryEntryUseCase = new AddDictionaryEntryUseCase(dictionaryEntryRepository);
        this.getDictionaryEntryUseCase = new GetDictionaryEntryUseCase(dictionaryEntryRepository);
    }

    /**
     * This is a clear violation of SOLID. For demoing purposes only.
     */
    @Override
    public void onUserInput(String userInput) {
        if (userInput.startsWith("/add")) {
            addDictionaryEntryUseCase.invoke(DictionaryEntryAdapter.toDictionaryEntry(userInput));
            view.displayResponse("Word added!");
        } else if (userInput.startsWith("/get")) {
            Optional<DictionaryEntry> dictionaryEntry = getDictionaryEntryUseCase.invoke(DictionaryEntryAdapter.getHeadword(userInput));
            if (dictionaryEntry.isEmpty()) {
                view.displayResponse("Word not present in dictionary!");
            } else {
                view.displayResponse("The definition of headword " + dictionaryEntry.get().headword + " is: " + dictionaryEntry.get().definitions);
            }
        } else if (userInput.startsWith("/quit")) {
            view.onDestroy();
        }
    }
}
