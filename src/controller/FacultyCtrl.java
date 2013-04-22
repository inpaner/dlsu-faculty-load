package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.FLException;
import model.Faculty;
import model.Initializer;

import view.FacultyPanel;
import view.MainFrame;
import view.Message;

public class FacultyCtrl {
    private FacultyPanel panel;
    private Faculty faculty;
    
    // Add Faculty
    public FacultyCtrl() {
        faculty = new Faculty();
        panel = new FacultyPanel();
        panel.addAcceptListener(addFacultyListener());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    // Edit Faculty
    public FacultyCtrl(Faculty faculty) {
        this.faculty = faculty;
        
        panel = new FacultyPanel(faculty);
        panel.addAcceptListener(editFaculty());
        panel.addCancelListener(back());
        
        new FacultyWorkloads(this, panel);
        new FacultyHistory(this, panel);
        new FacultyPreference(this, panel);
        Driver.setPanel(panel);
    }
    
    private void setDetailsFromView() {
        faculty.setLastName(panel.lastName());
        faculty.setFirstName(panel.firstName());
        faculty.setCurrentStatus(panel.currentStatus());
    }
    
    protected Faculty faculty() {
        return faculty;
    }

    private ActionListener addFacultyListener() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.isFilledUp()) 
                return;
            setDetailsFromView();
            try {
                faculty.add();
                Message.confirm(faculty.toString() + " added successfully.");
                new TermCtrl(TermCtrl.FACULTIES);
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }

    private ActionListener editFaculty() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.isFilledUp()) 
                return;
            setDetailsFromView();
            try {
                faculty.update();
                Message.confirm(faculty.toString() + " edited successfully.");
                new TermCtrl(TermCtrl.FACULTIES);
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
                new TermCtrl(TermCtrl.FACULTIES);
            }
        };
    }
}
