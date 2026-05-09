/**
 * GradeRecord.java
 * Stores a student's midterm and final exam scores for a course.
 * Provides letter-grade calculation based on a weighted average (40% midterm, 60% final).
 */
public class GradeRecord {
    private String studentUsername;
    private String courseCode;
    private double midterm;
    private double finalExam;

    public GradeRecord(String studentUsername, String courseCode, double midterm, double finalExam) {
        this.studentUsername = studentUsername;
        this.courseCode      = courseCode;
        this.midterm         = midterm;
        this.finalExam       = finalExam;
    }

    // Getters
    public String getStudentUsername() { return studentUsername; }
    public String getCourseCode()      { return courseCode; }
    public double getMidterm()         { return midterm; }
    public double getFinalExam()       { return finalExam; }

    // Setters
    public void setMidterm(double midterm)     { this.midterm = midterm; }
    public void setFinalExam(double finalExam) { this.finalExam = finalExam; }

    /**
     * Weighted average: 40 % midterm + 60 % final.
     */
    public double calculateAverage() {
        return midterm * 0.40 + finalExam * 0.60;
    }

    /**
     * Converts the weighted average to a letter grade.
     */
    public String getLetterGrade() {
        double avg = calculateAverage();
        if (avg >= 90) return "AA";
        if (avg >= 85) return "BA";
        if (avg >= 75) return "BB";
        if (avg >= 70) return "CB";
        if (avg >= 60) return "CC";
        if (avg >= 55) return "DC";
        if (avg >= 50) return "DD";
        return "FF";
    }

    /**
     * Maps letter grade to GPA points (4.0 scale).
     */
    public double getGradePoint() {
        switch (getLetterGrade()) {
            case "AA": return 4.0;
            case "BA": return 3.5;
            case "BB": return 3.0;
            case "CB": return 2.5;
            case "CC": return 2.0;
            case "DC": return 1.5;
            case "DD": return 1.0;
            default:   return 0.0; // FF
        }
    }

    /**
     * Serialises to pipe-delimited format.
     * Format: studentUsername|courseCode|midterm|finalExam
     */
    public String toFileString() {
        return studentUsername + "|" + courseCode + "|" + midterm + "|" + finalExam;
    }

    /**
     * Reconstructs a GradeRecord from a pipe-delimited file line.
     */
    public static GradeRecord fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        try {
            return new GradeRecord(parts[0], parts[1],
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return courseCode + ": " + String.format("%.1f", calculateAverage()) + " (" + getLetterGrade() + ")";
    }
}
