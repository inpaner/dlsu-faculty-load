package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import model.Faculty;
import model.FacultyStatus;
import model.User;
import model.Workload;

public class FacultyListPanel extends MigPanel implements TermSettable<MigPanel> {
    private Vector<Faculty> filteredFaculties;
    private Workload workload;
    private int year;
    private int term;
    
    private JTextField searchField;
    private ArrayList<JCheckBox> filterBoxes;   
    private JCheckBox underloaded;
    private JCheckBox normalloaded;
    private JCheckBox overloaded;
    private JCheckBox prefersSubject;
    private JCheckBox notPrefersSubject;
    private JCheckBox fullTime;
    private JCheckBox partTime;
    private JCheckBox onLeave;
    private JCheckBox hasTaught;
    private JCheckBox hasntTaught;
    
    private JScrollPane facultyListPane;
    private DefaultTableModel model; 
    private JTable facultyListTable;
    private JButton setToDefault;
    
    private JButton accept; 
    private JButton cancel;
    private JButton assign;
    
    
    private boolean isPopup;
    
    // Term Panel
    public FacultyListPanel() {
        this.year = User.year();
        this.term = User.term();
        isPopup = false;
        filteredFaculties = Faculty.facultyList();
        initComponents();
        initTermPanel();
        addComponents();
        refresh();
    }
    
    // Pop-up
    public FacultyListPanel(Workload workload) {
        this.workload = workload;
        this.year = User.year();
        this.term = User.term();
        isPopup = true;
        filteredFaculties = Faculty.facultyList();
        initComponents();
        initPopup();
        addComponents();
        refresh();
    }
    
    private void initComponents() {
        initTable();
        
        searchField = new JTextField(20);
        searchField.addKeyListener(searchListener());
        
        underloaded = new JCheckBox("Underloaded");
        normalloaded = new JCheckBox("Normal Loaded");
        overloaded = new JCheckBox("Overloaded");
        prefersSubject = new JCheckBox("Prefers Subject");
        notPrefersSubject = new JCheckBox("Doesn't prefer Subject");
        fullTime = new JCheckBox("Full Time");
        partTime = new JCheckBox("Part Time");
        onLeave = new JCheckBox("On Leave");
        hasTaught = new JCheckBox("Has taught");
        hasntTaught = new JCheckBox("Hasn't taught");
        
        filterBoxes = new ArrayList<>();
        filterBoxes.add(underloaded);
        filterBoxes.add(normalloaded);
        filterBoxes.add(overloaded);
        filterBoxes.add(prefersSubject);
        filterBoxes.add(notPrefersSubject);
        filterBoxes.add(fullTime);
        filterBoxes.add(partTime);
        filterBoxes.add(onLeave);
        filterBoxes.add(hasTaught);
        filterBoxes.add(hasntTaught);

        for (JCheckBox box : filterBoxes) {
            box.addActionListener(filtersChanged());
            box.setSelected(false);
        }
        
        facultyListPane = new JScrollPane(facultyListTable);
        
        accept = new JButton();
        cancel = new JButton();
        assign = new JButton();
        setToDefault = new JButton("Set statuses to current");
    }
    
    private void initTermPanel() {
        accept.setText("New faculty");
        cancel.setText("Edit faculty");
        assign.setText("Assign workload");
    }
    
    private void initPopup() {
        accept.setText("Select faculty");
        cancel.setText("Select none");
        assign.setVisible(false);
        setToDefault.setVisible(false);
    }
    
    private void addComponents() {
        add(createLabel("Search: "), "");
        add(searchField, "wrap 20");
        add(underloaded, "span, split 3");
        add(normalloaded);
        add(overloaded);
        add(fullTime, "span, split 3");
        add(partTime);
        add(onLeave);
        if (isPopup) {
            add(prefersSubject, "");
            add(notPrefersSubject, "wrap");    
            add(hasTaught);
            add(hasntTaught, "wrap");
        }
        add(facultyListPane, "span, wrap");
        add(setToDefault, "span, right, wrap 20");
        add(accept, "span, split 3");
        add(cancel);  
        add(assign);
        
    }
    
