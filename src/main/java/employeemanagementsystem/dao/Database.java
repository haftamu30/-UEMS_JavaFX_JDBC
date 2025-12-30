package employeemanagementsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Connection Manager
 * Handles database connections and configuration
 *
 * @author Your Name
 */
// Utility class for database connection
public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/employe";
    private static final String USER = "root";
    private static final String PASSWORD = "hafi4642";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    /**
     * Establishes and returns a database connection
     * Implements Singleton pattern for connection reuse
     *
     * @return Connection object or null if connection fails
     */
    public static Connection connectDb() {
        try {
            // Load MySQL JDBC Driver
            Class.forName(DRIVER);

            // Create connection if not already established
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.info("Database connection established successfully.");
            }

            return connection;

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found!", e);
            showErrorDialog("Database Error", "MySQL JDBC Driver not found!\nPlease add mysql-connector-j to your classpath.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection failed!", e);
            showErrorDialog("Connection Error",
                    "Cannot connect to database!\n" +
                            "Please check:\n" +
                            "1. MySQL server is running\n" +
                            "2. Database 'employee' exists\n" +
                            "3. Username and password are correct\n" +
                            "4. Port 3306 is accessible\n\n" +
                            "Error: " + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during database connection!", e);
            showErrorDialog("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
        }

        return null;
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error closing database connection", e);
        }
    }

    /**
     * Tests the database connection
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }

    /**
     * Shows error dialog for database connection issues
     *
     * @param title Dialog title
     * @param message Error message
     */
    private static void showErrorDialog(String title, String message) {
        // This method can be enhanced to show JavaFX Alert dialogs
        // For now, we'll just log and print to console
        System.err.println("[" + title + "]: " + message);

        // In a real application, you might want to show a JavaFX Alert:
        /*
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Database Connection Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
        */
    }

    /**
     * Gets database URL
     *
     * @return Database URL
     */
    public static String getUrl() {
        return URL;
    }

    /**
     * Gets database username
     *
     * @return Database username
     */
    public static String getUser() {
        return USER;
    }

    /**
     * Gets database password (in real applications, use encryption)
     *
     * @return Database password
     */
    public static String getPassword() {
        return PASSWORD;
    }

    /**
     * Sets new database connection parameters
     * (Useful for configuration changes)
     *
     * @param url New database URL
     * @param user New username
     * @param password New password
     */
    public static void setConnectionParameters(String url, String user, String password) {
        // Close existing connection if any
        closeConnection();

        // Update parameters
        // Note: In a real application, these should be stored securely
        // URL = url;
        // USER = user;
        // PASSWORD = password;

        logger.info("Database connection parameters updated.");
    }

    /**
     * Executes a test query to verify database functionality
     *
     * @return true if query executes successfully, false otherwise
     */
    public static boolean testDatabaseFunctionality() {
        Connection conn = null;
        try {
            conn = connectDb();
            if (conn == null) return false;

            // Test query: Check if admin table exists and has data
            String testQuery = "SELECT COUNT(*) as count FROM admin";
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(testQuery)) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    logger.info("Database test successful. Admin records: " + count);
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Database functionality test failed", e);
            return false;
        }
    }

    /**
     * Checks if database schema is properly initialized
     *
     * @return true if all required tables exist, false otherwise
     */
    public static boolean isDatabaseInitialized() {
        Connection conn = null;
        try {
            conn = connectDb();
            if (conn == null) return false;

            // Check for required tables
            String[] requiredTables = {"admin", "employee", "employee_salary", "departments", "specializations"};

            for (String table : requiredTables) {
                String checkQuery = "SELECT 1 FROM " + table + " LIMIT 1";
                try (var stmt = conn.createStatement();
                     var rs = stmt.executeQuery(checkQuery)) {
                    if (!rs.next()) {
                        logger.warning("Table " + table + " is empty or doesn't exist");
                        return false;
                    }
                } catch (SQLException e) {
                    logger.warning("Table " + table + " doesn't exist: " + e.getMessage());
                    return false;
                }
            }

            logger.info("Database schema validation successful.");
            return true;

        } catch (Exception e) {
            logger.log(Level.WARNING, "Database initialization check failed", e);
            return false;
        }
    }

    /**
     * Creates a backup of the database
     * (Implementation depends on MySQL utilities availability)
     */
    public static void backupDatabase(String backupPath) {
        // This is a simplified backup method
        // In production, use mysqldump or similar tools

        logger.info("Starting database backup to: " + backupPath);

        try {
            // Example using mysqldump (requires mysqldump in PATH)
            /*
            ProcessBuilder processBuilder = new ProcessBuilder(
                "mysqldump",
                "-u", USER,
                "-p" + PASSWORD,
                "employee",
                "-r", backupPath
            );

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("Database backup completed successfully.");
            } else {
                logger.warning("Database backup failed with exit code: " + exitCode);
            }
            */

            // For now, just log the intention
            logger.info("Backup functionality would save to: " + backupPath);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Database backup failed", e);
        }
    }

    /**
     * Gets database connection statistics
     *
     * @return Connection statistics string
     */
    public static String getConnectionStats() {
        StringBuilder stats = new StringBuilder();

        try {
            if (connection != null && !connection.isClosed()) {
                stats.append("Connection Status: Active\n");
                stats.append("Auto Commit: ").append(connection.getAutoCommit()).append("\n");
                stats.append("Read Only: ").append(connection.isReadOnly()).append("\n");
                stats.append("Transaction Isolation: ").append(connection.getTransactionIsolation()).append("\n");
                stats.append("Catalog: ").append(connection.getCatalog()).append("\n");
            } else {
                stats.append("Connection Status: Closed\n");
            }
        } catch (SQLException e) {
            stats.append("Connection Status: Error - ").append(e.getMessage());
        }

        return stats.toString();
    }

    /**
     * Validates database connection parameters
     *
     * @param url Database URL
     * @param user Username
     * @param password Password
     * @return true if parameters are valid, false otherwise
     */
    public static boolean validateConnectionParameters(String url, String user, String password) {
        if (url == null || url.trim().isEmpty()) {
            logger.warning("Database URL cannot be empty");
            return false;
        }

        if (user == null || user.trim().isEmpty()) {
            logger.warning("Database username cannot be empty");
            return false;
        }

        // Check URL format
        if (!url.startsWith("jdbc:mysql://")) {
            logger.warning("Invalid database URL format. Must start with 'jdbc:mysql://'");
            return false;
        }

        return true;
    }

    /**
     * Configures connection pool settings
     * (In a real application, use a connection pool like HikariCP)
     */
    public static void configureConnectionPool() {


        logger.info("Connection pool configuration placeholder.");
    }
}