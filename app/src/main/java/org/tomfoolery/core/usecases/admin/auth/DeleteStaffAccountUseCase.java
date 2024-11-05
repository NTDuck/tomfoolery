package org.tomfoolery.core.usecases.admin.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Collection;
import java.util.List;

public final class DeleteStaffAccountUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<DeleteStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;

    public static @NonNull DeleteStaffAccountUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository) {
        return new DeleteStaffAccountUseCase(authenticationTokenGenerator, authenticationTokenRepository, staffRepository);
    }

    private DeleteStaffAccountUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull StaffRepository staffRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.staffRepository = staffRepository;
    }

    @Override
    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffNotFoundException {
        val administratorAuthenticationToken = getAuthenticationTokenFromRepository();
        ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val staffId = request.getStaffId();
        ensureStaffExists(staffId);

        this.staffRepository.delete(staffId);
    }

    private void ensureStaffExists(Staff.@NonNull Id staffId) throws StaffNotFoundException {
        if (!this.staffRepository.contains(staffId))
            throw new StaffNotFoundException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Staff.@NonNull Id staffId;
    }

    public static class StaffNotFoundException extends Exception {}
}
