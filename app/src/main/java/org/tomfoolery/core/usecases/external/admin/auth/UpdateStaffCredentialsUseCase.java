package org.tomfoolery.core.usecases.external.admin.auth;

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
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.CredentialsVerifier;

import java.util.Collection;
import java.util.List;

public class UpdateStaffCredentialsUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateStaffCredentialsUseCase.Request> {
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

        val administratorId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(administratorAuthenticationToken);
        assert administratorId != null;

        val newStaffCredentials = request.getNewStaffCredentials();
        ensureStaffCredentialsAreValid(newStaffCredentials);

        val staffId = request.getStaffId();
        val staff = getStaffFromId(staffId);

        encodeStaffPassword(newStaffCredentials);

        staff.setCredentials(newStaffCredentials);
        this.staffRepository.save(staff);
    }

    private static void ensureStaffCredentialsAreValid(Staff.@NonNull Credentials staffCredentials) throws StaffCredentialsInvalidException {
        if (!CredentialsVerifier.verify(staffCredentials))
            throw new StaffCredentialsInvalidException();
    }

    private @NonNull Staff getStaffFromId(Staff.@NonNull Id staffId) throws StaffNotFoundException {
        val staff = this.staffRepository.getById(staffId);

        if (staff == null)
            throw new StaffNotFoundException();

        return staff;
    }

    private void encodeStaffPassword(Staff.@NonNull Credentials staffCredentials) {
        val password = staffCredentials.getPassword();
        val encodedPassword = this.passwordEncoder.encode(password);
        staffCredentials.setPassword(encodedPassword);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Id staffId;
        Staff.@NonNull Credentials newStaffCredentials;
    }

    public static class StaffCredentialsInvalidException extends Exception {}
    public static class StaffNotFoundException extends Exception {}
}
