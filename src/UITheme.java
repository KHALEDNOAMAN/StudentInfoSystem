import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * UITheme.java - Light Mode Design System
 * Centralised design tokens and factory helpers.
 */
public class UITheme {

    // ── Colour palette (Light Mode) ───────────────────────────────────────────
    public static final Color BG_MAIN       = new Color(245, 247, 250);
    public static final Color BG_CARD       = new Color(255, 255, 255);
    public static final Color BG_INPUT      = new Color(240, 242, 245);
    public static final Color BG_DARK       = BG_MAIN; // alias kept for compatibility
    public static final Color ACCENT_BLUE   = new Color(37, 99, 235);
    public static final Color ACCENT_PURPLE = new Color(109, 40, 217);
    public static final Color ACCENT_GREEN  = new Color(5, 150, 105);
    public static final Color ACCENT_RED    = new Color(220, 38, 38);
    public static final Color ACCENT_ORANGE = new Color(217, 119, 6);
    public static final Color TEXT_PRIMARY  = new Color(15, 23, 42);
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);
    public static final Color TABLE_ROW_ALT = new Color(248, 250, 252);
    public static final Color TABLE_SELECT  = new Color(219, 234, 254);
    public static final Color NAV_BG        = new Color(30, 58, 138);  // deep navy for navbar

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Panel factories ───────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_MAIN);
        return p;
    }

    // ── Labels ────────────────────────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_PRIMARY);
        l.setFont(FONT_BODY);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_PRIMARY);
        l.setFont(FONT_HEADING);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_MUTED);
        l.setFont(FONT_SMALL);
        return l;
    }

    // ── Inputs ────────────────────────────────────────────────────────────────
    public static JTextField textField() {
        JTextField tf = new JTextField();
        styleInput(tf);
        return tf;
    }

    public static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        styleInput(pf);
        return pf;
    }

    public static JComboBox<String> comboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1));
        return cb;
    }

    private static void styleInput(JTextField tf) {
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
    }

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static JButton button(String text, Color accent) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? accent.darker().darker()
                           : getModel().isRollover() ? accent.darker()
                           : accent;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 24, 34));
        return b;
    }

    public static JButton primaryBtn(String text)  { return button(text, ACCENT_BLUE);   }
    public static JButton successBtn(String text)  { return button(text, ACCENT_GREEN);  }
    public static JButton dangerBtn(String text)   { return button(text, ACCENT_RED);    }
    public static JButton warningBtn(String text)  { return button(text, ACCENT_ORANGE); }
    public static JButton purpleBtn(String text)   { return button(text, ACCENT_PURPLE); }

    // ── Table ─────────────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(TABLE_SELECT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(239, 246, 255));
        header.setForeground(ACCENT_BLUE);
        header.setFont(FONT_HEADING);
        header.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT_BLUE));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    setBackground(TABLE_SELECT);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_CARD : TABLE_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1));
        return sp;
    }

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        return sep;
    }
}
