package org.tomfoolery.core.dataproviders.repositories.relations;

import org.tomfoolery.core.dataproviders.repositories.relations.abc.BaseBiRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.relations.BorrowingRecord;
import org.tomfoolery.core.domain.users.Patron;

public interface DocumentBorrowRecordRepository extends BaseBiRepository<BorrowingRecord, BorrowingRecord.Id, Document.Id, Patron.Id> {

}
