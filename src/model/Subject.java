package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Subject implements Comparable<Subject> {
    private int pk;
    private String code;
    private double units;
    private Category category;
    
    private ArrayList<Offering> offeringList;
    
    private static HashMap<Integer, Subject> allSubjects;
    
    static {
        allSubjects = new HashMap<>();
    }
    
    protected static void addToMap(Subject subject) {
        allSubjects.put(subject.pk, subject);
    }
    
    protected static Subject get(int pk) {
        return allSubjects.get(pk);
    }
    
    public static ArrayList<Subject> subjectList() {
        ArrayList<Subject> list = new ArrayList<>();
        for (Subject subject: allSubjects.values()) {
            list.add(subject);
        }
        Collections.sort(list);
        return list;
    }
    
    public Subject() {
        offeringList = new ArrayList<>();
    }
    
    public Subject(int pk) {
        this();
        this.pk = pk;
        resetData();
        allSubjects.put(pk, this);
    }
    
    protected int pk() {
        return pk;
    }

    public String code() {
        return code;
    }

    public double units() {
        return units;
    }

    public Category category() {
        return category;
    }

    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    public void setCode(String code) {
        this.code = code.toUpperCase();
    }
    
    public void setUnits(double units) {
        this.units = units;
    }
    
    public void setCategory(Category category) {
        this.category = category;
        
    }
    
    protected void addOffering(Offering offering) {
        // TODO check for doubles or replace with TreeSet
        offeringList.add(offering);
    }
    
    protected void removeOffering(Offering offering) {
        offeringList.remove(offering);
    }

    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "SELECT code, units, categoryFK " +
                "FROM Subject " +
                "WHERE subjectPK = (?)"
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            code = rs.getString("code");
            units = rs.getDouble("units");
            category = Category.get(rs.getInt("categoryFK"));
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(ps);
            util.close(rs);
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
            checkValues();
            ps = conn.prepareStatement(
                "INSERT INTO Subject (code, units, categoryFK) " +
                "VALUES (?, ?, ?)"
            );
            ps.setString(1, code);
            ps.setDouble(2, units);
            ps.setInt(3, category.pk());
            ps.executeUpdate();
            
            ps = conn.prepareStatement(
                "SELECT subjectPK " +
                "FROM Subject " +
                "WHERE subjectPK = LAST_INSERT_ID() "
            );
            
            rs = ps.executeQuery();            
            if (rs.next())
                pk = rs.getInt("subjectPK"); 
            
            addToMap(this);
            successful = true;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            resetData();
            throw new FLException(code + " already exists.");
        }
        catch (SQLException e) {
            resetData();
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }            
        
        return successful;
    }
    
    private void checkValues() throws FLException {
        if (code.length() != 7)
            throw new FLException("Course code must be 7 characters in length.");
        if (units < 0)
            throw new FLException("Units must be postive.");
    }
    
    public boolean update() throws FLException {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps;
        try {
            checkValues();
            ps = conn.prepareStatement(
                "UPDATE Subject " +
                "SET code = ?, units = ?, categoryFK = ? " +
                "WHERE subjectPK = ? "
            );
            ps.setString(1, code);
            ps.setDouble(2, units);
            ps.setInt(3, category.pk());
            ps.setInt(4, pk);
            ps.executeUpdate();
            
            successful = true;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            resetData();
            throw new FLException(code + " already exists.");
        }
        catch (SQLException e) {
            resetData();
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }             
        return successful;
    }
    
    public boolean delete() {
        boolean successful = false;

        allSubjects.remove(pk);
        ArrayList<Offering> copy = new ArrayList<>(offeringList);
        for (Offering offering : copy)
            offering.delete();
        
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "DELETE FROM Subject " +
                "WHERE subjectPK = ? "
            );
            ps.setInt(1, pk);
            ps.executeUpdate();
            
            successful = true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(ps);
            util.close(conn);
        }             
        return successful;
    }

    
    
    public String toString() {
        return code;
    }
    
    @Override
    public int compareTo(Subject other) {
        int comparison = code.compareTo(other.code);
        if (comparison == 0) {
            Double diff = units - other.units;
            comparison = diff.intValue();
        }
        return comparison;
    }
    
    

}
