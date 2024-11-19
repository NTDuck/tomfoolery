package org.tomfoolery.infrastructures.dataproviders.repositories.sql.local;

import org.testng.annotations.*;
import static org.testng.Assert.*;
import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseInitializationTest {
    private static final String TEST_DB_URL = "jdbc:sqlite:test_library.db";
    private Connection connection;
    private DatabaseInitializer dbInitializer;

    @BeforeMethod
    public void setUp() {
        dbInitializer = new DatabaseInitializer(TEST_DB_URL);
        connection = dbInitializer.connect();
    }

    @AfterMethod
    public void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        // Delete test database file
        new File("test_library.db").delete();
    }

    @Test
    public void testBooksTableCreation() throws SQLException {
        // Initialize database
        dbInitializer.initializeDatabase();

        // Verify books table
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "books", null);

        assertTrue(tables.next(), "Books table should exist");

        // Verify columns
        ResultSet columns = metaData.getColumns(null, null, "books", null);
        Map<String, String> expectedColumns = new HashMap<>();
        expectedColumns.put("id", "INTEGER");
        expectedColumns.put("title", "TEXT");
        expectedColumns.put("author", "TEXT");
        expectedColumns.put("isbn", "TEXT");
        expectedColumns.put("quantity", "INTEGER");
        expectedColumns.put("created_at", "TIMESTAMP");

        int columnCount = 0;
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            assertTrue(expectedColumns.containsKey(columnName),
                    "Column " + columnName + " should exist");
            assertEquals(expectedColumns.get(columnName), columnType,
                    "Column " + columnName + " should have correct type");
            columnCount++;
        }

        assertEquals(columnCount, expectedColumns.size(),
                "Table should have correct number of columns");
    }

    @Test
    public void testMembersTableCreation() throws SQLException {
        dbInitializer.initializeDatabase();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "members", null);

        assertTrue(tables.next(), "Members table should exist");

        // Verify columns
        ResultSet columns = metaData.getColumns(null, null, "members", null);
        Map<String, String> expectedColumns = new HashMap<>();
        expectedColumns.put("id", "INTEGER");
        expectedColumns.put("name", "TEXT");
        expectedColumns.put("email", "TEXT");
        expectedColumns.put("membership_date", "TIMESTAMP");

        verifyColumns(columns, expectedColumns);
    }

    @Test
    public void testLoansTableCreation() throws SQLException {
        dbInitializer.initializeDatabase();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, "loans", null);

        assertTrue(tables.next(), "Loans table should exist");

        // Verify columns
        ResultSet columns = metaData.getColumns(null, null, "loans", null);
        Map<String, String> expectedColumns = new HashMap<>();
        expectedColumns.put("id", "INTEGER");
        expectedColumns.put("book_id", "INTEGER");
        expectedColumns.put("member_id", "INTEGER");
        expectedColumns.put("loan_date", "TIMESTAMP");
        expectedColumns.put("due_date", "TIMESTAMP");
        expectedColumns.put("return_date", "TIMESTAMP");

        verifyColumns(columns, expectedColumns);
    }

    @Test(expectedExceptions = SQLException.class)
    public void testUniqueIsbnConstraint() throws SQLException {
        dbInitializer.initializeDatabase();

        String sql = "INSERT INTO books (title, author, isbn, quantity) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // First insert
            pstmt.setString(1, "Book 1");
            pstmt.setString(2, "Author 1");
            pstmt.setString(3, "123456789");
            pstmt.setInt(4, 1);
            pstmt.executeUpdate();

            // Second insert with same ISBN - should throw exception
            pstmt.setString(1, "Book 2");
            pstmt.setString(2, "Author 2");
            pstmt.setString(3, "123456789");
            pstmt.setInt(4, 1);
            pstmt.executeUpdate();
        }
    }

    @Test
    public void testForeignKeyConstraints() throws SQLException {
        dbInitializer.initializeDatabase();

        // Try to insert a loan with non-existent book_id and member_id
        String sql = "INSERT INTO loans (book_id, member_id) VALUES (999, 999)";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            fail("Should have thrown SQLException due to foreign key constraint");
        } catch (SQLException e) {
            // Expected exception
            assertTrue(e.getMessage().contains("FOREIGN KEY"));
        }
    }

    @Test
    public void testDatabaseIntegrity() {
        dbInitializer.initializeDatabase();
        assertTrue(dbInitializer.verifyDatabaseIntegrity(),
                "Database integrity check should pass");
    }

    private void verifyColumns(ResultSet columns, Map<String, String> expectedColumns)
            throws SQLException {
        int columnCount = 0;
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            assertTrue(expectedColumns.containsKey(columnName),
                    "Column " + columnName + " should exist");
            assertEquals(expectedColumns.get(columnName), columnType,
                    "Column " + columnName + " should have correct type");
            columnCount++;
        }

        assertEquals(columnCount, expectedColumns.size(),
                "Table should have correct number of columns");
    }

    @Test(groups = "dataOperations")
    public void testClearDatabase() throws SQLException {
        dbInitializer.initializeDatabase();

        // Insert some test data
        String sql = "INSERT INTO books (title, author, isbn) VALUES ('Test Book', 'Test Author', '123')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }

        // Clear the database
        dbInitializer.clearDatabase();

        // Verify all tables are empty
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books");
            rs.next();
            assertEquals(rs.getInt(1), 0, "Books table should be empty");

            rs = stmt.executeQuery("SELECT COUNT(*) FROM members");
            rs.next();
            assertEquals(rs.getInt(1), 0, "Members table should be empty");

            rs = stmt.executeQuery("SELECT COUNT(*) FROM loans");
            rs.next();
            assertEquals(rs.getInt(1), 0, "Loans table should be empty");
        }
    }
}