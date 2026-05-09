/**
 * Enrollment.java
 * Represents a student's registration in a specific course.
 */
public class Enrollment {
    private String studentUsername;
    private String courseCode;

    public Enrollment(String studentUsername, String courseCode) {
        this.studentUsername = studentUsername;
        this.courseCode      = courseCode;
    }

    // Getters
    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode()      { return courseCode; }

    /**
     * Serialises to pipe-delimited format.
     * Format: studentUsername|courseCode
     */
    public String toFileString() {
        return studentUsername + "|" + courseCode;
    }

    /**
     * Reconstructs an Enrollment from a pipe-delimited file line.
     */
    public static Enrollment fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 2) return null;
        return new Enrollment(parts[0], parts[1]);
    }

    @Override
    public String toString() {
        return studentUsername + " → " + courseCode;
    }
}
