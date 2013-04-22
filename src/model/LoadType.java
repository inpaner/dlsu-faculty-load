package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public class LoadType implements Comparable<LoadType> {
    private int pk;
    private String name;
    private static HashMap<Integer, LoadType> allTypes;
    
    static {
        allTypes = new HashMap<>();
    }
    
    protected static LoadType get(int pk) {
        return allTypes.get(pk);
    }
    
    public static Vector<LoadType> loadTypesVector() {
        Vector<LoadType> list = new Vector<>(allTypes.values());
        return list;
    }
    
    protected LoadType(int pk) {
        this.pk = pk;
        resetData();
        allTypes.put(pk, this);
    }
    
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    protected void setName(String name) {
        this.name = name;
    }
    
    protected int pk() {
        return pk;
    }
    
    public String name() {
        return name;
    }
    
    public String toString() {
        return name;
    }
    
    
    @Override
    public int compareTo(LoadType other) {
        return name.compareTo(other.name);
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                "SELECT name " +
                "FROM LoadType " +
                "WHERE loadTypePK = (?) "
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            rs.next();
            name = rs.getString("name");
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

