package org.tomfoolery.configurations.monolith.terminal.views.selection;

import lombok.val;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.CreatePatronAccountActionView;
import org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth.LogUserInActionView;

public class GuestSelectionView implements SelectionView {
    private @Nullable Class<? extends ActionView> nextViewClass = null;

    @Override
    public void run() {
        System.out.println("""
            [0] Exit
            [1] Log in
            [2] Create Patron account
        """);

        try {
            val scanner = ScannerService.getScanner();
            int selection = scanner.nextInt();
            scanner.nextLine();

            switch (selection) {
                case 1:
                    this.nextViewClass = LogUserInActionView.class;
                    break;

                case 2:
                    this.nextViewClass = CreatePatronAccountActionView.class;
                    break;

                default:
                    this.nextViewClass = null;
                    break;
            }
        } catch (Exception exception) {}
    }

    @Override
    public @Nullable Class<? extends ActionView> getNextViewClass() {
        return this.nextViewClass;
    }
}
