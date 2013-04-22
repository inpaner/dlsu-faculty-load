package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import view.Message;
import view.WorkloadPanel;
import view.OfferingPanel;
import model.FLException;
import model.Faculty;
import model.Workload;

public class WorkloadCtrl {
    private Workload workload;
    private OfferingCtrl parent;
    private WorkloadPanel panel;
    
    // Adding
    public WorkloadCtrl(OfferingCtrl parent, OfferingPanel parentPanel, boolean isInitial) {
        this.workload = new Workload(parent.offering());
        this.parent = parent;
        panel = new WorkloadPanel(isInitial);
        panel.addAcceptListener(addWorkload());
        panel.addSetFacultyListener(new SelectFaculty());
        parentPanel.addNewTab("Adding: ", panel);
    }

    // Editing
    public WorkloadCtrl(Workload workload, OfferingCtrl parent, OfferingPanel parentPanel) {
        this.workload = workload;
        this.parent = parent;
        panel = new WorkloadPanel(workload);
        panel.addAcceptListener(editWorkload());
        panel.addCancelListener(deleteWorkload());
        panel.addSetFacultyListener(new SelectFaculty());
        parentPanel.addNewTab(workload.room(), panel);
    }
    
    protected void addInitial() {
        getDetailsFromView();
        try {
            workload.add();
        }
        catch (FLException ex){
            Message.deny(ex.getLocalizedMessage());
        }
        
    }
    
    private ActionListener addWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (panel.valuesValid()) {
                getDetailsFromView();
                try {
                    workload.add();
                    Message.confirm("Workload added.");
                    parent.refreshWorkloads();    
                }
                catch (FLException ex) {
                    Message.deny(ex.getLocalizedMessage());
                }
            }
            
        }
        };
    }
        
    public WorkloadPanel panel() {
        return panel;
    }
    
    public boolean valuesValid() {
        return panel.valuesValid();
    }
    
    private ActionListener editWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            getDetailsFromView();
            try {
                workload.update();
                Message.confirm("Workload edited.");
                parent.refreshWorkloads();
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
            
        }
        };
    }
    
    private void getDetailsFromView() {
        workload.setRoom(panel.room());
        workload.setDayList(panel.dayList());
        workload.replaceFaculty(panel.faculty());
    }

    private ActionListener deleteWorkload() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if (!Message.verified("Delete workload?")) 
                return;
            
            workload.delete();
            parent.refreshWorkloads();
            Message.confirm("Workload deleted.");
        }
        };
    }
    
    private class SelectFaculty implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Faculty faculty = Search.faculty(workload);
            panel.setFaculty(faculty);
        }
    }

}
