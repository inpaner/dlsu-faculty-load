package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import model.Day;
import model.Faculty;
import model.Workload;

public class WorkloadPanel extends MigPanel {
    private Workload workload;
    private Faculty faculty;
    private JLabel facultyTitle; 
    private JLabel facultyLabel;
    private JButton setFaculty;
    private JTextField room;
    
    private JButton mw;
    private JButton th;
    private JCheckBox mon;
    private JCheckBox tue;
    private JCheckBox wed;
    private JCheckBox thu;
    private JCheckBox fri;
    private JCheckBox sat;
    
    private JButton accept;
    private JButton cancel;
    
    // Adding
    public WorkloadPanel(boolean isInitial) {
        initComponents();
        initAdding(isInitial);
        addComponents();
    }
    
    // Editing
    public WorkloadPanel(Workload workload) {
        this.workload = workload;
        this.faculty = workload.faculty();
        initComponents();
        initEditing();
        addComponents();
    }
    
    
    private void initComponents() {
        facultyTitle = new JLabel("Faculty: ");
        
        facultyLabel = new JLabel("<None>");
        setFaculty = new JButton("Set Faculty");
        room = new JTextField(6);
        
        mw = new JButton("MW");
        th = new JButton("TH");
        mon = new JCheckBox("Mon");
        tue = new JCheckBox("Tue");
        wed = new JCheckBox("Wed");
        thu = new JCheckBox("Thu");
        fri = new JCheckBox("Fri");
        sat = new JCheckBox("Sat");
        
        mw.addActionListener(mwListener());
        th.addActionListener(thListener());
        
        accept = new JButton();
        cancel = new JButton();
    }

    private void initAdding(boolean isInitial) {
        facultyTitle.setVisible(false);
        facultyLabel.setVisible(false);
        setFaculty.setVisible(false);
        accept.setText("Add Assignment");
        cancel.setText("Cancel");
        if (isInitial) {
            accept.setVisible(false);
            cancel.setVisible(false);
        }
        
    }

    private void initEditing() {
        if (workload.hasFaculty())
            facultyLabel = new JLabel(workload.faculty().toString());
        
        room.setText(workload.room());
        
        for (Day day : workload.dayList()) {
            switch (day) {
            case MONDAY:    
                mon.setSelected(true);
                break;
            case TUESDAY:   
                tue.setSelected(true);            
                break;
            case WEDNESDAY: 
                wed.setSelected(true);
                break;
            case THURSDAY:  
                thu.setSelected(true);
                break;
            case FRIDAY:    
                fri.setSelected(true);
                break;
            case SATURDAY:  
                sat.setSelected(true);
            default:        
                break;
            }
        }
        accept.setText("Edit Assignment");
        cancel.setText("Delete Assignment");
    }

    private void addComponents() {
        add(facultyTitle);
        add(facultyLabel);
        add(setFaculty, "gap para, wrap");
        add(createLabel("Room: "));
        add(room, "wrap para");
        
        add(mw, "wmin 50, span, split 2");
        add(th, "wmin 50, wrap");
        add(mon, "span, split 6");
        add(tue);
        add(wed);
        add(thu);
        add(fri);
        add(sat, "wrap 20");
        
        add(accept, "span, split");
        add(cancel);
    }

    public Faculty faculty() {
        return faculty;
    }
    
    public String room() {
        return room.getText();
    }
    
    public ArrayList<Day> dayList() {
        ArrayList<Day> list = new ArrayList<>();
        if (mon.isSelected())
            list.add(Day.MONDAY);
        if (tue.isSelected())
            list.add(Day.TUESDAY);
        if (wed.isSelected())
            list.add(Day.WEDNESDAY);
        if (thu.isSelected())
            list.add(Day.THURSDAY);
        if (fri.isSelected())
            list.add(Day.FRIDAY);
        if (sat.isSelected())
            list.add(Day.SATURDAY);
        return list;
    }
    
    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
        refreshFacultyLabel();   
    }

    private void refreshFacultyLabel() {
        if (faculty == null)
            facultyLabel.setText("<None>");
        else
            facultyLabel.setText(faculty.toString());
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    public void addSetFacultyListener(ActionListener listener) {
        setFaculty.addActionListener(listener);
    }
    
    private ActionListener mwListener() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (mon.isSelected() && wed.isSelected()) {
                mon.setSelected(false);
                wed.setSelected(false);
            }
            else {
                mon.setSelected(true);
                tue.setSelected(false);
                wed.setSelected(true);
                thu.setSelected(false);
                fri.setSelected(false);
                sat.setSelected(false);
            }
        }
        };
    }
    
    private ActionListener thListener() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (tue.isSelected() && thu.isSelected()) {
                tue.setSelected(false);
                thu.setSelected(false);
            }
            else {
                mon.setSelected(false);
                tue.setSelected(true);
                wed.setSelected(false);
                thu.setSelected(true);
                fri.setSelected(false);
                sat.setSelected(false);
            }
        }
        };
    }
    
    public boolean valuesValid() {
        boolean complete = true;
        if (dayList().isEmpty()) {
            // TODO bring down to model
            complete = false;
            Message.deny("Workload requires at least one day.");
        }
        return complete;
    }
}
