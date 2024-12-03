package org.tomfoolery.core.usecases.admin.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public final class ShowStaffAccountsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowStaffAccountsUseCase.Request, ShowStaffAccountsUseCase.Response> {
    private final @NonNull StaffRepository staffRepository;

    public static @NonNull ShowStaffAccountsUseCase of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowStaffAccountsUseCase(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowStaffAccountsUseCase(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.staffRepository = staffRepository;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedStaff = this.getPaginatedStaff(pageIndex, maxPageSize);
        return Response.of(paginatedStaff);
    }

    private @NonNull Page<Staff> getPaginatedStaff(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedStaff = this.staffRepository.showPaginated(pageIndex, maxPageSize);

        if (paginatedStaff == null)
            throw new PaginationInvalidException();

        return paginatedStaff;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Staff> paginatedStaff;
    }

    public static class PaginationInvalidException extends Exception {}
}
