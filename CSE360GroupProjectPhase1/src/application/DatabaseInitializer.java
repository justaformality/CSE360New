package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initDatabase() {
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {
        	
        	 
            // 1. Create or update the users table with additional fields
            createUsersTable(stmt);

            // 2. Create the invitations table
            createInvitationsTable(stmt);

            // 3. Create the one_time_passwords table for password resets
            createOTPsTable(stmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to create or update the users table
    private static void createUsersTable(Statement stmt) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) UNIQUE," +
                "password VARCHAR(255)," +
                "email VARCHAR(255) UNIQUE," +
                "first_name VARCHAR(255)," +
                "last_name VARCHAR(255)," +
                "middle_name VARCHAR(255)," +
                "preferred_name VARCHAR(255)," +
                "role VARCHAR(50) NOT NULL, " +  // admin, student, instructor
                "account_setup_complete BOOLEAN DEFAULT FALSE" +
                ");";
        
        stmt.execute(createUsersTable);
        System.out.println("Users table created or already exists.");
    }

    // Method to create the invitations table
    private static void createInvitationsTable(Statement stmt) throws SQLException {
        String createInvitationsTable = "CREATE TABLE IF NOT EXISTS invitations (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "email VARCHAR(255) UNIQUE NOT NULL," +
                "invitation_code VARCHAR(255) UNIQUE NOT NULL," +
                "role VARCHAR(50) NOT NULL, " +  // student, instructor
                "expires_at TIMESTAMP NOT NULL" +
                ");";

        stmt.execute(createInvitationsTable);
        System.out.println("Invitations table created or already exists.");
    }

    // Method to create the one_time_passwords table for password resets
    private static void createOTPsTable(Statement stmt) throws SQLException {
        String createOTPsTable = "CREATE TABLE IF NOT EXISTS one_time_passwords (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "one_time_password VARCHAR(255) NOT NULL," +
                "expires_at TIMESTAMP NOT NULL," +
                "used BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +  // FK constraint to users table
                ");";

        stmt.execute(createOTPsTable);
        System.out.println("One-time passwords table created or already exists.");
    }
}
