/**
 * User.java
 * Represents a system user with role-based access control.
 * Roles: ADMIN, INSTRUCTOR, STUDENT
 */
public class User {
    private String username;
    private String password;
    private String role;       // ADMIN, INSTRUCTOR, STUDENT
    private String fullName;
    private String referenceId; // studentId or instructorCode

    public User(String username, String password, String role, String fullName, String referenceId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.referenceId = referenceId;
    }

    // Getters
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getRole()        { return role; }
    public String getFullName()    { return fullName; }
    public String getReferenceId() { return referenceId; }

    // Setters
    public void setPassword(String password)       { this.password = password; }
    public void setFullName(String fullName)        { this.fullName = fullName; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    /**
     * Serialises this user to a pipe-delimited line for file storage.
     * Format: username|password|role|fullName|referenceId
     */
    public String toFileString() {
        return username + "|" + password + "|" + role + "|" + fullName + "|" + referenceId;
    }

    /**
     * Reconstructs a User from a pipe-delimited file line.
     */
    public static User fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
