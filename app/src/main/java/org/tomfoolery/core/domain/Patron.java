package org.tomfoolery.core.domain;

import lombok.NonNull;

import java.util.Collection;
import java.util.HashSet;

public class Patron extends User {
    private final @NonNull Collection<Document.ID> borrowedDocumentIds = new HashSet<>();
}
