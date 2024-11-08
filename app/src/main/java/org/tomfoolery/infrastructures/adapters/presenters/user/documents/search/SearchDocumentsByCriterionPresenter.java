package org.tomfoolery.infrastructures.adapters.presenters.user.documents.search;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.user.abc.SearchDocumentsByCriterionUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(staticName = "of")
public final class SearchDocumentsByCriterionPresenter implements Presenter<SearchDocumentsByCriterionUseCase.Response, SearchDocumentsByCriterionPresenter.ViewModel> {
    @Override
    public @NonNull ViewModel apply(SearchDocumentsByCriterionUseCase.@NonNull Response response) {
        val paginatedDocumentPreviews = response.getPaginatedDocumentPreviews();

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

