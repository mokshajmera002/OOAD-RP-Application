import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentDashboard extends JFrame {
    private int studentId;
    private JButton applyButton, checkStatusButton;
    private JTextArea remarksArea;
    private JComboBox<String> facultyComboBox;
    
    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        setTitle("Student Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        facultyComboBox = new JComboBox<>();
        applyButton = new JButton("Apply for Research Practice");
        checkStatusButton = new JButton("Check Application Status");
        remarksArea = new JTextArea(5, 20);
        
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyForResearchPractice();
            }
        });
        
        checkStatusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkApplicationStatus();
            }
        });
        
        loadFaculties();
        
        setLayout(new FlowLayout());
        add(new JLabel("Select Faculty:"));
        add(facultyComboBox);
        add(new JLabel("Remarks:"));
        add(new JScrollPane(remarksArea));
        add(applyButton);
        add(checkStatusButton);
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
        int facultyId = facultyComboBox.getSelectedIndex() + 1;  // Assumption: Faculty ID matches index
        String remarks = remarksArea.getText();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO applications (student_id, faculty_id, application_description) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, facultyId);
                stmt.setString(3, remarks);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void checkApplicationStatus() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT status FROM applications WHERE student_id = ? ORDER BY application_id DESC LIMIT 1";
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
}
