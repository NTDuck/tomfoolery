package org.tomfoolery.core.dataproviders.repositories.relations;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepositoryTest;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.relations.BorrowingSessionMocker;

import static org.testng.Assert.*;

public abstract class BorrowingSessionRepositoryTest extends BaseBiRepositoryTest<BorrowingSession, BorrowingSession.Id, Document.Id, Patron.Id> {
    @Override
    protected abstract @NonNull BorrowingSessionRepository createTestSubject();

    @Override
    protected @NonNull EntityMocker<BorrowingSession, BorrowingSession.Id> createEntityMocker() {
        return BorrowingSessionMocker.of();
    }
}