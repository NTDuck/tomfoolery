package org.tomfoolery.infrastructures.dataproviders.repositories.localsql;

import java.sql.*;

public class DatabaseInitializer {
    private final String dbUrl;

    public DatabaseInitializer(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Connection connect() {
        try {
            return DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database", e);
        }
    }

    public void initializeDatabase() {
        // Create tables using a transaction to ensure atomicity
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                // Books table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS books (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        isbn TEXT UNIQUE,
                        quantity INTEGER DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // Members table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS members (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE,
                        membership_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // Loans table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS loans (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        book_id INTEGER,
                        member_id INTEGER,
                        loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        due_date TIMESTAMP,
                        return_date TIMESTAMP,
                        FOREIGN KEY (book_id) REFERENCES books(id),
                        FOREIGN KEY (member_id) REFERENCES members(id)
                    )
                """);

                // Create indexes for better query performance
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_members_email ON members(email)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_loans_book_id ON loans(book_id)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_loans_member_id ON loans(member_id)");

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to initialize database tables", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error", e);
        }
    }

    // Custom exception class for database-related errors
    public static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Utility method to verify database integrity
    public boolean verifyDatabaseIntegrity() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("PRAGMA integrity_check");
            return rs.next() && "ok".equalsIgnoreCase(rs.getString(1));

        } catch (SQLException e) {
            throw new DatabaseException("Failed to verify database integrity", e);
        }
    }

    // Method to clear all data (useful for testing)
    public void clearDatabase() {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM loans");
                stmt.execute("DELETE FROM members");
                stmt.execute("DELETE FROM books");
                // Reset autoincrement counters
                stmt.execute("DELETE FROM sqlite_sequence");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabaseException("Failed to clear database", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database connection error during clear", e);
        }
    }
}