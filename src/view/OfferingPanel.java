package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import model.OfferingStatus;
import model.Workload;
import model.Offering;
import model.Subject;

public class OfferingPanel extends MigPanel {
    private int year;
    private int term;
    private Offering offering;
    private Subject subject;
    private JLabel subjectLabel;
    private JButton selectSubject;
    private JTextField section;
    
    private JButton start0800;
    private JButton start0940;
    private JButton start1120;
    private JButton start1300;
    private JButton start1440;
    private JButton start1620;
        
    private JTextField startTime;
    private JTextField endTime;
    
    private JButton accept;
    private JButton delete;
    private JButton cancel;
    
    private JTabbedPane assignmentsPane;
    
    private JButton newWorkload;
    
    private JComboBox<OfferingStatus> offeringStatus;
    
    private boolean adding;
    
    // Adding
    public OfferingPanel(int year, int term) {
        adding = true;
        this.year = year;
        this.term = term;
        initializeComponents();
        initializeAdding();
        addComponents();
    }
    
    // Editing
    public OfferingPanel(Offering offering) {
        adding = false;
        this.offering = offering;
        this.year = offering.year();
        this.term = offering.term();
        subject = offering.subject();
        term = offering.term();
        initializeComponents();
        initializeEditing();
        addComponents();
    }
    
    private void initializeComponents() {
        subjectLabel = new JLabel("<None>");
        selectSubject = new JButton("Select course");
        section = new JTextField(4);
              
        offeringStatus = new JComboBox<>(OfferingStatus.allStatuses());

        start0800 = new JButton("08:00");
        start0940 = new JButton("09:40");
        start1120 = new JButton("11:20");
        start1300 = new JButton("13:00");
        start1440 = new JButton("14:40");
        start1620 = new JButton("16:20");
        start0800.addActionListener(setTime());
        start0940.addActionListener(setTime());
        start1120.addActionListener(setTime());
        start1300.addActionListener(setTime());
        start1440.addActionListener(setTime());
        start1620.addActionListener(setTime());

        startTime = new JTextField(5);
        endTime = new JTextField(5);
        
        accept = new JButton();
        delete = new JButton("Delete");
        
        assignmentsPane = new JTabbedPane();
        
        newWorkload = new JButton("Add workload");
        cancel = new JButton();  
    }
    
    private void initializeAdding() {
        subjectLabel.setText("<None>");
        accept.setVisible(false);
        delete.setVisible(false);
        newWorkload.setText("Add Offering");
        cancel.setText("Cancel");
    }
    
    private void initializeEditing() {
        subjectLabel.setText(subject.code());
        selectSubject.setVisible(false);
        // TODO remove test null!
        if (offering.startTimeStr() != null) {
            startTime.setText(offering.startTimeStr());
        }
        if (offering.endTimeStr() != null) {
            String time = offering.endTimeStr();
            endTime.setText(time);
        }
        section.setText(offering.section());
        offeringStatus.setSelectedItem(offering.status());
        accept.setText("Edit Offering");
        cancel.setText("Back");
    }
    
    private void addComponents() {
        addSeparator(this, "Details");
        add(createLabel("Term:"), "gap para");
        String termDisplay = String.valueOf(year) + "/" + String.valueOf(term);
        add(createLabel(termDisplay), "wrap");
        add(createLabel("Course: "), "gap para");
        add(subjectLabel, "gap, span, split ");
        add(selectSubject, "wrap");
        add(createLabel("Section: "), "gap para");
        add(section, "wrap");
        add(createLabel("Status: "), "gap para");
        add(offeringStatus, "wrap para");
        
      
        addSeparator(this, "Schedule");
        add(start0800, "gap para, span, split");
        add(start0940);
        add(start1120);
        add(start1300);
        add(start1440);
        add(start1620, "wrap");

        add(createLabel("Time Start: "), "gap para");
        add(startTime, "");        
        add(createLabel("Time End: "),  "gap");
        add(endTime, "wrap para");
        
        add(accept, "span, split");
        add(delete, "wrap para");
        
        addSeparator(this, "Assignments");
        add(assignmentsPane, "wmin 300, hmin 200, span, split, wrap para");
        add(newWorkload, "span, split, wrap para");
        
        add(cancel, "span, split, right, wrap para");
    }
    
