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
import org.tomfoolery.core.utils.dataclasses.documents.AverageRating;

import java.util.Set;

public final class AddDocumentRatingUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentRatingUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull AddDocumentRatingUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentRatingUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentRatingUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentNotFoundException, RatingValueInvalidException, PatronRatingAlreadyExistsException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val patronRating = request.getPatronRating();
        this.ensurePatronRatingIsValid(patronRating);

        val documentId = request.getDocumentId();
        this.ensurePatronRatingDoesNotExist(patron, documentId);

        val document = this.getDocumentFromId(documentId);
        this.addPatronRatingToDocument(document, patron, patronRating);

        this.documentRepository.save(document);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken staffAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(staffAuthenticationToken);

        if (patronId == null)
            throw new AuthenticationTokenInvalidException();

        val patron = patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensurePatronRatingIsValid(@Unsigned double patronRating) throws RatingValueInvalidException {
        if (!AverageRating.isValid(patronRating))
            throw new RatingValueInvalidException();
    }

    private void ensurePatronRatingDoesNotExist(@NonNull Patron patron, Document.@NonNull Id documentId) throws PatronRatingAlreadyExistsException {
        val patronRatingsByDocumentIds = patron.getAudit().getRatingsByDocumentIds();

        if (patronRatingsByDocumentIds.containsKey(documentId))
            throw new PatronRatingAlreadyExistsException();
    }

    private @NonNull Document getDocumentFromId(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.documentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private void addPatronRatingToDocument(@NonNull Document document, @NonNull Patron patron, @Unsigned double rating) {
        val patronRatingsByDocumentIds = patron.getAudit().getRatingsByDocumentIds();

        val documentId = document.getId();
        val documentRating = document.getAudit().getRating();

        patronRatingsByDocumentIds.put(documentId, rating);
        documentRating.addRating(rating);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        Document.@NonNull Id documentId;
        @Unsigned double patronRating;
    }

    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class RatingValueInvalidException extends Exception {}
    public static class PatronRatingAlreadyExistsException extends Exception {}
}
