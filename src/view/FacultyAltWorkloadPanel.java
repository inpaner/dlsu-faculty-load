package view;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.AltWorkload;
import model.Faculty;
import model.Offering;
import model.User;

public class FacultyAltWorkloadPanel extends MigPanel implements TermSettable<MigPanel> {
    private Faculty faculty;
    
    private Vector<AltWorkload> workloadList;
    private Vector<AltWorkload> filteredWorkloads;
    
    private JScrollPane workloadsPane;
    private JTable workloadsTable;
    private DefaultTableModel model;
    
    private JButton addWorkload;
    private JButton editWorkload;
    private JButton deleteWorkload;
    
    
    
    public FacultyAltWorkloadPanel(Faculty faculty) {
        this.faculty = faculty;
        setTerm(User.year(), User.term());
        initializeComponents();
        addComponents();
        refresh();
    }
    
    public void initializeComponents() {
        initializeTable();
        workloadsPane = new JScrollPane(workloadsTable);
        addWorkload = new JButton("Add new workload");
        editWorkload = new JButton("Edit workload");
        deleteWorkload = new JButton("Delete workload");
        
    }
    
    public void addComponents() {
        add(workloadsPane, "center, wrap");
        add(addWorkload, "span, split 3");
        add(editWorkload, "");
        add(deleteWorkload);
        
    }
    
    private void initializeTable() {
        String[] columnNames = new String[] {"Type", "Description", "Units"};
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class, String.class, String.class, 
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
        workloadsTable.getColumnModel().getColumn(0).setPreferredWidth(8);
        workloadsTable.getColumnModel().getColumn(1).setPreferredWidth(5);
    }
    
    public AltWorkload selectedWorkload() {
        AltWorkload workload = null;
        int index = workloadsTable.getSelectedRow();
        if (index >= 0)
            workload = filteredWorkloads.get(index);
        return workload;
    }
    
    public void addAddWorkloadListener(ActionListener listener) {
        addWorkload.addActionListener(listener);
    }
    
    public void addEditWorkloadListener(ActionListener listener) {
        editWorkload.addActionListener(listener);
    }
    
    public void addDeleteWorkloadListener(ActionListener listener) {
        deleteWorkload.addActionListener(listener);
    }
    
    public void refresh() {
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        
        String[] data = new String[3];
        for (AltWorkload workload: filteredWorkloads) {
            data[0] = workload.loadType().name();
            data[1] = workload.description();
            data[2] = String.valueOf(workload.units());
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }
    
    public void setTerm(int year, int term) {
        workloadList = faculty.altWorkloadList(year, term);
        filteredWorkloads = workloadList;
    }

    public void filterData() {
    }
}
