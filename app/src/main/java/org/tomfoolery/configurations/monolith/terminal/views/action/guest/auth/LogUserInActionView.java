package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.infrastructures.adapters.guest.auth.LogUserInAdapter;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInActionView implements ActionView {
    private final @NonNull LogUserInAdapter controller;

    @Override
    public void run() {
        val requestObject = this.getRequestObject();

    }

    private LogUserInAdapter.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerService.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        return LogUserInAdapter.RequestObject.of(username, password);
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return GuestSelectionView.class;
    }
}
