package model;

public enum Day {
    MONDAY(1, "M", "Mon"),
    TUESDAY(2, "T", "Tue"),
    WEDNESDAY(3, "W", "Wed"),
    THURSDAY(4, "H", "Thu"),
    FRIDAY(5, "F", "Fri"),
    SATURDAY(6, "S", "Sat"),
    SUNDAY(7, "N", "Sun");
    
    private final int pk;
    private final String day1;
    private final String day3;
    
    Day(int pk, String day1, String day3) {
        this.pk = pk;
        this.day1 = day1;
        this.day3 = day3;
    }
    
    public static Day get(int num) {
        Day day = null;
        switch (num) {
            case 1:  day =  MONDAY;
                            break;
            case 2:  day =  TUESDAY;
                            break;
            case 3:  day =  WEDNESDAY;
                            break;
            case 4:  day =  THURSDAY;
                            break;
            case 5:  day =  FRIDAY;
                            break;            
            case 6:  day =  SATURDAY;
                            break;
            default: day =  SUNDAY;
                            break;
        }
        return day;
    }
    
    protected int pk() {
        return pk;
    }
    
    public String toString() {
        return day1;
    }
}
