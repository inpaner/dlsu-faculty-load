package view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.*;

import model.Faculty;
import model.FacultyStatus;
import model.Offering;
import model.User;

// TODO find a way to extend TermPanel instead 
public class FacultyTermPanel extends MigPanel {
    private Faculty faculty;
    private int year;
    private int term;
    
    private JTextField yearField;
    private JRadioButton term1;
    private JRadioButton term2;
    private JRadioButton term3;
    private JRadioButton termS;
    private JButton setTerm;
    private JLabel units;
    private JComboBox<FacultyStatus> statusBox;
    private JTabbedPane tabbedPane;
    private ArrayList<TermSettable<MigPanel>> children;
    
    public FacultyTermPanel(Faculty faculty) {
        this.faculty = faculty;
        children = new ArrayList<>();
        this.year = User.year();
        this.term = User.term();
        initComponents();
        addComponents();
    }
    
    private void initComponents() {
        yearField = new JTextField(4);
        yearField.setText(String.valueOf(year));
        
        term1 = new JRadioButton("1");
        term2 = new JRadioButton("2");
        term3 = new JRadioButton("3");
        termS = new JRadioButton("Summer");
        
        switch(term) {
            case 1 :    term1.setSelected(true);
                        break;
            case 2 :    term2.setSelected(true);
                        break;
            case 3 :    term3.setSelected(true);
                        break;
            case 4 :    termS.setSelected(true);
                        break;
        }
        setTerm = new JButton("Set Term");
        statusBox = new JComboBox<>(FacultyStatus.statusVector());
        statusBox.setSelectedItem(faculty.status(year, term));
        
        ButtonGroup termGroup = new ButtonGroup();
        termGroup.add(term1);
        termGroup.add(term2);
        termGroup.add(term3);
        termGroup.add(termS);
        
        units = new JLabel();
        refreshUnits();
        tabbedPane = new JTabbedPane();
    }

    private void addComponents() {
        addSeparator(this, "Term");
        add(yearField, "gap para, span, split");
        add(term1);
        add(term2);
        add(term3);
        add(termS);
        add(setTerm, "gap para, wrap para");
        add(createLabel("Units: "), "gap para, span, split");
        add(units, "");
        add(createLabel("Status: "), "gap para");
        add(statusBox, "wrap para");
        add(tabbedPane, "wrap para");
    }
    
    private void refreshUnits() {
        String text = String.valueOf(faculty.units(year(), term()));
        units.setText(text);
    }
    
    public int year() {
        return Integer.valueOf(yearField.getText());
    }
    
    public int term() {
        int termValue = 1;
        if (term2.isSelected())
            termValue = 2;
        else if (term3.isSelected())
            termValue = 3;
        else if (termS.isSelected())
            termValue = 4;
        
        return termValue;
    }
    
    public FacultyStatus status() {
        FacultyStatus status = (FacultyStatus) statusBox.getSelectedItem();
        if (status.isDefault())
            status = null;
        return status;
    }
    
    public void addChild(String title, TermSettable<MigPanel> child) {
        tabbedPane.addTab(title, (Component) child);
        children.add(child);
    }
   
    public void addSetTermListener(ActionListener listener) {
        setTerm.addActionListener(listener);
    }
    
    public void addStatusBoxListener(ActionListener listener) {
        statusBox.addActionListener(listener);
    }       
    
    public void refresh() {
        year = User.year();
        term = User.term();
        refreshUnits();
        statusBox.setSelectedItem(faculty.status(year, term));
        for (TermSettable<MigPanel> child : children) {
            child.setTerm(year(), term());
            child.refresh();
        }
    }
}
