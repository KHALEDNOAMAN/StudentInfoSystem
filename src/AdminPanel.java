import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * AdminPanel.java - Light Mode
 * Dashboard shown to users with role ADMIN.
 * Tabs: Users | Students | Courses | Reports
 */
public class AdminPanel extends JPanel {

    private final DataStore ds;
    private final JFrame    owner;

    private DefaultTableModel usersModel;
    private JTable usersTable;
    private JTextField usrUsername, usrFullName, usrRefId;
    private JPasswordField usrPassword;
    private JComboBox<String> usrRole;

    private DefaultTableModel studentsModel;
    private JTable studentsTable;
    private JTextField stuId, stuName, stuDept, stuYear, stuUser;

    private DefaultTableModel coursesModel;
    private JTable coursesTable;
    private JTextField crsCode, crsName, crsCredit, crsQuota;
    private JComboBox<String> crsInstructor;

    private DefaultTableModel reportsModel;
    private JTable reportsTable;

    public AdminPanel(DataStore ds, JFrame owner) {
        this.ds    = ds;
        this.owner = owner;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(239, 246, 255));
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(12, 20, 12, 20)));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_BLUE);
        p.add(title, BorderLayout.WEST);

        JLabel sub = UITheme.muted("University Automation System - Administrator");
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_MAIN);
        tabs.setFont(UITheme.FONT_HEADING);

        tabs.addTab("Users",    buildUsersTab());
        tabs.addTab("Students", buildStudentsTab());
        tabs.addTab("Courses",  buildCoursesTab());
        tabs.addTab("Reports",  buildReportsTab());

        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 3) refreshReports(); });
        return tabs;
    }

    // ══ USERS TAB ════════════════════════════════════════════════════════════

    private JPanel buildUsersTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = gbc();

        gc.gridy=0; gc.gridx=0; form.add(UITheme.label("Username:"), gc);
        gc.gridx=1; usrUsername=field(160); form.add(usrUsername, gc);
        gc.gridx=2; form.add(UITheme.label("Full Name:"), gc);
        gc.gridx=3; usrFullName=field(180); form.add(usrFullName, gc);

        gc.gridy=1; gc.gridx=0; form.add(UITheme.label("Password:"), gc);
        gc.gridx=1; usrPassword=UITheme.passwordField(); form.add(usrPassword, gc);
        gc.gridx=2; form.add(UITheme.label("Role:"), gc);
        gc.gridx=3; usrRole=UITheme.comboBox(new String[]{"ADMIN","INSTRUCTOR","STUDENT","ADVISOR"}); form.add(usrRole, gc);

        gc.gridy=2; gc.gridx=0; form.add(UITheme.label("Reference ID:"), gc);
        gc.gridx=1; usrRefId=UITheme.textField(); form.add(usrRefId, gc);

        JButton addBtn = UITheme.primaryBtn("Add User");
        addBtn.addActionListener(e -> addUser());
        gc.gridx=2; gc.gridwidth=2; form.add(addBtn, gc); gc.gridwidth=1;

        String[] cols = {"Username","Full Name","Role","Reference ID"};
        usersModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        usersTable = new JTable(usersModel);
        UITheme.styleTable(usersTable);

        JButton delBtn = UITheme.dangerBtn("Delete Selected");
        delBtn.addActionListener(e -> deleteUser());
        JPanel btnBar = btnBarRight(delBtn);

        root.add(form, BorderLayout.NORTH);
        root.add(UITheme.scrollPane(usersTable), BorderLayout.CENTER);
        root.add(btnBar, BorderLayout.SOUTH);
        refreshUsersTable();
        return root;
    }

    private void addUser() {
        String uname = usrUsername.getText().trim();
        String fname = usrFullName.getText().trim();
        String pass  = new String(usrPassword.getPassword()).trim();
        String role  = (String) usrRole.getSelectedItem();
        String ref   = usrRefId.getText().trim();

        if (uname.isEmpty() || fname.isEmpty() || pass.isEmpty()) {
            error("Username, full name and password are required."); return;
        }
        if (ds.usernameExists(uname)) { warn("Username already exists."); return; }
        ds.users.add(new User(uname, pass, role, fname, ref));
        ds.saveUsers();
        refreshUsersTable();
        clearUserForm();
        info("User added successfully.");
    }

    private void deleteUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0) { info("Select a user to delete."); return; }
        String uname = (String) usersModel.getValueAt(row, 0);
        if ("admin".equals(uname)) { warn("Cannot delete default admin."); return; }
        if (confirm("Delete user '" + uname + "'?")) {
            ds.users.removeIf(u -> u.getUsername().equals(uname));
            ds.saveUsers(); refreshUsersTable();
        }
    }

    private void refreshUsersTable() {
        usersModel.setRowCount(0);
        for (User u : ds.users)
            usersModel.addRow(new Object[]{u.getUsername(), u.getFullName(), u.getRole(), u.getReferenceId()});
    }

    private void clearUserForm() {
        usrUsername.setText(""); usrFullName.setText(""); usrPassword.setText(""); usrRefId.setText("");
    }

    // ══ STUDENTS TAB ═════════════════════════════════════════════════════════

    private JPanel buildStudentsTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel form = UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = gbc();

        gc.gridy=0; gc.gridx=0; form.add(UITheme.label("Student ID:"), gc);
        gc.gridx=1; stuId=field(130); form.add(stuId, gc);
        gc.gridx=2; form.add(UITheme.label("Full Name:"), gc);
        gc.gridx=3; stuName=field(180); form.add(stuName, gc);

        gc.gridy=1; gc.gridx=0; form.add(UITheme.label("Department:"), gc);
        gc.gridx=1; stuDept=UITheme.textField(); form.add(stuDept, gc);
        gc.gridx=2; form.add(UITheme.label("Year (1-6):"), gc);
        gc.gridx=3; stuYear=UITheme.textField(); form.add(stuYear, gc);

        gc.gridy=2; gc.gridx=0; form.add(UITheme.label("Username:"), gc);
        gc.gridx=1; stuUser=UITheme.textField(); form.add(stuUser, gc);

        JButton addBtn = UITheme.successBtn("Add Student");
        addBtn.addActionListener(e -> addStudent());
        gc.gridx=2; gc.gridwidth=2; form.add(addBtn, gc);

        String[] cols={"Student ID","Full Name","Department","Year","Username"};
        studentsModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        studentsTable=new JTable(studentsModel);
        UITheme.styleTable(studentsTable);

        root.add(form, BorderLayout.NORTH);
        root.add(UITheme.scrollPane(studentsTable), BorderLayout.CENTER);
        refreshStudentsTable();
        return root;
    }

    private void addStudent() {
        String id=stuId.getText().trim(), name=stuName.getText().trim();
        String dept=stuDept.getText().trim(), uname=stuUser.getText().trim(), yearStr=stuYear.getText().trim();
        if (id.isEmpty()||name.isEmpty()||dept.isEmpty()||uname.isEmpty()||yearStr.isEmpty()){error("All fields are required.");return;}
        int year;
        try { year=Integer.parseInt(yearStr); if(year<1||year>6) throw new NumberFormatException(); }
        catch(NumberFormatException ex){error("Year must be 1 - 6.");return;}
        if (ds.findStudentById(id)!=null){warn("Student ID already exists.");return;}
        if (!ds.usernameExists(uname)){error("No user account for '"+uname+"'. Create it first.");return;}
        ds.students.add(new StudentProfile(id,name,dept,year,uname));
        ds.saveStudents(); refreshStudentsTable();
        stuId.setText(""); stuName.setText(""); stuDept.setText(""); stuYear.setText(""); stuUser.setText("");
        info("Student profile added.");
    }

    private void refreshStudentsTable() {
        studentsModel.setRowCount(0);
        for (StudentProfile s:ds.students)
            studentsModel.addRow(new Object[]{s.getStudentId(),s.getFullName(),s.getDepartment(),s.getYear(),s.getUsername()});
    }

    // ══ COURSES TAB ══════════════════════════════════════════════════════════

    private JPanel buildCoursesTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        JPanel form=UITheme.card();
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc=gbc();

        gc.gridy=0; gc.gridx=0; form.add(UITheme.label("Course Code:"),gc);
        gc.gridx=1; crsCode=field(130); form.add(crsCode,gc);
        gc.gridx=2; form.add(UITheme.label("Course Name:"),gc);
        gc.gridx=3; crsName=field(220); form.add(crsName,gc);

        gc.gridy=1; gc.gridx=0; form.add(UITheme.label("Credit:"),gc);
        gc.gridx=1; crsCredit=UITheme.textField(); form.add(crsCredit,gc);
        gc.gridx=2; form.add(UITheme.label("Quota:"),gc);
        gc.gridx=3; crsQuota=UITheme.textField(); form.add(crsQuota,gc);

        gc.gridy=2; gc.gridx=0; form.add(UITheme.label("Instructor:"),gc);
        gc.gridx=1; gc.gridwidth=2; crsInstructor=buildInstructorCombo(); form.add(crsInstructor,gc); gc.gridwidth=1;

        JButton addBtn=UITheme.purpleBtn("Add Course");
        addBtn.addActionListener(e->addCourse());
        gc.gridx=3; form.add(addBtn,gc);

        String[] cols={"Code","Name","Credit","Quota","Enrolled","Instructor"};
        coursesModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        coursesTable=new JTable(coursesModel);
        UITheme.styleTable(coursesTable);

        JButton delBtn=UITheme.dangerBtn("Delete Course");
        delBtn.addActionListener(e->deleteCourse());

        root.add(form, BorderLayout.NORTH);
        root.add(UITheme.scrollPane(coursesTable), BorderLayout.CENTER);
        root.add(btnBarRight(delBtn), BorderLayout.SOUTH);
        refreshCoursesTable();
        return root;
    }

    private JComboBox<String> buildInstructorCombo() {
        JComboBox<String> cb=new JComboBox<>();
        cb.setBackground(UITheme.BG_CARD); cb.setForeground(UITheme.TEXT_PRIMARY); cb.setFont(UITheme.FONT_BODY);
        for (User u:ds.users) if ("INSTRUCTOR".equals(u.getRole())||"ADVISOR".equals(u.getRole())) cb.addItem(u.getUsername());
        return cb;
    }

    private void addCourse() {
        String code=crsCode.getText().trim(), name=crsName.getText().trim();
        String creditS=crsCredit.getText().trim(), quotaS=crsQuota.getText().trim();
        String instr=(String)crsInstructor.getSelectedItem();
        if (code.isEmpty()||name.isEmpty()||creditS.isEmpty()||quotaS.isEmpty()){error("All fields required.");return;}
        if (instr==null||instr.isEmpty()){error("No instructor available.");return;}
        int credit,quota;
        try{credit=Integer.parseInt(creditS);quota=Integer.parseInt(quotaS);}
        catch(NumberFormatException ex){error("Credit and quota must be integers.");return;}
        if (ds.findCourse(code)!=null){warn("Course code already exists.");return;}
        ds.courses.add(new Course(code,name,credit,quota,instr));
        ds.saveCourses(); refreshCoursesTable();
        crsCode.setText(""); crsName.setText(""); crsCredit.setText(""); crsQuota.setText("");
        info("Course added.");
    }

    private void deleteCourse() {
        int row=coursesTable.getSelectedRow();
        if (row<0){info("Select a course.");return;}
        String code=(String)coursesModel.getValueAt(row,0);
        if (confirm("Delete course '"+code+"' and all its enrollments/grades?")) {
            ds.courses.removeIf(c->c.getCourseCode().equals(code));
            ds.enrollments.removeIf(e->e.getCourseCode().equals(code));
            ds.grades.removeIf(g->g.getCourseCode().equals(code));
            ds.saveCourses(); ds.saveEnrollments(); ds.saveGrades();
            refreshCoursesTable();
        }
    }

    private void refreshCoursesTable() {
        coursesModel.setRowCount(0);
        for (Course c:ds.courses) {
            int enrolled=ds.countEnrollmentsForCourse(c.getCourseCode());
            coursesModel.addRow(new Object[]{c.getCourseCode(),c.getCourseName(),c.getCredit(),c.getQuota(),enrolled,c.getInstructorUsername()});
        }
    }

    // ══ REPORTS TAB ══════════════════════════════════════════════════════════

    private JPanel buildReportsTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        JLabel title=UITheme.heading("System Report - All Students");
        title.setBorder(new EmptyBorder(0,0,10,0));

        String[] cols={"Student ID","Name","Department","Year","Enrolled","GPA"};
        reportsModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        reportsTable=new JTable(reportsModel);
        UITheme.styleTable(reportsTable);

        JButton refreshBtn=UITheme.primaryBtn("Refresh");
        refreshBtn.addActionListener(e->refreshReports());
        JPanel top=new JPanel(new BorderLayout()); top.setBackground(UITheme.BG_MAIN);
        top.add(title,BorderLayout.WEST); top.add(refreshBtn,BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);
        root.add(UITheme.scrollPane(reportsTable), BorderLayout.CENTER);
        refreshReports();
        return root;
    }

    private void refreshReports() {
        if (reportsModel==null) return;
        reportsModel.setRowCount(0);
        for (StudentProfile s:ds.students) {
            int enrolled=ds.getEnrollmentsByStudent(s.getUsername()).size();
            double gpa=ds.calculateGPA(s.getUsername());
            reportsModel.addRow(new Object[]{s.getStudentId(),s.getFullName(),s.getDepartment(),s.getYear(),enrolled,String.format("%.2f",gpa)});
        }
    }

    public void refreshInstructorCombo() {
        if (crsInstructor==null) return;
        crsInstructor.removeAllItems();
        for (User u:ds.users) if ("INSTRUCTOR".equals(u.getRole())||"ADVISOR".equals(u.getRole())) crsInstructor.addItem(u.getUsername());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private GridBagConstraints gbc() {
        GridBagConstraints gc=new GridBagConstraints();
        gc.insets=new Insets(6,8,6,8); gc.fill=GridBagConstraints.HORIZONTAL; return gc;
    }
    private JTextField field(int w) { JTextField tf=UITheme.textField(); tf.setPreferredSize(new Dimension(w,34)); return tf; }
    private JPanel btnBarRight(JButton b) { JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT)); p.setBackground(UITheme.BG_MAIN); p.add(b); return p; }
    private void error(String msg)  { JOptionPane.showMessageDialog(owner,msg,"Error",JOptionPane.ERROR_MESSAGE); }
    private void warn(String msg)   { JOptionPane.showMessageDialog(owner,msg,"Warning",JOptionPane.WARNING_MESSAGE); }
    private void info(String msg)   { JOptionPane.showMessageDialog(owner,msg,"Success",JOptionPane.INFORMATION_MESSAGE); }
    private boolean confirm(String msg) { return JOptionPane.showConfirmDialog(owner,msg,"Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION; }
}
