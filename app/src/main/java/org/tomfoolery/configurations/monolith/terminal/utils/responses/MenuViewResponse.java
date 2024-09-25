package org.tomfoolery.configurations.monolith.terminal.utils.responses;

import lombok.Value;

@Value(staticConstructor = "of")
public class MenuViewResponse {
    int selection;
}
