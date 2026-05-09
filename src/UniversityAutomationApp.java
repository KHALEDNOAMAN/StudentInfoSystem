import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * UniversityAutomationApp.java
 * Entry point. Manages the Login screen and role-based dashboard routing.
 */
public class UniversityAutomationApp extends JFrame {

    private final DataStore ds = new DataStore();
    private User currentUser;

    private JTextField     loginUserField;
    private JPasswordField loginPassField;
    private JPanel         mainContent;
    private CardLayout     cardLayout;

    private static final String CARD_LOGIN     = "LOGIN";
    private static final String CARD_DASHBOARD = "DASHBOARD";

    public UniversityAutomationApp() {
        ds.initialize();
        setupFrame();
        buildUI();
    }

    private void setupFrame() {
        setTitle("University Automation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 740);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_MAIN);
    }

    private void buildUI() {
        cardLayout  = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.add(buildLoginPanel(), CARD_LOGIN);
        mainContent.add(new JPanel(),      CARD_DASHBOARD);
        add(mainContent);
        cardLayout.show(mainContent, CARD_LOGIN);
    }

    // ── Login Panel ───────────────────────────────────────────────────────────

    private JPanel buildLoginPanel() {
        // Left decorative side bar
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(UITheme.NAV_BG);
        sidebar.setPreferredSize(new Dimension(360, 0));

        JPanel sideContent = new JPanel();
        sideContent.setLayout(new BoxLayout(sideContent, BoxLayout.Y_AXIS));
        sideContent.setBackground(UITheme.NAV_BG);
        sideContent.setBorder(new EmptyBorder(0, 40, 0, 40));

        JLabel uni = new JLabel("University");
        uni.setFont(new Font("Segoe UI", Font.BOLD, 36));
        uni.setForeground(Color.WHITE);
        uni.setAlignmentX(CENTER_ALIGNMENT);

        JLabel automation = new JLabel("Automation");
        automation.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        automation.setForeground(new Color(147, 197, 253));
        automation.setAlignmentX(CENTER_ALIGNMENT);

        JLabel system = new JLabel("System");
        system.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        system.setForeground(new Color(147, 197, 253));
        system.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,60));
        sep.setMaximumSize(new Dimension(200, 1));

        JLabel desc = new JLabel("<html><center>Student Information<br>Management Portal</center></html>");
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(new Color(186, 230, 253));
        desc.setAlignmentX(CENTER_ALIGNMENT);

        sideContent.add(Box.createVerticalStrut(20));
        sideContent.add(uni);
        sideContent.add(Box.createVerticalStrut(4));
        sideContent.add(automation);
        sideContent.add(system);
        sideContent.add(Box.createVerticalStrut(20));
        sideContent.add(sep);
        sideContent.add(Box.createVerticalStrut(16));
        sideContent.add(desc);

        sidebar.add(sideContent);

        // Right login form
        JPanel formArea = new JPanel(new GridBagLayout());
        formArea.setBackground(UITheme.BG_MAIN);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(40, 44, 40, 44)));
        card.setPreferredSize(new Dimension(380, 420));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(UITheme.FONT_TITLE);
        welcome.setForeground(UITheme.TEXT_PRIMARY);
        welcome.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Form fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.insets = new Insets(4, 0, 4, 0);

        gc.gridy = 0;
        JLabel ul = UITheme.label("Username");
        ul.setBorder(new EmptyBorder(0, 2, 4, 0));
        form.add(ul, gc);

        gc.gridy = 1;
        loginUserField = UITheme.textField();
        loginUserField.setPreferredSize(new Dimension(290, 38));
        form.add(loginUserField, gc);

        gc.gridy = 2;
        JLabel pl = UITheme.label("Password");
        pl.setBorder(new EmptyBorder(10, 2, 4, 0));
        form.add(pl, gc);

        gc.gridy = 3;
        loginPassField = UITheme.passwordField();
        form.add(loginPassField, gc);

        JButton loginBtn = UITheme.primaryBtn("Sign In");
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(e -> attemptLogin());
        loginPassField.addActionListener(e -> attemptLogin());
        loginUserField.addActionListener(e -> loginPassField.requestFocus());

        JLabel hint = new JLabel("Default: admin / admin123");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(CENTER_ALIGNMENT);

        card.add(welcome);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(form);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(hint);

        formArea.add(card);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);
        root.add(sidebar,  BorderLayout.WEST);
        root.add(formArea, BorderLayout.CENTER);
        return root;
    }

    // ── Authentication ────────────────────────────────────────────────────────

    private void attemptLogin() {
        String username = loginUserField.getText().trim();
        String password = new String(loginPassField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = ds.authenticate(username, password);
        if (user == null) {
            loginPassField.setText("");
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentUser = user;
        showDashboard();
    }

    // ── Dashboard routing ─────────────────────────────────────────────────────

    private void showDashboard() {
        mainContent.remove(mainContent.getComponent(1));
        mainContent.add(buildDashboardWrapper(), CARD_DASHBOARD);
        cardLayout.show(mainContent, CARD_DASHBOARD);
        loginUserField.setText("");
        loginPassField.setText("");
        revalidate();
        repaint();
    }

    private JPanel buildDashboardWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_MAIN);
        wrapper.add(buildNavBar(), BorderLayout.NORTH);

        JPanel rolePanel;
        switch (currentUser.getRole()) {
            case "ADMIN":      rolePanel = new AdminPanel(ds, this);                       break;
            case "INSTRUCTOR": rolePanel = new InstructorPanel(ds, currentUser, this);     break;
            case "STUDENT":    rolePanel = new StudentPanel(ds, currentUser, this);        break;
            case "ADVISOR":    rolePanel = new AdvisorPanel(ds, currentUser, this);        break;
            default:           rolePanel = new JPanel();                                   break;
        }
        wrapper.add(rolePanel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.NAV_BG);
        nav.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel appName = new JLabel("University Automation System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appName.setForeground(Color.WHITE);
        nav.add(appName, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UITheme.NAV_BG);

        String roleDisplay = "ADVISOR".equals(currentUser.getRole()) ? "Admin | Instructor" : currentUser.getRole();
        JLabel userLabel   = new JLabel(currentUser.getFullName() + "   [" + roleDisplay + "]");
        userLabel.setForeground(new Color(186, 230, 253));
        userLabel.setFont(UITheme.FONT_SMALL);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(UITheme.NAV_BG);
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(6, 14, 6, 14));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        right.add(userLabel);
        right.add(logoutBtn);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser = null;
            cardLayout.show(mainContent, CARD_LOGIN);
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        UIManager.put("TabbedPane.selected",         UITheme.BG_CARD);
        UIManager.put("TabbedPane.background",        UITheme.BG_MAIN);
        UIManager.put("TabbedPane.foreground",        UITheme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor",  UITheme.BG_MAIN);
        UIManager.put("OptionPane.background",        UITheme.BG_CARD);
        UIManager.put("Panel.background",             UITheme.BG_CARD);
        UIManager.put("OptionPane.messageForeground", UITheme.TEXT_PRIMARY);

        SwingUtilities.invokeLater(() -> new UniversityAutomationApp().setVisible(true));
    }
}
