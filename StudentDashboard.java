import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private int studentId;
    private JButton applyButton, checkStatusButton, logoutButton, respondToQuestionButton;
    private JTextArea remarksArea;
    private JComboBox<String> facultyComboBox;
    private JPanel mainPanel;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;

        // Set up the frame size to cover 70% of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.7);
        int height = (int) (screenSize.height * 0.7);
        setSize(width, height);

        // Center the frame
        setLocationRelativeTo(null);
        setTitle("Student Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set up the main panel with a background image
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("bg.jpg"); // replace with your background image path
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout()); // Center components
        mainPanel.setOpaque(false);

        // Create the content panel with rounded borders and a semi-transparent
        // background
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(width - 300, height - 200));
        contentPanel.setBackground(new Color(255, 255, 255, 255)); // semi-transparent background
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true)); // Rounded border
        contentPanel.setLayout(new GridBagLayout());

        // Initialize the UI components
        facultyComboBox = new JComboBox<>();
        applyButton = createButton("Apply for Research Practice");
        checkStatusButton = createButton("Check Application Status");
        logoutButton = createButton("Logout");
        respondToQuestionButton = createButton("Respond to Follow-up");

        remarksArea = new JTextArea(5, 20);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        remarksArea.setFont(new Font("Arial", Font.PLAIN, 14));
        remarksArea.setBackground(new Color(211, 211, 211)); // Light grey background
        remarksArea.setForeground(Color.BLACK); // Black text color (default)

        applyButton.addActionListener(e -> applyForResearchPractice());
        checkStatusButton.addActionListener(e -> checkApplicationStatus());
        logoutButton.addActionListener(e -> dispose()); // Close application on logout
        respondToQuestionButton.addActionListener(e -> showFollowUpQuestionDialog());

        // Load faculty data
        loadFaculties();

        // GridBag layout settings for arranging components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Place components in grid
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Select Faculty:"), gbc);

        gbc.gridx = 1;
        contentPanel.add(facultyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Statement of purpose"), gbc);

        gbc.gridx = 1;
        contentPanel.add(new JScrollPane(remarksArea), gbc);

        // Arrange buttons in pairs
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(applyButton, gbc);

        gbc.gridx = 1;
        contentPanel.add(checkStatusButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(respondToQuestionButton, gbc);

        gbc.gridx = 1;
        contentPanel.add(logoutButton, gbc);

        // Add the content panel to the main panel
        mainPanel.add(contentPanel);

        // Add the main panel to the frame
        add(mainPanel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        if(text.equalsIgnoreCase("Logout")){
            button.setBackground(new Color(225, 0, 0));
        }
        else
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void loadFaculties() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT faculty_id, faculty_name FROM faculties";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    facultyComboBox.addItem(rs.getString("faculty_name"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void applyForResearchPractice() {
        int facultyId = facultyComboBox.getSelectedIndex() + 1;
        String remarks = remarksArea.getText();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO applications (student_id, faculty_id, description, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, facultyId);
                stmt.setString(3, remarks);
                stmt.setString(4, "pending");
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            }
        } catch (SQLException ex) {
            if ((ex.toString()).contains("Duplicate")) {
                JOptionPane.showMessageDialog(this, "Application with current professor already exists");
            }
            ex.printStackTrace();
        }
    }

    private void checkApplicationStatus() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT status FROM applications WHERE student_id = ? ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Application Status: " + rs.getString("status"));
                } else {
                    JOptionPane.showMessageDialog(this, "No application found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showFollowUpQuestionDialog() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT follow_up_question FROM applications WHERE student_id = ? AND follow_up_question IS NOT NULL ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String followUpQuestion = rs.getString("follow_up_question");
                    JTextArea responseArea = new JTextArea(5, 20);
                    responseArea.setLineWrap(true);
                    responseArea.setWrapStyleWord(true);

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(new JLabel("Question from Faculty:"), BorderLayout.NORTH);
                    panel.add(new JScrollPane(new JTextArea(followUpQuestion, 3, 20)), BorderLayout.CENTER);
                    panel.add(new JLabel("Your Response:"), BorderLayout.SOUTH);
                    panel.add(new JScrollPane(responseArea), BorderLayout.SOUTH);

                    int option = JOptionPane.showConfirmDialog(this, panel, "Respond to Follow-up Question",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String followUpResponse = responseArea.getText();
                        submitFollowUpResponse(followUpResponse);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No follow-up question to respond to.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void submitFollowUpResponse(String response) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE applications SET student_response = ? WHERE student_id = ? AND follow_up_question IS NOT NULL ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, response);
                stmt.setInt(2, studentId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Response submitted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error submitting response.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard(1).setVisible(true));
    }
}
