import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JComboBox<String> userTypeComboBox;
    
    public LoginScreen() {
        setTitle("Research Practice Application - Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        emailField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        userTypeComboBox = new JComboBox<>(new String[]{"Student", "Faculty"});
        
        loginButton.addActionListener(new ActionListener() {
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
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(userTypeComboBox);
        add(loginButton);
    }

    private void loginAsStudent(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM students WHERE student_email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    new StudentDashboard(studentId).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loginAsFaculty(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM faculties WHERE faculty_email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int facultyId = rs.getInt("faculty_id");
                    new FacultyDashboard(facultyId).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
