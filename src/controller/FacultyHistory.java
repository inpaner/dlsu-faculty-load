package controller;

import view.FacultyHistoryPanel;
import view.FacultyPanel;

public class FacultyHistory {
    private FacultyCtrl parent;
    private FacultyPanel parentPanel;
    private Driver driver;
    
    protected FacultyHistory(FacultyCtrl parent, 
            FacultyPanel parentPanel) {
        this.parent = parent;
        this.parentPanel = parentPanel;
        this.driver = driver;
        
        FacultyHistoryPanel panel = new FacultyHistoryPanel(parent.faculty());
        parentPanel.addChild("History", panel);
    }
}
