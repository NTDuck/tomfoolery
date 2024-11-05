package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search.SearchDocumentsActionView;

import java.util.List;
import java.util.stream.Stream;

public abstract class UserSelectionView extends SelectionView {
    private static final List<SelectionItem> DEFAULT_SELECTION_ITEMS = List.of(
        SelectionItem.of(0, "Exit", null),
        SelectionItem.of(1, "Log out", LogUserOutActionView.class),
        SelectionItem.of(2, "Get Document info", GetDocumentByIdActionView.class),
        SelectionItem.of(3, "Show a Document's QRCode", GetDocumentQrCodeActionView.class),
        SelectionItem.of(4, "Get Document recommendation", GetDocumentRecommendationActionView.class),
        SelectionItem.of(5, "Search Documents", SearchDocumentsActionView.class),
        SelectionItem.of(6, "Show all Documents", ShowDocumentsActionView.class)
    );

    private static @NonNull List<SelectionItem> getCombinedItems(@NonNull List<SelectionItem> additionalSelectionItems) {
        return Stream.concat(DEFAULT_SELECTION_ITEMS.stream(), additionalSelectionItems.stream()).toList();
    }

    protected UserSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> additionalSelectionItems) {
        super(ioHandler, getCombinedItems(additionalSelectionItems));
    }
}
