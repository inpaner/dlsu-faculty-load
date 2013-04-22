package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Initializer {
    private DBUtil util;
    
    public Initializer() {
        util = new DBUtil();
        initCategories();
        initSubjects();
        initFacultyStatuses();
        initFaculties();
        initFacultyTerms();
        initPreferredCategories();
        initOfferingStatuses();
        initOfferings();
        initWorkloads();
        initWorkloadDays();
        initLoadTypes();
        initAltWorkloads();
    }

    

    // TODO change ALL initialize to init
    private void initCategories() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT categoryPK " +
                "FROM Category "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new Category(rs.getInt("categoryPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }

    
    private void initSubjects() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT subjectPK " +
                "FROM Subject "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new Subject(rs.getInt("subjectPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }

    private void initFaculties() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT facultyPK " +
                "FROM Faculty "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new Faculty(rs.getInt("facultyPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }

    private void initFacultyStatuses() {
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT facultyStatusPK " +
                "FROM FacultyStatus "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new FacultyStatus(rs.getInt("facultyStatusPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }
    }

    private void initPreferredCategories() {
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "SELECT facultyFK, categoryFK " +
                "FROM CategoryPreference "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                Faculty faculty = Faculty.get(rs.getInt("facultyFK"));
                Category category = Category.get(rs.getInt("categoryFK"));
                faculty.addCategory(category);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }
    
    private void initFacultyTerms() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT facultyStatusFK, facultyFK, year, term " +
                "FROM FacultyTerm "
            );
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Faculty faculty = Faculty.get(rs.getInt("facultyFK"));
                FacultyStatus status = FacultyStatus.get(rs.getInt("facultyStatusFK"));
                int year = rs.getInt("year");
                int term = rs.getInt("term");
                faculty.putStatus(year, term, status);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }
    
    private void initOfferingStatuses() {
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "SELECT offeringStatusPK " +
                "FROM OfferingStatus "
            );
            
            rs = ps.executeQuery();
            while (rs.next()) {
                new OfferingStatus(rs.getInt("offeringStatusPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }
    
    private void initOfferings() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT offeringPK " +
                "FROM Offering " 
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new Offering(rs.getInt("offeringPK"));
            }
        }
        
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }        
    }
    
    private void initWorkloadDays() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT workloadFK, dayFK " +
                "FROM WorkloadDay " 
            );
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Workload workload = Workload.get(rs.getInt("workloadFK"));
                Day day = Day.get(rs.getInt("dayFK"));
                workload.addDay(day);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }
    
    private void initWorkloads() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT workloadPK " +
                "FROM Workload " 
            );
            
            rs = ps.executeQuery();
            while (rs.next()) {
                new Workload(rs.getInt("workloadPK"));
            }
        }
        
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }        
    }

    private void initLoadTypes() {
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT loadTypePK " +
                "FROM LoadType "
            );
            rs = ps.executeQuery();
            while (rs.next()) {
                new LoadType(rs.getInt("loadTypePK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }
    }

    private void initAltWorkloads() {
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT altWorkloadPK " +
                "FROM AltWorkload " 
            );
            
            rs = ps.executeQuery();
            while (rs.next()) {
                new AltWorkload(rs.getInt("altWorkloadPK"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }        
    }
}
