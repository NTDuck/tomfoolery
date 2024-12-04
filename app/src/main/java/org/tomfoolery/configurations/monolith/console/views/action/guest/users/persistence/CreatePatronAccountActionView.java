package org.tomfoolery.configurations.monolith.console.views.action.guest.users.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.usecases.guest.users.persistence.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.persistence.CreatePatronAccountController;

import java.util.Arrays;

public final class CreatePatronAccountActionView extends BaseActionView {
    private final @NonNull CreatePatronAccountController createPatronAccountController;

    public static @NonNull CreatePatronAccountActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreatePatronAccountActionView(ioProvider, patronRepository, passwordEncoder);
    }

    private CreatePatronAccountActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.createPatronAccountController = CreatePatronAccountController.of(patronRepository, passwordEncoder);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.createPatronAccountController.accept(requestObject);
            this.onSuccess();

        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException exception) {
            this.onException(exception, Message.Hint.USERNAME, Message.Hint.PASSWORD);
        } catch (PasswordMismatchException | DayOfBirthInvalidException | MonthOfBirthInvalidException | YearOfBirthInvalidException | CreatePatronAccountUseCase.PatronAlreadyExistsException exception) {
            this.onException(exception);
        }
    }

    private CreatePatronAccountController.@NonNull RequestObject collectRequestObject() throws PasswordMismatchException, DayOfBirthInvalidException, MonthOfBirthInvalidException, YearOfBirthInvalidException {
        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.collectPassword();

        val firstName = this.ioProvider.readLine(Message.Format.PROMPT, "first name");
        val lastName = this.ioProvider.readLine(Message.Format.PROMPT, "last name");
        val dayOfBirth = this.collectDayOfBirth();
        val monthOfBirth = this.collectMonthOfBirth();
        val yearOfBirth = this.collectYearOfBirth();
        val phoneNumber = this.ioProvider.readLine(Message.Format.PROMPT, "phone number");
        val city = this.ioProvider.readLine(Message.Format.PROMPT, "city");
        val country = this.ioProvider.readLine(Message.Format.PROMPT, "country");
        val email = this.ioProvider.readLine(Message.Format.PROMPT, "email");

        return CreatePatronAccountController.RequestObject.of(username, password, firstName, lastName, dayOfBirth, monthOfBirth, yearOfBirth, phoneNumber, city, country, email);
    }

    private char @NonNull [] collectPassword() throws PasswordMismatchException {
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");
        val repeatPassword = this.ioProvider.readPassword("Re-enter %s: ", "password");

        if (!Arrays.equals(password, repeatPassword))
            throw new PasswordMismatchException();

        return password;
    }

    private @Unsigned int collectDayOfBirth() throws DayOfBirthInvalidException {
        val rawDayOfBirth = this.ioProvider.readLine(Message.Format.PROMPT, "day of birth");

        try {
            return Integer.parseInt(rawDayOfBirth);
        } catch (NumberFormatException exception) {
            throw new DayOfBirthInvalidException();
        }
    }

    private @Unsigned int collectMonthOfBirth() throws MonthOfBirthInvalidException {
        val rawMonthOfBirth = this.ioProvider.readLine(Message.Format.PROMPT, "month of birth");

        try {
            return Integer.parseInt(rawMonthOfBirth);
        } catch (NumberFormatException exception) {
            throw new MonthOfBirthInvalidException();
        }
    }

    private @Unsigned int collectYearOfBirth() throws YearOfBirthInvalidException {
        val rawYearOfBirth = this.ioProvider.readLine(Message.Format.PROMPT, "year of birth");

        try {
            return Integer.parseInt(rawYearOfBirth);
        } catch (NumberFormatException exception) {
            throw new YearOfBirthInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron account created");
    }

    public static class DayOfBirthInvalidException extends Exception {}
    public static class MonthOfBirthInvalidException extends Exception {}
    public static class YearOfBirthInvalidException extends Exception {}
    public static class PasswordMismatchException extends Exception {}
}
