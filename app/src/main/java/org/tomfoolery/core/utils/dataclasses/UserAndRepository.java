package org.tomfoolery.core.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

@Value(staticConstructor = "of")
public class UserAndRepository<User extends BaseUser> {
    @NonNull User user;
    @NonNull UserRepository<User> userRepository;
}
