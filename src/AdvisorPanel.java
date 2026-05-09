import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * AdvisorPanel.java - Light Mode
 * Combined Admin + Instructor dashboard for ADVISOR role.
 */
public class AdvisorPanel extends JPanel {

    private final DataStore ds;
    private final User      currentUser;
    private final JFrame    owner;

    private AdminPanel      adminPanel;
    private InstructorPanel instructorPanel;

    public AdvisorPanel(DataStore ds, User currentUser, JFrame owner) {
        this.ds          = ds;
        this.currentUser = currentUser;
        this.owner       = owner;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(255, 251, 235));
        p.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0,0,1,0,UITheme.BORDER_COLOR),
                new EmptyBorder(12,20,12,20)));

        JLabel title = new JLabel("Advisor Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.ACCENT_ORANGE);
        p.add(title, BorderLayout.WEST);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badges.setBackground(new Color(255, 251, 235));
        badges.add(UITheme.muted(currentUser.getFullName()+"   "));
        badges.add(badge("ADMIN",      UITheme.ACCENT_BLUE));
        badges.add(badge("INSTRUCTOR", UITheme.ACCENT_PURPLE));
        p.add(badges, BorderLayout.EAST);
        return p;
    }

    private JLabel badge(String text, Color color) {
        JLabel l = new JLabel("  " + text + "  ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Color.WHITE);
        l.setBackground(color);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(4, 6, 4, 6));
        return l;
    }

    private JTabbedPane buildContent() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setBackground(UITheme.BG_MAIN);
        tabs.setFont(UITheme.FONT_HEADING);

        adminPanel      = new AdminPanel(ds, owner);
        instructorPanel = new InstructorPanel(ds, currentUser, owner);

        tabs.addTab("Administration", wrapSection(adminPanel,
                "Administration Panel", "Manage users, students and courses.", UITheme.ACCENT_BLUE));
        tabs.addTab("Grade Entry",    wrapSection(instructorPanel,
                "Grade Entry Panel",    "Enter and update student grades for your courses.", UITheme.ACCENT_PURPLE));

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) adminPanel.refreshInstructorCombo();
        });
        return tabs;
    }

    private JPanel wrapSection(JPanel inner, String title, String subtitle, Color accent) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_MAIN);

        JPanel strip = new JPanel(new BorderLayout());
        strip.setBackground(accent);
        strip.setBorder(new EmptyBorder(7, 16, 7, 16));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_HEADING);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(UITheme.FONT_SMALL);
        s.setForeground(new Color(255, 255, 255, 200));

        strip.add(t, BorderLayout.WEST);
        strip.add(s, BorderLayout.EAST);
        wrapper.add(strip, BorderLayout.NORTH);
        wrapper.add(inner, BorderLayout.CENTER);
        return wrapper;
    }
}