    public void setSubject(Subject subject) {
        if (adding) {
            this.subject = subject;
            refreshSubjectLabel();
        }
    }

    private void refreshSubjectLabel() {
        if (subject == null)
            subjectLabel.setText("<None>");
        else
            subjectLabel.setText(subject.code());
    }
    
    public Subject subject() {
        return subject;
    }
    
    public String section() {
        return section.getText();
    }
    
    public String startTime() {
        //TODO checky check
        return startTime.getText();
    }
    
    public String endTime() {
        return endTime.getText();
    }
    
    public OfferingStatus status() {
        return (OfferingStatus) offeringStatus.getSelectedItem();
    }
    
    
    public void clearTabs() {
        assignmentsPane.removeAll();
    }
    
    public void addNewTab(String tabName, WorkloadPanel child) {
        assignmentsPane.addTab(tabName, child);
        int index = assignmentsPane.getComponentCount();
        assignmentsPane.setSelectedIndex(index - 1);
    }
    
    public void addSelectSubjectListener(ActionListener listener) {
        selectSubject.addActionListener(listener);
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addDeleteListener(ActionListener listener) {
        delete.addActionListener(listener);
    }
    
    public void addNewWorkloadListener(ActionListener listener) {
        newWorkload.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    public void setSelectedWorkload(WorkloadPanel panel) {
        // TODO should be not needed when workload added automatically
        if (assignmentsPane.getTabCount() > 0) {
            if (panel != null)
                assignmentsPane.setSelectedComponent(panel);
            else
                assignmentsPane.setSelectedIndex(0); // TODO remove soon
        }
    }
    
    public boolean valuesValid() {
        boolean complete = true;
        if (subject == null) {
            complete = false;
            Message.deny("Requires subject.");
        }
        if (section.getText().isEmpty()) {
            complete = false;
            section.setBackground(Color.PINK);
        }
        if (startTime.getText().isEmpty()) {
            complete = false;
            startTime.setBackground(Color.PINK);
        }
        if (endTime.getText().isEmpty()) {
            complete = false;
            endTime.setBackground(Color.PINK);
        }
        return complete;
    }
    
    private ActionListener setTime() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO try an enum
            if (e.getSource().equals(start0800)) {
                startTime.setText("08:00");
                endTime.setText("09:30");              
            }
            else if (e.getSource().equals(start0940)) {
                startTime.setText("09:40");
                endTime.setText("11:10");              
            } 
            else if (e.getSource().equals(start1120)) {
                startTime.setText("11:20");
                endTime.setText("12:50");              
            } 
            else if (e.getSource().equals(start1300)) {
                startTime.setText("13:00");
                endTime.setText("14:30");              
            } 
            else if (e.getSource().equals(start1440)) {
                startTime.setText("14:40");
                endTime.setText("16:10");              
            } 
            else if (e.getSource().equals(start1620)) {
                startTime.setText("16:20");
                endTime.setText("17:50");              
            }     
        }
        };
    }
      
    public void confirmAdd() {
        JOptionPane.showMessageDialog(this, 
                "Offering Added.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE
            );
    }
    
    public void denyAdd() {
        JOptionPane.showMessageDialog(this, 
                "Subject with same section already exists. Add unsuccessful.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
    }
    
    
    public void confirmEdit() {
        JOptionPane.showMessageDialog(this, 
                "Offering Edited.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE
            );
    }
    
    public void denyEdit() {
        JOptionPane.showMessageDialog(this, 
                "Subject with same section already exists. Add unsuccessful.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
    }
    
    public boolean verifyDelete() {
        int option = JOptionPane.showConfirmDialog(this, 
                        "Delete Offering?", 
                        "Verification", 
                        JOptionPane.YES_NO_OPTION
                    );
        return option == JOptionPane.YES_OPTION;
    }
    
    public void confirmDelete() {
        JOptionPane.showMessageDialog(this, 
                "Offering Deleted.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE
            );
    }
}
