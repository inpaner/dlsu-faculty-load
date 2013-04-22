package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import view.CategoryPanel;
import view.Message;
import model.Category;
import model.FLException;

public class CategoryCtrl {
    private Category category;
    private CategoryPanel panel;
    
    // Adding
    public CategoryCtrl() {
        category = new Category();
        panel = new CategoryPanel();
        panel.addAcceptListener(new AddCategory());
        panel.addCancelListener(new Back());
        Driver.setPanel(panel);
    }
    
    public CategoryCtrl(Category category) {
        if (category != null) { 
            this.category = category;
            panel = new CategoryPanel(category);
            panel.addAcceptListener(new EditCategory());
            panel.addDeleteListener(new DeleteCategory());
            panel.addCancelListener(new Back());
            Driver.setPanel(panel);
        }
    }
    
    private void getDetailsFromView() {
        category.setName(panel.name());
    }
    
    private class AddCategory implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.fieldsComplete()) 
                return;
            getDetailsFromView();
            try {
                category.add();
                Message.confirm("Added category");
                new CategoryList();
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
    }
    
    private class EditCategory implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.fieldsComplete()) 
                return;
            getDetailsFromView();
            try {
                category.update();
                Message.confirm("Edited category");
                new CategoryList();
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
    }
    
    private class DeleteCategory implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Message.verified("Delete category?")) {
                category.delete();
                Message.confirm("Category deleted");
                new CategoryList();
            }
        }
    }
    
    private class Back implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new CategoryList();
        }
        
    }
}
