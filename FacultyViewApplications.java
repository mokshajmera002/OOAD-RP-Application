import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class FacultyViewApplications extends JFrame {
    private int facultyId;
    private JTable applicationsTable;
    private DefaultTableModel model;

    public FacultyViewApplications(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Manage Student Applications");

        model = new DefaultTableModel(new String[]{"Application ID", "Student ID", "Description", "Status", "Follow-up Question", "Student Response"}, 0);
        applicationsTable = new JTable(model);

        JButton acceptButton = new JButton("Accept");
        JButton rejectButton = new JButton("Reject");
        JButton askQuestionButton = new JButton("Ask Follow-up Question");

        acceptButton.addActionListener(e -> updateApplicationStatus("Accepted"));
        rejectButton.addActionListener(e -> updateApplicationStatus("Rejected"));
        askQuestionButton.addActionListener(e -> askFollowUpQuestion());

        loadApplications();
        System.out.println("kgj" + facultyId);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(askQuestionButton);

        add(new JScrollPane(applicationsTable), "Center");
        add(buttonPanel, "South");

        setupFrame();
    }

    private void setupFrame() {
        setSize(800, 400);
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
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("follow_up_question"),
                    rs.getString("student_response")
                    
                });
                System.out.println("print" + rs);
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
}
