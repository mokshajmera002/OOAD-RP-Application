
import javax.swing.*;

public class FacultyDashboard extends JFrame {
    public int facultyId;
    public JButton viewApplicationsButton;

    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Faculty Dashboard");

        viewApplicationsButton = new JButton("View Student Applications");

        viewApplicationsButton.addActionListener(e -> new FacultyViewApplications(facultyId).setVisible(true));
        System.out.println("dashboard id" + facultyId);

        addComponents();
        setupFrame();
    }

    private void addComponents() {
        add(viewApplicationsButton);
    }

    private void setupFrame() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}