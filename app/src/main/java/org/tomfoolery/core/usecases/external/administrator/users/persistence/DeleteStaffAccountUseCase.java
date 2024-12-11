package org.tomfoolery.core.usecases.external.administrator.users.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Set;

public final class DeleteStaffAccountUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<DeleteStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;

    public static @NonNull DeleteStaffAccountUseCase of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountUseCase(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountUseCase(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        
        this.staffRepository = staffRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Administrator.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, StaffNotFoundException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val staffId = request.getStaffId();
        this.ensureStaffExists(staffId);

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
