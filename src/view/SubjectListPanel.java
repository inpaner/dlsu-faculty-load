package view;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Category;
import model.Subject;

public class SubjectListPanel extends MigPanel {
    private ArrayList<Subject> subjects;
    
    private JTextField searchField;
    private JScrollPane subjectsPane;
    private DefaultTableModel model; 
    private JTable subjectsTable;
    private JButton add;
    private JButton edit;
    private JButton cancel;
    
    public SubjectListPanel(boolean isPopup) {
        subjects = Subject.subjectList();
        initializeComponents();
        if (isPopup)
            initPopup();
        addComponents();
        refreshTable();
    }
    
    private void initializeComponents() {
        initializeTable();
        
        searchField = new JTextField(20);
        searchField.addKeyListener(searchListener());
        searchField.requestFocus();
        subjectsPane = new JScrollPane(subjectsTable);
        
        add = new JButton("Add course");
        edit = new JButton("Edit course");
        cancel = new JButton("Back");
    }
    
    private void initPopup() {
        add.setText("Select Subject");
        edit.setText("Select None");
        cancel.setVisible(false);
    }
    
    private void addComponents() {
        add(createLabel("Search"), "wrap");
        add(searchField, "wrap 20");
        add(subjectsPane, "wrap 20");
        add(add, "span, split");
        add(edit);
        add(cancel);
    }
    
    private void initializeTable() {
        String[] columnNames = new String[] {"Code", "Units", "Category"};
        
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class, String.class, Category.class
            };
            
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectsTable = new JTable(model);
    }
    
    private void refreshTable() {
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        
        String[] data = new String[3];
        for (Subject subject: subjects) {
            data[0] = subject.code();
            data[1] = String.valueOf(subject.units());
            Category category = subject.category();
            if (category != null)
                data[2] = category.toString();
            else
                data[2] = "<None>";
            
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }
    
    private KeyListener searchListener() {
        return new KeyListener() {
        public void keyPressed(KeyEvent arg0) {}

        public void keyReleased(KeyEvent arg0) {
            String text = searchField.getText();
            text = text.toUpperCase();
            subjects = new ArrayList<>();
            for (Subject subject: Subject.subjectList()) {
                if (subject.code().toUpperCase().contains(text) ||
                    subject.category().toString().toUpperCase().contains(text)) {
                    subjects.add(subject);
                }
            }
            refreshTable();
        }
        public void keyTyped(KeyEvent arg0) {}
        };
    }

    public void addAddListener(ActionListener listener) {
        add.addActionListener(listener);
    }
    
    public void addEditListener(ActionListener listener) {
        edit.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
    
    public Subject selectedSubject() {
        int index = subjectsTable.getSelectedRow();
        return subjects.get(index);
    }
   
}
