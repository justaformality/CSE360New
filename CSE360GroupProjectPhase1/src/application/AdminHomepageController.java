package application;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminHomepageController {

    private Main mainApp;

    public AdminHomepageController(Main mainApp) {
        this.mainApp = mainApp;
    }

    // Display the admin homepage with all required buttons
    public Scene createHomepageScene() {
        // Create labels and buttons for the UI
        Label welcomeLabel = new Label("Welcome, Admin!");
        
        // Button for inviting a user
        Button inviteUserButton = new Button("Invite User");
        inviteUserButton.setOnAction(e -> handleInviteUser());

        // Button for resetting a user's password
       // Button resetPasswordButton = new Button("Reset User Password");
       // resetPasswordButton.setOnAction(e -> handleResetUserPassword());

        

        // Button for listing all users
        Button listUsersButton = new Button("List Users");
        listUsersButton.setOnAction(e -> handleListUsers());

       

        // Button for logging out
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> handleLogout());

        // Arrange buttons and labels in a grid layout
        GridPane homepageLayout = new GridPane();
        homepageLayout.setPadding(new Insets(10));
        homepageLayout.setVgap(10); // Vertical spacing between rows
        homepageLayout.setHgap(10); // Horizontal spacing between columns
        
        // Add components to the layout
        homepageLayout.add(welcomeLabel, 0, 0);
        homepageLayout.add(inviteUserButton, 0, 1);
       // homepageLayout.add(resetPasswordButton, 0, 2);
       
        homepageLayout.add(listUsersButton, 0, 3);
       
        homepageLayout.add(logoutButton, 0, 5);

        // Return the scene
        return new Scene(homepageLayout, 400, 300);
    }

    // Placeholder methods for backend functionality

 // Invite a user via email and assign a role
    private void handleInviteUser() {
        // Step 1: Prompt admin for the email and role
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Invite User");
        emailDialog.setHeaderText("Invite a new user");
        emailDialog.setContentText("Please enter the user's email address:");

        Optional<String> emailResult = emailDialog.showAndWait();

        if (emailResult.isPresent() && !emailResult.get().isEmpty()) {
            String email = emailResult.get();

            // Step 2: Prompt for the role (Student or Instructor)
            TextInputDialog roleDialog = new TextInputDialog();
            roleDialog.setTitle("Assign Role");
            roleDialog.setHeaderText("Assign Role to User");
            roleDialog.setContentText("Please enter the user's role (student/instructor):");

            Optional<String> roleResult = roleDialog.showAndWait();

            if (roleResult.isPresent()) {
                String role = roleResult.get().toLowerCase();

                if (role.equals("student") || role.equals("instructor")) {
                    // Step 3: Generate a one-time invitation code
                    String invitationCode = generateInvitationCode();
                    String loginLink = "http://yourapplication.com/login?invitation=" + invitationCode;

                    // Step 4: Store the invitation and placeholder user details
                    storeInvitationDetails(email, role, invitationCode);

                    // Step 5: Insert placeholder user in users table
                    insertPlaceholderUser(email, role);

                    // Step 6: Send the invitation link to the user's email
                    sendEmail(email, loginLink, role);

                    // Notify the admin that the invitation was successful
                    showAlert("Invite User", "Invitation sent to " + email + " as " + role + ".");
                } else {
                    showAlert("Invalid Role", "Please enter either 'student' or 'instructor' as the role.");
                }
            }
        }
    }
    
 // Helper method to insert a placeholder user in the users table
    private void insertPlaceholderUser(String email, String role) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO users (email, role, account_setup_complete) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, role);
            pstmt.setBoolean(3, false); // Account setup is not yet complete
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to insert placeholder user.");
        }
    }
    
    // Helper method to generate a one-time invitation code
    private String generateInvitationCode() {
        // You can use UUID or a custom random string generator here
        return java.util.UUID.randomUUID().toString();
    }

 // Helper method to store the invitation details (email, role, invitation code) in the database
    private void storeInvitationDetails(String email, String role, String invitationCode) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO invitations (email, invitation_code, role, expires_at) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, invitationCode);
            pstmt.setString(3, role);
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(1))); // Set expiry 24 hours from now
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to store invitation details.");
        }
    }

    // Helper method to send the invitation email (dummy implementation)
    private void sendEmail(String email, String loginLink, String role) {
        // You should integrate an actual email service to send the email
        System.out.println("Sending email to: " + email);
        System.out.println("Login Link: " + loginLink);
        System.out.println("Assigned Role: " + role);
    }


    // Reset a user's password
    private void handleResetUserPassword() {
        // This method will eventually reset a user's password
        showAlert("Reset User Password", "This feature will allow you to reset user passwords.");
    }

  

    private void handleListUsers() {
        // Create a TableView for displaying users
        TableView<User> tableView = new TableView<>();

        // Define columns for the TableView
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, Boolean> accountSetupCol = new TableColumn<>("Account Setup Complete");
        accountSetupCol.setCellValueFactory(new PropertyValueFactory<>("accountSetupComplete"));

        // Add columns to the TableView
        tableView.getColumns().addAll(usernameCol, emailCol, roleCol, accountSetupCol);

        // Add role management buttons and Delete button
        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button adminButton = new Button("Admin");
            private final Button studentButton = new Button("Student");
            private final Button instructorButton = new Button("Instructor");
            private final Button deleteButton = new Button("Delete");

            {
                // Handle admin role button
                adminButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    updateRoleInDatabase(user.getUsername(), "admin"); // Update the role in the database
                    user.setRole("admin"); // Update the role in the UI
                    tableView.refresh(); // Refresh the TableView
                });

                // Handle student role button
                studentButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    updateRoleInDatabase(user.getUsername(), "student"); // Update the role in the database
                    user.setRole("student"); // Update the role in the UI
                    tableView.refresh(); // Refresh the TableView
                });

                // Handle instructor role button
                instructorButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    updateRoleInDatabase(user.getUsername(), "instructor"); // Update the role in the database
                    user.setRole("instructor"); // Update the role in the UI
                    tableView.refresh(); // Refresh the TableView
                });

                // Handle user deletion
                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUserFromDatabase(user.getUsername()); // Delete the user from the database
                    tableView.getItems().remove(user); // Remove the user from the UI
                });

                // Create an HBox to contain all the buttons
                HBox actionLayout = new HBox(10, adminButton, studentButton, instructorButton, deleteButton);
                setGraphic(actionLayout);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getGraphic());
                }
            }
        });

        tableView.getColumns().add(actionCol);

        // Fetch users from the database and populate the TableView
        try (Connection conn = DatabaseConnection.connect()) {
            String querySql = "SELECT username, email, role, account_setup_complete FROM users";
            PreparedStatement pstmt = conn.prepareStatement(querySql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean accountSetupComplete = rs.getBoolean("account_setup_complete");

                User user = new User(username, email, role, accountSetupComplete);
                tableView.getItems().add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Failed to list users: ", ex.getMessage());
        }

        // Create a layout for the TableView
        VBox layout = new VBox(tableView);
        Scene scene = new Scene(layout, 600, 400);

        // Show the TableView in a new window
        Stage userListStage = new Stage();
        userListStage.setTitle("User List");
        userListStage.setScene(scene);
        userListStage.show();
    }

    // Method to update role in the database
    private void updateRoleInDatabase(String username, String newRole) {
        String updateSql = "UPDATE users SET role = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error updating role", e.getMessage());
        }
    }

    // Method to delete a user from the database
    private void deleteUserFromDatabase(String username) {
        String deleteSql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error deleting user", e.getMessage());
        }
    }

   


    // Log out
    private void handleLogout() {
        // Redirect back to login screen (Main class will need a method for this)
        showAlert("Logout", "Logging out...");
        mainApp.showLoginScene(); // Call Main class method to redirect to login page
    }

    // Helper method to display alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
