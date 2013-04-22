package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.FLException;
import model.Initializer;
import model.Subject;

import view.Message;
import view.SubjectPanel;
import view.MainFrame;

public class SubjectCtrl {
    private Subject subject;
    private SubjectPanel panel;
    
    // adding
    public SubjectCtrl() {
        subject = new Subject();
        panel = new SubjectPanel();
        panel.addAcceptListener(addSubject());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    // editing
    public SubjectCtrl(Subject subject) {
        this.subject = subject;
        panel = new SubjectPanel(subject);
        panel.addAcceptListener(editSubject());
        panel.addDeleteListener(deleteSubject());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }
    
    private void getDetailsFromView() {
        subject.setCode(panel.code());
        subject.setUnits(panel.units());
        subject.setCategory(panel.category());
    }
    
    private ActionListener addSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.fieldsComplete()) return;
            getDetailsFromView();
            try {
                subject.add();
                Message.confirm("Course added successfully.");
                new SubjectList();
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }
    
    private ActionListener editSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!panel.fieldsComplete()) return;
            getDetailsFromView();
            try {
                subject.update();
                Message.confirm("Course edited successfully.");
            }
            catch (FLException ex) {
                Message.deny(ex.getLocalizedMessage());
            }
        }
        };
    }
    
    private ActionListener deleteSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Message.verified("Delete subject?")) return;
            if (subject.delete()) {
                System.out.println("Success");
                Message.confirm("Course deleted.");
                new SubjectList();
            }
            else {
                System.out.println("Fail");             
            }
        }
        };
    }
    
    
    
    private ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new SubjectList();
        }
        };
    }
}
