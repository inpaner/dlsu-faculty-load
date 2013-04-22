package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Subject;
import view.SubjectListPanel;

public class SubjectList {
    private SubjectListPanel panel;
    
    public SubjectList() {
        panel = new SubjectListPanel(false);
        panel.addAddListener(addSubject());
        panel.addEditListener(editSubject());
        panel.addCancelListener(back());
        Driver.setPanel(panel);
    }

    private ActionListener addSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new SubjectCtrl();
        }
        };
    }
    
    private ActionListener editSubject() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Subject subject = panel.selectedSubject();
            if (subject != null)
                new SubjectCtrl(subject);
        }
        };
    }
    

    private ActionListener back() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new MainMenu();
        }
        };
    }
}
