import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataStore.java
 * Central in-memory data repository with file-based persistence.
 * Handles loading from and saving to the five data files.
 */
public class DataStore {

    // ── Data directory ──────────────────────────────────────────────────────
    private static final String DATA_DIR = "data";

    // ── In-memory lists ─────────────────────────────────────────────────────
    public List<User>          users       = new ArrayList<>();
    public List<StudentProfile> students   = new ArrayList<>();
    public List<Course>        courses     = new ArrayList<>();
    public List<Enrollment>    enrollments = new ArrayList<>();
    public List<GradeRecord>   grades      = new ArrayList<>();

    // ── Initialisation ──────────────────────────────────────────────────────

    /**
     * Bootstraps the data directory, loads all files, and seeds default admin
     * credentials if no users exist.
     */
    public void initialize() {
        new File(DATA_DIR).mkdirs();
        loadUsers();
        loadStudents();
        loadCourses();
        loadEnrollments();
        loadGrades();

        // Seed a default admin account on first run
        if (users.isEmpty()) {
            users.add(new User("admin", "admin123", "ADMIN", "System Administrator", ""));
            saveUsers();
        }
    }

    // ── Authentication ───────────────────────────────────────────────────────

    /**
     * Returns the matching User if credentials are correct, otherwise null.
     */
    public User authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // ── User helpers ─────────────────────────────────────────────────────────

    public User findUser(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public boolean usernameExists(String username) {
        return findUser(username) != null;
    }

    // ── Student helpers ──────────────────────────────────────────────────────

    public StudentProfile findStudentProfileByUsername(String username) {
        return students.stream().filter(s -> s.getUsername().equals(username)).findFirst().orElse(null);
    }

    public StudentProfile findStudentById(String studentId) {
        return students.stream().filter(s -> s.getStudentId().equals(studentId)).findFirst().orElse(null);
    }

    // ── Course helpers ───────────────────────────────────────────────────────

    public Course findCourse(String courseCode) {
        return courses.stream().filter(c -> c.getCourseCode().equals(courseCode)).findFirst().orElse(null);
    }

    public List<Course> getCoursesByInstructor(String instructorUsername) {
        return courses.stream()
                .filter(c -> c.getInstructorUsername().equals(instructorUsername))
                .collect(Collectors.toList());
    }

    public int countEnrollmentsForCourse(String courseCode) {
        return (int) enrollments.stream().filter(e -> e.getCourseCode().equals(courseCode)).count();
    }

    // ── Enrollment helpers ───────────────────────────────────────────────────

    public boolean isStudentEnrolled(String studentUsername, String courseCode) {
        return enrollments.stream()
                .anyMatch(e -> e.getStudentUsername().equals(studentUsername) && e.getCourseCode().equals(courseCode));
    }

    public List<Enrollment> getEnrollmentsByStudent(String studentUsername) {
        return enrollments.stream()
                .filter(e -> e.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    public List<Enrollment> getEnrollmentsByCourse(String courseCode) {
        return enrollments.stream()
                .filter(e -> e.getCourseCode().equals(courseCode))
                .collect(Collectors.toList());
    }

    public boolean removeEnrollment(String studentUsername, String courseCode) {
        return enrollments.removeIf(
                e -> e.getStudentUsername().equals(studentUsername) && e.getCourseCode().equals(courseCode));
    }

    // ── Grade helpers ────────────────────────────────────────────────────────

    public GradeRecord findGrade(String studentUsername, String courseCode) {
        return grades.stream()
                .filter(g -> g.getStudentUsername().equals(studentUsername) && g.getCourseCode().equals(courseCode))
                .findFirst()
                .orElse(null);
    }

    public List<GradeRecord> getGradesByStudent(String studentUsername) {
        return grades.stream()
                .filter(g -> g.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    /**
     * Inserts a new GradeRecord or updates an existing one (upsert semantics).
     */
    public void upsertGrade(String studentUsername, String courseCode, double midterm, double finalExam) {
        GradeRecord existing = findGrade(studentUsername, courseCode);
        if (existing != null) {
            existing.setMidterm(midterm);
            existing.setFinalExam(finalExam);
        } else {
            grades.add(new GradeRecord(studentUsername, courseCode, midterm, finalExam));
        }
    }

    /**
     * Calculates a student's cumulative GPA weighted by course credits.
     */
    public double calculateGPA(String studentUsername) {
        List<GradeRecord> studentGrades = getGradesByStudent(studentUsername);
        if (studentGrades.isEmpty()) return 0.0;

        double totalPoints = 0;
        int    totalCredits = 0;

        for (GradeRecord gr : studentGrades) {
            Course c = findCourse(gr.getCourseCode());
            int credit = (c != null) ? c.getCredit() : 1;
            totalPoints  += gr.getGradePoint() * credit;
            totalCredits += credit;
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    // ── Persistence – Save ───────────────────────────────────────────────────

    public void saveUsers() {
        writeLines(DATA_DIR + "/users.txt", users.stream().map(User::toFileString).collect(Collectors.toList()));
    }

    public void saveStudents() {
        writeLines(DATA_DIR + "/students.txt", students.stream().map(StudentProfile::toFileString).collect(Collectors.toList()));
    }

    public void saveCourses() {
        writeLines(DATA_DIR + "/courses.txt", courses.stream().map(Course::toFileString).collect(Collectors.toList()));
    }

    public void saveEnrollments() {
        writeLines(DATA_DIR + "/enrollments.txt", enrollments.stream().map(Enrollment::toFileString).collect(Collectors.toList()));
    }

    public void saveGrades() {
        writeLines(DATA_DIR + "/grades.txt", grades.stream().map(GradeRecord::toFileString).collect(Collectors.toList()));
    }

    // ── Persistence – Load ───────────────────────────────────────────────────

    public void loadUsers() {
        List<String> lines = readLines(DATA_DIR + "/users.txt");
        users.clear();
        for (String line : lines) {
            User u = User.fromFileString(line);
            if (u != null) users.add(u);
        }
    }

    public void loadStudents() {
        List<String> lines = readLines(DATA_DIR + "/students.txt");
        students.clear();
        for (String line : lines) {
            StudentProfile sp = StudentProfile.fromFileString(line);
            if (sp != null) students.add(sp);
        }
    }

    public void loadCourses() {
        List<String> lines = readLines(DATA_DIR + "/courses.txt");
        courses.clear();
        for (String line : lines) {
            Course c = Course.fromFileString(line);
            if (c != null) courses.add(c);
        }
    }

    public void loadEnrollments() {
        List<String> lines = readLines(DATA_DIR + "/enrollments.txt");
        enrollments.clear();
        for (String line : lines) {
            Enrollment e = Enrollment.fromFileString(line);
            if (e != null) enrollments.add(e);
        }
    }

    public void loadGrades() {
        List<String> lines = readLines(DATA_DIR + "/grades.txt");
        grades.clear();
        for (String line : lines) {
            GradeRecord gr = GradeRecord.fromFileString(line);
            if (gr != null) grades.add(gr);
        }
    }

    // ── I/O helpers ───────────────────────────────────────────────────────────

    private void writeLines(String filePath, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) pw.println(line);
        } catch (IOException e) {
            System.err.println("Error writing " + filePath + ": " + e.getMessage());
        }
    }

    private List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading " + filePath + ": " + e.getMessage());
        }
        return lines;
    }
}
