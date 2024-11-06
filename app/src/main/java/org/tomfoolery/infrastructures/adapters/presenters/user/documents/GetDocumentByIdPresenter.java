package org.tomfoolery.infrastructures.adapters.presenters.user.documents;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.usecases.user.documents.GetDocumentByIdUseCase;
import org.tomfoolery.infrastructures.adapters.presenters.abc.DocumentPreviewPresenter;
import org.tomfoolery.infrastructures.utils.contracts.Presenter;

@NoArgsConstructor(staticName = "of")
public class GetDocumentByIdPresenter extends DocumentPreviewPresenter implements Presenter<GetDocumentByIdUseCase.Response, GetDocumentByIdPresenter.ViewModel> {
    @Override
    public @NonNull ViewModel apply(GetDocumentByIdUseCase.@NonNull Response responseModel) {
        val documentPreview = responseModel.getDocumentPreview();
        val viewModel = generateViewModelFromDocumentPreview(documentPreview);

        return viewModel;
    }
}
