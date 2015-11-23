package michii.de.scannapp.model.data.file;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Helper class which provides serveral methods for date calculations.
 * @author Michii
 * @since 07.06.2015
 */
public class DateUtil {

    public static long getCurrentDate() {
        Calendar cal = getCalendar();
        return cal.getTimeInMillis();
    }

    public static int getCurrentMonth() {
        Calendar cal = getCalendar();
        cal.getTime();
        return cal.get(Calendar.MONTH);
    }

    public static int getCurrentYear() {
        Calendar cal = getCalendar();
        cal.getTime();
        return cal.get(Calendar.YEAR);
    }

    public static long getDate(int year, int month, int day) {
        Calendar cal = getCalendar();
        cal.set(year, month, day);
        return cal.getTimeInMillis();
    }

    public static int getMonth(long date_milliseconds) {
        Date date = new Date(date_milliseconds);
        Calendar cal = getCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }


    public static int getYear(long date_milliseconds) {
        Date date = new Date(date_milliseconds);
        Calendar cal = getCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }


    public static Date getStartDate(int year) {
        Calendar cal = getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }


    public static Date getEndDate(int year) {
        Calendar cal = getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        return cal.getTime();
    }

    public static Date getStartDate(int year, int month) {
        Calendar cal = getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getEndDate(int year, int month) {
        Calendar cal = getCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();

    }

    public static String getDateString(long date_milliseconds) {
        Date date = new Date(date_milliseconds);
//        String format = "dd/MM/yy/HH";
//        SimpleDateFormat df = new SimpleDateFormat(format);
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.format(date);
    }

    private static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        return new GregorianCalendar();
    }

    public static Date daysAgo(int days) {
        Date today = new Date();
        Calendar cal = getCalendar();
        cal.setTime(today);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static String getMonthString(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
}
