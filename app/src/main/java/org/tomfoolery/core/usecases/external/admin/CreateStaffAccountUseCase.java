package org.tomfoolery.core.usecases.external.admin;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.PasswordEncoder;
import org.tomfoolery.core.dataproviders.StaffRepository;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.utils.function.ThrowableConsumer;
import org.tomfoolery.core.utils.validators.CredentialsValidator;

@RequiredArgsConstructor(staticName = "of")
public class CreateStaffAccountUseCase implements ThrowableConsumer<CreateStaffAccountUseCase.Request> {
    private final @NonNull StaffRepository staffRepository;
    private final @NonNull PasswordEncoder passwordEncoder;

    @Override
    public void accept(@NonNull Request request) throws ValidationException, StaffAlreadyExistsException {
        val staffCredentials = request.getStaffCredentials();
        
        if (!CredentialsValidator.isCredentialsValid(staffCredentials))
            throw new ValidationException();

        val staffUsername = staffCredentials.getUsername();
        if (this.staffRepository.contains(staffUsername))
            throw new StaffAlreadyExistsException();

        this.passwordEncoder.encode(staffCredentials);

        val adminId = request.getAdmin().getId();
        val staffAudit = Staff.Audit.of(adminId);

        val staff = Staff.of(staffCredentials, staffAudit);
        staffRepository.save(staff);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull Administrator admin;
        Staff.@NonNull Credentials staffCredentials;
    }

    public static class ValidationException extends Exception {}
    public static class StaffAlreadyExistsException extends Exception {}
}
