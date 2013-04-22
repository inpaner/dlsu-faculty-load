package view;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.AltWorkload;
import model.LoadType;

public class AltWorkloadPanel extends MigPanel {
    private AltWorkload workload;
    private JComboBox<LoadType> loadTypes; 
    private JTextField units;
    private JTextArea description;
    private JButton accept;
    private JButton cancel;
    
    // Adding
    public AltWorkloadPanel() {
        initComponents();
        initAdding();
        addComponents();
    }
    
    // Editing
    public AltWorkloadPanel(AltWorkload workload) {
        this.workload = workload;
        initComponents();
        initEditing();
        addComponents();
    }
    
    private void initComponents() {
        loadTypes = new JComboBox<>(LoadType.loadTypesVector());
        units = new JTextField(4);
        description = new JTextArea(15, 30);
        accept = new JButton();
        cancel = new JButton();
    }
    
    private void initAdding() {
        accept.setText("Add Workload");
        cancel.setText("Cancel");
    }
    
    private void initEditing() {
        loadTypes.setSelectedItem(workload.loadType());
        units.setText(String.valueOf(workload.units()));
        description.setText(workload.description());
        accept.setText("Save Edits");
        cancel.setText("Cancel");
    }
    
    private void addComponents() {
        addSeparator(this, "Details");
        add(createLabel("Load Type: "), "gap para, span, split");
        add(loadTypes);
        add(createLabel("Units: "), "gap para");
        add(units, "wrap");
        addSeparator(this, "Description");
        add(description, "span, split, gap para, wrap para");
        add(accept);
        add(cancel);
    }
    
    public LoadType loadType() {
        return (LoadType) loadTypes.getSelectedItem();
    }
    
    public double units() {
        return Double.valueOf(units.getText());
    }
    
    public String description() {
        return description.getText();
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    
}
