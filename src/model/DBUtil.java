package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class DBUtil {
    /** Code derived from Chelsea Celestino **/
    private static String dbUrl;
    private static String dbName;
    private static String dbDriver;
    private static String username;
    private static String password;
    private Connection conn;
    
    static {
        dbUrl = "jdbc:mysql://localhost:3306/";
        dbName = "facultyload";
        dbDriver = "com.mysql.jdbc.Driver";
        username = "root";
        password = "1234";
    }
    
    public DBUtil() {
    }

    public static void setup(String user, String pass) {
        username = user;
        password = pass;
    }
    
    protected Connection getConnection() {            
        try {
            Class.forName(dbDriver).newInstance();
            conn = DriverManager.getConnection(dbUrl+dbName,username,password);
            //conn.setSavepoint();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return conn;
    }

    protected void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void close(Statement st) {
        try {
            if (st != null)
                st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void close(PreparedStatement st) {
        try {
            if (st != null)
                st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void commit(Connection connection) {
        try {
            if (connection != null) 
                connection.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    protected void rollback(Connection connection) {
        try {
            if (connection != null)   
                connection.rollback();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
