/**
 * Course.java
 * Stores course/lecture information including capacity and assigned instructor.
 */
public class Course {
    private String courseCode;
    private String courseName;
    private int    credit;
    private int    quota;
    private String instructorUsername;

    public Course(String courseCode, String courseName, int credit, int quota, String instructorUsername) {
        this.courseCode         = courseCode;
        this.courseName         = courseName;
        this.credit             = credit;
        this.quota              = quota;
        this.instructorUsername = instructorUsername;
    }

    // Getters
    public String getCourseCode()         { return courseCode; }
    public String getCourseName()         { return courseName; }
    public int    getCredit()             { return credit; }
    public int    getQuota()              { return quota; }
    public String getInstructorUsername() { return instructorUsername; }

    // Setters
    public void setCourseName(String courseName)             { this.courseName = courseName; }
    public void setCredit(int credit)                         { this.credit = credit; }
    public void setQuota(int quota)                           { this.quota = quota; }
    public void setInstructorUsername(String instrUsername)  { this.instructorUsername = instrUsername; }

    /**
     * Serialises to pipe-delimited format.
     * Format: courseCode|courseName|credit|quota|instructorUsername
     */
    public String toFileString() {
        return courseCode + "|" + courseName + "|" + credit + "|" + quota + "|" + instructorUsername;
    }

    /**
     * Reconstructs a Course from a pipe-delimited file line.
     */
    public static Course fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        try {
            return new Course(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return courseCode + " – " + courseName + " (" + credit + " cr)";
    }
}
