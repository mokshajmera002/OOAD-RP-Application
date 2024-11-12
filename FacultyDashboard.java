import javax.swing.*;
import java.awt.*;

public class FacultyDashboard extends JFrame {
    private int facultyId;
    private JButton viewApplicationsButton;
    private JPanel contentPanel;

    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Faculty Dashboard");

        // Initialize components
        viewApplicationsButton = new JButton("View Student Applications");
        viewApplicationsButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewApplicationsButton.setBackground(new Color(70, 130, 180));  // Steel blue color
        viewApplicationsButton.setForeground(Color.WHITE);
        
        // Adding ActionListener to open application view
        viewApplicationsButton.addActionListener(e -> new FacultyViewApplications(facultyId).setVisible(true));
        
        // Set up background image
        setContentPane(createBackgroundPanel());

        // Set up the main content panel with a rounded rectangle shape
        contentPanel = createRoundedPanel();
        contentPanel.add(viewApplicationsButton);

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
                g2.setColor(new Color(255, 255, 255, 200));  // White with transparency
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        panel.setOpaque(false);  // Makes the panel transparent to show background image
        panel.setLayout(new GridBagLayout());  // Center button
        panel.setPreferredSize(new Dimension(600, 400));  // 70% of screen width
        return panel;
    }

    private JPanel createBackgroundPanel() {
        // Use a panel with an image background
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("bg.jpg");  // Set your background image path
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private void setupFrame() {
        setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.7), 
                (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7)); // 70% screen size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the frame
        // Remove the undecorated line to keep the title bar
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FacultyDashboard(1).setVisible(true);
        });
    }
}
