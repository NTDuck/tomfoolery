package org.tomfoolery.core.usecases.external.admin;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.domain.abc.User;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.services.CredentialsVerificationService;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

@RequiredArgsConstructor(staticName = "of")
public class CreateStaffAccountUseCase implements ThrowableConsumer<CreateStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;

    private final @NonNull PasswordService passwordService;
    private final @NonNull AuthenticationTokenService authenticationTokenService;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public void accept(@NonNull Request request) throws AdminAuthenticationTokenNotFoundException, AdminAuthenticationTokenInvalidException, StaffCredentialsInvalidException, StaffAlreadyExistsException {
        val staffCredentials = request.getStaffCredentials();

        val adminAuthenticationToken = getAdminAuthenticationTokenFromRepository();
        ensureAdminAuthenticationTokenIsValid(adminAuthenticationToken);
        val administratorId = getAdministratorIdFromAuthenticationToken(adminAuthenticationToken);

        ensureStaffCredentialsAreValid(staffCredentials);
        ensureStaffDoesNotExist(staffCredentials);
        encodeStaffPassword(staffCredentials);

        val staff = createStaffAndMarkAsCreatedByAdmin(staffCredentials, administratorId);
        staffRepository.save(staff);
    }

    private @NonNull AuthenticationToken getAdminAuthenticationTokenFromRepository() throws AdminAuthenticationTokenNotFoundException {
        val adminAuthenticationToken = this.authenticationTokenRepository.getToken();

        if (adminAuthenticationToken == null)
            throw new AdminAuthenticationTokenNotFoundException();

        return adminAuthenticationToken;
    }

    private void ensureAdminAuthenticationTokenIsValid(@NonNull AuthenticationToken adminAuthenticationToken) throws AdminAuthenticationTokenInvalidException {
        if (!this.authenticationTokenService.verifyToken(adminAuthenticationToken, Administrator.class))
            throw new AdminAuthenticationTokenInvalidException();
    }

    private Administrator.@NonNull Id getAdministratorIdFromAuthenticationToken(@NonNull AuthenticationToken adminAuthenticationToken) throws AdminAuthenticationTokenInvalidException {
        val administratorId = this.authenticationTokenService.getUserIdFromToken(adminAuthenticationToken);

        if (administratorId == null)
            throw new AdminAuthenticationTokenInvalidException();

        return administratorId;
    }

    private static void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials staffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerificationService.verifyCredentials(staffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private void ensureStaffDoesNotExist(Staff.@NonNull Credentials staffCredentials) throws StaffAlreadyExistsException {
        val staffUsername = staffCredentials.getUsername();

        if (this.staffRepository.contains(staffUsername))
            throw new StaffAlreadyExistsException();
    }

    private void encodeStaffPassword(Staff.@NonNull Credentials staffCredentials) {
        val password = staffCredentials.getPassword();
        val encodedPassword = this.passwordService.encodePassword(password);
        staffCredentials.setPassword(encodedPassword);
    }

    private static @NonNull Staff createStaffAndMarkAsCreatedByAdmin(Staff.@NonNull Credentials staffCredentials, Administrator.@NonNull Id administratorId) {
        val staffAudit = Staff.Audit.of(administratorId, Staff.Audit.Timestamps.of());
        return Staff.of(staffCredentials, staffAudit);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Credentials staffCredentials;
    }

    public static class AdminAuthenticationTokenNotFoundException extends Exception {}
    public static class AdminAuthenticationTokenInvalidException extends Exception {}
    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffAlreadyExistsException extends Exception {}
}