    private void initTable() {
        
        String[] columnNames = new String[] {"Name", "Units", "Status"};
        
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
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
        
        facultyListTable = new JTable(model);
        // TODO convert to private inner class
        
        RowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        facultyListTable.setRowSorter(sorter);
        
    }
        
    public void refresh() {
        filterData();
        
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        String[] data = new String[3];
        for (Faculty faculty: filteredFaculties) {
            data[0] = faculty.toString();
            data[1] = String.valueOf(faculty.units(year, term));
            data[2] = String.valueOf(faculty.status(year, term));
            
            model.addRow(data);
        }
        model.fireTableDataChanged();
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
            filteredFaculties = Faculty.facultyList();
            return;
        }
        // early return
        
        
        filteredFaculties = new Vector<>();
        for (Faculty faculty : Faculty.facultyList()) {
            // Filter by units
            boolean unitsAdd = false;
            if (underloaded.isSelected() && faculty.isUnderloaded(year, term))
                unitsAdd = true;
            if (normalloaded.isSelected() && faculty.isNormalLoaded(year, term))
                unitsAdd = true;
            if (overloaded.isSelected() && faculty.isOverloaded(year, term))
                unitsAdd = true;
            if (!underloaded.isSelected() && !normalloaded.isSelected() && !overloaded.isSelected())
                unitsAdd = true;
            
            // Filter by status
            boolean statusAdd = false;
            FacultyStatus status = faculty.status(year, term);
            if (fullTime.isSelected() && status.equals(FacultyStatus.fullTime()))
                statusAdd = true;
            if (partTime.isSelected() && status.equals(FacultyStatus.partTime()))
                statusAdd = true;
            if (onLeave.isSelected() && status.equals(FacultyStatus.onLeave()))
                statusAdd = true;
            if (!fullTime.isSelected() && !partTime.isSelected() && 
                    !onLeave.isSelected())
                statusAdd = true;
            
            boolean historyAdd = true;
            boolean prefAdd = true;
            
            if (isPopup) {
                historyAdd = false;
                // Filter by teaching history
                if (hasTaught.isSelected() && faculty.hasTaught(workload.subject()))
                    historyAdd = true;
                if (hasntTaught.isSelected() && !faculty.hasTaught(workload.subject()))
                    historyAdd = true;
                if (!hasTaught.isSelected() && !hasntTaught.isSelected())
                    historyAdd = true;
                
                // Filter by preference
                prefAdd = false;
                if (prefersSubject.isSelected() && faculty.prefers(workload.subject()))
                    prefAdd = true;
                if (notPrefersSubject.isSelected() && !faculty.prefers(workload.subject()))
                    prefAdd = true;
                if (!prefersSubject.isSelected() && !notPrefersSubject.isSelected())
                    prefAdd = true;
            }
            
            // Filter by search
            boolean searchAdd = true;
            if (!faculty.lastName().contains(text) && !faculty.firstName().contains(text))
                searchAdd = false;
            
            if (unitsAdd && statusAdd && historyAdd && prefAdd && searchAdd)
                filteredFaculties.add(faculty);
        }
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
    
    
    public void addSetToFullTimeListener(ActionListener listener) {
        setToDefault.addActionListener(listener);
    }
    
    public void addMouseListener(MouseAdapter adapter) {
        facultyListTable.addMouseListener(adapter);
    }
    
    private ActionListener filtersChanged() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            refresh();
        }
        };
    }
    
    public Faculty selectedFaculty() {
        Faculty faculty = null;
        int index = facultyListTable.getSelectedRow();
        if (index >= 0) {
            faculty = filteredFaculties.get(index);
        }
        return faculty;
    }

    public Vector<Faculty> filteredFaculties() {
        return filteredFaculties;
    }
    
    @Override
    public void setTerm(int year, int term) {
        this.year = year;
        this.term = term;
    }
    
}
