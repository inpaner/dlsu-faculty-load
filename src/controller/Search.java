package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import view.FacultyListPanel;
import view.SearchDialog;
import view.SubjectListPanel;
import view.WorkloadListPanel;

import model.Faculty;
import model.Subject;
import model.Workload;

public class Search {
    static Faculty faculty;
    static Subject subject;
    static Workload workload;
    
    public static Faculty faculty(Workload workloadFilter) {
        // TODO JDialog should be implemented in view
        final SearchDialog dialog = new SearchDialog();
        final FacultyListPanel panel = new FacultyListPanel(workloadFilter);
        
        ActionListener acceptListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                faculty = panel.selectedFaculty();
                dialog.dispose();
            }
        };
        ActionListener cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                faculty = null;
                dialog.dispose();
            }
        };
        panel.addAcceptListener(acceptListener);
        panel.addCancelListener(cancelListener);
        dialog.setPanel(panel);
        return faculty;
    }
    
    public static Workload workload(Faculty facultyFilter) {
        // TODO JDialog should be implemented in view
        final SearchDialog dialog = new SearchDialog();
        final WorkloadListPanel panel = new WorkloadListPanel(facultyFilter);
        
        ActionListener acceptListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                workload = panel.selectedWorkload();
                dialog.dispose();
            }
        };
        ActionListener cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                workload = null;
                dialog.dispose();
            }
        };
        panel.addAcceptListener(acceptListener);
        panel.addCancelListener(cancelListener);
        dialog.setPanel(panel);
        
        return workload;
    }
    
    public static Subject subject() {
        final SearchDialog dialog = new SearchDialog();
        final SubjectListPanel panel = new SubjectListPanel(true);
        ActionListener selectSubject = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                subject = panel.selectedSubject();
                dialog.dispose();
            }
        };
        ActionListener selectNone = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                subject = null;
                dialog.dispose();
            }
        };
        panel.addAddListener(selectSubject);
        panel.addEditListener(selectNone);
        dialog.setPanel(panel);
        return subject;
    }
    
}
