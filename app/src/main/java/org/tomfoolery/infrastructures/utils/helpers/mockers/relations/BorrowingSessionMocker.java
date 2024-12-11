package org.tomfoolery.infrastructures.utils.helpers.mockers.relations;

import lombok.NoArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.relations.BorrowingSession;
import org.tomfoolery.core.usecases.external.patron.documents.borrow.persistence.BorrowDocumentUseCase;
import org.tomfoolery.infrastructures.utils.helpers.mockers.abc.EntityMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.common.TimestampsMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.documents.DocumentMocker;
import org.tomfoolery.infrastructures.utils.helpers.mockers.users.PatronMocker;

@NoArgsConstructor(staticName = "of")
public class BorrowingSessionMocker implements EntityMocker<BorrowingSession, BorrowingSession.Id> {
    private final @NonNull DocumentMocker documentMocker = DocumentMocker.of();
    private final @NonNull PatronMocker patronMocker = PatronMocker.of();

    private final @NonNull TimestampsMocker timestampsMocker = TimestampsMocker.of();

    @Override
    public BorrowingSession.@NonNull Id createMockEntityId() {
        val documentId = this.documentMocker.createMockEntityId();
        val patronId = this.patronMocker.createMockEntityId();

        return BorrowingSession.Id.of(documentId, patronId);
    }

    @Override
    public @NonNull BorrowingSession createMockEntityWithId(BorrowingSession.@NonNull Id borrowingSessionId) {
        val borrowedTimestamp = this.timestampsMocker.createMockTimestamps();
        val dueTimestamp = borrowedTimestamp.plus(BorrowDocumentUseCase.BORROWING_PERIOD);

        return BorrowingSession.of(
            borrowingSessionId,
            borrowedTimestamp, dueTimestamp
        );
    }
}
