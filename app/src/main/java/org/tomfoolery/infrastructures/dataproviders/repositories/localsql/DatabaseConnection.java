package org.tomfoolery.infrastructures.dataproviders.repositories.localsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DatabaseConnection {
    static final @NonNull String DB_URL = "jdbc:sqlite:library.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection established successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
