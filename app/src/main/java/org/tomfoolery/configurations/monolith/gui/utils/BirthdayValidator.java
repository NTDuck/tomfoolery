package org.tomfoolery.configurations.monolith.gui.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BirthdayValidator {
    public static void validateBirthday(String birthday) throws MonthOfBirthInvalidException, DayOfBirthInvalidException, YearOfBirthInvalidException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate date;
        try {
            date = LocalDate.parse(birthday, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected MM/dd/yyyy.");
        }

        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        int year = date.getYear();

        if (month < 1 || month > 12) {
            throw new MonthOfBirthInvalidException();
        }

        int maxDays = date.getMonth().length(date.isLeapYear());
        if (day < 1 || day > maxDays) {
            throw new DayOfBirthInvalidException();
        }

        int currentYear = LocalDate.now().getYear();
        if (year < 1900 || year > currentYear) {
            throw new YearOfBirthInvalidException();
        }
    }

    public static class BirthdayInvalidException extends Exception {}
    public static class DayOfBirthInvalidException extends Exception {}
    public static class MonthOfBirthInvalidException extends Exception {}
    public static class YearOfBirthInvalidException extends Exception {}
}
