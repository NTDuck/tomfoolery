package org.tomfoolery.core.utils.structs;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;

@Value(staticConstructor = "of")
public class UserAndRepository<User extends ReadonlyUser> {
    @NonNull User user;
    @NonNull UserRepository<User> userRepository;
}
