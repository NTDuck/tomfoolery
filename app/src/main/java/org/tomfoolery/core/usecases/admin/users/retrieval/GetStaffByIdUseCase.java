package org.tomfoolery.core.usecases.admin.users.retrieval;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.admin.users.retrieval.abc.GetUserByIdUseCase;

public final class GetStaffByIdUseCase extends GetUserByIdUseCase<Staff> {
    public static @NonNull GetStaffByIdUseCase of(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new GetStaffByIdUseCase(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private GetStaffByIdUseCase(@NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }
}

