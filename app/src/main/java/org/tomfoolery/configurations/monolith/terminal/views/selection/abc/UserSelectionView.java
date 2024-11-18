package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search.SearchDocumentsActionView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class UserSelectionView extends BaseSelectionView {
    private static final List<SelectionItem> defaultSelectionItems = List.of(
        SelectionItem.of("Exit", null),
        SelectionItem.of("Log out", LogUserOutActionView.class),
        SelectionItem.of("Get Document info", GetDocumentByIdActionView.class),
        SelectionItem.of("Show a Document's QRCode", GetDocumentQrCodeActionView.class),
        SelectionItem.of("Get Document recommendation", GetDocumentRecommendationActionView.class),
        SelectionItem.of("Search Documents", SearchDocumentsActionView.class),
        SelectionItem.of("Show all Documents", ShowDocumentsActionView.class)
    );

    protected UserSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> additionalSelectionItems) {
        super(ioHandler, getMergedSelectionItems(additionalSelectionItems));
    }
    private static @NonNull List<SelectionItem> getMergedSelectionItems(@NonNull List<SelectionItem> additionalSelectionItems) {
        return Stream.concat(defaultSelectionItems.stream(), additionalSelectionItems.stream())
            .collect(Collectors.toUnmodifiableList());
    }
}
