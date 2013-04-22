package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Faculty;
import model.Offering;
import model.User;
import model.Workload;

public class WorkloadListPanel extends MigPanel implements TermSettable<MigPanel> {
    private int year;
    private int term;
    private Faculty faculty;
    private Vector<Workload> filteredWorkloads;
    
    private JTextField searchField;
    private JCheckBox unassigned;
    private JCheckBox assigned;
    private JCheckBox validOnly;
    private JCheckBox prefersOnly;
    private JCheckBox hasTaughtOnly;
    
    private ArrayList<JCheckBox> filterBoxes;
    
    private JScrollPane offeringsPane;
    private JTable workloadsTable;
    private DefaultTableModel model;
    
    private JButton accept;
    private JButton cancel;
    private JButton assign;
    
    private boolean isPopup;
    
    // Term Panel
    public WorkloadListPanel() {    
        this.year = User.year();
        this.term = User.term();
        isPopup = false;
        initComponents();
        initTermPanel();
        addComponents();
        refresh();
    }
    
    // Pop-up
    public WorkloadListPanel(Faculty faculty) {
        this.faculty = faculty;
        this.year = User.year();
        this.term = User.term();
        isPopup = true;
        initComponents();
        initPopup();
        addComponents();
        refresh();
    }
    
    private void initComponents() {
        initTable();
        
        searchField = new JTextField(20);
        searchField.addKeyListener(searchListener());
        
        assigned = new JCheckBox("Assigned");
        unassigned = new JCheckBox("Unassigned");
        validOnly = new JCheckBox("Valid only");
        prefersOnly = new JCheckBox("Prefers only");
        hasTaughtOnly = new JCheckBox("Has taught only");
        
        filterBoxes = new ArrayList<>();
        filterBoxes.add(assigned);
        filterBoxes.add(unassigned);
        filterBoxes.add(validOnly);
        filterBoxes.add(prefersOnly);
        filterBoxes.add(hasTaughtOnly);
        
        for (JCheckBox box : filterBoxes) {
            box.addActionListener(filtersChanged());
            box.setSelected(false);
        }
                
        offeringsPane = new JScrollPane(workloadsTable);
        
        accept = new JButton();
        cancel = new JButton();
        assign = new JButton();
    }
    
    private void initTermPanel() {
        accept.setText("New offering");
        cancel.setText("Edit offering"); 
        assign.setText("Assign faculty");
    }
    
    
    private void initPopup() {
        accept.setText("Select workload");
        cancel.setText("Select none");    
        assign.setVisible(false);
    }
    
    private void addComponents() {
        add(createLabel("Search: "), "");
        add(searchField, "wrap 20");
        
        add(assigned, "span, split 2");
        add(unassigned);
        if (isPopup) {
            add(validOnly, "span, split 3");
            add(prefersOnly);
            add(hasTaughtOnly);
        }
        
        add(offeringsPane, "wmin 500, span, wrap");
        add(accept, "span, split 3");
        add(cancel, "");
        add(assign);
    }
    
    private void initTable() {
        String[] columnNames = new String[] {
                "Code", "Section", "Start Time", "End Time", 
                "Days", "Room", "Units", "Faculty" 
        };
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class, String.class, String.class, String.class, 
                String.class, String.class, String.class, String.class
            };
            
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        workloadsTable = new JTable(model);
        workloadsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        workloadsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        workloadsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        workloadsTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        workloadsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        workloadsTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        workloadsTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        workloadsTable.getColumnModel().getColumn(6).setPreferredWidth(40);
        workloadsTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        
    }
    
    public Workload selectedWorkload() {
        Workload workload = null;
        int index = workloadsTable.getSelectedRow();
        if (index >= 0)
            workload = filteredWorkloads.get(index);
        return workload;
    }

    //TODO combine refresh and setTerm
    public void refresh() {
        filterData();
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        Offering previousOffering = null;
        
        for (Workload workload: filteredWorkloads) {
            String[] data = new String[8];
            if (!workload.offering().equals(previousOffering)) { 
                data[0] = workload.offering().subject().code();
                data[1] = workload.offering().section();
                data[2] = workload.offering().startTimeStr();
                data[3] = workload.offering().endTimeStr();
            }
            else {
                data[0] = "";
                data[1] = "";
                data[2] = "";
                data[3] = "";
            }
            data[4] = workload.dayString();
            data[5] = workload.room();
            data[6] = String.valueOf(workload.units());
            
            Faculty faculty = workload.faculty();
            if (faculty != null)
                data[7] = faculty.toString();  
            else
                data[7] = "";
            model.addRow(data);
            previousOffering = workload.offering();
        }
        model.fireTableDataChanged();
    }
    
    private void filterData() {
        String text = searchField.getText();
        text = text.toUpperCase();
        
        boolean noFilters = true;
        for (JCheckBox box : filterBoxes) {
            if (box.isSelected()) {
                noFilters = false;
                break;
            }
        }
        
        if (noFilters && searchField.getText().isEmpty()) {
            filteredWorkloads = Workload.workloadList(year, term);
            return;
        }
        // early return
        
        filteredWorkloads = new Vector<>();
        for (Workload workload : Workload.workloadList(year, term)) {
            // filter by being assigned
            boolean assignedAdd = false;
            if (assigned.isSelected() && workload.hasFaculty())
                assignedAdd = true;
            if (unassigned.isSelected() && !workload.hasFaculty())
                assignedAdd = true;
            if (!assigned.isSelected() && !unassigned.isSelected())
                assignedAdd = true;
            
            // singular filters
            boolean hasTaughtAdd = true;
            boolean prefersAdd = true;
            boolean overlapAdd = true;
            if (isPopup) {
                hasTaughtAdd = false;
                if (hasTaughtOnly.isSelected() && faculty.hasTaught(workload.subject()))
                    hasTaughtAdd = true;
                else if (!hasTaughtOnly.isSelected())
                    hasTaughtAdd = true;
                
                prefersAdd = false;
                if (prefersOnly.isSelected() && faculty.prefers(workload.subject()))
                    prefersAdd = true;
                else if (!prefersOnly.isSelected())
                    prefersAdd = true;
                
                overlapAdd = false;
                Workload overlap = faculty.hasOverlap(workload);
                if (validOnly.isSelected() && overlap == null)
                    overlapAdd = true;
                else if (!validOnly.isSelected())
                    overlapAdd = true;
                
                
            }
            
            boolean searchAdd = true;
            // TODO bring code and category down
            if (!workload.offering().subject().code().contains(text))
                searchAdd = false;
            
            if (assignedAdd && hasTaughtAdd && prefersAdd && overlapAdd && searchAdd)
                filteredWorkloads.add(workload);
            
        }
    }
    
    public void setTerm(int year, int term) {
        this.year = year;
        this.term = term;
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    public void addAssignListener(ActionListener listener) {
        assign.addActionListener(listener);
    }
    
    
    public void addMouseListener(MouseAdapter adapter) {
        workloadsTable.addMouseListener(adapter);
    }
    
    
    
    private ActionListener filtersChanged() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            refresh();
        }
        };
    }
    
    private KeyListener searchListener() {
        return new KeyListener() {
            public void keyPressed(KeyEvent arg0) {}

            public void keyReleased(KeyEvent arg0) {
                refresh();
            }
            public void keyTyped(KeyEvent arg0) {}
        };
    }
}
