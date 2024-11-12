import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JComboBox<String> userTypeComboBox;

    public LoginScreen() {
        // Set title and exit behavior
        setTitle("Research Practice Application - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set frame size to 70% of screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.7), (int) (screenSize.height * 0.7));
        setLocationRelativeTo(null); // Center frame on screen

        // Background image
        JLabel background = new JLabel(new ImageIcon("bg.jpg"));
        background.setLayout(new GridBagLayout()); // Set layout manager for the background

        // Create the login panel with a rounded border and padding
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true)
        ));
        loginPanel.setBackground(new Color(255, 255, 255, 180)); // Semi-transparent white

        // Add components to the login panel
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField(20);

        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userTypeComboBox = new JComboBox<>(new String[]{"Student", "Faculty"});

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeComboBox.getSelectedItem();
                if (userType.equals("Student")) {
                    loginAsStudent(email, password);
                } else {
                    loginAsFaculty(email, password);
                }
            }
        });

        // Add components to the login panel with spacing
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(userTypeLabel);
        loginPanel.add(userTypeComboBox);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);

        // Add the login panel to the background with padding
        background.add(loginPanel, new GridBagConstraints());
        add(background);

        setVisible(true);
    }

    private void loginAsStudent(String email, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM students WHERE student_email = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int studentId = rs.getInt("student_id");
                        new StudentDashboard(studentId).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid credentials");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loginAsFaculty(String email, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM faculties WHERE faculty_email = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int facultyId = rs.getInt("faculty_id");
                        new FacultyDashboard(facultyId).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid credentials");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
