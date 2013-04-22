package view;

import java.util.Vector;

import model.Faculty;

public class FacultyPreferencePanel extends CategoryListPanel {
    private Faculty faculty;
    
    public FacultyPreferencePanel(Faculty faculty) {
        super(false);
        this.faculty = faculty;
        edit.setText("Remove Category");
        cancel.setVisible(false);
        refresh();
    }
    
    protected void refreshCategories() {
        // null check required because super() calls
        // this function prior to initialization of faculty
        if (faculty != null)            
            categories = faculty.preferredCategories();
        else
            categories = new Vector<>();
            
    }
}
