
package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class StudentHomepageController {

    private Main mainApp;

    public StudentHomepageController(Main mainApp) {
        this.mainApp = mainApp;
    }

    // Display the admin homepage with all required buttons
    public Scene createHomepageScene() {
        // Create labels and buttons for the UI
        Label welcomeLabel = new Label("Welcome, Student!");
        
        

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
        homepageLayout.add(logoutButton, 0, 1);

        // Return the scene
        return new Scene(homepageLayout, 400, 300);
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