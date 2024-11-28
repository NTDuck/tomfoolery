package org.tomfoolery.infrastructures.dataproviders.repositories.cloud.users;

import org.tomfoolery.core.dataproviders.repositories.auth.abc.UserRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

public abstract class UserCloudRepository<User extends BaseUser> implements UserRepository<User> {
}
