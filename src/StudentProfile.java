/**
 * StudentProfile.java
 * Stores student-specific academic profile information.
 */
public class StudentProfile {
    private String studentId;
    private String fullName;
    private String department;
    private int    year;
    private String username;

    public StudentProfile(String studentId, String fullName, String department, int year, String username) {
        this.studentId  = studentId;
        this.fullName   = fullName;
        this.department = department;
        this.year       = year;
        this.username   = username;
    }

    // Getters
    public String getStudentId()  { return studentId; }
    public String getFullName()   { return fullName; }
    public String getDepartment() { return department; }
    public int    getYear()       { return year; }
    public String getUsername()   { return username; }

    // Setters
    public void setFullName(String fullName)     { this.fullName = fullName; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year)                { this.year = year; }

    /**
     * Serialises to pipe-delimited format.
     * Format: studentId|fullName|department|year|username
     */
    public String toFileString() {
        return studentId + "|" + fullName + "|" + department + "|" + year + "|" + username;
    }

    /**
     * Reconstructs a StudentProfile from a pipe-delimited file line.
     */
    public static StudentProfile fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            return new StudentProfile(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return fullName + " [" + studentId + "] – " + department + ", Year " + year;
    }
}
