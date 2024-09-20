//package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;
//
//import org.tomfoolery.core.domain.ReadonlyDictionaryEntry;
//import org.tomfoolery.core.usecases.CreateDictionaryEntryUseCase;
//
//public class CreateDictionaryEntryPresenter extends DictionaryEntryPresenter {
//    private final CreateDictionaryEntryUseCase createDictionaryEntryUseCase;
//
//    public CreateDictionaryEntryPresenter(CreateDictionaryEntryUseCase createDictionaryEntryUseCase) {
//        super("Add", "word", "definitions");
//        this.createDictionaryEntryUseCase = createDictionaryEntryUseCase;
//    }
//
//    @Override
//    public String generateResponse(String[] userInput) {
//        assert(userInput.length == this.getParameterLabels().length);
//
//        String headword = userInput[0];
//        String[] definitions = userInput[1].split("\\|");
//
//        ReadonlyDictionaryEntry dictionaryEntry = generateDictionaryEntry(headword, definitions);
//
//        this.createDictionaryEntryUseCase.invoke(headword, dictionaryEntry);
//
//        return new StringBuilder()
//            .append("Added word \"")
//            .append(headword)
//            .append("\" with ")
//            .append(definitions.length)
//            .append(" definition(s)")
//            .toString();
//    }
//}
