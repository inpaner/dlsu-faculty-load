package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class FacultyStatus {
    private int pk;
    private String status;
    private double minUnits;
    private double softMaxUnits;
    private double maxUnits;
    private double maxSpecialClasses;
    private double maxSubjects;


    private static HashMap<Integer, FacultyStatus> allStatuses;
    private static FacultyStatus defaultStatus;
    
    static {
        allStatuses = new HashMap<>();
        /*
        // imitate normal load
        defaultStatus = new FacultyStatus();
        defaultStatus.pk = 0;
        defaultStatus.status = "<Unspecified>";
        defaultStatus.minUnits = 12;
        defaultStatus.softMaxUnits = 15;
        defaultStatus.maxUnits = 18;
        defaultStatus.maxSpecialClasses = 2;
        defaultStatus.maxSubjects = 3;
        allStatuses.put(0, defaultStatus);
        */
    }
    
    public static FacultyStatus defaultStatus() {
        return defaultStatus;
    }
    
    public static FacultyStatus fullTime() {
        return allStatuses.get(1);
    }
    
    public static FacultyStatus partTime() {
        return allStatuses.get(2);
    }
    
    public static FacultyStatus onLeave() {
        return allStatuses.get(3);
    }
    
    public static FacultyStatus notEmployed() {
        return allStatuses.get(4);
    }
    
    
    public boolean isDefault() {
        return this.equals(defaultStatus);
    }
    
    public static Vector<FacultyStatus> statusVector() {
        return new Vector<>(allStatuses.values());
    }
    
    public static Vector<FacultyStatus> statusVectorWithoutDefault() {
        Vector<FacultyStatus> vector = new Vector<>(allStatuses.values());
        vector.remove(defaultStatus);
        return vector;
    }
    
    protected static FacultyStatus get(int pk) {
        return allStatuses.get(pk);
    }
    
    public FacultyStatus() {
    }
    
    
    protected FacultyStatus(int pk) {
        this.pk = pk;
        resetData();
        allStatuses.put(pk, this);
    }
    
    protected int pk() {
        return pk;
    }
    
    public void setMinUnits(double minUnits) {
        this.minUnits = minUnits;
    }

    public void setSoftMaxUnits(double softMaxUnits) {
        this.softMaxUnits = softMaxUnits;
    }

    public void setMaxUnits(int maxUnits) {
        this.maxUnits = maxUnits;
    }

    public void setMaxSpecialClasses(double maxSpecialClasses) {
        this.maxSpecialClasses = maxSpecialClasses;
    }

    public void setMaxSubjects(double maxSubjects) {
        this.maxSubjects = maxSubjects;
    }

    protected String status() {
        return status;
    }

    public double minUnits() {
        return minUnits;
    }

    public double softMaxUnits() {
        return softMaxUnits;
    }

    public double maxUnits() {
        return maxUnits;
    }

    public double maxSpecialClasses() {
        return maxSpecialClasses;
    }

    public double maxSubjects() {
        return maxSubjects;
    }
    
    public String toString() {
        return status;
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(
                "SELECT statusName, minUnits, softMaxUnits, maxUnits, " +
                "       maxSpecialClasses, maxSubjects " +
                "FROM FacultyStatus " +
                "WHERE facultyStatusPK = (?) "
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            while (rs.next()) {
                status = rs.getString("statusName");
                minUnits = rs.getInt("minUnits");
                softMaxUnits = rs.getInt("softMaxUnits");
                maxUnits = rs.getInt("maxUnits");
                maxSpecialClasses = rs.getInt("maxSpecialClasses");
                maxSubjects = rs.getInt("maxSubjects");
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
    
    private void checkValues() throws FLException {
        if (softMaxUnits < 0 ||
                minUnits < 0 ||
                maxUnits < 0 ||
                maxSpecialClasses < 0 ||
                maxSubjects < 0)
            throw new FLException("Negative values not allowed.");
        
        if (minUnits > softMaxUnits)
            throw new FLException("Min Units should be less than soft max units");
        if (minUnits > maxUnits)
            throw new FLException("Min Units should be less than max Units");
        if (softMaxUnits > maxUnits)
            throw new FLException("Min Units should be less than max Units");
        
    }
    
    public boolean update() throws FLException {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            checkValues();
            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE FacultyStatus " +
                "SET minUnits = ?, " +
                "    softMaxUnits = ?, " +
                "    maxUnits = ?, " +
                "    maxSpecialClasses = ?, " +
                "    maxSubjects = ? " +
                "WHERE facultyStatusPK = ? "
            );
            preparedStatement.setDouble(1, minUnits);
            preparedStatement.setDouble(2, softMaxUnits);
            preparedStatement.setDouble(3, maxUnits);
            preparedStatement.setDouble(4, maxSpecialClasses);
            preparedStatement.setDouble(5, maxSubjects);

            preparedStatement.setInt(6, pk);
            preparedStatement.executeUpdate();
            
            successful = true;
        }
        catch (FLException e) {
            resetData();
            throw e;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            resetData();
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
    
}
