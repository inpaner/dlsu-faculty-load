package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Offering implements Comparable<Offering> {
    private int pk;
    private int year;
    private int term;
    private Time startTime;
    private Time endTime;
    private Subject subject;
    private String section;
    private OfferingStatus status;
    private TreeSet<Workload> workloadList;
    
    private static HashMap<Integer, Offering> allOfferings;
    
    static {
        allOfferings = new HashMap<>();
    }
    
    protected static void addToMap(Offering offering) {
        allOfferings.put(offering.pk, offering);
    }
    
    protected static Offering get(int pk) {
        return allOfferings.get(pk);
    }
    
    public static ArrayList<Offering> offeringList() {
        ArrayList<Offering> list = new ArrayList<>();
        for (Offering offering : allOfferings.values()) {
            list.add(offering);
        }
        Collections.sort(list);
        return list;
    }
    
    public static ArrayList<Offering> offeringList(int year, int term) {
        ArrayList<Offering> list = new ArrayList<>();
        for (Offering offering : allOfferings.values()) {
            if (offering.year == year && offering.term == term)
                list.add(offering);
        }
        Collections.sort(list);
        return list;
    }
     
    public Offering() {
        workloadList = new TreeSet<>();
    }
    
    protected Offering(int pk) {
        this();
        this.pk = pk;
        resetData();
        allOfferings.put(pk, this);
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
    
    public Subject subject() {
        return subject;
    }
    
    public String section() {
        return section;
    }
    
    public double units() {
        return subject.units();
    }
    
    public OfferingStatus status() {
        return status;
    }
    
    private String shortenTime(Time time) {
        String str = ""; 
        if (time != null) {
            str = time.toString();
            str = str.substring(0, str.length() - 3);
        }
        return str;
    }
    
    protected Time startTime() {
        return startTime;
    }
    
    public String startTimeStr() {
        return shortenTime(startTime);
    }
    
    protected Time endTime() {
        return endTime;
    }
    
    public String endTimeStr() {
        return shortenTime(endTime);
    }
    
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    public void setSubject(Subject subject) {
        this.subject= subject;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public void setTerm(int term) {
        this.term = term;
    }
    
    public void setSection(String section) {
        this.section = section.toUpperCase();
    }
    
    public void setStartTime(String start) {
        start = start + ":00";
        this.startTime = Time.valueOf(start);
    }
    
    public void setStartTime(Time start) {
        this.startTime = start;
    }
    
    public void setEndTime(String end) {
        end = end + ":00";
        this.endTime = Time.valueOf(end);
    }
    
    public void setEndTime(Time end) {
        this.endTime = end;
    }
    
    public void setStatus(OfferingStatus status) {
        if (status != null)
            this.status = status;
    }
    
    public void addWorkload(Workload workload) {
        workloadList.add(workload);
    }
    
    protected void removeWorkload(Workload workload) {
        workloadList.remove(workload);
    }
    
    public ArrayList<Workload> workloadList() {
        ArrayList<Workload> list = new ArrayList<>(workloadList);
        Collections.sort(list);
        return list;
    }
    
    public boolean isAssigned() {
        boolean assigned = false;
        for (Workload workload: workloadList) {
            if (workload.hasFaculty()) {
                assigned = true;
                break;
            }
        }
        return assigned;
    }
    
    @Override
    public int compareTo(Offering other) {
        int comparison = year - other.year;
        if (comparison == 0) {
            comparison = term - other.term;
        }
        if (comparison == 0) {
            String otherSubjectCode = other.subject.code();
            comparison = subject.code().compareTo(otherSubjectCode);   
        }
        if (comparison == 0) {
            comparison = section.compareTo(other.section);
        }
        return comparison;
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT year, term, subjectFK, section, " +
                "       startTime, endTime, offeringStatusFK " +
                "FROM Offering " + 
                "WHERE offeringPK = (?) "
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            year = rs.getInt("year");
            term = rs.getInt("term");
            subject = Subject.get(rs.getInt("subjectFK"));
            section = rs.getString("section");
            startTime = rs.getTime("startTime");
            endTime = rs.getTime("endTime");
            status = OfferingStatus.get(rs.getInt("OfferingStatusFK"));
            subject.addOffering(this);
        }
        
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }     
    }
    
    public void add() throws FLException {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            checkValues();
            ps = conn.prepareStatement(
                "INSERT INTO Offering (year, term, subjectFK, section, offeringStatusFK) " +
                "VALUES (?, ?, ?, ?, ?)"
            );
            ps.setInt(1, year);
            ps.setInt(2, term);
            ps.setInt(3, subject.pk());
            ps.setString(4, section);
            ps.setInt(5, status.pk());
            ps.executeUpdate();
            
            // Get PK
            ps = conn.prepareStatement(
                "SELECT offeringPK " +
                "FROM Offering " +
                "WHERE offeringPK = LAST_INSERT_ID() "
            );
            rs = ps.executeQuery();            
            if (rs.next())
                pk = rs.getInt("offeringPK"); 
            
            subject.addOffering(this);
            addToMap(this);
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String msg = code() + " " + section + " already exists.";
            resetData();
            throw new FLException(msg);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(ps);
            util.close(conn);
        }            
    }
    
    private void checkValues() throws FLException {
        if (startTime.after(endTime))
            throw new FLException("Start time is before end time.");
    }
    
    public void update() throws FLException {
        DBUtil util = new DBUtil();
        PreparedStatement ps = null;
        Connection conn = util.getConnection();
        try {
            checkValues();
            ps = conn.prepareStatement(
                "UPDATE Offering " +
                "SET section = ?, startTime = ?, " +
                "    endTime = ?, offeringStatusFK = ? " +
                "WHERE offeringPK = ? "
            );
            ps.setString(1, section);
            ps.setTime(2, startTime);
            ps.setTime(3, endTime);
            ps.setInt(4, status.pk());
            ps.setInt(5, pk);
            ps.executeUpdate();
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String msg = code() + " " + section + " already exists.";
            resetData();
            throw new FLException(msg);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(ps);
            util.close(conn);
        }             
    }

    public void delete() {
        allOfferings.remove(pk);
        ArrayList<Workload> copy = new ArrayList<>(workloadList);
        for (Workload workload : copy)
            workload.delete();
        
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "DELETE FROM Offering " +
                "WHERE offeringPK = ? "
            );
            ps.setInt(1, pk);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            util.rollback(conn);
            e.printStackTrace();
        }
        finally {
            util.close(ps);
            util.close(conn);
        }             
    }
    
    public int workloadCount() {
        return workloadList.size();
    }

    public boolean isSpecial() {
        return status.equals(OfferingStatus.specialClass());
    }

    public String code() {
        return subject.code();
    }
    
    public String toString() {
        return code() + " " + section;
    }
}
   