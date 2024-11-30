package org.tomfoolery.core.dataproviders.repositories.users.abc;

import org.tomfoolery.core.dataproviders.repositories.abc.BaseRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;

public interface UserRepository<User extends BaseUser> extends BaseRepository<User, BaseUser.Id> {}
