import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * InstructorPanel.java - Light Mode
 * Dashboard for INSTRUCTOR role.
 * Tabs: My Courses | Grade Entry
 */
public class InstructorPanel extends JPanel {

    private final DataStore ds;
    private final User      currentUser;
    private final JFrame    owner;

    private DefaultTableModel myCoursesModel;
    private JTable myCoursesTable;

    private JComboBox<String>  gradeCourseCombo;
    private DefaultTableModel  enrolledModel;
    private JTable             enrolledTable;
    private JTextField         midField, finalField;
    private JLabel             selectedStudentLabel;

    public InstructorPanel(DataStore ds, User currentUser, JFrame owner) {
        this.ds          = ds;
        this.currentUser = currentUser;
        this.owner       = owner;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 243, 255));
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
                new EmptyBorder(12,20,12,20)));

        JLabel title = new JLabel("Instructor Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_PURPLE);
        p.add(title, BorderLayout.WEST);

        JLabel sub = UITheme.muted("Welcome, " + currentUser.getFullName());
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_MAIN);
        tabs.setFont(UITheme.FONT_HEADING);
        tabs.addTab("My Courses",  buildMyCoursesTab());
        tabs.addTab("Grade Entry", buildGradeEntryTab());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex()==0) refreshMyCourses();
            if (tabs.getSelectedIndex()==1) refreshGradeCourseCombo();
        });
        return tabs;
    }

    // ══ MY COURSES ════════════════════════════════════════════════════════════

    private JPanel buildMyCoursesTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        JLabel hdr=UITheme.heading("Courses Assigned to You");
        hdr.setBorder(new EmptyBorder(0,0,10,0));

        String[] cols={"Code","Name","Credit","Quota","Enrolled","Capacity"};
        myCoursesModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        myCoursesTable=new JTable(myCoursesModel);
        UITheme.styleTable(myCoursesTable);

        JButton refreshBtn=UITheme.primaryBtn("Refresh");
        refreshBtn.addActionListener(e->refreshMyCourses());
        JPanel top=new JPanel(new BorderLayout()); top.setBackground(UITheme.BG_MAIN);
        top.add(hdr,BorderLayout.WEST); top.add(refreshBtn,BorderLayout.EAST);

        root.add(top,BorderLayout.NORTH);
        root.add(UITheme.scrollPane(myCoursesTable),BorderLayout.CENTER);
        refreshMyCourses();
        return root;
    }

    private void refreshMyCourses() {
        if (myCoursesModel==null) return;
        myCoursesModel.setRowCount(0);
        for (Course c:ds.getCoursesByInstructor(currentUser.getUsername())) {
            int enrolled=ds.countEnrollmentsForCourse(c.getCourseCode());
            myCoursesModel.addRow(new Object[]{c.getCourseCode(),c.getCourseName(),c.getCredit(),c.getQuota(),enrolled,enrolled+"/"+c.getQuota()});
        }
    }

    // ══ GRADE ENTRY ═══════════════════════════════════════════════════════════

    private JPanel buildGradeEntryTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        // Course selector row
        JPanel courseRow=new JPanel(new FlowLayout(FlowLayout.LEFT,12,0));
        courseRow.setBackground(UITheme.BG_MAIN);
        courseRow.add(UITheme.label("Select Course:"));
        gradeCourseCombo=UITheme.comboBox(new String[]{});
        gradeCourseCombo.setPreferredSize(new Dimension(280,34));
        gradeCourseCombo.addActionListener(e->loadEnrolledStudents());
        courseRow.add(gradeCourseCombo);
        JButton loadBtn=UITheme.primaryBtn("Load Students");
        loadBtn.addActionListener(e->loadEnrolledStudents());
        courseRow.add(loadBtn);

        // Enrolled students table
        String[] cols={"Username","Student Name","Midterm","Final","Average","Grade"};
        enrolledModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        enrolledTable=new JTable(enrolledModel);
        UITheme.styleTable(enrolledTable);
        enrolledTable.getSelectionModel().addListSelectionListener(e->{
            if (!e.getValueIsAdjusting()) populateGradeFields();
        });

        // Grade form
        JPanel gradeForm=UITheme.card();
        gradeForm.setLayout(new FlowLayout(FlowLayout.LEFT,14,10));

        selectedStudentLabel=UITheme.label("No student selected");
        selectedStudentLabel.setForeground(UITheme.TEXT_MUTED);
        gradeForm.add(selectedStudentLabel);

        gradeForm.add(UITheme.label("  |  Midterm (0-100):"));
        midField=UITheme.textField(); midField.setPreferredSize(new Dimension(80,34));
        gradeForm.add(midField);

        gradeForm.add(UITheme.label("Final (0-100):"));
        finalField=UITheme.textField(); finalField.setPreferredSize(new Dimension(80,34));
        gradeForm.add(finalField);

        JButton saveBtn=UITheme.successBtn("Save Grade");
        saveBtn.addActionListener(e->saveGrade());
        gradeForm.add(saveBtn);

        root.add(courseRow,BorderLayout.NORTH);
        root.add(UITheme.scrollPane(enrolledTable),BorderLayout.CENTER);
        root.add(gradeForm,BorderLayout.SOUTH);

        refreshGradeCourseCombo();
        return root;
    }

    private void refreshGradeCourseCombo() {
        if (gradeCourseCombo==null) return;
        gradeCourseCombo.removeAllItems();
        for (Course c:ds.getCoursesByInstructor(currentUser.getUsername()))
            gradeCourseCombo.addItem(c.getCourseCode()+" - "+c.getCourseName());
        loadEnrolledStudents();
    }

    private void loadEnrolledStudents() {
        if (enrolledModel==null||gradeCourseCombo==null||gradeCourseCombo.getSelectedItem()==null) return;
        String sel=(String)gradeCourseCombo.getSelectedItem();
        if (sel==null) return;
        String code=sel.split(" - ")[0];
        enrolledModel.setRowCount(0);
        for (Enrollment en:ds.getEnrollmentsByCourse(code)) {
            String uname=en.getStudentUsername();
            StudentProfile sp=ds.findStudentProfileByUsername(uname);
            String sname=(sp!=null)?sp.getFullName():uname;
            GradeRecord gr=ds.findGrade(uname,code);
            String mid="",fin="",avg="",letter="";
            if (gr!=null){mid=String.format("%.1f",gr.getMidterm()); fin=String.format("%.1f",gr.getFinalExam()); avg=String.format("%.1f",gr.calculateAverage()); letter=gr.getLetterGrade();}
            enrolledModel.addRow(new Object[]{uname,sname,mid,fin,avg,letter});
        }
    }

    private void populateGradeFields() {
        int row=enrolledTable.getSelectedRow();
        if (row<0) return;
        String uname=(String)enrolledModel.getValueAt(row,0);
        String sname=(String)enrolledModel.getValueAt(row,1);
        selectedStudentLabel.setText("Editing: "+sname+" ("+uname+")");
        selectedStudentLabel.setForeground(UITheme.ACCENT_GREEN);
        midField.setText((String)enrolledModel.getValueAt(row,2));
        finalField.setText((String)enrolledModel.getValueAt(row,3));
    }

    private void saveGrade() {
        int row=enrolledTable.getSelectedRow();
        if (row<0){JOptionPane.showMessageDialog(owner,"Select a student.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        String sel=(String)gradeCourseCombo.getSelectedItem();
        if (sel==null) return;
        String code=sel.split(" - ")[0];
        String uname=(String)enrolledModel.getValueAt(row,0);
        double mid,fin;
        try{mid=Double.parseDouble(midField.getText().trim()); fin=Double.parseDouble(finalField.getText().trim());}
        catch(NumberFormatException ex){JOptionPane.showMessageDialog(owner,"Scores must be numeric.","Error",JOptionPane.ERROR_MESSAGE);return;}
        if (mid<0||mid>100||fin<0||fin>100){JOptionPane.showMessageDialog(owner,"Scores must be 0 - 100.","Error",JOptionPane.ERROR_MESSAGE);return;}
        ds.upsertGrade(uname,code,mid,fin); ds.saveGrades();
        loadEnrolledStudents();
        JOptionPane.showMessageDialog(owner,"Grade saved.","Success",JOptionPane.INFORMATION_MESSAGE);
    }
}
