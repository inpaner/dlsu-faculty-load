package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.JOptionPane;

import view.Message;
import view.TermPanel;
import view.TermSummaryPanel;

import model.AltWorkload;
import model.Faculty;
import model.FacultyStatus;
import model.Offering;
import model.User;
import model.Workload;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class TermSummary {
    private TermSummaryPanel panel;
    private int year;
    private int term;
    
    
    public TermSummary(TermCtrl parent, TermPanel parentPanel) {
        year = User.year();
        term = User.term();
        panel = new TermSummaryPanel();
        panel.addExportListener(export());
        
        parentPanel.addChild("Summary", panel);
    }
    
    private ActionListener export() {
        return new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = "Enter filename: ";
            String defaultName = year + "-" + term;
            String filename = Message.input(message, defaultName);
            filename = filename + ".xls";
            if (!filename.isEmpty())
                exportToExcel(filename);
        }
        };
    }
    
    private void exportToExcel(String filename) {
        try {
            Vector<Faculty> fullTimeFaculties = new Vector<>();
            Vector<Faculty> partTimeFaculties = new Vector<>();
            
            // repeat code from TermSummaryPanel 
            for (Faculty faculty : Faculty.facultyList()) {
                FacultyStatus fullTime = FacultyStatus.fullTime();
                FacultyStatus partTime = FacultyStatus.partTime();
                if (faculty.status(year, term).equals(fullTime))
                    fullTimeFaculties.add(faculty);
                else if (faculty.status(year, term).equals(partTime))
                    partTimeFaculties.add(faculty);
            }
            
            FileOutputStream out = new FileOutputStream(filename);
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row;
            Cell cell;
            
            // Header
            for (int i = 0; i < 4; i++) {
                row = sheet.createRow(i);
                cell = row.createCell(0);
                String header = "";
                switch (i) {
                case 0  : header = "Software Technology Department"; break;
                case 1  : header = "College of Computer Studies"; break;
                case 2  : header = "Term " + User.term() + " AY " + User.year(); break;
                case 3  : header = "Faculty Load"; break;
                default : break;
                }
                cell.setCellValue(header);
            }
            
            // Full time
            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("Full Time");
            
            int rowNum = 6;
            for (Faculty faculty : fullTimeFaculties) {
                rowNum = appendDetails(faculty, rowNum, sheet);
            }
            
            rowNum ++;
            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue("Part Time");
            rowNum++;
            
            for (Faculty faculty : partTimeFaculties) {
                rowNum = appendDetails(faculty, rowNum, sheet);
            }
            
            workbook.write(out);
            out.close();
            Message.confirm("Export successful.");
        }
        catch (FileNotFoundException e) {
            Message.deny(e.getLocalizedMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private int appendDetails(Faculty faculty, int rowNum, Sheet s) {
        Row row;
        Cell cell;
        row = s.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue(faculty.toString());
        cell = row.createCell(6);
        cell.setCellValue(String.valueOf(faculty.units(year, term)));
        rowNum++;
        
        Offering prevOffering = null;
        for (Workload workload : faculty.workloadList(year, term)) {
            row = s.createRow(rowNum);
            for (int j = 0; j < 7; j++) {
                cell = row.createCell(j);
                String text = "";
                switch (j) {
                case 0  :   text = workload.code(); break;
                case 1  :   text = workload.section(); break;
                case 2  :   text = workload.dayString(); break;
                case 3  :   text = workload.offering().startTimeStr(); break;
                case 4  :   text = workload.offering().endTimeStr(); break;
                case 5  :   text = workload.room(); break;
                case 6  :   text = String.valueOf(workload.units()); break;
                default : break;
                }
                
                if (workload.offering().equals(prevOffering)) {
                    switch (j) {
                    case 0  :   
                    case 1  :   
                    case 6  :   text = ""; break;
                    default : break;
                    }
                }
                
                cell.setCellValue(text);
            }
            prevOffering = workload.offering();
            rowNum++;
        }
        for (AltWorkload altWorkload : faculty.altWorkloadList(year, term)) {
            row = s.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue(altWorkload.description());
            cell = row.createCell(6);
            cell.setCellValue(String.valueOf(altWorkload.units()));
            rowNum++;
        }
        
        rowNum++;
        return rowNum;
    }
    
}
