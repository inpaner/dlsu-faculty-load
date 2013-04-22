package view;

import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import model.AltWorkload;
import model.Faculty;
import model.FacultyStatus;
import model.Offering;
import model.User;
import model.Workload;

public class TermSummaryPanel extends MigPanel implements TermSettable<MigPanel> {
    
    private int year;
    private int term;
    private Vector<Faculty> fullTimeFaculties;
    private Vector<Faculty> partTimeFaculties;
    private JScrollPane scrollPane;
    private JPanel summary;
    private JButton export;
    
    public TermSummaryPanel() {
        year = User.year();
        term = User.term();
        
        refresh();
        initComponents();
        addComponents();
        
    }
    
    private void initComponents() {
        summary = new JPanel();
        MigLayout layout = new MigLayout("wrap", "[]15[]15[]15[]15[]15[]15[]", "");
        summary.setLayout(layout);
        scrollPane = new JScrollPane(summary);
        scrollPane.setSize(300,500);
        summary.setSize(300, 500);
        
        export = new JButton("Export");
    }
    
    private void addComponents() {
        summary.add(createLabel("Full Time"), "wrap");
        for (Faculty faculty : fullTimeFaculties) {
            appendDetails(faculty);
        }
        
        summary.add(createLabel(""), "wrap 20");
        summary.add(createLabel("Part Time"), "wrap");
        for (Faculty faculty : partTimeFaculties) {
            appendDetails(faculty);
        }
        
        add(scrollPane, "wrap");
        add(export);
    }
    
    private void appendDetails(Faculty faculty) {
        summary.add(createLabel(faculty.toString()), "span 6, split 1");
        String totalUnits = String.valueOf( faculty.units(year, term) );
        summary.add(createLabel(totalUnits), "right, wrap");
        
        Offering prevOffering = null;
        for (Workload workload : faculty.workloadList(year, term)) {
            String code = workload.code();
            String section = workload.section();
            String units = String.valueOf(workload.units());
            
            if (workload.offering().equals(prevOffering)) {
                code = "";
                section = "";
                units = "";
            }
            
            summary.add(createLabel(code));
            summary.add(createLabel(section));
            summary.add(createLabel(workload.dayString()));
            String start = workload.offering().startTimeStr();
            summary.add(createLabel(start));
            String end = workload.offering().endTimeStr();
            summary.add(createLabel(end));
            summary.add(createLabel(workload.room()));
            summary.add(createLabel(units), "right");
            prevOffering = workload.offering();
        }
        for (AltWorkload altWorkload : faculty.altWorkloadList(year, term)) {
            summary.add(createLabel(altWorkload.description()), "span 6, split 1");
            String units = String.valueOf(altWorkload.units());
            summary.add(createLabel(units), "right");
        }
        summary.add(createLabel(""), "wrap 20");
    }
    
    @Override
    public void setTerm(int year, int term) {
        this.year = year;
        this.term = term;
    }

    @Override
    public void refresh() {
        fullTimeFaculties = new Vector<>();
        partTimeFaculties = new Vector<>();
        
        for (Faculty faculty : Faculty.facultyList()) {
            FacultyStatus fullTime = FacultyStatus.fullTime();
            FacultyStatus partTime = FacultyStatus.partTime();
            if (faculty.status(year, term).equals(fullTime))
                fullTimeFaculties.add(faculty);
            else if (faculty.status(year, term).equals(partTime))
                partTimeFaculties.add(faculty);
        }
    }
    
    public void addExportListener(ActionListener listener) {
        export.addActionListener(listener);
    }

    public void filterData() {
    }
}
