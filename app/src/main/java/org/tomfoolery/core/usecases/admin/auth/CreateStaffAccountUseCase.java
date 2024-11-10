package org.tomfoolery.core.usecases.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

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
        val administratorId = this.getAdministratorIdFromAuthenticationToken(administratorAuthenticationToken);

        val rawStaffCredentials = request.getRawStaffCredentials();
        this.ensureStaffCredentialsAreValid(rawStaffCredentials);
        this.ensureStaffDoesNotExist(rawStaffCredentials);

        val encodedStaffCredentials = this.passwordEncoder.encodeCredentials(rawStaffCredentials);
        val staff = this.createStaffAndMarkAsCreatedByAdministrator(encodedStaffCredentials, administratorId);

        this.staffRepository.save(staff);
    }

    private Administrator.@NonNull Id getAdministratorIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val administratorId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (administratorId == null)
            throw new AuthenticationTokenInvalidException();

        return administratorId;
    }

    private void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials rawStaffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(rawStaffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private void ensureStaffDoesNotExist(Staff.@NonNull Credentials staffCredentials) throws StaffAlreadyExistsException {
        val staffUsername = staffCredentials.getUsername();

        if (this.staffRepository.contains(staffUsername))
            throw new StaffAlreadyExistsException();
    }

    private @NonNull Staff createStaffAndMarkAsCreatedByAdministrator(Staff.@NonNull Credentials encodedStaffCredentials, Administrator.@NonNull Id administratorId) {
        val staffId = Staff.Id.of(UUID.randomUUID());
        val staffAuditTimestamps = Staff.Audit.Timestamps.of(Instant.now());
        val staffAudit = Staff.Audit.of(false, staffAuditTimestamps, administratorId);

        return Staff.of(staffId, encodedStaffCredentials, staffAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Credentials rawStaffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffAlreadyExistsException extends Exception {}
}
