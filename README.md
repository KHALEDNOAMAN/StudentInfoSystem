# 🎓 University Automation System

A comprehensive **Student Information System** built with **Java Swing**, featuring role-based access control, course management, grade tracking, and GPA calculation — all with a clean, modern light-mode UI.

---

## ✨ Features

### 🔐 Authentication
- Secure login system with role-based dashboard routing
- Default admin credentials seeded on first launch

### 👥 Four User Roles

| Role | Capabilities |
|------|-------------|
| **Admin** | Manage users, student profiles, courses; view system-wide reports with GPA |
| **Instructor** | View assigned courses, enter/update student grades (midterm & final) |
| **Student** | Browse & enroll in courses, drop courses, view transcript & GPA |
| **Advisor** | Combined Admin + Instructor access in a single dashboard |

### 📊 Core Modules
- **User Management** — Create, list, and delete user accounts with role assignment
- **Student Profiles** — Register students with ID, department, and academic year
- **Course Management** — Add/delete courses with credit hours, quotas, and instructor assignment
- **Enrollment System** — Students can enroll in open courses (with quota enforcement) and drop courses
- **Grade Entry** — Instructors enter midterm/final scores; automatic letter grade and GPA calculation
- **Transcript View** — Students see per-course grades, letter grades, and cumulative GPA
- **System Reports** — Admins view all students with enrollment counts and GPAs at a glance

### 🎨 UI Highlights
- Modern light-mode design with color-coded role dashboards
- Styled tables, cards, and form components via a centralized `UITheme` class
- Accent colors: 🔵 Admin, 🟢 Student, 🟣 Instructor, 🟠 Advisor

---

## 📁 Project Structure

```
StudentInfoSystem/
├── src/
│   ├── UniversityAutomationApp.java   # Entry point & login screen
│   ├── AdminPanel.java                # Admin dashboard (Users, Students, Courses, Reports)
│   ├── InstructorPanel.java           # Instructor dashboard (My Courses, Grade Entry)
│   ├── StudentPanel.java              # Student dashboard (Available Courses, My Courses, Transcript)
│   ├── AdvisorPanel.java              # Advisor dashboard (Admin + Instructor combined)
│   ├── DataStore.java                 # Central data layer with file-based persistence
│   ├── UITheme.java                   # UI design system (colors, fonts, styled components)
│   ├── User.java                      # User model
│   ├── StudentProfile.java            # Student profile model
│   ├── Course.java                    # Course model
│   ├── Enrollment.java                # Enrollment model
│   └── GradeRecord.java              # Grade record model (with letter grade & GPA logic)
├── data/                              # Persistent data files (auto-created)
│   ├── users.txt
│   ├── students.txt
│   ├── courses.txt
│   ├── enrollments.txt
│   └── grades.txt
├── run.bat                            # Windows build & run script
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 11** or higher installed
- `javac` and `java` available on your system PATH

### Option 1 — Using the batch script (Windows)
```bash
run.bat
```

### Option 2 — Manual compile & run
```bash
# Compile
javac --release 11 -d out src/*.java

# Run
java -cp out UniversityAutomationApp
```

---

## 🔑 Default Login

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Admin |

> Use the Admin panel to create additional users (Instructor, Student, Advisor).

---

## 📐 Grading System

Grades are calculated using **40% Midterm + 60% Final** weighting:

| Letter | Range | GPA Points |
|--------|-------|------------|
| AA | 90 – 100 | 4.0 |
| BA | 85 – 89 | 3.5 |
| BB | 80 – 84 | 3.0 |
| CB | 75 – 79 | 2.5 |
| CC | 70 – 74 | 2.0 |
| DC | 65 – 69 | 1.5 |
| DD | 60 – 64 | 1.0 |
| FD | 50 – 59 | 0.5 |
| FF | 0 – 49 | 0.0 |

Cumulative GPA is **credit-weighted** across all graded courses.

---

## 💾 Data Persistence

All data is stored in plain-text files under the `data/` directory using pipe (`|`) delimiters. The application loads data on startup and saves changes immediately after each operation — no external database required.

---

## 🛠️ Built With

- **Java 11+** — Core language
- **Java Swing** — GUI framework
- **File I/O** — Persistence layer

---

## 📄 License

This project is open source and available for educational purposes.
