package org.tomfoolery.configurations.monolith.terminal;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.View;
import org.tomfoolery.configurations.monolith.terminal.utils.containers.Views;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;

import java.util.Collections;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull Views views = Views.of(Collections.emptyList());

    @Override
    public void run() {
        Class<? extends View> viewClass = GuestSelectionView.class;
        View view;

        do {
            view = this.views.getViewByClass(viewClass);
            assert view != null;   // Expected
            viewClass = view.getNextViewClass();
        } while (viewClass != null);
    }

    @Override
    public void close() {
        ScannerService.close();
    }

    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
