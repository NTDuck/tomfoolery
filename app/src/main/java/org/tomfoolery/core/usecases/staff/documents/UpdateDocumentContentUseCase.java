package org.tomfoolery.core.usecases.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.relations.DocumentContent;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Set;

public final class UpdateDocumentContentUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<UpdateDocumentContentUseCase.Request> {
   private final @NonNull DocumentRepository documentRepository;
   private final @NonNull DocumentContentRepository documentContentRepository;

   public static @NonNull UpdateDocumentContentUseCase of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
       return new UpdateDocumentContentUseCase(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
   }

   private UpdateDocumentContentUseCase(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
       super(authenticationTokenGenerator, authenticationTokenRepository);

       this.documentRepository = documentRepository;
       this.documentContentRepository = documentContentRepository;
   }

   @Override
   protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
       return Set.of(Staff.class);
   }

   @Override
   public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException {
       val staffAuthenticationToken = this.getAuthenticationTokenFromRepository();
       this.ensureAuthenticationTokenIsValid(staffAuthenticationToken);
       val staffId = this.getUserIdFromAuthenticationToken(staffAuthenticationToken);

       val documentISBN = request.getDocumentISBN();
       val documentId = this.getDocumentIdFromISBN(documentISBN);
       val document = this.getDocumentById(documentId);

       val newDocumentContent = request.getNewDocumentContent();
       this.documentContentRepository.save(newDocumentContent);

       this.markDocumentAsLastModifiedByStaff(document, staffId);
       this.documentRepository.save(document);
   }

   private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
       val documentId = Document.Id.of(documentISBN);

       if (documentId == null)
           throw new DocumentISBNInvalidException();

       return documentId;
   }

   private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
       val document = this.documentRepository.getById(documentId);

       if (document == null)
           throw new DocumentNotFoundException();

       return document;
   }

   private void markDocumentAsLastModifiedByStaff(@NonNull Document document, Staff.@NonNull Id staffId) {
       val documentAudit = document.getAudit();
       val documentAuditTimestamps = documentAudit.getTimestamps();

       documentAudit.setLastModifiedByStaffId(staffId);
       documentAuditTimestamps.setLastModified(Instant.now());
   }

   @Value(staticConstructor = "of")
   public static class Request {
       @NonNull String documentISBN;
       @NonNull DocumentContent newDocumentContent;
   }

   public static class DocumentISBNInvalidException extends Exception {}
   public static class DocumentNotFoundException extends Exception {}
}
