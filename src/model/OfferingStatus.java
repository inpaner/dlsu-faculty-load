package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public class OfferingStatus {
    private int pk;
    private String type;
    private static HashMap<Integer, OfferingStatus> allStatuses;
    
    static {
        allStatuses = new HashMap<>();
    }

    protected static OfferingStatus get(int pk) {
        return allStatuses.get(pk);
    }
    
    public static Vector<OfferingStatus> allStatuses() {
        return new Vector<>(allStatuses.values());
    }
    
    public static OfferingStatus specialClass() {
        return get(2);
    }
    
    protected OfferingStatus() {
    }
    
    protected OfferingStatus(int pk) {
        this.pk = pk;
        resetData();
        allStatuses.put(pk, this);
    }
    
    
    protected int pk() {
        return pk;
    }
    
    public String type() {
        return type;
    }
    
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return type;
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT type " +
                "FROM OfferingStatus " +
                "WHERE offeringStatusPK = (?) "
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            type = rs.getString("type");    
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
}
