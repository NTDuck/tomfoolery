package org.tomfoolery.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

public class User extends ReadonlyUser {
    public User(@NonNull Credentials credentials, @NonNull Audit audit, @NonNull Timestamps timestamps) {
        super(credentials, audit, timestamps);
    }
    
    @Override
    public @NonNull Timestamps getTimestamps() {
        return (Timestamps) super.getTimestamps();
    }

    @Getter @Setter
    public static class Timestamps extends ReadonlyUser.Timestamps {
        private @NonNull LocalDateTime lastModified;

        public Timestamps(@NonNull LocalDateTime lastLogin, @NonNull LocalDateTime lastLogout, @NonNull LocalDateTime lastModified) {
            super(lastLogin, lastLogout);
            this.lastModified = lastModified;
        }
    }
}
