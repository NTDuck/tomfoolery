package org.tomfoolery.infrastructures.adapters.guest.auth;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.usecases.external.guest.auth.CreatePatronAccountUseCase;

import java.util.function.Function;

@NoArgsConstructor(staticName = "of")
public class CreatePatronAccountAdapter implements Function<CreatePatronAccountAdapter.RequestObject, CreatePatronAccountUseCase.Request> {
    @Override
    public CreatePatronAccountUseCase.@NonNull Request apply(@NonNull RequestObject requestObject) {
        val username = requestObject.getUsername();
        val password = requestObject.getPassword();

        val credentials = Patron.Credentials.of(username, password);

        val firstName = requestObject.getFirstName();
        val lastName = requestObject.getLastName();
        val address = requestObject.getAddress();
        val gmail = requestObject.getGmail();

        val fullName = getFullName(firstName, lastName);
        val metadata = Patron.Metadata.of(fullName, address, gmail);

        return CreatePatronAccountUseCase.Request.of(credentials, metadata);
    }

    private static @NonNull String getFullName(@NonNull String firstName, @NonNull String lastName) {
        return firstName + " " + lastName;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;

        @NonNull String firstName;
        @NonNull String lastName;
        @NonNull String address;
        @NonNull String gmail;
    }
}
