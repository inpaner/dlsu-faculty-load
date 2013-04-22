    package view;

import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MainMenuPanel extends MigPanel {
    private JButton facultyManager;
    private JButton offeringManager;
    private JButton summary; 
    
    private JButton subjectManager;
    private JButton categoryManager;
    
    private JButton editSettings;
    private JButton logout;
    
    public MainMenuPanel() {
        initializeComponents();
        addComponents();
    }
    
    private void initializeComponents() {
        facultyManager = new JButton("Faculty Manager");
        offeringManager = new JButton("Offering Manager");
        summary = new JButton("Term Summary");
        subjectManager = new JButton("Course Manager");
        categoryManager = new JButton("Category Manager");
        editSettings = new JButton("Edit Settings");
        logout = new JButton("Logout");
    }
    
    private void addComponents() {
        add(offeringManager, "growx, wrap");
        add(facultyManager, "growx, wrap");
        add(summary, "grow, wrap para");
        add(subjectManager, "growx, wrap");
        add(categoryManager, "growx, wrap para");
        add(editSettings, "growx, wrap para");
        add(logout);
        
    }
    
    public void addFacultyManagerListener(ActionListener listener) {
        facultyManager.addActionListener(listener);
    }
    
    public void addOfferingManagerListener(ActionListener listener) {
        offeringManager.addActionListener(listener);
    }
    
    public void addSummaryListener(ActionListener listener) {
        summary.addActionListener(listener);
    }
    
    public void addSubjectManagerListener(ActionListener listener) {
        subjectManager.addActionListener(listener);
    }
    
    public void addCategoryManagerListener(ActionListener listener) {
        categoryManager.addActionListener(listener);
    }
    
    public void addEditSettingsListener(ActionListener listener) {
        editSettings.addActionListener(listener);
    }
    
    public void addLogoutListener(ActionListener listener) {
        logout.addActionListener(listener);
    }
}
