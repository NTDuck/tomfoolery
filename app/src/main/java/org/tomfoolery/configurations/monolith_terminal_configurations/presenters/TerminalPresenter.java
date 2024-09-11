package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;

import org.tomfoolery.configurations.monolith_terminal_configurations.contracts.TerminalContract;
import org.tomfoolery.core.domain.DictionaryEntry;
import org.tomfoolery.core.repositories.DictionaryEntryRepository;
import org.tomfoolery.core.usecases.AddDictionaryEntryUseCase;
import org.tomfoolery.core.usecases.GetDictionaryEntryUseCase;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TerminalPresenter implements TerminalContract.Presenter {
    private static record UserAction(
        Function<String[], String> responseResolver,
        String labelForUserActionSelection,
        String... labelsForUserActionInputs
    ) {}

    private record UserActionManager(UserAction... userActions) {
        public Optional<UserAction> getUserAction(String userActionSelection) {
            try {
                return Optional.ofNullable(this.userActions[Integer.valueOf(userActionSelection)]);
            } catch (Exception exception) {
                return Optional.empty();
            }
        }

        public String getMenuTextString() {
            StringBuilder menuStringBuilder = new StringBuilder()
                .append("Welcome to My Application!\n");

            for (int index = 0; index < userActions.length; ++index)
                menuStringBuilder
                    .append("[")
                    .append(index)
                    .append("] ")
                    .append(userActions[index].labelForUserActionSelection)
                    .append("\n");

            return menuStringBuilder.toString();
        }
    }

    private final TerminalContract.View view;

    private final UserActionManager userActionManager;
    private String userActionSelection;

    private final AddDictionaryEntryUseCase addDictionaryEntryUseCase;
    private final GetDictionaryEntryUseCase getDictionaryEntryUseCase;

    public TerminalPresenter(TerminalContract.View view, DictionaryEntryRepository dictionaryEntryRepository) {
        this.view = view;

        this.addDictionaryEntryUseCase = new AddDictionaryEntryUseCase(dictionaryEntryRepository);
        this.getDictionaryEntryUseCase = new GetDictionaryEntryUseCase(dictionaryEntryRepository);

        this.userActionManager = new UserActionManager(
            new UserAction((String[] userInputs) -> {
                this.view.onDestroy();
                return "Thank you for listening\n";
            }, "Exit"),

            new UserAction((String[] userInputs) -> {
                String headword = userInputs[0];
                String definition = userInputs[1];

                this.addDictionaryEntryUseCase.invoke(
                    new DictionaryEntry(headword, List.of(definition))
                );

                return "";
            }, "Add", "word: ", "definition: "),

            new UserAction((String[] userInputs) -> {
                String headword = userInputs[0];
                Optional<DictionaryEntry> dictionaryEntryOptional = this.getDictionaryEntryUseCase.invoke(headword);

                return dictionaryEntryOptional.isEmpty()
                        ? "No definition found"
                        : String.join("\n", dictionaryEntryOptional.get().definitions);
            }, "Get", "word: ")
        );
    }

    @Override
    public void onStart() {
        this.view.display(this.userActionManager.getMenuTextString());
    }

    @Override
    public void onUserActionSelection(String userActionSelection) {
        this.userActionSelection = userActionSelection;
        Optional<UserAction> userAction = this.userActionManager.getUserAction(this.userActionSelection);

        if (userAction.isEmpty()) {
            this.view.display("Invalid argument");
            return;
        }

        String[] labelsForUserActionInputs = userAction.get().labelsForUserActionInputs;
        this.view.requestUserActionInputs(labelsForUserActionInputs);
    }

    @Override
    public void onUserInputs(String[] userInputs) {
        this.userActionSelection = userActionSelection;
        Optional<UserAction> userAction = this.userActionManager.getUserAction(this.userActionSelection);

        if (userAction.isEmpty()) {
            this.view.display("Invalid argument");
            return;
        }

        String response = userAction.get().responseResolver.apply(userInputs);
        this.view.display(response);
    }
}
