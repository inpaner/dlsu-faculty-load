package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import model.FacultyStatus;

import controller.Driver;

public class FacultyStatusPanel extends MigPanel {
    private Driver driver;
    private JComboBox<FacultyStatus> statuses;
    private JTextField minUnits;
    private JTextField softMaxUnits;
    private JTextField maxSpecialClasses;
    private JTextField maxUnits;
    private JTextField maxSubjects;
    private JButton accept;
    private JButton cancel;
    
    public FacultyStatusPanel() {
        initComponents();
        addComponents();
        refreshValues();
    }
    
    private void initComponents() {
        statuses = new JComboBox<>(FacultyStatus.statusVectorWithoutDefault());
        statuses.addActionListener(changeStatus());
        minUnits = new JTextField(5);
        softMaxUnits = new JTextField(5);
        maxSpecialClasses = new JTextField(5);
        maxUnits = new JTextField(5);
        maxSubjects = new JTextField(5);
        accept = new JButton("Save changes");
        cancel = new JButton("Back");
    }
    
    private void addComponents() {
        add(createLabel("Status: "), "gap para, right");
        add(statuses, "wrap para");
        add(createLabel("Min units: "), "gap para, right");
        add(minUnits, "wrap");
        add(createLabel("Soft max units: "), "gap para, right");
        add(softMaxUnits, "wrap");
        add(createLabel("Max units: "), "gap para, right");
        add(maxUnits, "wrap");
        add(createLabel("Max special classes: "), "gap para, right");
        add(maxSpecialClasses, "wrap");
        add(createLabel("Max courses: "), "gap para, right");
        add(maxSubjects, "wrap");
        
        add(accept, "span, split");
        add(cancel);
    }
    
    public int minUnits() {
        return Integer.valueOf(minUnits.getText());
    }
    public int softMaxUnits() {;
        return Integer.valueOf(softMaxUnits.getText());
    }
    
    public int maxUnits() {
        return Integer.valueOf(maxUnits.getText());
    }
    
    public int maxSpecialClasses() {
        return Integer.valueOf(maxSpecialClasses.getText());
    }
    
    public int maxSubjects() {
        return Integer.valueOf(minUnits.getText());
    }
    
    
    public boolean valuesValid() {
        boolean valid = false;
        try {
            minUnits();
            softMaxUnits();
            maxUnits();
            maxSpecialClasses();
            maxSubjects();
            valid = true;
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Values must be integers.",
                    "Number Format Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return valid;
    }
    
    private void refreshValues() {
        FacultyStatus status = status();
        minUnits.setText(String.valueOf(status.minUnits()));
        softMaxUnits.setText(String.valueOf(status.softMaxUnits()));
        maxUnits.setText(String.valueOf(status.maxUnits()));
        maxSpecialClasses.setText(String.valueOf(status.maxSpecialClasses()));
        maxSubjects.setText(String.valueOf(status.maxSubjects()));
    }
    
    
    private ActionListener changeStatus() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshValues();
        }
        };
    }
    
    public FacultyStatus status() {
        return (FacultyStatus) statuses.getSelectedItem();
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    

    
}
