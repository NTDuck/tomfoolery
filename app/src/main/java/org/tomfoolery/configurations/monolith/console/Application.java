package org.tomfoolery.configurations.monolith.console;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.resources.PopulatedApplicationResources;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByAuthenticationTokenActionView;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull PopulatedApplicationResources resources = PopulatedApplicationResources.of();

    @Override
    public void run() {
        val views = this.resources.getViews();
        BaseView view;
        Class<? extends BaseView> viewClass = LogUserInByAuthenticationTokenActionView.class;

        do {
            view = views.getViewByClass(viewClass);
            assert view != null;   // Expected

            view.run();
            viewClass = view.getNextViewClass();

        } while (viewClass != null);
    }

    @Override
    public void close() throws Exception {
        this.resources.close();
    }

    @SneakyThrows
    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
