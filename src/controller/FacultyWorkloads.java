package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.AltWorkload;
import model.FLException;
import model.Faculty;
import model.FacultyStatus;
import model.Offering;
import model.User;
import model.Workload;
import view.FacultyAltWorkloadPanel;
import view.FacultyPanel;
import view.FacultyTermPanel;
import view.FacultyWorkloadListPanel;
import view.Message;

public class FacultyWorkloads {
    private Faculty faculty;
    private FacultyCtrl parent;
    private FacultyPanel parentPanel;
    private FacultyTermPanel facultyTermP;
    private FacultyWorkloadListPanel workloadPanel;
    private FacultyAltWorkloadPanel altWorkloadP;
    
    public FacultyWorkloads(FacultyCtrl parent, FacultyPanel parentPanel) {
        this.parent = parent;
        this.parentPanel = parentPanel;
        this.faculty = parent.faculty();
        initChildPanels();
    }

    private void initChildPanels() {
        facultyTermP = new FacultyTermPanel(faculty);
        
        facultyTermP.addSetTermListener(setTerm());
        facultyTermP.addStatusBoxListener(updateStatus());
        parentPanel.addChild("Workloads", facultyTermP);
        
        workloadPanel = new FacultyWorkloadListPanel(parent.faculty());
        workloadPanel.addAssignOfferingListener(assignWorkload());
        workloadPanel.addEditOfferingListener(editOffering());
        workloadPanel.addUnassignWorkloadListener(new UnassignWorkload());
        String hdr = "Workloads: " + faculty.workloadUnits(User.year(), User.term());
        facultyTermP.addChild(hdr, workloadPanel);
        
        altWorkloadP = new FacultyAltWorkloadPanel(parent.faculty());
        altWorkloadP.addAddWorkloadListener(addAltWorkload());
        altWorkloadP.addEditWorkloadListener(editAltWorkload());
        altWorkloadP.addDeleteWorkloadListener(new DeleteAltWorkload());
        hdr = "Alternate workloads: " + faculty.altWorkloadUnits(User.year(), User.term());
        facultyTermP.addChild(hdr, altWorkloadP);
    }
    
    private ActionListener assignWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Workload workload = Search.workload(faculty);
            if (workload == null) 
                return;
            // early return
            
            try {
                workload.replaceFaculty(faculty);
                workload.update();
                Message.confirm("Workload assigned.");
                new FacultyCtrl(faculty);
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }
    
    private ActionListener editOffering() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Workload workload = workloadPanel.selectedWorkload();
            if (workload != null)
                new OfferingCtrl(workload, parent.faculty());
        }
        };
    }
    
    private class UnassignWorkload implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            Workload workload = workloadPanel.selectedWorkload();
            if (workload != null && Message.verified("Unassign workload?")) {
                workload.replaceFaculty(null);
                try {
                    workload.update();
                    Message.confirm("Workload unassigned.");
                    new FacultyCtrl(parent.faculty());
                }
                catch (FLException ex) {
                    Message.deny(ex.getLocalizedMessage());
                }
                
            }
            
        }
        
    }
    
    private ActionListener addAltWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AltWorkloadCtrl(parent.faculty());
        }
        };
    }
    
    private ActionListener editAltWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            AltWorkload workload = altWorkloadP.selectedWorkload();
            if (workload != null)
                new AltWorkloadCtrl(workload);
        }
        };
    }
    
    private class DeleteAltWorkload implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AltWorkload workload = altWorkloadP.selectedWorkload();
            if (workload != null && Message.verified("Delete workload?"))
                workload.delete();
                Message.confirm("Workload deleted.");
                new FacultyCtrl(parent.faculty());
        }
    }
    
    
    private ActionListener setTerm() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int year = facultyTermP.year();
            int term = facultyTermP.term();
            User.setYearTerm(year, term);
            facultyTermP.refresh();
        }
        };
    }
    
    private ActionListener updateStatus() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FacultyStatus status = facultyTermP.status();
            if (status != null) {
                Faculty faculty = parent.faculty();
                int year = User.year();
                int term = User.term();
                if (faculty.updateStatus(year, term, status))
                    System.out.println("Success");
                else
                    System.out.println("Fail");
            }
        }
        };
    }
}
