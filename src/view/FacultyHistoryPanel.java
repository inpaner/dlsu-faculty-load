package view;

import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Faculty;
import model.Offering;
import model.Subject;

public class FacultyHistoryPanel extends MigPanel { 
    private Faculty faculty;
    private JScrollPane historyPane;
    private JTable historyTable;
    private DefaultTableModel model;
    
    
    public FacultyHistoryPanel(Faculty faculty) {
        this.faculty = faculty;
        initComponents();
        addComponents();
        refresh();
    }
    
    private void initComponents() {
        initializeTable();
        historyPane = new JScrollPane(historyTable);
    }
    
    private void addComponents() {
        add(historyPane);
    }
    
    private void initializeTable() {
        String[] columnNames = new String[] {"Code", "Sections taught"};
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class, Integer.class
            };
            
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(model);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(8);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(5);
    }
    
    public void refresh() {
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        
        String[] data = new String[3];
        HashMap<Subject, Integer> history = faculty.history();
        
        for (Subject subject : history.keySet()) {
            data[0] = subject.toString();
            data[1] = String.valueOf(history.get(subject));
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }
}
