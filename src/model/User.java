package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class User {
    private static int year;
    private static int term;
    private static int pk;
    private boolean isValid;
    public static int year() {
        return year;
    }
    
    public static int term() {
        return term;
    }
    
    public static boolean setYearTerm(int yearArg, int termArg) {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE User " +
                "SET year = ?, term = ? " +
                "WHERE userPK = ? "
            );
            preparedStatement.setInt(1, yearArg);
            preparedStatement.setInt(2, termArg);
            preparedStatement.setInt(3, User.pk);
            preparedStatement.executeUpdate();
            
            year = yearArg;
            term = termArg;
            
            successful = true;
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
    
    public User(String username, String password) {
        isValid = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT userPK, year, term " +
                "FROM User " +
                "WHERE username = ? AND password = sha2(?, 0) "
            );
            ps.setString(1, username);
            ps.setString(2, password);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                pk = rs.getInt("userPK");
                year = rs.getInt("year");
                term = rs.getInt("term");
                isValid = true;
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
    
    public boolean isValid() {
        return isValid;
    }
    
}
