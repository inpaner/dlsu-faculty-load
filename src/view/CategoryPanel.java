package view;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import model.Category;

public class CategoryPanel extends MigPanel {
    private Category category;
    private JTextField name;
    private JButton accept;
    private JButton delete;
    private JButton cancel;
    
    public CategoryPanel() {
        category = new Category();
        initComponents();
        initAdding();
        addComponents();
    }
    
    public CategoryPanel(Category category) {
        this.category = category;
        initComponents();
        initEditing();
        addComponents();
    }
    
    
    private void initComponents() {
        name = new JTextField(10);
        accept = new JButton();
        delete = new JButton();
        cancel = new JButton();
    }
    
    private void initAdding() {
        accept.setText("Add category");
        delete.setVisible(false);
        cancel.setText("Cancel");
    }
    
    private void initEditing() {
        name.setText(category.name());
        accept.setText("Save changes");
        delete.setText("Delete category");
        cancel.setText("Back");
    }
    
    private void addComponents() {
        add(createLabel("Name: "));
        add(name, "wrap");
        
        add(accept, "span, split");
        add(delete);
        add(cancel, "right");
    }
    
    public boolean fieldsComplete() {
        boolean complete = true;
        if (name.getText().isEmpty()) {
            complete = false;
            name.setBackground(Color.PINK);
        }
        return complete;
    }
    
    public String name() {
        return name.getText();
    }
    
    public void addAcceptListener(ActionListener listener) {
        accept.addActionListener(listener);
    }
    
    public void addDeleteListener(ActionListener listener) {
        delete.addActionListener(listener);
    }
        
    public void addCancelListener(ActionListener listener) {
        cancel.addActionListener(listener);
    }
}
