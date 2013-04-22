package controller;

import javax.swing.JComponent;

import view.MainFrame;

import model.AltWorkload;
import model.Faculty;
import model.Offering;
import model.Initializer;
import model.Subject;
import model.Workload;

public class Driver {
    private static MainFrame frame;
    
    public static void main(String[] args) {
        new Driver();
    }
    
    private Driver() {
        initialize();
        new Login();
        
    }
    
    protected void initialize() {
        frame = new MainFrame();
    }
    
    
    public static void setPanel(JComponent component) {
        frame.setPanel(component);
    }
    
   /* protected void login() {
        new Login(this);
    }
    
    public void mainMenu() {
        new MainMenu(this);
    }
    
    protected void addFaculty() {
        new FacultyCtrl(this);
    }

    protected void editFaculty(Faculty faculty) {
        new FacultyCtrl(faculty, this);
    }
    
    protected void addSubject() {
        new SubjectCtrl(this);
    }
    
    protected void subjectList() {
        new SubjectList(this);
    }
    
    protected void editSubject(Subject subject) {
        new SubjectCtrl(subject, this);
    }
    
    protected void editFacultyStatus() {
        new FacultyStatusCtrl(this);
    }

    protected void addOffering() {
        new OfferingCtrl(this);
    }
    
    protected void editOffering(Offering offering) {
        new OfferingCtrl(offering, this);
    }
    
    public void editOffering(Workload workload) {
        new OfferingCtrl(workload, this);
    }

    protected void editOffering(Workload workload, Faculty faculty) {
        new OfferingCtrl(workload, faculty, this);
    }
    
    protected void offeringManager() {
        new TermCtrl(0, this);
    }
    
    protected void facultyManager() {
        new TermCtrl(1, this);
    }
    
    protected void summary() {
        new TermCtrl(2, this);
    }
    
    
    protected void addAltWorkload(Faculty faculty) {
        new AltWorkloadCtrl(faculty, this);
    }
    
    protected void editAltWorkload(AltWorkload workload) {
        new AltWorkloadCtrl(workload, this);
    }

    */
}
