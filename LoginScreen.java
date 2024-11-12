import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// RoundedButton class definition (for rounded buttons)
class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false); // Makes the button's content area transparent
        setFocusPainted(false); // Remove the focus border
        setBorderPainted(false); // Remove the border
        setFont(new Font("Arial", Font.BOLD, 14)); // Set a bold font for the button text
        setForeground(Color.WHITE); // Set text color
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isPressed()) {
            g.setColor(new Color(0, 102, 204)); // Button color when pressed
        } else if (getModel().isRollover()) {
            g.setColor(new Color(0, 128, 255)); // Button color when hovered
        } else {
            g.setColor(new Color(0, 153, 255)); // Default button color
        }

        g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Draw rounded corners
        super.paintComponent(g); // Draw the button text
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 50); // Set preferred size for the button
    }
}


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

        // Background image or gradient color
        JLabel background = new JLabel(new ImageIcon("bg.jpg"));
        background.setLayout(new GridBagLayout()); // Set layout manager for the background

        // Create login panel with rounded border, padding, and shadow
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 230)); // Slightly transparent white
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 2, 10, 10, new Color(200, 200, 200)), // Outer matte border
                BorderFactory.createEmptyBorder(40, 40, 40, 40) // Inner padding
        ));

        // Apply a shadow effect
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(50, 50, 50, 50),
                BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(0, 0, 0))));

        // Custom font for labels
        Font labelFont = new Font("SansSerif", Font.PLAIN, 18);

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField = new JTextField("", 50);
        emailField.setForeground(Color.GRAY);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(emailField);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField("", 50);
        passwordField.setForeground(Color.GRAY);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        styleTextField(passwordField);

        // User type combo box
        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setFont(labelFont);
        userTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userTypeComboBox = new JComboBox<>(new String[] { "Student", "Faculty" });
        styleComboBox(userTypeComboBox);

        // Login button with rounded corners and shadow
        loginButton = new RoundedButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String userType = (String) userTypeComboBox.getSelectedItem();
            // // Implement your login logic here
            if (userType.equals("Student")) {
                loginAsStudent(email, password);
            } else {
                loginAsFaculty(email, password);
            }
            // JOptionPane.showMessageDialog(LoginScreen.this, "Logged in as: " + userType);
        });

        // Add components to the login panel with spacing
        // Add components to the login panel with spacing
        loginPanel.add(emailLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(emailField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(userTypeLabel);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(userTypeComboBox);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(loginButton);

        // Add the login panel to the background with padding
        background.add(loginPanel, new GridBagConstraints());
        add(background);

        setVisible(true);
    }

    // Helper method to style JTextFields
    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setOpaque(false);
    }

    // Helper method to style JComboBox
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
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
