package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.AltWorkloadPanel;
import view.Message;
import model.AltWorkload;
import model.FLException;
import model.Faculty;
import model.User;

public class AltWorkloadCtrl {
    AltWorkload workload;
    AltWorkloadPanel panel;
    Faculty faculty;
    
    // Adding
    protected AltWorkloadCtrl(Faculty faculty) {
        this.faculty = faculty;
        workload = new AltWorkload();
        workload.setFaculty(faculty);
        workload.setYear(User.year());
        workload.setTerm(User.term());
        
        panel = new AltWorkloadPanel();
        panel.addAcceptListener(addWorkload());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    // Editing
    protected AltWorkloadCtrl(AltWorkload workload) {
        this.workload = workload;
        this.faculty = workload.faculty();
        panel = new AltWorkloadPanel(workload);
        panel.addAcceptListener(editWorkload());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    private void getDetailsFromView() {
        workload.setLoadType(panel.loadType());
        workload.setUnits(panel.units());
        workload.setDescription(panel.description());
    }
    
    private ActionListener addWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                getDetailsFromView();
                faculty.checkIfCanAssign(workload);
                workload.add();
                Message.confirm("Workload added.");
                new FacultyCtrl(faculty);
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
            
        }
        };
    }
    
    private ActionListener editWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                getDetailsFromView();
                faculty.checkIfCanAssign(workload);
                workload.update();
                Message.confirm("Workload edited.");
                new FacultyCtrl(faculty);
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }
    
    private ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new FacultyCtrl(faculty);
        }
        };
    }
}
