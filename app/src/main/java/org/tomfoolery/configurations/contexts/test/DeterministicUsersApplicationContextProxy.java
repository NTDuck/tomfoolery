package org.tomfoolery.configurations.contexts.test;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.contexts.abc.ApplicationContext;
import org.tomfoolery.configurations.contexts.test.abc.ApplicationContextProxy;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.AdministratorMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.StaffMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.abc.UserMocker;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
public class DeterministicUsersApplicationContextProxy implements ApplicationContextProxy {
    private static final List<Administrator.Credentials> ADMINISTRATOR_CREDENTIALS = List.of(
        Administrator.Credentials.of("admin_123", SecureString.of("Root_123"))
    );

    private static final List<Staff.Credentials> STAFF_CREDENTIALS = List.of(
        Staff.Credentials.of("staff_123", SecureString.of("Root_123"))
    );

    private static final List<Patron.Credentials> PATRON_CREDENTIALS = List.of(
        Patron.Credentials.of("patron_123", SecureString.of("Root_123"))
    );

    private final @NonNull Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final @NonNull AdministratorMocker administratorMocker = AdministratorMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();
    private final @NonNull StaffMocker staffMocker = StaffMocker.of();

    @Override
    public @NonNull CompletableFuture<Void> intercept(@NonNull ApplicationContext applicationContext) {
        return CompletableFuture.allOf(
            this.populateAdministratorRepository(applicationContext),
            this.populatePatronRepository(applicationContext),
            this.populateStaffRepository(applicationContext)
        );
    }

    private @NonNull CompletableFuture<Void> populateAdministratorRepository(@NonNull ApplicationContext applicationContext) {
        val administratorRepository = applicationContext.getAdministratorRepository();
        val passwordEncoder = applicationContext.getPasswordEncoder();

        return this.populateUserRepository(administratorRepository, passwordEncoder, this.administratorMocker, ADMINISTRATOR_CREDENTIALS);
    }

    private @NonNull CompletableFuture<Void> populatePatronRepository(@NonNull ApplicationContext applicationContext) {
        val patronRepository = applicationContext.getPatronRepository();
        val passwordEncoder = applicationContext.getPasswordEncoder();

        return this.populateUserRepository(patronRepository, passwordEncoder, this.patronMocker, PATRON_CREDENTIALS);
    }

    private @NonNull CompletableFuture<Void> populateStaffRepository(@NonNull ApplicationContext applicationContext) {
        val staffRepository = applicationContext.getStaffRepository();
        val passwordEncoder = applicationContext.getPasswordEncoder();

        return this.populateUserRepository(staffRepository, passwordEncoder, this.staffMocker, STAFF_CREDENTIALS);
    }

    private <User extends BaseUser> @NonNull CompletableFuture<Void> populateUserRepository(@NonNull UserRepository<User> userRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull UserMocker<User> userMocker, List<User.Credentials> rawCredentials) {
        val futures = rawCredentials.parallelStream()
            .map(credentials -> CompletableFuture
                .supplyAsync(() -> encodeRawCredentials(passwordEncoder, credentials), this.executor)
                .thenApply(encodedCredentials -> createMockUserWithEncodedCredentials(userMocker, encodedCredentials))
                .thenAccept(userRepository::save))
            .collect(Collectors.toUnmodifiableList());

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static <User extends BaseUser> User.@NonNull Credentials encodeRawCredentials(@NonNull PasswordEncoder passwordEncoder, User.@NonNull Credentials rawCredentials) {
        val rawPassword = rawCredentials.getPassword();
        val encodedPassword = passwordEncoder.encode(rawPassword);

        return rawCredentials.withPassword(encodedPassword);
    }

    private <User extends BaseUser> User createMockUserWithEncodedCredentials(@NonNull UserMocker<User> userMocker, User.@NonNull Credentials encodedCredentials) {
        val mockUserId = userMocker.createMockEntityId();
        return userMocker.createMockUserWithIdAndCredentials(mockUserId, encodedCredentials);
    }
}
