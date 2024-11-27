package org.tomfoolery.configurations.monolith.console.utils.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class Message {
    public static final @NonNull String USERNAME_CONSTRAINT = "Username must be 8-16 characters long; contains only lowercase letters, digits, and underscores; must not start with a digit or end with an underscore.";
    public static final @NonNull String PASSWORD_CONSTRAINT = "Password must be 8-32 characters long; contains only letters, digits, and underscores.";

    @NoArgsConstructor(access = AccessLevel.NONE)
    public static final class Format {
        public static final @NonNull String SUCCESS = "Success: %s.";
        public static final @NonNull String ERROR = "Error: %s.";
        public static final @NonNull String PROMPT = "Enter %s: ";
    }
}
