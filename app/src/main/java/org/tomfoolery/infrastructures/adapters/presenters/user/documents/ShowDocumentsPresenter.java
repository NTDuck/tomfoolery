package org.tomfoolery.infrastructures.adapters.presenters.user.documents;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.user.documents.ShowDocumentsUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(staticName = "of")
public final class ShowDocumentsPresenter implements Presenter<ShowDocumentsUseCase.Response, ShowDocumentsPresenter.ViewModel> {
    @Override
    public ShowDocumentsPresenter.@NonNull ViewModel apply(ShowDocumentsUseCase.@NonNull Response response) {
        val paginatedDocumentPreviews = response.getPaginatedFragmentaryDocuments();

        val pageIndex = paginatedDocumentPreviews.getPageIndex();
        val maxPageIndex = paginatedDocumentPreviews.getMaxPageIndex();

        val viewonlyDocumentPreviews = StreamSupport
            .stream(paginatedDocumentPreviews.spliterator(), false)
            .map(ViewableDocumentPreview::of)
            .collect(Collectors.toUnmodifiableList());

        return ViewModel.of(pageIndex, maxPageIndex, viewonlyDocumentPreviews);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        int pageIndex;
        int maxPageIndex;

        List<ViewableDocumentPreview> viewableDocumentPreviews;
    }
}
