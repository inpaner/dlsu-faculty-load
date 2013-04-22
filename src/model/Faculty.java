package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Faculty implements Comparable<Faculty> {
    private static HashMap<Integer, Faculty> allFaculties;
    
    // **********************************************************
    // * Static Methods
    // **********************************************************
    
    static {
        allFaculties = new HashMap<>();
    }

    protected static void addToMap(Faculty faculty) {
        allFaculties.put(faculty.pk, faculty);
    }
    
    protected static Faculty get(int pk) {
        return allFaculties.get(pk);
    }
    
    public static Vector<Faculty> facultyList() {
        Vector<Faculty> list = new Vector<>(allFaculties.values());
        Collections.sort(list);
        return list;
    }
    
    
    // **********************************************************
    // * Non-static Methods
    // **********************************************************
    private int pk;
    private String lastName;
    private String firstName;
    private TreeSet<Workload> workloadList;
    private TreeSet<AltWorkload> altWorkloadList;
    private TreeSet<Category> preferredCategories;
    private HashMap<String, FacultyStatus> statusMap;
    private FacultyStatus currentStatus;    
    
    public Faculty() {
        workloadList = new TreeSet<>();
        altWorkloadList = new TreeSet<>();
        preferredCategories = new TreeSet<>();
        statusMap = new HashMap<>();
    }

    protected Faculty(int pk) {
        this();
        this.pk = pk;
        resetData();
        allFaculties.put(pk, this);
    }
    
    public Vector<Workload> workloadList(int year, int term) {
        Vector<Workload> list = new Vector<>();
        for (Workload workload : workloadList) {
            if (workload.year() == year && workload.term() == term)
                list.add(workload);
        }
        Collections.sort(list);
        return list;
    }
    
    public Vector<AltWorkload> altWorkloadList(int year, int term) {
        Vector<AltWorkload> list = new Vector<>();
        for (AltWorkload workload : altWorkloadList) {
            if (workload.year() == year && workload.term() == term)
                list.add(workload);
        }
        //TODO Collections.sort(list);
        return list;
    }
    
    protected void setPK(int pk) {
        this.pk = pk;
    }
    
    protected int pk() {
        return pk;
    }
    
    public String toString() {
        return lastName() + ", " + firstName();
    }
    
    protected void addWorkload(Workload workload) {
        workloadList.add(workload);
    }
    
    protected void addWorkload(AltWorkload workload) {
        altWorkloadList.add(workload);
    }
    
    
    public void removeWorkload(Workload workload) {
        workloadList.remove(workload);
    }
    
    public void removeWorkload(AltWorkload workload) {
        altWorkloadList.remove(workload);
    }
    
    public Vector<Category> preferredCategories() {
        Vector<Category> vector = new Vector<>(preferredCategories);
        Collections.sort(vector);
        
        return vector;
    }
    
    public void addCategory(Category category) {
        preferredCategories.add(category);
    }
    
    public void removeCategory(Category category) {
        preferredCategories.remove(category);
    }
    
    protected void putStatus(int year, int term, FacultyStatus status) {
        String key = termString(year, term);
        statusMap.put(key, status);
    }

    public FacultyStatus currentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(FacultyStatus status) {
        currentStatus = status;
    }
    
    public boolean isOverloaded(int year, int term) {
        double units = units(year, term);
        FacultyStatus status = status(year, term);
        return (units > status.softMaxUnits());
    }
    
    public boolean isUnderloaded(int year, int term) {
        double units = units(year, term);
        FacultyStatus status = status(year, term);
        return (units < status.minUnits());
    }
    
    public boolean isNormalLoaded(int year, int term) {
        return (!isUnderloaded(year, term) &&
                !isOverloaded(year, term));
    }
    
    public boolean hasTaught(Subject subject) {
        boolean hasTaught = false;
        for (Workload workload : workloadList) {
            if (workload.subject().equals(subject)) {
                hasTaught = true;
                break;
            }
        }
        return hasTaught;
    }
    
    public FacultyStatus status(int year, int term) {
        // Returns default status if not set in DB
        FacultyStatus status = statusMap.get(termString(year, term));
        if (status == null) {
            status = currentStatus;
        }
        return status;
    }
    
    private boolean reachedMaxSpecialClasses(int year, int term) {
        int specialClasses = 0;
        for (Workload workload : workloadList) {
            if (workload.year() == year && workload.term() == term &&
                    workload.isSpecial())
            specialClasses++;
        }
        
        boolean reached = false;
        if (specialClasses == status(year, term).maxSpecialClasses())
            reached = true;
        
        return reached;
    }
    
    public Workload hasOverlap(Workload beingTested) {
        Workload overlap = null;
        for (Workload assigned : workloadList)  {
            // Ignores same offerings
            if (beingTested.offering().equals( assigned.offering() ))
                continue;
            if (beingTested.overlapsWith(assigned)) {
                overlap = assigned;
                break;
            }
        }
        return overlap;
    }
        
    public void checkIfCanAssign(Workload workload) throws FLException {
        // check if on leave
        if (status(User.year(), User.term()).equals(FacultyStatus.onLeave())) {
            throw new FLException("Faculty is on leave. Cannot assign workload.");
        }
        
        if (status(User.year(), User.term()).equals(FacultyStatus.notEmployed())) {
            throw new FLException("Faculty is not employed. Cannot assign workload.");
        }
        
        
        // check if workload has overlap
        Workload overlap = hasOverlap(workload);
        if (overlap != null)
            throw new FLException("Workload overlaps with " + overlap.toString() + ".");
                
        // check if more than max units
        double currentUnits = units(workload.year(), workload.term());
        FacultyStatus status = status(workload.year(), workload.term());
        if (currentUnits + workload.units() > status.maxUnits())
            throw new FLException("Faculty exceeds max units.");
        
        // check if more than max possible subjects
        TreeSet<Subject> subjects = new TreeSet<>();
        for (Workload otherWorkload : workloadList) {
            subjects.add(otherWorkload.offering().subject());
        }
        subjects.add(workload.offering().subject());
        if (subjects.size() > status.maxSubjects()) 
            throw new FLException("Faculty exceeds max subjects.");
        
        // check if more than max possible consecutive workloads
        if (reachedMaxConsecutives(workload)) 
            throw new FLException("Faculty exceeds max consecutive classes"); 
       
        // check if more than max special classes
        if (workload.isSpecial() && reachedMaxSpecialClasses(workload.year(), workload.term()))
            throw new FLException("Faculty exceeds max special classes");
    }
    
    public void checkIfCanAssign(AltWorkload workload) throws FLException {
        // check if on leave
        if (status(User.year(), User.term()).equals(FacultyStatus.onLeave())) {
            throw new FLException("Faculty is on leave. Cannot assign workload.");
        }
        
        if (status(User.year(), User.term()).equals(FacultyStatus.notEmployed())) {
            throw new FLException("Faculty is not employed. Cannot assign workload.");
        }
                
        // check if more than max units
        double currentUnits = units(workload.year(), workload.term());
        FacultyStatus status = status(workload.year(), workload.term());
        if (currentUnits + workload.units() > status.maxUnits())
            throw new FLException("Faculty exceeds max units.");
    }
    
    private boolean reachedMaxConsecutives(Workload beingTested) {
        boolean reachedMax = false;
                                    // 08:00, 09:40, 11:20, 13:00, 14:40, 16:20, 18;00
        boolean[] mon = new boolean[] {false, false, false, false, false, false, false};
        boolean[] tue = new boolean[] {false, false, false, false, false, false, false};
        boolean[] wed = new boolean[] {false, false, false, false, false, false, false};
        boolean[] thu = new boolean[] {false, false, false, false, false, false, false};
        boolean[] fri = new boolean[] {false, false, false, false, false, false, false};
        boolean[] sat = new boolean[] {false, false, false, false, false, false, false};
        boolean[][] days = new boolean[][] {mon, tue, wed, thu, fri, sat};
        

        // Put workload times to day arrays
        for (Workload workload : workloadList) {
            // get time index
            Time startTime = workload.offering().startTime();
            int index = timeIndex(startTime);

            // get days
            for (Day day : beingTested.dayList()) {
                timeToArray(day, index, days);
            }
        }
        
        // Put time of workload to add to day arrays
        Time startTime = beingTested.offering().startTime();
        int index = timeIndex(startTime);
        for (Day day : beingTested.dayList()) {
            timeToArray(day, index, days);
        }
        
        final int MAX_CONSECUTIVES = 3;
        outer:
        for (int i = 0; i < days.length; i++) {
            int consecutives = 0;
            for (int j = 0; j < days[i].length; j++) {
                if (days[i][j])
                    consecutives++;
                else
                    consecutives = 0;
                if (consecutives > MAX_CONSECUTIVES) {
                    reachedMax = true;
                    break outer;
                }
            }
        }
        
        return reachedMax;
    }
    
    private int timeIndex(Time startTime) {
        Time[] endTimes = new Time[] {Time.valueOf("9:30:00"), 
                Time.valueOf("11:10:00"), Time.valueOf("12:50:00"), 
                Time.valueOf("14:30:00"), Time.valueOf("16:10:00"), 
                Time.valueOf("17:50:00"), Time.valueOf("19:30:00") };
        
        int index = 0;
        for (int i = 0; i < endTimes.length; i++) {
            if (startTime.before(endTimes[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public HashMap<Subject, Integer> history() {
        HashMap<Subject, Integer> history = new HashMap<>();
        for (Workload workload : workloadList) {
            if (history.containsKey(workload.subject())) {
                int count = history.get(workload.subject());
                history.put(workload.subject(), count+1);
            }
            else
                history.put(workload.subject(), 1);
        }
        
        return history;
    }
    
    private void timeToArray(Day day, int index, boolean[][] days) {
        switch(day) {
            case MONDAY:    days[0][index] = true;
                            break;
            case TUESDAY:   days[1][index] = true;
                            break;
            case WEDNESDAY: days[2][index] = true;
                            break;
            case THURSDAY:  days[3][index] = true;
                            break;
            case FRIDAY:    days[4][index] = true;
                            break;
            case SATURDAY:  days[5][index] = true;
                            break;
            default:        break;                       
        }
    
    }
    
    private String termString(int year, int term) {
        return String.valueOf(year) + "-" + String.valueOf(term);
    }

    public double workloadUnits(int year, int term) {
        double total = 0;
        // Counts units by offering, not workload
        // Faculty can be assigned 100 workloads of the same
        // offering and be assigned only 3 units 
        TreeSet<Offering> offerings = new TreeSet<>();
        for (Workload workload : workloadList) {
            if (workload.year() == year && workload.term() == term) {
                offerings.add(workload.offering());
            }
        }
        for (Offering offering : offerings) {
            total += offering.units();
        }
        
        return total;
    }
    
    public double altWorkloadUnits(int year, int term) {
        double total = 0;
        for (AltWorkload workload : altWorkloadList) {
            if (workload.year() == year && workload.term() == term) {
                total += workload.units();
            }
        }
        return total;
    }
    
    
    public double units(int year, int term) {
        return workloadUnits(year, term) + altWorkloadUnits(year, term); 
    }
    
    public boolean prefers(Subject subject) {
        boolean prefers = false;
        if (subject.category() != null) {
            for (Category category : preferredCategories) {
                if (category.equals(subject.category())) {
                    prefers = true;
                    break;
                }
            }
        }
        return prefers;
    }
    
    public String lastName() {
        return lastName;
    }
    
    public String firstName() {
        return firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName.trim().toUpperCase();   
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName.trim().toUpperCase();
    }
    
    // **********************************************************
    // * Database Methods
    // **********************************************************
    
    private void resetData() {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                "SELECT lastName, firstName, currentStatusFK " +
                "FROM Faculty " +
                "WHERE facultyPK = (?) "       
            );
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            while (rs.next()) {
                lastName = rs.getString("lastName");
                firstName = rs.getString("firstName");
                currentStatus = FacultyStatus.get(rs.getInt("currentStatusFK"));
            }
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
    
    public void add() throws FLException {

        DBUtil util = new DBUtil();
        Connection conn = util.getConnection(); 
        ResultSet rs = null;
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Faculty (lastName, firstName, currentStatusFK) " +
                "VALUES (?, ?, ?)"
            );
            ps.setString(1, lastName());
            ps.setString(2, firstName());
            ps.setInt(3, currentStatus.pk());
            ps.executeUpdate();
            
            ps = conn.prepareStatement(
                "SELECT facultyPK " +
                "FROM Faculty " +
                "WHERE facultyPK = LAST_INSERT_ID() "
            );
            rs = ps.executeQuery();            
            if (rs.next())
                pk = rs.getInt("facultyPK"); 
            addToMap(this);

        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String msg = "Faculty " + this.toString() + " already exists";
            throw new FLException(msg);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(conn);
        }            
        

    }

    public void update() throws FLException {
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        try {
            
            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE Faculty " +
                "SET lastName = ?, firstName = ?, currentStatusFK = ? " +
                "WHERE facultyPK = ? "
            );
            preparedStatement.setString(1, lastName);
            preparedStatement.setString(2, firstName);
            preparedStatement.setInt(3, currentStatus.pk());
            preparedStatement.setInt(4, pk);
            preparedStatement.executeUpdate();
            

        }
        catch (MySQLIntegrityConstraintViolationException e) {
            String msg = "Faculty " + this.toString() + " already exists";
            resetData();
            throw new FLException(msg);
        }
        catch (SQLException e) {
            resetData();
            e.printStackTrace();
        }
        finally {
            util.close(rs);
            util.close(conn);
        }             

    }

    public boolean updateStatus(int year, int term, FacultyStatus status) {
        boolean successful = false;
        // check if status exists
        if (statusMap.containsKey(termString(year, term))) 
            successful = updateExistingStatus(year, term, status);
        
        else 
            successful = insertStatus(year, term, status);
        
        if (successful)
            putStatus(year, term, status);
        return successful;
    }
    
    private boolean insertStatus(int year, int term, FacultyStatus status) {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO FacultyTerm " +
                "   (year, term, facultyFK, facultyStatusFK) " +
                "VALUES (?, ?, ?, ?) "
            );
            preparedStatement.setInt(1, year);
            preparedStatement.setInt(2, term);
            preparedStatement.setInt(3, pk);
            preparedStatement.setInt(4, status.pk());
            preparedStatement.executeUpdate();
            
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

    private boolean updateExistingStatus(int year, int term, FacultyStatus status) {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE FacultyTerm " +
                "SET facultyStatusFK = ? " +
                "WHERE year = ? AND term = ? AND facultyFK = ? "
            );
            preparedStatement.setInt(1, status.pk());
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, term);
            preparedStatement.setInt(4, pk);
            preparedStatement.executeUpdate();
            
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
    
    public boolean updatePreferredCategories() {
        boolean successful = false;
        DBUtil util = new DBUtil();
        Connection conn = util.getConnection();
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try {

            // delete all preferred categories
            preparedStatement = conn.prepareStatement(
                "DELETE FROM CategoryPreference " +
                "WHERE facultyFK = ? "
            );
            preparedStatement.setInt(1, pk);
            preparedStatement.executeUpdate();
            
            // add preferences
            preparedStatement = conn.prepareStatement(
                "INSERT INTO CategoryPreference (facultyFK, categoryFK) " +
                "VALUES (?, ?)"                
            );
            preparedStatement.setInt(1, pk);
            for (Category category : preferredCategories) {
                preparedStatement.setInt(2, category.pk());
                preparedStatement.executeUpdate();
            }
            
            successful = true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            util.close(preparedStatement);
            util.close(rs);
            util.close(conn);
        }             
        return successful;
    }
    
    @Override
    public int compareTo(Faculty other) {
        int comparison = lastName.compareTo(other.lastName);
        if (comparison == 0) {
            comparison = firstName.compareTo(firstName);
        }
        return comparison;
    }

}
