package view;

import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Category;
import model.Faculty;

public class CategoryListPanel extends MigPanel {
    protected Vector<Category> categories;
    private JScrollPane categoriesPane;
    private JTable categoriesTable;
    protected DefaultTableModel model;
    protected JButton add;
    protected JButton edit;
    protected JButton cancel;
    
    public CategoryListPanel(boolean isPopup) {
        initComponents();
        addComponents();
        if (isPopup)
            initPopup();
        refresh();
    }
    
    private void initComponents() {
        initializeTable();
        categoriesPane = new JScrollPane(categoriesTable);
        add = new JButton("Add Category");
        edit = new JButton("Edit Category");
        cancel = new JButton("Back");
    }
    
    public void initPopup() {
        add.setText("Select Category");
        edit.setText("Select None");
        cancel.setText("Cancel");
    }
    
    private void addComponents() {
        add(categoriesPane, "wrap");
        add(add, "span, split 3");
        add(edit);
        add(cancel);
    }
    
    private void initializeTable() {
        String[] columnNames = new String[] {
                "Category"
        };
        model = new DefaultTableModel(columnNames, 0) {
            Class[] columnTypes = new Class[] {
                String.class
            };
            
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoriesTable = new JTable(model);
        //categoriesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        categoriesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
    }
    
    public void refresh() {
        for (int i = model.getRowCount() - 1; i >= 0 ; i--) {
            model.removeRow(i);
        }
        
        refreshCategories();
        String[] data = new String[1];
        for (Category category : categories) {
            data[0] = category.toString();
            model.addRow(data);
        }
        model.fireTableDataChanged();
    }
    
    protected void refreshCategories() {
        categories = Category.categoriesVector();
    }
    
    public Category selectedCategory() {
        Category category = null;
        int index = categoriesTable.getSelectedRow();
        if (index >= 0) {
            category = categories.get(index);
        }
        return category;
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
}
