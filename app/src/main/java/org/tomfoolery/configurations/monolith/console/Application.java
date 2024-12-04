package org.tomfoolery.configurations.monolith.console;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.utils.resources.ApplicationResources;
import org.tomfoolery.configurations.monolith.console.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication.LogUserInByAuthenticationTokenActionView;

@NoArgsConstructor(staticName = "of")
public class Application implements Runnable, AutoCloseable {
    private final @NonNull ApplicationResources resources = ApplicationResources.of();

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

    //
    // private void populate() {
    //     this.populateUserRepositories();
    // }
    //
    // private void populateUserRepositories() {
    //     this.administratorRepository.save(Administrator.of(
    //         BaseUser.Id.of(UUID.randomUUID()),
    //         BaseUser.Credentials.of("admin_123", this.passwordEncoder.encode(SecureString.of("Root_123"))),
    //         BaseUser.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH))
    //     ));
    //
    //     this.patronRepository.save(Patron.of(
    //         BaseUser.Id.of(UUID.randomUUID()),
    //         BaseUser.Credentials.of("patron_123", this.passwordEncoder.encode(SecureString.of("Root_123"))),
    //         Patron.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH)),
    //         Patron.Metadata.of("", "", "")
    //     ));
    //
    //     this.staffRepository.save(Staff.of(
    //         BaseUser.Id.of(UUID.randomUUID()),
    //         BaseUser.Credentials.of("staff_123", this.passwordEncoder.encode(SecureString.of("Root_123"))),
    //         Staff.Audit.of(ModifiableUser.Audit.Timestamps.of(Instant.EPOCH), Administrator.Id.of(UUID.randomUUID()))
    //     ));
    // }

    @SneakyThrows
    public static void main(String[] args) {
        val application = Application.of();

        application.run();
        application.close();
    }
}
