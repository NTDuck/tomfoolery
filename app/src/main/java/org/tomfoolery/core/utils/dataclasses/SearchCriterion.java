package org.tomfoolery.core.utils.dataclasses;

import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

@Value(staticConstructor = "of")
public class SearchCriterion<AttributeName extends Enum<AttributeName>, AttributeValue> {
    @NonNull SearchMethod searchMethod;
    @NonNull AttributeName attributeName;
    @NonNull AttributeValue attributeValue;

    public enum SearchMethod {
        FULL_TEXT,
        PREFIX,
        SUFFIX,
    }
}
