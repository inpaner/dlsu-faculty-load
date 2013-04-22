package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import model.FLException;
import model.Faculty;
import model.FacultyStatus;
import model.User;
import model.Workload;
import view.FacultyListPanel;
import view.FacultyTermPanel;
import view.Message;
import view.TermPanel;

public class TermFaculties {
    private FacultyListPanel panel;
    
    public TermFaculties(TermPanel parentPanel) {
        panel = new FacultyListPanel();
        panel.addAcceptListener(new AddFaculty());
        panel.addCancelListener(new EditFaculty());
        panel.addSetToFullTimeListener(new SetToCurrent());
        panel.addAssignListener(new AssignWorkload());
        panel.addMouseListener(doubleClick());
        parentPanel.addChild("Faculty", panel);
    }
    
    private class AddFaculty implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new FacultyCtrl();
        }
    }
    
    private class EditFaculty implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Faculty faculty = panel.selectedFaculty();
            if (faculty != null)
                new FacultyCtrl(faculty);
        }
    }
    
    private class SetToCurrent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (Faculty faculty : panel.filteredFaculties()) {
                faculty.updateStatus(User.year(), User.term(), faculty.currentStatus());
            }
            Message.confirm("Statuses set to current.");
            new TermCtrl(TermCtrl.FACULTIES);
        }
    }
    
    public class AssignWorkload implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Faculty faculty = panel.selectedFaculty();
            try {
                if (faculty != null) {
                    Workload workload = Search.workload(faculty);
                    workload.replaceFaculty(faculty);
                    workload.update();
                    Message.confirm("Assignment successful.");
                    new TermCtrl(TermCtrl.FACULTIES);
                }   
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }   
        }
    }
    
    private MouseAdapter doubleClick() {
        return new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
           if (e.getClickCount() == 2) {
              Faculty faculty = panel.selectedFaculty();
              new FacultyCtrl(faculty);
          }
        }
        };
    }
    
}
