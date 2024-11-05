package org.tomfoolery.configurations.monolith.terminal.views.action.user.documents.recommendation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;

public class GetDocumentRecommendationActionView implements ActionView {
    @Override
    public void run() {

    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return null;
    }
}
