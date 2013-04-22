package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Category;
import model.Faculty;

import view.FacultyPanel;
import view.FacultyPreferencePanel;

public class FacultyPreference {
    private Faculty faculty;
    private FacultyCtrl parent;
    private FacultyPanel parentPanel;
    private FacultyPreferencePanel panel;
    
    protected FacultyPreference(FacultyCtrl parent, 
            FacultyPanel parentPanel) {
        this.faculty = parent.faculty();
        this.parent = parent;
        this.parentPanel = parentPanel;
        
        panel = new FacultyPreferencePanel(parent.faculty());
        panel.addAddListener(addPreference());
        panel.addEditListener(removePreference());
        parentPanel.addChild("Preferences", panel);
    }
    
    private ActionListener addPreference() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Category category = CategorySearch.dialog();
            faculty.addCategory(category);
            if (faculty.updatePreferredCategories()) {
                System.out.println("Success");
                panel.refresh();
            }
            else {
                System.out.println("Fail");
            }
        }
        };
    }
    
    private ActionListener removePreference() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Category category = panel.selectedCategory();
            faculty.removeCategory(category);
            if (faculty.updatePreferredCategories()) {
                System.out.println("Success");
                panel.refresh();
            }
            else {
                System.out.println("Fail");
            }
        }
        };
    }
}
