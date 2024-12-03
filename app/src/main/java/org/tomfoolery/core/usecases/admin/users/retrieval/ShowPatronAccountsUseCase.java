package org.tomfoolery.core.usecases.admin.users.retrieval;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.Page;

public final class ShowPatronAccountsUseCase extends AuthenticatedUserUseCase implements ThrowableFunction<ShowPatronAccountsUseCase.Request, ShowPatronAccountsUseCase.Response> {
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull ShowPatronAccountsUseCase of(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ShowPatronAccountsUseCase(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ShowPatronAccountsUseCase(@NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.patronRepository = patronRepository;
    }

    @Override
    public @NonNull Response apply(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PaginationInvalidException {
        val administratorAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(administratorAuthenticationToken);

        val pageIndex = request.getPageIndex();
        val maxPageSize = request.getMaxPageSize();

        val paginatedPatrons = this.getPaginatedPatrons(pageIndex, maxPageSize);
        return Response.of(paginatedPatrons);
    }

    private @NonNull Page<Patron> getPaginatedPatrons(@Unsigned int pageIndex, @Unsigned int maxPageSize) throws PaginationInvalidException {
        val paginatedPatrons = this.patronRepository.showPaginated(pageIndex, maxPageSize);

        if (paginatedPatrons == null)
            throw new PaginationInvalidException();

        return paginatedPatrons;
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @Unsigned int pageIndex;
        @Unsigned int maxPageSize;
    }

    @Value(staticConstructor = "of")
    public static class Response {
        @NonNull Page<Patron> paginatedPatrons;
    }

    public static class PaginationInvalidException extends Exception {}
}
