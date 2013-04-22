package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.FacultyTermPanel;
import view.MigPanel;
import view.TermPanel;
import view.TermSettable;

import model.Offering;
import model.User;

public class TermCtrl {
    private TermPanel panel;
    public static final int WORKLOADS = 0;
    public static final int FACULTIES = 1;
    public static final int SUMMARY = 2;
    
    
    
    public TermCtrl(int selected) {
        panel = new TermPanel(User.year(), User.term());
        panel.addCancelListener(back());
        panel.addSetTermListener(setTermListener());
        
        // Add tabs
        new TermWorkloads(panel);
        new TermFaculties(panel);
        new TermSummary(this, panel);
        panel.setSelectedChild(selected);
        
        Driver.setPanel(panel);
    }
    
    public ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new MainMenu();
        }
        };
    }
    
    
    private ActionListener setTermListener() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            User.setYearTerm(panel.year(), panel.term());
            panel.refresh();
        }
        };
    }
}
