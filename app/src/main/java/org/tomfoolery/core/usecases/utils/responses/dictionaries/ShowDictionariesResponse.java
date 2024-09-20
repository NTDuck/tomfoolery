package org.tomfoolery.core.usecases.utils.responses.dictionaries;

import lombok.NonNull;
import lombok.Value;
import org.tomfoolery.core.domain.Dictionary;

import java.util.List;

@Value(staticConstructor = "of")
public class ShowDictionariesResponse {
    @NonNull List<Dictionary> dictionaries;
}
