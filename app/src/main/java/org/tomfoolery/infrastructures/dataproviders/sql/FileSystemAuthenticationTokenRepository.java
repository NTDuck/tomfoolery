package org.tomfoolery.infrastructures.dataproviders.sql;

import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.io.*;

@NoArgsConstructor(staticName = "of")
public class FileSystemAuthenticationTokenRepository implements AuthenticationTokenRepository {
    private File tokenFile;

    public FileSystemAuthenticationTokenRepository(@NonNull String filePath) {
        this.tokenFile = new File(filePath);
    }

    @Override
    public void saveToken(@NonNull AuthenticationToken token) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(tokenFile);
            fileWriter.write(token.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeToken() {
        if (tokenFile.exists()) { tokenFile.delete(); }
    }

    @Override
    public @Nullable AuthenticationToken getToken() {
        return null;
    }
}
