import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyViewApplications extends JFrame {
    private int facultyId;
    private JTable applicationsTable;
    private DefaultTableModel model;
    private JPanel contentPanel;

    public FacultyViewApplications(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Manage Student Applications");

        // Set up the background panel
        setContentPane(createBackgroundPanel());

        // Set up content panel
        contentPanel = createRoundedPanel();

        // Initialize table and model
        model = new DefaultTableModel(new String[] { "Application ID", "Student ID", "Description", "Status",
                "Follow-up Question", "Student Response" }, 0);
        applicationsTable = new JTable(model);
        applicationsTable.setFillsViewportHeight(true);
        applicationsTable.setRowHeight(25);
        applicationsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set custom column widths for the last two columns
        // applicationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable
        // auto-resizing for custom widths

        // TableColumnModel columnModel = applicationsTable.getColumnModel();
        // columnModel.getColumn(4).setPreferredWidth(200); // Set width for "Follow-up
        // Question"
        // columnModel.getColumn(5).setPreferredWidth(200); // Set width for "Student
        // Response"

        // Add mouse listener to table
        applicationsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = applicationsTable.rowAtPoint(e.getPoint());
                int column = applicationsTable.columnAtPoint(e.getPoint());

                // Check if the row and column are valid
                if (row >= 0 && column >= 0) {
                    // Retrieve the value from the selected cell
                    Object cellValue = applicationsTable.getValueAt(row, column);

                    // Display the cell value in a dialog
                    JPanel panel = new JPanel(new BorderLayout());
                    JTextArea textArea = new JTextArea(5, 30); // 5 rows, 30 columns as an example size
                    textArea.setText(cellValue.toString());
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Arial", Font.PLAIN, 12));
                    textArea.setBackground(new Color(255, 255, 255)); // light grey background for better readability

                    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

                    // Show the JPanel with JTextArea inside a dialog (or you can add it to your
                    // main panel)
                    JOptionPane.showMessageDialog(null, panel, "Follow-up Question", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        // Buttons
        JButton acceptButton = createStyledButton("Accept");
        JButton rejectButton = createStyledButton("Reject");
        JButton askQuestionButton = createStyledButton("Ask Follow-up Question");
        JButton logoutButton = createStyledButton("Logout");
        // Action listeners
        acceptButton.addActionListener(e -> updateApplicationStatus("Accepted"));
        rejectButton.addActionListener(e -> updateApplicationStatus("Rejected"));
        askQuestionButton.addActionListener(e -> askFollowUpQuestion());

        // Load data
        loadApplications();

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent background
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(askQuestionButton);

        buttonPanel.add(logoutButton);

        // Add components to the content panel
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.add(new JScrollPane(applicationsTable), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        logoutButton.addActionListener(e -> dispose());
        // Add content panel to the main frame
        add(contentPanel);
        setupFrame();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        if (text.equalsIgnoreCase("logout")) {
            button.setBackground(new Color(225, 0, 0));
        } else
            button.setBackground(new Color(70, 130, 180)); // Steel blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createRoundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200)); // White with transparency
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(600, 400)); // Adjust size as needed
        return panel;
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("bg.jpg"); // Update the path to your background image
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private void setupFrame() {
        setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7)); // 70% screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadApplications() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM applications WHERE faculty_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, facultyId);

            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("follow_up_question"),
                        rs.getString("student_response")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateApplicationStatus(String status) {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow != -1) {
            int applicationId = (int) model.getValueAt(selectedRow, 0);
            try (Connection conn = DBConnection.getConnection()) {
                String query = "UPDATE applications SET status = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, status);
                stmt.setInt(2, applicationId);
                stmt.executeUpdate();
                loadApplications();
                JOptionPane.showMessageDialog(this, "Application status updated to " + status);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an application.");
        }
    }

    private void askFollowUpQuestion() {
        int selectedRow = applicationsTable.getSelectedRow();
        if (selectedRow != -1) {
            int applicationId = (int) model.getValueAt(selectedRow, 0);
            String question = JOptionPane.showInputDialog(this, "Enter follow-up question:");
            if (question != null && !question.trim().isEmpty()) {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "UPDATE applications SET follow_up_question = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, question);
                    stmt.setInt(2, applicationId);
                    stmt.executeUpdate();
                    loadApplications();
                    JOptionPane.showMessageDialog(this, "Follow-up question sent.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid question.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an application.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FacultyViewApplications(1).setVisible(true));
    }
}
