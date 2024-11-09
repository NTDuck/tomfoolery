package org.tomfoolery.configurations.monolith.terminal.views.selection.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.auth.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.search.SearchDocumentsByCriterionActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class UserSelectionView extends BaseSelectionView {
    private static final List<SelectionItem> DEFAULT_SELECTION_ITEMS = List.of(
        SelectionItem.of(0, "Exit", null),
        SelectionItem.of(1, "Log out", LogUserOutActionView.class),
        SelectionItem.of(2, "Get Document info", GetDocumentByIdActionView.class),
        SelectionItem.of(3, "Show a Document's QRCode", GetDocumentQrCodeActionView.class),
        SelectionItem.of(4, "Get Document recommendation", GetDocumentRecommendationActionView.class),
        SelectionItem.of(5, "Search Documents", SearchDocumentsByCriterionActionView.class),
        SelectionItem.of(6, "Show all Documents", ShowDocumentsActionView.class)
    );

    private static final @NonNull Map<Class<? extends BaseUser>, Class<? extends UserSelectionView>> USER_CLASS_TO_VIEW_CLASS_MAP = Map.of(
        Administrator.class, AdministratorSelectionView.class,
        Patron.class, PatronSelectionView.class,
        Staff.class, StaffSelectionView.class
    );

    public static @NonNull Class<? extends UserSelectionView> getUserSelectionViewClassFromUserClass(@NonNull Class<? extends BaseUser> userClass) {
        return USER_CLASS_TO_VIEW_CLASS_MAP.get(userClass);
    }

    private static @NonNull List<SelectionItem> getCombinedItems(@NonNull List<SelectionItem> additionalSelectionItems) {
        return Stream.concat(DEFAULT_SELECTION_ITEMS.stream(), additionalSelectionItems.stream()).toList();
    }

    protected UserSelectionView(@NonNull IOHandler ioHandler, @NonNull List<SelectionItem> additionalSelectionItems) {
        super(ioHandler, getCombinedItems(additionalSelectionItems));
    }
}
