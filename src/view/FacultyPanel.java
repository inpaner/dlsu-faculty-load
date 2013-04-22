package view;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import model.Faculty;
import model.FacultyStatus;
import net.miginfocom.swing.MigLayout;

public class FacultyPanel extends MigPanel {
    private Faculty faculty;
    private JTextField firstName;
    private JTextField lastName;

    private JTextField facultyID;
    private JComboBox<FacultyStatus> currentStatus;
    
    private JTabbedPane tabbedPane;
    
    private JButton accept;
    private JButton cancel;
    
    private boolean adding;
    
    // Adding
    public FacultyPanel() {
        adding = true;
        initializeComponents();
        initializeAdding();
        addComponents();
    }
    
    // Editing
    public FacultyPanel(Faculty faculty) {
        adding = false;
        this.faculty = faculty;
        initializeComponents();
        initializeEditing();
        addComponents();
    }
    
    
    private void initializeComponents() {
        firstName = new JTextField(15);
        lastName = new JTextField(15);
        
        facultyID = new JTextField(10);
        currentStatus = new JComboBox<>(FacultyStatus.statusVector());
        
        accept = new JButton();
        
        tabbedPane = new JTabbedPane();
        
        cancel = new JButton();
 
            
    }
    
    private void initializeAdding() {
        accept.setText("Add Faculty");
        cancel.setText("Cancel");
    }
    
    private void initializeEditing() {
        firstName.setText(faculty.firstName());
        lastName.setText(faculty.lastName());
        currentStatus.setSelectedItem(faculty.currentStatus());
        accept.setText("Save Changes");
        cancel.setText("Back");
    }
    
    private void addComponents() {
        addSeparator(this, "Name");
        add(createLabel("First Name"), "gap para, span, split");
        add(firstName, "");
        add(createLabel("Last Name"), "gap para");
        add(lastName,    "wrap para");
        add(createLabel("Current status: "), "gap para, span, split");
        add(currentStatus, "wrap");
        
        add(accept, "wrap para");
        if (!adding) {
            addSeparator(this, "Details");
            add(tabbedPane, "wrap para");
        }
        
        add(cancel, "gap para, right");
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
        
    }

    public boolean isFilledUp() {
        boolean valid = true;
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty())
            valid = false;
        return valid;
    }
    
    public String firstName() {
        return firstName.getText();
    }
    
    public String middleName() {
        return lastName.getText();
    }
    
    public String lastName() {
        return lastName.getText();
    }
    
    public FacultyStatus currentStatus() {
        return (FacultyStatus) currentStatus.getSelectedItem();
    }
    
    public void addChild(String title, Component child) {
        tabbedPane.addTab(title, child);
    }
    
}
