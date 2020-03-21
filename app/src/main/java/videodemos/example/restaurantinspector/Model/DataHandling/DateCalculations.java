package videodemos.example.restaurantinspector.Model.DataHandling;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;

import videodemos.example.restaurantinspector.R;

/**
 * A class that handles calculations on calendar dates.
 */
public class DateCalculations {

    private HashMap<Integer, String> months = new HashMap<>();
    private Context context;

    public DateCalculations(Context context) {
        this.context = context;

        populateMonths();
    }

    public DateCalculations(){}

    public String getMonthName(int monthIndex){
        return months.get(monthIndex);
    }

    public int daysInBetween(String dateInput) {
        String year = dateInput.substring(0, 4);
        String month = dateInput.substring(4, 6);
        String date = dateInput.substring(6, 8);
        String dateInQuestion = year + "-" + month + "-" + date;
        DateTime dateTime = new DateTime();
        int days = Days.daysBetween(DateTime.parse(dateInQuestion), dateTime).getDays();
        return days;
    }

    public int hoursInBetween(String dateInput){
        DateTime dateTimeToday = new DateTime();
        Log.d("TIMES:", "Today: " + dateTimeToday.toString() + " Last Update: " + dateInput);
        int hours = Hours.hoursBetween(DateTime.parse(dateInput), dateTimeToday).getHours();
        return hours;
    }

    public int minutesInBetween(String dateInput){
        DateTime dateTimeToday = new DateTime();
        int minutes = Minutes.minutesBetween(DateTime.parse(dateInput), dateTimeToday).getMinutes();
        return minutes;
    }

    public int secondsInBetween(String dateInput){
        DateTime dateTimeToday = new DateTime();
        int seconds = Seconds.secondsBetween(DateTime.parse(dateInput), dateTimeToday).getSeconds();
        return seconds;
    }

    private void populateMonths() {
        months.put(1, context.getResources().getString(R.string.january));
        months.put(2, context.getResources().getString(R.string.february));
        months.put(3, context.getResources().getString(R.string.march));
        months.put(4, context.getResources().getString(R.string.april));
        months.put(5, context.getResources().getString(R.string.may));
        months.put(6, context.getResources().getString(R.string.june));
        months.put(7, context.getResources().getString(R.string.july));
        months.put(8, context.getResources().getString(R.string.august));
        months.put(9, context.getResources().getString(R.string.september));
        months.put(10, context.getResources().getString(R.string.october));
        months.put(11, context.getResources().getString(R.string.november));
        months.put(12, context.getResources().getString(R.string.december));

    }
}
