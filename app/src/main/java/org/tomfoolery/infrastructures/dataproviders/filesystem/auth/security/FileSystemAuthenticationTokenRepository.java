package org.tomfoolery.infrastructures.dataproviders.filesystem.auth.security;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@NoArgsConstructor(staticName = "of")
public class FileSystemAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private File tokenFile;

    public FileSystemAuthenticationTokenRepository(@NonNull String filePath) {
        this.tokenFile = new File(filePath);
    }

    @Override
    public void saveAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(tokenFile);
            fileWriter.write(authenticationToken.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuthenticationToken() {
        if (tokenFile.exists()) { tokenFile.delete(); }
    }

    @Override
    public @Nullable AuthenticationToken getAuthenticationToken() {
        return null;
    }
}
