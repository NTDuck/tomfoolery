package org.tomfoolery.configurations.monolith.console.views.selection.abc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.dataclasses.SelectionItem;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.views.action.common.users.authentication.LogUserOutActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.GetDocumentByIdActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.retrieval.ShowDocumentsActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.recommendation.GetDocumentRecommendationActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.references.GetDocumentQrCodeActionView;
import org.tomfoolery.configurations.monolith.console.views.action.common.documents.search.SearchDocumentsActionView;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.AdministratorRepository;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class UserSelectionView extends BaseSelectionView {
    private static final List<SelectionItem> defaultSelectionItems = List.of(
        SelectionItem.of("Exit", null),
        SelectionItem.of("Log out", LogUserOutActionView.class),
        SelectionItem.of("Get Document details", GetDocumentByIdActionView.class),
        SelectionItem.of("Get Document QR code", GetDocumentQrCodeActionView.class),
        SelectionItem.of("Get Document recommendation", GetDocumentRecommendationActionView.class),
        SelectionItem.of("Search Documents", SearchDocumentsActionView.class),
        SelectionItem.of("Show all Documents", ShowDocumentsActionView.class)
    );

    protected UserSelectionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AdministratorRepository administratorRepository, @NonNull PatronRepository patronRepository, @NonNull StaffRepository staffRepository, @NonNull List<SelectionItem> additionalSelectionItems) {
        super(ioProvider, documentRepository, administratorRepository, patronRepository, staffRepository, getMergedSelectionItems(additionalSelectionItems));
    }
    private static @NonNull List<SelectionItem> getMergedSelectionItems(@NonNull List<SelectionItem> additionalSelectionItems) {
        return Stream.concat(defaultSelectionItems.stream(), additionalSelectionItems.stream())
            .collect(Collectors.toUnmodifiableList());
    }
}
