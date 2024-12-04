package org.tomfoolery.configurations.monolith.console.utils.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class Message {
    @NoArgsConstructor(access = AccessLevel.NONE)
    public static final class Format {
        public static final @NonNull String SUCCESS = "Success: %s.";
        public static final @NonNull String ERROR = "Error: %s.";
        public static final @NonNull String PROMPT = "Enter %s: ";
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static final class Hint {
        public static final @NonNull String USERNAME = "Username must be 8-16 characters long; contains only lowercase letters, digits, and underscores; must not start with a digit or end with an underscore.";
        public static final @NonNull String PASSWORD = "Password must be 8-32 characters long; contains only letters, digits, and underscores.";
    }

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static final class Page {
        public static final @Unsigned int MAX_PAGE_SIZE = 7;
    }
}
