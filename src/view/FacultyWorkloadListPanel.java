package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Faculty;
import model.Offering;
import model.User;
import model.Workload;

// Very similar to WorkloadListPanel
// Find a clean way to extend WorkloadListPanel
public class FacultyWorkloadListPanel extends MigPanel implements TermSettable<MigPanel> {
    private Faculty faculty;
    protected Vector<Workload> filteredWorkloads;
    private int year;
    private int term;
    
    private JTextField searchField;
    private ArrayList<JCheckBox> filterBoxes;
    
    private JScrollPane offeringsPane;
    protected JTable workloadsTable;
    protected DefaultTableModel model;
    
    private JButton addWorkload;
    private JButton editWorkload;
    private JButton deleteWorkload;
    
    public FacultyWorkloadListPanel(Faculty faculty) {
        this.faculty = faculty;
        setTerm(User.year(), User.term());
        initializeComponents();
        addComponents();
        refresh();
    }
    
    public void initializeComponents() {
        initializeTable();
        
        searchField = new JTextField(20);
        searchField.addKeyListener(searchListener());
        
        filterBoxes = new ArrayList<>();
        
        for (JCheckBox box : filterBoxes) {
            box.addActionListener(filtersChanged());
            box.setSelected(false);
        }
                
        offeringsPane = new JScrollPane(workloadsTable);
        
        addWorkload = new JButton("Assign workload");
        editWorkload = new JButton("Edit workload");
        deleteWorkload = new JButton("Unassign workload");
    }
    
    public void addComponents() {
        add(createLabel("Search: "), "");
        add(searchField, "wrap 20");
        
        add(offeringsPane, "wmin 500, span, wrap");
        add(addWorkload, "span, split 3");
        add(editWorkload, "");
        add(deleteWorkload);
    }
    
    private void initializeTable() {
        String[] columnNames = new String[] {
                "Code", "Section", "Start Time", "End Time", 
                "Days", "Room", "Units"
        };
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class, String.class, String.class, String.class, 
                String.class, String.class, String.class
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
        workloadsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        workloadsTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        workloadsTable.getColumnModel().getColumn(6).setPreferredWidth(40);
        
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
        Offering prevOffering = null;
        
        for (Workload workload: filteredWorkloads) {
            String[] data = new String[7];
            data[0] = workload.offering().subject().code();
            data[1] = workload.offering().section();
            data[2] = workload.offering().startTimeStr();
            data[3] = workload.offering().endTimeStr();
            data[4] = workload.dayString();
            data[5] = workload.room();
            data[6] = String.valueOf(workload.units());
            if (workload.offering().equals(prevOffering)) {
                data[0] = "";
                data[1] = "";
                data[6] = "";
            }
            
            prevOffering = workload.offering();
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }
    
    protected void filterData() {
        String text = searchField.getText();
        text = text.toUpperCase();
        
        boolean noFilters = true;
        for (JCheckBox box : filterBoxes) {
            if (box.isSelected()) {
                noFilters = false;
                break;
            }
        }
        
        filteredWorkloads = new Vector<>();
        // TODO This is dumb. Get workloads from faculty
        if (noFilters && searchField.getText().isEmpty()) {
            filteredWorkloads = faculty.workloadList(year, term);
            return;
        }
        // early return
        
        for (Workload workload : faculty.workloadList(year, term)) {
            
            // filter by being assigned
            boolean assignedAdd = false;
            
            boolean searchAdd = true;
            if (!workload.code().contains(text))
                searchAdd = false;
            
            if (assignedAdd && searchAdd)
                filteredWorkloads.add(workload);
        }
        
    }
    
    public void setTerm(int year, int term) {
        this.year = year;
        this.term = term;
    }
    
    public void addAssignOfferingListener(ActionListener listener) {
        addWorkload.addActionListener(listener);
    }
    
    public void addEditOfferingListener(ActionListener listener) {
        editWorkload.addActionListener(listener);
    }
    
    public void addUnassignWorkloadListener(ActionListener listener) {
        deleteWorkload.addActionListener(listener);
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
