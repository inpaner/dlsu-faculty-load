package view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.*;

import net.miginfocom.swt.MigLayout;

import model.Faculty;
import model.Offering;

public class TermPanel extends MigPanel {
    private JTextField yearField;
    private JRadioButton term1;
    private JRadioButton term2;
    private JRadioButton term3;
    private JRadioButton termS;
    private JButton setTerm;
    private JTabbedPane tabbedPane;
    private ArrayList<TermSettable<MigPanel>> children;
    private JButton cancel;
    
    public TermPanel(int initYear, int initTerm) {
        children = new ArrayList<>();
        initComponents(initYear, initTerm);
        addComponents();
    }
    
    private void initComponents(int year, int term) {
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
        
        ButtonGroup termGroup = new ButtonGroup();
        termGroup.add(term1);
        termGroup.add(term2);
        termGroup.add(term3);
        termGroup.add(termS);
        
        tabbedPane = new JTabbedPane();
        
        cancel = new JButton("Back");
    }

    private void addComponents() {
        addSeparator(this, "Term");
        add(yearField, "gap para, span, split");
        add(term1);
        add(term2);
        add(term3);
        add(termS);
        add(setTerm, "gap para, wrap para");
        add(tabbedPane, "span, split, wmin 600, wrap para");
        add(cancel, "right");
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
    
    public void setSelectedChild(int index) {
        tabbedPane.setSelectedIndex(index);
    }
    
    public void addChild(String title, TermSettable<MigPanel> child) {
        tabbedPane.addTab(title, (Component) child);
        children.add(child);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    public void addSetTermListener(ActionListener listener) {
        setTerm.addActionListener(listener);
    }
    
    public void refresh() {
        for (TermSettable<MigPanel> child : children) {
            child.setTerm(year(), term());
            child.refresh();
        }
    }
}
