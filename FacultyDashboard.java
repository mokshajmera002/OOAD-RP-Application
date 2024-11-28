import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FacultyDashboard extends JFrame {
    private int facultyId;
    private JButton viewApplicationsButton, updateDomainButton;
    private JPanel contentPanel;

    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Faculty Dashboard");

        // Initialize components
        viewApplicationsButton = new JButton("View Student Applications");
        viewApplicationsButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewApplicationsButton.setBackground(new Color(70, 130, 180)); // Steel blue color
        viewApplicationsButton.setForeground(Color.WHITE);

        updateDomainButton = new JButton("Update domain/current projects");
        updateDomainButton.setFont(new Font("Arial", Font.BOLD, 16));
        updateDomainButton.setBackground(new Color(70, 130, 180)); // Steel blue color
        updateDomainButton.setForeground(Color.WHITE);

        viewApplicationsButton.setLayout(null);
        viewApplicationsButton.setLocation(500,500);    
        // Adding ActionListener to open application view
        viewApplicationsButton.addActionListener(e -> new FacultyViewApplications(facultyId).setVisible(true));
        updateDomainButton.addActionListener(e -> handleUpdateDomain());
        // Set up background image
        setContentPane(createBackgroundPanel());

        // Set up the main content panel with a rounded rectangle shape
        contentPanel = createRoundedPanel();
        contentPanel.add(viewApplicationsButton);
        contentPanel.add(updateDomainButton);
        viewApplicationsButton.setLocation(500,500);    

        // Add the content panel to the main frame
        add(contentPanel);
        setupFrame();
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
        panel.setOpaque(false); // Makes the panel transparent to show background image
        panel.setLayout(new GridBagLayout()); // Center button
        panel.setPreferredSize(new Dimension(600, 400)); // 70% of screen width
        return panel;
    }

    private JPanel createBackgroundPanel() {
        // Use a panel with an image background
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("bg.jpg"); // Set your background image path
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private void setupFrame() {
        setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7)); // 70% screen size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        // Remove the undecorated line to keep the title bar
    }

    private void handleUpdateDomain() {
        // Create a JTextArea for domain input
        JTextArea domainArea = new JTextArea(5, 30); // 5 rows, 30 columns
        domainArea.setLineWrap(true);
        domainArea.setWrapStyleWord(true);
    
        // Wrap JTextArea in a JScrollPane for better usability
        JScrollPane scrollPane = new JScrollPane(domainArea);
    
        // Display a custom dialog with the text area
        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Update Domain/Current Projects",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
    
        if (result == JOptionPane.OK_OPTION) {
            String newDomain = domainArea.getText().trim();
            if (!newDomain.isEmpty()) {
                try (Connection conn = DBConnection.getConnection()) {
                    String query = "UPDATE faculties SET domain = ? WHERE faculty_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
    
                    stmt.setString(1, newDomain);
                    stmt.setInt(2, facultyId);
    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Domain updated successfully!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update domain. Please try again.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "An error occurred while updating the domain.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Domain cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyDashboard(1).setVisible(true);
        });
    }
}
