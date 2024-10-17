package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {
    private Main mainApp;
    private Stage primaryStage;

    public LoginController(Main mainApp) {
        this.mainApp = mainApp;
        
    }

    public Scene createLoginScene(Stage primaryStage) {
        this.primaryStage = primaryStage; // Set reference to the stage

        // Create UI elements for the login scene
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account"); // Create account button
        Label messageLabel = new Label();

        // Create layout
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(10));
        loginLayout.setVgap(8);
        loginLayout.setHgap(10);
        loginLayout.add(usernameLabel, 0, 0);
        loginLayout.add(usernameField, 1, 0);
        loginLayout.add(passwordLabel, 0, 1);
        loginLayout.add(passwordField, 1, 1);
        loginLayout.add(loginButton, 1, 2);
        loginLayout.add(createAccountButton, 1, 3); // Add create account button
        loginLayout.add(messageLabel, 1, 4);

        // Handle login button click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = validateLogin(username, password, primaryStage);
            if (role != null) { // Only proceed if login is successful and role is returned
                mainApp.showHomepage(role); // Pass the role to the homepage method
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Handle "Create Account" button click
        createAccountButton.setOnAction(e -> {
            RegistrationController registrationController = new RegistrationController(mainApp);
            registrationController.showRegistrationScene(primaryStage); // Redirect to registration scene
        });

        // Create and return the login scene
        return new Scene(loginLayout, 300, 200);
    }
    
    private String validateLogin(String username, String password, Stage primaryStage) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT password, account_setup_complete, role, first_name, last_name FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");

                // Verify the plain text password against the hashed password
                if (PasswordValidator.verifyPassword(password, dbPassword)) {
                    boolean accountSetupComplete = rs.getBoolean("account_setup_complete");
                    String role = rs.getString("role");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    // If account setup is incomplete, show account setup scene
                    if (firstName == null || lastName == null || !accountSetupComplete) {
                        showAccountSetupScene(primaryStage, username, password);
                        return null; // Account setup scene, no homepage yet
                    } else {
                        return role; // Return role if login is successful
                    }
                }
            } else {
                showErrorAlert("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Login failed: " + e.getMessage());
        }

        return null; // Return null if login fails
    }

    
    private void showAccountSetupScene(Stage primaryStage, String username, String password) {
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);

        // Create UI elements for account setup
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");

        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("Preferred Name");

        Button finishButton = new Button("Finish");

        finishButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String middleName = middleNameField.getText();
            String preferredName = preferredNameField.getText();

            // Hash the password before saving (assuming you have a hashing method)
            String hashedPassword = PasswordValidator.hashPassword(password); // Use the provided password

            try (Connection conn = DatabaseConnection.connect()) {
                // Check if the email exists in the users table (as a placeholder)
                String checkEmailSql = "SELECT * FROM users WHERE email = ? AND account_setup_complete = FALSE";
                PreparedStatement checkEmailPstmt = conn.prepareStatement(checkEmailSql);
                checkEmailPstmt.setString(1, email);
                ResultSet rs = checkEmailPstmt.executeQuery();

                if (rs.next()) {
                	
                	String userRole = rs.getString("role"); // Fetch user role
                	 // Now delete the old user with the same username and password
                    String deleteSql = "DELETE FROM users WHERE username = ? AND password = ?";
                    PreparedStatement deletePstmt = conn.prepareStatement(deleteSql);
                    deletePstmt.setString(1, username);
                    deletePstmt.setString(2, hashedPassword);
                    deletePstmt.executeUpdate();
                    
                    // If the email exists and account setup is not complete, update the user details
                    String updateSql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, middle_name = ?, preferred_name = ?, account_setup_complete = TRUE WHERE email = ?";
                    PreparedStatement pstmt = conn.prepareStatement(updateSql);
                    pstmt.setString(1, username); // Use the existing username
                    pstmt.setString(2, hashedPassword); // Store the hashed password
                    pstmt.setString(3, firstName);
                    pstmt.setString(4, lastName);
                    pstmt.setString(5, middleName);
                    pstmt.setString(6, preferredName);
                    pstmt.setString(7, email);
                    pstmt.executeUpdate();
                    
                   
                    // Redirect to the appropriate homepage based on user role
                    mainApp.showHomepage(userRole); // Adjust to show the correct homepage based on role
                } else {
                	
                	
                	 // Now delete the old user with the same username and password
                    String deleteSql = "DELETE FROM users WHERE username = ? AND password = ?";
                    PreparedStatement deletePstmt = conn.prepareStatement(deleteSql);
                    deletePstmt.setString(1, username);
                    deletePstmt.setString(2, hashedPassword);
                    deletePstmt.executeUpdate();
                    
                    // If no placeholder exists, create a new account
                    String insertSql = "INSERT INTO users (username, password, email, first_name, last_name, middle_name, preferred_name, role, account_setup_complete) VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";
                    PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
                    insertPstmt.setString(1, username);
                    insertPstmt.setString(2, hashedPassword);
                    insertPstmt.setString(3, email);
                    insertPstmt.setString(4, firstName);
                    insertPstmt.setString(5, lastName);
                    insertPstmt.setString(6, middleName);
                    insertPstmt.setString(7, preferredName);
                    insertPstmt.setString(8, "admin"); // Set role to admin for the first user
                    insertPstmt.executeUpdate();
                    
                    String userRole = rs.getString("role"); // Fetch user role

                    // Redirect to the appropriate homepage for the admin
                    mainApp.showHomepage(userRole); // Adjust to show the correct homepage based on role
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Account setup failed: " + ex.getMessage());
            }
        });

        // Add elements to the layout
        layout.getChildren().addAll(emailField, firstNameField, lastNameField, middleNameField, preferredNameField, finishButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}