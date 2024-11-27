package org.tomfoolery.core.usecases.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.util.Set;

public final class RemoveDocumentRatingUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentRatingUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull RemoveDocumentRatingUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentRatingUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentRatingUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, PatronRatingNotFoundException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val documentId = request.getDocumentId();
        val document = this.getDocumentFromId(documentId);
        val patronRating = this.getPatronRatingFromDocument(patron, documentId);

        this.removePatronRatingFromDocument(document, patron, patronRating);

        this.documentRepository.save(document);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private @Unsigned double getPatronRatingFromDocument(@NonNull Patron patron, Document.@NonNull Id documentId) throws PatronRatingNotFoundException {
        val patronRatingsByDocumentIds = patron.getAudit().getRatingsByDocumentIds();
        val patronRating = patronRatingsByDocumentIds.get(documentId);

        if (patronRating == null)
            throw new PatronRatingNotFoundException();

        return patronRating;
    }

    private @NonNull Document getDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private void removePatronRatingFromDocument(@NonNull Document document, @NonNull Patron patron, @Unsigned double patronRating) {
        val patronAudit = patron.getAudit();
        val patronRatingValues = patronAudit.getRatingsByDocumentIds();

        val documentId = document.getId();
        val documentAudit = document.getAudit();
        val documentRating = documentAudit.getRating();

        patronRatingValues.remove(documentId);
        documentRating.removeRating(patronRating);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class PatronRatingNotFoundException extends Exception {}
}
