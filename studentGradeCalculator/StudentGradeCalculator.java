import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentGradeCalculator extends JFrame {

    // Theme Palette
    private static final Color BG_DARK = new Color(22, 24, 34);
    private static final Color CARD_BG = new Color(30, 34, 48);
    private static final Color INPUT_BG = new Color(18, 20, 28);
    private static final Color ACCENT_BLUE = new Color(79, 110, 247);
    private static final Color TEXT_WHITE = new Color(240, 243, 250);
    private static final Color TEXT_MUTED = new Color(150, 156, 175);
    private static final Color BORDER_COLOR = new Color(45, 52, 74);

    // Dynamic Subject Rows
    private final JPanel subjectsListPanel;
    private final List<SubjectRow> subjectRows = new ArrayList<>();

    // Result Badges
    private JLabel totalMarksBadge;
    private JLabel averagePercentageBadge;
    private JLabel gradeBadge;

    public StudentGradeCalculator() {
        setTitle("Student Grade Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // Header Results Panel (Top)
        add(createResultsHeader(), BorderLayout.NORTH);

        // Center Panel with scrollable subject list
        subjectsListPanel = new JPanel();
        subjectsListPanel.setLayout(new BoxLayout(subjectsListPanel, BoxLayout.Y_AXIS));
        subjectsListPanel.setBackground(BG_DARK);

        JScrollPane scrollPane = new JScrollPane(subjectsListPanel);
        scrollPane.setBorder(new EmptyBorder(10, 25, 10, 25));
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        // Footer Action Panel (Bottom)
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Add 5 default subjects
        for (int i = 1; i <= 5; i++) {
            addSubjectRow("Subject " + i, "");
        }
    }

    private JPanel createResultsHeader() {
        JPanel header = new JPanel(new GridLayout(1, 3, 12, 0));
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(25, 25, 15, 25));

        totalMarksBadge = createCard("TOTAL MARKS", "0 / 0");
        averagePercentageBadge = createCard("PERCENTAGE", "0.0%");
        gradeBadge = createCard("GRADE", "—");

        header.add(totalMarksBadge);
        header.add(averagePercentageBadge);
        header.add(gradeBadge);

        return header;
    }

    private JLabel createCard(String title, String val) {
        JLabel card = new JLabel(formatCardHtml(title, val, "#F0F3FA"), SwingConstants.CENTER);
        card.setOpaque(true);
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 6, 12, 6)
        ));
        return card;
    }

    private String formatCardHtml(String title, String val, String valColor) {
        return "<html><center><span style='font-size:9px; color:#969CAF; letter-spacing:1px;'>" + title + "</span><br>"
                + "<span style='font-size:17px; font-weight:bold; color:" + valColor + ";'>" + val + "</span></center></html>";
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        footer.setBackground(BG_DARK);
        footer.setBorder(new EmptyBorder(5, 20, 20, 20));

        JButton addBtn = new JButton("+ Add Subject");
        styleButton(addBtn, CARD_BG, TEXT_WHITE, 130);
        addBtn.addActionListener(e -> addSubjectRow("Subject " + (subjectRows.size() + 1), ""));

        JButton calcBtn = new JButton("Calculate Grade");
        styleButton(calcBtn, ACCENT_BLUE, TEXT_WHITE, 160);
        calcBtn.addActionListener(e -> calculateGrade());

        footer.add(addBtn);
        footer.add(calcBtn);
        return footer;
    }

    private void styleButton(JButton btn, Color bg, Color fg, int width) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(width, 42));
    }

    private void addSubjectRow(String subjectName, String marks) {
        SubjectRow row = new SubjectRow(subjectName, marks);
        subjectRows.add(row);
        subjectsListPanel.add(row);
        subjectsListPanel.add(Box.createVerticalStrut(10));
        subjectsListPanel.revalidate();
        subjectsListPanel.repaint();
    }

    private void removeSubjectRow(SubjectRow row) {
        if (subjectRows.size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one subject is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        subjectRows.remove(row);
        subjectsListPanel.remove(row);
        subjectsListPanel.revalidate();
        subjectsListPanel.repaint();
    }

    private void calculateGrade() {
        double totalMarks = 0;
        int validSubjectCount = 0;

        for (SubjectRow row : subjectRows) {
            String marksText = row.marksField.getText().trim();
            if (marksText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter marks for all subjects.", "Input Required", JOptionPane.WARNING_MESSAGE);
                row.marksField.requestFocus();
                return;
            }

            try {
                double marks = Double.parseDouble(marksText);
                if (marks < 0 || marks > 100) {
                    JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100.", "Invalid Range", JOptionPane.ERROR_MESSAGE);
                    row.marksField.requestFocus();
                    return;
                }
                totalMarks += marks;
                validSubjectCount++;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter numeric values for marks.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                row.marksField.requestFocus();
                return;
            }
        }

        double maxPossibleMarks = validSubjectCount * 100;
        double averagePercentage = (totalMarks / maxPossibleMarks) * 100;

        // Grade calculation & accent colors
        String grade;
        String gradeColor;

        if (averagePercentage >= 90) {
            grade = "A+ (Outstanding)";
            gradeColor = "#48BB78"; // Emerald Green
        } else if (averagePercentage >= 80) {
            grade = "A (Excellent)";
            gradeColor = "#38A169";
        } else if (averagePercentage >= 70) {
            grade = "B (Very Good)";
            gradeColor = "#4299E1"; // Blue
        } else if (averagePercentage >= 60) {
            grade = "C (Good)";
            gradeColor = "#ED8936"; // Amber
        } else if (averagePercentage >= 50) {
            grade = "D (Pass)";
            gradeColor = "#ECC94B"; // Yellow
        } else {
            grade = "F (Fail)";
            gradeColor = "#F56565"; // Red
        }

        // Update Dashboard Cards
        totalMarksBadge.setText(formatCardHtml("TOTAL MARKS", String.format("%.0f / %.0f", totalMarks, maxPossibleMarks), "#F0F3FA"));
        averagePercentageBadge.setText(formatCardHtml("PERCENTAGE", String.format("%.2f%%", averagePercentage), "#F0F3FA"));
        gradeBadge.setText(formatCardHtml("GRADE", grade, gradeColor));
    }

    // Inner Class representing an individual subject input row
    private class SubjectRow extends JPanel {
        private final JTextField nameField;
        private final JTextField marksField;

        public SubjectRow(String defaultName, String defaultMarks) {
            setLayout(new BorderLayout(10, 0));
            setBackground(CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(8, 12, 8, 12)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            nameField = new JTextField(defaultName);
            styleField(nameField, 160);

            marksField = new JTextField(defaultMarks);
            marksField.setHorizontalAlignment(JTextField.CENTER);
            styleField(marksField, 80);

            JLabel outOfLabel = new JLabel("/ 100");
            outOfLabel.setForeground(TEXT_MUTED);
            outOfLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            JPanel marksPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            marksPanel.setOpaque(false);
            marksPanel.add(marksField);
            marksPanel.add(outOfLabel);

            JButton removeBtn = new JButton("✕");
            removeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            removeBtn.setForeground(new Color(245, 101, 101));
            removeBtn.setContentAreaFilled(false);
            removeBtn.setBorderPainted(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeBtn.addActionListener(e -> removeSubjectRow(this));

            marksPanel.add(removeBtn);

            add(nameField, BorderLayout.WEST);
            add(marksPanel, BorderLayout.EAST);
        }

        private void styleField(JTextField field, int width) {
            field.setPreferredSize(new Dimension(width, 32));
            field.setBackground(INPUT_BG);
            field.setForeground(TEXT_WHITE);
            field.setCaretColor(TEXT_WHITE);
            field.setFont(new Font("SansSerif", Font.PLAIN, 13));
            field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentGradeCalculator app = new StudentGradeCalculator();
            app.setVisible(true);
        });
    }
}
