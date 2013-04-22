
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class Workload implements Comparable<Workload>{
    private int pk;
    private Faculty faculty;
    private Offering offering;
    private String room;
    private ArrayList<Day> dayList;
    
    private static HashMap<Integer, Workload> allWorkloads;
    
    static {
        allWorkloads = new HashMap<>();
    }

    protected static Workload get(int pk) {
        return allWorkloads.get(pk);
    }

    public static ArrayList<Workload> workloadList() {
        return new ArrayList<>(allWorkloads.values());
    }
    
    public static Vector<Workload> workloadList(int year, int term) {
        
        Vector<Workload> list = new Vector<>();
        for (Workload workload : allWorkloads.values()) {
            if (workload.offering.year() == year && 
                    workload.offering.term() == term)
                list.add(workload);
        }
        Collections.sort(list);
        return list;
    }
    
    
    private Workload() {
        dayList = new ArrayList<>();
        room = "";
    }
    
    protected Workload(int pk) {
        this();
        this.pk = pk;
        resetData();
        allWorkloads.put(pk, this);
        if (faculty != null)
            faculty.addWorkload(this);
    }

    public Workload(Offering offering) {
        this();
        this.offering = offering;
    }

    public double units() {
        return offering.units();
    }
    
    public String toString() {
        return offering.toString();
    }
    
    public String room() {
        return room;
    }
    
    protected int pk() {
        return pk;
    }
    
    public Faculty faculty() {
        return faculty;
    }
    
    public Offering offering() {
        return offering;
    }
    
    public Subject subject() {
        return offering.subject();
    }

    public boolean hasFaculty() {
        return faculty != null;
    }
    
    public int year() {
        return offering.year();
    }
    
    public int term() {
        return offering.term();
    }
    
    public void replaceFaculty(Faculty replacement) {
        if (faculty != null)
            faculty.removeWorkload(this);
        faculty = replacement;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    
    protected void addDay(Day day) {
        dayList.add(day);
    }
    
    public ArrayList<Day> dayList() {
        return dayList;
    }
    
    public String dayString() {
        StringBuilder dayString = new StringBuilder();
        for (Day day : dayList) {
            dayString.append(day.toString());
        }
        return dayString.toString();
    }
    
    public void setDayList(ArrayList<Day> list) {
        dayList = list;
    }
    
    public boolean overlapsWith(Workload other) {
        boolean overlaps = true;
        if (offering.year() != other.offering.year())
            overlaps = false;
        else if (offering.term() != other.offering.term())
            overlaps = false;
        else if (offering.startTime().after(other.offering.endTime()))
            overlaps = false;
        else if (offering.endTime().before(other.offering.startTime()))
            overlaps = false;
        else if (!daysOverlap(other))
            overlaps = false;
        return overlaps; 
    }
    
    private boolean daysOverlap(Workload other) {
        boolean overlaps = false;
        top:
        for (Day day : dayList) {
            for (Day otherDay : other.dayList) {
                if (day.equals(otherDay)) {
                    overlaps = true;
                    break top;
                }
            }
        }
        return overlaps;
    }

    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT facultyFK, offeringFK, room " +
                "FROM Workload " +
                "WHERE workloadPK = (?)"
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            faculty = Faculty.get(rs.getInt("facultyFK"));
            offering = Offering.get(rs.getInt("offeringFK"));
            room = rs.getString("room");
            offering.addWorkload(this);
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
    
    private void resetWorkloadDays() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT workloadFK, dayFK " +
                "FROM WorkloadDay " +
                "WHERE workloadFK = (?)"
                      
            );
            ps.setInt(1, pk);
            
            rs = ps.executeQuery();
            dayList = new ArrayList<>();
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
    
    private void checkValues() throws FLException {
        if (dayList.isEmpty())
                throw new FLException("Must set at least one day.");
    }
    
    public void add() throws FLException {

        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            // Insert into DB
            checkValues();
            PreparedStatement preparedStatement = null;
            preparedStatement = conn.prepareStatement(
                "INSERT INTO Workload (facultyFK, offeringFK, room) " +
                "VALUES (?, ?, ?)"
            );
            if (hasFaculty())
                preparedStatement.setInt(1, faculty.pk());
            else
                preparedStatement.setNull(1, 0);
            preparedStatement.setInt(2, offering.pk());
            preparedStatement.setString(3, room);   
            preparedStatement.executeUpdate();
            
            
            // Get PK
            preparedStatement = conn.prepareStatement(
                "SELECT workloadPK " +
                "FROM Workload " +
                "WHERE workloadPK = LAST_INSERT_ID() "
            );
            
            rs = preparedStatement.executeQuery();            
            if (rs.next())
                pk = rs.getInt("workLoadPK"); 
            
            // Add self to parent entities
            offering.addWorkload(this);
            if (hasFaculty())
                faculty.addWorkload(this);
            
            allWorkloads.put(pk, this);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }            
    }
    
    public void update() throws FLException {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            checkValues();
            if (faculty != null) {
                faculty.checkIfCanAssign(this);
                faculty.addWorkload(this);
            }
            
            // update NormalWorkload
            ps = conn.prepareStatement(
                "UPDATE Workload " +
                "SET room = ?, facultyFK = ? " +
                "WHERE workloadPK = ? "
            );
            ps.setString(1, room);
            if (faculty != null)
                ps.setInt(2, faculty.pk());
            else
                ps.setNull(2, 0);
            ps.setInt(3, pk);
            ps.executeUpdate();
                    
            // delete all days
            ps = conn.prepareStatement(
                "DELETE FROM WorkloadDay " +
                "WHERE workloadFK = ? "
            );
            ps.setInt(1, pk);
            ps.executeUpdate();
            
            // add days
            ps = conn.prepareStatement(
                "INSERT INTO WorkloadDay (workloadFK, dayFK) " +
                "VALUES (?, ?)"                
            );
            ps.setInt(1, pk);
            for (Day day : dayList) {
                ps.setInt(2, day.pk());
                ps.executeUpdate();
            }
        }
        catch (FLException e) {
            System.out.println("here");
            resetData();
            resetWorkloadDays();
            
            throw e;
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
    
    public void delete() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        
        // Remove self from other entities
        allWorkloads.remove(pk);
        offering.removeWorkload(this);
        if (hasFaculty())
            faculty.removeWorkload(this);
        
        try {   
            // Delete all days
            ps = conn.prepareStatement(
                "DELETE FROM WorkloadDay " +
                "WHERE workloadFK = ? "
            );
            ps.setInt(1, pk);
            ps.executeUpdate();
            
            // Delete this workload
            ps = conn.prepareStatement(
                "DELETE FROM Workload " +
                "WHERE workloadPK = ? "
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
    public int compareTo(Workload other) {
        int comparison = offering.compareTo(other.offering);
        if (comparison == 0) {
            comparison = pk - other.pk; //TODO questionable
        }
        return comparison;
    }
    
    public boolean isSpecial() {
        return offering.isSpecial();
    }

    public String code() {
        return offering.code();
    }

    public String section() {
        return offering.section();
    }
}
