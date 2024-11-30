package org.tomfoolery.core.usecases.documents.review;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.domain.documents.DocumentWithoutContent;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.util.Set;

public final class AddDocumentReviewUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentReviewUseCase.Request> {
    private static final @Unsigned int MIN_RATING = 0;
    private static final @Unsigned int MAX_RATING = 5;

    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull PatronRepository patronRepository;

    public static @NonNull AddDocumentReviewUseCase of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentReviewUseCase(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentReviewUseCase(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
        this.patronRepository = patronRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, PatronNotFoundException, DocumentISBNInvalidException, DocumentNotFoundException, RatingValueInvalidException, PatronRatingAlreadyExistsException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patron = this.getPatronFromAuthenticationToken(patronAuthenticationToken);

        val patronRating = request.getPatronRating();
        this.ensurePatronRatingIsValid(patronRating);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        this.ensurePatronRatingDoesNotExist(patron, documentId);

        val documentWithoutContent = this.getDocumentWithoutContentById(documentId);
        this.addPatronRatingToDocument(documentWithoutContent, patron, patronRating);

        this.documentRepository.save(documentWithoutContent);
        this.patronRepository.save(patron);
    }

    private @NonNull Patron getPatronFromAuthenticationToken(@NonNull AuthenticationToken patronAuthenticationToken) throws AuthenticationTokenInvalidException, PatronNotFoundException {
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);
        val patron = patronRepository.getById(patronId);

        if (patron == null)
            throw new PatronNotFoundException();

        return patron;
    }

    private void ensurePatronRatingIsValid(@Unsigned double patronRating) throws RatingValueInvalidException {
        if (patronRating < MIN_RATING || patronRating > MAX_RATING)
            throw new RatingValueInvalidException();
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private void ensurePatronRatingDoesNotExist(@NonNull Patron patron, Document.@NonNull Id documentId) throws PatronRatingAlreadyExistsException {
        val patronRatingsByDocumentIds = patron.getAudit().getRatingsByDocumentIds();

        if (patronRatingsByDocumentIds.containsKey(documentId))
            throw new PatronRatingAlreadyExistsException();
    }

    private @NonNull DocumentWithoutContent getDocumentWithoutContentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val documentWithoutContent = this.documentRepository.getByIdWithoutContent(documentId);

        if (documentWithoutContent == null)
            throw new DocumentNotFoundException();

        return documentWithoutContent;
    }

    private void addPatronRatingToDocument(@NonNull DocumentWithoutContent documentWithoutContent, @NonNull Patron patron, @Unsigned double rating) {
        val patronRatingsByDocumentIds = patron.getAudit().getRatingsByDocumentIds();

        val documentId = documentWithoutContent.getId();
        val documentRating = documentWithoutContent.getRating();

        patronRatingsByDocumentIds.put(documentId, rating);
        documentRating.add(rating);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
        @Unsigned double patronRating;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class PatronNotFoundException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class RatingValueInvalidException extends Exception {}
    public static class PatronRatingAlreadyExistsException extends Exception {}
}
