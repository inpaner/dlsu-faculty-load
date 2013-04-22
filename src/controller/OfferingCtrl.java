package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.FLException;
import model.Faculty;
import model.Workload;
import model.Offering;
import model.Subject;
import model.User;
import view.Message;
import view.WorkloadPanel;
import view.OfferingPanel;
import view.SubjectListPanel;

public class OfferingCtrl {
    private Offering offering;
    private Workload selectedWorkload;

    private OfferingPanel panel;
    private WorkloadCtrl initialWorkload;
    
    // Editing
    public OfferingCtrl(Offering offering) {
        this.offering = offering;
        panel = new OfferingPanel(offering);
        panel.addDeleteListener(deleteOffering());
        panel.addAcceptListener(editOffering());
        panel.addCancelListener(backToMainMenu());
        panel.addNewWorkloadListener(addWorkload());
        Driver.setPanel(panel);
        refreshWorkloads();
    }
    
    // Editing from faculty manager
    public OfferingCtrl(Workload workload, Faculty faculty) {
        offering = workload.offering();
        selectedWorkload = workload;
        panel = new OfferingPanel(offering);
        panel.addDeleteListener(deleteOffering());
        panel.addAcceptListener(editOffering());
        panel.addCancelListener(backToFaculty(faculty));
        panel.addNewWorkloadListener(addWorkload());
        Driver.setPanel(panel);
        refreshWorkloads();
    }
    
    // Editing
    public OfferingCtrl(Workload workload) {
        this(workload.offering());
        this.offering = workload.offering();
        selectedWorkload = workload;
        refreshWorkloads();
    }
    
    // Adding
    public OfferingCtrl() {
        offering = new Offering();
        offering.setYear(User.year());
        offering.setTerm(User.term());
        
        panel = new OfferingPanel(User.year(), User.term());
        panel.addSelectSubjectListener(selectSubject());
        panel.addNewWorkloadListener(addOffering());
        panel.addCancelListener(backToMainMenu());
        
        initialWorkload = new WorkloadCtrl(this, panel, true);
        
        Driver.setPanel(panel);
    }
    
    private ActionListener selectSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Subject subject = Search.subject();;
            panel.setSubject(subject);
        }
        };
    }
    
    private void getDetailsFromView() {
        offering.setSubject(panel.subject());
        offering.setSection(panel.section());
        offering.setStartTime(panel.startTime());
        offering.setEndTime(panel.endTime());
        offering.setStatus(panel.status());
    }


    protected Offering offering() {
        return offering;
    }

    protected void refreshWorkloads() {
        panel.clearTabs();
        WorkloadPanel selectedPanel = null;
        for (Workload workload: offering.workloadList()) {
            WorkloadCtrl manager = new WorkloadCtrl(workload, this, panel);
            if (workload.equals(selectedWorkload))
                selectedPanel = manager.panel();
        }
        panel.setSelectedWorkload(selectedPanel); 
    }

    private ActionListener addOffering() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            if (!panel.valuesValid() || !initialWorkload.valuesValid()) 
                return;
            try {
                getDetailsFromView();
                offering.add();
                initialWorkload.addInitial();
                Message.confirm(offering.toString() + " added successfully.");
                new TermCtrl(TermCtrl.WORKLOADS);
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
            if (!panel.valuesValid()) 
                return;
            getDetailsFromView();
            try {
                offering.update();
                Message.confirm(offering.toString() + " edited successfully.");
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }            
        };
    }
    
    private ActionListener deleteOffering() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.verifyDelete())
                return;
            offering.delete();
            Message.confirm(offering.toString() + " deleted successfully.");
            new TermCtrl(TermCtrl.WORKLOADS);    
        }
        };            
    }
    
    private ActionListener addWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new WorkloadCtrl(OfferingCtrl.this, panel, false);
        }            
        };
    }
    
    private ActionListener backToMainMenu() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new TermCtrl(TermCtrl.WORKLOADS);
        }            
        };
    }
    
    private ActionListener backToFaculty(final Faculty faculty) {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new FacultyCtrl(faculty);
        }            
        };
    }
}
