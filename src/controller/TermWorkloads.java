package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.FLException;
import model.Faculty;
import model.Workload;
import view.Message;
import view.TermPanel;
import view.WorkloadListPanel;

public class TermWorkloads {
    private WorkloadListPanel panel;
    
    public TermWorkloads(TermPanel parentPanel) {
        panel = new WorkloadListPanel();
        panel.addAcceptListener(addOffering());
        panel.addCancelListener(editOffering());
        panel.addAssignListener(assignFaculty());
        panel.addMouseListener(doubleClick());
        
        parentPanel.addChild("Offerings", panel);
    }
    
    public ActionListener addOffering() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new OfferingCtrl();
        }
        };
    }
    
    public ActionListener editOffering() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Workload workload = panel.selectedWorkload();
            if (workload != null)
                new OfferingCtrl(workload);
        }
        };
    }
    
    public ActionListener assignFaculty() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Workload workload = panel.selectedWorkload();
            try {
                if (workload != null) {
                    Faculty faculty = Search.faculty(workload);
                    workload.replaceFaculty(faculty);
                    workload.update();
                    new TermCtrl(TermCtrl.WORKLOADS);
                }   
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }   
        }
        };
    }
    
    private MouseAdapter doubleClick() {
        return new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
           if (e.getClickCount() == 2) {
              Workload workload = panel.selectedWorkload();
              new OfferingCtrl(workload);
          }
        }
        };
    }

    
    
}
