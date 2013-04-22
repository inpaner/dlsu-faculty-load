package view;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.*;

import model.Category;
import model.Subject;

@SuppressWarnings("serial")
public class SubjectPanel extends MigPanel {
    private JTextField code;
    private JTextField units;
    private JComboBox<Category> categories;
    
    private JButton accept;
    private JButton delete;
    private JButton cancel;
    
    // adding
    public SubjectPanel() {
        initComponents();
        initAdding();
        addComponents();
    }
    
    // editing
    public SubjectPanel(Subject subject) {
        initComponents();
        initEditing(subject);
        addComponents();
    }
    
    private void initComponents() {
        code = new JTextField(7);
        // set code max length to 7;
        final int limit = 7;
        code.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if(getLength() + str.length() <= limit)
                    super.insertString(offs, str, a);
            }
        });
        units = new JTextField(3);
        categories = new JComboBox<>(Category.categoriesVector());
        accept = new JButton();
        delete = new JButton();
        cancel = new JButton();
    }
    
    private void initAdding() {
        accept.setText("Add subject");
        delete.setVisible(false);
        cancel.setText("Cancel");
    }
    
    private void initEditing(Subject subject) {
        code.setText(subject.code());
        units.setText(String.valueOf(subject.units()));
        categories.setSelectedItem(subject.category());
        accept.setText("Save changes");
        delete.setText("Delete subject");
        cancel.setText("Back");
    }
    
    private void addComponents() {
        addSeparator(this, "Details");
        add(createLabel("Code: "), "gap para");
        add(code, "wrap");
        add(createLabel("Units: "), "gap para");
        add(units, "wrap");
        add(createLabel("Category: "), "gap para");
        add(categories, "wrap");
        
        add(accept, "span, split");
        add(delete);
        add(cancel, "right");
    }
    
    public String code() {
        return code.getText();
    }
    
    public double units() {
        return Double.valueOf(units.getText());
    }
    
    public Category category() {
        return (Category) categories.getSelectedItem();
    }
    
    public boolean fieldsComplete() {
        boolean complete = true;
        if (code.getText().isEmpty()) {
            complete = false;
            code.setBackground(Color.PINK);
        }
        else
            code.setBackground(Color.WHITE);
        
        if (units.getText().isEmpty()) {
            complete = false;
            units.setBackground(Color.PINK);
        }
        else
            units.setBackground(Color.WHITE);
        
        return complete;
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

