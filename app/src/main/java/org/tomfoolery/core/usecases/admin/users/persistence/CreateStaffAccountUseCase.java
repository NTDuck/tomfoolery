package org.tomfoolery.core.usecases.admin.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.verifiers.auth.security.CredentialsVerifier;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class CreateStaffAccountUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<CreateStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull CreateStaffAccountUseCase of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountUseCase(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private CreateStaffAccountUseCase(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffCredentialsInvalidException, StaffAlreadyExistsException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);
        val administratorId = this.getUserIdFromAuthenticationToken(administratorAuthenticationToken);

        val rawStaffCredentials = request.getRawStaffCredentials();
        this.ensureStaffCredentialsAreValid(rawStaffCredentials);
        this.ensureStaffDoesNotExist(rawStaffCredentials);

        val encodedStaffCredentials = this.encodeStaffCredentials(rawStaffCredentials);
        val staff = this.createStaffAndMarkAsCreatedByAdministrator(encodedStaffCredentials, administratorId);

        this.staffRepository.save(staff);
    }

    private void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials rawStaffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verify(rawStaffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private void ensureStaffDoesNotExist(Staff.@NonNull Credentials staffCredentials) throws StaffAlreadyExistsException {
        val staffUsername = staffCredentials.getUsername();

        if (this.staffRepository.contains(staffUsername))
            throw new StaffAlreadyExistsException();
    }

    private Staff.@NonNull Credentials encodeStaffCredentials(Staff.@NonNull Credentials rawStaffCredentials) {
        val rawStaffPassword = rawStaffCredentials.getPassword();
        val encodedStaffPassword = this.passwordEncoder.encode(rawStaffPassword);

        return rawStaffCredentials.withPassword(encodedStaffPassword);
    }

    private @NonNull Staff createStaffAndMarkAsCreatedByAdministrator(Staff.@NonNull Credentials encodedStaffCredentials, Administrator.@NonNull Id administratorId) {
        val staffId = Staff.Id.of(UUID.randomUUID());
        val staffAuditTimestamps = Staff.Audit.Timestamps.of(Instant.now());
        val staffAudit = Staff.Audit.of(staffAuditTimestamps, administratorId);

        return Staff.of(staffId, staffAudit, encodedStaffCredentials);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Credentials rawStaffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffAlreadyExistsException extends Exception {}
}
