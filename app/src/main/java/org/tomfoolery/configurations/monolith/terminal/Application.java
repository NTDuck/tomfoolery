package org.tomfoolery.configurations.monolith.terminal;

public class Application {
//    private final InMemoryDictionaryEntryRepository dictionaryEntryRepository = InMemoryDictionaryEntryRepository.of();
//
//    private final Dictionary dictionary = Dictionary.of(this.dictionaryEntryRepository);
//
//    private final MenuView menuView = MenuView.of();
//    private final AddDictionaryEntryView addDictionaryEntryView = AddDictionaryEntryView.of();
//    private final GetDictionaryEntryView getDictionaryEntryView = GetDictionaryEntryView.of();
//
//    public void start() {
//        while (true) {
//            val menuUserSubmission = this.menuView.waitForUserSubmission();
//
//            try {
//                val menuViewResponse = MenuViewAdapter.adapt(menuUserSubmission);
//                val selection = menuViewResponse.getSelection();
//
//                switch (selection) {
//                    case ActionLabel.EXIT -> { return; }
//
//                    case ActionLabel.ADD_DICTIONARY_ENTRY -> this.onAddDictionaryEntry();
//                    case ActionLabel.GET_DICTIONARY_ENTRY -> this.onGetDictionaryEntry();
//
//                    default -> throw new ValidationException();
//                }
//            } catch (ValidationException exception) {
//                this.menuView.onValidationException();
//                continue;
//            }
//        }
//    }
//
//    public void close() {
//        this.menuView.close();
//        this.addDictionaryEntryView.close();
//        this.getDictionaryEntryView.close();
//    }
//
//    private void onAddDictionaryEntry() {
//        val userSubmission = this.addDictionaryEntryView.waitForUserSubmission();
//        val useCaseRequest = AddDictionaryEntryViewAdapter.adapt(userSubmission);
//
//        try {
//            val useCaseResponse = this.dictionary.addEntry(useCaseRequest);
//            val viewResponse = AddDictionaryEntryViewAdapter.adapt(useCaseResponse);
//            this.addDictionaryEntryView.displayResponse(viewResponse);
//        } catch (AlreadyExistsException exception) {
//            this.addDictionaryEntryView.onAlreadyExistsException();
//        }
//    }
//
//    private void onGetDictionaryEntry() {
//        val userSubmission = this.getDictionaryEntryView.waitForUserSubmission();
//        val useCaseRequest = GetDictionaryEntryViewAdapter.adapt(userSubmission);
//
//        try {
//            val useCaseResponse = this.dictionary.getEntry(useCaseRequest);
//            val viewResponse = GetDictionaryEntryViewAdapter.adapt(useCaseResponse);
//            this.getDictionaryEntryView.displayResponse(viewResponse);
//        } catch (NotFoundException exception) {
//            this.getDictionaryEntryView.onNotFoundException();
//        }
//    }

    public static void main(String[] args) {
//        Application application = new Application();

//        application.start();
//        application.close();

        System.out.println("Changing requirements is crazy");
    }
}
