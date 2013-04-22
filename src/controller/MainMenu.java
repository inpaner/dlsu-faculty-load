package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view.MainMenuPanel;

public class MainMenu {
    protected MainMenu() {
        MainMenuPanel panel = new MainMenuPanel();
        panel.addFacultyManagerListener(facultyManager());
        panel.addOfferingManagerListener(offeringManager());
        panel.addSummaryListener(summary());
        
        panel.addSubjectManagerListener(new SubjectManager());
        
        panel.addEditSettingsListener(editSettings());
        panel.addCategoryManagerListener(new CategoryManager());
        panel.addLogoutListener(new Logout());
        Driver.setPanel(panel);
    }
    
    private ActionListener facultyManager() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new TermCtrl(TermCtrl.FACULTIES);
        }
        };
    }
    
    private ActionListener offeringManager() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new TermCtrl(TermCtrl.WORKLOADS);
        }
        };
    }    
    
    private ActionListener summary() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new TermCtrl(TermCtrl.SUMMARY);
        }
        };
    }    
    
    
    private ActionListener editSettings() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new FacultyStatusCtrl();
        }
        };
    }  
    
    private class SubjectManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new SubjectList();  
        };
    }    
    
    private class CategoryManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new CategoryList();
        };
    }
    
    private class Logout implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Login();
        };
    }
}
