import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * StudentPanel.java - Light Mode
 * Dashboard for STUDENT role.
 * Tabs: Available Courses | My Courses | Transcript
 */
public class StudentPanel extends JPanel {

    private final DataStore      ds;
    private final User           currentUser;
    private final JFrame         owner;
    private final StudentProfile profile;

    private DefaultTableModel availModel;
    private JTable availTable;

    private DefaultTableModel myModel;
    private JTable myTable;

    private DefaultTableModel transcriptModel;
    private JTable transcriptTable;
    private JLabel gpaLabel, creditsLabel;

    public StudentPanel(DataStore ds, User currentUser, JFrame owner) {
        this.ds          = ds;
        this.currentUser = currentUser;
        this.owner       = owner;
        this.profile     = ds.findStudentProfileByUsername(currentUser.getUsername());
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(240, 253, 244));
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
                new EmptyBorder(12,20,12,20)));

        JLabel title = new JLabel("Student Portal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_GREEN);
        p.add(title, BorderLayout.WEST);

        String info = (profile!=null)
                ? currentUser.getFullName()+"   |   "+profile.getStudentId()+"   |   "+profile.getDepartment()
                : currentUser.getFullName();
        p.add(UITheme.muted(info), BorderLayout.EAST);
        return p;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_MAIN);
        tabs.setFont(UITheme.FONT_HEADING);
        tabs.addTab("Available Courses", buildAvailableTab());
        tabs.addTab("My Courses",        buildMyCoursesTab());
        tabs.addTab("Transcript",        buildTranscriptTab());
        tabs.addChangeListener(e -> {
            int idx=tabs.getSelectedIndex();
            if (idx==0) refreshAvailable();
            if (idx==1) refreshMyCourses();
            if (idx==2) refreshTranscript();
        });
        return tabs;
    }

    // ══ AVAILABLE COURSES ════════════════════════════════════════════════════

    private JPanel buildAvailableTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        JLabel hdr=UITheme.heading("Open Courses   (select a row, then click Enroll)");
        hdr.setBorder(new EmptyBorder(0,0,10,0));

        String[] cols={"Code","Name","Credit","Quota","Enrolled","Instructor","Status"};
        availModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        availTable=new JTable(availModel);
        UITheme.styleTable(availTable);

        JButton enrollBtn=UITheme.successBtn("Enroll");
        enrollBtn.addActionListener(e->enroll());
        JButton refreshBtn=UITheme.primaryBtn("Refresh");
        refreshBtn.addActionListener(e->refreshAvailable());

        JPanel btnBar=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBar.setBackground(UITheme.BG_MAIN);
        btnBar.add(refreshBtn); btnBar.add(enrollBtn);

        root.add(hdr,BorderLayout.NORTH);
        root.add(UITheme.scrollPane(availTable),BorderLayout.CENTER);
        root.add(btnBar,BorderLayout.SOUTH);
        refreshAvailable();
        return root;
    }

    private void refreshAvailable() {
        if (availModel==null) return;
        availModel.setRowCount(0);
        for (Course c:ds.courses) {
            int enrolled=ds.countEnrollmentsForCourse(c.getCourseCode());
            boolean full=enrolled>=c.getQuota();
            boolean already=ds.isStudentEnrolled(currentUser.getUsername(),c.getCourseCode());
            String status=already?"Enrolled":(full?"Full":"Open");
            availModel.addRow(new Object[]{c.getCourseCode(),c.getCourseName(),c.getCredit(),c.getQuota(),enrolled,c.getInstructorUsername(),status});
        }
    }

    private void enroll() {
        int row=availTable.getSelectedRow();
        if (row<0){JOptionPane.showMessageDialog(owner,"Select a course to enroll in.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        String code=(String)availModel.getValueAt(row,0);
        String status=(String)availModel.getValueAt(row,6);
        if ("Enrolled".equals(status)){JOptionPane.showMessageDialog(owner,"Already enrolled in this course.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        if ("Full".equals(status)){JOptionPane.showMessageDialog(owner,"Course is full.","Error",JOptionPane.ERROR_MESSAGE);return;}
        ds.enrollments.add(new Enrollment(currentUser.getUsername(),code));
        ds.saveEnrollments(); refreshAvailable();
        JOptionPane.showMessageDialog(owner,"Enrolled in "+code+" successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
    }

    // ══ MY COURSES ════════════════════════════════════════════════════════════

    private JPanel buildMyCoursesTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        JLabel hdr=UITheme.heading("Your Enrolled Courses");
        hdr.setBorder(new EmptyBorder(0,0,10,0));

        String[] cols={"Code","Name","Credit","Instructor"};
        myModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        myTable=new JTable(myModel);
        UITheme.styleTable(myTable);

        JButton dropBtn=UITheme.dangerBtn("Drop Course");
        dropBtn.addActionListener(e->dropCourse());
        JButton refreshBtn=UITheme.primaryBtn("Refresh");
        refreshBtn.addActionListener(e->refreshMyCourses());

        JPanel btnBar=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBar.setBackground(UITheme.BG_MAIN);
        btnBar.add(refreshBtn); btnBar.add(dropBtn);

        root.add(hdr,BorderLayout.NORTH);
        root.add(UITheme.scrollPane(myTable),BorderLayout.CENTER);
        root.add(btnBar,BorderLayout.SOUTH);
        refreshMyCourses();
        return root;
    }

    private void refreshMyCourses() {
        if (myModel==null) return;
        myModel.setRowCount(0);
        for (Enrollment en:ds.getEnrollmentsByStudent(currentUser.getUsername())) {
            Course c=ds.findCourse(en.getCourseCode());
            if (c!=null) myModel.addRow(new Object[]{c.getCourseCode(),c.getCourseName(),c.getCredit(),c.getInstructorUsername()});
        }
    }

    private void dropCourse() {
        int row=myTable.getSelectedRow();
        if (row<0){JOptionPane.showMessageDialog(owner,"Select a course to drop.","Info",JOptionPane.INFORMATION_MESSAGE);return;}
        String code=(String)myModel.getValueAt(row,0);
        int c=JOptionPane.showConfirmDialog(owner,"Drop course "+code+"?","Confirm",JOptionPane.YES_NO_OPTION);
        if (c==JOptionPane.YES_OPTION){ds.removeEnrollment(currentUser.getUsername(),code); ds.saveEnrollments(); refreshMyCourses();}
    }

    // ══ TRANSCRIPT ════════════════════════════════════════════════════════════

    private JPanel buildTranscriptTab() {
        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(new EmptyBorder(16,16,16,16));

        // GPA summary card
        JPanel summary=UITheme.card();
        summary.setLayout(new GridBagLayout());
        GridBagConstraints gc=new GridBagConstraints();
        gc.insets=new Insets(6,20,6,20);

        gpaLabel=new JLabel("GPA:  --");
        gpaLabel.setFont(new Font("Segoe UI",Font.BOLD,28));
        gpaLabel.setForeground(UITheme.ACCENT_GREEN);
        creditsLabel=UITheme.label("Total Credits:  --");

        gc.gridx=0; gc.gridy=0; summary.add(gpaLabel,gc);
        gc.gridx=1;              summary.add(creditsLabel,gc);

        String name=(profile!=null)?profile.getFullName():currentUser.getFullName();
        String dept=(profile!=null)?profile.getDepartment():"";
        JLabel nameLabel=UITheme.heading(name+(dept.isEmpty()?"":" - "+dept));
        gc.gridx=0; gc.gridy=1; gc.gridwidth=2; summary.add(nameLabel,gc);

        String[] cols={"Course Code","Course Name","Credit","Midterm","Final","Average","Letter Grade"};
        transcriptModel=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        transcriptTable=new JTable(transcriptModel);
        UITheme.styleTable(transcriptTable);

        JButton refreshBtn=UITheme.primaryBtn("Refresh");
        refreshBtn.addActionListener(e->refreshTranscript());
        JPanel top=new JPanel(new BorderLayout()); top.setBackground(UITheme.BG_MAIN);
        top.add(summary,BorderLayout.CENTER); top.add(refreshBtn,BorderLayout.EAST);

        root.add(top,BorderLayout.NORTH);
        root.add(UITheme.scrollPane(transcriptTable),BorderLayout.CENTER);
        refreshTranscript();
        return root;
    }

    private void refreshTranscript() {
        if (transcriptModel==null) return;
        transcriptModel.setRowCount(0);
        int totalCredits=0;
        for (GradeRecord gr:ds.getGradesByStudent(currentUser.getUsername())) {
            Course c=ds.findCourse(gr.getCourseCode());
            String cname=(c!=null)?c.getCourseName():"--";
            int credit=(c!=null)?c.getCredit():0;
            totalCredits+=credit;
            transcriptModel.addRow(new Object[]{gr.getCourseCode(),cname,credit,
                    String.format("%.1f",gr.getMidterm()),String.format("%.1f",gr.getFinalExam()),
                    String.format("%.1f",gr.calculateAverage()),gr.getLetterGrade()});
        }
        double gpa=ds.calculateGPA(currentUser.getUsername());
        if (gpaLabel!=null){
            gpaLabel.setText(String.format("GPA:   %.2f / 4.00",gpa));
            gpaLabel.setForeground(gpa>=2.0?UITheme.ACCENT_GREEN:UITheme.ACCENT_RED);
        }
        if (creditsLabel!=null) creditsLabel.setText("Total Credits Graded:   "+totalCredits);
    }
}
