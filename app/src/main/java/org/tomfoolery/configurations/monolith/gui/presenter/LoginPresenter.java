package org.tomfoolery.configurations.monolith.gui.presenter;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

@RequiredArgsConstructor(staticName = "of")
public class LoginPresenter {
    private @NonNull AuthenticationTokenService authenticationTokenService;

    private String userClassToFXMLPath(Class<? extends ReadonlyUser> userCLass) {
        if (userCLass.equals(Administrator.class)) {
            return "/fxml/Admin/Dashboard.fxml";
        } else if (userCLass.equals(Staff.class)) {
            return "/fxml/Staff/Dashboard.fxml";
        } else return "/fxml/Patron/Dashboard.fxml";
    }

    public String getMainMenuPathFromResponseModel(LogUserInUseCase.@NonNull Response responseModel) throws LogUserInUseCase.CredentialsInvalidException {
        AuthenticationToken authenticationToken = responseModel.getAuthenticationToken();
        String path = userClassToFXMLPath(authenticationTokenService.getUserClassFromToken(authenticationToken));
        if (authenticationTokenService.verifyToken(authenticationToken)) {
            return path;
        } else {
            throw new LogUserInUseCase.CredentialsInvalidException();
        }
    }
}
