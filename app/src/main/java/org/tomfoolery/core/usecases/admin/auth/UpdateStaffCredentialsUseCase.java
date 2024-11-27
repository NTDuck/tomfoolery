package org.tomfoolery.core.usecases.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.helpers.auth.security.CredentialsVerifier;

import java.time.Instant;
import java.util.Set;

public final class UpdateStaffCredentialsUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateStaffCredentialsUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    public static @NonNull UpdateStaffCredentialsUseCase of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsUseCase(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsUseCase(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffCredentialsInvalidException, StaffNotFoundException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);
        val administratorId = this.getAdministratorIdFromAuthenticationToken(administratorAuthenticationToken);

        val rawNewStaffCredentials = request.getRawNewStaffCredentials();
        this.ensureStaffCredentialsAreValid(rawNewStaffCredentials);
        val encodedNewStaffCredentials = this.passwordEncoder.encodeCredentials(rawNewStaffCredentials);

        val staffId = request.getStaffId();
        val staff = this.getStaffFromId(staffId);

        this.updateStaffCredentialsAndMarkAsLastModifiedByAdministrator(staff, encodedNewStaffCredentials, administratorId);

        this.staffRepository.save(staff);
    }

    private Administrator.@NonNull Id getAdministratorIdFromAuthenticationToken(@NonNull AuthenticationToken administratorAuthenticationToken) throws AuthenticationTokenInvalidException {
        val administratorId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(administratorAuthenticationToken);

        if (administratorId == null)
            throw new AuthenticationTokenInvalidException();

        return administratorId;
    }

    private void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials rawStaffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verifyCredentials(rawStaffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private @NonNull Staff getStaffFromId(Staff.@NonNull Id staffId) throws StaffNotFoundException {
        val staff = this.staffRepository.getById(staffId);

        if (staff == null)
            throw new StaffNotFoundException();

        return staff;
    }

    private void updateStaffCredentialsAndMarkAsLastModifiedByAdministrator(@NonNull Staff staff, Staff.@NonNull Credentials encodedNewStaffCredentials, Administrator.@NonNull Id administratorId) {
        staff.setCredentials(encodedNewStaffCredentials);

        val staffAudit = staff.getAudit();
        val staffAuditTimestamps = staffAudit.getTimestamps();

        staffAudit.setLastModifiedByAdminId(administratorId);
        staffAuditTimestamps.setLastModified(Instant.now());
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Id staffId;
        Staff.@NonNull Credentials rawNewStaffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffNotFoundException extends Exception {}
}
