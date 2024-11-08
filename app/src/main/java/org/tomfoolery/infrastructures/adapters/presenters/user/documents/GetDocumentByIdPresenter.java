package org.tomfoolery.infrastructures.adapters.presenters.user.documents;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

@NoArgsConstructor(staticName = "of")
public final class GetDocumentByIdPresenter implements Presenter<GetDocumentByIdUseCase.Response, GetDocumentByIdPresenter.ViewModel> {
    @Override
    public @NonNull ViewModel apply(GetDocumentByIdUseCase.@NonNull Response responseModel) {
        val documentPreview = responseModel.getDocumentPreview();
        val viewonlyDocumentPreview = ViewableDocumentPreview.of(documentPreview);

        return ViewModel.of(viewonlyDocumentPreview);
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {
        @NonNull
        ViewableDocumentPreview viewableDocumentPreview;
    }
}
