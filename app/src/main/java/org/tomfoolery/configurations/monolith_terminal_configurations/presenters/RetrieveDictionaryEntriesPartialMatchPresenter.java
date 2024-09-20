//package org.tomfoolery.configurations.monolith_terminal_configurations.presenters;
//
//import org.tomfoolery.core.domain.ReadonlyDictionaryEntry;
//import org.tomfoolery.core.usecases.RetrieveDictionaryEntriesPartialMatchUseCase;
//
//import java.util.List;
//
//public class RetrieveDictionaryEntriesPartialMatchPresenter extends DictionaryEntryPresenter {
//    private final RetrieveDictionaryEntriesPartialMatchUseCase retrieveDictionaryEntriesPartialMatchUseCase;
//
//    public RetrieveDictionaryEntriesPartialMatchPresenter(RetrieveDictionaryEntriesPartialMatchUseCase retrieveDictionaryEntriesPartialMatchUseCase) {
//        super("Search", "word");
//        this.retrieveDictionaryEntriesPartialMatchUseCase = retrieveDictionaryEntriesPartialMatchUseCase;
//    }
//
//    @Override
//    public String generateResponse(String[] userInput) {
//        assert(userInput.length == this.getParameterLabels().length);
//
//        String partialHeadword = userInput[0];
//
//        List<ReadonlyDictionaryEntry> dictionaryEntries = this.retrieveDictionaryEntriesPartialMatchUseCase.invoke(partialHeadword);
//
//        return new StringBuilder()
//                .append("Found ")
//                .append(dictionaryEntries.size())
//                .append(" entries:\n")
//                .append(generateStringFromDictionaryEntries(dictionaryEntries))
//                .toString();
//    }
//}
