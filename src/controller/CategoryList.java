package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Category;
import view.CategoryListPanel;

public class CategoryList {
    private CategoryListPanel panel;
    
    public CategoryList() {
        panel = new CategoryListPanel(false);
        panel.addAddListener(addCategoryt());
        panel.addEditListener(editSubject());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    private ActionListener addCategoryt() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new CategoryCtrl();
        }
        };
    }
    
    private ActionListener editSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Category category = panel.selectedCategory();
            new CategoryCtrl(category);    
        }
        };
    }
    

    private ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new MainMenu();
        }
        };
    }
}
