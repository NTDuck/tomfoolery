package org.tomfoolery.core.usecases.external.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.auth.StaffRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class CreateStaffAccountUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<CreateStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull CreateStaffAccountUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountUseCase(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    private CreateStaffAccountUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffCredentialsInvalidException, StaffAlreadyExistsException {
        val administratorAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(administratorAuthenticationToken);
        val administratorId = getAdministratorIdFromAuthenticationToken(administratorAuthenticationToken);

        val staffCredentials = request.getStaffCredentials();

        ensureStaffCredentialsAreValid(staffCredentials);
        ensureStaffDoesNotExist(staffCredentials);
        encodeStaffPassword(staffCredentials);

        val staff = createStaffAndMarkAsCreatedByAdmin(staffCredentials, administratorId);

        this.staffRepository.save(staff);
    }

    private Administrator.@NonNull Id getAdministratorIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val administratorId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (administratorId == null)
            throw new AuthenticationTokenInvalidException();

        return administratorId;
    }

    private static void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials staffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verify(staffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private void ensureStaffDoesNotExist(Staff.@NonNull Credentials staffCredentials) throws StaffAlreadyExistsException {
        val staffUsername = staffCredentials.getUsername();

        if (this.staffRepository.contains(staffUsername))
            throw new StaffAlreadyExistsException();
    }

    private void encodeStaffPassword(Staff.@NonNull Credentials staffCredentials) {
        val password = staffCredentials.getPassword();
        val encodedPassword = this.passwordEncoder.encode(password);
        staffCredentials.setPassword(encodedPassword);
    }

    private static @NonNull Staff createStaffAndMarkAsCreatedByAdmin(Staff.@NonNull Credentials staffCredentials, Administrator.@NonNull Id administratorId) {
        val staffId = Staff.Id.of(UUID.randomUUID());
        val staffAuditTimestamps = Staff.Audit.Timestamps.of(Instant.now());
        val staffAudit = Staff.Audit.of(false, staffAuditTimestamps, administratorId);

        return Staff.of(staffId, staffCredentials, staffAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Credentials staffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffAlreadyExistsException extends Exception {}
}
