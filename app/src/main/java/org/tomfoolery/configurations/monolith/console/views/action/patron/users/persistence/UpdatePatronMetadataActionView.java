package org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.users.persistence.UpdatePatronMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence.UpdatePatronMetadataController;

public final class UpdatePatronMetadataActionView extends UserActionView {
    private final @NonNull UpdatePatronMetadataController controller;

    public static @NonNull UpdatePatronMetadataActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdatePatronMetadataActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdatePatronMetadataActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = UpdatePatronMetadataController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdatePatronMetadataUseCase.AuthenticationTokenNotFoundException | UpdatePatronMetadataUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (DayOfBirthInvalidException | MonthOfBirthInvalidException | YearOfBirthInvalidException | UpdatePatronMetadataUseCase.PatronNotFoundException exception) {
            this.onException(exception);
        }
    }

    private UpdatePatronMetadataController.@NonNull RequestObject collectRequestObject() throws DayOfBirthInvalidException, MonthOfBirthInvalidException, YearOfBirthInvalidException {
        val firstName = this.ioProvider.readLine(Message.Format.PROMPT, "first name");
        val lastName = this.ioProvider.readLine(Message.Format.PROMPT, "last name");
        val dayOfBirth = this.collectDayOfBirth();
        val monthOfBirth = this.collectMonthOfBirth();
        val yearOfBirth = this.collectYearOfBirth();
        val phoneNumber = this.ioProvider.readLine(Message.Format.PROMPT, "phone number");
        val city = this.ioProvider.readLine(Message.Format.PROMPT, "city");
        val country = this.ioProvider.readLine(Message.Format.PROMPT, "country");
        val email = this.ioProvider.readLine(Message.Format.PROMPT, "email");

        return UpdatePatronMetadataController.RequestObject.of(firstName, lastName, dayOfBirth, monthOfBirth, yearOfBirth, phoneNumber, city, country, email);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron metadata updated");
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

    public static class DayOfBirthInvalidException extends Exception {}
    public static class MonthOfBirthInvalidException extends Exception {}
    public static class YearOfBirthInvalidException extends Exception {}
}
