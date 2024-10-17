package application;
public class User {
    private String username;
    private String email;
    private String role;
    private boolean accountSetupComplete;

    public User(String username, String email, String role, boolean accountSetupComplete) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.accountSetupComplete = accountSetupComplete;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
    
    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAccountSetupComplete() {
        return accountSetupComplete;
    }
}
