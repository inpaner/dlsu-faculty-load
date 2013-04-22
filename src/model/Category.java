package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Category implements Comparable<Category> {
    private int pk;
    private String name;
    private static HashMap<Integer, Category> allCategories;
    
    // **********************************************************
    // * Static Methods
    // **********************************************************
    
    static {
        allCategories = new HashMap<>();
    }

    protected static void addToMap(Category category) {
        allCategories.put(category.pk, category);
    }
    
    protected static Category get(int pk) {
        return allCategories.get(pk);
    }
    
    public static Vector<Category> categoriesVector() {
        Vector<Category> vector = new Vector<>();
        for (Category category: allCategories.values()) {
            vector.add(category);
        }
        Collections.sort(vector);
        return vector;
    }
    
    public Category() {
    }
    
    
    protected Category(int pk) {
        this.pk = pk;
        resetData();
        allCategories.put(pk, this);
    }
    
    protected int pk() {
        return pk;
    }
    
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    public String name() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name.toUpperCase();
    }
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "SELECT name " +
                "FROM Category " +
                "WHERE categoryPK = (?)"
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
            ps = conn.prepareStatement(
                "INSERT INTO Category (name) " +
                "VALUES (?)"
            );
            ps.setString(1, name);
            ps.executeUpdate();
            
            ps = conn.prepareStatement(
                "SELECT categoryPK " +
                "FROM Category " +
                "WHERE categoryPK = LAST_INSERT_ID() "
            );
            rs = ps.executeQuery();            
            rs.next();
            pk = rs.getInt("categoryPK"); 
            addToMap(this);
            successful = true;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String error = "Category with name " + name + " already exists.";
            throw new FLException(error);
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
            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE Category " +
                "SET name = ? " +
                "WHERE categoryPK = ? "
            );
            preparedStatement.setString(1, name());
            preparedStatement.setInt(2, pk);
            preparedStatement.executeUpdate();
            successful = true;
        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String error = "Category with name " + name + " already exists.";
            throw new FLException(error);
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
        allCategories.remove(pk);
        for (Subject subject : Subject.subjectList()) {
            if (subject.category().equals(this))
                subject.delete();
        }
        
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "DELETE FROM Category " +
                "WHERE categoryPK = ? "
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
    public int compareTo(Category other) {
        return name.compareTo(other.name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
