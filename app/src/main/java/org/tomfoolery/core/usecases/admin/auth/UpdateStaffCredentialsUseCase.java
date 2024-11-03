package org.tomfoolery.core.usecases.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public final class UpdateStaffCredentialsUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateStaffCredentialsUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull UpdateStaffCredentialsUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsUseCase(authenticationTokenGenerator, authenticationTokenRepository, staffRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffCredentialsInvalidException, StaffNotFoundException {
        val administratorAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(administratorAuthenticationToken);
        val administratorId = getAdministratorIdFromAuthenticationToken(administratorAuthenticationToken);

        val newStaffCredentials = request.getNewStaffCredentials();
        ensureStaffCredentialsAreValid(newStaffCredentials);
        encodeStaffPassword(newStaffCredentials);

        val staffId = request.getStaffId();
        val staff = getStaffFromId(staffId);

        updateStaffCredentialsAndMarkAsLastModifiedByAdministrator(staff, newStaffCredentials, administratorId);

        this.staffRepository.save(staff);
    }

    private Administrator.@NonNull Id getAdministratorIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val administratorId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (administratorId == null)
            throw new AuthenticationTokenInvalidException();

        return administratorId;
    }

    private static void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials staffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(staffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private void encodeStaffPassword(Staff.@NonNull Credentials staffCredentials) {
        val password = staffCredentials.getPassword();
        val encodedPassword = this.passwordEncoder.encodePassword(password);
        staffCredentials.setPassword(encodedPassword);
    }

    private @NonNull Staff getStaffFromId(Staff.@NonNull Id staffId) throws StaffNotFoundException {
        val staff = this.staffRepository.getById(staffId);

        if (staff == null)
            throw new StaffNotFoundException();

        return staff;
    }

    private static void updateStaffCredentialsAndMarkAsLastModifiedByAdministrator(@NonNull Staff staff, Staff.@NonNull Credentials newStaffCredentials, Administrator.@NonNull Id administratorId) {
        staff.setCredentials(newStaffCredentials);

        val staffAudit = staff.getAudit();
        val staffAuditTimestamps = staffAudit.getTimestamps();

        staffAudit.setLastModifiedByAdminId(administratorId);
        staffAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Id staffId;
        Staff.@NonNull Credentials newStaffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffNotFoundException extends Exception {}
}
