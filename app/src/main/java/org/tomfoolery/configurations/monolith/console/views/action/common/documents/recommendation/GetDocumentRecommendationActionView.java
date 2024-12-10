package org.tomfoolery.configurations.monolith.console.views.action.common.documents.recommendation;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.github.freva.asciitable.OverflowBehaviour;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.utils.helpers.EnumResolver;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.documents.recommendation.DocumentRecommendationGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.common.documents.recommendation.abc.GetDocumentRecommendationUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.recommendation.GetDocumentRecommendationController;
import org.tomfoolery.infrastructures.adapters.controllers.common.documents.retrieval.GetDocumentByIdController;

import java.util.List;
import java.util.stream.Collectors;

public final class GetDocumentRecommendationActionView extends UserActionView {
    private static final @NonNull String RECOMMENDATION_TYPE_PROMPT = String.format("recommendation type (%s)", EnumResolver.getCapitalizedEnumeratedNames(
        GetDocumentRecommendationController.RecommendationType.class, "%s (%d)", 0
    ).stream().collect(Collectors.joining(", ")));

    private final @NonNull GetDocumentRecommendationController getDocumentRecommendationController;

    public static @NonNull GetDocumentRecommendationActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetDocumentRecommendationActionView(ioProvider, documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetDocumentRecommendationActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentRecommendationGenerator documentRecommendationGenerator, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.getDocumentRecommendationController = GetDocumentRecommendationController.of(documentRepository, documentRecommendationGenerator, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            val viewModel = this.getDocumentRecommendationController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (RecommendationTypeIndexInvalidException exception) {
            this.onException(exception);

        } catch (GetDocumentRecommendationUseCase.AuthenticationTokenNotFoundException | GetDocumentRecommendationUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        }
    }

    private GetDocumentRecommendationController.@NonNull RequestObject collectRequestObject() throws RecommendationTypeIndexInvalidException {
        val recommendationType = this.collectRecommendationType();

        return GetDocumentRecommendationController.RequestObject.of(recommendationType);
    }

    private GetDocumentRecommendationController.@NonNull RecommendationType collectRecommendationType() throws RecommendationTypeIndexInvalidException {
        val rawRecommendationTypeIndex = this.ioProvider.readLine(Message.Format.PROMPT, RECOMMENDATION_TYPE_PROMPT);

        try {
            val recommendationTypeIndex = Integer.parseUnsignedInt(rawRecommendationTypeIndex);
            val recommendationType = GetDocumentRecommendationController.RecommendationType.values()[recommendationTypeIndex];

            return recommendationType;

        } catch (Exception exception) {
            throw new RecommendationTypeIndexInvalidException();
        }
    }

    private void onSuccess(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        this.nextViewClass = cachedViewClass;

        this.displayViewModel(viewModel);
    }

    private void displayViewModel(GetDocumentRecommendationController.@NonNull ViewModel viewModel) {
        val table = AsciiTable.builder()
            .border(AsciiTable.NO_BORDERS)
            .data(viewModel.getDocumentRecommendation(), List.of(
                new Column()
                    .header("ISBN 10")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetDocumentByIdController.ViewModel::getDocumentISBN_10),
                new Column()
                    .header("ISBN 13")
                    .headerAlign(HorizontalAlign.CENTER)
                    .with(GetDocumentByIdController.ViewModel::getDocumentISBN_13),
                new Column()
                    .header("Title")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(30, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(GetDocumentByIdController.ViewModel::getDocumentTitle),
                new Column()
                    .header("Authors")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(14, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(document -> String.join(", ", document.getDocumentAuthors())),
                new Column()
                    .header("Genres")
                    .headerAlign(HorizontalAlign.CENTER)
                    .maxWidth(14, OverflowBehaviour.CLIP_RIGHT)
                    .dataAlign(HorizontalAlign.LEFT)
                    .with(document -> String.join(", ", document.getDocumentGenres())),
                new Column()
                    .header("Year")
                    .headerAlign(HorizontalAlign.CENTER)
                    .dataAlign(HorizontalAlign.RIGHT)
                    .with(document -> String.valueOf(document.getDocumentPublishedYear()))
            ))
            .asString();

        this.ioProvider.writeLine(table);
    }

    private static class RecommendationTypeIndexInvalidException extends Exception {}
}
