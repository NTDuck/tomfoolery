package org.tomfoolery.core.utils.dataclasses.users;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.abc.UserRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;

@Value(staticConstructor = "of")
public class UserAndRepository<User extends BaseUser> {
    @NonNull User user;
    @NonNull UserRepository<User> userRepository;
}
