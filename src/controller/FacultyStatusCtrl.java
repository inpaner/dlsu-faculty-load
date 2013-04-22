package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.FLException;
import model.FacultyStatus;

import view.FacultyStatusPanel;
import view.Message;

public class FacultyStatusCtrl {
    private FacultyStatusPanel panel;
    
    public FacultyStatusCtrl() {
        panel = new FacultyStatusPanel();
        panel.addAcceptListener(editValues());
        panel.addCancelListener(back());
        
        Driver.setPanel(panel);
        
    }
    
    public ActionListener editValues() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FacultyStatus status = panel.status();
            if (!panel.valuesValid()) return; 
            
            status.setMinUnits(panel.minUnits());
            status.setSoftMaxUnits(panel.softMaxUnits());
            status.setMaxUnits(panel.maxUnits());
            status.setMaxSpecialClasses(panel.maxSpecialClasses());
            status.setMaxSubjects(panel.maxSubjects());
            try {
                status.update();
                Message.confirm(status.toString() + " edited.");
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }
    
    public ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new MainMenu();
        }
        };
    }
}
