import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyDashboard extends JFrame {
    private int facultyId;
    private JButton viewApplicationsButton;
    private JTable applicationsTable;
    
    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Faculty Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        viewApplicationsButton = new JButton("View Applications");
        viewApplicationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewApplications();
            }
        });
        
        applicationsTable = new JTable();
        
        setLayout(new BorderLayout());
        add(viewApplicationsButton, BorderLayout.NORTH);
        add(new JScrollPane(applicationsTable), BorderLayout.CENTER);
    }
    
    private void viewApplications() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Student Name", "Description", "Status"}, 0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT s.student_name, a.application_description, a.status FROM applications a " +
                         "JOIN students s ON a.student_id = s.student_id WHERE a.faculty_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, facultyId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("student_name"),
                        rs.getString("application_description"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        applicationsTable.setModel(model);
    }
}
