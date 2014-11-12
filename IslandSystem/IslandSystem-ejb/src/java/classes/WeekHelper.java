package classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeekHelper {

    private Calendar mCalendar;

    public WeekHelper() {
        mCalendar = new GregorianCalendar();
    }

    public boolean is52weeks(int year) {
        mCalendar.set(Calendar.YEAR, year); // Set only year 
        mCalendar.set(Calendar.MONTH, 12); // Don't change
        mCalendar.set(Calendar.DAY_OF_MONTH, 31); // Don't change
        int totalWeeks = mCalendar.get(Calendar.WEEK_OF_YEAR);
        return (totalWeeks == 52);
    }

    public int getDay() {
        return mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getWeek() {
        return mCalendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getYear(int offset) {
        int period = getPeriod(offset);
        int curr = getPeriod(0);
        if (offset < 0) {
            return (curr > period) ? mCalendar.get(Calendar.YEAR) : mCalendar.get(Calendar.YEAR) - 1;
        } else {
            return (curr > period) ? mCalendar.get(Calendar.YEAR) + 1 : mCalendar.get(Calendar.YEAR);
        }
    }

    public int getPeriod(int offset) {
        int curr = (getWeek() / 4) + 1;
        curr = curr + offset;
        if (offset < 0) {
            return (curr < 0) ? (13 + curr) : curr;
        } else {
            return (curr <= 13) ? curr : (curr - 13);
        }
    }

    public int getPeriodFirstWeek(int offset) {
        int period = getPeriod(offset);
        return (period * 4) - 3;
    }

    public int getWeekOfPeriod() {
        if (getWeek() == 53) {
            return 5;
        } else {
            return getWeek() % 4;
        }
    }

    public int getWeekOfPeriod(int offset) {
        if (offset == 53) {
            return 5;
        } else {
            return offset% 4;
        }
    }
        
    public int getWeeklyDemand(int amt, int week) {
        int demand = amt / 4;
        int remainder = amt % 4;
        if (week <= remainder) {
            return demand + 1;
        } else {
            return demand;
        }
    }

    public int getDailyDemand(int amt, int day) {
        int demand = amt / 5;
        int remainder = amt % 5;
        if (day <= remainder) {
            return demand + 1;
        } else {
            return demand;
        }
    }

    public String periodToDate(int year, int period) {
        Calendar cal = new GregorianCalendar();
        cal.set(GregorianCalendar.YEAR, year);
        cal.set(GregorianCalendar.MONTH, 1);
        cal.set(GregorianCalendar.DAY_OF_YEAR, 1);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 28 * period);
        return cal.get(GregorianCalendar.YEAR) + "-" + cal.get(GregorianCalendar.MONTH) + "-" + cal.get(GregorianCalendar.DAY_OF_MONTH);
    }

    public Date getCurrDate() {
        return mCalendar.getTime();
    }

    public Date getDate(int year, int week) {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.WEEK_OF_YEAR, week);
        mCalendar.set(Calendar.DAY_OF_WEEK, 1);
        return mCalendar.getTime();
    }

    public String getDateString(Date date) {
        //SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
    
    public String getPeriodString(int offset) {
        int year = getYear(offset);
        int week = getPeriodFirstWeek(offset);
        return year+" W"+week+"-W"+(week+3);
    }
}
