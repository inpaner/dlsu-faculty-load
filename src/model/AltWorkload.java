package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class AltWorkload implements Comparable<AltWorkload>{
    private int pk;
    private int year;
    private int term;
    private Faculty faculty;
    private String description;
    private double units;
    private LoadType loadType;
    private static HashMap<Integer, AltWorkload> allWorkloads;
    
    static {
        allWorkloads = new HashMap<>();
    }
    
    protected static void addToMap(AltWorkload workload) {
        allWorkloads.put(workload.pk, workload);
    }
    
    protected static AltWorkload get(int pk) {
        return allWorkloads.get(pk);
    }
    
    public static ArrayList<AltWorkload> workloadList() {
        ArrayList<AltWorkload> list = new ArrayList<>();
        for (AltWorkload workload: allWorkloads.values()) {
            list.add(workload);
        }
        return list;
    }
    
    public AltWorkload() {
    }
    
    protected AltWorkload(int pk) {
        this.pk = pk;
        resetData();
        allWorkloads.put(pk, this);
        faculty.addWorkload(this);
    }
        
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public void setTerm(int term) {
        this.term = term;
    }
    
    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
    
    public void setUnits(double units) {
        this.units = units;
    }
    
    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }
    
    public void setDescription(String description) {
        this.description = description.toUpperCase();
    }
    
    protected int pk() {
        return pk;
    }
    
    public int year() {
        return year;
    }

    public int term() {
        return term;
    }

    public Faculty faculty() {
        return faculty;
    }
    
    public double units() {
        return units;
    }
    
    public LoadType loadType() { 
        return loadType; 
    }
    
    public String description() {
        return description;
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT year, term, facultyFK, " +
                "       loadTypeFK, units, description " +
                "FROM AltWorkload " +
                "WHERE altWorkloadPK = (?) "
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            year = rs.getInt("year");
            term = rs.getInt("term");
            units = rs.getInt("units");
            description = rs.getString("description");
            loadType = LoadType.get(rs.getInt("loadTypeFK"));
            faculty = Faculty.get(rs.getInt("facultyFK"));
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
    
    public boolean add() throws FLException {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            faculty.checkIfCanAssign(this);
            ps = conn.prepareStatement(
                "INSERT INTO AltWorkload " +
                "   (year, term, facultyFK, loadTypeFK, units, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            
            //TODO description
            ps.setInt(1, year);
            ps.setInt(2, term);
            ps.setInt(3, faculty.pk());
            ps.setInt(4, loadType.pk());
            ps.setDouble(5, units);
            ps.setString(6, description);
            ps.executeUpdate();
            
            ps = conn.prepareStatement(
                "SELECT altWorkloadPK " +
                "FROM AltWorkload " +
                "WHERE altWorkloadPK = LAST_INSERT_ID() "
            );
            rs = ps.executeQuery();            
            if (rs.next())
                pk = rs.getInt("AltWorkloadPK"); 
            addToMap(this);
            faculty.addWorkload(this);
            successful = true;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }            
        return successful;
    }

    public boolean update() throws FLException {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            faculty.checkIfCanAssign(this);

            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE AltWorkload " +
                "SET loadTypeFK = ?, units = ?, description = ? " +
                "WHERE altWorkloadPK = ? "
            );
            //TODO description
            preparedStatement.setInt(1, loadType.pk());
            preparedStatement.setDouble(2, units);
            preparedStatement.setString(3, description);
            preparedStatement.setInt(4, pk);
            preparedStatement.executeUpdate();
            
            successful = true;
        }
        catch (FLException e) {
            resetData();
            throw e;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }             
        return successful;
    }


    public void delete() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        
        // Remove self from other entities
        allWorkloads.remove(pk);
        faculty.removeWorkload(this);
        
        try {   
            ps = conn.prepareStatement(
                "DELETE FROM AltWorkload " +
                "WHERE altWorkloadPK = ? "
            );
            ps.setInt(1, pk);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(ps);
            util.close(conn);
        }             
    }
    
    @Override
    public int compareTo(AltWorkload other) {
        int comparison = loadType.compareTo(other.loadType);
        if (comparison == 0) {
            // Direct subtraction would result to 0
            // when casted to int
            if (units > other.units)
                comparison = 1;
            else if (units < other.units)
                comparison = -1;
            else
                comparison = 0;
        }
        return comparison;
    }

    
}
